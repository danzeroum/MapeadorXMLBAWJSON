package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;

import java.util.*;

/**
 * Manages the construction of root view with iterative DFS traversal.
 * Builds a flattened view of the process including subprocess calls up to a specified depth.
 */
public class RootViewBuilderService {

    private static final int DEFAULT_DEPTH_LIMIT = 1;
    private static final int MAX_STEPS_LIMIT = 1000; // Prevent runaway traversal

    private final ProcessLoader processLoader;

    public RootViewBuilderService(ProcessLoader processLoader) {
        this.processLoader = processLoader;
    }

    /**
     * Builds root view with default depth limit.
     */
    public void buildRootView(JsonReportV2.Artifact rootArtifact, String rootCleanId) {
        buildRootView(rootArtifact, rootCleanId, DEFAULT_DEPTH_LIMIT);
    }

    /**
     * Builds root view with specified depth limit.
     *
     * @param rootArtifact The artifact to populate with root view
     * @param rootCleanId The clean ID of the root process
     * @param depthLimit Maximum depth for subprocess expansion
     */
    public void buildRootView(JsonReportV2.Artifact rootArtifact, String rootCleanId, int depthLimit) {
        List<JsonReportV2.FlowStep> rootSteps = generateShallowFlowSteps(rootCleanId);
        if (rootSteps.isEmpty()) {
            System.out.println("[ROOT-VIEW] No flow steps found for: " + rootCleanId);
            return;
        }

        rootArtifact.getRootView().clear();

        IterativeDfsTraversal traversal = new IterativeDfsTraversal(rootSteps, depthLimit);
        List<JsonReportV2.FlowStep> orderedSteps = traversal.traverse();

        rootArtifact.getRootView().addAll(orderedSteps);

        System.out.println("[ROOT-VIEW] Built root view with " + orderedSteps.size() +
                " steps (depth=" + depthLimit + ")");
    }

    /**
     * Generates shallow flow steps (no subprocess expansion) for an artifact.
     */
    private List<JsonReportV2.FlowStep> generateShallowFlowSteps(String artifactId) {
        Object artifact = processLoader.getArtefatoDoCache(artifactId);

        if (artifact instanceof Definitions) {
            return generateBpmnFlowSteps((Definitions) artifact);
        } else if (artifact instanceof Teamworks) {
            return generateLegacyFlowSteps((Teamworks) artifact);
        }

        return Collections.emptyList();
    }

    /**
     * Generates flow steps from BPMN Definitions.
     */
    private List<JsonReportV2.FlowStep> generateBpmnFlowSteps(Definitions definitions) {
        if (definitions.getProcess() == null) {
            return Collections.emptyList();
        }

        Process process = definitions.getProcess();
        List<JsonReportV2.FlowStep> steps = new ArrayList<>();

        // Create ordered list of flow nodes
        List<FlowNode> orderedNodes = createOrderedNodeList(process);

        int orderIndex = 0;
        for (FlowNode node : orderedNodes) {
            JsonReportV2.FlowStep step = createStepFromBpmnNode(node, orderIndex++);
            if (step != null) {
                steps.add(step);
            }
        }

        return steps;
    }

    /**
     * Creates an ordered list of BPMN flow nodes using topological sort.
     */
    private List<FlowNode> createOrderedNodeList(Process process) {
        Map<String, FlowNode> nodeMap = new HashMap<>();
        Set<String> startNodes = new HashSet<>();

        // Build node map and identify start nodes
        for (Object element : process.getFlowElements()) {
            if (element instanceof FlowNode) {
                FlowNode node = (FlowNode) element;
                nodeMap.put(node.getId(), node);

                if (element instanceof StartEvent) {
                    startNodes.add(node.getId());
                }
            }
        }

        // Perform DFS traversal to order nodes
        List<FlowNode> orderedNodes = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String startId : startNodes) {
            dfsTraversal(startId, nodeMap, visited, orderedNodes, process.getSequenceFlows());
        }

        // Add any unvisited nodes
        for (FlowNode node : nodeMap.values()) {
            if (!visited.contains(node.getId())) {
                orderedNodes.add(node);
            }
        }

        return orderedNodes;
    }

    /**
     * DFS traversal to order nodes based on sequence flows.
     */
    private void dfsTraversal(String nodeId, Map<String, FlowNode> nodeMap, Set<String> visited,
                              List<FlowNode> orderedNodes, List<SequenceFlow> sequenceFlows) {
        if (!visited.add(nodeId)) {
            return;
        }

        FlowNode node = nodeMap.get(nodeId);
        if (node != null) {
            orderedNodes.add(node);

            // Find outgoing sequence flows
            for (SequenceFlow flow : sequenceFlows) {
                if (nodeId.equals(flow.getSourceRef())) {
                    dfsTraversal(flow.getTargetRef(), nodeMap, visited, orderedNodes, sequenceFlows);
                }
            }
        }
    }

    /**
     * Creates a flow step from a BPMN flow node.
     */
    private JsonReportV2.FlowStep createStepFromBpmnNode(FlowNode node, int orderIndex) {
        JsonReportV2.FlowStep step = new JsonReportV2.FlowStep();
        step.setStepId(node.getId());
        step.setName(node.getName());
        step.setOrderIndex(orderIndex);
        step.setType(determineBpmnNodeType(node));

        // Handle incoming and outgoing flows
        if (node.getIncoming() != null) {
            step.getIncomingFlows().addAll(node.getIncoming());
        }
        if (node.getOutgoing() != null) {
            step.getOutgoingFlows().addAll(node.getOutgoing());
        }

        // Handle specific node types
        if (node instanceof CallActivity) {
            CallActivity callActivity = (CallActivity) node;
            step.setCalledArtifactId(callActivity.getCalledElement());
        } else if (node instanceof ScriptTask) {
            ScriptTask scriptTask = (ScriptTask) node;
            step.setScript(scriptTask.getScript());
        }

        return step;
    }

    /**
     * Determines the type name for a BPMN node.
     */
    private String determineBpmnNodeType(FlowNode node) {
        if (node instanceof StartEvent) return "StartEvent";
        if (node instanceof EndEvent) return "EndEvent";
        if (node instanceof CallActivity) return "CallActivity";
        if (node instanceof ScriptTask) return "ScriptTask";
        if (node instanceof FormTask) return "FormTask";
        if (node instanceof Task) return "Task";
        if (node instanceof ExclusiveGateway) return "ExclusiveGateway";
        if (node instanceof SubProcess) return "SubProcess";
        return "FlowNode";
    }

    /**
     * Generates flow steps from legacy Teamworks artifacts.
     */
    private List<JsonReportV2.FlowStep> generateLegacyFlowSteps(Teamworks teamworks) {
        if (teamworks.getProcess() == null) {
            return Collections.emptyList();
        }

        br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process = teamworks.getProcess();
        List<JsonReportV2.FlowStep> steps = new ArrayList<>();

        // Handle processes with items (legacy flow structure)
        if (process.getItems() != null && !process.getItems().isEmpty()) {
            List<Item> orderedItems = createOrderedItemList(process);

            int orderIndex = 0;
            for (Item item : orderedItems) {
                JsonReportV2.FlowStep step = createStepFromLegacyItem(item, orderIndex++);
                if (step != null) {
                    steps.add(step);
                }
            }
        } else {
            // Handle single-step services (no items)
            JsonReportV2.FlowStep singleStep = createSingleStepFromProcess(process);
            if (singleStep != null) {
                steps.add(singleStep);
            }
        }

        return steps;
    }

    /**
     * Creates an ordered list of legacy process items.
     */
    private List<Item> createOrderedItemList(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process) {
        Map<String, Item> itemMap = new HashMap<>();
        Map<String, List<String>> outgoingMap = new HashMap<>();
        Set<String> hasIncoming = new HashSet<>();

        // Build item map
        for (Item item : process.getItems()) {
            itemMap.put(item.getProcessItemId(), item);
        }

        // Build connection map
        if (process.getLinks() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link link : process.getLinks()) {
                String fromId = link.getFromProcessItemId();
                String toId = link.getToProcessItemId();

                outgoingMap.computeIfAbsent(fromId, k -> new ArrayList<>()).add(toId);
                hasIncoming.add(toId);
            }
        }

        // Find start items (no incoming links)
        List<String> startItems = new ArrayList<>();
        for (String itemId : itemMap.keySet()) {
            if (!hasIncoming.contains(itemId)) {
                startItems.add(itemId);
            }
        }

        // DFS traversal
        List<Item> orderedItems = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String startId : startItems) {
            legacyDfsTraversal(startId, itemMap, outgoingMap, visited, orderedItems);
        }

        // Add any unvisited items
        for (Item item : itemMap.values()) {
            if (!visited.contains(item.getProcessItemId())) {
                orderedItems.add(item);
            }
        }

        return orderedItems;
    }

    /**
     * DFS traversal for legacy items.
     */
    private void legacyDfsTraversal(String itemId, Map<String, Item> itemMap,
                                    Map<String, List<String>> outgoingMap,
                                    Set<String> visited, List<Item> orderedItems) {
        if (!visited.add(itemId)) {
            return;
        }

        Item item = itemMap.get(itemId);
        if (item != null) {
            orderedItems.add(item);

            List<String> outgoing = outgoingMap.get(itemId);
            if (outgoing != null) {
                for (String nextId : outgoing) {
                    legacyDfsTraversal(nextId, itemMap, outgoingMap, visited, orderedItems);
                }
            }
        }
    }

    /**
     * Creates a flow step from a legacy process item.
     */
    private JsonReportV2.FlowStep createStepFromLegacyItem(Item item, int orderIndex) {
        JsonReportV2.FlowStep step = new JsonReportV2.FlowStep();
        step.setStepId(item.getProcessItemId());
        step.setName(item.getName());
        step.setOrderIndex(orderIndex);
        step.setType(item.getTWComponentName());

        // Handle subprocess calls
        if (item.getTwComponent() != null) {
            if ("SubProcess".equalsIgnoreCase(item.getTWComponentName())) {
                step.setCalledArtifactId(item.getTwComponent().getAttachedProcessRef());
            }

            if (item.getTwComponent().getScript() != null) {
                step.setScript(item.getTwComponent().getScript());
            }
        }

        return step;
    }

    /**
     * Creates a single step for processes without items.
     */
    private JsonReportV2.FlowStep createSingleStepFromProcess(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process) {
        JsonReportV2.FlowStep step = new JsonReportV2.FlowStep();
        step.setStepId("single_step");
        step.setName(process.getName() != null ? process.getName() : "Unnamed Process");
        step.setOrderIndex(0);

        // Determine type based on process content
        if (process.getClobField1() != null && !process.getClobField1().trim().isEmpty()) {
            step.setScript(process.getClobField1());
            step.setType("ScriptTask");
        } else {
            step.setType("ServiceTask");
        }

        return step;
    }

    /**
     * Creates a clone of a flow step for root view (with reset metadata).
     */
    private JsonReportV2.FlowStep cloneStepForRootView(JsonReportV2.FlowStep original) {
        JsonReportV2.FlowStep clone = new JsonReportV2.FlowStep();

        // Copy core properties
        clone.setStepId(original.getStepId());
        clone.setName(original.getName());
        clone.setType(original.getType());
        clone.setCalledArtifactId(original.getCalledArtifactId());
        clone.setScript(original.getScript());
        clone.setLane(original.getLane());
        clone.setCoachId(original.getCoachId());

        // Copy collections
        if (original.getIncomingFlows() != null) {
            clone.getIncomingFlows().addAll(original.getIncomingFlows());
        }
        if (original.getOutgoingFlows() != null) {
            clone.getOutgoingFlows().addAll(original.getOutgoingFlows());
        }

        // Copy parameter mapping if present
        if (original.getParameterMapping() != null) {
            JsonReportV2.ParameterMapping originalMapping = original.getParameterMapping();
            JsonReportV2.ParameterMapping clonedMapping = new JsonReportV2.ParameterMapping();

            if (originalMapping.getInput() != null) {
                clonedMapping.getInput().addAll(originalMapping.getInput());
            }
            if (originalMapping.getOutput() != null) {
                clonedMapping.getOutput().addAll(originalMapping.getOutput());
            }

            clone.setParameterMapping(clonedMapping);
        }

        // Copy conditions if present
        if (original.getConditions() != null) {
            clone.getConditions().addAll(original.getConditions());
        }

        return clone;
    }

    // ========== ITERATIVE DFS TRAVERSAL ==========

    /**
     * Inner class for iterative DFS traversal to build root view.
     * Handles depth-limited expansion of subprocesses.
     */
    private class IterativeDfsTraversal {
        private final Deque<TraversalFrame> stack = new ArrayDeque<>();
        private final Set<String> visited = new HashSet<>();
        private final List<JsonReportV2.FlowStep> result = new ArrayList<>();
        private final int depthLimit;
        private int orderIndex = 0;

        /**
         * Frame representing a level in the traversal stack.
         * Made non-static for Java 8 compatibility.
         */
        private class TraversalFrame {
            final String artifactId;
            final List<JsonReportV2.FlowStep> steps;
            final int depth;
            final String parentStepId;
            int currentIndex = 0;

            TraversalFrame(String artifactId, List<JsonReportV2.FlowStep> steps, int depth, String parentStepId) {
                this.artifactId = artifactId;
                this.steps = steps;
                this.depth = depth;
                this.parentStepId = parentStepId;
            }

            boolean hasMoreSteps() {
                return currentIndex < steps.size();
            }

            JsonReportV2.FlowStep getCurrentStep() {
                return steps.get(currentIndex);
            }

            void moveToNextStep() {
                currentIndex++;
            }
        }

        public IterativeDfsTraversal(List<JsonReportV2.FlowStep> rootSteps, int depthLimit) {
            this.depthLimit = depthLimit;
            // Initialize with root frame
            stack.push(new TraversalFrame("root", rootSteps, 0, null));
        }

        /**
         * Performs the iterative DFS traversal.
         */
        public List<JsonReportV2.FlowStep> traverse() {
            while (!stack.isEmpty() && result.size() < MAX_STEPS_LIMIT) {
                TraversalFrame frame = stack.peek();

                if (!frame.hasMoreSteps()) {
                    stack.pop();
                    continue;
                }

                JsonReportV2.FlowStep step = frame.getCurrentStep();
                frame.moveToNextStep();

                // Process current step
                JsonReportV2.FlowStep processedStep = processStepForRootView(step, frame);
                result.add(processedStep);

                // Add child frame if step has subprocess call and within depth limit
                addChildFrameIfNeeded(step, frame);
            }

            if (result.size() >= MAX_STEPS_LIMIT) {
                System.out.println("[ROOT-VIEW] Warning: Reached maximum steps limit (" + MAX_STEPS_LIMIT + ")");
            }

            return result;
        }

        /**
         * Processes a step for inclusion in root view.
         */
        private JsonReportV2.FlowStep processStepForRootView(JsonReportV2.FlowStep step, TraversalFrame frame) {
            JsonReportV2.FlowStep processedStep = cloneStepForRootView(step);

            // Set root view specific metadata
            processedStep.setOrderIndex(orderIndex++);
            processedStep.setSubflowDepth(frame.depth);
            processedStep.setParentStepId(frame.parentStepId);

            return processedStep;
        }

        /**
         * Adds a child frame for subprocess expansion if conditions are met.
         */
        private void addChildFrameIfNeeded(JsonReportV2.FlowStep step, TraversalFrame parentFrame) {
            // Check if step calls another artifact
            if (step.getCalledArtifactId() == null || step.getCalledArtifactId().trim().isEmpty()) {
                return;
            }

            // Check depth limit
            if (parentFrame.depth >= depthLimit) {
                return;
            }

            // Check if already visited to prevent cycles
            String childId = normalizeIdSafe(step.getCalledArtifactId());;
            if (childId == null || !visited.add(childId)) {
                return;
            }

            // Check if child artifact exists in cache
            Object childArtifact = processLoader.getArtefatoDoCache(childId);
            if (childArtifact == null) {
                System.out.println("[ROOT-VIEW] Child artifact not found in cache: " + childId);
                return;
            }

            // Generate child steps
            List<JsonReportV2.FlowStep> childSteps = generateShallowFlowSteps(childId);
            if (!childSteps.isEmpty()) {
                TraversalFrame childFrame = new TraversalFrame(
                        childId,
                        childSteps,
                        parentFrame.depth + 1,
                        step.getStepId()
                );
                stack.push(childFrame);

                System.out.println("[ROOT-VIEW] Added child frame: " + childId +
                        " (depth=" + childFrame.depth + ", steps=" + childSteps.size() + ")");
            }
        }
    }
    private String normalizeIdSafe(String originalId) {
        try {
            // Tentar normalizeId (método correto)
            return processLoader.normalizeId(originalId);
        } catch (Exception e1) {
            try {
                // Fallback para getCleanId via reflection
                java.lang.reflect.Method getCleanIdMethod = processLoader.getClass()
                        .getMethod("getCleanId", String.class);
                return (String) getCleanIdMethod.invoke(processLoader, originalId);
            } catch (Exception e2) {
                // Último recurso: normalização manual
                return manualIdNormalization(originalId);
            }
        }
    }

    private String manualIdNormalization(String originalId) {
        if (originalId == null) return null;

        String normalized = originalId.trim()
                .replace("\\", "/")
                .replaceAll("/+", "/");

        // Remove barras no início e fim
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized.isEmpty() ? originalId : normalized;
    }
    // ========== PUBLIC UTILITIES ==========

    /**
     * Gets debugging information about root view construction.
     */
    public RootViewAnalysisSummary analyzeRootView(JsonReportV2.Artifact artifact) {
        if (artifact == null || artifact.getRootView() == null) {
            return new RootViewAnalysisSummary(0, 0, 0, Collections.emptyMap());
        }

        int totalSteps = artifact.getRootView().size();
        int maxDepth = 0;
        int subprocessCallCount = 0;
        Map<Integer, Integer> stepsByDepth = new HashMap<>();

        for (JsonReportV2.FlowStep step : artifact.getRootView()) {
            int depth = step.getSubflowDepth();
            maxDepth = Math.max(maxDepth, depth);
            stepsByDepth.merge(depth, 1, Integer::sum);

            if (step.getCalledArtifactId() != null && !step.getCalledArtifactId().trim().isEmpty()) {
                subprocessCallCount++;
            }
        }

        return new RootViewAnalysisSummary(totalSteps, maxDepth, subprocessCallCount, stepsByDepth);
    }

    /**
     * Summary of root view analysis for debugging.
     */
    public static class RootViewAnalysisSummary {
        private final int totalSteps;
        private final int maxDepth;
        private final int subprocessCallCount;
        private final Map<Integer, Integer> stepsByDepth;

        public RootViewAnalysisSummary(int totalSteps, int maxDepth, int subprocessCallCount,
                                       Map<Integer, Integer> stepsByDepth) {
            this.totalSteps = totalSteps;
            this.maxDepth = maxDepth;
            this.subprocessCallCount = subprocessCallCount;
            this.stepsByDepth = new HashMap<>(stepsByDepth);
        }

        public int getTotalSteps() { return totalSteps; }
        public int getMaxDepth() { return maxDepth; }
        public int getSubprocessCallCount() { return subprocessCallCount; }
        public Map<Integer, Integer> getStepsByDepth() { return Collections.unmodifiableMap(stepsByDepth); }

        @Override
        public String toString() {
            return String.format("RootViewAnalysis{totalSteps=%d, maxDepth=%d, subprocessCalls=%d, depthDistribution=%s}",
                    totalSteps, maxDepth, subprocessCallCount, stepsByDepth);
        }
    }
}