package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Pool;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BpdParameter;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.PrivateVariable;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.Property;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass;

import java.util.*;

/**
 * VERSÃO FINAL - TWXToV2PlusVariablesExtractor
 * Extrai variáveis enriquecidas e Data Types com JSON Schema, implementando
 * detecção de ciclos, cache e fallbacks robustos, conforme a estratégia definida.
 *
 * @version 3.1.0
 */
public class TWXToV2PlusVariablesExtractor {

    private final ProcessLoaderV2Plus loader;
    private final Set<String> processedDataTypeIds = new HashSet<>();
    private final Set<String> currentlyProcessing = new HashSet<>(); // Para detecção de ciclo
    private final Map<String, DataTypeDefinitionV2Plus> dataTypeCache = new HashMap<>(); // Cache de performance

    public TWXToV2PlusVariablesExtractor(ProcessLoaderV2Plus loader) {
        this.loader = loader;
    }

    /**
     * Extrai as variáveis (input, output, private) de um Bpd, utilizando
     * os métodos de conversão da classe VariableDefinitionV2Plus.
     */
    public ProcessVariablesV2Plus extractVariables(Bpd bpd) {
        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();
        if (bpd == null) {
            return variables;
        }

        // Extrai Parâmetros de Entrada/Saída do BPD
        if (bpd.getBpdParameters() != null) {
            for (BpdParameter param : bpd.getBpdParameters()) {
                ProcessDefinitionV2Plus.VariableDefinitionV2Plus varDef = ProcessDefinitionV2Plus.VariableDefinitionV2Plus.fromBpdParameter(param);
                if (param.getParameterType() == 1) { // Input
                    variables.addInputVariable(varDef.getName(), varDef.getTypeRef(), varDef.getCardinality(), varDef.isNullable(), varDef.getDescription());
                } else { // Output
                    variables.addOutputVariable(varDef.getName(), varDef.getTypeRef(), varDef.getCardinality(), varDef.isNullable(), varDef.getDescription());
                }
            }
        }

        // Extrai Variáveis Privadas dos Pools
        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();
        if (diagram != null && diagram.getPools() != null) {
            for (Pool pool : diagram.getPools()) {
                if (pool.getPrivateVariables() != null) {
                    for (PrivateVariable pVar : pool.getPrivateVariables()) {
                        ProcessDefinitionV2Plus.VariableDefinitionV2Plus varDef = ProcessDefinitionV2Plus.VariableDefinitionV2Plus.fromPrivateVariable(pVar);
                        variables.addPrivateVariable(varDef.getName(), varDef.getTypeRef(), varDef.getCardinality(), varDef.isNullable(), varDef.getDescription());
                    }
                }
            }
        }
        return variables;
    }

    /**
     * Ponto de entrada para extrair todas as definições de tipo de dados.
     */
    public List<DataTypeDefinitionV2Plus> extractDataTypeDefinitions(ProcessVariablesV2Plus variables) {
        List<DataTypeDefinitionV2Plus> dataTypes = new ArrayList<>();
        processedDataTypeIds.clear();
        addPrimitiveDataTypes(dataTypes); // Garante que os tipos básicos sempre existam

        variables.getInput().forEach(var -> processVariableType(var.getTypeRef(), dataTypes));
        variables.getOutput().forEach(var -> processVariableType(var.getTypeRef(), dataTypes));
        variables.getPrivateVars().forEach(var -> processVariableType(var.getTypeRef(), dataTypes));

        return dataTypes;
    }

    /**
     * Processa um tipo de variável de forma recursiva, com proteções.
     */
    private void processVariableType(String typeRef, List<DataTypeDefinitionV2Plus> dataTypes) {
        if (typeRef == null || processedDataTypeIds.contains(typeRef)) {
            return; // Já processado ou inválido
        }

        // CRÍTICO: Detecção de ciclo, como sugerido
        if (currentlyProcessing.contains(typeRef)) {
            System.err.println("⚠️ Cycle detected for typeRef: " + typeRef + ". Skipping recursive processing.");
            return;
        }

        // Otimização: Usa o cache se o tipo já foi gerado
        if (dataTypeCache.containsKey(typeRef)) {
            if (!processedDataTypeIds.contains(typeRef)) {
                dataTypes.add(dataTypeCache.get(typeRef));
                processedDataTypeIds.add(typeRef);
            }
            return;
        }

        currentlyProcessing.add(typeRef);
        try {
            String classId = typeRef.replace("dt:", "").replaceAll("@\\d+$", "");
            Object artifact = loader.getArtefatoDoCache(classId);

            if (artifact instanceof Teamworks && ((Teamworks) artifact).getTwClass() != null) {
                TwClass twClass = ((Teamworks) artifact).getTwClass();

                DataTypeDefinitionV2Plus dataType = new DataTypeDefinitionV2Plus();
                dataType.setId(typeRef);
                dataType.setName(twClass.getName());
                dataType.setDescription(twClass.getDescription());
                dataType.setJsonSchema(createJsonSchemaFromTwClass(twClass));

                dataTypes.add(dataType);
                processedDataTypeIds.add(typeRef);
                dataTypeCache.put(typeRef, dataType); // Adiciona ao cache

                // Processa recursivamente os tipos das propriedades internas
                if (twClass.getDefinition() != null && twClass.getDefinition().getProperties() != null) {
                    for (Property prop : twClass.getDefinition().getProperties()) {
                        processVariableType(ProcessDefinitionV2Plus.VariableDefinitionV2Plus.convertClassIdToTypeRef(prop.getClassRef()), dataTypes);
                    }
                }
            }
        } finally {
            currentlyProcessing.remove(typeRef); // Garante a limpeza para futuras chamadas
        }
    }

    /**
     * Cria a estrutura JSON Schema a partir de um TwClass, com validação.
     */
    private Map<String, Object> createJsonSchemaFromTwClass(TwClass twClass) {
        if (twClass == null) return createEmptySchema("Null TwClass object provided.");

        try {
            Map<String, Object> schema = new HashMap<>();
            schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
            schema.put("type", "object");
            schema.put("title", twClass.getName());
            schema.put("description", twClass.getDescription());

            Map<String, Object> properties = new HashMap<>();
            List<String> required = new ArrayList<>();

            if (twClass.getDefinition() != null && twClass.getDefinition().getProperties() != null) {
                for (Property prop : twClass.getDefinition().getProperties()) {
                    if(prop.getName() != null) { // Validação de propriedade malformada
                        properties.put(prop.getName(), createPropertySchema(prop));
                        if (prop.isPropertyRequired()) {
                            required.add(prop.getName());
                        }
                    }
                }
            }
            schema.put("properties", properties);
            if (!required.isEmpty()) {
                schema.put("required", required);
            }
            return schema;
        } catch (Exception e) {
            System.err.println("Error creating JSON schema for " + twClass.getName() + ": " + e.getMessage());
            return createFallbackSchema(twClass.getName());
        }
    }

    private Map<String, Object> createPropertySchema(Property prop) {
        Map<String, Object> propSchema = new HashMap<>();
        String typeRef = ProcessDefinitionV2Plus.VariableDefinitionV2Plus.convertClassIdToTypeRef(prop.getClassRef());

        if (prop.isArrayProperty()) {
            propSchema.put("type", "array");
            Map<String, String> items = new HashMap<>();
            // A referência aponta para a definição de tipo que será criada
            items.put("$ref", "#/definitions/" + typeRef);
            propSchema.put("items", items);
        } else {
            propSchema.put("$ref", "#/definitions/" + typeRef);
        }
        propSchema.put("description", prop.getDescription());
        return propSchema;
    }

    private Map<String, Object> createEmptySchema(String reason) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("description", "Schema could not be generated: " + reason);
        return schema;
    }

    private Map<String, Object> createFallbackSchema(String name) {
        return createEmptySchema("Error during property processing for " + name);
    }

    /**
     * COMPLETO: Adiciona as definições de todos os tipos primitivos padrão do BAW.
     */
    private void addPrimitiveDataTypes(List<DataTypeDefinitionV2Plus> dataTypes) {
        // String
        if (processedDataTypeIds.add("dt:String@1")) {
            DataTypeDefinitionV2Plus stringType = new DataTypeDefinitionV2Plus("dt:String@1", "String", "Primitive string type.");
            Map<String, Object> schema = new HashMap<>();
            schema.put("type", "string");
            stringType.setJsonSchema(schema);
            dataTypes.add(stringType);
            dataTypeCache.put("dt:String@1", stringType);
        }
        // Integer
        if (processedDataTypeIds.add("dt:Integer@1")) {
            DataTypeDefinitionV2Plus intType = new DataTypeDefinitionV2Plus("dt:Integer@1", "Integer", "Primitive integer type.");
            Map<String, Object> schema = new HashMap<>();
            schema.put("type", "integer");
            intType.setJsonSchema(schema);
            dataTypes.add(intType);
            dataTypeCache.put("dt:Integer@1", intType);
        }
        // Boolean
        if (processedDataTypeIds.add("dt:Boolean@1")) {
            DataTypeDefinitionV2Plus boolType = new DataTypeDefinitionV2Plus("dt:Boolean@1", "Boolean", "Primitive boolean type.");
            Map<String, Object> schema = new HashMap<>();
            schema.put("type", "boolean");
            boolType.setJsonSchema(schema);
            dataTypes.add(boolType);
            dataTypeCache.put("dt:Boolean@1", boolType);
        }
        // Decimal
        if (processedDataTypeIds.add("dt:Decimal@1")) {
            DataTypeDefinitionV2Plus decimalType = new DataTypeDefinitionV2Plus("dt:Decimal@1", "Decimal", "Primitive decimal type.");
            Map<String, Object> schema = new HashMap<>();
            schema.put("type", "number");
            decimalType.setJsonSchema(schema);
            dataTypes.add(decimalType);
            dataTypeCache.put("dt:Decimal@1", decimalType);
        }
        // Date
        if (processedDataTypeIds.add("dt:Date@1")) {
            DataTypeDefinitionV2Plus dateType = new DataTypeDefinitionV2Plus("dt:Date@1", "Date", "Primitive date type.");
            Map<String, Object> schema = new HashMap<>();
            schema.put("type", "string");
            schema.put("format", "date-time");
            dateType.setJsonSchema(schema);
            dataTypes.add(dateType);
            dataTypeCache.put("dt:Date@1", dateType);
        }
        // ANY (Object)
        if (processedDataTypeIds.add("dt:ANY@1")) {
            DataTypeDefinitionV2Plus anyType = new DataTypeDefinitionV2Plus("dt:ANY@1", "ANY", "Generic object type (ANY).");
            Map<String, Object> schema = new HashMap<>();
            schema.put("type", "object");
            schema.put("description", "Can be any type of object.");
            anyType.setJsonSchema(schema);
            dataTypes.add(anyType);
            dataTypeCache.put("dt:ANY@1", anyType);
        }
    }
}