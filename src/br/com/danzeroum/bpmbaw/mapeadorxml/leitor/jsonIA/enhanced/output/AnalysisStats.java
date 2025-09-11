package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output;

/**
 * Analysis statistics
 */
class AnalysisStats {
    private int totalArtifacts;
    private int totalNodes;
    private int totalEdges;
    private int complexArtifacts;
    private int warnings;

    // Getters and setters...
    public int getTotalArtifacts() {
        return totalArtifacts;
    }

    public void setTotalArtifacts(int totalArtifacts) {
        this.totalArtifacts = totalArtifacts;
    }

    public int getTotalNodes() {
        return totalNodes;
    }

    public void setTotalNodes(int totalNodes) {
        this.totalNodes = totalNodes;
    }

    public int getTotalEdges() {
        return totalEdges;
    }

    public void setTotalEdges(int totalEdges) {
        this.totalEdges = totalEdges;
    }

    public int getComplexArtifacts() {
        return complexArtifacts;
    }

    public void setComplexArtifacts(int complexArtifacts) {
        this.complexArtifacts = complexArtifacts;
    }

    public int getWarnings() {
        return warnings;
    }

    public void setWarnings(int warnings) {
        this.warnings = warnings;
    }
}
