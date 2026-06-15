package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.FlowNode;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.SequenceFlow;

/**
 * Extracts ProcessGraphV2 and ProcessLogicV2 from BPMN 2.0 Definitions artifacts.
 * All methods are stateless.
 */
public final class BpmnGraphExtractor {

    private BpmnGraphExtractor() {}

    public static void extractFromDefinitions(Definitions definitions, ProcessGraphV2 graph, ProcessLogicV2 logic) {
        if (definitions.getProcess() == null) return;

        if (definitions.getProcess().getFlowElements() != null) {
            for (Object element : definitions.getProcess().getFlowElements()) {
                try {
                    if (element instanceof FlowNode) {
                        ProcessNodeV2 node = convertFlowNode((FlowNode) element);
                        if (node != null) graph.addNode(node);
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Erro ao converter BPMN node: " + e.getMessage());
                }
            }
        }

        if (definitions.getProcess().getSequenceFlows() != null) {
            for (SequenceFlow sf : definitions.getProcess().getSequenceFlows()) {
                try {
                    ProcessEdgeV2 edge = convertSequenceFlow(sf);
                    if (edge != null) graph.addEdge(edge);
                } catch (Exception e) {
                    System.err.println("⚠️ Erro ao converter sequence flow: " + e.getMessage());
                }
            }
        }
    }

    public static int countNodes(Definitions def) {
        if (def.getProcess() != null && def.getProcess().getFlowElements() != null) {
            return def.getProcess().getFlowElements().size();
        }
        return 0;
    }

    public static int countEdges(Definitions def) {
        if (def.getProcess() != null && def.getProcess().getSequenceFlows() != null) {
            return def.getProcess().getSequenceFlows().size();
        }
        return 0;
    }

    public static int extractScripts(Definitions def, ProcessLogicV2 logic) {
        return 0;
    }

    // ---- private helpers ----

    private static ProcessNodeV2 convertFlowNode(FlowNode flowNode) {
        return ProcessNodeV2.createWithFlexibleId(
                flowNode.getId(), flowNode.getName(), flowNode.getClass().getSimpleName());
    }

    private static ProcessEdgeV2 convertSequenceFlow(SequenceFlow sf) {
        return ProcessEdgeV2.create(sf.getId(), sf.getSourceRef(), sf.getTargetRef());
    }
}
