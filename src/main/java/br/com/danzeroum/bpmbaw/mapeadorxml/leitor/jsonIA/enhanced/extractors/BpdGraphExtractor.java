package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.ProcessParameter;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.ProcessVariable;

import java.util.*;

/**
 * Extracts ProcessGraphV2, ProcessLogicV2, and DataTypeDefinitionV2 from
 * IBM BAW legacy BPD (Teamworks) artifacts. All methods are stateless.
 */
public final class BpdGraphExtractor {

    private BpdGraphExtractor() {}

    public static void extractFromBpd(Teamworks teamworks, ProcessGraphV2 graph) {
        if (teamworks.getBpd() == null || teamworks.getBpd().getBusinessProcessDiagram() == null) {
            return;
        }

        BusinessProcessDiagram bpd = teamworks.getBpd().getBusinessProcessDiagram();

        List<FlowObject> allFlowObjects = new ArrayList<>();
        if (bpd.getPools() != null) {
            for (Pool pool : bpd.getPools()) {
                if (pool.getLanes() != null) {
                    for (Lane lane : pool.getLanes()) {
                        if (lane.getFlowObjects() != null) {
                            allFlowObjects.addAll(lane.getFlowObjects());
                        }
                    }
                }
            }
        }

        for (FlowObject flowObject : allFlowObjects) {
            try {
                ProcessNodeV2 node = convertFlowObjectToNode(flowObject);
                if (node != null) graph.addNode(node);
            } catch (Exception e) {
                System.err.println("⚠️ Erro ao converter FlowObject para nó: " + e.getMessage());
            }
        }

        if (bpd.getFlows() != null && !bpd.getFlows().isEmpty()) {
            buildLegacyBpdLinkMap(bpd.getFlows(), allFlowObjects);

            int edgeCount = 0;
            for (Flow flow : bpd.getFlows()) {
                try {
                    if (flow.getSourceObjectId() != null && flow.getTargetObjectId() != null) {
                        ProcessEdgeV2 edge = ProcessEdgeV2.create(
                                flow.getId() != null ? flow.getId() : generateEdgeId(),
                                flow.getSourceObjectId(),
                                flow.getTargetObjectId()
                        );
                        edge.setLabel(flow.getName());
                        edge.setType(ProcessEdgeV2.EdgeType.SEQUENCE_FLOW);
                        graph.addEdge(edge);
                        edgeCount++;
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao adicionar edge: " + e.getMessage());
                }
            }
            System.out.println("✅ Processados " + edgeCount + " edges com sucesso");
        }
    }

    public static void extractFromTeamworksProcess(
            br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process,
            ProcessGraphV2 graph, ProcessLogicV2 logic) {

        if (process.getItems() != null) {
            for (Item item : process.getItems()) {
                try {
                    ProcessNodeV2 node = convertItemToNode(item);
                    if (node != null) graph.addNode(node);
                } catch (Exception e) {
                    System.err.println("⚠️ Erro ao converter item: " + e.getMessage());
                }
            }
        }

        if (process.getLinks() != null) {
            for (Link link : process.getLinks()) {
                try {
                    ProcessEdgeV2 edge = convertLinkToEdge(link);
                    if (edge != null) graph.addEdge(edge);
                } catch (Exception e) {
                    System.err.println("⚠️ Erro ao converter link: " + e.getMessage());
                }
            }
        }
    }

    public static int extractScriptsFromProcess(
            br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process,
            ProcessLogicV2 logic) {

        int scriptCount = 0;
        if (process.getItems() != null) {
            for (Item item : process.getItems()) {
                if (item.getTwComponent() != null && item.getTwComponent().getScript() != null) {
                    ProcessLogicV2.LogicScriptV2 script = new ProcessLogicV2.LogicScriptV2();
                    script.setId("script-" + item.getProcessItemId());
                    script.setContent(item.getTwComponent().getScript());
                    script.setLanguage(ProcessLogicV2.ScriptLanguage.JAVASCRIPT);
                    logic.getScripts().add(script);
                    scriptCount++;
                }
            }
        }
        return scriptCount;
    }

    public static List<DataTypeDefinitionV2> extractDataTypes(Teamworks teamworks) {
        List<DataTypeDefinitionV2> dataTypes = new ArrayList<>();
        if (teamworks.getProcess() == null) return dataTypes;

        if (teamworks.getProcess().getProcessParameters() != null) {
            for (ProcessParameter param : teamworks.getProcess().getProcessParameters()) {
                DataTypeDefinitionV2 dt = convertParameterToDataType(param);
                if (dt != null) dataTypes.add(dt);
            }
        }

        if (teamworks.getProcess().getProcessVariables() != null) {
            for (ProcessVariable var : teamworks.getProcess().getProcessVariables()) {
                DataTypeDefinitionV2 dt = convertVariableToDataType(var);
                if (dt != null) dataTypes.add(dt);
            }
        }
        return dataTypes;
    }

    public static int countNodesInBpd(Bpd bpd) {
        int count = 0;
        if (bpd.getBusinessProcessDiagram() != null && bpd.getBusinessProcessDiagram().getPools() != null) {
            for (Pool pool : bpd.getBusinessProcessDiagram().getPools()) {
                if (pool.getLanes() != null) {
                    for (Lane lane : pool.getLanes()) {
                        if (lane.getFlowObjects() != null) count += lane.getFlowObjects().size();
                    }
                }
            }
        }
        return count;
    }

    public static int countEdgesInBpd(Bpd bpd) {
        if (bpd.getBusinessProcessDiagram() != null && bpd.getBusinessProcessDiagram().getFlows() != null) {
            return bpd.getBusinessProcessDiagram().getFlows().size();
        }
        return 0;
    }

    // ---- private helpers ----

    private static void buildLegacyBpdLinkMap(List<Flow> flows, List<FlowObject> allFlowObjects) {
        for (Flow flow : flows) {
            for (FlowObject fo : allFlowObjects) {
                if (fo.getOutputPorts() != null) {
                    for (OutputPort port : fo.getOutputPorts()) {
                        if (port.getFlow() != null && flow.getId() != null
                                && flow.getId().equals(port.getFlow().getRef())) {
                            flow.setSourceObjectId(fo.getId());
                            break;
                        }
                    }
                }
                if (fo.getInputPorts() != null) {
                    for (InputPort port : fo.getInputPorts()) {
                        if (port.getFlow() != null && flow.getId() != null
                                && flow.getId().equals(port.getFlow().getRef())) {
                            flow.setTargetObjectId(fo.getId());
                            break;
                        }
                    }
                }
            }
        }
    }

    private static ProcessNodeV2 convertFlowObjectToNode(FlowObject flowObject) {
        if (flowObject == null) return null;
        try {
            ProcessNodeV2 node = ProcessNodeV2.createWithFlexibleId(
                    flowObject.getId(), flowObject.getName(), flowObject.getComponentType());
            node.setDescription(flowObject.getName());
            return node;
        } catch (Exception e) {
            ProcessNodeV2 fallback = new ProcessNodeV2();
            fallback.setId("node-" + System.currentTimeMillis());
            fallback.setName(flowObject.getName() != null ? flowObject.getName() : "Unnamed Node");
            fallback.setType(ProcessNodeV2.NodeType.TASK);
            return fallback;
        }
    }

    private static ProcessNodeV2 convertItemToNode(Item item) {
        return ProcessNodeV2.createWithFlexibleId(
                item.getProcessItemId(), item.getName(), item.getTWComponentName());
    }

    private static ProcessEdgeV2 convertLinkToEdge(Link link) {
        return ProcessEdgeV2.create(
                link.getProcessLinkId(), link.getFromProcessItemId(), link.getToProcessItemId());
    }

    private static DataTypeDefinitionV2 convertParameterToDataType(ProcessParameter param) {
        DataTypeDefinitionV2 dt = new DataTypeDefinitionV2();
        dt.setId(param.getClassId());
        dt.setName(param.getName());
        dt.setDescription("Process parameter: " + (param.getParameterType() == 1 ? "Input" : "Output"));
        return dt;
    }

    private static DataTypeDefinitionV2 convertVariableToDataType(ProcessVariable var) {
        DataTypeDefinitionV2 dt = new DataTypeDefinitionV2();
        dt.setId(var.getClassId());
        dt.setName(var.getName());
        dt.setDescription("Process variable");
        return dt;
    }

    private static String generateEdgeId() {
        return "edge_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
}
