package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * ProcessNodeV2Plus - Representa um nó no grafo de processo V2Plus
 *
 * Esta classe representa todos os tipos de nós possíveis em um processo BPMN/BAW:
 * - Eventos (Start, End, Intermediate)
 * - Atividades (Task, SubProcess, UserTask, ScriptTask, etc.)
 * - Gateways (Exclusive, Parallel, Inclusive, etc.)
 * - Artefatos (DataObject, Annotation, etc.)
 *
 * @version 3.0.0 - Versão completa Java 8
 * @author Enhanced BAW Analysis System
 */
@JsonPropertyOrder({
        "id", "type", "name", "description", "lane", "pool",
        "x", "y", "width", "height", "properties", "metadata",
        "incoming", "outgoing", "attachedTo", "boundary"
})
public class ProcessNodeV2Plus {

    // =========================================================================
    // ENUMS
    // =========================================================================

    /**
     * Tipos de nós suportados
     */
    public enum NodeType {
        // Eventos
        START_EVENT("StartEvent"),
        END_EVENT("EndEvent"),
        INTERMEDIATE_EVENT("IntermediateEvent"),
        BOUNDARY_EVENT("BoundaryEvent"),

        // Tarefas
        TASK("Task"),
        USER_TASK("UserTask"),
        SCRIPT("ScriptTask"),
        SERVICE_TASK("ServiceTask"),
        SEND_TASK("SendTask"),
        RECEIVE_TASK("ReceiveTask"),
        MANUAL_TASK("ManualTask"),
        BUSINESS_RULE_TASK("BusinessRuleTask"),
        SYSTEM_TASK("SystemTask"),

        // Subprocessos
        SUBPROCESS("SubProcess"),
        CALL_ACTIVITY("CallActivity"),
        AD_HOC_SUBPROCESS("AdHocSubProcess"),
        TRANSACTION("Transaction"),
        EVENT_SUBPROCESS("EventSubProcess"),

        // Gateways
        GATEWAY("ExclusiveGateway"),
        EXCLUSIVE_GATEWAY("ExclusiveGateway"),
        PARALLEL_GATEWAY("ParallelGateway"),
        INCLUSIVE_GATEWAY("InclusiveGateway"),
        COMPLEX_GATEWAY("ComplexGateway"),
        EVENT_BASED_GATEWAY("EventBasedGateway"),

        // Artefatos
        DATA_OBJECT("DataObject"),
        DATA_STORE("DataStore"),
        ANNOTATION("TextAnnotation"),
        GROUP("Group"),

        // Pontos de controle
        ENTRY_POINT("EntryPoint"),
        EXIT_POINT("ExitPoint"),
        STAY_ON_PAGE("StayOnPage"),

        // Desconhecido
        UNKNOWN("Unknown");

        private final String bpmnName;

        NodeType(String bpmnName) {
            this.bpmnName = bpmnName;
        }

        public String getBpmnName() {
            return bpmnName;
        }

        /**
         * Converte string para enum
         */
        public static NodeType fromString(String type) {
            if (type == null) return UNKNOWN;

            // Tentar match direto
            for (NodeType nt : values()) {
                if (nt.name().equalsIgnoreCase(type) ||
                        nt.bpmnName.equalsIgnoreCase(type)) {
                    return nt;
                }
            }

            // Tentar match parcial
            String upper = type.toUpperCase();
            if (upper.contains("START")) return START_EVENT;
            if (upper.contains("END")) return END_EVENT;
            if (upper.contains("USER")) return USER_TASK;
            if (upper.contains("SCRIPT")) return SCRIPT;
            if (upper.contains("SUBPROCESS")) return SUBPROCESS;
            if (upper.contains("GATEWAY")) return GATEWAY;

            return UNKNOWN;
        }
    }

    // =========================================================================
    // CAMPOS PRINCIPAIS
    // =========================================================================

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private NodeType type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    // =========================================================================
    // LOCALIZAÇÃO
    // =========================================================================

    @JsonProperty("lane")
    private String lane;

    @JsonProperty("pool")
    private String pool;

    @JsonProperty("x")
    private Double x;

    @JsonProperty("y")
    private Double y;

    @JsonProperty("width")
    private Double width;

    @JsonProperty("height")
    private Double height;

    // =========================================================================
    // CONEXÕES
    // =========================================================================

    @JsonProperty("incoming")
    private List<String> incoming;

    @JsonProperty("outgoing")
    private List<String> outgoing;

    @JsonProperty("attachedTo")
    private String attachedTo;

    @JsonProperty("boundary")
    private Boolean boundary;

    // =========================================================================
    // PROPRIEDADES E METADATA
    // =========================================================================

    @JsonProperty("properties")
    private Map<String, Object> properties;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    @JsonProperty("calledArtifactId")
    private String calledArtifactId; // ID do subprocesso chamado

    @JsonProperty("coachId")
    private String coachId; // ID do Coach associado

    @JsonProperty("parameterMapping")
    private ParameterMappingV2Plus parameterMapping; // Mapeamento de variáveis


    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    /**
     * Constructor padrão
     */
    public ProcessNodeV2Plus() {
        this.type = NodeType.UNKNOWN;
        this.incoming = new ArrayList<String>();
        this.outgoing = new ArrayList<String>();
        this.properties = new HashMap<String, Object>();
        this.metadata = new HashMap<String, Object>();
        this.boundary = false;
    }

    /**
     * Constructor com id e tipo
     */
    public ProcessNodeV2Plus(String id, NodeType type) {
        this();
        this.id = id;
        this.type = type;
    }

    /**
     * Constructor completo
     */
    public ProcessNodeV2Plus(String id, NodeType type, String name, String lane) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.lane = lane;
        this.parameterMapping = new ParameterMappingV2Plus(); // Inicializa o mapeamento
    }

    // =========================================================================
    // GETTERS E SETTERS
    // =========================================================================
    // Adicione getters e setters para os novos campos
    public String getCalledArtifactId() {
        return calledArtifactId;
    }

    public void setCalledArtifactId(String calledArtifactId) {
        this.calledArtifactId = calledArtifactId;
    }

    public String getCoachId() {
        return coachId;
    }

    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }

    public ParameterMappingV2Plus getParameterMapping() {
        return parameterMapping;
    }

    public void setParameterMapping(ParameterMappingV2Plus parameterMapping) {
        this.parameterMapping = parameterMapping;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLane() {
        return lane;
    }

    public void setLane(String lane) {
        this.lane = lane;
    }

    public String getPool() {
        return pool;
    }

    public void setPool(String pool) {
        this.pool = pool;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public List<String> getIncoming() {
        if (incoming == null) {
            incoming = new ArrayList<String>();
        }
        return incoming;
    }

    public void setIncoming(List<String> incoming) {
        this.incoming = incoming;
    }

    public List<String> getOutgoing() {
        if (outgoing == null) {
            outgoing = new ArrayList<String>();
        }
        return outgoing;
    }

    public void setOutgoing(List<String> outgoing) {
        this.outgoing = outgoing;
    }

    public String getAttachedTo() {
        return attachedTo;
    }

    public void setAttachedTo(String attachedTo) {
        this.attachedTo = attachedTo;
    }

    public Boolean getBoundary() {
        return boundary;
    }

    public void setBoundary(Boolean boundary) {
        this.boundary = boundary;
    }

    public Map<String, Object> getProperties() {
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, Object> getMetadata() {
        if (metadata == null) {
            metadata = new HashMap<String, Object>();
        }
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    // =========================================================================
    // MÉTODOS DE NEGÓCIO
    // =========================================================================

    /**
     * Adiciona conexão de entrada
     */
    public void addIncoming(String edgeId) {
        if (edgeId != null && !edgeId.isEmpty()) {
            getIncoming().add(edgeId);
        }
    }

    /**
     * Adiciona conexão de saída
     */
    public void addOutgoing(String edgeId) {
        if (edgeId != null && !edgeId.isEmpty()) {
            getOutgoing().add(edgeId);
        }
    }

    /**
     * Adiciona propriedade
     */
    public void addProperty(String key, Object value) {
        getProperties().put(key, value);
    }

    /**
     * Obtém propriedade
     */
    public Object getProperty(String key) {
        return getProperties().get(key);
    }

    /**
     * Adiciona metadata
     */
    public void addMetadata(String key, Object value) {
        getMetadata().put(key, value);
    }

    /**
     * Verifica se é um evento
     */
    public boolean isEvent() {
        return type == NodeType.START_EVENT ||
                type == NodeType.END_EVENT ||
                type == NodeType.INTERMEDIATE_EVENT ||
                type == NodeType.BOUNDARY_EVENT;
    }

    /**
     * Verifica se é uma tarefa
     */
    public boolean isTask() {
        return type.name().contains("TASK") || type == NodeType.SCRIPT;
    }

    /**
     * Verifica se é um gateway
     */
    public boolean isGateway() {
        return type.name().contains("GATEWAY");
    }

    /**
     * Verifica se é um subprocess
     */
    public boolean isSubprocess() {
        return type == NodeType.SUBPROCESS ||
                type == NodeType.CALL_ACTIVITY ||
                type == NodeType.AD_HOC_SUBPROCESS ||
                type == NodeType.TRANSACTION ||
                type == NodeType.EVENT_SUBPROCESS;
    }

    /**
     * Verifica se é um nó de início
     */
    public boolean isStartNode() {
        return type == NodeType.START_EVENT || type == NodeType.ENTRY_POINT;
    }

    /**
     * Verifica se é um nó de fim
     */
    public boolean isEndNode() {
        return type == NodeType.END_EVENT || type == NodeType.EXIT_POINT;
    }

    /**
     * Calcula o centro do nó
     */
    public double getCenterX() {
        if (x != null && width != null) {
            return x + (width / 2);
        }
        return x != null ? x : 0;
    }

    public double getCenterY() {
        if (y != null && height != null) {
            return y + (height / 2);
        }
        return y != null ? y : 0;
    }

    /**
     * Define posição do nó
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Define dimensões do nó
     */
    public void setDimensions(double width, double height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Obtém tipo canônico como string
     */
    public String getCanonicalType() {
        if (properties != null && properties.containsKey("canonicalType")) {
            return properties.get("canonicalType").toString();
        }
        return type.getBpmnName();
    }

    /**
     * Define tipo canônico
     */
    public void setCanonicalType(String canonicalType) {
        addProperty("canonicalType", canonicalType);
    }

    // =========================================================================
    // VALIDAÇÃO
    // =========================================================================

    /**
     * Valida se o nó está configurado corretamente
     */
    public boolean validate() {
        // ID é obrigatório
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        // Tipo não pode ser UNKNOWN
        if (type == null || type == NodeType.UNKNOWN) {
            return false;
        }

        // Nome é recomendado mas não obrigatório
        if (name == null || name.trim().isEmpty()) {
            // Gerar nome padrão baseado no tipo
            name = type.getBpmnName() + "_" + id;
        }

        return true;
    }

    /**
     * Obtém erros de validação
     */
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<String>();

        if (id == null || id.trim().isEmpty()) {
            errors.add("Node ID is required");
        }

        if (type == null || type == NodeType.UNKNOWN) {
            errors.add("Node type is invalid or unknown");
        }

        // Validações específicas por tipo
        if (isGateway() && (getOutgoing().size() < 2)) {
            errors.add("Gateway must have at least 2 outgoing edges");
        }

        if (isStartNode() && !getIncoming().isEmpty()) {
            errors.add("Start node should not have incoming edges");
        }

        if (isEndNode() && !getOutgoing().isEmpty()) {
            errors.add("End node should not have outgoing edges");
        }

        return errors;
    }

    // =========================================================================
    // CLONAGEM
    // =========================================================================

    /**
     * Clona o nó
     */
    public ProcessNodeV2Plus clone() {
        ProcessNodeV2Plus clone = new ProcessNodeV2Plus();
        clone.id = this.id;
        clone.type = this.type;
        clone.name = this.name;
        clone.description = this.description;
        clone.lane = this.lane;
        clone.pool = this.pool;
        clone.x = this.x;
        clone.y = this.y;
        clone.width = this.width;
        clone.height = this.height;
        clone.attachedTo = this.attachedTo;
        clone.boundary = this.boundary;

        if (this.incoming != null) {
            clone.incoming = new ArrayList<String>(this.incoming);
        }
        if (this.outgoing != null) {
            clone.outgoing = new ArrayList<String>(this.outgoing);
        }
        if (this.properties != null) {
            clone.properties = new HashMap<String, Object>(this.properties);
        }
        if (this.metadata != null) {
            clone.metadata = new HashMap<String, Object>(this.metadata);
        }

        return clone;
    }

    // =========================================================================
    // MÉTODOS UTILITÁRIOS
    // =========================================================================

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProcessNodeV2Plus{");
        sb.append("id='").append(id).append('\'');
        sb.append(", type=").append(type);
        sb.append(", name='").append(name).append('\'');
        if (lane != null) sb.append(", lane='").append(lane).append('\'');
        if (x != null && y != null) {
            sb.append(", pos=(").append(x).append(",").append(y).append(")");
        }
        sb.append(", in=").append(getIncoming().size());
        sb.append(", out=").append(getOutgoing().size());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessNodeV2Plus that = (ProcessNodeV2Plus) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}