package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;

import java.util.*;

/**
 * Generates execution paths through a process artifact.
 * Handles both happy path and alternative path generation.
 */
public class ExecutionPathGeneratorService {

    private static final int DEFAULT_MAX_PATH_LENGTH = 30;
    private static final int DEFAULT_MAX_REPEATS_PER_NODE = 2;
    private static final int MAX_ALTERNATIVE_PATHS = 3;

    /**
     * Generates all execution paths for an artifact.
     *
     * @param artifact The artifact to analyze
     * @return List of execution paths (happy + alternatives)
     */
    public List<JsonReportV2.ExecutionPath> generateExecutionPaths(JsonReportV2.Artifact artifact) {
        if (!hasValidGraphStructure(artifact)) {
            return Collections.emptyList();
        }

        List<JsonReportV2.ExecutionPath> paths = new ArrayList<>();

        // Generate happy paths from first 2 entry points
        paths.addAll(generateHappyPaths(artifact));

        // Generate alternative paths
        paths.addAll(generateAlternativePaths(artifact));

        return deduplicatePaths(paths);
    }

    /**
     * Generates happy paths (default flows) from entry points.
     */
    private List<JsonReportV2.ExecutionPath> generateHappyPaths(JsonReportV2.Artifact artifact) {
        List<JsonReportV2.ExecutionPath> happyPaths = new ArrayList<>();
        List<String> entryPoints = artifact.getGraph().getEntryPoints();

        int maxEntryPoints = Math.min(2, entryPoints.size());
        for (int i = 0; i < maxEntryPoints; i++) {
            String startNode = entryPoints.get(i);
            PathTraversalResult result = followDefaultPath(artifact, startNode);

            if (!result.getSteps().isEmpty()) {
                JsonReportV2.ExecutionPath path = createExecutionPath(
                        artifact.getId(),
                        i == 0 ? "happy-path" : "happy-path-alt-" + i,
                        result.getSteps(),
                        result.getConditions(),
                        generateStepLabels(artifact, result.getSteps())
                );
                happyPaths.add(path);
            }
        }

        return happyPaths;
    }

    /**
     * Generates alternative paths by taking non-default branches.
     */
    private List<JsonReportV2.ExecutionPath> generateAlternativePaths(JsonReportV2.Artifact artifact) {
        List<JsonReportV2.ExecutionPath> altPaths = new ArrayList<>();

        if (artifact.getGraph().getEntryPoints().isEmpty()) {
            return altPaths;
        }

        String startNode = artifact.getGraph().getEntryPoints().get(0);

        for (int i = 0; i < MAX_ALTERNATIVE_PATHS; i++) {
            PathTraversalResult result = followAlternativePath(artifact, startNode, i + 1);

            if (!result.getSteps().isEmpty()) {
                JsonReportV2.ExecutionPath path = createExecutionPath(
                        artifact.getId(),
                        "alternative-" + (i + 1),
                        result.getSteps(),
                        result.getConditions(),
                        generateStepLabels(artifact, result.getSteps())
                );
                altPaths.add(path);
            }
        }

        return altPaths;
    }

    /**
     * Follows the default/happy path through the process.
     */
    private PathTraversalResult followDefaultPath(JsonReportV2.Artifact artifact, String startNode) {
        List<String> steps = new ArrayList<>();
        List<String> conditions = new ArrayList<>();
        Map<String, Integer> nodeVisitCount = new HashMap<>();

        String currentNode = startNode;
        while (currentNode != null && steps.size() < DEFAULT_MAX_PATH_LENGTH) {
            // Check for excessive repeats to avoid infinite loops
            int visitCount = nodeVisitCount.merge(currentNode, 1, Integer::sum);
            if (visitCount > DEFAULT_MAX_REPEATS_PER_NODE) {
                break;
            }

            steps.add(currentNode);

            if (isEndNode(artifact, currentNode)) {
                break;
            }

            // Find next node using default flow or first available
            PathStep nextStep = findNextDefaultStep(artifact, currentNode);
            if (nextStep == null) {
                break;
            }

            if (nextStep.getCondition() != null) {
                conditions.add(nextStep.getCondition());
            }

            currentNode = nextStep.getTargetNode();
        }

        return new PathTraversalResult(steps, conditions);
    }

    /**
     * Follows an alternative path by taking non-default branches.
     */
    private PathTraversalResult followAlternativePath(JsonReportV2.Artifact artifact, String startNode, int alternativeIndex) {
        List<String> steps = new ArrayList<>();
        List<String> conditions = new ArrayList<>();
        Map<String, Integer> nodeVisitCount = new HashMap<>();
        boolean hasDiverted = false;

        String currentNode = startNode;
        while (currentNode != null && steps.size() < DEFAULT_MAX_PATH_LENGTH) {
            int visitCount = nodeVisitCount.merge(currentNode, 1, Integer::sum);
            if (visitCount > DEFAULT_MAX_REPEATS_PER_NODE) {
                break;
            }

            steps.add(currentNode);

            if (isEndNode(artifact, currentNode)) {
                break;
            }

            PathStep nextStep;
            if (!hasDiverted) {
                // Try to take an alternative branch
                nextStep = findAlternativeStep(artifact, currentNode, alternativeIndex);
                if (nextStep != null) {
                    hasDiverted = true;
                } else {
                    // Fall back to default if no alternative available
                    nextStep = findNextDefaultStep(artifact, currentNode);
                }
            } else {
                // After diverting, follow default path
                nextStep = findNextDefaultStep(artifact, currentNode);
            }

            if (nextStep == null) {
                break;
            }

            if (nextStep.getCondition() != null) {
                conditions.add(nextStep.getCondition());
            }

            currentNode = nextStep.getTargetNode();
        }

        return new PathTraversalResult(steps, conditions);
    }

    /**
     * Finds the next step using default flow or first available edge.
     */
    private PathStep findNextDefaultStep(JsonReportV2.Artifact artifact, String nodeId) {
        // Try default flow for gateways first
        String defaultFlow = findDefaultFlowForGateway(artifact, nodeId);
        if (defaultFlow != null) {
            String target = findEdgeTarget(artifact, defaultFlow);
            String condition = findConditionLabel(artifact, defaultFlow);
            return new PathStep(target, condition);
        }

        // Fall back to first outgoing edge
        String firstEdge = findFirstOutgoingEdge(artifact, nodeId);
        if (firstEdge != null) {
            String target = findEdgeTarget(artifact, firstEdge);
            String condition = findConditionLabel(artifact, firstEdge);
            return new PathStep(target, condition);
        }

        return null;
    }

    /**
     * Finds an alternative (non-default) step for path diversification.
     */
    private PathStep findAlternativeStep(JsonReportV2.Artifact artifact, String nodeId, int alternativeIndex) {
        List<JsonReportV2.Edge> outgoingEdges = findOutgoingEdges(artifact, nodeId);
        if (outgoingEdges.size() <= 1) {
            return null; // No alternatives available
        }

        String defaultFlow = findDefaultFlowForGateway(artifact, nodeId);

        // Find non-default edges
        List<JsonReportV2.Edge> alternatives = new ArrayList<>();
        for (JsonReportV2.Edge edge : outgoingEdges) {
            if (defaultFlow == null || !defaultFlow.equals(edge.getId())) {
                alternatives.add(edge);
            }
        }

        if (alternatives.isEmpty()) {
            return null;
        }

        // Select alternative based on index (cycling through available alternatives)
        int selectedIndex = (alternativeIndex - 1) % alternatives.size();
        JsonReportV2.Edge selectedEdge = alternatives.get(selectedIndex);

        String condition = findConditionLabel(artifact, selectedEdge.getId());
        return new PathStep(selectedEdge.getTarget(), condition);
    }

    /**
     * Generates human-readable labels for steps in the path.
     */
    private List<String> generateStepLabels(JsonReportV2.Artifact artifact, List<String> stepIds) {
        List<String> labels = new ArrayList<>();

        for (String stepId : stepIds) {
            String name = findNodeName(artifact, stepId);
            String type = findNodeType(artifact, stepId);
            String lane = findNodeLane(artifact, stepId);

            StringBuilder labelBuilder = new StringBuilder();
            labelBuilder.append(name != null ? name : stepId);

            if (type != null) {
                labelBuilder.append(" [").append(type).append("]");
            }

            if (lane != null) {
                labelBuilder.append(" — ").append(lane);
            }

            labels.add(labelBuilder.toString());
        }

        return labels;
    }

    /**
     * Creates an ExecutionPath object with proper metadata.
     */
    private JsonReportV2.ExecutionPath createExecutionPath(String artifactId, String pathId,
                                                           List<String> steps, List<String> conditions,
                                                           List<String> stepLabels) {
        JsonReportV2.ExecutionPath path = new JsonReportV2.ExecutionPath();
        path.setArtifactId(artifactId);
        path.setPathId(pathId);
        path.setSteps(new ArrayList<>(steps));
        path.setConditions(new ArrayList<>(conditions));

        // Set step labels using reflection (optional field)
        try {
            java.lang.reflect.Method setStepLabelsMethod = path.getClass().getMethod("setStepLabels", List.class);
            setStepLabelsMethod.invoke(path, stepLabels);
        } catch (Exception e) {
            // Ignore if stepLabels field doesn't exist in the model
        }

        return path;
    }

    /**
     * Removes duplicate paths based on step sequence.
     */
    private List<JsonReportV2.ExecutionPath> deduplicatePaths(List<JsonReportV2.ExecutionPath> paths) {
        Set<String> seenSignatures = new HashSet<>();
        List<JsonReportV2.ExecutionPath> uniquePaths = new ArrayList<>();

        for (JsonReportV2.ExecutionPath path : paths) {
            String signature = path.getSteps().toString();
            if (seenSignatures.add(signature)) {
                uniquePaths.add(path);
            }
        }

        return uniquePaths;
    }

    // ========== GRAPH NAVIGATION HELPER METHODS ==========

    private boolean hasValidGraphStructure(JsonReportV2.Artifact artifact) {
        return artifact != null &&
                artifact.getGraph() != null &&
                artifact.getGraph().getEntryPoints() != null &&
                !artifact.getGraph().getEntryPoints().isEmpty();
    }

    private boolean isEndNode(JsonReportV2.Artifact artifact, String nodeId) {
        return artifact.getGraph().getEndPoints() != null &&
                artifact.getGraph().getEndPoints().contains(nodeId);
    }

    private String findDefaultFlowForGateway(JsonReportV2.Artifact artifact, String nodeId) {
        if (artifact.getGraph().getGateways() == null) return null;

        for (JsonReportV2.Gateway gateway : artifact.getGraph().getGateways()) {
            if (nodeId.equals(gateway.getId())) {
                return gateway.getDefaultFlow();
            }
        }
        return null;
    }

    private List<JsonReportV2.Edge> findOutgoingEdges(JsonReportV2.Artifact artifact, String nodeId) {
        List<JsonReportV2.Edge> outgoing = new ArrayList<>();

        if (artifact.getGraph().getEdges() != null) {
            for (JsonReportV2.Edge edge : artifact.getGraph().getEdges()) {
                if (nodeId.equals(edge.getSource())) {
                    outgoing.add(edge);
                }
            }
        }

        return outgoing;
    }

    private String findFirstOutgoingEdge(JsonReportV2.Artifact artifact, String nodeId) {
        List<JsonReportV2.Edge> outgoing = findOutgoingEdges(artifact, nodeId);
        return outgoing.isEmpty() ? null : outgoing.get(0).getId();
    }

    private String findEdgeTarget(JsonReportV2.Artifact artifact, String edgeId) {
        if (artifact.getGraph().getEdges() == null) return null;

        for (JsonReportV2.Edge edge : artifact.getGraph().getEdges()) {
            if (edgeId.equals(edge.getId())) {
                return edge.getTarget();
            }
        }
        return null;
    }

    private String findConditionLabel(JsonReportV2.Artifact artifact, String edgeId) {
        if (artifact.getGraph().getConditions() == null) return null;

        for (JsonReportV2.EdgeCondition condition : artifact.getGraph().getConditions()) {
            if (edgeId.equals(condition.getEdgeId())) {
                String expr = condition.getExpression();
                if (expr != null) {
                    expr = expr.replaceAll("\\s+", " ").trim();
                    return "edge:" + edgeId + " (" + expr + ")";
                }
            }
        }
        return null;
    }

    private String findNodeName(JsonReportV2.Artifact artifact, String nodeId) {
        if (artifact.getGraph().getNodes() == null) return nodeId;

        for (JsonReportV2.Node node : artifact.getGraph().getNodes()) {
            if (nodeId.equals(node.getId())) {
                if (node.getName() != null && !node.getName().trim().isEmpty()) {
                    return node.getName();
                }
                break;
            }
        }

        // Fallback to flow steps
        if (artifact.getFlow() != null) {
            for (JsonReportV2.FlowStep step : artifact.getFlow()) {
                if (nodeId.equals(step.getStepId()) &&
                        step.getName() != null && !step.getName().trim().isEmpty()) {
                    return step.getName();
                }
            }
        }

        return nodeId;
    }

    private String findNodeType(JsonReportV2.Artifact artifact, String nodeId) {
        if (artifact.getGraph().getNodes() == null) return null;

        for (JsonReportV2.Node node : artifact.getGraph().getNodes()) {
            if (nodeId.equals(node.getId())) {
                return node.getType();
            }
        }
        return null;
    }

    private String findNodeLane(JsonReportV2.Artifact artifact, String nodeId) {
        if (artifact.getGraph().getNodes() == null) return null;

        for (JsonReportV2.Node node : artifact.getGraph().getNodes()) {
            if (nodeId.equals(node.getId())) {
                return node.getLane();
            }
        }
        return null;
    }

    // ========== HELPER CLASSES ==========

    /**
     * Represents the result of traversing a path through the process.
     */
    private static class PathTraversalResult {
        private final List<String> steps;
        private final List<String> conditions;

        public PathTraversalResult(List<String> steps, List<String> conditions) {
            this.steps = new ArrayList<>(steps);
            this.conditions = new ArrayList<>(conditions);
        }

        public List<String> getSteps() { return steps; }
        public List<String> getConditions() { return conditions; }
    }

    /**
     * Represents a single step in a path with optional condition.
     */
    private static class PathStep {
        private final String targetNode;
        private final String condition;

        public PathStep(String targetNode, String condition) {
            this.targetNode = targetNode;
            this.condition = condition;
        }

        public String getTargetNode() { return targetNode; }
        public String getCondition() { return condition; }
    }
}