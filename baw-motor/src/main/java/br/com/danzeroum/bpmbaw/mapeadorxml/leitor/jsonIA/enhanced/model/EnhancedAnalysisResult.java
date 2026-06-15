// EnhancedAnalysisResult.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;

public class EnhancedAnalysisResult {
    private ProcessLoaderV2Plus loader;
    private JsonReportV2 standardReport;
    private BusinessContext businessContext;
    private AIReadinessScore aiReadinessScore;

    // Construtores, getters e setters básicos...
    public EnhancedAnalysisResult() {}

    public ProcessLoaderV2Plus getLoader() {
        return loader;
    }

    public void setLoader(ProcessLoaderV2Plus loader) {
        this.loader = loader;
    }

    public JsonReportV2 getStandardReport() {
        return standardReport;
    }

    public void setStandardReport(JsonReportV2 standardReport) {
        this.standardReport = standardReport;
    }

    public BusinessContext getBusinessContext() {
        return businessContext;
    }

    public void setBusinessContext(BusinessContext businessContext) {
        this.businessContext = businessContext;
    }

    public AIReadinessScore getAiReadinessScore() {
        return aiReadinessScore;
    }

    public void setAiReadinessScore(AIReadinessScore aiReadinessScore) {
        this.aiReadinessScore = aiReadinessScore;
    }


}