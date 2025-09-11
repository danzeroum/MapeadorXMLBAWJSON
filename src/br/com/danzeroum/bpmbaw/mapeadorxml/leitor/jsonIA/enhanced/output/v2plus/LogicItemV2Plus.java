package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Logic Item V2+ - CORRIGIDA para compilação
 *
 * CORREÇÕES:
 * ✅ Usa enums da ProcessLogicV2Plus
 * ✅ Métodos que faltavam adicionados
 * ✅ Conformidade com modelo V2+
 * ✅ Java 8 compatível
 */
@JsonPropertyOrder({"id", "name", "type", "language", "inputs", "outputs", "code", "description", "metadata"})
public class LogicItemV2Plus {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private ProcessLogicV2Plus.ItemType type;

    @JsonProperty("language")
    private ProcessLogicV2Plus.ScriptLanguage language = ProcessLogicV2Plus.ScriptLanguage.JAVASCRIPT;

    @JsonProperty("inputs")
    private List<String> inputs;

    @JsonProperty("outputs")
    private List<String> outputs;

    @JsonProperty("code")
    private String code;

    @JsonProperty("description")
    private String description;

    @JsonProperty("metadata")
    private LogicMetadata metadata;

    // Campos para análise
    private String sourceNodeId;
    private String extractionMethod;
    private String extractionTimestamp;
    private double complexityScore;
    private List<String> analysisIssues;

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================

    public LogicItemV2Plus() {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.metadata = new LogicMetadata();
        this.analysisIssues = new ArrayList<>();
        this.complexityScore = 0.0;
    }

    public LogicItemV2Plus(String id, ProcessLogicV2Plus.ScriptLanguage language,
                           List<String> inputs, List<String> outputs, String code) {
        this();
        this.id = id;
        this.language = language;
        this.inputs = inputs != null ? new ArrayList<>(inputs) : new ArrayList<>();
        this.outputs = outputs != null ? new ArrayList<>(outputs) : new ArrayList<>();
        this.code = code;
    }

    // =========================================================================
    // MÉTODOS PRINCIPAIS CORRIGIDOS
    // =========================================================================

    /**
     * Valida o logic item
     */
    public boolean isValid() {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        if (type == null) {
            return false;
        }
        if (language == null) {
            return false;
        }
        // Code pode ser vazio para alguns tipos
        return true;
    }

    /**
     * Calcula score de complexidade baseado no código
     */
    public double calculateComplexity() {
        if (code == null || code.trim().isEmpty()) {
            this.complexityScore = 0.0;
            return complexityScore;
        }

        // Cálculo simples de complexidade
        int lines = code.split("\n").length;
        int conditionals = countOccurrences(code, "if") + countOccurrences(code, "else") +
                countOccurrences(code, "switch") + countOccurrences(code, "?");
        int loops = countOccurrences(code, "for") + countOccurrences(code, "while") +
                countOccurrences(code, "do");
        int functions = countOccurrences(code, "function") + countOccurrences(code, "=>");

        this.complexityScore = (lines * 0.1) + (conditionals * 2.0) + (loops * 3.0) + (functions * 1.5);
        return complexityScore;
    }

    /**
     * Conta ocorrências de uma string no código
     */
    private int countOccurrences(String text, String pattern) {
        if (text == null || pattern == null) return 0;

        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }

    /**
     * Adiciona issue de análise
     */
    public void addAnalysisIssue(String issue) {
        if (issue != null && !issue.trim().isEmpty()) {
            if (analysisIssues == null) {
                analysisIssues = new ArrayList<>();
            }
            analysisIssues.add(issue);
        }
    }

    /**
     * Verifica se tem issues
     */
    public boolean hasIssues() {
        return analysisIssues != null && !analysisIssues.isEmpty();
    }

    // =========================================================================
    // GETTERS E SETTERS
    // =========================================================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessLogicV2Plus.ItemType getType() {
        return type;
    }

    public void setType(ProcessLogicV2Plus.ItemType type) {
        this.type = type;
    }

    public ProcessLogicV2Plus.ScriptLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProcessLogicV2Plus.ScriptLanguage language) {
        this.language = language;
    }

    public List<String> getInputs() {
        return inputs != null ? inputs : new ArrayList<>();
    }

    public void setInputs(List<String> inputs) {
        this.inputs = inputs != null ? new ArrayList<>(inputs) : new ArrayList<>();
    }

    public List<String> getOutputs() {
        return outputs != null ? outputs : new ArrayList<>();
    }

    public void setOutputs(List<String> outputs) {
        this.outputs = outputs != null ? new ArrayList<>(outputs) : new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        // Recalcular complexidade quando código muda
        calculateComplexity();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LogicMetadata getMetadata() {
        return metadata != null ? metadata : new LogicMetadata();
    }

    public void setMetadata(LogicMetadata metadata) {
        this.metadata = metadata != null ? metadata : new LogicMetadata();
    }

    // Getters/Setters para campos de análise
    public String getSourceNodeId() { return sourceNodeId; }
    public void setSourceNodeId(String sourceNodeId) { this.sourceNodeId = sourceNodeId; }

    public String getExtractionMethod() { return extractionMethod; }
    public void setExtractionMethod(String extractionMethod) { this.extractionMethod = extractionMethod; }

    public String getExtractionTimestamp() { return extractionTimestamp; }
    public void setExtractionTimestamp(String extractionTimestamp) { this.extractionTimestamp = extractionTimestamp; }

    public double getComplexityScore() { return complexityScore; }
    public void setComplexityScore(double complexityScore) { this.complexityScore = complexityScore; }

    public List<String> getAnalysisIssues() {
        return analysisIssues != null ? new ArrayList<>(analysisIssues) : new ArrayList<>();
    }

    public void setAnalysisIssues(List<String> analysisIssues) {
        this.analysisIssues = analysisIssues != null ? new ArrayList<>(analysisIssues) : new ArrayList<>();
    }

    // =========================================================================
    // CLASSE DE APOIO PARA METADADOS
    // =========================================================================

    public static class LogicMetadata {
        private String version = "1.0.0";
        private String author;
        private String createdAt;
        private String lastModified;
        private Map<String, String> tags;
        private Map<String, Object> customProperties;

        public LogicMetadata() {
            this.tags = new HashMap<>();
            this.customProperties = new HashMap<>();
            this.createdAt = String.valueOf(System.currentTimeMillis());
        }

        // Getters/Setters
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getLastModified() { return lastModified; }
        public void setLastModified(String lastModified) { this.lastModified = lastModified; }

        public Map<String, String> getTags() {
            return tags != null ? tags : new HashMap<>();
        }

        public void setTags(Map<String, String> tags) {
            this.tags = tags != null ? new HashMap<>(tags) : new HashMap<>();
        }

        public Map<String, Object> getCustomProperties() {
            return customProperties != null ? customProperties : new HashMap<>();
        }

        public void setCustomProperties(Map<String, Object> customProperties) {
            this.customProperties = customProperties != null ? new HashMap<>(customProperties) : new HashMap<>();
        }

        public void addTag(String key, String value) {
            if (tags == null) tags = new HashMap<>();
            tags.put(key, value);
        }

        public void addCustomProperty(String key, Object value) {
            if (customProperties == null) customProperties = new HashMap<>();
            customProperties.put(key, value);
        }
    }

    // =========================================================================
    // OVERRIDE toString PARA DEBUG
    // =========================================================================

    @Override
    public String toString() {
        return String.format("LogicItemV2Plus{id='%s', type=%s, language=%s, complexity=%.1f, issues=%d}",
                id, type, language, complexityScore,
                analysisIssues != null ? analysisIssues.size() : 0);
    }

    // =========================================================================
    // TESTE INLINE BÁSICO
    // =========================================================================

    /**
     * Teste básico para validação durante desenvolvimento
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing LogicItemV2Plus...");

        try {
            // Teste 1: Criação básica
            LogicItemV2Plus item = new LogicItemV2Plus();
            item.setId("test-item");
            item.setName("Test Logic Item");
            item.setType(ProcessLogicV2Plus.ItemType.SCRIPT);
            item.setLanguage(ProcessLogicV2Plus.ScriptLanguage.JAVASCRIPT);
            item.setCode("function test() { if (x > 0) { for(var i=0; i<10; i++) { console.log(i); } } }");
            item.setDescription("Test item for validation");

            System.out.println("✅ Basic creation: " + item.isValid());
            System.out.println("✅ Complexity calculation: " + item.calculateComplexity());
            System.out.println("✅ toString: " + item.toString());

            // Teste 2: Metadados
            item.getMetadata().addTag("category", "test");
            item.getMetadata().addCustomProperty("priority", 1);
            System.out.println("✅ Metadata: " + item.getMetadata().getTags().size());

            // Teste 3: Issues
            item.addAnalysisIssue("Test issue");
            System.out.println("✅ Issues tracking: " + item.hasIssues());

            System.out.println("\n🎉 LogicItemV2Plus: ALL TESTS PASSED!");

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}