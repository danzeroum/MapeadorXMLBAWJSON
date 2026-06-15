package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model.AIReadinessScore;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model.BusinessContext;

import java.util.List;

/**
 * Main structured process report
 */
class StructuredProcessReport {
    private String schema;
    private String schemaVersion;
    private ProcessProvenance provenance;
    private ProcessDomain domain;
    private List<DataTypeDefinition> dataTypes;
    private ProcessGraph processGraph;
    private ProcessLogic logic;
    private ProcessUI ui;
    private SecurityConfig security;
    private AIReadinessScore aiReadinessScore;
    private BusinessContext businessContext;

    // Getters and setters...
    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public ProcessProvenance getProvenance() {
        return provenance;
    }

    public void setProvenance(ProcessProvenance provenance) {
        this.provenance = provenance;
    }

    public ProcessDomain getDomain() {
        return domain;
    }

    public void setDomain(ProcessDomain domain) {
        this.domain = domain;
    }

    public List<DataTypeDefinition> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(List<DataTypeDefinition> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public ProcessGraph getProcessGraph() {
        return processGraph;
    }

    public void setProcessGraph(ProcessGraph processGraph) {
        this.processGraph = processGraph;
    }

    public ProcessLogic getLogic() {
        return logic;
    }

    public void setLogic(ProcessLogic logic) {
        this.logic = logic;
    }

    public ProcessUI getUi() {
        return ui;
    }

    public void setUi(ProcessUI ui) {
        this.ui = ui;
    }

    public SecurityConfig getSecurity() {
        return security;
    }

    public void setSecurity(SecurityConfig security) {
        this.security = security;
    }

    public AIReadinessScore getAiReadinessScore() {
        return aiReadinessScore;
    }

    public void setAiReadinessScore(AIReadinessScore aiReadinessScore) {
        this.aiReadinessScore = aiReadinessScore;
    }

    public BusinessContext getBusinessContext() {
        return businessContext;
    }

    public void setBusinessContext(BusinessContext businessContext) {
        this.businessContext = businessContext;
    }
}
