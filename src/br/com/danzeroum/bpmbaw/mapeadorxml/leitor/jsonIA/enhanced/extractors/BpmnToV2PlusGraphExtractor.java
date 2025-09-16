package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessEdgeV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessGraphV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessLaneV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessNodeV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;

public class BpmnToV2PlusGraphExtractor {

    public static ProcessGraphV2Plus extractGraph(Process process) {
        ProcessGraphV2Plus graph = ProcessGraphV2Plus.create(process.getId());

        // Extrai Nós (Nodes)
        if (process.getFlowElements() != null) {
            for (Object element : process.getFlowElements()) {
                if (element instanceof FlowNode) {
                    FlowNode fn = (FlowNode) element;
                    ProcessNodeV2Plus node = new ProcessNodeV2Plus(
                            fn.getId(),
                            ProcessNodeV2Plus.NodeType.fromString(fn.getClass().getSimpleName()),
                            fn.getName(),
                            null // A lane será atribuída depois
                    );
                    graph.addNode(node);
                }
            }
        }

        // Extrai Conexões (Edges)
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

        // Extrai Raias (Lanes) e associa aos nós
        if (process.getLaneSet() != null && process.getLaneSet().getLanes() != null) {
            for (Lane lane : process.getLaneSet().getLanes()) {
                ProcessLaneV2Plus pLane = new ProcessLaneV2Plus(lane.getId(), lane.getName(), ProcessLaneV2Plus.LaneType.ROLE);
                graph.addLane(pLane);

                if (lane.getFlowNodeRefs() != null) {
                    for (String nodeId : lane.getFlowNodeRefs()) {
                        ProcessNodeV2Plus node = graph.findNode(nodeId);
                        if (node != null) {
                            node.setLane(lane.getId());
                        }
                    }
                }
            }
        }

        graph.autoDetectEntryPoints();
        graph.autoDetectExitPoints();

        return graph;
    }
}