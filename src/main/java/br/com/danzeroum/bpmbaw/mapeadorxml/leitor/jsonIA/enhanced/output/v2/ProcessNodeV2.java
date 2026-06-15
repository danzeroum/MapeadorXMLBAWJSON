package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.Map;

public class ProcessNodeV2 {
    private String id; // URN format
    private String name;
    private NodeType type;
    private String description;
    private String logicRef;
    private String lane;
    private Map<String, Object> properties;
    private NodeComplexity complexity;

    public ProcessNodeV2() {
    }

    // Factory method
    public static ProcessNodeV2 create(String id, String name, String typeStr) {
        ProcessNodeV2 node = new ProcessNodeV2();
        node.setId(id);
        node.setName(name);
        node.setType(NodeType.fromString(typeStr));
        return node;
    }

    // Validação
    public void validate() {
        if (!isValidStableId(id)) {
            throw new IllegalArgumentException("Invalid node ID format");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Node name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Node type cannot be null");
        }
    }

    private boolean isValidStableId(String id) {
        // Versão mais flexível que aceita vários formatos de ID
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        // Aceitar vários formatos:
        // - URN format: urn:pv:node:name:version
        // - UUID format: 25.xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
        // - Formato IBM BAW: números com pontos e hífens
        // - IDs simples: qualquer string não vazia

        return id.matches("^(urn:pv:[a-z]+:[a-z0-9-]+:[0-9]+|[0-9]{4}\\.[a-f0-9-]{36}|[0-9]+\\.[a-f0-9-]+|[a-zA-Z0-9_.-]+)$");
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        // Versão mais flexível - apenas verificar se não é nulo/vazio
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Node ID cannot be null or empty");
        }

        // Se não é um formato válido, criar um ID compatível
        if (!isValidStableId(id)) {
            // Converter ID para formato compatível
            String cleanId = id.replaceAll("[^a-zA-Z0-9.-]", "-");
            this.id = cleanId;
            System.out.println("⚠️ Converted invalid ID '" + id + "' to '" + cleanId + "'");
        } else {
            this.id = id;
        }
    }

    public static ProcessNodeV2 createWithFlexibleId(String id, String name, String typeStr) {
        ProcessNodeV2 node = new ProcessNodeV2();

        // Definir ID primeiro sem validação
        if (id == null || id.trim().isEmpty()) {
            node.id = "node-" + System.currentTimeMillis();
        } else {
            node.id = id.replaceAll("[^a-zA-Z0-9.-]", "-");
        }

        node.setName(name);

        // Converter tipo com fallback
        try {
            node.setType(NodeType.fromString(typeStr));
        } catch (Exception e) {
            node.setType(NodeType.TASK); // Tipo padrão
        }

        return node;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    // String getter para compatibilidade JSON
    public String getTypeString() {
        return type != null ? type.toString() : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogicRef() {
        return logicRef;
    }

    public void setLogicRef(String logicRef) {
        this.logicRef = logicRef;
    }

    public String getLane() {
        return lane;
    }

    public void setLane(String lane) {
        this.lane = lane;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public NodeComplexity getComplexity() {
        return complexity;
    }

    public void setComplexity(NodeComplexity complexity) {
        this.complexity = complexity;
    }

    // Enum para tipos de node
    public enum NodeType {
        TASK("Task"),
        SUB_PROCESS("SubProcess"),
        SCRIPT_TASK("ScriptTask"),
        SERVICE_TASK("ServiceTask"),
        USER_TASK("UserTask"),
        GATEWAY("Gateway"),
        START_EVENT("StartEvent"),
        END_EVENT("EndEvent"),
        INTERMEDIATE_EVENT("IntermediateEvent");

        private final String value;

        NodeType(String value) {
            this.value = value;
        }

        public static NodeType fromString(String value) {
            for (NodeType type : values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return TASK; // Default fallback
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static class NodeComplexity {
        private int cyclomaticComplexity;
        private int depth;
        private int fanIn;
        private int fanOut;

        // Getters and setters
        public int getCyclomaticComplexity() {
            return cyclomaticComplexity;
        }

        public void setCyclomaticComplexity(int cyclomaticComplexity) {
            this.cyclomaticComplexity = cyclomaticComplexity;
        }

        public int getDepth() {
            return depth;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }

        public int getFanIn() {
            return fanIn;
        }

        public void setFanIn(int fanIn) {
            this.fanIn = fanIn;
        }

        public int getFanOut() {
            return fanOut;
        }

        public void setFanOut(int fanOut) {
            this.fanOut = fanOut;
        }
    }
}
