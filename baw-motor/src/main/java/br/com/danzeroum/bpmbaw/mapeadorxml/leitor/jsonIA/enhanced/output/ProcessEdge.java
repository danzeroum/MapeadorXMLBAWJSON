package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output;

/**
 * Process edge
 */
class ProcessEdge {
    private String id;
    private String source;
    private String target;
    private String label;
    private String conditionRef;

    // Getters and setters...
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

    public String getConditionRef() {
        return conditionRef;
    }

    public void setConditionRef(String conditionRef) {
        this.conditionRef = conditionRef;
    }
}
