package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.HashMap;
import java.util.Map;

public class GraphStatistics {
    private int nodeCount;
    private int edgeCount;
    private int laneCount;
    private Map<String, Long> nodesByType;
    private boolean isDirectedAcyclicGraph;
    private double density;

    public GraphStatistics() {
        this.nodesByType = new HashMap<>();
    }

    // Getters and setters
    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public void setEdgeCount(int edgeCount) {
        this.edgeCount = edgeCount;
    }

    public int getLaneCount() {
        return laneCount;
    }

    public void setLaneCount(int laneCount) {
        this.laneCount = laneCount;
    }

    public Map<String, Long> getNodesByType() {
        return nodesByType;
    }

    public void setNodesByType(Map<String, Long> nodesByType) {
        this.nodesByType = nodesByType;
    }

    public boolean isDirectedAcyclicGraph() {
        return isDirectedAcyclicGraph;
    }

    public void setDirectedAcyclicGraph(boolean directedAcyclicGraph) {
        isDirectedAcyclicGraph = directedAcyclicGraph;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }
}
