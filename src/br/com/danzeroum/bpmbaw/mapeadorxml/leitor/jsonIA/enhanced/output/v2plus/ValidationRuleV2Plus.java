package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * VERSÃO CORRIGIDA - ValidationRuleV2Plus
 * Regra de validação compatível com V2Plus extractors
 */
public class ValidationRuleV2Plus {

    @JsonProperty("$id")
    private String id;
    private String name;
    private String description;
    private String fieldRef;
    private ValidationType type;
    private String expression;
    private Map<String, Object> parameters;

    // =========================================================================
    // ENUMS
    // =========================================================================

    public enum ValidationType {
        REQUIRED, LENGTH, PATTERN, RANGE, CUSTOM
    }

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    public ValidationRuleV2Plus() {
        this.parameters = new HashMap<>();
    }

    public ValidationRuleV2Plus(String id, String fieldRef, ValidationType type, String expression) {
        this();
        this.id = id;
        this.fieldRef = fieldRef;
        this.type = type;
        this.expression = expression;
    }

    // =========================================================================
    // FACTORY METHODS - CORRIGIDOS
    // =========================================================================

    /**
     * Cria validação de campo obrigatório
     */
    public static ValidationRuleV2Plus createRequiredField(String fieldRef, String id) {
        ValidationRuleV2Plus rule = new ValidationRuleV2Plus();
        rule.setId(id);
        rule.setFieldRef(fieldRef);
        rule.setType(ValidationType.REQUIRED);
        rule.setExpression(fieldRef + " != null && " + fieldRef + " != ''");
        rule.setName("Required Field: " + fieldRef);
        rule.setDescription("Field " + fieldRef + " is required");
        return rule;
    }

    /**
     * Cria validação de comprimento
     */
    public static ValidationRuleV2Plus createLengthValidation(String fieldRef, int minLength, int maxLength) {
        ValidationRuleV2Plus rule = new ValidationRuleV2Plus();
        rule.setId("vl:" + fieldRef.replaceAll("\\.", "_") + "_length");
        rule.setFieldRef(fieldRef);
        rule.setType(ValidationType.LENGTH);
        rule.setExpression(String.format("%s.length >= %d && %s.length <= %d",
                fieldRef, minLength, fieldRef, maxLength));
        rule.setName("Length Validation: " + fieldRef);
        rule.setDescription(String.format("Field %s must have length between %d and %d",
                fieldRef, minLength, maxLength));
        rule.getParameters().put("minLength", minLength);
        rule.getParameters().put("maxLength", maxLength);
        return rule;
    }

    // =========================================================================
    // GETTERS AND SETTERS
    // =========================================================================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFieldRef() { return fieldRef; }
    public void setFieldRef(String fieldRef) { this.fieldRef = fieldRef; }

    public ValidationType getType() { return type; }
    public void setType(ValidationType type) { this.type = type; }

    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }

    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters != null ? parameters : new HashMap<>();
    }

    // =========================================================================
    // BUSINESS METHODS
    // =========================================================================

    /**
     * Verifica se a validação deve ser aplicada
     */
    public boolean shouldApply(String currentStep, String role, Map<String, Object> variables) {
        // Implementação simples - pode ser expandida
        return true;
    }

    /**
     * Executa a validação
     */
    public ValidationResult validate(Object fieldValue, Map<String, Object> variables) {
        ValidationResult result = new ValidationResult();

        try {
            switch (type) {
                case REQUIRED:
                    result.isValid = fieldValue != null && !fieldValue.toString().trim().isEmpty();
                    if (!result.isValid) {
                        result.errorMessage = "Field " + fieldRef + " is required";
                    }
                    break;

                case LENGTH:
                    if (fieldValue != null) {
                        String strValue = fieldValue.toString();
                        Integer minLength = (Integer) parameters.get("minLength");
                        Integer maxLength = (Integer) parameters.get("maxLength");

                        if (minLength != null && strValue.length() < minLength) {
                            result.isValid = false;
                            result.errorMessage = "Field " + fieldRef + " must be at least " + minLength + " characters";
                        } else if (maxLength != null && strValue.length() > maxLength) {
                            result.isValid = false;
                            result.errorMessage = "Field " + fieldRef + " must be at most " + maxLength + " characters";
                        } else {
                            result.isValid = true;
                        }
                    } else {
                        result.isValid = false;
                        result.errorMessage = "Field " + fieldRef + " cannot be null for length validation";
                    }
                    break;

                default:
                    result.isValid = true; // Default to valid for unknown types
                    break;
            }
        } catch (Exception e) {
            result.isValid = false;
            result.errorMessage = "Validation error: " + e.getMessage();
        }

        return result;
    }

    // =========================================================================
    // INNER CLASSES
    // =========================================================================

    public static class ValidationResult {
        public boolean isValid = true;
        public String errorMessage;

        public ValidationResult() {}

        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }
    }
}

