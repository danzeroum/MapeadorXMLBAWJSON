// ReportMetadata.java - VERSÃO COMPATÍVEL COM JAVA 8
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;

/**
 * Metadados do relatório enhanced
 * COMPATÍVEL COM JAVA 8 - Usa String para datas ao invés de LocalDateTime
 */
public class ReportMetadata {
    private String reportVersion = "4.0-ENHANCED";
    private String sessionId;
    private String generatedAt;
    private AnalysisConfig configuration;

    public ReportMetadata() {}

    // Getters e Setters
    public String getReportVersion() {
        return reportVersion;
    }

    public void setReportVersion(String reportVersion) {
        this.reportVersion = reportVersion;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public AnalysisConfig getConfiguration() {
        return configuration;
    }

    public void setConfiguration(AnalysisConfig configuration) {
        this.configuration = configuration;
    }
}