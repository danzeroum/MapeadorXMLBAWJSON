package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.*;

/**
 * Process Lane V2+ - Raia/Lane Organizacional IA-Friendly
 *
 * MELHORA ESTRUTURA V1/V2:
 * ❌ V1/V2: lanes sem estrutura clara de participantes
 * ❌ V1/V2: responsabilidades implícitas
 * ❌ V1/V2: sem hierarquia organizacional
 * ✅ V2+: participantes explícitos e estruturados
 * ✅ V2+: roles e responsabilidades claras
 * ✅ V2+: hierarquia organizacional
 *
 * CARACTERÍSTICAS V2+:
 * ✅ Participantes estruturados (users, roles, systems)
 * ✅ Responsabilidades explícitas
 * ✅ Hierarquia organizacional (parent/child lanes)
 * ✅ Capacidades e restrições
 * ✅ Metadados organizacionais
 * ✅ Validação de assignment de nodes
 *
 * @version 2.1.0
 * @since V2+ IA-Friendly Migration
 */
@JsonPropertyOrder({
        "id", "name", "type", "participants", "responsibilities", "parent", "children", "properties", "metadata"
})
public class ProcessLaneV2Plus {

    /**
     * ID único da lane (estável entre versões)
     */
    @JsonProperty("id")
    private String id;

    /**
     * Nome display da lane
     */
    @JsonProperty("name")
    private String name;

    /**
     * Tipo da lane (organizacional, sistema, etc.)
     */
    @JsonProperty("type")
    private LaneType type;

    /**
     * 🆕 EXPANDIDO V2+: Participantes estruturados
     * Lista de users, roles, systems que operam nesta lane
     */
    @JsonProperty("participants")
    private List<LaneParticipant> participants;

    /**
     * 🆕 NOVO V2+: Responsabilidades explícitas
     * O que esta lane é responsável por fazer
     */
    @JsonProperty("responsibilities")
    private List<String> responsibilities;

    /**
     * 🆕 NOVO V2+: Lane pai (para hierarquia)
     * Para sub-lanes organizacionais
     */
    @JsonProperty("parent")
    private String parent;

    /**
     * 🆕 NOVO V2+: Lanes filhas
     * Para decomposição organizacional
     */
    @JsonProperty("children")
    private List<String> children;

    /**
     * Propriedades customizadas da lane
     */
    @JsonProperty("properties")
    private Map<String, Object> properties;

    /**
     * Metadados organizacionais
     */
    @JsonProperty("metadata")
    private LaneMetadata metadata;

    // =========================================================================
    // ENUMS E TIPOS
    // =========================================================================

    /**
     * Tipos de lane para classificação organizacional
     */
    public enum LaneType {
        USER("user", "Lane de usuário individual"),
        ROLE("role", "Lane de papel/função organizacional"),
        DEPARTMENT("department", "Lane departamental"),
        SYSTEM("system", "Lane de sistema automatizado"),
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

        /**
         * Converte string legada para LaneType
         */
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

    /**
     * Participante da lane com tipo e capacidades
     */
    public static class LaneParticipant {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("type")
        private ParticipantType type;

        @JsonProperty("email")
        private String email;

        @JsonProperty("capabilities")
        private List<String> capabilities;

        @JsonProperty("restrictions")
        private List<String> restrictions;

        public LaneParticipant() {
            this.capabilities = new ArrayList<>();
            this.restrictions = new ArrayList<>();
        }

        public LaneParticipant(String id, String name, ParticipantType type) {
            this();
            this.id = id;
            this.name = name;
            this.type = type;

        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public ParticipantType getType() { return type; }
        public void setType(ParticipantType type) { this.type = type; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public List<String> getCapabilities() { return capabilities; }
        public void setCapabilities(List<String> capabilities) {
            this.capabilities = capabilities != null ? capabilities : new ArrayList<>();
        }

        public List<String> getRestrictions() { return restrictions; }
        public void setRestrictions(List<String> restrictions) {
            this.restrictions = restrictions != null ? restrictions : new ArrayList<>();
        }

        @Override
        public String toString() {
            return String.format("Participant{id='%s', name='%s', type=%s}", id, name, type);
        }

        public void setRole(String laneRole) {
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
    // CONSTRUTORES
    // =========================================================================

    public ProcessLaneV2Plus() {
        this.type = LaneType.GENERIC;
        this.participants = new ArrayList<>();
        this.responsibilities = new ArrayList<>();
        this.children = new ArrayList<>();
        this.properties = new HashMap<>();
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
        lane.name = name != null ? name : "Unnamed Lane";
        lane.type = LaneType.fromString(typeStr);

        // Converter participantes legados
        if (legacyParticipants != null) {
            for (String participant : legacyParticipants) {
                lane.addParticipant(participant, ParticipantType.INDIVIDUAL_USER);
            }
        }

        // Definir responsabilidades básicas baseadas no tipo
        lane.responsibilities.addAll(getDefaultResponsibilities(lane.type));

        // Metadados de migração
        lane.metadata.sourceVersion = "1.0";
        lane.metadata.originalType = typeStr;
        lane.metadata.migrationTimestamp = java.time.Instant.now().toString();

        return lane;
    }

    /**
     * Normaliza ID de lane para formato consistente
     */
    private static String normalizeLaneId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return "lane-" + System.currentTimeMillis();
        }

        // Manter formato existente se válido
        if (id.matches("^[a-zA-Z0-9._-]+$")) {
            return id;
        }

        // Limpar caracteres inválidos
        return id.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Responsabilidades padrão por tipo de lane
     */
    private static List<String> getDefaultResponsibilities(LaneType type) {
        List<String> responsibilities = new ArrayList<>();

        switch (type) {
            case USER:
                responsibilities.add("Executar tarefas manuais");
                responsibilities.add("Tomar decisões de negócio");
                responsibilities.add("Validar informações");
                break;
            case ROLE:
                responsibilities.add("Executar atividades do papel");
                responsibilities.add("Supervisionar processos");
                responsibilities.add("Aprovar ou rejeitar solicitações");
                break;
            case DEPARTMENT:
                responsibilities.add("Coordenar atividades departamentais");
                responsibilities.add("Gerenciar recursos");
                responsibilities.add("Reportar resultados");
                break;
            case SYSTEM:
                responsibilities.add("Executar operações automatizadas");
                responsibilities.add("Processar dados");
                responsibilities.add("Integrar com outros sistemas");
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
    // GERENCIAMENTO DE PARTICIPANTES
    // =========================================================================

    /**
     * Adiciona participante à lane
     */
    public void addParticipant(String name, ParticipantType type) {
        String participantId = generateParticipantId(name);
        LaneParticipant participant = new LaneParticipant(participantId, name, type);
        participants.add(participant);
    }

    /**
     * Adiciona participante completo
     */
    public void addParticipant(LaneParticipant participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Participant cannot be null");
        }
        if (participant.getId() == null || participant.getId().trim().isEmpty()) {
            participant.setId(generateParticipantId(participant.getName()));
        }
        participants.add(participant);
    }

    /**
     * Remove participante por ID
     */
    public boolean removeParticipant(String participantId) {
        return participants.removeIf(p -> participantId.equals(p.getId()));
    }

    /**
     * Encontra participante por ID
     */
    public LaneParticipant findParticipant(String participantId) {
        return participants.stream()
                .filter(p -> participantId.equals(p.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gera ID único para participante
     */
    private String generateParticipantId(String name) {
        if (name == null) {
            return "participant-" + System.currentTimeMillis();
        }

        String normalized = name.toLowerCase()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");

        return "part_" + normalized + "_" + (participants.size() + 1);
    }

    // =========================================================================
    // HIERARQUIA ORGANIZACIONAL
    // =========================================================================

    /**
     * Define lane pai (hierarquia)
     */
    public void setParent(String parentId) {
        this.parent = parentId;
    }

    /**
     * Adiciona lane filha
     */
    public void addChild(String childId) {
        if (childId != null && !children.contains(childId)) {
            children.add(childId);
        }
    }

    /**
     * Remove lane filha
     */
    public boolean removeChild(String childId) {
        return children.remove(childId);
    }

    /**
     * Verifica se é lane raiz (sem pai)
     */
    public boolean isRootLane() {
        return parent == null || parent.trim().isEmpty();
    }

    /**
     * Verifica se é lane folha (sem filhas)
     */
    public boolean isLeafLane() {
        return children.isEmpty();
    }

    /**
     * Obtém nível na hierarquia (simulado)
     */
    public int getHierarchyLevel() {
        // Em implementação real, seria calculado baseado na árvore completa
        if (isRootLane()) return 0;
        return 1; // Simplificado
    }

    // =========================================================================
    // RESPONSABILIDADES E CAPACIDADES
    // =========================================================================

    /**
     * Adiciona responsabilidade
     */
    public void addResponsibility(String responsibility) {
        if (responsibility != null && !responsibility.trim().isEmpty() &&
                !responsibilities.contains(responsibility)) {
            responsibilities.add(responsibility);
        }
    }

    /**
     * Remove responsabilidade
     */
    public boolean removeResponsibility(String responsibility) {
        return responsibilities.remove(responsibility);
    }

    /**
     * Verifica se tem responsabilidade específica
     */
    public boolean hasResponsibility(String responsibility) {
        return responsibilities.contains(responsibility);
    }

    /**
     * Obtém todas as capacidades dos participantes
     */
    public Set<String> getAllCapabilities() {
        Set<String> allCapabilities = new HashSet<>();
        for (LaneParticipant participant : participants) {
            allCapabilities.addAll(participant.getCapabilities());
        }
        return allCapabilities;
    }

    /**
     * Verifica se lane tem capacidade específica
     */
    public boolean hasCapability(String capability) {
        return getAllCapabilities().contains(capability);
    }

    // =========================================================================
    // ANÁLISE E CLASSIFICAÇÃO
    // =========================================================================

    /**
     * Verifica se é lane automatizada (só sistemas)
     */
    public boolean isAutomated() {
        return type == LaneType.SYSTEM ||
                participants.stream().allMatch(p -> p.getType() == ParticipantType.SYSTEM_SERVICE ||
                        p.getType() == ParticipantType.BOT_AGENT);
    }

    /**
     * Verifica se é lane externa
     */
    public boolean isExternal() {
        return type == LaneType.EXTERNAL ||
                participants.stream().allMatch(p -> p.getType() == ParticipantType.EXTERNAL_ENTITY);
    }

    /**
     * Obtém contagem de participantes por tipo
     */
    public Map<ParticipantType, Long> getParticipantCountByType() {
        Map<ParticipantType, Long> counts = new HashMap<>();
        for (LaneParticipant participant : participants) {
            ParticipantType type = participant.getType();
            counts.put(type, counts.getOrDefault(type, 0L) + 1L);
        }
        return counts;
    }

    /**
     * Obtém nível de complexidade da lane
     */
    public int getComplexityLevel() {
        int complexity = 1; // Base

        complexity += participants.size(); // +1 por participante
        complexity += responsibilities.size() / 2; // +1 a cada 2 responsabilidades
        complexity += children.size(); // +1 por lane filha

        if (!isRootLane()) complexity += 1; // +1 se tem hierarquia
        if (isAutomated()) complexity -= 1; // -1 se é automatizada (mais simples)

        return Math.max(1, Math.min(complexity, 10)); // Entre 1 e 10
    }

    // =========================================================================
    // CLONAGEM E COMPARAÇÃO
    // =========================================================================

    /**
     * Clona a lane para novo contexto
     */
    public ProcessLaneV2Plus clone() {
        ProcessLaneV2Plus cloned = new ProcessLaneV2Plus();
        cloned.id = this.id;
        cloned.name = this.name;
        cloned.type = this.type;
        cloned.parent = this.parent;

        // Clonar collections
        cloned.participants = new ArrayList<>();
        for (LaneParticipant participant : this.participants) {
            LaneParticipant clonedParticipant = new LaneParticipant();
            clonedParticipant.setId(participant.getId());
            clonedParticipant.setName(participant.getName());
            clonedParticipant.setType(participant.getType());
            clonedParticipant.setEmail(participant.getEmail());
            clonedParticipant.setCapabilities(new ArrayList<>(participant.getCapabilities()));
            clonedParticipant.setRestrictions(new ArrayList<>(participant.getRestrictions()));
            cloned.participants.add(clonedParticipant);
        }

        cloned.responsibilities = new ArrayList<>(this.responsibilities);
        cloned.children = new ArrayList<>(this.children);
        cloned.properties = new HashMap<>(this.properties);

        // Clonar metadata
        if (this.metadata != null) {
            cloned.metadata = new LaneMetadata();
            cloned.metadata.sourceVersion = this.metadata.sourceVersion;
            cloned.metadata.originalType = this.metadata.originalType;
            cloned.metadata.migrationTimestamp = this.metadata.migrationTimestamp;
            cloned.metadata.organizationalUnit = this.metadata.organizationalUnit;
        }

        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessLaneV2Plus that = (ProcessLaneV2Plus) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Lane{id='%s', name='%s', type=%s, participants=%d, children=%d}",
                id, name, type, participants.size(), children.size());
    }

    // =========================================================================
    // GETTERS AND SETTERS
    // =========================================================================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LaneType getType() { return type; }
    public void setType(LaneType type) { this.type = type; }

    public List<LaneParticipant> getParticipants() { return participants; }
    public void setParticipants(List<LaneParticipant> participants) {
        this.participants = participants != null ? participants : new ArrayList<>();
    }

    public List<String> getResponsibilities() { return responsibilities; }
    public void setResponsibilities(List<String> responsibilities) {
        this.responsibilities = responsibilities != null ? responsibilities : new ArrayList<>();
    }

    public String getParent() { return parent; }

    public List<String> getChildren() { return children; }
    public void setChildren(List<String> children) {
        this.children = children != null ? children : new ArrayList<>();
    }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties != null ? properties : new HashMap<>();
    }

    public LaneMetadata getMetadata() { return metadata; }
    public void setMetadata(LaneMetadata metadata) { this.metadata = metadata; }

    // =========================================================================
    // CLASSES DE APOIO
    // =========================================================================

    /**
     * Metadados organizacionais da lane
     */
    public static class LaneMetadata {
        public String sourceVersion;            // "1.0", "2.0"
        public String originalType;             // tipo original na migração
        public String migrationTimestamp;       // timestamp da migração
        public String organizationalUnit;       // unidade organizacional
        public String costCenter;              // centro de custo
        public String location;                // localização física
        public Map<String, String> attributes; // atributos customizados

        public LaneMetadata() {
            this.attributes = new HashMap<>();
        }

        @Override
        public String toString() {
            return String.format("LaneMetadata{source=%s, orgUnit=%s, location=%s}",
                    sourceVersion, organizationalUnit, location);
        }
    }

    // =========================================================================
    // TESTE INLINE RÁPIDO
    // =========================================================================

    /**
     * Teste básico para validação durante desenvolvimento
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing ProcessLaneV2Plus...");

        try {
            // Teste 1: Criação básica
            ProcessLaneV2Plus lane1 = new ProcessLaneV2Plus("operacoes", "Operações", LaneType.DEPARTMENT);
            System.out.println("✅ Basic creation: " + lane1.isValid());

            // Teste 2: Migração de legacy
            ProcessLaneV2Plus lane2 = ProcessLaneV2Plus.fromLegacy(
                    "legacy_lane", "Aprovadores", "role",
                    Arrays.asList("supervisor1", "gerente1")
            );
            System.out.println("✅ Legacy migration: " + (lane2.getParticipants().size() == 2));

            // Teste 3: Adição de participantes
            lane1.addParticipant("João Silva", ParticipantType.INDIVIDUAL_USER);
            lane1.addParticipant("Sistema ERP", ParticipantType.SYSTEM_SERVICE);
            System.out.println("✅ Participant addition: " + (lane1.getParticipants().size() == 2));

            // Teste 4: Responsabilidades
            lane1.addResponsibility("Processar pedidos");
            lane1.addResponsibility("Validar documentos");
            System.out.println("✅ Responsibilities: " + (lane1.getResponsibilities().size() >= 2));

            // Teste 5: Hierarquia
            ProcessLaneV2Plus childLane = new ProcessLaneV2Plus("sub_ops", "Sub-Operações", LaneType.ROLE);
            lane1.addChild(childLane.getId());
            childLane.setParent(lane1.getId());
            System.out.println("✅ Hierarchy: parent=" + lane1.isRootLane() + ", child=" + childLane.isLeafLane());

            // Teste 6: Classificação
            System.out.println("✅ Classification: automated=" + lane1.isAutomated() +
                    ", external=" + lane1.isExternal());

            // Teste 7: Capacidades agregadas
            LaneParticipant skilled = new LaneParticipant("expert1", "Especialista", ParticipantType.INDIVIDUAL_USER);
            skilled.getCapabilities().add("validacao_avancada");
            skilled.getCapabilities().add("aprovacao_especial");
            lane1.addParticipant(skilled);
            System.out.println("✅ Capabilities: " + lane1.getAllCapabilities());

            // Teste 8: Complexidade
            System.out.println("✅ Complexity levels: " + lane1.getComplexityLevel() +
                    ", " + childLane.getComplexityLevel());

            // Teste 9: Contagem por tipo
            Map<ParticipantType, Long> counts = lane1.getParticipantCountByType();
            System.out.println("✅ Participant counts: " + counts);

            // Teste 10: Clonagem
            ProcessLaneV2Plus cloned = lane1.clone();
            System.out.println("✅ Cloning: " + cloned.equals(lane1));

            System.out.println("\n🎉 ProcessLaneV2Plus: ALL TESTS PASSED!");
            System.out.println("Sample lane: " + lane1);

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}