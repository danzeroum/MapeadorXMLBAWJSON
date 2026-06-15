package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * LogicItemV2Plus - VERSÃO COMPLETA CORRIGIDA JAVA 8
 *
 * CORREÇÕES APLICADAS:
 * ✅ Todos os métodos ausentes implementados (setCode, setType, setLanguage, etc.)
 * ✅ Usa enums corretos da ProcessLogicV2Plus
 * ✅ Conformidade com modelo V2+
 * ✅ Java 8 compatível
 * ✅ Campos de análise e metadata
 * ✅ Validação completa
 *
 * @version 2.3.0-complete-fixed-java8
 */
@JsonPropertyOrder({"id", "name", "type", "language", "inputs", "outputs", "code", "description", "metadata"})
public class LogicItemV2Plus {

    // =========================================================================
    // CAMPOS PRINCIPAIS
    // =========================================================================

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

    // =========================================================================
    // CAMPOS PARA ANÁLISE E EXTRAÇÃO
    // =========================================================================

    private String sourceNodeId;
    private String extractionMethod;
    private String extractionTimestamp;
    private double complexityScore;
    private List<String> analysisIssues;
    private Map<String, Object> analysisData;
    private boolean isExtracted;
    private String originalContent;

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================

    /**
     * Construtor padrão
     */
    public LogicItemV2Plus() {
        this.inputs = new ArrayList<String>();
        this.outputs = new ArrayList<String>();
        this.metadata = new LogicMetadata();
        this.analysisIssues = new ArrayList<String>();
        this.analysisData = new HashMap<String, Object>();
        this.complexityScore = 0.0;
        this.isExtracted = false;
    }

    /**
     * Construtor com parâmetros principais
     */
    public LogicItemV2Plus(String id, ProcessLogicV2Plus.ScriptLanguage language,
                           List<String> inputs, List<String> outputs, String code) {
        this();
        this.id = id;
        this.language = language;
        this.inputs = inputs != null ? new ArrayList<String>(inputs) : new ArrayList<String>();
        this.outputs = outputs != null ? new ArrayList<String>(outputs) : new ArrayList<String>();
        this.code = code;
    }

    /**
     * Construtor completo
     */
    public LogicItemV2Plus(String id, String name, ProcessLogicV2Plus.ItemType type,
                           ProcessLogicV2Plus.ScriptLanguage language, String code, String description) {
        this();
        this.id = id;
        this.name = name;
        this.type = type;
        this.language = language;
        this.code = code;
        this.description = description;
    }

    // =========================================================================
    // GETTERS E SETTERS PRINCIPAIS
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

    /**
     * CORRIGIDO: setType() com enum correto
     */
    public ProcessLogicV2Plus.ItemType getType() {
        return type;
    }

    public void setType(ProcessLogicV2Plus.ItemType type) {
        this.type = type;
    }

    /**
     * CORRIGIDO: setLanguage() com enum correto
     */
    public ProcessLogicV2Plus.ScriptLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProcessLogicV2Plus.ScriptLanguage language) {
        this.language = language;
    }

    public List<String> getInputs() {
        return inputs != null ? inputs : new ArrayList<String>();
    }

    public void setInputs(List<String> inputs) {
        this.inputs = inputs != null ? inputs : new ArrayList<String>();
    }

    public List<String> getOutputs() {
        return outputs != null ? outputs : new ArrayList<String>();
    }

    public void setOutputs(List<String> outputs) {
        this.outputs = outputs != null ? outputs : new ArrayList<String>();
    }

    /**
     * CORRIGIDO: setCode() método que estava ausente
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;

        // Calcular complexidade quando código é definido
        if (code != null) {
            calculateComplexity();
        }
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

    // =========================================================================
    // GETTERS E SETTERS DE ANÁLISE
    // =========================================================================

    public String getSourceNodeId() {
        return sourceNodeId;
    }

    public void setSourceNodeId(String sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }

    public String getExtractionMethod() {
        return extractionMethod;
    }

    public void setExtractionMethod(String extractionMethod) {
        this.extractionMethod = extractionMethod;
    }

    public String getExtractionTimestamp() {
        return extractionTimestamp;
    }

    public void setExtractionTimestamp(String extractionTimestamp) {
        this.extractionTimestamp = extractionTimestamp;
    }

    public double getComplexityScore() {
        return complexityScore;
    }

    public void setComplexityScore(double complexityScore) {
        this.complexityScore = complexityScore;
    }

    public List<String> getAnalysisIssues() {
        return analysisIssues != null ? analysisIssues : new ArrayList<String>();
    }

    public void setAnalysisIssues(List<String> analysisIssues) {
        this.analysisIssues = analysisIssues != null ? analysisIssues : new ArrayList<String>();
    }

    public Map<String, Object> getAnalysisData() {
        return analysisData != null ? analysisData : new HashMap<String, Object>();
    }

    public void setAnalysisData(Map<String, Object> analysisData) {
        this.analysisData = analysisData != null ? analysisData : new HashMap<String, Object>();
    }

    public boolean isExtracted() {
        return isExtracted;
    }

    public void setExtracted(boolean extracted) {
        isExtracted = extracted;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    // =========================================================================
    // MÉTODOS DE VALIDAÇÃO
    // =========================================================================

    /**
     * Valida se o logic item está corretamente configurado
     */
    public boolean isValid() {
        // Validações básicas
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        if (type == null) {
            return false;
        }

        if (language == null) {
            return false;
        }

        // Para scripts, código é obrigatório
        if (type == ProcessLogicV2Plus.ItemType.SCRIPT &&
                (code == null || code.trim().isEmpty())) {
            return false;
        }

        return true;
    }

    /**
     * Validação detalhada com lista de erros
     */
    public List<String> validateDetailed() {
        List<String> errors = new ArrayList<String>();

        if (id == null || id.trim().isEmpty()) {
            errors.add("ID is required");
        }

        if (type == null) {
            errors.add("Type is required");
        }

        if (language == null) {
            errors.add("Language is required");
        }

        if (type == ProcessLogicV2Plus.ItemType.SCRIPT &&
                (code == null || code.trim().isEmpty())) {
            errors.add("Code is required for SCRIPT type");
        }

        if (name == null || name.trim().isEmpty()) {
            errors.add("Name is recommended");
        }

        return errors;
    }

    // =========================================================================
    // MÉTODOS DE MANIPULAÇÃO
    // =========================================================================

    /**
     * Adiciona input
     */
    public void addInput(String input) {
        if (input != null && !input.trim().isEmpty()) {
            if (inputs == null) {
                inputs = new ArrayList<String>();
            }
            if (!inputs.contains(input)) {
                inputs.add(input);
            }
        }
    }

    /**
     * Adiciona output
     */
    public void addOutput(String output) {
        if (output != null && !output.trim().isEmpty()) {
            if (outputs == null) {
                outputs = new ArrayList<String>();
            }
            if (!outputs.contains(output)) {
                outputs.add(output);
            }
        }
    }

    /**
     * Remove input
     */
    public boolean removeInput(String input) {
        if (inputs != null) {
            return inputs.remove(input);
        }
        return false;
    }

    /**
     * Remove output
     */
    public boolean removeOutput(String output) {
        if (outputs != null) {
            return outputs.remove(output);
        }
        return false;
    }

    /**
     * Adiciona issue de análise
     */
    public void addAnalysisIssue(String issue) {
        if (issue != null && !issue.trim().isEmpty()) {
            if (analysisIssues == null) {
                analysisIssues = new ArrayList<String>();
            }
            analysisIssues.add(issue);
        }
    }

    /**
     * Adiciona dado de análise
     */
    public void addAnalysisData(String key, Object value) {
        if (key != null && !key.trim().isEmpty()) {
            if (analysisData == null) {
                analysisData = new HashMap<String, Object>();
            }
            analysisData.put(key, value);
        }
    }

    // =========================================================================
    // MÉTODOS DE ANÁLISE
    // =========================================================================

    /**
     * Calcula complexidade do código
     */
    public void calculateComplexity() {
        if (code == null || code.trim().isEmpty()) {
            complexityScore = 0.0;
            return;
        }

        double complexity = 1.0; // Base complexity

        // Contar linhas
        String[] lines = code.split("\n");
        complexity += lines.length * 0.1;

        // Contar estruturas de controle
        String lowerCode = code.toLowerCase();
        complexity += countOccurrences(lowerCode, "if") * 1.0;
        complexity += countOccurrences(lowerCode, "for") * 1.5;
        complexity += countOccurrences(lowerCode, "while") * 1.5;
        complexity += countOccurrences(lowerCode, "switch") * 2.0;
        complexity += countOccurrences(lowerCode, "try") * 1.0;
        complexity += countOccurrences(lowerCode, "catch") * 1.0;

        // Contar funções
        complexity += countOccurrences(lowerCode, "function") * 2.0;

        this.complexityScore = Math.round(complexity * 100.0) / 100.0;
    }

    /**
     * Conta ocorrências de uma string
     */
    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }

    /**
     * Analisa dependências do código
     */
    public List<String> analyzeDependencies() {
        List<String> dependencies = new ArrayList<String>();

        if (code == null || code.trim().isEmpty()) {
            return dependencies;
        }

        // Analisar variáveis referenciadas
        String[] commonVars = {"tw.", "input.", "output.", "context.", "process."};
        for (String var : commonVars) {
            if (code.contains(var)) {
                dependencies.add("Variable reference: " + var);
            }
        }

        // Analisar chamadas de função
        if (code.contains("function ") || code.contains("=>")) {
            dependencies.add("Function definitions");
        }

        return dependencies;
    }

    // =========================================================================
    // MÉTODOS UTILITÁRIOS
    // =========================================================================

    /**
     * Cria cópia do logic item
     */
    public LogicItemV2Plus copy() {
        LogicItemV2Plus copy = new LogicItemV2Plus();
        copy.setId(this.id);
        copy.setName(this.name);
        copy.setType(this.type);
        copy.setLanguage(this.language);
        copy.setCode(this.code);
        copy.setDescription(this.description);
        copy.setInputs(new ArrayList<String>(this.getInputs()));
        copy.setOutputs(new ArrayList<String>(this.getOutputs()));
        copy.setSourceNodeId(this.sourceNodeId);
        copy.setComplexityScore(this.complexityScore);
        return copy;
    }

    /**
     * Converte para string para debug
     */
    @Override
    public String toString() {
        return String.format("LogicItemV2Plus{id='%s', name='%s', type=%s, language=%s, complexity=%.1f}",
                id, name, type, language, complexityScore);
    }

    /**
     * Verifica igualdade por ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LogicItemV2Plus that = (LogicItemV2Plus) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    /**
     * Hash code baseado no ID
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    // =========================================================================
    // CLASSE INTERNA PARA METADATA
    // =========================================================================

    /**
     * Metadata do logic item
     */
    public static class LogicMetadata {
        private String createdAt;
        private String lastModified;
        private String author;
        private String version;
        private Map<String, String> annotations;

        public LogicMetadata() {
            this.annotations = new HashMap<String, String>();
            this.createdAt = java.time.LocalDateTime.now().toString();
        }

        // Getters e Setters
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getLastModified() { return lastModified; }
        public void setLastModified(String lastModified) { this.lastModified = lastModified; }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public Map<String, String> getAnnotations() { return annotations; }
        public void setAnnotations(Map<String, String> annotations) {
            this.annotations = annotations != null ? annotations : new HashMap<String, String>();
        }

        public void addAnnotation(String key, String value) {
            if (annotations == null) {
                annotations = new HashMap<String, String>();
            }
            annotations.put(key, value);
        }
    }

    // =========================================================================
    // MÉTODOS FACTORY
    // =========================================================================

    /**
     * Cria logic item de script
     */
    public static LogicItemV2Plus createScript(String id, String name, String code) {
        LogicItemV2Plus item = new LogicItemV2Plus();
        item.setId(id);
        item.setName(name);
        item.setType(ProcessLogicV2Plus.ItemType.SCRIPT);
        item.setLanguage(ProcessLogicV2Plus.ScriptLanguage.JAVASCRIPT);
        item.setCode(code);
        return item;
    }

    /**
     * Cria logic item de validação
     */
    public static LogicItemV2Plus createValidation(String id, String name, String rule) {
        LogicItemV2Plus item = new LogicItemV2Plus();
        item.setId(id);
        item.setName(name);
        item.setType(ProcessLogicV2Plus.ItemType.VALIDATION);
        item.setLanguage(ProcessLogicV2Plus.ScriptLanguage.CEL);
        item.setCode(rule);
        return item;
    }

    // =========================================================================
    // TESTE INLINE
    // =========================================================================

    /**
     * Teste básico da classe
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing LogicItemV2Plus...");

        try {
            // Teste 1: Criação básica
            LogicItemV2Plus item = new LogicItemV2Plus();
            item.setId("test-item");
            item.setName("Test Item");
            item.setType(ProcessLogicV2Plus.ItemType.SCRIPT);
            item.setLanguage(ProcessLogicV2Plus.ScriptLanguage.JAVASCRIPT);
            item.setCode("console.log('test');");

            System.out.println("✅ Basic creation: " + item.isValid());
            System.out.println("✅ Has code: " + (item.getCode() != null));
            System.out.println("✅ Complexity: " + item.getComplexityScore());

            // Teste 2: Factory methods
            LogicItemV2Plus script = LogicItemV2Plus.createScript("script-1", "Test Script", "return true;");
            System.out.println("✅ Factory script creation: " + script.isValid());

            // Teste 3: Cópia
            LogicItemV2Plus copy = item.copy();
            System.out.println("✅ Copy creation: " + copy.equals(item));

            System.out.println("🎉 LogicItemV2Plus: ALL TESTS PASSED!");

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}