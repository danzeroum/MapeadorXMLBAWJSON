/**
 * ProcessDataTypeV2Plus - VERSÃO COMPLETA JAVA 8
 *
 * Representa um tipo de dados no sistema V2Plus, compatível com:
 * - IBM BAW Business Objects (TwClass)
 * - Tipos primitivos (String, Integer, Boolean, etc.)
 * - Tipos complexos com JSON Schema
 * - Arrays e collections
 * - Tipos customizados do usuário
 *
 * CARACTERÍSTICAS:
 * ✅ Java 8 compatível (sem var, sem features Java 9+)
 * ✅ JSON Schema integration para validação
 * ✅ Metadata rich para IA analysis
 * ✅ Hierarchical type support (baseType)
 * ✅ Array/Collection support
 *
 * @version 2.3.0-complete-java8
 * @author Enhanced BAW Analysis Team
 */
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;
import java.util.*;

@JsonPropertyOrder({
        "id", "name", "jsonSchema", "description", "metadata",
        "baseType", "isArray", "arrayItemType", "properties", "constraints"
})
public class ProcessDataTypeV2Plus {

    // =========================================================================
    // CORE PROPERTIES
    // =========================================================================

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("jsonSchema")
    private Map<String, Object> jsonSchema;

    @JsonProperty("description")
    private String description;

    @JsonProperty("metadata")
    private DataTypeMetadata metadata;

    // =========================================================================
    // TYPE HIERARCHY AND STRUCTURE
    // =========================================================================

    @JsonProperty("baseType")
    private String baseType;

    @JsonProperty("isArray")
    private boolean isArray;

    @JsonProperty("arrayItemType")
    private String arrayItemType;

    @JsonProperty("properties")
    private Map<String, DataTypeProperty> properties;

    @JsonProperty("constraints")
    private List<DataTypeConstraint> constraints;

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    /**
     * Constructor padrão
     */
    public ProcessDataTypeV2Plus() {
        this.jsonSchema = new HashMap<String, Object>();
        this.metadata = new DataTypeMetadata();
        this.properties = new HashMap<String, DataTypeProperty>();
        this.constraints = new ArrayList<DataTypeConstraint>();
        this.isArray = false;
    }

    /**
     * Constructor com ID e nome
     */
    public ProcessDataTypeV2Plus(String id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor completo
     */
    public ProcessDataTypeV2Plus(String id, String name, String description, String baseType) {
        this(id, name);
        this.description = description;
        this.baseType = baseType;
    }

    // =========================================================================
    // FACTORY METHODS
    // =========================================================================

    /**
     * Criar tipo primitivo padrão
     */
    public static ProcessDataTypeV2Plus createPrimitiveType(String id, String name, String primitiveType) {
        ProcessDataTypeV2Plus dataType = new ProcessDataTypeV2Plus(id, name);
        dataType.setDescription(name + " primitive type");
        dataType.setBaseType(primitiveType);

        // JSON Schema para tipo primitivo
        Map<String, Object> schema = new HashMap<String, Object>();
        schema.put("type", primitiveType.toLowerCase());
        dataType.setJsonSchema(schema);

        // Metadata
        dataType.getMetadata().setVersion("1.0.0");
        dataType.getMetadata().setCreatedAt(LocalDateTime.now().toString());
        dataType.getMetadata().setAuthor("BAW Analysis V2Plus");

        return dataType;
    }

    /**
     * Criar tipo String
     */
    public static ProcessDataTypeV2Plus createStringType() {
        ProcessDataTypeV2Plus stringType = createPrimitiveType("dt:string@1", "String", "string");

        // Constraints específicos para String
        DataTypeConstraint lengthConstraint = new DataTypeConstraint();
        lengthConstraint.setType("maxLength");
        lengthConstraint.setValue("4000");
        lengthConstraint.setDescription("Maximum string length");
        stringType.addConstraint(lengthConstraint);

        return stringType;
    }

    /**
     * Criar tipo Integer
     */
    public static ProcessDataTypeV2Plus createIntegerType() {
        ProcessDataTypeV2Plus integerType = createPrimitiveType("dt:integer@1", "Integer", "integer");

        // Constraints específicos para Integer
        DataTypeConstraint rangeConstraint = new DataTypeConstraint();
        rangeConstraint.setType("range");
        rangeConstraint.setValue("-2147483648:2147483647");
        rangeConstraint.setDescription("32-bit signed integer range");
        integerType.addConstraint(rangeConstraint);

        return integerType;
    }

    /**
     * Criar tipo Boolean
     */
    public static ProcessDataTypeV2Plus createBooleanType() {
        return createPrimitiveType("dt:boolean@1", "Boolean", "boolean");
    }

    /**
     * Criar tipo Object genérico
     */
    public static ProcessDataTypeV2Plus createObjectType() {
        ProcessDataTypeV2Plus objectType = createPrimitiveType("dt:object@1", "Object", "object");
        objectType.setDescription("Generic object type for complex data structures");

        // JSON Schema para object
        Map<String, Object> schema = new HashMap<String, Object>();
        schema.put("type", "object");
        schema.put("additionalProperties", true);
        objectType.setJsonSchema(schema);

        return objectType;
    }

    /**
     * Criar tipo Array
     */
    public static ProcessDataTypeV2Plus createArrayType(String itemTypeId, String itemTypeName) {
        ProcessDataTypeV2Plus arrayType = new ProcessDataTypeV2Plus();
        arrayType.setId("dt:array_" + itemTypeId + "@1");
        arrayType.setName("Array<" + itemTypeName + ">");
        arrayType.setDescription("Array of " + itemTypeName);
        arrayType.setIsArray(true);
        arrayType.setArrayItemType(itemTypeId);
        arrayType.setBaseType("array");

        // JSON Schema para array
        Map<String, Object> schema = new HashMap<String, Object>();
        schema.put("type", "array");

        Map<String, Object> items = new HashMap<String, Object>();
        items.put("$ref", "#/dataTypes/" + itemTypeId);
        schema.put("items", items);

        arrayType.setJsonSchema(schema);

        return arrayType;
    }

    /**
     * Criar tipo customizado baseado em IBM BAW TwClass
     */
    public static ProcessDataTypeV2Plus createFromTwClass(String classId, String className, String description) {
        ProcessDataTypeV2Plus customType = new ProcessDataTypeV2Plus();
        customType.setId("dt:twclass_" + classId + "@1");
        customType.setName(className);
        customType.setDescription(description != null ? description : "Custom type from IBM BAW TwClass");
        customType.setBaseType("object");

        // JSON Schema para TwClass
        Map<String, Object> schema = new HashMap<String, Object>();
        schema.put("type", "object");
        schema.put("title", className);
        schema.put("description", customType.getDescription());

        // Propriedades serão adicionadas separadamente
        Map<String, Object> properties = new HashMap<String, Object>();
        schema.put("properties", properties);
        schema.put("additionalProperties", false);

        customType.setJsonSchema(schema);

        // Metadata para TwClass
        customType.getMetadata().getCustomProperties().put("sourceType", "TwClass");
        customType.getMetadata().getCustomProperties().put("originalClassId", classId);

        return customType;
    }

    // =========================================================================
    // UTILITY METHODS
    // =========================================================================

    /**
     * Adicionar propriedade ao tipo
     */
    public void addProperty(String propertyName, String propertyType, String description, boolean required) {
        DataTypeProperty property = new DataTypeProperty();
        property.setName(propertyName);
        property.setType(propertyType);
        property.setDescription(description);
        property.setRequired(required);

        this.properties.put(propertyName, property);

        // Atualizar JSON Schema
        updateJsonSchemaWithProperty(propertyName, propertyType, required);
    }

    /**
     * Adicionar constraint
     */
    public void addConstraint(DataTypeConstraint constraint) {
        if (constraint != null) {
            this.constraints.add(constraint);
        }
    }

    /**
     * Verificar se é tipo primitivo
     */
    public boolean isPrimitive() {
        return baseType != null && (
                "string".equals(baseType) ||
                        "integer".equals(baseType) ||
                        "number".equals(baseType) ||
                        "boolean".equals(baseType)
        );
    }

    /**
     * Verificar se é tipo complexo
     */
    public boolean isComplex() {
        return "object".equals(baseType) && !properties.isEmpty();
    }

    /**
     * Obter tipo simplificado para exibição
     */
    public String getSimpleType() {
        if (isArray) {
            return "Array<" + (arrayItemType != null ? arrayItemType : "Object") + ">";
        }

        if (baseType != null) {
            return baseType;
        }

        return "Object";
    }

    /**
     * Atualizar JSON Schema com nova propriedade
     */
    private void updateJsonSchemaWithProperty(String propertyName, String propertyType, boolean required) {
        if (jsonSchema == null) {
            jsonSchema = new HashMap<String, Object>();
        }

        // Garantir que properties existe no schema
        Object propertiesObj = jsonSchema.get("properties");
        Map<String, Object> schemaProperties;
        if (propertiesObj instanceof Map) {
            schemaProperties = (Map<String, Object>) propertiesObj;
        } else {
            schemaProperties = new HashMap<String, Object>();
            jsonSchema.put("properties", schemaProperties);
        }

        // Adicionar propriedade
        Map<String, Object> propertySchema = new HashMap<String, Object>();
        propertySchema.put("type", propertyType);
        schemaProperties.put(propertyName, propertySchema);

        // Atualizar required array
        if (required) {
            Object requiredObj = jsonSchema.get("required");
            List<String> requiredList;
            if (requiredObj instanceof List) {
                requiredList = (List<String>) requiredObj;
            } else {
                requiredList = new ArrayList<String>();
                jsonSchema.put("required", requiredList);
            }

            if (!requiredList.contains(propertyName)) {
                requiredList.add(propertyName);
            }
        }
    }

    /**
     * Validar definição do tipo
     */
    public List<String> validate() {
        List<String> errors = new ArrayList<String>();

        if (id == null || id.trim().isEmpty()) {
            errors.add("DataType ID is required");
        }

        if (name == null || name.trim().isEmpty()) {
            errors.add("DataType name is required");
        }

        if (isArray && (arrayItemType == null || arrayItemType.trim().isEmpty())) {
            errors.add("Array type must specify arrayItemType");
        }

        if (jsonSchema == null || jsonSchema.isEmpty()) {
            errors.add("JSON Schema is required");
        }

        // Validar constraints
        for (DataTypeConstraint constraint : constraints) {
            if (constraint.getType() == null || constraint.getType().trim().isEmpty()) {
                errors.add("Constraint type is required");
            }
        }

        return errors;
    }

    // =========================================================================
    // GETTERS AND SETTERS
    // =========================================================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getJsonSchema() {
        return jsonSchema != null ? jsonSchema : new HashMap<String, Object>();
    }

    public void setJsonSchema(Map<String, Object> jsonSchema) {
        this.jsonSchema = jsonSchema != null ? jsonSchema : new HashMap<String, Object>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DataTypeMetadata getMetadata() {
        return metadata != null ? metadata : new DataTypeMetadata();
    }

    public void setMetadata(DataTypeMetadata metadata) {
        this.metadata = metadata != null ? metadata : new DataTypeMetadata();
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setIsArray(boolean isArray) {
        this.isArray = isArray;
    }

    public String getArrayItemType() {
        return arrayItemType;
    }

    public void setArrayItemType(String arrayItemType) {
        this.arrayItemType = arrayItemType;
    }

    public Map<String, DataTypeProperty> getProperties() {
        return properties != null ? properties : new HashMap<String, DataTypeProperty>();
    }

    public void setProperties(Map<String, DataTypeProperty> properties) {
        this.properties = properties != null ? properties : new HashMap<String, DataTypeProperty>();
    }

    public List<DataTypeConstraint> getConstraints() {
        return constraints != null ? constraints : new ArrayList<DataTypeConstraint>();
    }

    public void setConstraints(List<DataTypeConstraint> constraints) {
        this.constraints = constraints != null ? constraints : new ArrayList<DataTypeConstraint>();
    }

    // =========================================================================
    // INNER CLASSES
    // =========================================================================

    /**
     * Metadata do DataType
     */
    public static class DataTypeMetadata {
        private String version;
        private String createdAt;
        private String author;
        private Map<String, Object> customProperties;

        public DataTypeMetadata() {
            this.customProperties = new HashMap<String, Object>();
        }

        // Getters and Setters
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public Map<String, Object> getCustomProperties() {
            return customProperties != null ? customProperties : new HashMap<String, Object>();
        }
        public void setCustomProperties(Map<String, Object> customProperties) {
            this.customProperties = customProperties != null ? customProperties : new HashMap<String, Object>();
        }
    }

    /**
     * Propriedade de um DataType
     */
    public static class DataTypeProperty {
        private String name;
        private String type;
        private String description;
        private boolean required;
        private Object defaultValue;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }

        public Object getDefaultValue() { return defaultValue; }
        public void setDefaultValue(Object defaultValue) { this.defaultValue = defaultValue; }
    }

    /**
     * Constraint de um DataType
     */
    public static class DataTypeConstraint {
        private String type;
        private String value;
        private String description;
        private String errorMessage;

        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    // =========================================================================
    // UTILITY AND TEST METHODS
    // =========================================================================

    /**
     * Criar conjunto de tipos padrão do IBM BAW
     */
    public static List<ProcessDataTypeV2Plus> createDefaultBAWTypes() {
        List<ProcessDataTypeV2Plus> defaultTypes = new ArrayList<ProcessDataTypeV2Plus>();

        // Tipos primitivos
        defaultTypes.add(createStringType());
        defaultTypes.add(createIntegerType());
        defaultTypes.add(createBooleanType());
        defaultTypes.add(createObjectType());

        // Tipos específicos do BAW
        ProcessDataTypeV2Plus dateType = createPrimitiveType("dt:date@1", "Date", "string");
        dateType.getJsonSchema().put("format", "date-time");
        defaultTypes.add(dateType);

        ProcessDataTypeV2Plus decimalType = createPrimitiveType("dt:decimal@1", "Decimal", "number");
        defaultTypes.add(decimalType);

        // Tipo TWObject (base para Business Objects)
        ProcessDataTypeV2Plus twObjectType = createFromTwClass("TWObject", "TWObject", "Base type for IBM BAW Business Objects");
        defaultTypes.add(twObjectType);

        return defaultTypes;
    }

    /**
     * Teste de funcionalidade básica
     */
    public static void testDataTypeCreation() {
        System.out.println("🧪 Testing ProcessDataTypeV2Plus...");

        try {
            // Teste 1: Tipo primitivo
            ProcessDataTypeV2Plus stringType = createStringType();
            assert stringType != null : "String type should be created";
            assert "string".equals(stringType.getBaseType()) : "Base type should be string";
            assert !stringType.isArray() : "String type should not be array";
            System.out.println("✅ Primitive type creation: PASSED");

            // Teste 2: Tipo array
            ProcessDataTypeV2Plus arrayType = createArrayType("dt:string@1", "String");
            assert arrayType != null : "Array type should be created";
            assert arrayType.isArray() : "Array type should be array";
            assert "dt:string@1".equals(arrayType.getArrayItemType()) : "Array item type should match";
            System.out.println("✅ Array type creation: PASSED");

            // Teste 3: Tipo complexo
            ProcessDataTypeV2Plus customType = createFromTwClass("Customer", "Customer", "Customer business object");
            customType.addProperty("name", "string", "Customer name", true);
            customType.addProperty("age", "integer", "Customer age", false);

            assert customType.isComplex() : "Custom type should be complex";
            assert customType.getProperties().size() == 2 : "Should have 2 properties";
            System.out.println("✅ Complex type creation: PASSED");

            // Teste 4: Validação
            List<String> errors = customType.validate();
            assert errors.isEmpty() : "Custom type should be valid";
            System.out.println("✅ Type validation: PASSED");

            // Teste 5: Tipos padrão
            List<ProcessDataTypeV2Plus> defaultTypes = createDefaultBAWTypes();
            assert !defaultTypes.isEmpty() : "Should create default types";
            assert defaultTypes.size() >= 5 : "Should have at least 5 default types";
            System.out.println("✅ Default types creation: PASSED");

            System.out.println("🎉 All ProcessDataTypeV2Plus tests passed!");

        } catch (Exception e) {
            System.err.println("❌ ProcessDataTypeV2Plus test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "ProcessDataTypeV2Plus{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", baseType='" + baseType + '\'' +
                ", isArray=" + isArray +
                ", properties=" + properties.size() +
                ", constraints=" + constraints.size() +
                '}';
    }
}