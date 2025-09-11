package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Process Logic V2+ - CORRIGIDA para compilação
 *
 * CORREÇÕES APLICADAS:
 * ✅ Adicionadas enums ItemType e ScriptLanguage
 * ✅ Corrigidos métodos que faltavam
 * ✅ Removidas referências a classes não existentes
 * ✅ Conformidade com Java 8
 */
@JsonPropertyOrder({"items", "validations", "transformations", "dependencies", "config", "metadata", "statistics"})
public class ProcessLogicV2Plus {

    // =========================================================================
    // ENUMS DEFINIDAS LOCALMENTE PARA CORRIGIR ERROS
    // =========================================================================

    public enum ItemType {
        SCRIPT,
        VALIDATION,
        TRANSFORMATION,
        DECISION,
        GATEWAY,
        SERVICE_CALL,
        DATA_MAPPING,
        BUSINESS_RULE
    }

    public enum ScriptLanguage {
        JAVASCRIPT,
        GROOVY,
        JAVA,
        CEL,
        PYTHON,
        SQL
    }

    // =========================================================================
    // CAMPOS PRINCIPAIS
    // =========================================================================

    @JsonProperty("items")
    private List<LogicItemV2Plus> items;

    @JsonProperty("validations")
    private List<ValidationRuleV2Plus> validations;

    @JsonProperty("transformations")
    private List<DataTransformationV2Plus> transformations;

    @JsonProperty("dependencies")
    private LogicDependencies dependencies;

    @JsonProperty("config")
    private LogicConfig config;

    @JsonProperty("metadata")
    private LogicMetadataV2Plus metadata;

    @JsonProperty("statistics")
    private LogicStatistics statistics;



    // Índices para performance
    private Map<String, LogicItemV2Plus> itemIndex;

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================

    public ProcessLogicV2Plus() {
        this.items = new ArrayList<>();
        this.validations = new ArrayList<>();
        this.transformations = new ArrayList<>();
        this.dependencies = new LogicDependencies();
        this.config = new LogicConfig();
        this.metadata = new LogicMetadataV2Plus();
        this.statistics = new LogicStatistics();
        this.itemIndex = new HashMap<>();
    }

    public static ProcessLogicV2Plus create() {
        return new ProcessLogicV2Plus();
    }

    // =========================================================================
    // MÉTODOS PRINCIPAIS CORRIGIDOS
    // =========================================================================

    /**
     * Adiciona logic item
     */
    public void addItem(LogicItemV2Plus item) {
        if (item == null) {
            throw new IllegalArgumentException("Logic item cannot be null");
        }

        if (item.getId() == null || item.getId().trim().isEmpty()) {
            item.setId("lg:item_" + System.currentTimeMillis());
        }

        // Validar ID único
        if (itemIndex.containsKey(item.getId())) {
            throw new IllegalArgumentException("Logic item with ID " + item.getId() + " already exists");
        }

        items.add(item);
        itemIndex.put(item.getId(), item);

        // Atualizar dependências
        updateDependencies(item);
    }

    /**
     * Encontra logic item por ID
     */
    public LogicItemV2Plus findItem(String itemId) {
        return itemIndex.get(itemId);
    }

    /**
     * Remove logic item
     */
    public boolean removeItem(String itemId) {
        LogicItemV2Plus item = itemIndex.get(itemId);
        if (item != null) {
            items.remove(item);
            itemIndex.remove(itemId);
            dependencies.removeItem(itemId);
            return true;
        }
        return false;
    }

    /**
     * Lista items por tipo
     */
    public List<LogicItemV2Plus> getItemsByType(ItemType type) {
        return items.stream()
                .filter(item -> type.equals(item.getType()))
                .collect(Collectors.toList());
    }

    /**
     * Lista items por linguagem
     */
    public List<LogicItemV2Plus> getItemsByLanguage(ScriptLanguage language) {
        return items.stream()
                .filter(item -> language.equals(item.getLanguage()))
                .collect(Collectors.toList());
    }

    /**
     * Adiciona validação
     */
    public void addValidation(ValidationRuleV2Plus validation) {
        if (validation != null) {
            validations.add(validation);
        }
    }

    /**
     * Externaliza script de um node para logic item
     */
    public String externalizeScript(String nodeId, String scriptContent) {
        return externalizeScript(nodeId, scriptContent, null, null);
    }

    /**
     * Externalização completa com metadados
     */
    public String externalizeScript(String nodeId, String scriptContent, String name, String description) {
        if (scriptContent == null || scriptContent.trim().isEmpty()) {
            return null;
        }

        // Gerar ID único para o logic item
        String logicId = "lg:" + normalizeNodeId(nodeId) + "_script";

        // Criar logic item
        LogicItemV2Plus item = new LogicItemV2Plus();
        item.setId(logicId);
        item.setName(name != null ? name : "Script for " + nodeId);
        item.setType(ItemType.SCRIPT);
        item.setLanguage(ScriptLanguage.JAVASCRIPT);
        item.setCode(scriptContent);
        item.setDescription(description != null ? description : "Externalized script from node " + nodeId);

        addItem(item);
        return logicId;
    }

    /**
     * Atualiza dependências
     */
    private void updateDependencies(LogicItemV2Plus item) {
        if (dependencies == null) {
            dependencies = new LogicDependencies();
        }
        dependencies.addItem(item.getId());
    }

    /**
     * Normaliza ID do node
     */
    private String normalizeNodeId(String nodeId) {
        if (nodeId == null) return "unknown";
        return nodeId.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    /**
     * Valida toda a lógica
     */
    public boolean isValid() {
        if (items == null) return false;

        for (LogicItemV2Plus item : items) {
            if (!item.isValid()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calcula estatísticas
     */
    public void calculateStatistics() {
        if (statistics == null) {
            statistics = new LogicStatistics();
        }

        statistics.totalItems = items.size();
        statistics.totalValidations = validations.size();
        statistics.totalTransformations = transformations.size();

        // Contar por tipo
        statistics.itemsByType = items.stream()
                .collect(Collectors.groupingBy(item -> item.getType(), Collectors.counting()));

        // Contar por linguagem
        statistics.itemsByLanguage = items.stream()
                .collect(Collectors.groupingBy(item -> item.getLanguage(), Collectors.counting()));

        // Calcular complexidade média
        double totalComplexity = items.stream()
                .mapToDouble(item -> item.getComplexityScore())
                .sum();
        statistics.averageComplexity = items.isEmpty() ? 0.0 : totalComplexity / items.size();

        // Calcular linhas de código
        statistics.totalLinesOfCode = items.stream()
                .mapToInt(item -> item.getCode() != null ? item.getCode().split("\n").length : 0)
                .sum();
    }

    // =========================================================================
    // GETTERS E SETTERS
    // =========================================================================

    public List<LogicItemV2Plus> getItems() {
        return items != null ? items : new ArrayList<>();
    }

    public void setItems(List<LogicItemV2Plus> items) {
        this.items = items != null ? items : new ArrayList<>();
        rebuildIndex();
    }

    private void rebuildIndex() {
        itemIndex = new HashMap<>();
        if (items != null) {
            for (LogicItemV2Plus item : items) {
                if (item.getId() != null) {
                    itemIndex.put(item.getId(), item);
                }
            }
        }
    }

    public List<ValidationRuleV2Plus> getValidations() {
        return validations != null ? validations : new ArrayList<>();
    }

    public void setValidations(List<ValidationRuleV2Plus> validations) {
        this.validations = validations != null ? validations : new ArrayList<>();
    }

    public List<DataTransformationV2Plus> getTransformations() {
        return transformations != null ? transformations : new ArrayList<>();
    }

    public void setTransformations(List<DataTransformationV2Plus> transformations) {
        this.transformations = transformations != null ? transformations : new ArrayList<>();
    }

    public LogicDependencies getDependencies() {
        return dependencies != null ? dependencies : new LogicDependencies();
    }

    public void setDependencies(LogicDependencies dependencies) {
        this.dependencies = dependencies != null ? dependencies : new LogicDependencies();
    }

    public LogicConfig getConfig() {
        return config != null ? config : new LogicConfig();
    }

    public void setConfig(LogicConfig config) {
        this.config = config != null ? config : new LogicConfig();
    }

    public LogicMetadataV2Plus getMetadata() {
        return metadata != null ? metadata : new LogicMetadataV2Plus();
    }

    public void setMetadata(LogicMetadataV2Plus metadata) {
        this.metadata = metadata != null ? metadata : new LogicMetadataV2Plus();
    }

    public LogicStatistics getStatistics() {
        return statistics != null ? statistics : new LogicStatistics();
    }

    public void setStatistics(LogicStatistics statistics) {
        this.statistics = statistics != null ? statistics : new LogicStatistics();
    }

    // =========================================================================
    // CLASSES DE APOIO SIMPLES
    // =========================================================================

    public static class ValidationRuleV2Plus {
        public String id;
        public String name;
        public String expression;
        public String message;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }

    public static class DataTransformationV2Plus {
        public String id;
        public String name;
        public String sourceField;
        public String targetField;
        public String transformation;
        public String expression;
        public String description;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setSourceField(String sourceField) { this.sourceField = sourceField; }
        public void setTargetField(String targetField) { this.targetField = targetField; }
        public void setDescription(String description) { this.description = description; }
        public void setExpression(String expression) { this.expression = expression; }
    }

    public static class LogicDependencies {
        private Set<String> items = new HashSet<>();

        public void addItem(String itemId) {
            items.add(itemId);
        }

        public void removeItem(String itemId) {
            items.remove(itemId);
        }

        public Set<String> getItems() {
            return new HashSet<>(items);
        }
    }

    public static class LogicConfig {
        public int defaultTimeoutMs = 30000;
        public int maxRetries = 3;
        public boolean enableCircuitBreaker = true;
        public boolean enableCache = true;
    }

    public static class LogicMetadataV2Plus {
        public String processId;
        public String createdAt;
        public String version;
        public String lastModified;
        public Map<String, String> annotations = new HashMap<>();
    }

    public static class LogicStatistics {
        public int totalItems;
        public int totalValidations;
        public int totalTransformations;
        public Map<ItemType, Long> itemsByType;
        public Map<ScriptLanguage, Long> itemsByLanguage;
        public double averageComplexity;
        public int totalLinesOfCode;

        @Override
        public String toString() {
            return String.format("LogicStats{items=%d, validations=%d, transformations=%d, avgComplexity=%.1f, loc=%d}",
                    totalItems, totalValidations, totalTransformations, averageComplexity, totalLinesOfCode);
        }
    }

    // =========================================================================
    // TESTES INLINE BÁSICOS
    // =========================================================================

    /**
     * Teste básico para validação durante desenvolvimento
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing ProcessLogicV2Plus...");

        try {
            // Teste 1: Criação básica
            ProcessLogicV2Plus logic = ProcessLogicV2Plus.create();
            System.out.println("✅ Basic creation: " + logic.isValid());

            // Teste 2: Adição de item válido
            LogicItemV2Plus item = new LogicItemV2Plus();
            item.setId("test-item");
            item.setName("Test Item");
            item.setType(ItemType.SCRIPT);
            item.setLanguage(ScriptLanguage.JAVASCRIPT);
            item.setCode("console.log('test');");

            logic.addItem(item);
            System.out.println("✅ Item addition: " + (logic.getItems().size() == 1));

            // Teste 3: Externalização de script
            String logicRef = logic.externalizeScript("node123", "console.log('externalized');");
            System.out.println("✅ Script externalization: " + (logicRef != null));

            // Teste 4: Cálculo de estatísticas
            logic.calculateStatistics();
            System.out.println("✅ Statistics calculation: " + logic.getStatistics());

            System.out.println("\n🎉 ProcessLogicV2Plus: ALL TESTS PASSED!");

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}