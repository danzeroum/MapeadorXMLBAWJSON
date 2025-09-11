package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * Enhanced Structured Process Report V2 - AI-Trustworthy
 * Implementa todas as melhorias P0 para enterprise-grade
 */
@JsonPropertyOrder({
        "$schema", "schemaVersion", "$id", "provenance", "integrity",
        "domain", "dataTypes", "processGraph", "logic", "ui", "security",
        "aiReadinessScore", "businessContext", "indices", "issues"
})
public class EnhancedStructuredProcessReportV2 {

    @JsonProperty("$schema")
    private String schema = "https://processveritas.io/schema/enhanced-process/v2.json";

    @JsonProperty("schemaVersion")
    private String schemaVersion = "2.0.0";

    @JsonProperty("$id")
    private String id;

    private ProvenanceV2 provenance;
    private IntegrityManifest integrity;
    private ProcessDomainV2 domain;
    private List<DataTypeDefinitionV2> dataTypes;
    private ProcessGraphV2 processGraph;
    private ProcessLogicV2 logic;
    private ProcessUIV2 ui;
    private SecurityConfigV2 security;
    private AIReadinessScoreV2 aiReadinessScore;
    private BusinessContextV2 businessContext;
    private IndexManifest indices;
    private List<AnalysisIssue> issues;

    // Getters and setters with validation
    public String getSchema() { return schema; }
    public void setSchema(String schema) {
        if (!schema.matches("https://processveritas\\.io/schema/enhanced-process/v[0-9]+\\.[0-9]+\\.json")) {
            throw new IllegalArgumentException("Invalid schema URI format");
        }
        this.schema = schema;
    }

    public String getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(String schemaVersion) {
        if (!schemaVersion.matches("[0-9]+\\.[0-9]+\\.[0-9]+")) {
            throw new IllegalArgumentException("Schema version must follow semantic versioning");
        }
        this.schemaVersion = schemaVersion;
    }

    public String getId() { return id; }
    public void setId(String id) {
        if (!isValidStableId(id)) {
            throw new IllegalArgumentException("ID must be valid URN or UUID format");
        }
        this.id = id;
    }

    private boolean isValidStableId(String id) {
        if (id == null) return false;
        return id.matches("^(urn:pv:[a-z]+:[a-z0-9-]+:[0-9]+|[0-9]{4}\\.[a-f0-9-]{36})$");
    }

    // Standard getters/setters for other fields
    public ProvenanceV2 getProvenance() { return provenance; }
    public void setProvenance(ProvenanceV2 provenance) { this.provenance = provenance; }

    public IntegrityManifest getIntegrity() { return integrity; }
    public void setIntegrity(IntegrityManifest integrity) { this.integrity = integrity; }

    public ProcessDomainV2 getDomain() { return domain; }
    public void setDomain(ProcessDomainV2 domain) { this.domain = domain; }

    public List<DataTypeDefinitionV2> getDataTypes() { return dataTypes; }
    public void setDataTypes(List<DataTypeDefinitionV2> dataTypes) { this.dataTypes = dataTypes; }

    public ProcessGraphV2 getProcessGraph() { return processGraph; }
    public void setProcessGraph(ProcessGraphV2 processGraph) { this.processGraph = processGraph; }

    public ProcessLogicV2 getLogic() { return logic; }
    public void setLogic(ProcessLogicV2 logic) { this.logic = logic; }

    public ProcessUIV2 getUi() { return ui; }
    public void setUi(ProcessUIV2 ui) { this.ui = ui; }

    public SecurityConfigV2 getSecurity() { return security; }
    public void setSecurity(SecurityConfigV2 security) { this.security = security; }

    public AIReadinessScoreV2 getAiReadinessScore() { return aiReadinessScore; }
    public void setAiReadinessScore(AIReadinessScoreV2 aiReadinessScore) { this.aiReadinessScore = aiReadinessScore; }

    public BusinessContextV2 getBusinessContext() { return businessContext; }
    public void setBusinessContext(BusinessContextV2 businessContext) { this.businessContext = businessContext; }

    public IndexManifest getIndices() { return indices; }
    public void setIndices(IndexManifest indices) { this.indices = indices; }

    public List<AnalysisIssue> getIssues() { return issues; }
    public void setIssues(List<AnalysisIssue> issues) { this.issues = issues; }
}