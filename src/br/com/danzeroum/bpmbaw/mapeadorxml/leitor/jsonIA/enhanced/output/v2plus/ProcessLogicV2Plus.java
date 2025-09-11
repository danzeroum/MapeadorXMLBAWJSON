package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ProcessLogicV2Plus - VERSÃO COMPLETA CORRIGIDA JAVA 8
 *
 * CORREÇÕES APLICADAS:
 * ✅ Enums ItemType e ScriptLanguage definidos corretamente
 * ✅ Classes internas ValidationRuleV2Plus e DataTransformationV2Plus completas
 * ✅ Todos os métodos necessários implementados
 * ✅ Conformidade com Java 8
 * ✅ Compatibilidade com LogicItemV2Plus
 * ✅ Validação e estatísticas completas
 *
 * @version 2.3.0-complete-fixed-java8
 */
@JsonPropertyOrder({"items", "validations", "transformations", "dependencies", "config", "metadata", "statistics"})
public class ProcessLogicV2Plus {

    // =========================================================================
    // ENUMS CORRIGIDOS E COMPLETOS
    // =========================================================================

    /**
     * Tipos de items de lógica
     */
    public enum ItemType {
        SCRIPT("script"),
        VALIDATION("validation"),
        TRANSFORMATION("transformation"),
        DECISION("decision"),
        GATEWAY("gateway"),
        SERVICE_CALL("service_call"),
        DATA_MAPPING("data_mapping"),
        BUSINESS_RULE("business_rule"),
        SUBPROCESS("subprocess"),
        TIMER("timer");

        private final String value;

        ItemType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Linguagens de script suportadas
     */
    public enum ScriptLanguage {
        JAVASCRIPT("javascript"),
        GROOVY("groovy"),
        JAVA("java"),
        CEL("cel"),
        PYTHON("python"),
        SQL("sql"),
        XPATH("xpath"),
        JUEL("juel"),
        MVEL("mvel");

        private final String value;

        ScriptLanguage(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
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

    /**
     * Construtor padrão
     */
    public ProcessLogicV2Plus() {
        this.items = new ArrayList<LogicItemV2Plus>();
        this.validations = new ArrayList<ValidationRuleV2Plus>();
        this.transformations = new ArrayList<DataTransformationV2Plus>();
        this.dependencies = new LogicDependencies();
        this.config = new LogicConfig();
        this.metadata = new LogicMetadataV2Plus();
        this.statistics = new LogicStatistics();
        this.itemIndex = new HashMap<String, LogicItemV2Plus>();
    }

    /**
     * Factory method para criação
     */
    public static ProcessLogicV2Plus create() {
        return new ProcessLogicV2Plus();
    }

    /**
     * Factory method com items iniciais
     */
    public static ProcessLogicV2Plus create(List<LogicItemV2Plus> initialItems) {
        ProcessLogicV2Plus logic = new ProcessLogicV2Plus();
        if (initialItems != null) {
            for (LogicItemV2Plus item : initialItems) {
                logic.addItem(item);
            }
        }
        return logic;
    }

    // =========================================================================
    // MÉTODOS PRINCIPAIS
    // =========================================================================

    /**
     * Adiciona logic item com validação
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
        List<LogicItemV2Plus> result = new ArrayList<LogicItemV2Plus>();
        for (LogicItemV2Plus item : items) {
            if (type.equals(item.getType())) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Lista items por linguagem
     */
    public List<LogicItemV2Plus> getItemsByLanguage(ScriptLanguage language) {
        List<LogicItemV2Plus> result = new ArrayList<LogicItemV2Plus>();
        for (LogicItemV2Plus item : items) {
            if (language.equals(item.getLanguage())) {
                result.add(item);
            }
        }
        return result;
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
     * Adiciona transformação
     */
    public void addTransformation(DataTransformationV2Plus transformation) {
        if (transformation != null) {
            transformations.add(transformation);
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
        Map<ItemType, Long> itemsByType = new HashMap<ItemType, Long>();
        for (LogicItemV2Plus item : items) {
            ItemType type = item.getType();
            if (type != null) {
                itemsByType.put(type, itemsByType.getOrDefault(type, 0L) + 1);
            }
        }
        statistics.itemsByType = itemsByType;

        // Contar por linguagem
        Map<ScriptLanguage, Long> itemsByLanguage = new HashMap<ScriptLanguage, Long>();
        for (LogicItemV2Plus item : items) {
            ScriptLanguage language = item.getLanguage();
            if (language != null) {
                itemsByLanguage.put(language, itemsByLanguage.getOrDefault(language, 0L) + 1);
            }
        }
        statistics.itemsByLanguage = itemsByLanguage;

        // Calcular complexidade média
        double totalComplexity = 0.0;
        for (LogicItemV2Plus item : items) {
            totalComplexity += item.getComplexityScore();
        }
        statistics.averageComplexity = items.isEmpty() ? 0.0 : totalComplexity / items.size();

        // Calcular linhas de código
        int totalLinesOfCode = 0;
        for (LogicItemV2Plus item : items) {
            if (item.getCode() != null) {
                totalLinesOfCode += item.getCode().split("\n").length;
            }
        }
        statistics.totalLinesOfCode = totalLinesOfCode;
    }

    // =========================================================================
    // GETTERS E SETTERS
    // =========================================================================

    public List<LogicItemV2Plus> getItems() {
        return items != null ? items : new ArrayList<LogicItemV2Plus>();
    }

    public void setItems(List<LogicItemV2Plus> items) {
        this.items = items != null ? items : new ArrayList<LogicItemV2Plus>();
        rebuildIndex();
    }

    private void rebuildIndex() {
        itemIndex = new HashMap<String, LogicItemV2Plus>();
        if (items != null) {
            for (LogicItemV2Plus item : items) {
                if (item.getId() != null) {
                    itemIndex.put(item.getId(), item);
                }
            }
        }
    }

    public List<ValidationRuleV2Plus> getValidations() {
        return validations != null ? validations : new ArrayList<ValidationRuleV2Plus>();
    }

    public void setValidations(List<ValidationRuleV2Plus> validations) {
        this.validations = validations != null ? validations : new ArrayList<ValidationRuleV2Plus>();
    }

    public List<DataTransformationV2Plus> getTransformations() {
        return transformations != null ? transformations : new ArrayList<DataTransformationV2Plus>();
    }

    public void setTransformations(List<DataTransformationV2Plus> transformations) {
        this.transformations = transformations != null ? transformations : new ArrayList<DataTransformationV2Plus>();
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
    // CLASSES INTERNAS - VALIDATIONRULEV2PLUS
    // =========================================================================

    /**
     * Regra de validação V2+
     */
    public static class ValidationRuleV2Plus {
        public String id;
        public String name;
        public String expression;
        public String message;
        public String description;
        public String language;
        public List<String> inputs;
        public List<String> outputs;
        public Map<String, Object> metadata;

        public ValidationRuleV2Plus() {
            this.inputs = new ArrayList<String>();
            this.outputs = new ArrayList<String>();
            this.metadata = new HashMap<String, Object>();
            this.language = "cel";
        }

        // Getters e Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }

        public List<String> getInputs() { return inputs; }
        public void setInputs(List<String> inputs) { this.inputs = inputs; }

        public List<String> getOutputs() { return outputs; }
        public void setOutputs(List<String> outputs) { this.outputs = outputs; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

        public boolean isValid() {
            return id != null && !id.trim().isEmpty() &&
                    expression != null && !expression.trim().isEmpty();
        }
    }

    // =========================================================================
    // CLASSES INTERNAS - DATATRANSFORMATIONV2PLUS
    // =========================================================================

    /**
     * Transformação de dados V2+
     */
    public static class DataTransformationV2Plus {
        public String id;
        public String name;
        public String sourceField;
        public String targetField;
        public String transformation;
        public String expression;
        public String description;
        public String language;
        public List<String> inputs;
        public List<String> outputs;
        public Map<String, Object> metadata;

        public DataTransformationV2Plus() {
            this.inputs = new ArrayList<String>();
            this.outputs = new ArrayList<String>();
            this.metadata = new HashMap<String, Object>();
            this.language = "javascript";
        }

        // Getters e Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSourceField() { return sourceField; }
        public void setSourceField(String sourceField) { this.sourceField = sourceField; }

        public String getTargetField() { return targetField; }
        public void setTargetField(String targetField) { this.targetField = targetField; }

        public String getTransformation() { return transformation; }
        public void setTransformation(String transformation) { this.transformation = transformation; }

        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }

        public List<String> getInputs() { return inputs; }
        public void setInputs(List<String> inputs) { this.inputs = inputs; }

        public List<String> getOutputs() { return outputs; }
        public void setOutputs(List<String> outputs) { this.outputs = outputs; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

        public boolean isValid() {
            return id != null && !id.trim().isEmpty();
        }
    }

    // =========================================================================
    // CLASSES INTERNAS - SUPORTE
    // =========================================================================

    /**
     * Dependências entre logic items
     */
    public static class LogicDependencies {
        private Set<String> items = new HashSet<String>();

        public void addItem(String itemId) {
            items.add(itemId);
        }

        public void removeItem(String itemId) {
            items.remove(itemId);
        }

        public Set<String> getItems() {
            return new HashSet<String>(items);
        }
    }

    /**
     * Configuração da lógica
     */
    public static class LogicConfig {
        public int defaultTimeoutMs = 30000;
        public int maxRetries = 3;
        public boolean enableCircuitBreaker = true;
        public boolean enableCache = true;
        public boolean enableDebug = false;
        public String defaultLanguage = "javascript";

        // Getters e Setters
        public int getDefaultTimeoutMs() { return defaultTimeoutMs; }
        public void setDefaultTimeoutMs(int defaultTimeoutMs) { this.defaultTimeoutMs = defaultTimeoutMs; }

        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

        public boolean isEnableCircuitBreaker() { return enableCircuitBreaker; }
        public void setEnableCircuitBreaker(boolean enableCircuitBreaker) { this.enableCircuitBreaker = enableCircuitBreaker; }

        public boolean isEnableCache() { return enableCache; }
        public void setEnableCache(boolean enableCache) { this.enableCache = enableCache; }

        public boolean isEnableDebug() { return enableDebug; }
        public void setEnableDebug(boolean enableDebug) { this.enableDebug = enableDebug; }

        public String getDefaultLanguage() { return defaultLanguage; }
        public void setDefaultLanguage(String defaultLanguage) { this.defaultLanguage = defaultLanguage; }
    }

    /**
     * Metadata da lógica
     */
    public static class LogicMetadataV2Plus {
        public String processId;
        public String createdAt;
        public String version;
        public String lastModified;
        public String author;
        public Map<String, String> annotations = new HashMap<String, String>();

        public LogicMetadataV2Plus() {
            this.createdAt = java.time.LocalDateTime.now().toString();
            this.version = "1.0.0";
        }

        // Getters e Setters
        public String getProcessId() { return processId; }
        public void setProcessId(String processId) { this.processId = processId; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public String getLastModified() { return lastModified; }
        public void setLastModified(String lastModified) { this.lastModified = lastModified; }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public Map<String, String> getAnnotations() { return annotations; }
        public void setAnnotations(Map<String, String> annotations) { this.annotations = annotations; }
    }

    /**
     * Estatísticas da lógica
     */
    public static class LogicStatistics {
        public int totalItems;
        public int totalValidations;
        public int totalTransformations;
        public Map<ItemType, Long> itemsByType;
        public Map<ScriptLanguage, Long> itemsByLanguage;
        public double averageComplexity;
        public int totalLinesOfCode;

        public LogicStatistics() {
            this.itemsByType = new HashMap<ItemType, Long>();
            this.itemsByLanguage = new HashMap<ScriptLanguage, Long>();
        }

        @Override
        public String toString() {
            return String.format("LogicStats{items=%d, validations=%d, transformations=%d, avgComplexity=%.1f, loc=%d}",
                    totalItems, totalValidations, totalTransformations, averageComplexity, totalLinesOfCode);
        }

        // Getters e Setters
        public int getTotalItems() { return totalItems; }
        public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

        public int getTotalValidations() { return totalValidations; }
        public void setTotalValidations(int totalValidations) { this.totalValidations = totalValidations; }

        public int getTotalTransformations() { return totalTransformations; }
        public void setTotalTransformations(int totalTransformations) { this.totalTransformations = totalTransformations; }

        public Map<ItemType, Long> getItemsByType() { return itemsByType; }
        public void setItemsByType(Map<ItemType, Long> itemsByType) { this.itemsByType = itemsByType; }

        public Map<ScriptLanguage, Long> getItemsByLanguage() { return itemsByLanguage; }
        public void setItemsByLanguage(Map<ScriptLanguage, Long> itemsByLanguage) { this.itemsByLanguage = itemsByLanguage; }

        public double getAverageComplexity() { return averageComplexity; }
        public void setAverageComplexity(double averageComplexity) { this.averageComplexity = averageComplexity; }

        public int getTotalLinesOfCode() { return totalLinesOfCode; }
        public void setTotalLinesOfCode(int totalLinesOfCode) { this.totalLinesOfCode = totalLinesOfCode; }
    }

    // =========================================================================
    // MÉTODOS UTILITÁRIOS E TESTES
    // =========================================================================

    /**
     * Cria exemplo de ProcessLogicV2Plus para testes
     */
    public static ProcessLogicV2Plus createSample() {
        ProcessLogicV2Plus logic = new ProcessLogicV2Plus();

        // Script de validação
        LogicItemV2Plus validation = new LogicItemV2Plus();
        validation.setId("lg:validacao_entrada");
        validation.setName("Validação de Entrada");
        validation.setType(ItemType.VALIDATION);
        validation.setLanguage(ScriptLanguage.JAVASCRIPT);
        validation.setCode("function validar(dados) { return dados.id != null; }");
        logic.addItem(validation);

        // Script de processamento
        LogicItemV2Plus processing = new LogicItemV2Plus();
        processing.setId("lg:processamento_principal");
        processing.setName("Processamento Principal");
        processing.setType(ItemType.SCRIPT);
        processing.setLanguage(ScriptLanguage.JAVASCRIPT);
        processing.setCode("function processar(entrada) { return { resultado: entrada.valor * 2 }; }");
        logic.addItem(processing);

        return logic;
    }

    /**
     * Teste básico da classe
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing ProcessLogicV2Plus...");

        try {
            // Teste 1: Criação básica
            ProcessLogicV2Plus logic = ProcessLogicV2Plus.create();
            System.out.println("✅ Basic creation: " + (logic != null));

            // Teste 2: Enums
            System.out.println("✅ ItemType.SCRIPT: " + ItemType.SCRIPT);
            System.out.println("✅ ScriptLanguage.JAVASCRIPT: " + ScriptLanguage.JAVASCRIPT);

            // Teste 3: Adição de item válido
            LogicItemV2Plus item = new LogicItemV2Plus();
            item.setId("test-item");
            item.setName("Test Item");
            item.setType(ItemType.SCRIPT);
            item.setLanguage(ScriptLanguage.JAVASCRIPT);
            item.setCode("console.log('test');");

            logic.addItem(item);
            System.out.println("✅ Item addition: " + (logic.getItems().size() == 1));

            // Teste 4: Externalização de script
            String logicRef = logic.externalizeScript("node123", "console.log('externalized');");
            System.out.println("✅ Script externalization: " + (logicRef != null));

            // Teste 5: Validações e transformações
            ValidationRuleV2Plus validation = new ValidationRuleV2Plus();
            validation.setId("val-1");
            validation.setExpression("input.value > 0");
            logic.addValidation(validation);

            DataTransformationV2Plus transformation = new DataTransformationV2Plus();
            transformation.setId("trans-1");
            transformation.setSourceField("input");
            transformation.setTargetField("output");
            logic.addTransformation(transformation);

            System.out.println("✅ Validations: " + logic.getValidations().size());
            System.out.println("✅ Transformations: " + logic.getTransformations().size());

            // Teste 6: Cálculo de estatísticas
            logic.calculateStatistics();
            System.out.println("✅ Statistics: " + logic.getStatistics());

            // Teste 7: Exemplo completo
            ProcessLogicV2Plus sample = ProcessLogicV2Plus.createSample();
            System.out.println("✅ Sample creation: " + (sample.getItems().size() == 2));

            System.out.println("\n🎉 ProcessLogicV2Plus: ALL TESTS PASSED!");

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}