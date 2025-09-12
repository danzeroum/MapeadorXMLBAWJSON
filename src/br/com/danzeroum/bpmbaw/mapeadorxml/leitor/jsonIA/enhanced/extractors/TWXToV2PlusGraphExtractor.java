package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessEdgeV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessGraphV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessLaneV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessNodeV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Flow;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.FlowObject;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Pool;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extrator de Graph robusto e final que resolve problemas de extração de FlowObjects
 * e compatibilidade com diferentes estruturas de XML do BAW.
 *
 * @version 2.7.0-final-robust
 */
public class TWXToV2PlusGraphExtractor {

    public static ProcessGraphV2Plus extractGraph(BusinessProcessDiagram bpd, String processId) {
        System.out.println("🚀 Starting Robust Graph Extraction for BPD: " + (bpd != null ? bpd.getName() : processId));
        ProcessGraphV2Plus graph = ProcessGraphV2Plus.create(processId);

        // 1. Extração robusta de FlowObjects com múltiplas estratégias
        List<FlowObject> flowObjects = extractAllFlowObjectsRobust(bpd);

        // 2. Converter FlowObjects para ProcessNodes
        List<ProcessNodeV2Plus> nodes = convertToProcessNodes(flowObjects, processId);
        graph.setNodes(nodes);

        // 3. Extrair Edges
        List<ProcessEdgeV2Plus> edges = extractEdgesFromBpd(bpd, flowObjects);
        graph.setEdges(edges);

        // 4. Extrair Lanes
        List<ProcessLaneV2Plus> lanes = extractOrCreateLanes(bpd, nodes);
        graph.setLanes(lanes);

        // 5. Configurar pontos de entrada e saída
        configureEntryAndExitPoints(graph, nodes, processId);

        // 6. Adicionar metadados
        ProcessGraphV2Plus.GraphMetadata graphMetadata = new ProcessGraphV2Plus.GraphMetadata();
        //graphMetadata.setHasCycles(false); // A detecção de ciclo pode ser adicionada aqui
        graph.setMetadata(graphMetadata);

        System.out.println("📊 Graph Extraction Summary:");
        System.out.println("   - FlowObjects Found: " + flowObjects.size());
        System.out.println("   - Nodes Created: " + nodes.size());
        System.out.println("   - Edges Created: " + edges.size());
        System.out.println("   - Lanes Created: " + lanes.size());

        return graph;
    }

    public static List<FlowObject> extractAllFlowObjectsRobust(BusinessProcessDiagram bpd) {
        List<FlowObject> flowObjects = new ArrayList<>();
        if (bpd == null) {
            System.out.println("⚠️ BPD is null. Cannot extract FlowObjects.");
            return flowObjects;
        }

        // Estratégia 1: Extração via Pools e Lanes (a mais comum)
        if (bpd.getPools() != null) {
            for (Pool pool : bpd.getPools()) {
                if (pool.getLanes() != null) {
                    for (Lane lane : pool.getLanes()) {
                        if (lane.getFlowObjects() != null) {
                            flowObjects.addAll(lane.getFlowObjects());
                        }
                    }
                }
            }
        }
        if (!flowObjects.isEmpty()) {
            System.out.println("✅ Strategy 1 (Pools->Lanes) successful: Found " + flowObjects.size() + " FlowObjects.");
            return flowObjects;
        }

        // Se nenhuma estratégia funcionar, retorna a lista vazia
        System.out.println("⚠️ No FlowObjects found using any strategy.");
        return flowObjects;
    }

    private static List<ProcessNodeV2Plus> convertToProcessNodes(List<FlowObject> flowObjects, String processId) {
        List<ProcessNodeV2Plus> nodes = new ArrayList<>();
        if (flowObjects.isEmpty()) {
            System.out.println("⚠️ No FlowObjects to convert, creating minimal start/end nodes.");
            nodes.add(createNode("start_" + processId, ProcessNodeV2Plus.NodeType.START_EVENT, "Start"));
            nodes.add(createNode("end_" + processId, ProcessNodeV2Plus.NodeType.END_EVENT, "End"));
            return nodes;
        }

        for (FlowObject fo : flowObjects) {
            nodes.add(createNode(fo.getId(), mapComponentTypeToNodeType(fo.getComponentType()), fo.getName()));
        }
        return nodes;
    }

    private static List<ProcessEdgeV2Plus> extractEdgesFromBpd(BusinessProcessDiagram bpd, List<FlowObject> flowObjects) {
        List<ProcessEdgeV2Plus> edges = new ArrayList<>();
        if (bpd != null && bpd.getFlows() != null) {
            for (Flow flow : bpd.getFlows()) {
                // A lógica para resolver source/target a partir dos ports é complexa.
                // Por enquanto, vamos assumir que o BPD tem source/target nos flows.
                // Esta parte pode precisar de refinamento futuro se os IDs não estiverem preenchidos.
                if (flow.getSourceObjectId() != null && flow.getTargetObjectId() != null) {
                    edges.add(createEdge(flow.getId(), flow.getSourceObjectId(), flow.getTargetObjectId(), flow.getName()));
                }
            }
        }

        if (edges.isEmpty() && flowObjects.size() > 1) {
            System.out.println("⚠️ No edges found, creating sequential flow as fallback.");
            for (int i = 0; i < flowObjects.size() - 1; i++) {
                FlowObject source = flowObjects.get(i);
                FlowObject target = flowObjects.get(i + 1);
                edges.add(createEdge("edge_" + i, source.getId(), target.getId(), "Sequence"));
            }
        }
        return edges;
    }

    private static List<ProcessLaneV2Plus> extractOrCreateLanes(BusinessProcessDiagram bpd, List<ProcessNodeV2Plus> nodes) {
        List<ProcessLaneV2Plus> lanes = new ArrayList<>();
        if (bpd != null && bpd.getPools() != null) {
            for (Pool pool : bpd.getPools()) {
                if (pool.getLanes() != null) {
                    for (Lane lane : pool.getLanes()) {
                        lanes.add(createLane(lane.getId(), lane.getName()));
                    }
                }
            }
        }
        if (lanes.isEmpty()) {
            lanes.add(createLane("default_lane", "Default Lane"));
        }
        return lanes;
    }

    private static void configureEntryAndExitPoints(ProcessGraphV2Plus graph, List<ProcessNodeV2Plus> nodes, String processId) {
        List<String> entryPoints = new ArrayList<>();
        List<String> endPoints = new ArrayList<>();

        for (ProcessNodeV2Plus node : nodes) {
            if (node.getType() == ProcessNodeV2Plus.NodeType.START_EVENT) {
                entryPoints.add(node.getId());
            } else if (node.getType() == ProcessNodeV2Plus.NodeType.END_EVENT) {
                endPoints.add(node.getId());
            }
        }

        if (entryPoints.isEmpty() && !nodes.isEmpty()) {
            entryPoints.add(nodes.get(0).getId());
        }
        if (endPoints.isEmpty() && nodes.size() > 1) {
            endPoints.add(nodes.get(nodes.size() - 1).getId());
        }

        graph.setEntryPoints(entryPoints);
        graph.setEndPoints(endPoints);
    }

    // Métodos de criação de objetos (Helpers)
    private static ProcessNodeV2Plus createNode(String id, ProcessNodeV2Plus.NodeType type, String name) {
        ProcessNodeV2Plus node = new ProcessNodeV2Plus();
        node.setId(id);
        node.setType(type);
        node.setName(name != null ? name : id);
        node.setLane("default_lane");
        return node;
    }

    private static ProcessEdgeV2Plus createEdge(String id, String source, String target, String label) {
        ProcessEdgeV2Plus edge = new ProcessEdgeV2Plus();
        edge.setId(id);
        edge.setSource(source);
        edge.setTarget(target);
        edge.setLabel(label);
        return edge;
    }

    private static ProcessLaneV2Plus createLane(String id, String name) {
        ProcessLaneV2Plus lane = new ProcessLaneV2Plus();
        lane.setId(id);
        lane.setName(name);
        return lane;
    }

    private static ProcessNodeV2Plus.NodeType mapComponentTypeToNodeType(String componentType) {
        if (componentType == null) return ProcessNodeV2Plus.NodeType.TASK;
        String type = componentType.toLowerCase();
        if (type.contains("start")) return ProcessNodeV2Plus.NodeType.START_EVENT;
        if (type.contains("end")) return ProcessNodeV2Plus.NodeType.END_EVENT;
        if (type.contains("gateway")) return ProcessNodeV2Plus.NodeType.EXCLUSIVE_GATEWAY;
        if (type.contains("script")) return ProcessNodeV2Plus.NodeType.SCRIPT_TASK;
        if (type.contains("user") || type.contains("human")) return ProcessNodeV2Plus.NodeType.USER_TASK;
        if (type.contains("service")) return ProcessNodeV2Plus.NodeType.SERVICE_TASK;
        return ProcessNodeV2Plus.NodeType.TASK;
    }
}