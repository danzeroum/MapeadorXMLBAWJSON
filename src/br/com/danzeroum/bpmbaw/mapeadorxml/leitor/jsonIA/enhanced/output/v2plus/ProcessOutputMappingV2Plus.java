/**
 * ProcessOutputMappingV2Plus - VERSÃO COMPLETA JAVA 8
 *
 * Representa um mapeamento de saída (output) no sistema V2Plus.
 * Usado para mapear variáveis internas do processo para dados de saída.
 *
 * CARACTERÍSTICAS:
 * ✅ Java 8 compatível (sem var, sem features Java 9+)
 * ✅ Compatível com IBM BAW output parameters
 * ✅ Suporte a aliases para campos de saída
 * ✅ Suporte a expressões CEL/JavaScript/TWX
 * ✅ Validação robusta de mappings
 * ✅ Metadata rich para análise
 *
 * EXEMPLO DE USO:
 * ```java
 * ProcessOutputMappingV2Plus mapping = new ProcessOutputMappingV2Plus();
 * mapping.setSourceField("tw.local.result");
 * mapping.setTargetField("processResult");
 * mapping.setAlias("result");
 * mapping.setDescription("Map internal result to output");
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
        "sourceField", "targetField", "alias", "expression", "description",
        "conditional", "transformation", "metadata"
})
public class ProcessOutputMappingV2Plus {

    // =========================================================================
    // CORE MAPPING PROPERTIES
    // =========================================================================

    @JsonProperty("sourceField")
    private String sourceField;

    @JsonProperty("targetField")
    private String targetField;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("expression")
    private String expression;

    @JsonProperty("description")
    private String description;

    // =========================================================================
    // MAPPING BEHAVIOR
    // =========================================================================

    @JsonProperty("conditional")
    private OutputCondition conditional;

    @JsonProperty("transformation")
    private OutputTransformation transformation;

    @JsonProperty("metadata")
    private OutputMappingMetadata metadata;

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    /**
     * Constructor padrão
     */
    public ProcessOutputMappingV2Plus() {
        this.conditional = new OutputCondition();
        this.transformation = new OutputTransformation();
        this.metadata = new OutputMappingMetadata();
    }

    /**
     * Constructor com campos básicos
     */
    public ProcessOutputMappingV2Plus(String sourceField, String targetField) {
        this();
        this.sourceField = sourceField;
        this.targetField = targetField;
    }

    /**
     * Constructor com alias
     */
    public ProcessOutputMappingV2Plus(String sourceField, String targetField, String alias) {
        this(sourceField, targetField);
        this.alias = alias;
    }

    /**
     * Constructor completo
     */
    public ProcessOutputMappingV2Plus(String sourceField, String targetField, String alias, String description) {
        this(sourceField, targetField, alias);
        this.description = description;
    }

    // =========================================================================
    // FACTORY METHODS
    // =========================================================================

    /**
     * Criar mapping simples de campo para campo
     */
    public static ProcessOutputMappingV2Plus createSimpleMapping(String sourceField, String targetField, String alias, String description) {
        ProcessOutputMappingV2Plus mapping = new ProcessOutputMappingV2Plus(sourceField, targetField, alias, description);
        mapping.getMetadata().setMappingType("SIMPLE");
        return mapping;
    }

    /**
     * Criar mapping com expressão
     */
    public static ProcessOutputMappingV2Plus createExpressionMapping(String sourceField, String targetField, String alias, String expression, String description) {
        ProcessOutputMappingV2Plus mapping = new ProcessOutputMappingV2Plus(sourceField, targetField, alias, description);
        mapping.setExpression(expression);
        mapping.getMetadata().setMappingType("EXPRESSION");
        return mapping;
    }

    /**
     * Criar mapping condicional
     */
    public static ProcessOutputMappingV2Plus createConditionalMapping(String sourceField, String targetField, String alias, String condition, String description) {
        ProcessOutputMappingV2Plus mapping = new ProcessOutputMappingV2Plus(sourceField, targetField, alias, description);
        mapping.getConditional().setCondition(condition);
        mapping.getConditional().setEnabled(true);
        mapping.getMetadata().setMappingType("CONDITIONAL");
        return mapping;
    }

    /**
     * Criar mapping TWX específico (IBM BAW)
     */
    public static ProcessOutputMappingV2Plus createTWXMapping(String twLocalVariable, String targetField, String alias, String description) {
        String sourceField = twLocalVariable.startsWith("tw.local.") ? twLocalVariable : "tw.local." + twLocalVariable;
        ProcessOutputMappingV2Plus mapping = new ProcessOutputMappingV2Plus(sourceField, targetField, alias, description);
        mapping.getMetadata().setMappingType("TWX");
        mapping.getMetadata().setSourceSystem("IBM_BAW");
        return mapping;
    }

    /**
     * Criar mapping para resultado do processo
     */
    public static ProcessOutputMappingV2Plus createProcessResultMapping(String resultVariableName, String description) {
        ProcessOutputMappingV2Plus mapping = new ProcessOutputMappingV2Plus();
        mapping.setSourceField("tw.local." + cleanFieldName(resultVariableName));
        mapping.setTargetField("processResult");
        mapping.setAlias("result");
        mapping.setDescription(description != null ? description : "Process result: " + resultVariableName);
        mapping.getMetadata().setMappingType("PROCESS_RESULT");
        return mapping;
    }

    /**
     * Criar mapping para status do processo
     */
    public static ProcessOutputMappingV2Plus createProcessStatusMapping() {
        ProcessOutputMappingV2Plus mapping = new ProcessOutputMappingV2Plus();
        mapping.setSourceField("tw.local.processStatus");
        mapping.setTargetField("status");
        mapping.setAlias("status");
        mapping.setDescription("Process execution status");
        mapping.getMetadata().setMappingType("PROCESS_STATUS");
        return mapping;
    }

    // =========================================================================
    // UTILITY METHODS
    // =========================================================================

    /**
     * Adicionar condição para mapping condicional
     */
    public void addCondition(String condition, Object valueIfTrue, Object valueIfFalse) {
        this.conditional.setEnabled(true);
        this.conditional.setCondition(condition);
        this.conditional.setValueIfTrue(valueIfTrue);
        this.conditional.setValueIfFalse(valueIfFalse);
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
     * Adicionar agregação (para casos onde se combina múltiplos valores)
     */
    public void addAggregation(String aggregationType, List<String> sourceFields) {
        this.transformation.setType("AGGREGATION");
        this.transformation.setAggregationType(aggregationType);
        this.transformation.setAggregationSources(sourceFields);
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
        if (conditional != null && conditional.isEnabled()) {
            return "CONDITIONAL";
        }

        if (expression != null && !expression.trim().isEmpty()) {
            return "EXPRESSION";
        }

        if (transformation != null && transformation.getType() != null) {
            return "TRANSFORMATION";
        }

        if (sourceField != null && sourceField.startsWith("tw.local.")) {
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

        // Validar alias se presente
        if (alias != null && !alias.trim().isEmpty()) {
            if (alias.length() > 100) {
                errors.add("Alias is too long (max 100 characters)");
            }

            if (!alias.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
                errors.add("Alias must be a valid identifier");
            }
        }

        // Validar expressão se presente
        if (expression != null && !expression.trim().isEmpty()) {
            if (expression.length() > 4000) {
                errors.add("Expression is too long (max 4000 characters)");
            }
        }

        // Validar condição se presente
        if (conditional != null && conditional.isEnabled()) {
            List<String> conditionalErrors = conditional.validate();
            errors.addAll(conditionalErrors);
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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

    public OutputCondition getConditional() {
        return conditional != null ? conditional : new OutputCondition();
    }

    public void setConditional(OutputCondition conditional) {
        this.conditional = conditional != null ? conditional : new OutputCondition();
    }

    public OutputTransformation getTransformation() {
        return transformation != null ? transformation : new OutputTransformation();
    }

    public void setTransformation(OutputTransformation transformation) {
        this.transformation = transformation != null ? transformation : new OutputTransformation();
    }

    public OutputMappingMetadata getMetadata() {
        return metadata != null ? metadata : new OutputMappingMetadata();
    }

    public void setMetadata(OutputMappingMetadata metadata) {
        this.metadata = metadata != null ? metadata : new OutputMappingMetadata();
    }

    // =========================================================================
    // INNER CLASSES
    // =========================================================================

    /**
     * Condição para output mapping condicional
     */
    public static class OutputCondition {
        private boolean enabled;
        private String condition;
        private Object valueIfTrue;
        private Object valueIfFalse;
        private String language;

        public OutputCondition() {
            this.enabled = false;
            this.language = "javascript";
        }

        public List<String> validate() {
            List<String> errors = new ArrayList<String>();

            if (enabled) {
                if (condition == null || condition.trim().isEmpty()) {
                    errors.add("Condition expression is required when conditional is enabled");
                }

                if (condition != null && condition.length() > 1000) {
                    errors.add("Condition expression is too long (max 1000 characters)");
                }
            }

            return errors;
        }

        // Getters and Setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }

        public Object getValueIfTrue() { return valueIfTrue; }
        public void setValueIfTrue(Object valueIfTrue) { this.valueIfTrue = valueIfTrue; }

        public Object getValueIfFalse() { return valueIfFalse; }
        public void setValueIfFalse(Object valueIfFalse) { this.valueIfFalse = valueIfFalse; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
    }

    /**
     * Transformação aplicada ao output mapping
     */
    public static class OutputTransformation {
        private String type;
        private String fromType;
        private String toType;
        private String fromFormat;
        private String toFormat;
        private String aggregationType;
        private List<String> aggregationSources;
        private Map<String, Object> parameters;

        public OutputTransformation() {
            this.aggregationSources = new ArrayList<String>();
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
                } else if ("AGGREGATION".equals(type)) {
                    if (aggregationType == null || aggregationSources == null || aggregationSources.isEmpty()) {
                        errors.add("Aggregation requires aggregationType and aggregationSources");
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

        public String getAggregationType() { return aggregationType; }
        public void setAggregationType(String aggregationType) { this.aggregationType = aggregationType; }

        public List<String> getAggregationSources() {
            return aggregationSources != null ? aggregationSources : new ArrayList<String>();
        }
        public void setAggregationSources(List<String> aggregationSources) {
            this.aggregationSources = aggregationSources != null ? aggregationSources : new ArrayList<String>();
        }

        public Map<String, Object> getParameters() {
            return parameters != null ? parameters : new HashMap<String, Object>();
        }
        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters != null ? parameters : new HashMap<String, Object>();
        }
    }

    /**
     * Metadata do output mapping
     */
    public static class OutputMappingMetadata {
        private String mappingType;
        private String sourceSystem;
        private String createdBy;
        private String createdAt;
        private boolean isRequired;
        private boolean isArray;
        private String dataType;
        private Map<String, Object> customProperties;

        public OutputMappingMetadata() {
            this.customProperties = new HashMap<String, Object>();
            this.isRequired = false;
            this.isArray = false;
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

        public boolean isRequired() { return isRequired; }
        public void setRequired(boolean required) { this.isRequired = required; }

        public boolean isArray() { return isArray; }
        public void setArray(boolean array) { this.isArray = array; }

        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }

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
    public static List<ProcessOutputMappingV2Plus> createSampleMappings() {
        List<ProcessOutputMappingV2Plus> samples = new ArrayList<ProcessOutputMappingV2Plus>();

        // Mapping simples
        ProcessOutputMappingV2Plus simple = createSimpleMapping(
                "tw.local.customerData",
                "customerInfo",
                "customer",
                "Customer information output"
        );
        samples.add(simple);

        // Mapping com expressão
        ProcessOutputMappingV2Plus expression = createExpressionMapping(
                "tw.local.orderTotal",
                "totalAmount",
                "total",
                "formatCurrency(tw.local.orderTotal)",
                "Formatted order total"
        );
        samples.add(expression);

        // Mapping condicional
        ProcessOutputMappingV2Plus conditional = createConditionalMapping(
                "tw.local.processStatus",
                "status",
                "status",
                "tw.local.processStatus === 'SUCCESS'",
                "Conditional status output"
        );
        conditional.addCondition(
                "tw.local.processStatus === 'SUCCESS'",
                "COMPLETED",
                "FAILED"
        );
        samples.add(conditional);

        // Mapping TWX
        ProcessOutputMappingV2Plus twx = createTWXMapping(
                "resultData",
                "processResult",
                "result",
                "Final process result from TWX"
        );
        samples.add(twx);

        // Mapping de resultado
        ProcessOutputMappingV2Plus result = createProcessResultMapping(
                "finalResult",
                "Final processing result"
        );
        samples.add(result);

        // Mapping de status
        ProcessOutputMappingV2Plus status = createProcessStatusMapping();
        samples.add(status);

        return samples;
    }

    /**
     * Criar mappings específicos para Caetano Retail
     */
    public static List<ProcessOutputMappingV2Plus> createCaetanoRetailMappings() {
        List<ProcessOutputMappingV2Plus> mappings = new ArrayList<ProcessOutputMappingV2Plus>();

        // Orçamento final
        ProcessOutputMappingV2Plus orcamento = createTWXMapping(
                "orcamentoFinal",
                "orcamentoRecondicionamento",
                "orcamento",
                "Orçamento final do recondicionamento de veículo"
        );
        orcamento.getMetadata().setDataType("currency");
        orcamento.getMetadata().setRequired(true);
        mappings.add(orcamento);

        // Status do processo
        ProcessOutputMappingV2Plus status = createConditionalMapping(
                "tw.local.statusProcesso",
                "statusRecondicionamento",
                "status",
                "tw.local.statusProcesso !== null && tw.local.statusProcesso !== ''",
                "Status do processo de recondicionamento"
        );
        status.addCondition(
                "tw.local.statusProcesso === 'APROVADO'",
                "APPROVED",
                "PENDING"
        );
        mappings.add(status);

        // Resultado do processamento
        ProcessOutputMappingV2Plus resultado = createSimpleMapping(
                "tw.local.resultadoProcessamento",
                "resultadoFinal",
                "resultado",
                "Resultado final do processamento do recondicionamento"
        );
        resultado.getMetadata().setDataType("object");
        mappings.add(resultado);

        // Dados do veículo processado
        ProcessOutputMappingV2Plus veiculo = createTWXMapping(
                "dadosVeiculo",
                "veiculoRecondicionado",
                "veiculo",
                "Dados do veículo após recondicionamento"
        );
        veiculo.getMetadata().setDataType("object");
        mappings.add(veiculo);

        // Lista de serviços realizados
        ProcessOutputMappingV2Plus servicos = createTWXMapping(
                "servicosRealizados",
                "listaServicos",
                "servicos",
                "Lista de serviços realizados no recondicionamento"
        );
        servicos.getMetadata().setArray(true);
        servicos.getMetadata().setDataType("array");
        mappings.add(servicos);

        return mappings;
    }

    /**
     * Teste de funcionalidade básica
     */
    public static void testOutputMappingCreation() {
        System.out.println("🧪 Testing ProcessOutputMappingV2Plus...");

        try {
            // Teste 1: Criação básica
            ProcessOutputMappingV2Plus mapping = new ProcessOutputMappingV2Plus("source", "target");
            assert mapping != null : "Mapping should be created";
            assert "source".equals(mapping.getSourceField()) : "Source field should match";
            assert "target".equals(mapping.getTargetField()) : "Target field should match";
            System.out.println("✅ Basic creation: PASSED");

            // Teste 2: Factory methods
            ProcessOutputMappingV2Plus twxMapping = createTWXMapping("variable", "output", "alias", "Test TWX mapping");
            assert twxMapping.getSourceField().startsWith("tw.local.") : "TWX mapping should have correct source";
            assert "TWX".equals(twxMapping.getMetadata().getMappingType()) : "Should be TWX type";
            assert "alias".equals(twxMapping.getAlias()) : "Alias should match";
            System.out.println("✅ Factory methods: PASSED");

            // Teste 3: Validação
            mapping.setAlias("validAlias");
            List<String> errors = mapping.validate();
            assert errors.isEmpty() : "Valid mapping should have no errors: " + errors;

            ProcessOutputMappingV2Plus invalidMapping = new ProcessOutputMappingV2Plus();
            List<String> invalidErrors = invalidMapping.validate();
            assert !invalidErrors.isEmpty() : "Invalid mapping should have errors";
            System.out.println("✅ Validation: PASSED");

            // Teste 4: Condições
            mapping.addCondition("source !== null", "output_value", "default_value");
            assert mapping.getConditional().isEnabled() : "Conditional should be enabled";
            assert "source !== null".equals(mapping.getConditional().getCondition()) : "Condition should match";
            System.out.println("✅ Conditions: PASSED");

            // Teste 5: Transformações
            mapping.addTypeTransformation("string", "number");
            assert "TYPE_CONVERSION".equals(mapping.getTransformation().getType()) : "Transformation should be set";
            System.out.println("✅ Transformations: PASSED");

            // Teste 6: Samples
            List<ProcessOutputMappingV2Plus> samples = createSampleMappings();
            assert !samples.isEmpty() : "Should create sample mappings";
            assert samples.size() >= 5 : "Should have multiple samples";

            for (ProcessOutputMappingV2Plus sample : samples) {
                List<String> sampleErrors = sample.validate();
                assert sampleErrors.isEmpty() : "Sample mapping should be valid: " + sampleErrors;
            }
            System.out.println("✅ Sample mappings: PASSED");

            // Teste 7: Caetano Retail specific
            List<ProcessOutputMappingV2Plus> caetanoMappings = createCaetanoRetailMappings();
            assert !caetanoMappings.isEmpty() : "Should create Caetano mappings";
            assert caetanoMappings.size() >= 3 : "Should have multiple Caetano mappings";

            for (ProcessOutputMappingV2Plus caetanoMapping : caetanoMappings) {
                List<String> caetanoErrors = caetanoMapping.validate();
                assert caetanoErrors.isEmpty() : "Caetano mapping should be valid: " + caetanoErrors;
            }
            System.out.println("✅ Caetano Retail mappings: PASSED");

            System.out.println("🎉 All ProcessOutputMappingV2Plus tests passed!");

        } catch (Exception e) {
            System.err.println("❌ ProcessOutputMappingV2Plus test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "ProcessOutputMappingV2Plus{" +
                "sourceField='" + sourceField + '\'' +
                ", targetField='" + targetField + '\'' +
                ", alias='" + alias + '\'' +
                ", hasExpression=" + (expression != null && !expression.isEmpty()) +
                ", hasCondition=" + (conditional != null && conditional.isEnabled()) +
                ", hasTransformation=" + (transformation != null && transformation.getType() != null) +
                '}';
    }
}