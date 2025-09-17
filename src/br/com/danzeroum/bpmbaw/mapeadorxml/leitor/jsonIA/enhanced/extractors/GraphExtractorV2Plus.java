package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane; // Especifica a Lane de BPD
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GraphExtractorV2Plus - VERSÃO CORRIGIDA E OTIMIZADA
 *
 * CORREÇÕES APLICADAS:
 * ✅ Lógica de Resolução de Links: Adicionado o método 'resolveFlowConnections' para mapear as conexões (edges) antes de processar o grafo. Esta é a correção principal.
 * ✅ Robustez na Extração: O método 'extractAllFlowObjectsComplete' foi aprimorado para garantir que todos os objetos de fluxo sejam capturados.
 * ✅ Conversão de Nós: O método 'convertFlowObjectsToNodesComplete' foi revisado para garantir que todos os FlowObjects sejam convertidos em ProcessNodeV2Plus.
 * ✅ Extração de Lanes: Simplificada para focar na estrutura correta.
 *
 * @version 2.4.0-hotfix
 */
public class GraphExtractorV2Plus {

    private static boolean detailedLogging = false;

    public static ProcessGraphV2Plus extractGraph(BusinessProcessDiagram bpd) {
        String graphId = bpd != null ? bpd.getId() : "unknown-graph";
        ProcessGraphV2Plus graph = ProcessGraphV2Plus.create(graphId);

        if (bpd == null) {
            System.out.println("⚠️ BPD is null, returning empty graph.");
            return graph;
        }

        try {
            System.out.println("🔄 Starting graph extraction for BPD: " + bpd.getId());

            // 1. Extrair todos os componentes visuais do processo.
            List<FlowObject> allFlowObjects = extractAllFlowObjectsComplete(bpd);
            System.out.println("[LOG-GRAPH] Total de FlowObjects (nós potenciais) encontrados: " + allFlowObjects.size());
            if(allFlowObjects.isEmpty()){
                System.err.println("[LOG-GRAPH-ERRO] Nenhum FlowObject foi encontrado. O grafo ficará vazio.");
            }
            // 2. *** CORREÇÃO CENTRAL *** Mapear as conexões (setas) para seus nós de origem e destino.
            // Esta etapa é crucial e estava faltando. Sem ela, os 'edges' não podem ser criados.
            resolveFlowConnections(bpd.getFlows(), allFlowObjects);
            long flowsResolvidos = bpd.getFlows() != null ? bpd.getFlows().stream().filter(f -> f.getSourceObjectId() != null && f.getTargetObjectId() != null).count() : 0;
            System.out.println("[LOG-GRAPH] Conexões de fluxo resolvidas: " + flowsResolvidos + " de " + (bpd.getFlows() != null ? bpd.getFlows().size() : 0));


            // 3. Converter os FlowObjects em Nós do grafo final.
            List<ProcessNodeV2Plus> nodes = convertFlowObjectsToNodesComplete(allFlowObjects);
            graph.setNodes(nodes);
            System.out.println("✅ Nodes created: " + nodes.size());

            // 4. Converter os Flows (com conexões resolvidas) em Edges do grafo.
            List<ProcessEdgeV2Plus> edges = extractEdgesComplete(bpd, allFlowObjects);
            graph.setEdges(edges);
            System.out.println("✅ Edges created: " + edges.size());
            if(edges.isEmpty() && flowsResolvidos > 0){
                System.err.println("[LOG-GRAPH-ERRO] Fluxos foram resolvidos, mas nenhuma aresta (edge) foi criada. Verifique a lógica em 'extractEdgesComplete'.");
            }

            // 5. Extrair as Lanes (raias) do processo.
            List<ProcessLaneV2Plus> lanes = extractLanesComplete(bpd);
            graph.setLanes(lanes);
            System.out.println("✅ Lanes created: " + lanes.size());

            // 6. Identificar os pontos de início e fim do processo.
            identifyEntryExitPointsComplete(graph);
            System.out.println("🏁 Entry/Exit points identified.");

        } catch (Exception e) {
            System.err.println("⚠️ Critical error during graph extraction: " + e.getMessage());
            e.printStackTrace();
        }

        return graph;
    }

    /**
     * NOVO MÉTODO ESTRATÉGICO
     * Mapeia cada Flow (seta) ao seu FlowObject de origem e destino, preenchendo os campos
     * 'sourceObjectId' e 'targetObjectId' que são @XmlTransient.
     */
    private static void resolveFlowConnections(List<Flow> flows, List<FlowObject> flowObjects) {
        if (flows == null || flowObjects == null) {
            return;
        }

        Map<String, FlowObject> flowObjectMap = flowObjects.stream()
                .collect(Collectors.toMap(FlowObject::getId, fo -> fo));

        for (Flow flow : flows) {
            // Itera por todos os objetos para encontrar a ORIGEM da seta
            for (FlowObject sourceCandidate : flowObjects) {
                if (sourceCandidate.getOutputPorts() != null) {
                    for (OutputPort port : sourceCandidate.getOutputPorts()) {
                        if (port.getFlow() != null && flow.getId().equals(port.getFlow().getRef())) {
                            flow.setSourceObjectId(sourceCandidate.getId());
                            break;
                        }
                    }
                }
                if (flow.getSourceObjectId() != null) break;
            }

            // Itera por todos os objetos para encontrar o DESTINO da seta
            for (FlowObject targetCandidate : flowObjects) {
                if (targetCandidate.getInputPorts() != null) {
                    for (InputPort port : targetCandidate.getInputPorts()) {
                        if (port.getFlow() != null && flow.getId().equals(port.getFlow().getRef())) {
                            flow.setTargetObjectId(targetCandidate.getId());
                            break;
                        }
                    }
                }
                if (flow.getTargetObjectId() != null) break;
            }
        }
    }

    // --- Demais métodos da classe (com pequenas melhorias de robustez) ---

    public static List<FlowObject> extractAllFlowObjectsComplete(BusinessProcessDiagram bpd) {
        List<FlowObject> allFlowObjects = new ArrayList<>();
        if (bpd == null) {
            return allFlowObjects;
        }

        if (bpd.getPools() != null) {
            for (Pool pool : bpd.getPools()) {
                if (pool != null && pool.getLanes() != null) {
                    for (Lane lane : pool.getLanes()) {
                        if (lane != null && lane.getFlowObjects() != null) {
                            allFlowObjects.addAll(lane.getFlowObjects());
                        }
                    }
                }
            }
        }
        return allFlowObjects;
    }

    private static List<ProcessNodeV2Plus> convertFlowObjectsToNodesComplete(List<FlowObject> flowObjects) {
        List<ProcessNodeV2Plus> nodes = new ArrayList<>();
        if (flowObjects == null) {
            return nodes;
        }

        for (FlowObject fo : flowObjects) {
            try {
                ProcessNodeV2Plus node = new ProcessNodeV2Plus();
                node.setId(fo.getId());
                node.setName(fo.getName() != null ? fo.getName() : fo.getId());
                String componentType = getFlowObjectTypeComplete(fo);
                node.setType(mapComponentTypeToV2PlusComplete(componentType));
                node.setLane("default_lane"); // Lane será atribuída depois
                node.setDescription("Component type: " + componentType);
                nodes.add(node);
            } catch (Exception e) {
                System.err.println("⚠️ Error converting FlowObject " + fo.getId() + " to node: " + e.getMessage());
            }
        }
        return nodes;
    }

    private static List<ProcessEdgeV2Plus> extractEdgesComplete(BusinessProcessDiagram bpd, List<FlowObject> flowObjects) {
        List<ProcessEdgeV2Plus> edges = new ArrayList<>();
        if (bpd == null || bpd.getFlows() == null) {
            return edges;
        }

        for (Flow flow : bpd.getFlows()) {
            if (flow.getSourceObjectId() != null && flow.getTargetObjectId() != null) {
                ProcessEdgeV2Plus edge = new ProcessEdgeV2Plus();
                edge.setId(flow.getId());
                edge.setSource(flow.getSourceObjectId());
                edge.setTarget(flow.getTargetObjectId());
                if (flow.getName() != null && !flow.getName().trim().isEmpty()) {
                    edge.setLabel(flow.getName());
                }
                edges.add(edge);
            }
        }
        return edges;
    }

    private static List<ProcessLaneV2Plus> extractLanesComplete(BusinessProcessDiagram bpd) {
        List<ProcessLaneV2Plus> lanes = new ArrayList<>();
        if (bpd == null || bpd.getPools() == null) {
            return lanes;
        }

        for (Pool pool : bpd.getPools()) {
            if (pool != null && pool.getLanes() != null) {
                for (Lane lane : pool.getLanes()) {
                    ProcessLaneV2Plus v2Lane = new ProcessLaneV2Plus();
                    v2Lane.setId(lane.getId());
                    v2Lane.setName(lane.getName() != null ? lane.getName() : lane.getId());
                    lanes.add(v2Lane);
                }
            }
        }

        if (lanes.isEmpty()) {
            lanes.add(SafeLaneSetter.createLaneSafe("default_lane", "Default Lane", "Default lane for process", new ArrayList<>()));
        }
        return lanes;
    }

    private static void identifyEntryExitPointsComplete(ProcessGraphV2Plus graph) {
        if (graph == null || graph.getNodes() == null || graph.getEdges() == null) {
            return;
        }
        Set<String> targets = graph.getEdges().stream().map(ProcessEdgeV2Plus::getTarget).collect(Collectors.toSet());
        Set<String> sources = graph.getEdges().stream().map(ProcessEdgeV2Plus::getSource).collect(Collectors.toSet());

        List<String> entryPoints = graph.getNodes().stream()
                .map(ProcessNodeV2Plus::getId)
                .filter(id -> !targets.contains(id))
                .collect(Collectors.toList());

        List<String> exitPoints = graph.getNodes().stream()
                .map(ProcessNodeV2Plus::getId)
                .filter(id -> !sources.contains(id))
                .collect(Collectors.toList());

        graph.setEntryPoints(entryPoints);
        graph.setEndPoints(exitPoints);
    }

    private static String getFlowObjectTypeComplete(FlowObject flowObject) {
        if (flowObject == null) return "unknown";
        if (flowObject.getComponentType() != null && !flowObject.getComponentType().trim().isEmpty()) {
            return flowObject.getComponentType();
        }
        if (flowObject.getComponent() != null && flowObject.getComponent().getImplementation() != null) {
            return "Service"; // Assumindo que implementações são serviços
        }
        return "Task";
    }

    private static ProcessNodeV2Plus.NodeType mapComponentTypeToV2PlusComplete(String componentType) {
        if (componentType == null) return ProcessNodeV2Plus.NodeType.TASK;
        String type = componentType.toLowerCase();
        if (type.contains("start")) return ProcessNodeV2Plus.NodeType.START_EVENT;
        if (type.contains("end")) return ProcessNodeV2Plus.NodeType.END_EVENT;
        if (type.contains("script")) return ProcessNodeV2Plus.NodeType.SCRIPT_TASK;
        if (type.contains("user") || type.contains("human")) return ProcessNodeV2Plus.NodeType.USER_TASK;
        if (type.contains("service")) return ProcessNodeV2Plus.NodeType.SERVICE_TASK;
        if (type.contains("gateway")) return ProcessNodeV2Plus.NodeType.EXCLUSIVE_GATEWAY;
        if (type.contains("subprocess")) return ProcessNodeV2Plus.NodeType.SUB_PROCESS;
        if (type.contains("callactivity")) return ProcessNodeV2Plus.NodeType.CALL_ACTIVITY;
        return ProcessNodeV2Plus.NodeType.TASK;
    }

    // Manter os demais métodos da classe original, se houver
    public static List<FlowObject> extractAllFlowObjects(BusinessProcessDiagram diagram) {
        return extractAllFlowObjectsComplete(diagram);
    }
}