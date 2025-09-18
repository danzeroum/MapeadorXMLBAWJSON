package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.*;

/**
 * Process Edge V2+ - Aresta IA-Friendly (Condições Centralizadas)
 *
 * ELIMINA PROBLEMAS V1/V2:
 * ❌ V1/V2: condições inline duplicadas nos edges
 * ❌ V1/V2: expressões TWJS não padronizadas
 * ❌ V1/V2: paths implícitos sem classificação
 * ✅ V2+: conditionRef para condições centralizadas
 * ✅ V2+: pathTags explícitos (happy-path, alternative-*)
 * ✅ V2+: validação source/target obrigatória
 *
 * CARACTERÍSTICAS V2+:
 * ✅ ConditionRef para reutilização de condições
 * ✅ PathTags para classificação de fluxos
 * ✅ Prioridade de execução explícita
 * ✅ Metadados de roteamento
 * ✅ Validação robusta de conectividade
 * ✅ Suporte a fluxos condicionais e default
 *
 * @version 2.1.0
 * @since V2+ IA-Friendly Migration
 */
@JsonPropertyOrder({
        "id", "source", "target", "label", "conditionRef", "pathTags", "priority", "isDefault", "properties", "metadata"
})
public class ProcessEdgeV2Plus {

    /**
     * ID único da edge (estável entre versões)
     */
    @JsonProperty("id")
    private String id;

    @JsonProperty("conditionRef")
    private String conditionRef;
    /**
     * ID do node de origem
     * OBRIGATÓRIO: Deve existir em graph.nodes[]
     */
    @JsonProperty("source")
    private String source;

    /**
     * ID do node de destino
     * OBRIGATÓRIO: Deve existir em graph.nodes[]
     */
    @JsonProperty("target")
    private String target;

    /**
     * Rótulo display da edge
     * OPCIONAL: Para visualização
     */
    @JsonProperty("label")
    private String label;

    /**
     * 🆕 NOVO V2+: Referência para condição centralizada
     * SUBSTITUI: expression inline da V1/V2
     * FORMATO: cd:nomeCondicao (aponta para conditions[])
     */

    /**
     * 🆕 NOVO V2+: Tags de classificação de path
     * VALORES: ["happy-path"], ["alternative-error"], ["alternative-timeout"], etc.
     */
    @JsonProperty("pathTags")
    private List<String> pathTags;

    /**
     * 🆕 NOVO V2+: Prioridade de avaliação
     * MENOR valor = maior prioridade (default: 100)
     */
    @JsonProperty("priority")
    private int priority;

    /**
     * Se é o fluxo padrão (default flow)
     * TRUE: Executado quando nenhuma condição é satisfeita
     */
    @JsonProperty("isDefault")
    private boolean isDefault;

    /**
     * Propriedades customizadas da edge
     */
    @JsonProperty("properties")
    private Map<String, Object> properties;


    /**
     * Metadados de migração e roteamento
     */
    @JsonProperty("metadata")
    private EdgeMetadata metadata;

    // ❌ CAMPOS REMOVIDOS DEFINITIVAMENTE (Breaking Changes):
    // - String expression                 → conditionRef (centralizado)
    // - String expressionLanguage         → movido para conditions[]
    // - Object inlineCondition            → conditionRef + conditions[]

    // =========================================================================
    // ENUMS E TIPOS
    // =========================================================================

    /**
     * Tipos de edge para classificação
     */
    public enum EdgeType {
        SEQUENCE_FLOW("sequenceFlow", "Fluxo de sequência padrão"),
        CONDITIONAL_FLOW("conditionalFlow", "Fluxo com condição"),
        DEFAULT_FLOW("defaultFlow", "Fluxo padrão (catch-all)"),
        MESSAGE_FLOW("messageFlow", "Fluxo de mensagem"),
        ASSOCIATION("association", "Associação"),
        DATA_FLOW("dataFlow", "Fluxo de dados");

        private final String bpmnName;
        private final String description;

        EdgeType(String bpmnName, String description) {
            this.bpmnName = bpmnName;
            this.description = description;
        }

        public String getBpmnName() { return bpmnName; }
        public String getDescription() { return description; }
    }

    /**
     * Tags de path predefinidos
     */
    public static class PathTags {
        public static final String HAPPY_PATH = "happy-path";
        public static final String ALTERNATIVE_ERROR = "alternative-error";
        public static final String ALTERNATIVE_TIMEOUT = "alternative-timeout";
        public static final String ALTERNATIVE_RETRY = "alternative-retry";
        public static final String ALTERNATIVE_ESCALATION = "alternative-escalation";
        public static final String ALTERNATIVE_CANCEL = "alternative-cancel";
        public static final String VALIDATION_FAILED = "validation-failed";
        public static final String BUSINESS_RULE = "business-rule";
        public static final String EXCEPTION_HANDLING = "exception-handling";
        public static final String COMPENSATION = "compensation";

        /**
         * Verifica se é um path de erro/exceção
         */
        public static boolean isErrorPath(List<String> tags) {
            if (tags == null) return false;
            return tags.stream().anyMatch(tag ->
                    tag.contains("error") || tag.contains("exception") || tag.contains("failed"));
        }

        /**
         * Verifica se é o happy path
         */
        public static boolean isHappyPath(List<String> tags) {
            return tags != null && tags.contains(HAPPY_PATH);
        }
    }

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================

    public ProcessEdgeV2Plus() {
        this.pathTags = new ArrayList<>();
        this.priority = 100; // Prioridade padrão
        this.isDefault = false;
        this.properties = new HashMap<>();
        this.metadata = new EdgeMetadata();
    }

    /**
     * Construtor completo para criação rápida
     */
    public ProcessEdgeV2Plus(String id, String source, String target, String label) {
        this();
        this.id = id;
        this.source = source;
        this.target = target;
        this.label = label;

        // Validar após construção
        if (!isBasicValid()) {
            throw new IllegalArgumentException("Invalid edge: " + getValidationErrors());
        }
    }

    /**
     * Factory method para criar edge a partir de dados V1/V2
     */
    public static ProcessEdgeV2Plus fromLegacy(String id, String source, String target, String label,
                                               String expression, String expressionLanguage) {
        ProcessEdgeV2Plus edge = new ProcessEdgeV2Plus();

        // Configurar campos básicos
        edge.id = normalizeEdgeId(id);
        edge.source = source;
        edge.target = target;
        edge.label = label;

        // Se há expressão, será centralizada (não inline)
        if (expression != null && !expression.trim().isEmpty()) {
            // ConditionRef será definido durante migração
            edge.metadata.hasLegacyCondition = true;
            edge.metadata.legacyExpression = expression;
            edge.metadata.legacyExpressionLanguage = expressionLanguage;

            // Detectar tipo de path baseado na expressão
            edge.pathTags.addAll(detectPathTagsFromExpression(expression));
        } else {
            // Sem condição = provavelmente happy path
            edge.pathTags.add(PathTags.HAPPY_PATH);
            edge.isDefault = true;
        }

        // Metadados de migração
        edge.metadata.sourceVersion = "1.0";
        edge.metadata.migrationTimestamp = java.time.Instant.now().toString();

        return edge;
    }

    /**
     * Normaliza ID de edge para formato consistente
     */
    private static String normalizeEdgeId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return "edge-" + System.currentTimeMillis();
        }

        // Manter formato existente se válido
        if (id.matches("^[a-zA-Z0-9._-]+$")) {
            return id;
        }

        // Limpar caracteres inválidos
        return id.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Detecta path tags baseado na expressão legada
     */
    private static List<String> detectPathTagsFromExpression(String expression) {
        List<String> tags = new ArrayList<>();

        if (expression == null) return tags;

        String expr = expression.toLowerCase();

        // Detectar padrões comuns
        if (expr.contains("error") || expr.contains("erro")) {
            tags.add(PathTags.ALTERNATIVE_ERROR);
        } else if (expr.contains("timeout") || expr.contains("tempo")) {
            tags.add(PathTags.ALTERNATIVE_TIMEOUT);
        } else if (expr.contains("valid") || expr.contains("valida")) {
            if (expr.contains("!") || expr.contains("false") || expr.contains("== 0")) {
                tags.add(PathTags.VALIDATION_FAILED);
            } else {
                tags.add(PathTags.HAPPY_PATH);
            }
        } else if (expr.contains("true") || expr.contains("== true")) {
            tags.add(PathTags.HAPPY_PATH);
        } else {
            tags.add(PathTags.BUSINESS_RULE);
        }

        return tags;
    }

    // =========================================================================
    // VALIDAÇÃO
    // =========================================================================

    /**
     * Validação completa da edge
     */
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    /**
     * Validação básica (usado no construtor)
     */
    private boolean isBasicValid() {
        return id != null && !id.trim().isEmpty() &&
                source != null && !source.trim().isEmpty() &&
                target != null && !target.trim().isEmpty();
    }

    /**
     * Lista todos os erros de validação
     */
    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        // 1. ID obrigatório e válido
        if (id == null || id.trim().isEmpty()) {
            errors.append("ID cannot be null or empty. ");
        } else if (!isValidEdgeId(id)) {
            errors.append("ID must contain only letters, numbers, dots, underscores, and hyphens. ");
        }

        // 2. Source obrigatório
        if (source == null || source.trim().isEmpty()) {
            errors.append("Source cannot be null or empty. ");
        }

        // 3. Target obrigatório
        if (target == null || target.trim().isEmpty()) {
            errors.append("Target cannot be null or empty. ");
        }

        // 4. Source ≠ Target (evitar self-loops desnecessários)
        if (source != null && source.equals(target)) {
            errors.append("Source and target cannot be the same (self-loop). ");
        }

        // 5. ConditionRef format (se presente)
        if (conditionRef != null && !conditionRef.trim().isEmpty() && !isValidConditionRef(conditionRef)) {
            errors.append("ConditionRef must follow format 'cd:conditionName'. ");
        }

        // 6. Prioridade válida
        if (priority < 0 || priority > 1000) {
            errors.append("Priority must be between 0 and 1000. ");
        }

        // 7. Se é default, não deve ter conditionRef
        if (isDefault && conditionRef != null && !conditionRef.trim().isEmpty()) {
            errors.append("Default edge cannot have conditionRef. ");
        }

        return errors.toString().trim();
    }

    /**
     * Valida formato do ID da edge
     */
    private boolean isValidEdgeId(String id) {
        return id != null && id.matches("^[a-zA-Z0-9._-]+$");
    }

    /**
     * Valida formato do conditionRef
     */
    private boolean isValidConditionRef(String conditionRef) {
        return conditionRef != null && conditionRef.matches("^cd:[a-zA-Z0-9_-]+$");
    }

    // =========================================================================
    // CONDIÇÕES CENTRALIZADAS (V2+ CORE FEATURE)
    // =========================================================================

    /**
     * 🆕 Associa condição centralizada à edge
     * SUBSTITUI: expression inline da V1/V2
     */
    public void setCentralizedCondition(String conditionId, String description) {
        if (conditionId == null || conditionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Condition ID cannot be null or empty");
        }

        // Formato padrão: cd:nomeCondicao
        if (!conditionId.startsWith("cd:")) {
            conditionId = "cd:" + conditionId;
        }

        this.conditionRef = conditionId;
        this.isDefault = false; // Se tem condição, não é default

        // Atualizar metadados
        this.metadata.hasCentralizedCondition = true;
        if (description != null) {
            this.metadata.conditionDescription = description;
        }
    }

    /**
     * Verifica se edge tem condição centralizada
     */
    public boolean hasCentralizedCondition() {
        return conditionRef != null && !conditionRef.trim().isEmpty();
    }

    /**
     * Remove condição (torna edge incondicional)
     */
    public void clearCondition() {
        this.conditionRef = null;
        this.metadata.hasCentralizedCondition = false;
        this.metadata.conditionDescription = null;
    }

    /**
     * Define como fluxo padrão (sem condições)
     */
    public void setAsDefaultFlow() {
        this.isDefault = true;
        this.conditionRef = null;
        this.priority = 999; // Baixa prioridade (executado por último)

        // Adicionar tag de default se não existir
        if (!pathTags.contains("default-flow")) {
            pathTags.add("default-flow");
        }
    }

    // =========================================================================
    // PATH TAGS E CLASSIFICAÇÃO
    // =========================================================================

    /**
     * Adiciona path tag
     */
    public void addPathTag(String tag) {
        if (tag != null && !tag.trim().isEmpty() && !pathTags.contains(tag)) {
            pathTags.add(tag);
        }
    }

    /**
     * Remove path tag
     */
    public void removePathTag(String tag) {
        pathTags.remove(tag);
    }

    /**
     * Verifica se tem path tag específico
     */
    public boolean hasPathTag(String tag) {
        return pathTags.contains(tag);
    }

    /**
     * Define como happy path
     */
    public void setAsHappyPath() {
        pathTags.clear();
        pathTags.add(PathTags.HAPPY_PATH);
        priority = 10; // Alta prioridade
    }

    /**
     * Define como path de erro
     */
    public void setAsErrorPath() {
        removePathTag(PathTags.HAPPY_PATH);
        addPathTag(PathTags.ALTERNATIVE_ERROR);
        priority = 50; // Prioridade média
    }

    /**
     * Verifica se é happy path
     */
    public boolean isHappyPath() {
        return PathTags.isHappyPath(pathTags);
    }

    /**
     * Verifica se é path de erro
     */
    public boolean isErrorPath() {
        return PathTags.isErrorPath(pathTags);
    }

    // =========================================================================
    // ANÁLISE E CLASSIFICAÇÃO
    // =========================================================================

    /**
     * Obtém tipo da edge baseado nas características
     */
    public EdgeType getEdgeType() {
        if (isDefault) {
            return EdgeType.DEFAULT_FLOW;
        } else if (hasCentralizedCondition()) {
            return EdgeType.CONDITIONAL_FLOW;
        } else {
            return EdgeType.SEQUENCE_FLOW;
        }
    }

    /**
     * Verifica se é edge crítica (happy path ou erro)
     */
    public boolean isCritical() {
        return isHappyPath() || isErrorPath();
    }

    /**
     * Obtém nível de complexidade da edge
     */
    public int getComplexityLevel() {
        int complexity = 1; // Base

        if (hasCentralizedCondition()) complexity += 2;
        if (isErrorPath()) complexity += 1;
        if (pathTags.size() > 2) complexity += 1;
        if (priority != 100) complexity += 1; // Prioridade customizada

        return Math.min(complexity, 5); // Max 5
    }

    /**
     * Gera descrição automática baseada nas características
     */
    public String generateAutoDescription() {
        StringBuilder desc = new StringBuilder();

        if (isDefault) {
            desc.append("Default flow");
        } else if (hasCentralizedCondition()) {
            desc.append("Conditional flow");
        } else {
            desc.append("Sequence flow");
        }

        if (isHappyPath()) {
            desc.append(" (happy path)");
        } else if (isErrorPath()) {
            desc.append(" (error handling)");
        }

        if (priority != 100) {
            desc.append(" [priority: ").append(priority).append("]");
        }

        return desc.toString();
    }

    // =========================================================================
    // CLONAGEM E COMPARAÇÃO
    // =========================================================================

    /**
     * Clona a edge para novo contexto
     */
    public ProcessEdgeV2Plus clone() {
        ProcessEdgeV2Plus cloned = new ProcessEdgeV2Plus();
        cloned.id = this.id;
        cloned.source = this.source;
        cloned.target = this.target;
        cloned.label = this.label;
        cloned.conditionRef = this.conditionRef;
        cloned.priority = this.priority;
        cloned.isDefault = this.isDefault;

        // Clonar collections
        cloned.pathTags = new ArrayList<>(this.pathTags);
        cloned.properties = new HashMap<>(this.properties);

        // Clonar metadata
        if (this.metadata != null) {
            cloned.metadata = new EdgeMetadata();
            cloned.metadata.sourceVersion = this.metadata.sourceVersion;
            cloned.metadata.hasLegacyCondition = this.metadata.hasLegacyCondition;
            cloned.metadata.legacyExpression = this.metadata.legacyExpression;
            cloned.metadata.hasCentralizedCondition = this.metadata.hasCentralizedCondition;
            cloned.metadata.migrationTimestamp = this.metadata.migrationTimestamp;
        }

        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessEdgeV2Plus that = (ProcessEdgeV2Plus) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Edge{id='%s', %s->%s, type=%s, hasCondition=%s, tags=%s}",
                id, source, target, getEdgeType(), hasCentralizedCondition(), pathTags);
    }

    // =========================================================================
    // GETTERS AND SETTERS
    // =========================================================================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getConditionRef() { return conditionRef; }
    public void setConditionRef(String conditionRef) { this.conditionRef = conditionRef; }

    public List<String> getPathTags() { return pathTags; }
    public void setPathTags(List<String> pathTags) {
        this.pathTags = pathTags != null ? pathTags : new ArrayList<>();
    }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties != null ? properties : new HashMap<>();
    }

    public EdgeMetadata getMetadata() { return metadata; }
    public void setMetadata(EdgeMetadata metadata) { this.metadata = metadata; }

    // =========================================================================
    // CLASSES DE APOIO
    // =========================================================================

    /**
     * Metadados de migração e roteamento da edge
     */
    public static class EdgeMetadata {
        public String sourceVersion;              // "1.0", "2.0"
        public boolean hasLegacyCondition;        // true se tinha expression inline
        public String legacyExpression;           // expressão original (para debug)
        public String legacyExpressionLanguage;   // "TWX-Expr", "JavaScript"
        public boolean hasCentralizedCondition;   // true se tem conditionRef
        public String conditionDescription;       // descrição da condição
        public String migrationTimestamp;         // timestamp da migração
        public int executionCount;                // quantas vezes foi executada (analytics)
        public double averageExecutionTime;       // tempo médio de execução (analytics)

        @Override
        public String toString() {
            return String.format("EdgeMetadata{source=%s, hasLegacy=%s, hasCentralized=%s, execCount=%d}",
                    sourceVersion, hasLegacyCondition, hasCentralizedCondition, executionCount);
        }
    }

    // =========================================================================
    // TESTE INLINE RÁPIDO
    // =========================================================================

    /**
     * Teste básico para validação durante desenvolvimento
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing ProcessEdgeV2Plus...");

        try {
            // Teste 1: Criação básica
            ProcessEdgeV2Plus edge1 = new ProcessEdgeV2Plus("edge1", "node1", "node2", "Success Path");
            System.out.println("✅ Basic creation: " + edge1.isValid());

            // Teste 2: Migração de legacy com condição
            ProcessEdgeV2Plus edge2 = ProcessEdgeV2Plus.fromLegacy(
                    "2027.abc123", "nodeA", "nodeB", "Validation Check",
                    "tw.local.validate.errors.size() == 0", "TWX-Expr"
            );
            System.out.println("✅ Legacy migration: " + edge2.getMetadata().hasLegacyCondition);

            // Teste 3: Condição centralizada
            edge1.setCentralizedCondition("validacao_ok", "Validação passou sem erros");
            System.out.println("✅ Centralized condition: " + edge1.hasCentralizedCondition());

            // Teste 4: Path tags
            edge1.setAsHappyPath();
            edge2.setAsErrorPath();
            System.out.println("✅ Path classification: happy=" + edge1.isHappyPath() +
                    ", error=" + edge2.isErrorPath());

            // Teste 5: Fluxo default
            ProcessEdgeV2Plus edge3 = new ProcessEdgeV2Plus("edge3", "gateway1", "node3", "Default");
            edge3.setAsDefaultFlow();
            System.out.println("✅ Default flow: " + edge3.isDefault());

            // Teste 6: Tipos automáticos
            System.out.println("✅ Edge types: " + edge1.getEdgeType() + ", " +
                    edge2.getEdgeType() + ", " + edge3.getEdgeType());

            // Teste 7: Complexidade
            System.out.println("✅ Complexity levels: " + edge1.getComplexityLevel() +
                    ", " + edge2.getComplexityLevel());

            // Teste 8: Descrição automática
            System.out.println("✅ Auto descriptions: ");
            System.out.println("   - " + edge1.generateAutoDescription());
            System.out.println("   - " + edge2.generateAutoDescription());
            System.out.println("   - " + edge3.generateAutoDescription());

            // Teste 9: Validação
            ProcessEdgeV2Plus invalidEdge = new ProcessEdgeV2Plus();
            invalidEdge.setId(""); // ID vazio
            System.out.println("✅ Invalid edge detection: " + !invalidEdge.isValid());

            // Teste 10: Clonagem
            ProcessEdgeV2Plus cloned = edge1.clone();
            System.out.println("✅ Cloning: " + cloned.equals(edge1));

            System.out.println("\n🎉 ProcessEdgeV2Plus: ALL TESTS PASSED!");
            System.out.println("Sample edge: " + edge1);

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}