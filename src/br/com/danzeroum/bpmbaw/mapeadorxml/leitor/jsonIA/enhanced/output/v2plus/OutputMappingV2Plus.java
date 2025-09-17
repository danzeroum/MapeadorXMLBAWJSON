package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

public class OutputMappingV2Plus {
    private String sourceField;
    private String targetField;
    private String alias;
    private String description;
    private String expression;
    public OutputMappingV2Plus() {}

    public OutputMappingV2Plus(String sourceField, String targetField) {
        this.sourceField = sourceField;
        this.targetField = targetField;
        this.alias = sourceField;
        this.description = "Auto-generated mapping";
    }

    public OutputMappingV2Plus(String sourceField, String targetField, String alias, String description) {
        this.sourceField = sourceField;
        this.targetField = targetField;
        this.alias = alias;
        this.description = description;
    }

    public boolean validate() {
        return sourceField != null && !sourceField.trim().isEmpty() &&
                targetField != null && !targetField.trim().isEmpty();
    }

    // Getters e setters
    public String getSourceField() { return sourceField; }
    public void setSourceField(String sourceField) { this.sourceField = sourceField; }

    public String getTargetField() { return targetField; }
    public void setTargetField(String targetField) { this.targetField = targetField; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}