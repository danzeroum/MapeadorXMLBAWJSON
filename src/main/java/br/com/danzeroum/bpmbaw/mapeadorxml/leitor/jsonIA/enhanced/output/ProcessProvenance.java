package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output;

/**
 * Process provenance information
 */
class ProcessProvenance {
    private String sourceTwx;
    private String exportedAt;
    private String tool;
    private String contentHash;
    private AnalysisStats analysisStats;

    // Getters and setters...
    public String getSourceTwx() {
        return sourceTwx;
    }

    public void setSourceTwx(String sourceTwx) {
        this.sourceTwx = sourceTwx;
    }

    public String getExportedAt() {
        return exportedAt;
    }

    public void setExportedAt(String exportedAt) {
        this.exportedAt = exportedAt;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public AnalysisStats getAnalysisStats() {
        return analysisStats;
    }

    public void setAnalysisStats(AnalysisStats analysisStats) {
        this.analysisStats = analysisStats;
    }
}
