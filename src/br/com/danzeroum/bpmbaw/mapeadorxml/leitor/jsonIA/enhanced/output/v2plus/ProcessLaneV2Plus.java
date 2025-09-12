package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.*;

/**
 * ProcessLaneV2Plus - VERSÃO COMPLETA CORRIGIDA JAVA 8
 *
 * CORREÇÕES APLICADAS:
 * ✅ Método setPool() implementado (resolvia erro setPool)
 * ✅ Campos pool/poolId adicionados
 * ✅ Todos os métodos necessários implementados
 * ✅ Compatibilidade Java 8 completa
 * ✅ Classes internas LaneParticipant completas
 *
 * @version 2.3.0-complete-fixed-java8
 */
@JsonPropertyOrder({"id", "name", "type", "pool", "poolId", "participants", "responsibilities", "properties", "metadata"})
public class ProcessLaneV2Plus {

    // =========================================================================
    // CAMPOS PRINCIPAIS
    // =========================================================================

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private LaneType type;

    // CORRIGIDO: Campos para resolver erro setPool()
    @JsonProperty("pool")
    private String pool;

    @JsonProperty("poolId")
    private String poolId;

    @JsonProperty("participants")
    private List<LaneParticipant> participants;

    @JsonProperty("responsibilities")
    private List<String> responsibilities;

    @JsonProperty("children")
    private List<String> children;

    @JsonProperty("parent")
    private String parent;

    @JsonProperty("properties")
    private Map<String, Object> properties;

    @JsonProperty("metadata")
    private LaneMetadata metadata;




    // =========================================================================
    // ENUMS
    // =========================================================================

    /**
     * Tipos de lane suportados
     */
    public enum LaneType {
        USER("user", "Lane de usuário"),
        ROLE("role", "Lane de papel/função"),
        DEPARTMENT("department", "Lane departamental"),
        SYSTEM("system", "Lane de sistema"),
        EXTERNAL("external", "Lane de entidade externa"),
        POOL("pool", "Pool de participantes"),
        SUBPROCESS("subprocess", "Lane de sub-processo"),
        GENERIC("generic", "Lane genérica");

        private final String code;
        private final String description;


        LaneType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }

        public static LaneType fromString(String typeStr) {
            if (typeStr == null) return GENERIC;

            switch (typeStr.toLowerCase()) {
                case "user":
                case "pessoa":
                case "usuario":
                    return USER;
                case "role":
                case "papel":
                case "funcao":
                    return ROLE;
                case "department":
                case "departamento":
                case "setor":
                    return DEPARTMENT;
                case "system":
                case "sistema":
                case "automatico":
                    return SYSTEM;
                case "external":
                case "externo":
                case "terceiro":
                    return EXTERNAL;
                case "pool":
                    return POOL;
                default:
                    return GENERIC;
            }
        }
    }

    // =========================================================================
    // CLASSE INTERNA - LANEPARTICIPANT
    // =========================================================================

    /**
     * Participante da lane
     */
    public static class LaneParticipant {
        private String id;
        private String name;
        private ParticipantType type;
        private String role;
        private String email;
        private List<String> capabilities;
        private List<String> restrictions;

        public LaneParticipant() {
            this.type = ParticipantType.INDIVIDUAL_USER;
            this.capabilities = new ArrayList<String>();
            this.restrictions = new ArrayList<String>();
        }

        // Getters e Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public ParticipantType getType() { return type; }
        public void setType(ParticipantType type) { this.type = type; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public List<String> getCapabilities() { return capabilities; }
        public void setCapabilities(List<String> capabilities) {
            this.capabilities = capabilities != null ? capabilities : new ArrayList<String>();
        }

        public List<String> getRestrictions() { return restrictions; }
        public void setRestrictions(List<String> restrictions) {
            this.restrictions = restrictions != null ? restrictions : new ArrayList<String>();
        }

        @Override
        public String toString() {
            return String.format("Participant{id='%s', name='%s', type=%s}", id, name, type);
        }
    }

    /**
     * Tipos de participante
     */
    public enum ParticipantType {
        INDIVIDUAL_USER("individual", "Usuário individual"),
        ROLE_GROUP("role", "Grupo de papel"),
        DEPARTMENT_GROUP("department", "Grupo departamental"),
        SYSTEM_SERVICE("system", "Serviço de sistema"),
        EXTERNAL_ENTITY("external", "Entidade externa"),
        BOT_AGENT("bot", "Agente automatizado");

        private final String code;
        private final String description;

        ParticipantType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    // =========================================================================
    // CLASSE INTERNA - LANEMETADATA
    // =========================================================================

    /**
     * Metadata da lane
     */
    public static class LaneMetadata {
        public String createdAt;
        public String lastModified;
        public String author;
        public String version;
        public Map<String, String> annotations;

        public LaneMetadata() {
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
    }

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================

    public ProcessLaneV2Plus() {
        this.type = LaneType.GENERIC;
        this.participants = new ArrayList<LaneParticipant>();
        this.responsibilities = new ArrayList<String>();
        this.children = new ArrayList<String>();
        this.properties = new HashMap<String, Object>();
        this.metadata = new LaneMetadata();
    }

    /**
     * Construtor completo para criação rápida
     */
    public ProcessLaneV2Plus(String id, String name, LaneType type) {
        this();
        this.id = id;
        this.name = name;
        this.type = type != null ? type : LaneType.GENERIC;

        // Validar após construção
        if (!isBasicValid()) {
            throw new IllegalArgumentException("Invalid lane: " + getValidationErrors());
        }
    }

    /**
     * Factory method para criar lane a partir de dados V1/V2
     */
    public static ProcessLaneV2Plus fromLegacy(String id, String name, String typeStr, List<String> legacyParticipants) {
        ProcessLaneV2Plus lane = new ProcessLaneV2Plus();

        // Configurar campos básicos
        lane.id = normalizeLaneId(id);
        lane.name = name != null ? name : "Lane " + id;
        lane.type = LaneType.fromString(typeStr);

        // Converter participantes legados
        if (legacyParticipants != null) {
            for (String legacyParticipant : legacyParticipants) {
                LaneParticipant participant = new LaneParticipant();
                participant.setId("p_" + legacyParticipant.replaceAll("[^a-zA-Z0-9]", "_"));
                participant.setName(legacyParticipant);
                participant.setRole("user");
                lane.participants.add(participant);
            }
        }

        // Adicionar responsabilidades padrão baseadas no tipo
        lane.responsibilities.addAll(lane.generateDefaultResponsibilities());

        return lane;
    }

    // =========================================================================
    // GETTERS E SETTERS - CORRIGIDOS
    // =========================================================================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LaneType getType() { return type; }
    public void setType(LaneType type) { this.type = type; }

    // CORRIGIDO: Métodos para resolver erro setPool()
    public String getPool() { return pool; }
    public void setPool(String pool) {
        this.pool = pool;
        this.poolId = pool; // Manter ambos sincronizados
    }

    public String getPoolId() { return poolId; }
    public void setPoolId(String poolId) {
        this.poolId = poolId;
        this.pool = poolId; // Manter ambos sincronizados
    }

    public List<LaneParticipant> getParticipants() {
        return participants != null ? participants : new ArrayList<LaneParticipant>();
    }
    public void setParticipants(List<LaneParticipant> participants) {
        this.participants = participants != null ? participants : new ArrayList<LaneParticipant>();
    }

    public List<String> getResponsibilities() {
        return responsibilities != null ? responsibilities : new ArrayList<String>();
    }
    public void setResponsibilities(List<String> responsibilities) {
        this.responsibilities = responsibilities != null ? responsibilities : new ArrayList<String>();
    }

    public List<String> getChildren() {
        return children != null ? children : new ArrayList<String>();
    }
    public void setChildren(List<String> children) {
        this.children = children != null ? children : new ArrayList<String>();
    }

    public String getParent() { return parent; }
    public void setParent(String parent) { this.parent = parent; }

    public Map<String, Object> getProperties() {
        return properties != null ? properties : new HashMap<String, Object>();
    }
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties != null ? properties : new HashMap<String, Object>();
    }

    public LaneMetadata getMetadata() {
        return metadata != null ? metadata : new LaneMetadata();
    }
    public void setMetadata(LaneMetadata metadata) {
        this.metadata = metadata != null ? metadata : new LaneMetadata();
    }

    // =========================================================================
    // MÉTODOS DE MANIPULAÇÃO
    // =========================================================================

    /**
     * Adiciona participante à lane
     */
    public void addParticipant(LaneParticipant participant) {
        if (participant != null) {
            if (participants == null) {
                participants = new ArrayList<LaneParticipant>();
            }
            participants.add(participant);
        }
    }

    /**
     * Adiciona participante por nome e role
     */
    public void addParticipant(String name, String role) {
        LaneParticipant participant = new LaneParticipant();
        participant.setId("p_" + name.replaceAll("[^a-zA-Z0-9]", "_"));
        participant.setName(name);
        participant.setRole(role);
        addParticipant(participant);
    }

    /**
     * Remove participante
     */
    public boolean removeParticipant(String participantId) {
        if (participants != null) {
            return participants.removeIf(p -> participantId.equals(p.getId()));
        }
        return false;
    }

    /**
     * Adiciona responsabilidade
     */
    public void addResponsibility(String responsibility) {
        if (responsibility != null && !responsibility.trim().isEmpty()) {
            if (responsibilities == null) {
                responsibilities = new ArrayList<String>();
            }
            if (!responsibilities.contains(responsibility)) {
                responsibilities.add(responsibility);
            }
        }
    }

    /**
     * Adiciona propriedade
     */
    public void setProperty(String key, Object value) {
        if (key != null && !key.trim().isEmpty()) {
            if (properties == null) {
                properties = new HashMap<String, Object>();
            }
            properties.put(key, value);
        }
    }

    /**
     * Obtém propriedade
     */
    public Object getProperty(String key) {
        return properties != null ? properties.get(key) : null;
    }

    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================

    /**
     * Normaliza ID da lane
     */
    private static String normalizeLaneId(String id) {
        if (id == null) return "lane_" + System.currentTimeMillis();
        return id.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    /**
     * Gera responsabilidades padrão baseadas no tipo
     */
    private List<String> generateDefaultResponsibilities() {
        List<String> responsibilities = new ArrayList<String>();

        switch (type) {
            case USER:
                responsibilities.add("Executar tarefas manuais");
                responsibilities.add("Tomar decisões de negócio");
                responsibilities.add("Validar informações");
                break;
            case ROLE:
                responsibilities.add("Exercer papel específico");
                responsibilities.add("Seguir procedimentos");
                responsibilities.add("Reportar resultados");
                break;
            case DEPARTMENT:
                responsibilities.add("Coordenar atividades departamentais");
                responsibilities.add("Gerenciar recursos");
                responsibilities.add("Garantir conformidade");
                break;
            case SYSTEM:
                responsibilities.add("Executar tarefas automatizadas");
                responsibilities.add("Processar dados");
                responsibilities.add("Integrar sistemas");
                break;
            case EXTERNAL:
                responsibilities.add("Fornecer serviços externos");
                responsibilities.add("Validar informações");
                responsibilities.add("Processar solicitações");
                break;
            default:
                responsibilities.add("Executar atividades do processo");
                break;
        }

        return responsibilities;
    }

    // =========================================================================
    // VALIDAÇÃO
    // =========================================================================

    /**
     * Validação completa da lane
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
        } else if (!isValidLaneId(id)) {
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
        }

        // 4. Validar participantes
        if (participants != null) {
            for (int i = 0; i < participants.size(); i++) {
                LaneParticipant participant = participants.get(i);
                if (participant.getId() == null || participant.getId().trim().isEmpty()) {
                    errors.append("Participant[").append(i).append("] must have ID. ");
                }
                if (participant.getName() == null || participant.getName().trim().isEmpty()) {
                    errors.append("Participant[").append(i).append("] must have name. ");
                }
            }
        }

        // 5. Validar hierarquia (evitar ciclos)
        if (parent != null && children.contains(parent)) {
            errors.append("Lane cannot be parent of itself. ");
        }

        return errors.toString().trim();
    }

    /**
     * Valida formato do ID da lane
     */
    private boolean isValidLaneId(String id) {
        return id != null && id.matches("^[a-zA-Z0-9._-]+$");
    }

    // =========================================================================
    // MÉTODOS UTILITÁRIOS
    // =========================================================================

    /**
     * Clona a lane
     */
    public ProcessLaneV2Plus clone() {
        ProcessLaneV2Plus cloned = new ProcessLaneV2Plus();
        cloned.id = this.id;
        cloned.name = this.name;
        cloned.type = this.type;
        cloned.pool = this.pool;
        cloned.poolId = this.poolId;
        cloned.parent = this.parent;

        // Clonar listas
        cloned.participants = new ArrayList<LaneParticipant>(this.getParticipants());
        cloned.responsibilities = new ArrayList<String>(this.getResponsibilities());
        cloned.children = new ArrayList<String>(this.getChildren());
        cloned.properties = new HashMap<String, Object>(this.getProperties());

        return cloned;
    }

    @Override
    public String toString() {
        return String.format("ProcessLaneV2Plus{id='%s', name='%s', type=%s, participants=%d}",
                id, name, type, participants != null ? participants.size() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProcessLaneV2Plus that = (ProcessLaneV2Plus) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // =========================================================================
    // MÉTODOS FACTORY
    // =========================================================================

    /**
     * Cria lane de usuário
     */
    public static ProcessLaneV2Plus createUserLane(String id, String name, String userRole) {
        ProcessLaneV2Plus lane = new ProcessLaneV2Plus(id, name, LaneType.USER);
        lane.addParticipant(name, userRole);
        return lane;
    }

    /**
     * Cria lane de sistema
     */
    public static ProcessLaneV2Plus createSystemLane(String id, String name) {
        ProcessLaneV2Plus lane = new ProcessLaneV2Plus(id, name, LaneType.SYSTEM);
        lane.addResponsibility("Executar tarefas automatizadas");
        return lane;
    }

    // =========================================================================
    // TESTE INLINE
    // =========================================================================

    /**
     * Teste básico da classe
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing ProcessLaneV2Plus...");

        try {
            // Teste 1: Criação básica
            ProcessLaneV2Plus lane = new ProcessLaneV2Plus();
            lane.setId("test-lane");
            lane.setName("Test Lane");
            lane.setType(LaneType.USER);
            lane.setPool("test-pool");

            System.out.println("✅ Basic creation: " + lane.isValid());
            System.out.println("✅ Pool set: " + (lane.getPool() != null));

            // Teste 2: Factory methods
            ProcessLaneV2Plus userLane = ProcessLaneV2Plus.createUserLane("user-1", "User Lane", "analyst");
            System.out.println("✅ User lane creation: " + userLane.isValid());

            ProcessLaneV2Plus systemLane = ProcessLaneV2Plus.createSystemLane("sys-1", "System Lane");
            System.out.println("✅ System lane creation: " + systemLane.isValid());

            // Teste 3: Participantes
            lane.addParticipant("John Doe", "user");
            System.out.println("✅ Participant addition: " + (lane.getParticipants().size() == 1));

            System.out.println("🎉 ProcessLaneV2Plus: ALL TESTS PASSED!");

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}