/**
 * ProcessInputMappingV2Plus - VERSÃO COMPLETA JAVA 8
 *
 * Representa um mapeamento de entrada (input) no sistema V2Plus.
 * Usado para mapear dados de entrada do processo para variáveis internas.
 *
 * CARACTERÍSTICAS:
 * ✅ Java 8 compatível (sem var, sem features Java 9+)
 * ✅ Compatível com IBM BAW input parameters
 * ✅ Suporte a expressões CEL/JavaScript/TWX
 * ✅ Validação robusta de mappings
 * ✅ Metadata rich para análise
 *
 * EXEMPLO DE USO:
 * ```java
 * ProcessInputMappingV2Plus mapping = new ProcessInputMappingV2Plus();
 * mapping.setSourceField("customerData");
 * mapping.setTargetField("tw.local.customer");
 * mapping.setDescription("Map customer input to local variable");
 * ```
 *
 * @version 2.3.0-complete-java8
 * @author Enhanced BAW Analysis Team
 */
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.*;

@JsonPropertyOrder({
        "sourceField", "targetField", "expression", "description",
        "required", "defaultValue", "transformation", "metadata"
})
public class ProcessInputMappingV2Plus {

    // =========================================================================
    // CORE MAPPING PROPERTIES
    // =========================================================================

    @JsonProperty("sourceField")
    private String sourceField;

    @JsonProperty("targetField")
    private String targetField;

    @JsonProperty("expression")
    private String expression;

    @JsonProperty("description")
    private String description;

    // =========================================================================
    // MAPPING BEHAVIOR
    // =========================================================================

    @JsonProperty("required")
    private boolean required;

    @JsonProperty("defaultValue")
    private Object defaultValue;

    @JsonProperty("transformation")
    private InputTransformation transformation;

    @JsonProperty("metadata")
    private InputMappingMetadata metadata;

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    /**
     * Constructor padrão
     */
    public ProcessInputMappingV2Plus() {
        this.required = false;
        this.transformation = new InputTransformation();
        this.metadata = new InputMappingMetadata();
    }

    /**
     * Constructor com campos básicos
     */
    public ProcessInputMappingV2Plus(String sourceField, String targetField) {
        this();
        this.sourceField = sourceField;
        this.targetField = targetField;
    }

    /**
     * Constructor completo
     */
    public ProcessInputMappingV2Plus(String sourceField, String targetField, String description) {
        this(sourceField, targetField);
        this.description = description;
    }

    // =========================================================================
    // FACTORY METHODS
    // =========================================================================

    /**
     * Criar mapping simples de campo para campo
     */
    public static ProcessInputMappingV2Plus createSimpleMapping(String sourceField, String targetField, String description) {
        ProcessInputMappingV2Plus mapping = new ProcessInputMappingV2Plus(sourceField, targetField, description);
        mapping.getMetadata().setMappingType("SIMPLE");
        return mapping;
    }

    /**
     * Criar mapping com expressão
     */
    public static ProcessInputMappingV2Plus createExpressionMapping(String sourceField, String targetField, String expression, String description) {
        ProcessInputMappingV2Plus mapping = new ProcessInputMappingV2Plus(sourceField, targetField, description);
        mapping.setExpression(expression);
        mapping.getMetadata().setMappingType("EXPRESSION");
        return mapping;
    }

    /**
     * Criar mapping com transformação
     */
    public static ProcessInputMappingV2Plus createTransformationMapping(String sourceField, String targetField, String transformationType, String description) {
        ProcessInputMappingV2Plus mapping = new ProcessInputMappingV2Plus(sourceField, targetField, description);
        mapping.getTransformation().setType(transformationType);
        mapping.getMetadata().setMappingType("TRANSFORMATION");
        return mapping;
    }

    /**
     * Criar mapping TWX específico (IBM BAW)
     */
    public static ProcessInputMappingV2Plus createTWXMapping(String sourceField, String twLocalVariable, String description) {
        String targetField = twLocalVariable.startsWith("tw.local.") ? twLocalVariable : "tw.local." + twLocalVariable;
        ProcessInputMappingV2Plus mapping = new ProcessInputMappingV2Plus(sourceField, targetField, description);
        mapping.getMetadata().setMappingType("TWX");
        mapping.getMetadata().setSourceSystem("IBM_BAW");
        return mapping;
    }

    /**
     * Criar mapping para parâmetro de entrada do processo
     */
    public static ProcessInputMappingV2Plus createProcessParameterMapping(String parameterName, String description) {
        ProcessInputMappingV2Plus mapping = new ProcessInputMappingV2Plus();
        mapping.setSourceField(parameterName);
        mapping.setTargetField("tw.local." + cleanFieldName(parameterName));
        mapping.setDescription(description != null ? description : "Process parameter: " + parameterName);
        mapping.setRequired(true);
        mapping.getMetadata().setMappingType("PROCESS_PARAMETER");
        return mapping;
    }

    // =========================================================================
    // UTILITY METHODS
    // =========================================================================

    /**
     * Definir valor padrão se o campo de origem estiver vazio
     */
    public void setDefaultValueIfEmpty(Object defaultValue) {
        this.defaultValue = defaultValue;
        this.getMetadata().setHasDefaultValue(true);
    }

    /**
     * Marcar como obrigatório
     */
    public void makeRequired() {
        this.required = true;
    }

    /**
     * Marcar como opcional
     */
    public void makeOptional() {
        this.required = false;
    }

    /**
     * Adicionar transformação de tipo
     */
    public void addTypeTransformation(String fromType, String toType) {
        this.transformation.setType("TYPE_CONVERSION");
        this.transformation.setFromType(fromType);
        this.transformation.setToType(toType);
    }

    /**
     * Adicionar transformação de formato
     */
    public void addFormatTransformation(String fromFormat, String toFormat) {
        this.transformation.setType("FORMAT_CONVERSION");
        this.transformation.setFromFormat(fromFormat);
        this.transformation.setToFormat(toFormat);
    }

    /**
     * Verificar se é mapping válido
     */
    public boolean isValid() {
        return sourceField != null && !sourceField.trim().isEmpty() &&
                targetField != null && !targetField.trim().isEmpty();
    }

    /**
     * Obter tipo de mapping baseado no conteúdo
     */
    public String inferMappingType() {
        if (expression != null && !expression.trim().isEmpty()) {
            return "EXPRESSION";
        }

        if (transformation != null && transformation.getType() != null) {
            return "TRANSFORMATION";
        }

        if (targetField != null && targetField.startsWith("tw.local.")) {
            return "TWX";
        }

        return "SIMPLE";
    }

    /**
     * Validar mapping
     */
    public List<String> validate() {
        List<String> errors = new ArrayList<String>();

        if (sourceField == null || sourceField.trim().isEmpty()) {
            errors.add("Source field is required");
        }

        if (targetField == null || targetField.trim().isEmpty()) {
            errors.add("Target field is required");
        }

        if (sourceField != null && targetField != null && sourceField.equals(targetField)) {
            errors.add("Source and target fields cannot be the same");
        }

        // Validar expressão se presente
        if (expression != null && !expression.trim().isEmpty()) {
            if (expression.length() > 4000) {
                errors.add("Expression is too long (max 4000 characters)");
            }
        }

        // Validar transformação se presente
        if (transformation != null) {
            List<String> transformationErrors = transformation.validate();
            errors.addAll(transformationErrors);
        }

        return errors;
    }

    /**
     * Limpar nome de campo
     */
    private static String cleanFieldName(String fieldName) {
        if (fieldName == null) return "field";

        return fieldName.replaceAll("[^a-zA-Z0-9_]", "_")
                .replaceAll("_{2,}", "_")
                .toLowerCase();
    }

    // =========================================================================
    // GETTERS AND SETTERS
    // =========================================================================

    public String getSourceField() {
        return sourceField;
    }

    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    public String getTargetField() {
        return targetField;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public InputTransformation getTransformation() {
        return transformation != null ? transformation : new InputTransformation();
    }

    public void setTransformation(InputTransformation transformation) {
        this.transformation = transformation != null ? transformation : new InputTransformation();
    }

    public InputMappingMetadata getMetadata() {
        return metadata != null ? metadata : new InputMappingMetadata();
    }

    public void setMetadata(InputMappingMetadata metadata) {
        this.metadata = metadata != null ? metadata : new InputMappingMetadata();
    }

    // =========================================================================
    // INNER CLASSES
    // =========================================================================

    /**
     * Transformação aplicada ao input mapping
     */
    public static class InputTransformation {
        private String type;
        private String fromType;
        private String toType;
        private String fromFormat;
        private String toFormat;
        private Map<String, Object> parameters;

        public InputTransformation() {
            this.parameters = new HashMap<String, Object>();
        }

        public List<String> validate() {
            List<String> errors = new ArrayList<String>();

            if (type != null && !type.trim().isEmpty()) {
                if ("TYPE_CONVERSION".equals(type)) {
                    if (fromType == null || toType == null) {
                        errors.add("Type conversion requires fromType and toType");
                    }
                } else if ("FORMAT_CONVERSION".equals(type)) {
                    if (fromFormat == null || toFormat == null) {
                        errors.add("Format conversion requires fromFormat and toFormat");
                    }
                }
            }

            return errors;
        }

        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getFromType() { return fromType; }
        public void setFromType(String fromType) { this.fromType = fromType; }

        public String getToType() { return toType; }
        public void setToType(String toType) { this.toType = toType; }

        public String getFromFormat() { return fromFormat; }
        public void setFromFormat(String fromFormat) { this.fromFormat = fromFormat; }

        public String getToFormat() { return toFormat; }
        public void setToFormat(String toFormat) { this.toFormat = toFormat; }

        public Map<String, Object> getParameters() {
            return parameters != null ? parameters : new HashMap<String, Object>();
        }
        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters != null ? parameters : new HashMap<String, Object>();
        }
    }

    /**
     * Metadata do input mapping
     */
    public static class InputMappingMetadata {
        private String mappingType;
        private String sourceSystem;
        private String createdBy;
        private String createdAt;
        private boolean hasDefaultValue;
        private Map<String, Object> customProperties;

        public InputMappingMetadata() {
            this.customProperties = new HashMap<String, Object>();
            this.hasDefaultValue = false;
        }

        // Getters and Setters
        public String getMappingType() { return mappingType; }
        public void setMappingType(String mappingType) { this.mappingType = mappingType; }

        public String getSourceSystem() { return sourceSystem; }
        public void setSourceSystem(String sourceSystem) { this.sourceSystem = sourceSystem; }

        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public boolean isHasDefaultValue() { return hasDefaultValue; }
        public void setHasDefaultValue(boolean hasDefaultValue) { this.hasDefaultValue = hasDefaultValue; }

        public Map<String, Object> getCustomProperties() {
            return customProperties != null ? customProperties : new HashMap<String, Object>();
        }
        public void setCustomProperties(Map<String, Object> customProperties) {
            this.customProperties = customProperties != null ? customProperties : new HashMap<String, Object>();
        }
    }

    // =========================================================================
    // UTILITY AND TEST METHODS
    // =========================================================================

    /**
     * Criar mappings de exemplo para teste
     */
    public static List<ProcessInputMappingV2Plus> createSampleMappings() {
        List<ProcessInputMappingV2Plus> samples = new ArrayList<ProcessInputMappingV2Plus>();

        // Mapping simples
        ProcessInputMappingV2Plus simple = createSimpleMapping(
                "customerName",
                "tw.local.customer.name",
                "Customer name input"
        );
        samples.add(simple);

        // Mapping com expressão
        ProcessInputMappingV2Plus expression = createExpressionMapping(
                "inputData",
                "tw.local.processedData",
                "inputData.trim().toUpperCase()",
                "Process and transform input data"
        );
        samples.add(expression);

        // Mapping TWX
        ProcessInputMappingV2Plus twx = createTWXMapping(
                "processRequest",
                "requestData",
                "Map process request to TWX variable"
        );
        samples.add(twx);

        // Mapping de parâmetro
        ProcessInputMappingV2Plus param = createProcessParameterMapping(
                "orderId",
                "Order identification parameter"
        );
        param.makeRequired();
        samples.add(param);

        return samples;
    }

    /**
     * Teste de funcionalidade básica
     */
    public static void testInputMappingCreation() {
        System.out.println("🧪 Testing ProcessInputMappingV2Plus...");

        try {
            // Teste 1: Criação básica
            ProcessInputMappingV2Plus mapping = new ProcessInputMappingV2Plus("source", "target");
            assert mapping != null : "Mapping should be created";
            assert "source".equals(mapping.getSourceField()) : "Source field should match";
            assert "target".equals(mapping.getTargetField()) : "Target field should match";
            System.out.println("✅ Basic creation: PASSED");

            // Teste 2: Factory methods
            ProcessInputMappingV2Plus twxMapping = createTWXMapping("input", "variable", "Test TWX mapping");
            assert twxMapping.getTargetField().startsWith("tw.local.") : "TWX mapping should have correct target";
            assert "TWX".equals(twxMapping.getMetadata().getMappingType()) : "Should be TWX type";
            System.out.println("✅ Factory methods: PASSED");

            // Teste 3: Validação
            List<String> errors = mapping.validate();
            assert errors.isEmpty() : "Valid mapping should have no errors";

            ProcessInputMappingV2Plus invalidMapping = new ProcessInputMappingV2Plus();
            List<String> invalidErrors = invalidMapping.validate();
            assert !invalidErrors.isEmpty() : "Invalid mapping should have errors";
            System.out.println("✅ Validation: PASSED");

            // Teste 4: Transformações
            mapping.addTypeTransformation("string", "integer");
            assert "TYPE_CONVERSION".equals(mapping.getTransformation().getType()) : "Transformation should be set";
            System.out.println("✅ Transformations: PASSED");

            // Teste 5: Samples
            List<ProcessInputMappingV2Plus> samples = createSampleMappings();
            assert !samples.isEmpty() : "Should create sample mappings";
            assert samples.size() >= 3 : "Should have multiple samples";

            for (ProcessInputMappingV2Plus sample : samples) {
                List<String> sampleErrors = sample.validate();
                assert sampleErrors.isEmpty() : "Sample mapping should be valid: " + sampleErrors;
            }
            System.out.println("✅ Sample mappings: PASSED");

            System.out.println("🎉 All ProcessInputMappingV2Plus tests passed!");

        } catch (Exception e) {
            System.err.println("❌ ProcessInputMappingV2Plus test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "ProcessInputMappingV2Plus{" +
                "sourceField='" + sourceField + '\'' +
                ", targetField='" + targetField + '\'' +
                ", required=" + required +
                ", hasExpression=" + (expression != null && !expression.isEmpty()) +
                ", hasTransformation=" + (transformation != null && transformation.getType() != null) +
                '}';
    }
}