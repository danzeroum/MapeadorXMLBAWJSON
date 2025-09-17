// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/jsonIA/enhanced/extractors/BpmnToV2PlusGraphExtractor.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessEdgeV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessGraphV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessLaneV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessNodeV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;

import java.util.HashMap;
import java.util.Map;

public class BpmnToV2PlusGraphExtractor {

    public static ProcessGraphV2Plus extractGraph(Process process) {
        ProcessGraphV2Plus graph = ProcessGraphV2Plus.create(process.getId());

        // --- LÓGICA DE MELHORIA INSPIRADA NO NAVIGATOR ---
        // 1. Criar um mapa de ID de nó para o NOME da Lane correspondente.
        Map<String, String> nodeIdToLaneNameMap = new HashMap<>();
        if (process.getLaneSet() != null && process.getLaneSet().getLanes() != null) {
            for (Lane lane : process.getLaneSet().getLanes()) {
                if (lane.getFlowNodeRefs() != null) {
                    for (String nodeId : lane.getFlowNodeRefs()) {
                        // Mapeia o ID do nó para o NOME da lane, não o ID da lane.
                        nodeIdToLaneNameMap.put(nodeId, lane.getName());
                    }
                }
            }
        }

        // 2. Extrai Nós (Nodes) e já associa o nome da Lane
        if (process.getFlowElements() != null) {
            for (Object element : process.getFlowElements()) {
                if (element instanceof FlowNode) {
                    FlowNode fn = (FlowNode) element;
                    ProcessNodeV2Plus.NodeType nodeType = ProcessNodeV2Plus.NodeType.fromString(fn.getClass().getSimpleName());

                    // Busca o nome da lane no mapa
                    String laneName = nodeIdToLaneNameMap.get(fn.getId());

                    ProcessNodeV2Plus node = new ProcessNodeV2Plus(
                            fn.getId(),
                            nodeType,
                            fn.getName(),
                            laneName // Associa o nome da lane diretamente aqui
                    );

                    if (nodeType == ProcessNodeV2Plus.NodeType.SCRIPT_TASK) {
                        node.setLogicRef("lg:" + fn.getId());
                    }
                    graph.addNode(node);
                }
            }
        }
        // --- FIM DA LÓGICA DE MELHORIA ---

        // Extrai Conexões (Edges) - Sem alterações
        if (process.getSequenceFlows() != null) {
            for (SequenceFlow sf : process.getSequenceFlows()) {
                ProcessEdgeV2Plus edge = new ProcessEdgeV2Plus(
                        sf.getId(),
                        sf.getSourceRef(),
                        sf.getTargetRef(),
                        sf.getName()
                );
                graph.addEdge(edge);
            }
        }

        // Extrai Raias (Lanes) - Sem alterações, já que agora usamos para mapeamento
        if (process.getLaneSet() != null && process.getLaneSet().getLanes() != null) {
            for (Lane lane : process.getLaneSet().getLanes()) {
                ProcessLaneV2Plus pLane = new ProcessLaneV2Plus(lane.getId(), lane.getName(), ProcessLaneV2Plus.LaneType.ROLE);
                graph.addLane(pLane);
                // A associação dos nós à lane já foi feita acima
            }
        }

        graph.autoDetectEntryPoints();
        graph.autoDetectExitPoints();

        return graph;
    }

}