// EnhancedJsonReport.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;

public class EnhancedJsonReport {
    private ReportMetadata metadata;
    private BusinessContext businessContext;
    private JsonReportV2 processDefinition;
    private AIReadinessScore aiReadinessScore;

    // Getters e setters...
    public EnhancedJsonReport() {}

    public ReportMetadata getMetadata() { return metadata; }
    public void setMetadata(ReportMetadata metadata) { this.metadata = metadata; }

    public BusinessContext getBusinessContext() { return businessContext; }
    public void setBusinessContext(BusinessContext businessContext) { this.businessContext = businessContext; }

    public JsonReportV2 getProcessDefinition() { return processDefinition; }
    public void setProcessDefinition(JsonReportV2 processDefinition) { this.processDefinition = processDefinition; }

    public AIReadinessScore getAiReadinessScore() { return aiReadinessScore; }
    public void setAiReadinessScore(AIReadinessScore aiReadinessScore) { this.aiReadinessScore = aiReadinessScore; }


}

