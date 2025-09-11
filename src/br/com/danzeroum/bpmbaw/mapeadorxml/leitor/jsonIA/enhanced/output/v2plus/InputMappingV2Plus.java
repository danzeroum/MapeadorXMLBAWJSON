package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

public class InputMappingV2Plus {
    private String sourceField;
    private String targetField;
    private String description;

    public InputMappingV2Plus() {}

    public InputMappingV2Plus(String sourceField, String targetField) {
        this.sourceField = sourceField;
        this.targetField = targetField;
        this.description = "Auto-generated mapping";
    }

    public InputMappingV2Plus(String sourceField, String targetField, String description) {
        this.sourceField = sourceField;
        this.targetField = targetField;
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

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}