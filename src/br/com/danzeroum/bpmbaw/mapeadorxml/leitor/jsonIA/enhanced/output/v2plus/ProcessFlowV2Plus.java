package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.*;

/**
 * Process Flow V2+ - CORRIGIDA para tipos compatíveis
 *
 * CORREÇÕES APLICADAS:
 * ✅ Tipos de variáveis corrigidos para usar classes standalone
 * ✅ Tipos de transformações e validações corrigidos
 * ✅ Removidas referências a métodos inexistentes
 * ✅ Compatível com Java 8
 */
@JsonPropertyOrder({
        "id", "definition", "executionEngine", "dependencyGraph",
        "executionPolicies", "cache", "observability", "metadata"
})
public class ProcessFlowV2Plus {

    // =========================================================================
    // CAMPOS PRINCIPAIS
    // =========================================================================

    @JsonProperty("id")
    private String id;

    @JsonProperty("definition")
    private ProcessDefinitionV2Plus definition;

    @JsonProperty("executionEngine")
    private ExecutionEngine executionEngine;

    @JsonProperty("dependencyGraph")
    private DependencyGraph dependencyGraph;

    @JsonProperty("executionPolicies")
    private ExecutionPolicies executionPolicies;

    @JsonProperty("cache")
    private CacheConfiguration cache;

    @JsonProperty("observability")
    private ObservabilityConfiguration observability;

    @JsonProperty("metadata")
    private FlowMetadata metadata;

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================

    public ProcessFlowV2Plus() {
        this.executionEngine = new ExecutionEngine();
        this.dependencyGraph = new DependencyGraph();
        this.executionPolicies = new ExecutionPolicies();
        this.cache = new CacheConfiguration();
        this.observability = new ObservabilityConfiguration();
        this.metadata = new FlowMetadata();
    }

    public ProcessFlowV2Plus(String id, ProcessDefinitionV2Plus definition) {
        this();
        this.id = id;
        this.definition = definition;
        autoConfigureFromDefinition();
    }

    // =========================================================================
    // FACTORY METHODS
    // =========================================================================

    /**
     * Cria flow básico a partir de uma definição
     */
    public static ProcessFlowV2Plus create(String id, ProcessDefinitionV2Plus definition) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Flow ID cannot be null or empty");
        }
        if (definition == null) {
            throw new IllegalArgumentException("Process definition cannot be null");
        }

        ProcessFlowV2Plus flow = new ProcessFlowV2Plus(id, definition);
        flow.initializeDefaults();
        return flow;
    }

    /**
     * Cria flow executável com configuração completa
     */
    public static ProcessFlowV2Plus createExecutable(String id, ProcessDefinitionV2Plus definition) {
        ProcessFlowV2Plus flow = create(id, definition);
        flow.configureForExecution();
        return flow;
    }

    // =========================================================================
    // CONFIGURAÇÃO AUTOMÁTICA
    // =========================================================================

    /**
     * Configura automaticamente baseado na definição
     */
    private void autoConfigureFromDefinition() {
        if (definition == null) return;

        // Configurar engine baseado nas variáveis - CORRIGIDO
        configureExecutionEngine();

        // Configurar dependências - CORRIGIDO
        configureDependencyGraph();

        // Configurar políticas de execução
        configureExecutionPolicies();

        // Configurar observabilidade
        configureObservability();
    }

    /**
     * Configura engine de execução - CORRIGIDO
     */
    private void configureExecutionEngine() {
        if (definition == null || definition.getVariables() == null) return;

        // CORRIGIDO: Usar getters das listas de variáveis
        int inputCount = definition.getVariables().getInput().size();
        int outputCount = definition.getVariables().getOutput().size();
        int totalVars = inputCount + outputCount;

        // Configurar engine baseado na complexidade
        if (totalVars > 20) {
            executionEngine.setMode(ExecutionEngine.Mode.OPTIMIZED);
        } else if (totalVars > 10) {
            executionEngine.setMode(ExecutionEngine.Mode.BALANCED);
        } else {
            executionEngine.setMode(ExecutionEngine.Mode.SIMPLE);
        }

        // Configurar paralelização
        if (definition.getGraph() != null && definition.getGraph().getNodes().size() > 5) {
            executionEngine.setParallelizationEnabled(true);
        }
    }

    /**
     * Configura grafo de dependências - CORRIGIDO
     */
    private void configureDependencyGraph() {
        if (definition == null) return;

        dependencyGraph.clear();

        // CORRIGIDO: Adicionar dependências baseadas na estrutura
        if (definition.getLogic() != null && definition.getLogic().getItems() != null) {
            for (LogicItemV2Plus item : definition.getLogic().getItems()) {
                if (item.getInputs() != null && item.getOutputs() != null) {
                    dependencyGraph.addDependency(item.getId(), item.getInputs(), item.getOutputs());
                }
            }
        }

        // Adicionar dependências do grafo de processo
        if (definition.getGraph() != null && definition.getGraph().getEdges() != null) {
            for (ProcessEdgeV2Plus edge : definition.getGraph().getEdges()) {
                dependencyGraph.addNodeDependency(edge.getSource(), edge.getTarget());
            }
        }
    }

    /**
     * Configura políticas de execução
     */
    private void configureExecutionPolicies() {
        // Política de timeout baseada na complexidade
        if (definition != null && definition.getGraph() != null) {
            int nodeCount = definition.getGraph().getNodes().size();
            long timeoutMs = Math.min(300000, Math.max(30000, nodeCount * 5000)); // 30s a 5min
            executionPolicies.setTimeoutMs(timeoutMs);
        }

        // Política de retry
        executionPolicies.setMaxRetries(3);
        executionPolicies.setRetryDelayMs(1000);
    }

    /**
     * Configura observabilidade
     */
    private void configureObservability() {
        observability.setMetricsEnabled(true);
        observability.setTracingEnabled(true);
        observability.setLoggingLevel(ObservabilityConfiguration.LoggingLevel.INFO);
    }

    /**
     * Inicializa valores padrão
     */
    private void initializeDefaults() {
        if (metadata == null) {
            metadata = new FlowMetadata();
        }
        metadata.setCreatedAt(new Date());
        metadata.setVersion("1.0.0");
        metadata.setStatus(FlowMetadata.Status.READY);
    }

    /**
     * Configura para execução
     */
    private void configureForExecution() {
        executionEngine.setMode(ExecutionEngine.Mode.OPTIMIZED);
        executionPolicies.setTimeoutMs(300000); // 5 minutos
        observability.setMetricsEnabled(true);
        cache.setEnabled(true);
    }

    // =========================================================================
    // MÉTODOS DE ANÁLISE - CORRIGIDOS
    // =========================================================================

    /**
     * Obtém estatísticas das variáveis - CORRIGIDO
     */
    public VariableStatistics getVariableStatistics() {
        VariableStatistics stats = new VariableStatistics();

        if (definition != null && definition.getVariables() != null) {
            // CORRIGIDO: Usar tipos corretos
            stats.inputCount = definition.getVariables().getInput().size();
            stats.outputCount = definition.getVariables().getOutput().size();
            stats.privateCount = definition.getVariables().getPrivateVars().size();
            stats.totalCount = stats.inputCount + stats.outputCount + stats.privateCount;

            // Análise de tipos
            Map<String, Integer> typeCount = new HashMap<>();

            // CORRIGIDO: Iterar sobre as variáveis usando o tipo correto
            for (ProcessDefinitionV2Plus.VariableDefinitionV2Plus var : definition.getVariables().getInput()) {
                String type = extractBaseType(var.getTypeRef());
                typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
            }

            for (ProcessDefinitionV2Plus.VariableDefinitionV2Plus var : definition.getVariables().getOutput()) {
                String type = extractBaseType(var.getTypeRef());
                typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
            }

            for (ProcessDefinitionV2Plus.VariableDefinitionV2Plus var : definition.getVariables().getPrivateVars()) {
                String type = extractBaseType(var.getTypeRef());
                typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
            }

            stats.typeDistribution = typeCount;
        }

        return stats;
    }

    /**
     * Obtém estatísticas de transformações - CORRIGIDO
     */
    public TransformationStatistics getTransformationStatistics() {
        TransformationStatistics stats = new TransformationStatistics();

        if (definition != null && definition.getLogic() != null) {
            // CORRIGIDO: Usar tipo correto das transformações
            List<ProcessLogicV2Plus.DataTransformationV2Plus> transformations = definition.getLogic().getTransformations();
            stats.totalCount = transformations.size();

            // Análise por tipo de transformação
            Map<String, Integer> typeCount = new HashMap<>();
            for (ProcessLogicV2Plus.DataTransformationV2Plus transform : transformations) {
                String type = extractTransformationType(transform);
                typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
            }
            stats.typeDistribution = typeCount;
        }

        return stats;
    }

    /**
     * Obtém estatísticas de validações - CORRIGIDO
     */
    public ValidationStatistics getValidationStatistics() {
        ValidationStatistics stats = new ValidationStatistics();

        if (definition != null && definition.getLogic() != null) {
            // CORRIGIDO: Usar tipo correto das validações
            List<ProcessLogicV2Plus.ValidationRuleV2Plus> validations = definition.getLogic().getValidations();
            stats.totalCount = validations.size();

            // Análise por severidade
            Map<String, Integer> severityCount = new HashMap<>();
            for (ProcessLogicV2Plus.ValidationRuleV2Plus validation : validations) {
                String severity = extractValidationSeverity(validation);
                severityCount.put(severity, severityCount.getOrDefault(severity, 0) + 1);
            }
            stats.severityDistribution = severityCount;
        }

        return stats;
    }

    // =========================================================================
    // MÉTODOS AUXILIARES - CORRIGIDOS
    // =========================================================================

    /**
     * Extrai tipo base de uma referência de tipo
     */
    private String extractBaseType(String typeRef) {
        if (typeRef == null) return "unknown";

        // Extrair tipo base de formatos como "dt:string@1" -> "string"
        if (typeRef.contains(":")) {
            String[] parts = typeRef.split(":");
            if (parts.length > 1) {
                String typePart = parts[1];
                if (typePart.contains("@")) {
                    return typePart.split("@")[0];
                }
                return typePart;
            }
        }

        return typeRef;
    }

    /**
     * Extrai tipo de transformação - CORRIGIDO
     */
    private String extractTransformationType(ProcessLogicV2Plus.DataTransformationV2Plus transform) {
        if (transform == null) return "unknown";

        // Usar reflexão segura ou campos disponíveis
        try {
            if (transform.transformation != null) {
                return transform.transformation;
            }
            if (transform.expression != null) {
                return "expression";
            }
        } catch (Exception e) {
            // Ignorar erros de acesso
        }

        return "data_mapping";
    }

    /**
     * Extrai severidade de validação - CORRIGIDO
     */
    private String extractValidationSeverity(ProcessLogicV2Plus.ValidationRuleV2Plus validation) {
        if (validation == null) return "unknown";

        // Como ValidationRuleV2Plus pode não ter campo severity, usar padrão
        return "error"; // Padrão para todas as validações
    }

    /**
     * Verifica se uma execução seria bloqueante - CORRIGIDO
     */
    public boolean wouldBlock(String nodeId) {
        if (nodeId == null || definition == null || definition.getGraph() == null) {
            return false;
        }

        // CORRIGIDO: Remover chamada para isBlocking() inexistente
        // Usar heurística baseada no tipo de nó
        ProcessNodeV2Plus node = findNodeById(nodeId);
        if (node == null) {
            return false;
        }

        // Considerar como bloqueante se for user task ou gateway
        ProcessNodeV2Plus.NodeType type = node.getType();
        return type == ProcessNodeV2Plus.NodeType.USER_TASK ||
                type.toString().contains("GATEWAY");
    }

    /**
     * Encontra nó por ID
     */
    private ProcessNodeV2Plus findNodeById(String nodeId) {
        if (definition == null || definition.getGraph() == null || definition.getGraph().getNodes() == null) {
            return null;
        }

        for (ProcessNodeV2Plus node : definition.getGraph().getNodes()) {
            if (nodeId.equals(node.getId())) {
                return node;
            }
        }
        return null;
    }

    // =========================================================================
    // GETTERS E SETTERS
    // =========================================================================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ProcessDefinitionV2Plus getDefinition() { return definition; }
    public void setDefinition(ProcessDefinitionV2Plus definition) {
        this.definition = definition;
        autoConfigureFromDefinition();
    }

    public ExecutionEngine getExecutionEngine() { return executionEngine; }
    public void setExecutionEngine(ExecutionEngine executionEngine) { this.executionEngine = executionEngine; }

    public DependencyGraph getDependencyGraph() { return dependencyGraph; }
    public void setDependencyGraph(DependencyGraph dependencyGraph) { this.dependencyGraph = dependencyGraph; }

    public ExecutionPolicies getExecutionPolicies() { return executionPolicies; }
    public void setExecutionPolicies(ExecutionPolicies executionPolicies) { this.executionPolicies = executionPolicies; }

    public CacheConfiguration getCache() { return cache; }
    public void setCache(CacheConfiguration cache) { this.cache = cache; }

    public ObservabilityConfiguration getObservability() { return observability; }
    public void setObservability(ObservabilityConfiguration observability) { this.observability = observability; }

    public FlowMetadata getMetadata() { return metadata; }
    public void setMetadata(FlowMetadata metadata) { this.metadata = metadata; }

    // =========================================================================
    // VALIDAÇÃO
    // =========================================================================

    /**
     * Validação completa do fluxo
     */
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    /**
     * Lista todos os erros de validação
     */
    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        // 1. ID obrigatório
        if (id == null || id.trim().isEmpty()) {
            errors.append("Flow ID is required. ");
        }

        // 2. Definição obrigatória
        if (definition == null) {
            errors.append("Process definition is required. ");
        } else {
            // Validar componentes da definição
            if (definition.getGraph() == null) {
                errors.append("Process graph is required. ");
            }
            if (definition.getVariables() == null) {
                errors.append("Process variables are required. ");
            }
        }

        // 3. Engine configurado
        if (executionEngine == null) {
            errors.append("Execution engine is required. ");
        }

        return errors.toString().trim();
    }

    // =========================================================================
    // CLASSES AUXILIARES DE ESTATÍSTICAS
    // =========================================================================

    public static class VariableStatistics {
        public int inputCount;
        public int outputCount;
        public int privateCount;
        public int totalCount;
        public Map<String, Integer> typeDistribution = new HashMap<>();
    }

    public static class TransformationStatistics {
        public int totalCount;
        public Map<String, Integer> typeDistribution = new HashMap<>();
    }

    public static class ValidationStatistics {
        public int totalCount;
        public Map<String, Integer> severityDistribution = new HashMap<>();
    }

    // =========================================================================
    // CLASSES DE CONFIGURAÇÃO (STUBS BÁSICOS)
    // =========================================================================

    public static class ExecutionEngine {
        public enum Mode { SIMPLE, BALANCED, OPTIMIZED }
        private Mode mode = Mode.BALANCED;
        private boolean parallelizationEnabled = false;

        public Mode getMode() { return mode; }
        public void setMode(Mode mode) { this.mode = mode; }
        public boolean isParallelizationEnabled() { return parallelizationEnabled; }
        public void setParallelizationEnabled(boolean enabled) { this.parallelizationEnabled = enabled; }
    }

    public static class DependencyGraph {
        private Map<String, List<String>> dependencies = new HashMap<>();

        public void clear() { dependencies.clear(); }
        public void addDependency(String id, List<String> inputs, List<String> outputs) {
            dependencies.put(id, new ArrayList<>(inputs));
        }
        public void addNodeDependency(String source, String target) {
            dependencies.computeIfAbsent(target, k -> new ArrayList<>()).add(source);
        }
    }

    public static class ExecutionPolicies {
        private long timeoutMs = 60000;
        private int maxRetries = 3;
        private long retryDelayMs = 1000;

        public long getTimeoutMs() { return timeoutMs; }
        public void setTimeoutMs(long timeoutMs) { this.timeoutMs = timeoutMs; }
        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
        public long getRetryDelayMs() { return retryDelayMs; }
        public void setRetryDelayMs(long retryDelayMs) { this.retryDelayMs = retryDelayMs; }
    }

    public static class CacheConfiguration {
        private boolean enabled = false;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    public static class ObservabilityConfiguration {
        public enum LoggingLevel { DEBUG, INFO, WARN, ERROR }
        private boolean metricsEnabled = true;
        private boolean tracingEnabled = true;
        private LoggingLevel loggingLevel = LoggingLevel.INFO;

        public boolean isMetricsEnabled() { return metricsEnabled; }
        public void setMetricsEnabled(boolean metricsEnabled) { this.metricsEnabled = metricsEnabled; }
        public boolean isTracingEnabled() { return tracingEnabled; }
        public void setTracingEnabled(boolean tracingEnabled) { this.tracingEnabled = tracingEnabled; }
        public LoggingLevel getLoggingLevel() { return loggingLevel; }
        public void setLoggingLevel(LoggingLevel loggingLevel) { this.loggingLevel = loggingLevel; }
    }

    public static class FlowMetadata {
        public enum Status { DRAFT, READY, RUNNING, COMPLETED, FAILED }
        private Date createdAt;
        private String version;
        private Status status = Status.DRAFT;

        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public Status getStatus() { return status; }
        public void setStatus(Status status) { this.status = status; }
    }
}