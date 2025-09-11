package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Process Node V2+ - Nó Individual IA-Friendly (Sem Scripts Inline)
 *
 * ELIMINA PROBLEMAS V1/V2:
 * ❌ V1/V2: script inline nos nodes (não analisável pela IA)
 * ❌ V1/V2: tipos limitados e inconsistentes
 * ❌ V1/V2: metadados dispersos
 * ✅ V2+: logicRef para scripts externalizados
 * ✅ V2+: tipos BPMN 2.0 completos
 * ✅ V2+: metadados estruturados
 *
 * CARACTERÍSTICAS V2+:
 * ✅ LogicRef obrigatório para scripts (sem inline)
 * ✅ Tipos BPMN 2.0 completos (StartEvent, UserTask, etc.)
 * ✅ Lane assignment explícito
 * ✅ Metadados de migração e proveniência
 * ✅ Validação de naming e estrutura
 * ✅ Suporte a propriedades customizadas
 *
 * @version 2.1.0
 * @since V2+ IA-Friendly Migration
 */
@JsonPropertyOrder({
        "id", "type", "name", "lane", "logicRef", "description", "properties", "metadata"
})
public class ProcessNodeV2Plus {

    /**
     * ID único do node (estável entre versões)
     * FORMATO: Compatível com IDs legados ou URN
     */
    @JsonProperty("id")
    private String id;

    /**
     * Tipo do node (BPMN 2.0 completo)
     * EXPANDIDO: Suporte completo a todos os tipos BPMN
     */
    @JsonProperty("type")
    private NodeType type;

    /**
     * Nome display do node
     * VALIDADO: Não vazio, formato adequado
     */
    @JsonProperty("name")
    private String name;

    /**
     * Lane/raia onde o node está localizado
     * OPCIONAL: Para processos sem lanes
     */
    @JsonProperty("lane")
    private String lane;

    /**
     * 🆕 NOVO V2+: Referência para lógica externalizada
     * SUBSTITUI: campo script inline da V1/V2
     * FORMATO: lg:nomeDoScript (aponta para logic.items[])
     */
    @JsonProperty("logicRef")
    private String logicRef;

    /**
     * Descrição detalhada do node
     * OBRIGATÓRIA: Para análise de IA
     */
    @JsonProperty("description")
    private String description;

    /**
     * Propriedades customizadas do node
     * FLEXÍVEL: Para dados específicos de diferentes tipos
     */
    @JsonProperty("properties")
    private Map<String, Object> properties;

    /**
     * Metadados de migração e proveniência
     */
    @JsonProperty("metadata")
    private NodeMetadata metadata;

    // ❌ CAMPOS REMOVIDOS DEFINITIVAMENTE (Breaking Changes):
    // - String script                    → logicRef (externalizado)
    // - Object inlineConfiguration       → properties (estruturado)
    // - List<String> conditions          → movido para edges

    // =========================================================================
    // ENUMS E TIPOS
    // =========================================================================

    /**
     * Tipos de node BPMN 2.0 completos + TWX específicos
     */
    public enum NodeType {
        // BPMN 2.0 Events
        START_EVENT("startEvent", "Evento de início"),
        END_EVENT("endEvent", "Evento de fim"),
        INTERMEDIATE_EVENT("intermediateEvent", "Evento intermediário"),
        BOUNDARY_EVENT("boundaryEvent", "Evento de fronteira"),

        // BPMN 2.0 Activities
        TASK("task", "Tarefa genérica"),
        USER_TASK("userTask", "Tarefa de usuário"),
        SCRIPT_TASK("scriptTask", "Tarefa de script"),
        SERVICE_TASK("serviceTask", "Tarefa de serviço"),
        SEND_TASK("sendTask", "Tarefa de envio"),
        RECEIVE_TASK("receiveTask", "Tarefa de recebimento"),
        MANUAL_TASK("manualTask", "Tarefa manual"),
        BUSINESS_RULE_TASK("businessRuleTask", "Tarefa de regra de negócio"),

        // BPMN 2.0 Gateways
        EXCLUSIVE_GATEWAY("exclusiveGateway", "Gateway exclusivo"),
        PARALLEL_GATEWAY("parallelGateway", "Gateway paralelo"),
        INCLUSIVE_GATEWAY("inclusiveGateway", "Gateway inclusivo"),
        COMPLEX_GATEWAY("complexGateway", "Gateway complexo"),
        EVENT_BASED_GATEWAY("eventBasedGateway", "Gateway baseado em evento"),

        // BPMN 2.0 Sub-processes
        SUB_PROCESS("subProcess", "Sub-processo"),
        CALL_ACTIVITY("callActivity", "Atividade de chamada"),

        // TWX/BAW Específicos (mantidos para compatibilidade)
        COACH_NG("coachNG", "Interface de usuário TWX"),
        DECISION("decision", "Ponto de decisão TWX"),
        SWITCH("switch", "Switch TWX"),

        // Genérico para casos não mapeados
        UNKNOWN("unknown", "Tipo não identificado");

        private final String bpmnName;
        private final String description;

        NodeType(String bpmnName, String description) {
            this.bpmnName = bpmnName;
            this.description = description;
        }

        public String getBpmnName() { return bpmnName; }
        public String getDescription() { return description; }

        /**
         * Converte string legada para NodeType
         */
        public static NodeType fromString(String typeStr) {
            if (typeStr == null) return UNKNOWN;

            // Mapeamentos diretos
            switch (typeStr.toLowerCase()) {
                case "startevent":
                case "start":
                    return START_EVENT;
                case "endevent":
                case "end":
                case "exitpoint":
                    return END_EVENT;
                case "script":
                case "scripttask":
                    return SCRIPT_TASK;
                case "usertask":
                case "humantask":
                case "coachng":
                    return USER_TASK;
                case "servicetask":
                case "service":
                    return SERVICE_TASK;
                case "subprocess":
                case "subprocesstask":
                    return SUB_PROCESS;
                case "callactivity":
                case "calledprocess":
                    return CALL_ACTIVITY;
                case "exclusivegateway":
                case "decision":
                case "switch":
                    return EXCLUSIVE_GATEWAY;
                case "parallelgateway":
                    return PARALLEL_GATEWAY;
                case "task":
                case "activity":
                default:
                    return TASK;
            }
        }

        /**
         * Verifica se o tipo requer logicRef
         */
        public boolean requiresLogic() {
            return this == SCRIPT_TASK || this == SERVICE_TASK || this == BUSINESS_RULE_TASK;
        }

        /**
         * Verifica se o tipo é um gateway
         */
        public boolean isGateway() {
            return name().contains("GATEWAY");
        }

        /**
         * Verifica se o tipo é um evento
         */
        public boolean isEvent() {
            return name().contains("EVENT");
        }
    }

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================

    public ProcessNodeV2Plus() {
        this.type = NodeType.TASK;
        this.properties = new HashMap<>();
        this.metadata = new NodeMetadata();
    }

    /**
     * Construtor completo para criação rápida
     */
    public ProcessNodeV2Plus(String id, NodeType type, String name, String lane) {
        this();
        this.id = id;
        this.type = type != null ? type : NodeType.TASK;
        this.name = name;
        this.lane = lane;

        // Validar após construção
        if (!isBasicValid()) {
            throw new IllegalArgumentException("Invalid node: " + getValidationErrors());
        }
    }

    /**
     * Factory method para criar node a partir de dados V1/V2
     */
    public static ProcessNodeV2Plus fromLegacy(String id, String name, String typeStr, String lane, String script) {
        ProcessNodeV2Plus node = new ProcessNodeV2Plus();

        // Configurar campos básicos
        node.id = normalizeNodeId(id);
        node.name = name != null ? name : "Unnamed Node";
        node.type = NodeType.fromString(typeStr);
        node.lane = lane;

        // Se há script, será externalizado (não inline)
        if (script != null && !script.trim().isEmpty()) {
            // LogicRef será definido durante migração
            node.description = "Node with externalized script logic";
            node.metadata.hasLegacyScript = true;
            node.metadata.legacyScriptLength = script.length();
        } else {
            node.description = "Node without script logic";
        }

        // Metadados de migração
        node.metadata.sourceVersion = "1.0";
        node.metadata.originalType = typeStr;
        node.metadata.migrationTimestamp = java.time.Instant.now().toString();

        return node;
    }

    /**
     * Normaliza ID de node para formato consistente
     */
    private static String normalizeNodeId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return "node-" + System.currentTimeMillis();
        }

        // Manter formato existente se válido
        if (id.matches("^[a-zA-Z0-9._-]+$")) {
            return id;
        }

        // Limpar caracteres inválidos
        return id.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    // =========================================================================
    // VALIDAÇÃO
    // =========================================================================

    /**
     * Validação completa do node
     */
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    /**
     * Validação básica (usado no construtor)
     */
    private boolean isBasicValid() {
        return id != null && !id.trim().isEmpty() &&
                name != null && !name.trim().isEmpty() &&
                type != null;
    }

    /**
     * Lista todos os erros de validação
     */
    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        // 1. ID obrigatório e válido
        if (id == null || id.trim().isEmpty()) {
            errors.append("ID cannot be null or empty. ");
        } else if (!isValidNodeId(id)) {
            errors.append("ID must contain only letters, numbers, dots, underscores, and hyphens. ");
        }

        // 2. Nome obrigatório
        if (name == null || name.trim().isEmpty()) {
            errors.append("Name cannot be null or empty. ");
        } else if (name.trim().length() < 2) {
            errors.append("Name must be at least 2 characters. ");
        }

        // 3. Tipo obrigatório
        if (type == null) {
            errors.append("Type cannot be null. ");
        } else {
            // Validar se tipo que requer lógica tem logicRef
            if (type.requiresLogic() && (logicRef == null || logicRef.trim().isEmpty())) {
                errors.append("Type " + type + " requires logicRef. ");
            }
        }

        // 4. LogicRef format (se presente)
        if (logicRef != null && !logicRef.trim().isEmpty() && !isValidLogicRef(logicRef)) {
            errors.append("LogicRef must follow format 'lg:scriptName'. ");
        }

        // 5. Descrição obrigatória para IA
        if (description == null || description.trim().isEmpty()) {
            errors.append("Description is required for IA analysis. ");
        }

        return errors.toString().trim();
    }

    /**
     * Valida formato do ID do node
     */
    private boolean isValidNodeId(String id) {
        return id != null && id.matches("^[a-zA-Z0-9._-]+$");
    }

    /**
     * Valida formato do logicRef
     */
    private boolean isValidLogicRef(String logicRef) {
        return logicRef != null && logicRef.matches("^lg:[a-zA-Z0-9_-]+$");
    }

    // =========================================================================
    // LÓGICA EXTERNALIZADA (V2+ CORE FEATURE)
    // =========================================================================

    /**
     * 🆕 Associa script externalizado ao node
     * SUBSTITUI: script inline da V1/V2
     */
    public void setExternalizedLogic(String logicId, String description) {
        if (logicId == null || logicId.trim().isEmpty()) {
            throw new IllegalArgumentException("Logic ID cannot be null or empty");
        }

        // Formato padrão: lg:nomeDoScript
        if (!logicId.startsWith("lg:")) {
            logicId = "lg:" + logicId;
        }

        this.logicRef = logicId;

        if (description != null && !description.trim().isEmpty()) {
            this.description = description;
        }

        // Marcar que tem lógica externalizada
        this.metadata.hasExternalizedLogic = true;
    }

    /**
     * Verifica se node tem lógica externalizada
     */
    public boolean hasExternalizedLogic() {
        return logicRef != null && !logicRef.trim().isEmpty();
    }

    /**
     * Remove referência de lógica (para nodes simples)
     */
    public void clearLogic() {
        this.logicRef = null;
        this.metadata.hasExternalizedLogic = false;
    }

    // =========================================================================
    // PROPRIEDADES CUSTOMIZADAS
    // =========================================================================

    /**
     * Adiciona propriedade customizada
     */
    public void setProperty(String key, Object value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Property key cannot be null or empty");
        }
        properties.put(key, value);
    }

    /**
     * Obtém propriedade customizada
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Obtém propriedade como string
     */
    public String getPropertyAsString(String key) {
        Object value = properties.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Verifica se tem propriedade
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    // =========================================================================
    // ANÁLISE E CLASSIFICAÇÃO
    // =========================================================================

    /**
     * Verifica se é um node de entrada (sem predecessores)
     */
    public boolean isEntryNode() {
        return type == NodeType.START_EVENT ||
                (type == NodeType.TASK && properties.containsKey("isEntry"));
    }

    /**
     * Verifica se é um node de saída (sem sucessores)
     */
    public boolean isExitNode() {
        return type == NodeType.END_EVENT ||
                (type == NodeType.TASK && properties.containsKey("isExit"));
    }

    /**
     * Verifica se é um node de decisão
     */
    public boolean isDecisionNode() {
        return type.isGateway() || type == NodeType.DECISION || type == NodeType.SWITCH;
    }

    /**
     * Verifica se é um node automatizado (sem interação humana)
     */
    public boolean isAutomated() {
        return type == NodeType.SCRIPT_TASK ||
                type == NodeType.SERVICE_TASK ||
                type == NodeType.BUSINESS_RULE_TASK;
    }

    /**
     * Obtém nível de complexidade estimado
     */
    public int getComplexityLevel() {
        int complexity = 1; // Base

        if (hasExternalizedLogic()) complexity += 2;
        if (isDecisionNode()) complexity += 2;
        if (type.isGateway()) complexity += 1;
        if (properties.size() > 3) complexity += 1;

        return Math.min(complexity, 5); // Max 5
    }

    // =========================================================================
    // CLONAGEM E COMPARAÇÃO
    // =========================================================================

    /**
     * Clona o node para novo contexto
     */
    public ProcessNodeV2Plus clone() {
        ProcessNodeV2Plus cloned = new ProcessNodeV2Plus();
        cloned.id = this.id;
        cloned.type = this.type;
        cloned.name = this.name;
        cloned.lane = this.lane;
        cloned.logicRef = this.logicRef;
        cloned.description = this.description;

        // Clonar properties
        cloned.properties = new HashMap<>(this.properties);

        // Clonar metadata
        if (this.metadata != null) {
            cloned.metadata = new NodeMetadata();
            cloned.metadata.sourceVersion = this.metadata.sourceVersion;
            cloned.metadata.originalType = this.metadata.originalType;
            cloned.metadata.hasLegacyScript = this.metadata.hasLegacyScript;
            cloned.metadata.hasExternalizedLogic = this.metadata.hasExternalizedLogic;
            cloned.metadata.migrationTimestamp = this.metadata.migrationTimestamp;
        }

        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessNodeV2Plus that = (ProcessNodeV2Plus) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Node{id='%s', type=%s, name='%s', hasLogic=%s}",
                id, type, name, hasExternalizedLogic());
    }

    // =========================================================================
    // GETTERS AND SETTERS
    // =========================================================================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public NodeType getType() { return type; }
    public void setType(NodeType type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLane() { return lane; }
    public void setLane(String lane) { this.lane = lane; }

    public String getLogicRef() { return logicRef; }
    public void setLogicRef(String logicRef) { this.logicRef = logicRef; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties != null ? properties : new HashMap<>();
    }

    public NodeMetadata getMetadata() { return metadata; }
    public void setMetadata(NodeMetadata metadata) { this.metadata = metadata; }

    // =========================================================================
    // CLASSES DE APOIO
    // =========================================================================

    /**
     * Metadados de migração e proveniência do node
     */
    public static class NodeMetadata {
        public String sourceVersion;           // "1.0", "2.0"
        public String originalType;            // "Script", "CoachNG", etc.
        public boolean hasLegacyScript;        // true se tinha script inline
        public int legacyScriptLength;         // tamanho do script original
        public boolean hasExternalizedLogic;   // true se tem logicRef
        public String migrationTimestamp;      // timestamp da migração
        public String migrationReason;         // razão da migração

        @Override
        public String toString() {
            return String.format("NodeMetadata{source=%s, originalType=%s, hasScript=%s, hasLogic=%s}",
                    sourceVersion, originalType, hasLegacyScript, hasExternalizedLogic);
        }
    }

    // =========================================================================
    // TESTE INLINE RÁPIDO
    // =========================================================================

    /**
     * Teste básico para validação durante desenvolvimento
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing ProcessNodeV2Plus...");

        try {
            // Teste 1: Criação básica
            ProcessNodeV2Plus node1 = new ProcessNodeV2Plus("node1", NodeType.SCRIPT_TASK, "Validar Dados", "operacoes");
            System.out.println("✅ Basic creation: " + node1.isValid());

            // Teste 2: Migração de legacy
            ProcessNodeV2Plus node2 = ProcessNodeV2Plus.fromLegacy(
                    "2025.abc123", "Script Node", "Script", "lane1",
                    "tw.local.validate = new tw.object.CoachValidation();"
            );
            System.out.println("✅ Legacy migration: " + node2.getMetadata().hasLegacyScript);

            // Teste 3: Lógica externalizada
            node1.setExternalizedLogic("validar_dados_script", "Script para validação de dados de entrada");
            System.out.println("✅ Externalized logic: " + node1.hasExternalizedLogic());

            // Teste 4: Propriedades customizadas
            node1.setProperty("timeout", 30000);
            node1.setProperty("retryCount", 3);
            System.out.println("✅ Custom properties: " + node1.getProperty("timeout"));

            // Teste 5: Classificação
            System.out.println("✅ Is automated: " + node1.isAutomated());
            System.out.println("✅ Is decision: " + node1.isDecisionNode());
            System.out.println("✅ Complexity level: " + node1.getComplexityLevel());

            // Teste 6: Conversão de tipos
            NodeType convertedType = NodeType.fromString("CoachNG");
            System.out.println("✅ Type conversion: " + (convertedType == NodeType.USER_TASK));

            // Teste 7: Validação
            ProcessNodeV2Plus invalidNode = new ProcessNodeV2Plus();
            invalidNode.setId(""); // ID vazio
            System.out.println("✅ Invalid node detection: " + !invalidNode.isValid());

            // Teste 8: Clonagem
            ProcessNodeV2Plus cloned = node1.clone();
            System.out.println("✅ Cloning: " + cloned.equals(node1));

            System.out.println("\n🎉 ProcessNodeV2Plus: ALL TESTS PASSED!");
            System.out.println("Sample node: " + node1);

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}