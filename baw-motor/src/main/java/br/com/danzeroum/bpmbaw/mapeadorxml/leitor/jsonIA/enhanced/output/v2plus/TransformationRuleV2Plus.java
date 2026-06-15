package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map; /**
 * VERSÃO CORRIGIDA - TransformationRuleV2Plus
 * Regra de transformação compatível com V2Plus extractors
 */
public class TransformationRuleV2Plus {

    @JsonProperty("$id")
    private String id;
    private String name;
    private String description;
    private String source;
    private String target;
    private TransformationType type;
    private String expression;
    private Map<String, Object> parameters;

    // =========================================================================
    // ENUMS
    // =========================================================================

    public enum TransformationType {
        TEXT_FORMAT, CALCULATION, MAPPING, CUSTOM
    }

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    public TransformationRuleV2Plus() {
        this.parameters = new HashMap<>();
    }

    public TransformationRuleV2Plus(String id, String source, String target, TransformationType type, String expression) {
        this();
        this.id = id;
        this.source = source;
        this.target = target;
        this.type = type;
        this.expression = expression;
    }

    // =========================================================================
    // FACTORY METHODS - CORRIGIDOS
    // =========================================================================

    /**
     * Cria transformação de formatação de texto
     */
    public static TransformationRuleV2Plus createTextFormat(String target, String expression, String format) {
        TransformationRuleV2Plus rule = new TransformationRuleV2Plus();
        rule.setId("tf:" + target.replaceAll("\\.", "_"));
        rule.setTarget(target);
        rule.setType(TransformationType.TEXT_FORMAT);
        rule.setExpression(expression);
        rule.setName("Text Format: " + target);
        rule.setDescription("Format text for field " + target);
        rule.getParameters().put("format", format);
        return rule;
    }

    /**
     * Cria transformação de cálculo
     */
    public static TransformationRuleV2Plus createCalculation(String target, String expression) {
        TransformationRuleV2Plus rule = new TransformationRuleV2Plus();
        rule.setId("calc:" + target.replaceAll("\\.", "_"));
        rule.setTarget(target);
        rule.setType(TransformationType.CALCULATION);
        rule.setExpression(expression);
        rule.setName("Calculation: " + target);
        rule.setDescription("Calculate value for field " + target);
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

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public TransformationType getType() { return type; }
    public void setType(TransformationType type) { this.type = type; }

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
     * Verifica se a transformação deve ser aplicada
     */
    public boolean shouldApply(String currentStep, String role, Map<String, Object> variables) {
        // Implementação simples - pode ser expandida
        return true;
    }

    /**
     * Executa a transformação
     */
    public TransformationResult transform(Object sourceValue, Map<String, Object> variables) {
        TransformationResult result = new TransformationResult();

        try {
            switch (type) {
                case TEXT_FORMAT:
                    if (sourceValue != null) {
                        String format = (String) parameters.get("format");
                        if ("UPPERCASE".equals(format)) {
                            result.targetValue = sourceValue.toString().toUpperCase();
                        } else if ("LOWERCASE".equals(format)) {
                            result.targetValue = sourceValue.toString().toLowerCase();
                        } else {
                            result.targetValue = sourceValue.toString();
                        }
                        result.success = true;
                    }
                    break;

                case CALCULATION:
                    // Implementação simplificada de cálculo
                    // Em uma implementação real, você usaria um engine de expressões
                    result.targetValue = sourceValue; // Placeholder
                    result.success = true;
                    break;

                default:
                    result.targetValue = sourceValue;
                    result.success = true;
                    break;
            }
        } catch (Exception e) {
            result.success = false;
            result.errorMessage = "Transformation error: " + e.getMessage();
        }

        return result;
    }

    // =========================================================================
    // INNER CLASSES
    // =========================================================================

    public static class TransformationResult {
        public boolean success = true;
        public Object targetValue;
        public String errorMessage;

        public TransformationResult() {}

        public TransformationResult(boolean success, Object targetValue, String errorMessage) {
            this.success = success;
            this.targetValue = targetValue;
            this.errorMessage = errorMessage;
        }
    }
}
