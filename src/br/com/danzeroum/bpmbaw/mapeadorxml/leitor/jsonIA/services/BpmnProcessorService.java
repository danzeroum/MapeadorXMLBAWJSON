package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoader;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportGeneratorV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;

import java.util.*;

/**
 * Collection of specialized processor services for different artifact types.
 * Each processor handles a specific type of IBM BAW artifact.
 */

// ==================================================
// BPMN PROCESSOR SERVICE
// ==================================================

/**
 * Processes modern BPMN Definitions artifacts.
 */
public class BpmnProcessorService {
    private final JsonReportGeneratorV2 generator;
    private final ProcessLoader loader;
    private final VariableEnricherService variableEnricher;

    public BpmnProcessorService(JsonReportGeneratorV2 generator, ProcessLoader loader,
                                VariableEnricherService variableEnricher) {
        this.generator = generator;
        this.loader = loader;
        this.variableEnricher = variableEnricher;
    }

    public void processDefinitions(Definitions definitions, JsonReportV2.Artifact artifact) {
        Process process = definitions.getProcess();
        if (process == null) return;

        // Process variables
        populateBpmnVariables(process, artifact);

        // Process participants from lanes
        addParticipantsFromLanes(process, artifact);

        // Build graph structure
        buildGraphStructure(process, artifact);

        // Generate flow steps
        generateFlowSteps(process, artifact);

        // Enrich all variables
        variableEnricher.enrichAllVariablesInArtifact(artifact);
    }

    private void populateBpmnVariables(Process process, JsonReportV2.Artifact artifact) {
        IoSpecification io = process.getIoSpecification();
        if (io == null) return;

        if (io.getDataInputs() != null) {
            for (DataInput di : io.getDataInputs()) {
                JsonReportV2.VariableInfo v = new JsonReportV2.VariableInfo();
                v.setName(di.getName());
                v.setTypeId(di.getItemSubjectRef());
                v.setList(di.getIsCollection() != null && di.getIsCollection());
                artifact.getVariables().getInput().add(v);
            }
        }

        if (io.getDataOutputs() != null) {
            for (DataOutput d : io.getDataOutputs()) {
                JsonReportV2.VariableInfo v = new JsonReportV2.VariableInfo();
                v.setName(d.getName());
                v.setTypeId(d.getItemSubjectRef());
                v.setList(d.getIsCollection() != null && d.getIsCollection());
                artifact.getVariables().getOutput().add(v);
            }
        }
    }

    private void addParticipantsFromLanes(Process process, JsonReportV2.Artifact artifact) {
        if (process.getLaneSet() == null || process.getLaneSet().getLanes() == null) return;

        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Lane lane : process.getLaneSet().getLanes()) {
            if (lane.getPartitionElementRef() != null && !lane.getPartitionElementRef().trim().isEmpty()) {
                String participantName = resolveParticipantName(lane.getPartitionElementRef());
                if (participantName == null || participantName.trim().isEmpty()) {
                    participantName = lane.getPartitionElementRef();
                }
                if (!artifact.getParticipants().contains(participantName)) {
                    artifact.getParticipants().add(participantName);
                }
            }
        }
    }

    private String resolveParticipantName(String participantId) {
        try {
            ProcessLoader.ArtifactLocation loc = loader.findArtifactLocation(participantId);
            if (loc != null && loc.objectInfo != null && loc.objectInfo.getName() != null) {
                return loc.objectInfo.getName();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void buildGraphStructure(Process process, JsonReportV2.Artifact artifact) {
        Map<String, FlowNode> nodeMap = createNodeMap(process.getFlowElements());
        Map<String, String> nodeToLane = mapNodeToLaneNames(process);

        // Build nodes
        for (FlowNode node : nodeMap.values()) {
            JsonReportV2.Node graphNode = new JsonReportV2.Node();
            graphNode.setId(node.getId());
            graphNode.setType(getNodeType(node));
            graphNode.setName(node.getName() != null ? node.getName() : node.getId());
            graphNode.setLane(nodeToLane.get(node.getId()));
            artifact.getGraph().getNodes().add(graphNode);
        }

        // Build edges
        if (process.getSequenceFlows() != null) {
            for (SequenceFlow sf : process.getSequenceFlows()) {
                JsonReportV2.Edge edge = new JsonReportV2.Edge();
                edge.setId(sf.getId());
                edge.setSource(sf.getSourceRef());
                edge.setTarget(sf.getTargetRef());
                edge.setLabel(sf.getName());
                artifact.getGraph().getEdges().add(edge);

                // Add condition if present
                if (sf.getConditionExpression() != null && sf.getConditionExpression().getExpression() != null) {
                    JsonReportV2.EdgeCondition ec = new JsonReportV2.EdgeCondition();
                    ec.setEdgeId(sf.getId());
                    ec.setExpression(normalizeExpression(sf.getConditionExpression().getExpression()));
                    ec.setDefault(false);
                    ec.setLanguage("TWX-Expr");
                    artifact.getGraph().getConditions().add(ec);
                }
            }
        }

        // Build gateways
        for (FlowNode node : nodeMap.values()) {
            if (node instanceof ExclusiveGateway) {
                ExclusiveGateway gw = (ExclusiveGateway) node;
                JsonReportV2.Gateway gateway = new JsonReportV2.Gateway();
                gateway.setId(gw.getId());
                gateway.setType("ExclusiveGateway");
                gateway.setDefaultFlow(gw.getDefaultFlow());
                artifact.getGraph().getGateways().add(gateway);
            }
        }

        // Set entry and exit points
        List<String> entryPoints = new ArrayList<>();
        List<String> exitPoints = new ArrayList<>();
        for (FlowNode node : nodeMap.values()) {
            if (node instanceof StartEvent) entryPoints.add(node.getId());
            if (node instanceof EndEvent) exitPoints.add(node.getId());
        }
        artifact.getGraph().getEntryPoints().addAll(entryPoints);
        artifact.getGraph().getEndPoints().addAll(exitPoints);
    }

    private void generateFlowSteps(Process process, JsonReportV2.Artifact artifact) {
        Map<String, FlowNode> nodeMap = createNodeMap(process.getFlowElements());
        Map<String, List<SequenceFlow>> flowMap = createSourceIdToFlowMap(process.getSequenceFlows());
        Map<String, String> nodeToLane = mapNodeToLaneNames(process);

        // Create ordered list of nodes
        List<FlowNode> orderedNodes = createOrderedNodeList(nodeMap, flowMap);

        int orderIndex = 0;
        for (FlowNode node : orderedNodes) {
            JsonReportV2.FlowStep step = new JsonReportV2.FlowStep();
            step.setStepId(node.getId());
            step.setName(node.getName());
            step.setType(getNodeType(node));
            step.setOrderIndex(orderIndex++);
            step.setLane(nodeToLane.get(node.getId()));

            if (node.getIncoming() != null) step.getIncomingFlows().addAll(node.getIncoming());
            if (node.getOutgoing() != null) step.getOutgoingFlows().addAll(node.getOutgoing());

            // Handle specific node types
            if (node instanceof ScriptTask) {
                step.setScript(((ScriptTask) node).getScript());
            } else if (node instanceof CallActivity) {
                CallActivity ca = (CallActivity) node;
                step.setCalledArtifactId(ca.getCalledElement());
                step.setParameterMapping(createParameterMapping(ca));
            } else if (node instanceof ExclusiveGateway) {
                step.getConditions().addAll(createGatewayConditions((ExclusiveGateway) node, flowMap));
            }

            artifact.getFlow().add(step);
        }
    }

    // Helper methods
    private Map<String, FlowNode> createNodeMap(List<Object> elements) {
        Map<String, FlowNode> map = new LinkedHashMap<>();
        if (elements == null) return map;
        for (Object el : elements) {
            if (el instanceof FlowNode) {
                FlowNode fn = (FlowNode) el;
                if (fn.getId() != null) map.put(fn.getId(), fn);
            }
        }
        return map;
    }

    private Map<String, List<SequenceFlow>> createSourceIdToFlowMap(List<SequenceFlow> flows) {
        Map<String, List<SequenceFlow>> map = new HashMap<>();
        if (flows == null) return map;
        for (SequenceFlow f : flows) {
            if (f.getSourceRef() != null) {
                map.computeIfAbsent(f.getSourceRef(), k -> new ArrayList<>()).add(f);
            }
        }
        return map;
    }

    private List<FlowNode> createOrderedNodeList(Map<String, FlowNode> nodeMap,
                                                 Map<String, List<SequenceFlow>> flowMap) {
        // Simple topological sort starting from StartEvents
        List<FlowNode> ordered = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        // Find start nodes
        for (FlowNode node : nodeMap.values()) {
            if (node instanceof StartEvent) {
                dfsTraversal(node.getId(), nodeMap, flowMap, visited, ordered);
            }
        }

        // Add remaining unvisited nodes
        for (FlowNode node : nodeMap.values()) {
            if (!visited.contains(node.getId())) {
                ordered.add(node);
            }
        }

        return ordered;
    }

    private void dfsTraversal(String nodeId, Map<String, FlowNode> nodeMap,
                              Map<String, List<SequenceFlow>> flowMap,
                              Set<String> visited, List<FlowNode> ordered) {
        if (!visited.add(nodeId)) return;

        FlowNode node = nodeMap.get(nodeId);
        if (node != null) {
            ordered.add(node);

            List<SequenceFlow> outgoing = flowMap.get(nodeId);
            if (outgoing != null) {
                for (SequenceFlow flow : outgoing) {
                    dfsTraversal(flow.getTargetRef(), nodeMap, flowMap, visited, ordered);
                }
            }
        }
    }

    private JsonReportV2.ParameterMapping createParameterMapping(CallActivity callActivity) {
        JsonReportV2.ParameterMapping pm = new JsonReportV2.ParameterMapping();

        if (callActivity.getDataInputAssociations() != null) {
            for (DataInputAssociation dia : callActivity.getDataInputAssociations()) {
                if (dia.getAssignment() != null && dia.getAssignment().getFrom() != null) {
                    JsonReportV2.Mapping m = new JsonReportV2.Mapping();
                    m.setSource(dia.getAssignment().getFrom().getExpression());
                    m.setTarget(dia.getTargetRef());
                    pm.getInput().add(m);
                }
            }
        }

        if (callActivity.getDataOutputAssociations() != null) {
            for (DataOutputAssociation doa : callActivity.getDataOutputAssociations()) {
                if (doa.getAssignment() != null && doa.getAssignment().getTo() != null) {
                    JsonReportV2.Mapping m = new JsonReportV2.Mapping();
                    m.setSource(doa.getSourceRef());
                    m.setTarget(doa.getAssignment().getTo().getContent());
                    pm.getOutput().add(m);
                }
            }
        }

        return pm;
    }

    private List<JsonReportV2.Condition> createGatewayConditions(ExclusiveGateway gateway,
                                                                 Map<String, List<SequenceFlow>> flowMap) {
        List<JsonReportV2.Condition> conditions = new ArrayList<>();
        List<SequenceFlow> outgoing = flowMap.get(gateway.getId());
        if (outgoing == null) return conditions;

        for (SequenceFlow sf : outgoing) {
            JsonReportV2.Condition condition = new JsonReportV2.Condition();
            condition.setTargetStepId(sf.getTargetRef());
            condition.setName(sf.getName());

            if (sf.getConditionExpression() != null && sf.getConditionExpression().getExpression() != null) {
                condition.setExpression(normalizeExpression(sf.getConditionExpression().getExpression()));
                condition.setExpressionLanguage("TWX-Expr");
            } else {
                condition.setExpression("true");
                condition.setExpressionLanguage("TWX-Expr");
            }

            condition.setDefault(sf.getId() != null && sf.getId().equals(gateway.getDefaultFlow()));
            conditions.add(condition);
        }

        return conditions;
    }

    private Map<String, String> mapNodeToLaneNames(Process process) {
        Map<String, String> nodeToLane = new HashMap<>();
        if (process.getLaneSet() == null || process.getLaneSet().getLanes() == null) return nodeToLane;

        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Lane lane : process.getLaneSet().getLanes()) {
            String participantName = resolveParticipantName(lane.getPartitionElementRef());
            if (participantName == null) continue;

            // Try to get flow node refs using reflection
            try {
                java.lang.reflect.Method getFlowNodeRefs = lane.getClass().getMethod("getFlowNodeRefs");
                Object refs = getFlowNodeRefs.invoke(lane);
                if (refs instanceof List) {
                    for (Object ref : (List<?>) refs) {
                        if (ref instanceof String) {
                            nodeToLane.put((String) ref, participantName);
                        }
                    }
                }
            } catch (Exception ignored) {
                // Try alternative method names
                String[] methodNames = {"getFlowNodeRefIds", "getFlowNodes", "getNodeRefs"};
                for (String methodName : methodNames) {
                    try {
                        java.lang.reflect.Method method = lane.getClass().getMethod(methodName);
                        Object refs = method.invoke(lane);
                        if (refs instanceof List) {
                            for (Object ref : (List<?>) refs) {
                                String nodeId = ref instanceof String ? (String) ref : String.valueOf(ref);
                                nodeToLane.put(nodeId, participantName);
                            }
                            break;
                        }
                    } catch (Exception ignored2) {}
                }
            }
        }

        return nodeToLane;
    }

    private String getNodeType(FlowNode node) {
        if (node instanceof CallActivity) return "CallActivity";
        if (node instanceof SubProcess) return "SubProcess";
        if (node instanceof ScriptTask) return "ScriptTask";
        if (node instanceof FormTask) return "FormTask";
        if (node instanceof Task) return "Task";
        if (node instanceof StartEvent) return "StartEvent";
        if (node instanceof EndEvent) return "EndEvent";
        if (node instanceof ExclusiveGateway) return "ExclusiveGateway";
        return "FlowNode";
    }

    private String normalizeExpression(String expr) {
        return expr != null ? expr.replaceAll("\\s+", " ").trim() : "true";
    }
}

// ==================================================
// TEAMWORKS PROCESSOR SERVICE
// ==================================================

// ==================================================
// OTHER PROCESSOR SERVICES
// ==================================================

