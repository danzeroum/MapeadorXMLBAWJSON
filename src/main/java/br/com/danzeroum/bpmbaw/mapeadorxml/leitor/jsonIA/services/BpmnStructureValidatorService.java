package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;

import java.util.*;

/**
 * Validates BPMN process structure and identifies potential issues
 * that could affect AI interpretation or process execution.
 */
public class BpmnStructureValidatorService {

    /**
     * Validates the structure of a BPMN process and returns validation results.
     */
    public BpmnValidationResult validateBpmnStructure(JsonReportV2.Artifact artifact) {
        BpmnValidationResult result = new BpmnValidationResult(artifact.getId(), artifact.getName());

        if (artifact.getGraph() == null) {
            result.addError(ValidationSeverity.HIGH, "NO_GRAPH_STRUCTURE",
                    "Artifact has no graph structure defined");
            return result;
        }

        // Core structural validations
        validateStartAndEndEvents(artifact, result);
        validateSequenceFlowConnectivity(artifact, result);
        validateGatewayStructure(artifact, result);
        validateNodeNaming(artifact, result);
        validateProcessFlow(artifact, result);
        validateDataConsistency(artifact, result);

        return result;
    }

    /**
     * Validates that the process has proper start and end events.
     */
    private void validateStartAndEndEvents(JsonReportV2.Artifact artifact, BpmnValidationResult result) {
        List<String> entryPoints = artifact.getGraph().getEntryPoints();
        List<String> endPoints = artifact.getGraph().getEndPoints();

        if (entryPoints == null || entryPoints.isEmpty()) {
            result.addError(ValidationSeverity.HIGH, "NO_START_EVENTS",
                    "Process has no start events defined");
        } else if (entryPoints.size() > 3) {
            result.addWarning(ValidationSeverity.MEDIUM, "MULTIPLE_START_EVENTS",
                    "Process has " + entryPoints.size() + " start events - consider consolidation");
        }

        if (endPoints == null || endPoints.isEmpty()) {
            result.addError(ValidationSeverity.HIGH, "NO_END_EVENTS",
                    "Process has no end events defined");
        }

        // Check for unreachable end events
        if (endPoints != null && entryPoints != null) {
            for (String endPoint : endPoints) {
                if (!isNodeReachableFromAnyStart(endPoint, entryPoints, artifact)) {
                    result.addWarning(ValidationSeverity.MEDIUM, "UNREACHABLE_END_EVENT",
                            "End event '" + getNodeName(endPoint, artifact) + "' may not be reachable");
                }
            }
        }
    }

    /**
     * Validates sequence flow connectivity.
     */
    private void validateSequenceFlowConnectivity(JsonReportV2.Artifact artifact, BpmnValidationResult result) {
        if (artifact.getGraph().getEdges() == null) {
            result.addError(ValidationSeverity.HIGH, "NO_SEQUENCE_FLOWS",
                    "Process has no sequence flows defined");
            return;
        }

        Set<String> connectedNodes = new HashSet<>();
        Map<String, Integer> incomingCount = new HashMap<>();
        Map<String, Integer> outgoingCount = new HashMap<>();

        // Count incoming and outgoing flows for each node
        for (JsonReportV2.Edge edge : artifact.getGraph().getEdges()) {
            if (edge.getSource() != null && edge.getTarget() != null) {
                connectedNodes.add(edge.getSource());
                connectedNodes.add(edge.getTarget());

                outgoingCount.merge(edge.getSource(), 1, Integer::sum);
                incomingCount.merge(edge.getTarget(), 1, Integer::sum);
            } else {
                result.addError(ValidationSeverity.HIGH, "INVALID_SEQUENCE_FLOW",
                        "Sequence flow '" + edge.getId() + "' has missing source or target");
            }
        }

        // Check for orphaned nodes
        if (artifact.getGraph().getNodes() != null) {
            for (JsonReportV2.Node node : artifact.getGraph().getNodes()) {
                if (!connectedNodes.contains(node.getId())) {
                    String nodeType = node.getType();
                    if (!"StartEvent".equals(nodeType) && !"EndEvent".equals(nodeType)) {
                        result.addWarning(ValidationSeverity.MEDIUM, "ORPHANED_NODE",
                                "Node '" + node.getName() + "' (" + nodeType + ") is not connected to any sequence flows");
                    }
                }
            }
        }

        // Check for nodes with unusual flow patterns
        for (Map.Entry<String, Integer> entry : outgoingCount.entrySet()) {
            String nodeId = entry.getKey();
            int outgoing = entry.getValue();
            String nodeType = getNodeType(nodeId, artifact);

            if ("EndEvent".equals(nodeType) && outgoing > 0) {
                result.addError(ValidationSeverity.HIGH, "END_EVENT_WITH_OUTGOING",
                        "End event '" + getNodeName(nodeId, artifact) + "' has outgoing sequence flows");
            }

            if (outgoing > 5 && !"ExclusiveGateway".equals(nodeType)) {
                result.addWarning(ValidationSeverity.MEDIUM, "MANY_OUTGOING_FLOWS",
                        "Node '" + getNodeName(nodeId, artifact) + "' has " + outgoing + " outgoing flows");
            }
        }
    }

    /**
     * Validates gateway structure and conditions.
     */
    private void validateGatewayStructure(JsonReportV2.Artifact artifact, BpmnValidationResult result) {
        if (artifact.getGraph().getGateways() == null) return;

        for (JsonReportV2.Gateway gateway : artifact.getGraph().getGateways()) {
            String gatewayId = gateway.getId();

            // Check for gateways without outgoing flows
            List<JsonReportV2.Edge> outgoingEdges = getOutgoingEdges(gatewayId, artifact);
            if (outgoingEdges.isEmpty()) {
                result.addError(ValidationSeverity.HIGH, "GATEWAY_NO_OUTGOING",
                        "Gateway '" + getNodeName(gatewayId, artifact) + "' has no outgoing flows");
                continue;
            }

            // For exclusive gateways, check conditions
            if ("ExclusiveGateway".equals(gateway.getType())) {
                validateExclusiveGatewayConditions(gateway, outgoingEdges, artifact, result);
            }

            // Check for single outgoing flow (unnecessary gateway)
            if (outgoingEdges.size() == 1) {
                result.addWarning(ValidationSeverity.LOW, "UNNECESSARY_GATEWAY",
                        "Gateway '" + getNodeName(gatewayId, artifact) + "' has only one outgoing flow - may be unnecessary");
            }
        }
    }

    /**
     * Validates exclusive gateway conditions.
     */
    private void validateExclusiveGatewayConditions(JsonReportV2.Gateway gateway,
                                                    List<JsonReportV2.Edge> outgoingEdges,
                                                    JsonReportV2.Artifact artifact,
                                                    BpmnValidationResult result) {
        String gatewayId = gateway.getId();
        String defaultFlow = gateway.getDefaultFlow();
        boolean hasDefaultFlow = defaultFlow != null && !defaultFlow.trim().isEmpty();

        int conditionCount = 0;
        int unconditionalCount = 0;

        for (JsonReportV2.Edge edge : outgoingEdges) {
            JsonReportV2.EdgeCondition condition = getConditionForEdge(edge.getId(), artifact);

            if (condition != null && condition.getExpression() != null &&
                    !condition.getExpression().trim().isEmpty() &&
                    !"true".equalsIgnoreCase(condition.getExpression().trim())) {
                conditionCount++;
            } else {
                unconditionalCount++;
            }
        }

        // Validation rules for exclusive gateways
        if (!hasDefaultFlow && unconditionalCount == 0 && conditionCount == outgoingEdges.size()) {
            result.addWarning(ValidationSeverity.MEDIUM, "NO_DEFAULT_FLOW",
                    "Exclusive gateway '" + getNodeName(gatewayId, artifact) +
                            "' has no default flow - may cause process to hang if no conditions are met");
        }

        if (unconditionalCount > 1 && !hasDefaultFlow) {
            result.addError(ValidationSeverity.HIGH, "MULTIPLE_UNCONDITIONAL_FLOWS",
                    "Exclusive gateway '" + getNodeName(gatewayId, artifact) +
                            "' has multiple unconditional flows without proper default flow designation");
        }

        if (conditionCount == 0 && outgoingEdges.size() > 1) {
            result.addWarning(ValidationSeverity.MEDIUM, "GATEWAY_NO_CONDITIONS",
                    "Exclusive gateway '" + getNodeName(gatewayId, artifact) +
                            "' has multiple flows but no conditions defined");
        }
    }

    /**
     * Validates node naming consistency.
     */
    private void validateNodeNaming(JsonReportV2.Artifact artifact, BpmnValidationResult result) {
        if (artifact.getGraph().getNodes() == null) return;

        Set<String> usedNames = new HashSet<>();
        int unnamedCount = 0;

        for (JsonReportV2.Node node : artifact.getGraph().getNodes()) {
            String name = node.getName();

            if (name == null || name.trim().isEmpty() || name.equals(node.getId())) {
                unnamedCount++;
                result.addWarning(ValidationSeverity.LOW, "UNNAMED_NODE",
                        "Node '" + node.getId() + "' (" + node.getType() + ") has no descriptive name");
            } else {
                if (!usedNames.add(name.toLowerCase())) {
                    result.addWarning(ValidationSeverity.LOW, "DUPLICATE_NODE_NAME",
                            "Node name '" + name + "' is used multiple times");
                }
            }
        }

        if (unnamedCount > 0) {
            result.addInfo("NAMING_SUMMARY",
                    unnamedCount + " out of " + artifact.getGraph().getNodes().size() + " nodes lack descriptive names");
        }
    }

    /**
     * Validates overall process flow logic.
     */
    private void validateProcessFlow(JsonReportV2.Artifact artifact, BpmnValidationResult result) {
        // Check for circular references without proper loop constructs
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        List<String> entryPoints = artifact.getGraph().getEntryPoints();
        if (entryPoints != null) {
            for (String entryPoint : entryPoints) {
                if (hasCircularDependency(entryPoint, artifact, visited, recursionStack)) {
                    result.addWarning(ValidationSeverity.MEDIUM, "POTENTIAL_INFINITE_LOOP",
                            "Process may contain infinite loops starting from '" + getNodeName(entryPoint, artifact) + "'");
                }
            }
        }

        // Check for unreachable nodes
        Set<String> reachableNodes = findAllReachableNodes(artifact);
        if (artifact.getGraph().getNodes() != null) {
            for (JsonReportV2.Node node : artifact.getGraph().getNodes()) {
                if (!reachableNodes.contains(node.getId()) &&
                        !"StartEvent".equals(node.getType())) {
                    result.addWarning(ValidationSeverity.MEDIUM, "UNREACHABLE_NODE",
                            "Node '" + node.getName() + "' may not be reachable from any start event");
                }
            }
        }
    }

    /**
     * Validates data consistency (variables, mappings).
     */
    private void validateDataConsistency(JsonReportV2.Artifact artifact, BpmnValidationResult result) {
        // Check for undefined variables in expressions
        if (artifact.getGraph().getConditions() != null) {
            for (JsonReportV2.EdgeCondition condition : artifact.getGraph().getConditions()) {
                if (condition.getExpression() != null) {
                    List<String> referencedVars = extractVariableReferences(condition.getExpression());
                    for (String var : referencedVars) {
                        if (!isVariableDefined(var, artifact)) {
                            result.addWarning(ValidationSeverity.MEDIUM, "UNDEFINED_VARIABLE",
                                    "Variable '" + var + "' referenced in condition but not defined in process variables");
                        }
                    }
                }
            }
        }

        // Check for unused variables
        if (artifact.getVariables() != null) {
            Set<String> definedVars = getAllDefinedVariables(artifact);
            Set<String> usedVars = getAllUsedVariables(artifact);

            for (String definedVar : definedVars) {
                if (!usedVars.contains(definedVar)) {
                    result.addInfo("UNUSED_VARIABLE",
                            "Variable '" + definedVar + "' is defined but never used");
                }
            }
        }
    }

    // ========== HELPER METHODS ==========

    private boolean isNodeReachableFromAnyStart(String targetNode, List<String> startNodes, JsonReportV2.Artifact artifact) {
        for (String startNode : startNodes) {
            if (isNodeReachable(startNode, targetNode, artifact, new HashSet<>())) {
                return true;
            }
        }
        return false;
    }

    private boolean isNodeReachable(String fromNode, String toNode, JsonReportV2.Artifact artifact, Set<String> visited) {
        if (fromNode.equals(toNode)) return true;
        if (!visited.add(fromNode)) return false;

        List<JsonReportV2.Edge> outgoing = getOutgoingEdges(fromNode, artifact);
        for (JsonReportV2.Edge edge : outgoing) {
            if (isNodeReachable(edge.getTarget(), toNode, artifact, visited)) {
                return true;
            }
        }
        return false;
    }

    private List<JsonReportV2.Edge> getOutgoingEdges(String nodeId, JsonReportV2.Artifact artifact) {
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

    private JsonReportV2.EdgeCondition getConditionForEdge(String edgeId, JsonReportV2.Artifact artifact) {
        if (artifact.getGraph().getConditions() != null) {
            for (JsonReportV2.EdgeCondition condition : artifact.getGraph().getConditions()) {
                if (edgeId.equals(condition.getEdgeId())) {
                    return condition;
                }
            }
        }
        return null;
    }

    private String getNodeName(String nodeId, JsonReportV2.Artifact artifact) {
        if (artifact.getGraph().getNodes() != null) {
            for (JsonReportV2.Node node : artifact.getGraph().getNodes()) {
                if (nodeId.equals(node.getId())) {
                    return node.getName() != null ? node.getName() : nodeId;
                }
            }
        }
        return nodeId;
    }

    private String getNodeType(String nodeId, JsonReportV2.Artifact artifact) {
        if (artifact.getGraph().getNodes() != null) {
            for (JsonReportV2.Node node : artifact.getGraph().getNodes()) {
                if (nodeId.equals(node.getId())) {
                    return node.getType();
                }
            }
        }
        return "Unknown";
    }

    private boolean hasCircularDependency(String startNode, JsonReportV2.Artifact artifact,
                                          Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(startNode)) return true;
        if (visited.contains(startNode)) return false;

        visited.add(startNode);
        recursionStack.add(startNode);

        List<JsonReportV2.Edge> outgoing = getOutgoingEdges(startNode, artifact);
        for (JsonReportV2.Edge edge : outgoing) {
            if (hasCircularDependency(edge.getTarget(), artifact, visited, recursionStack)) {
                return true;
            }
        }

        recursionStack.remove(startNode);
        return false;
    }

    private Set<String> findAllReachableNodes(JsonReportV2.Artifact artifact) {
        Set<String> reachable = new HashSet<>();

        if (artifact.getGraph().getEntryPoints() != null) {
            for (String entryPoint : artifact.getGraph().getEntryPoints()) {
                findReachableNodesRecursive(entryPoint, artifact, reachable);
            }
        }

        return reachable;
    }

    private void findReachableNodesRecursive(String nodeId, JsonReportV2.Artifact artifact, Set<String> reachable) {
        if (!reachable.add(nodeId)) return;

        List<JsonReportV2.Edge> outgoing = getOutgoingEdges(nodeId, artifact);
        for (JsonReportV2.Edge edge : outgoing) {
            findReachableNodesRecursive(edge.getTarget(), artifact, reachable);
        }
    }

    private List<String> extractVariableReferences(String expression) {
        List<String> variables = new ArrayList<>();
        if (expression == null) return variables;

        // Simple regex to find variable patterns like tw.local.variableName
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("tw\\.local\\.([a-zA-Z_][a-zA-Z0-9_]*)");
        java.util.regex.Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            variables.add(matcher.group(1));
        }

        return variables;
    }

    private boolean isVariableDefined(String variableName, JsonReportV2.Artifact artifact) {
        return getAllDefinedVariables(artifact).contains(variableName);
    }

    private Set<String> getAllDefinedVariables(JsonReportV2.Artifact artifact) {
        Set<String> definedVars = new HashSet<>();

        if (artifact.getVariables() != null) {
            if (artifact.getVariables().getInput() != null) {
                for (JsonReportV2.VariableInfo var : artifact.getVariables().getInput()) {
                    if (var.getName() != null) definedVars.add(var.getName());
                }
            }

            if (artifact.getVariables().getOutput() != null) {
                for (JsonReportV2.VariableInfo var : artifact.getVariables().getOutput()) {
                    if (var.getName() != null) definedVars.add(var.getName());
                }
            }

            // Try to get private variables using reflection (handle both method names)
            try {
                @SuppressWarnings("unchecked")
                List<JsonReportV2.VariableInfo> privateVars =
                        (List<JsonReportV2.VariableInfo>) artifact.getVariables().getClass()
                                .getMethod("getPrivate").invoke(artifact.getVariables());
                if (privateVars != null) {
                    for (JsonReportV2.VariableInfo var : privateVars) {
                        if (var.getName() != null) definedVars.add(var.getName());
                    }
                }
            } catch (Exception e1) {
                try {
                    @SuppressWarnings("unchecked")
                    List<JsonReportV2.VariableInfo> privateVars =
                            (List<JsonReportV2.VariableInfo>) artifact.getVariables().getClass()
                                    .getMethod("getPrivite").invoke(artifact.getVariables());
                    if (privateVars != null) {
                        for (JsonReportV2.VariableInfo var : privateVars) {
                            if (var.getName() != null) definedVars.add(var.getName());
                        }
                    }
                } catch (Exception e2) {
                    // Ignore reflection errors
                }
            }
        }

        return definedVars;
    }

    private Set<String> getAllUsedVariables(JsonReportV2.Artifact artifact) {
        Set<String> usedVars = new HashSet<>();

        // Check conditions
        if (artifact.getGraph() != null && artifact.getGraph().getConditions() != null) {
            for (JsonReportV2.EdgeCondition condition : artifact.getGraph().getConditions()) {
                if (condition.getExpression() != null) {
                    usedVars.addAll(extractVariableReferences(condition.getExpression()));
                }
            }
        }

        // Check scripts in flow steps
        if (artifact.getFlow() != null) {
            for (JsonReportV2.FlowStep step : artifact.getFlow()) {
                if (step.getScript() != null) {
                    usedVars.addAll(extractVariableReferences(step.getScript()));
                }

                // Check parameter mappings
                if (step.getParameterMapping() != null) {
                    if (step.getParameterMapping().getInput() != null) {
                        for (JsonReportV2.Mapping mapping : step.getParameterMapping().getInput()) {
                            if (mapping.getSource() != null) {
                                usedVars.addAll(extractVariableReferences(mapping.getSource()));
                            }
                        }
                    }

                    if (step.getParameterMapping().getOutput() != null) {
                        for (JsonReportV2.Mapping mapping : step.getParameterMapping().getOutput()) {
                            if (mapping.getTarget() != null) {
                                usedVars.addAll(extractVariableReferences(mapping.getTarget()));
                            }
                        }
                    }
                }
            }
        }

        return usedVars;
    }

    // ========== RESULT CLASSES ==========

    /**
     * Contains the results of BPMN structure validation.
     */
    public static class BpmnValidationResult {
        private final String artifactId;
        private final String artifactName;
        private final List<ValidationIssue> issues = new ArrayList<>();
        private final Map<ValidationSeverity, Integer> issueCounts = new HashMap<>();

        public BpmnValidationResult(String artifactId, String artifactName) {
            this.artifactId = artifactId;
            this.artifactName = artifactName;
        }

        public void addError(ValidationSeverity severity, String type, String message) {
            issues.add(new ValidationIssue(severity, type, message));
            issueCounts.merge(severity, 1, Integer::sum);
        }

        public void addWarning(ValidationSeverity severity, String type, String message) {
            addError(severity, type, message);
        }

        public void addInfo(String type, String message) {
            addError(ValidationSeverity.INFO, type, message);
        }

        // Getters
        public String getArtifactId() { return artifactId; }
        public String getArtifactName() { return artifactName; }
        public List<ValidationIssue> getIssues() { return Collections.unmodifiableList(issues); }
        public Map<ValidationSeverity, Integer> getIssueCounts() { return Collections.unmodifiableMap(issueCounts); }

        public boolean hasErrors() {
            return issueCounts.getOrDefault(ValidationSeverity.HIGH, 0) > 0;
        }

        public boolean hasWarnings() {
            return issueCounts.getOrDefault(ValidationSeverity.MEDIUM, 0) > 0 ||
                    issueCounts.getOrDefault(ValidationSeverity.LOW, 0) > 0;
        }

        public ValidationSummary getSummary() {
            int errorCount = issueCounts.getOrDefault(ValidationSeverity.HIGH, 0);
            int warningCount = issueCounts.getOrDefault(ValidationSeverity.MEDIUM, 0) +
                    issueCounts.getOrDefault(ValidationSeverity.LOW, 0);
            int infoCount = issueCounts.getOrDefault(ValidationSeverity.INFO, 0);

            if (errorCount > 0) return ValidationSummary.INVALID;
            if (warningCount > 3) return ValidationSummary.NEEDS_IMPROVEMENT;
            if (warningCount > 0) return ValidationSummary.VALID_WITH_WARNINGS;
            return ValidationSummary.VALID;
        }

        @Override
        public String toString() {
            return String.format("BpmnValidation{artifact='%s', summary=%s, issues=%d}",
                    artifactName, getSummary(), issues.size());
        }
    }

    /**
     * Represents a single validation issue.
     */
    public static class ValidationIssue {
        private final ValidationSeverity severity;
        private final String type;
        private final String message;

        public ValidationIssue(ValidationSeverity severity, String type, String message) {
            this.severity = severity;
            this.type = type;
            this.message = message;
        }

        public ValidationSeverity getSeverity() { return severity; }
        public String getType() { return type; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return String.format("[%s] %s: %s", severity, type, message);
        }
    }

    /**
     * Validation severity levels.
     */
    public enum ValidationSeverity {
        INFO("Info"),
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High");

        private final String displayName;
        ValidationSeverity(String displayName) { this.displayName = displayName; }
        @Override public String toString() { return displayName; }
    }

    /**
     * Overall validation summary.
     */
    public enum ValidationSummary {
        VALID("Process structure is valid"),
        VALID_WITH_WARNINGS("Process structure is valid but has minor issues"),
        NEEDS_IMPROVEMENT("Process structure needs improvement"),
        INVALID("Process structure has critical errors");

        private final String description;
        ValidationSummary(String description) { this.description = description; }
        @Override public String toString() { return description; }
    }

    /**
     * Validates multiple artifacts and provides aggregated results.
     */
    public static class BpmnValidationReport {
        private final List<BpmnValidationResult> results = new ArrayList<>();
        private final Map<ValidationSeverity, Integer> aggregatedCounts = new HashMap<>();

        public void addResult(BpmnValidationResult result) {
            results.add(result);

            // Aggregate counts
            for (Map.Entry<ValidationSeverity, Integer> entry : result.getIssueCounts().entrySet()) {
                aggregatedCounts.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        public List<BpmnValidationResult> getResults() { return Collections.unmodifiableList(results); }
        public Map<ValidationSeverity, Integer> getAggregatedCounts() { return Collections.unmodifiableMap(aggregatedCounts); }

        public int getValidArtifactCount() {
            return (int) results.stream().filter(r -> r.getSummary() == ValidationSummary.VALID).count();
        }

        public int getInvalidArtifactCount() {
            return (int) results.stream().filter(r -> r.getSummary() == ValidationSummary.INVALID).count();
        }

        public boolean hasAnyErrors() {
            return aggregatedCounts.getOrDefault(ValidationSeverity.HIGH, 0) > 0;
        }

        @Override
        public String toString() {
            return String.format("BpmnValidationReport{artifacts=%d, valid=%d, invalid=%d, totalIssues=%d}",
                    results.size(), getValidArtifactCount(), getInvalidArtifactCount(),
                    aggregatedCounts.values().stream().mapToInt(Integer::intValue).sum());
        }
    }
}