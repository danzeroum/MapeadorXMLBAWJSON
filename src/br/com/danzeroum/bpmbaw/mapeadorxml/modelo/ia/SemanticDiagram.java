// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/modelo/ia/SemanticDiagram.java

package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.ia;

import java.util.Collection;
import java.util.List;

/**
 * Contêiner para a representação semântica completa do diagrama de processo.
 */
public class SemanticDiagram {

    private Collection<SemanticFlowNode> nodes;
    private List<SemanticSequenceFlow> flows;

    // Getters e Setters

    public Collection<SemanticFlowNode> getNodes() {
        return nodes;
    }

    public void setNodes(Collection<SemanticFlowNode> nodes) {
        this.nodes = nodes;
    }

    public List<SemanticSequenceFlow> getFlows() {
        return flows;
    }

    public void setFlows(List<SemanticSequenceFlow> flows) {
        this.flows = flows;
    }
}