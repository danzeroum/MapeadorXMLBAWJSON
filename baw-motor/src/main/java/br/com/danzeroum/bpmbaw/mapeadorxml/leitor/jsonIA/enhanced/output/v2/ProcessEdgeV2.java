package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.Map;

public class ProcessEdgeV2 {
    private String id;
    private String source;
    private String target;
    private String label;
    private String guard; // Condição de transição
    private Double probability; // 0.0 a 1.0
    private String conditionRef;
    private String expectedDuration; // ISO 8601 duration
    private EdgeType type;
    private Map<String, Object> properties;

    public ProcessEdgeV2() {
    }

    // Factory method
    public static ProcessEdgeV2 create(String id, String source, String target) {
        ProcessEdgeV2 edge = new ProcessEdgeV2();
        edge.setId(id);
        edge.setSource(source);
        edge.setTarget(target);
        edge.setType(EdgeType.SEQUENCE_FLOW);
        return edge;
    }

    // Validação
    public void validate() {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Edge ID cannot be null or empty");
        }
        if (source == null || target == null) {
            throw new IllegalArgumentException("Edge source and target cannot be null");
        }
        if (source.equals(target)) {
            throw new IllegalArgumentException("Self-loops not allowed");
        }
        if (probability != null && (probability < 0.0 || probability > 1.0)) {
            throw new IllegalArgumentException("Probability must be between 0.0 and 1.0");
        }
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getGuard() {
        return guard;
    }

    public void setGuard(String guard) {
        this.guard = guard;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        if (probability != null && (probability < 0.0 || probability > 1.0)) {
            throw new IllegalArgumentException("Probability must be between 0.0 and 1.0");
        }
        this.probability = probability;
    }

    public String getConditionRef() {
        return conditionRef;
    }

    public void setConditionRef(String conditionRef) {
        this.conditionRef = conditionRef;
    }

    public String getExpectedDuration() {
        return expectedDuration;
    }

    public void setExpectedDuration(String expectedDuration) {
        this.expectedDuration = expectedDuration;
    }

    public EdgeType getType() {
        return type;
    }

    public void setType(EdgeType type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public enum EdgeType {
        SEQUENCE_FLOW("SequenceFlow"),
        MESSAGE_FLOW("MessageFlow"),
        ASSOCIATION("Association"),
        DATA_FLOW("DataFlow");

        private final String value;

        EdgeType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
