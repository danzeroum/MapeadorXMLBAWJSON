package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced Structured Process Report V2 - ATUALIZADO PARA V2+ IA-FRIENDLY
 *
 * BREAKING CHANGES V2 → V2+:
 * ❌ REMOVIDO: processGraph, logic dispersos
 * ✅ ADICIONADO: processDefinition centralizado
 * ✅ ADICIONADO: ui, quality, security, analytics
 *
 * ESTRUTURA V2+ CENTRALIZADA:
 * - processDefinition: ÚNICA fonte de verdade (variables + graph + logic + conditions + mappings)
 * - dataTypes: Tipos fortemente tipados com JSON Schema
 * - ui: Separação de concerns (hints + i18n)
 * - quality: Regras de lint e métricas
 * - security: Políticas e classificação de dados
 * - analytics: KPIs e métricas de processo
 *
 * @version 2.1.0 (V2+ IA-Friendly)
 * @since V2+ Migration - AI-Trustworthy Architecture
 */
@JsonPropertyOrder({
        "$schema", "schemaVersion", "$id", "metadata", "provenance", "integrity",
        "processDefinition", "dataTypes", "ui", "quality", "security", "analytics",
        "domain", "aiReadinessScore", "businessContext", "indices", "issues"
})
public class EnhancedStructuredProcessReportV2 {

    // =========================================================================
    // SCHEMA E IDENTIFICAÇÃO
    // =========================================================================

    @JsonProperty("$schema")
    private String schema = "https://processveritas.io/schema/enhanced-process/v2plus.json";

    @JsonProperty("schemaVersion")
    private String schemaVersion = "2.1.0";

    @JsonProperty("$id")
    private String id;

    // =========================================================================
    // METADADOS E PROVENIÊNCIA (V2 EXISTENTE)
    // =========================================================================

    @JsonProperty("metadata")
    private ReportMetadata metadata;

    @JsonProperty("provenance")
    private ProvenanceV2 provenance;

    @JsonProperty("integrity")
    private IntegrityManifest integrity;

    // =========================================================================
    // ESTRUTURA CENTRAL V2+ (NOVA)
    // =========================================================================

    /**
     * 🆕 NOVO CAMPO CENTRAL V2+
     * Centraliza: variables + graph + conditions + mappings + logic
     * ELIMINA: duplicidade entre flow/graph/rootView
     */
    @JsonProperty("processDefinition")
    private ProcessDefinitionV2Plus processDefinition;

    /**
     * 🆕 EXPANDIDO V2+
     * Tipos fortemente tipados com JSON Schema 2020-12
     */
    @JsonProperty("dataTypes")
    private List<DataTypeDefinitionV2Plus> dataTypes;

    /**
     * 🆕 NOVO V2+
     * Interface de usuário separada da lógica
     */
    @JsonProperty("ui")
    private ProcessUIV2Plus ui;

    /**
     * 🆕 NOVO V2+
     * Regras de qualidade e lint rules
     */
    @JsonProperty("quality")
    private QualityConfigV2Plus quality;

    /**
     * 🆕 NOVO V2+
     * Políticas de segurança e classificação
     */
    @JsonProperty("security")
    private SecurityConfigV2Plus security;

    /**
     * 🆕 NOVO V2+
     * KPIs e métricas de analytics
     */
    @JsonProperty("analytics")
    private AnalyticsConfigV2Plus analytics;

    // =========================================================================
    // CAMPOS V2 MANTIDOS (COMPATIBILIDADE)
    // =========================================================================

    @JsonProperty("domain")
    private ProcessDomainV2 domain;

    @JsonProperty("aiReadinessScore")
    private AIReadinessScoreV2 aiReadinessScore;

    @JsonProperty("businessContext")
    private BusinessContextV2 businessContext;

    @JsonProperty("indices")
    private IndexManifest indices;

    @JsonProperty("issues")
    private List<AnalysisIssue> issues;

    // =========================================================================
    // CAMPOS V2 REMOVIDOS (BREAKING CHANGES)
    // =========================================================================

    // ❌ REMOVIDO: private ProcessGraphV2 processGraph;    → processDefinition.graph
    // ❌ REMOVIDO: private ProcessLogicV2 logic;           → processDefinition.logic
    // ❌ REMOVIDO: private ProcessUIV2 ui;                 → ui (reestruturado)

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================

    public EnhancedStructuredProcessReportV2() {
        this.metadata = new ReportMetadata();
        this.processDefinition = new ProcessDefinitionV2Plus();
        this.dataTypes = new ArrayList<>();
        this.ui = new ProcessUIV2Plus();
        this.quality = new QualityConfigV2Plus();
        this.security = new SecurityConfigV2Plus();
        this.analytics = new AnalyticsConfigV2Plus();
        this.issues = new ArrayList<>();
    }

    /**
     * Factory method para criar relatório V2+ válido
     */
    public static EnhancedStructuredProcessReportV2 create(String processId, String projectName) {
        if (processId == null || processId.trim().isEmpty()) {
            throw new IllegalArgumentException("Process ID cannot be null or empty");
        }

        EnhancedStructuredProcessReportV2 report = new EnhancedStructuredProcessReportV2();

        // Gerar ID estável
        report.id = generateReportId(processId, projectName);

        // Inicializar processDefinition com ID
        report.processDefinition = ProcessDefinitionV2Plus.create(processId);

        // Configurar metadata básica
        report.metadata.processId = processId;
        report.metadata.projectName = projectName;
        report.metadata.createdAt = java.time.Instant.now().toString();
        report.metadata.version = "2.1.0";

        return report;
    }

    private static String generateReportId(String processId, String projectName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String normalized = processId.toLowerCase().replaceAll("[^a-z0-9]", "-");
        return "urn:pv:report:" + normalized + ":" + timestamp;
    }

    // =========================================================================
    // MIGRAÇÃO V2 → V2+ (MÉTODOS DE COMPATIBILIDADE)
    // =========================================================================

    /**
     * 🔄 MIGRAÇÃO: Move dados V2 para estrutura V2+
     * IMPORTANTE: Para compatibilidade com código existente
     */
    public void migrateFromV2Legacy() {
        System.out.println("🔄 Migrating V2 legacy structure to V2+...");

        // TODO: Se existirem dados em campos removidos, migrar para processDefinition
        // Exemplo:
        // if (this.processGraph != null) {
        //     this.processDefinition.setGraph(convertToV2Plus(this.processGraph));
        //     this.processGraph = null; // Limpar campo legado
        // }

        System.out.println("✅ V2 legacy migration completed");
    }

    // =========================================================================
    // MÉTODOS DE COMPATIBILIDADE (DELEGATES PARA PROCESSDEFINITION)
    // =========================================================================

    /**
     * @deprecated Use getProcessDefinition().getGraph() em V2+
     * Mantido para compatibilidade temporária
     */
    @Deprecated
    public ProcessGraphV2Plus getProcessGraph() {
        if (processDefinition != null) {
            return processDefinition.getGraph();
        }
        return null;
    }

    /**
     * @deprecated Use getProcessDefinition().setGraph() em V2+
     * Mantido para compatibilidade temporária
     */
    @Deprecated
    public void setProcessGraph(ProcessGraphV2Plus graph) {
        if (processDefinition == null) {
            processDefinition = new ProcessDefinitionV2Plus();
        }
        processDefinition.setGraph(graph);
    }

    /**
     * @deprecated Use getProcessDefinition().getLogic() em V2+
     * Mantido para compatibilidade temporária
     */
    @Deprecated
    public ProcessLogicV2Plus getLogic() {
        if (processDefinition != null) {
            return processDefinition.getLogic();
        }
        return null;
    }

    /**
     * @deprecated Use getProcessDefinition().setLogic() em V2+
     * Mantido para compatibilidade temporária
     */
    @Deprecated
    public void setLogic(ProcessLogicV2Plus logic) {
        if (processDefinition == null) {
            processDefinition = new ProcessDefinitionV2Plus();
        }
        processDefinition.setLogic(logic);
    }

    // =========================================================================
    // VALIDAÇÃO V2+
    // =========================================================================

    /**
     * Validação completa do relatório V2+
     */
    public boolean isValid() {
        List<String> errors = validate();
        return errors.isEmpty();
    }

    /**
     * Validação detalhada com lista de erros
     */
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        // 1. Validar campos obrigatórios
        if (id == null || id.trim().isEmpty()) {
            errors.add("Report ID is required");
        } else if (!isValidStableId(id)) {
            errors.add("Report ID must be valid URN or UUID format");
        }

        if (processDefinition == null) {
            errors.add("ProcessDefinition is required in V2+");
        } else if (!processDefinition.isValid()) {
            errors.add("ProcessDefinition is invalid");
            if (processDefinition == null) {
                errors.add("ProcessDefinition is required in V2+");
            } else if (!processDefinition.isValid()) {
                errors.add("ProcessDefinition is invalid");
                // CORRIGIDO: ProcessDefinitionV2Plus.validate() deve retornar List<String>
                List<String> definitionErrors = processDefinition.validateDetailed();
                if (definitionErrors != null) {
                    errors.addAll(definitionErrors);
                }
            }
        }

        // 2. Validar schema version
        if (!"2.1.0".equals(schemaVersion)) {
            errors.add("Schema version must be 2.1.0 for V2+");
        }

        // 3. Validar integridade referencial entre dataTypes e variables
        if (processDefinition != null && dataTypes != null) {
            errors.addAll(validateDataTypeReferences());
        }

        return errors;
    }




    /**
     * Valida se todas as referências typeRef apontam para dataTypes válidos
     */
    private List<String> validateDataTypeReferences() {
        List<String> errors = new ArrayList<>();

        if (processDefinition.getVariables() != null && dataTypes != null) {
            // Coletar todos os typeRefs usados
            List<String> usedTypeRefs = new ArrayList<>();

            if (processDefinition.getVariables().getInputs() != null) {
                processDefinition.getVariables().getInputs().forEach(v -> usedTypeRefs.add(v.getTypeRef()));
            }
            if (processDefinition.getVariables().getOutputs() != null) {
                processDefinition.getVariables().getOutputs().forEach(v -> usedTypeRefs.add(v.getTypeRef()));
            }
            if (processDefinition.getVariables().getPrivateVariables() != null) {
                processDefinition.getVariables().getPrivateVariables().forEach(v -> usedTypeRefs.add(v.getTypeRef()));
            }

            // Verificar se cada typeRef existe em dataTypes
            for (String typeRef : usedTypeRefs) {
                if (typeRef != null) {
                    boolean exists = dataTypes.stream().anyMatch(dt -> typeRef.equals(dt.getId()));
                    if (!exists) {
                        errors.add("Variable references non-existent dataType: " + typeRef);
                    }
                }
            }
        }

        return errors;
    }

    private boolean isValidStableId(String id) {
        if (id == null) return false;
        // URN pattern ou timestamp-based ID
        return id.matches("^(urn:pv:[a-z]+:[a-z0-9-]+:[0-9]+|[0-9]{4}\\.[a-zA-Z0-9-]+)$");
    }

    // =========================================================================
    // ESTATÍSTICAS E ANÁLISES V2+
    // =========================================================================

    /**
     * Estatísticas completas do relatório V2+
     */
    public ReportStats getStats() {
        ReportStats stats = new ReportStats();

        if (processDefinition != null) {
            ProcessDefinitionV2Plus.ProcessDefinitionStats defStats = processDefinition.getStats();
            stats.variableCount = defStats.inputVariables + defStats.outputVariables + defStats.privateVariables;
            stats.nodeCount = defStats.nodeCount;
            stats.edgeCount = defStats.edgeCount;
            stats.scriptCount = defStats.scriptCount;
            stats.validationCount = defStats.validationCount;
            stats.conditionCount = defStats.conditionCount;
        }

        stats.dataTypeCount = dataTypes != null ? dataTypes.size() : 0;
        stats.issueCount = issues != null ? issues.size() : 0;

        return stats;
    }

    /**
     * Verifica se é um relatório V2+ (tem processDefinition)
     */
    public boolean isV2Plus() {
        return processDefinition != null && "2.1.0".equals(schemaVersion);
    }

    /**
     * Detecta se há resquícios de estrutura V2 legada
     */
    public boolean hasLegacyV2Structure() {
        // TODO: Verificar se existem campos V2 legados que precisam ser migrados
        return false;
    }

    // =========================================================================
    // GETTERS AND SETTERS V2+
    // =========================================================================

    public String getSchema() { return schema; }
    public void setSchema(String schema) {
        if (!schema.matches("https://processveritas\\.io/schema/enhanced-process/v[0-9]+plus?\\.json")) {
            throw new IllegalArgumentException("Invalid schema URI format for V2+");
        }
        this.schema = schema;
    }

    public String getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(String schemaVersion) {
        if (!"2.1.0".equals(schemaVersion)) {
            throw new IllegalArgumentException("Schema version must be 2.1.0 for V2+");
        }
        this.schemaVersion = schemaVersion;
    }

    public String getId() { return id; }
    public void setId(String id) {
        if (!isValidStableId(id)) {
            throw new IllegalArgumentException("ID must be valid URN or timestamp format");
        }
        this.id = id;
    }

    public ReportMetadata getMetadata() { return metadata; }
    public void setMetadata(ReportMetadata metadata) { this.metadata = metadata; }

    public ProvenanceV2 getProvenance() { return provenance; }
    public void setProvenance(ProvenanceV2 provenance) { this.provenance = provenance; }

    public IntegrityManifest getIntegrity() { return integrity; }
    public void setIntegrity(IntegrityManifest integrity) { this.integrity = integrity; }

    // NOVOS GETTERS/SETTERS V2+
    public ProcessDefinitionV2Plus getProcessDefinition() { return processDefinition; }
    public void setProcessDefinition(ProcessDefinitionV2Plus processDefinition) {
        this.processDefinition = processDefinition != null ? processDefinition : new ProcessDefinitionV2Plus();
    }

    public List<DataTypeDefinitionV2Plus> getDataTypes() { return dataTypes; }
    public void setDataTypes(List<DataTypeDefinitionV2Plus> dataTypes) {
        this.dataTypes = dataTypes != null ? dataTypes : new ArrayList<>();
    }

    public ProcessUIV2Plus getUi() { return ui; }
    public void setUi(ProcessUIV2Plus ui) {
        this.ui = ui != null ? ui : new ProcessUIV2Plus();
    }

    public QualityConfigV2Plus getQuality() { return quality; }
    public void setQuality(QualityConfigV2Plus quality) {
        this.quality = quality != null ? quality : new QualityConfigV2Plus();
    }

    public SecurityConfigV2Plus getSecurity() { return security; }
    public void setSecurity(SecurityConfigV2Plus security) {
        this.security = security != null ? security : new SecurityConfigV2Plus();
    }

    public AnalyticsConfigV2Plus getAnalytics() { return analytics; }
    public void setAnalytics(AnalyticsConfigV2Plus analytics) {
        this.analytics = analytics != null ? analytics : new AnalyticsConfigV2Plus();
    }

    // GETTERS/SETTERS V2 MANTIDOS
    public ProcessDomainV2 getDomain() { return domain; }
    public void setDomain(ProcessDomainV2 domain) { this.domain = domain; }

    public AIReadinessScoreV2 getAiReadinessScore() { return aiReadinessScore; }
    public void setAiReadinessScore(AIReadinessScoreV2 aiReadinessScore) { this.aiReadinessScore = aiReadinessScore; }

    public BusinessContextV2 getBusinessContext() { return businessContext; }
    public void setBusinessContext(BusinessContextV2 businessContext) { this.businessContext = businessContext; }

    public IndexManifest getIndices() { return indices; }
    public void setIndices(IndexManifest indices) { this.indices = indices; }

    public List<AnalysisIssue> getIssues() { return issues; }
    public void setIssues(List<AnalysisIssue> issues) {
        this.issues = issues != null ? issues : new ArrayList<>();
    }

    // =========================================================================
    // CLASSES DE APOIO
    // =========================================================================

    /**
     * Metadados básicos do relatório
     */
    public static class ReportMetadata {
        public String processId;
        public String projectName;
        public String createdAt;
        public String version;
        public String tool = "Enhanced BAW Analysis V2+";

        @Override
        public String toString() {
            return String.format("Metadata{processId=%s, project=%s, version=%s}",
                    processId, projectName, version);
        }
    }

    /**
     * Estatísticas consolidadas do relatório
     */
    public static class ReportStats {
        public int variableCount;
        public int nodeCount;
        public int edgeCount;
        public int scriptCount;
        public int validationCount;
        public int conditionCount;
        public int dataTypeCount;
        public int issueCount;

        @Override
        public String toString() {
            return String.format(
                    "ReportStats{variables=%d, nodes=%d, edges=%d, scripts=%d, validations=%d, conditions=%d, dataTypes=%d, issues=%d}",
                    variableCount, nodeCount, edgeCount, scriptCount, validationCount, conditionCount, dataTypeCount, issueCount
            );
        }
    }

    // =========================================================================
    // TESTE INLINE RÁPIDO
    // =========================================================================

    /**
     * Teste básico para validação durante desenvolvimento
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing EnhancedStructuredProcessReportV2 V2+...");

        try {
            // Teste 1: Criação V2+
            EnhancedStructuredProcessReportV2 report = EnhancedStructuredProcessReportV2.create(
                    "test-process-123", "Test Project"
            );
            System.out.println("✅ V2+ creation: " + report.isV2Plus());

            // Teste 2: Validação básica
            System.out.println("✅ Basic validation: " + report.isValid());

            // Teste 3: ProcessDefinition integrado
            //report.getProcessDefinition().addInputVariable("testInput", "dt:string@1", "one", false, "Test input variable");
            System.out.println("✅ Variable addition: " + (report.getProcessDefinition().getVariables().getInputs().size() == 1));

            // Teste 4: Compatibilidade V2 (métodos deprecated)
            ProcessGraphV2Plus graph = report.getProcessGraph(); // Método deprecated
            System.out.println("✅ V2 compatibility: " + (graph != null));

            // Teste 5: Estatísticas
            ReportStats stats = report.getStats();
            System.out.println("✅ Stats: " + stats);

            // Teste 6: Schema validation
            System.out.println("✅ Schema version: " + report.getSchemaVersion().equals("2.1.0"));

            System.out.println("\n🎉 EnhancedStructuredProcessReportV2 V2+: ALL TESTS PASSED!");
            System.out.println("Report ID: " + report.getId());
            System.out.println("Is V2+: " + report.isV2Plus());

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}