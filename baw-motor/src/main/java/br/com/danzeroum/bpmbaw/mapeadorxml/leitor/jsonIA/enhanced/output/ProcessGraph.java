package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output;

import java.util.List;

/**
 * Process graph
 */
class ProcessGraph {
    private List<ProcessNode> nodes;
    private List<ProcessEdge> edges;
    private List<ProcessLane> lanes;

    // Getters and setters...
    public List<ProcessNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ProcessNode> nodes) {
        this.nodes = nodes;
    }

    public List<ProcessEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<ProcessEdge> edges) {
        this.edges = edges;
    }

    public List<ProcessLane> getLanes() {
        return lanes;
    }

    public void setLanes(List<ProcessLane> lanes) {
        this.lanes = lanes;
    }
}
