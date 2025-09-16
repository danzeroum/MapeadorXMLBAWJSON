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

    public List<DataTypeDefinitionV2Plus> extractDataTypeDefinitions(ProcessVariablesV2Plus variables) {
        List<DataTypeDefinitionV2Plus> dataTypes = new ArrayList<>();
        processedDataTypeIds.clear(); // Limpa o controle para uma nova execução
        addPrimitiveDataTypes(dataTypes); // Garante que os tipos básicos sempre existam

        // Itera sobre todas as variáveis (entrada, saída e privadas) e processa seus tipos
        variables.getInput().forEach(var -> processVariableType(var.getTypeRef(), dataTypes));
        variables.getOutput().forEach(var -> processVariableType(var.getTypeRef(), dataTypes));
        variables.getPrivateVars().forEach(var -> processVariableType(var.getTypeRef(), dataTypes));

        return dataTypes;
    }


    private void processVariableType(String typeRef, List<DataTypeDefinitionV2Plus> dataTypes) {
        System.out.println("[DEBUG-VARS] A processar TypeRef: " + typeRef); // LOG 1
        if (typeRef == null || processedDataTypeIds.contains(typeRef)) {
            if(typeRef != null) System.out.println("[DEBUG-VARS] -> Já processado ou nulo. A saltar."); // LOG 2
            return;
        }

        // Proteção contra recursão infinita (ciclos)
        if (currentlyProcessing.contains(typeRef)) {
            System.err.println("⚠️ Ciclo detectado para o tipo: " + typeRef + ". Pulando processamento recursivo.");
            return;
        }

        // Otimização de performance com cache
        if (dataTypeCache.containsKey(typeRef)) {
            if (!processedDataTypeIds.contains(typeRef)) {
                dataTypes.add(dataTypeCache.get(typeRef));
                processedDataTypeIds.add(typeRef);
            }
            return;
        }

        currentlyProcessing.add(typeRef);
        try {
            String classIdWithPrefix  = typeRef.replace("dt:", "").replaceAll("@\\d+$", "");
            String classId = classIdWithPrefix.startsWith("itm.") ? classIdWithPrefix.substring(4) : classIdWithPrefix;
            System.out.println("[DEBUG-VARS] -> ID do Artefacto a procurar no cache: " + classId); // LOG 3
            Object artifact = loader.getArtefatoDoCache(classId);

            if (artifact != null) {
                System.out.println("[DEBUG-VARS] -> Artefacto encontrado no cache! Tipo: " + artifact.getClass().getName()); // LOG 4
            } else {
                System.out.println("[DEBUG-VARS] -> AVISO: Artefacto NÃO encontrado no cache para o ID: " + classId); // LOG 5
            }

            if (artifact instanceof Teamworks && ((Teamworks) artifact).getTwClass() != null) {
                TwClass twClass = ((Teamworks) artifact).getTwClass();

                DataTypeDefinitionV2Plus dataType = new DataTypeDefinitionV2Plus();
                dataType.setId(typeRef);
                dataType.setName(twClass.getName());
                dataType.setDescription(twClass.getDescription());
                dataType.setJsonSchema(createJsonSchemaFromTwClass(twClass, dataTypes)); // Passa a lista para recursão

                dataTypes.add(dataType);
                processedDataTypeIds.add(typeRef);
                dataTypeCache.put(typeRef, dataType); // Adiciona ao cache
            }
        } finally {
            currentlyProcessing.remove(typeRef); // Libera o tipo do controle de ciclo
        }
    }

    private Map<String, Object> createJsonSchemaFromTwClass(TwClass twClass, List<DataTypeDefinitionV2Plus> dataTypes) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
        schema.put("type", "object");
        schema.put("title", twClass.getName());
        schema.put("description", twClass.getDescription());

        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        if (twClass.getDefinition() != null && twClass.getDefinition().getProperties() != null) {
            for (Property prop : twClass.getDefinition().getProperties()) {
                if(prop.getName() != null) {
                    properties.put(prop.getName(), createPropertySchema(prop, dataTypes));
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
    }

    private Map<String, Object> createPropertySchema(Property prop, List<DataTypeDefinitionV2Plus> dataTypes) {
        Map<String, Object> propSchema = new HashMap<>();
        String propertyTypeRef = ProcessDefinitionV2Plus.VariableDefinitionV2Plus.convertClassIdToTypeRef(prop.getClassRef());

        // Processa o tipo da propriedade de forma recursiva para garantir que ele seja definido
        processVariableType(propertyTypeRef, dataTypes);

        if (prop.isArrayProperty()) {
            propSchema.put("type", "array");
            Map<String, String> items = new HashMap<>();
            items.put("$ref", "#/definitions/" + propertyTypeRef); // Referência ao tipo aninhado
            propSchema.put("items", items);
        } else {
            propSchema.put("$ref", "#/definitions/" + propertyTypeRef);
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
        // Lista de tipos primitivos para adicionar
        String[] primitives = {"String", "Integer", "Boolean", "Decimal", "Date", "ANY"};

        for (String primitive : primitives) {
            String typeRef = "dt:" + primitive + "@1";
            if (processedDataTypeIds.add(typeRef)) {
                DataTypeDefinitionV2Plus primitiveType = new DataTypeDefinitionV2Plus(typeRef, primitive, "Primitive " + primitive.toLowerCase() + " type.");
                Map<String, Object> schema = new HashMap<>();

                switch(primitive) {
                    case "Integer": schema.put("type", "integer"); break;
                    case "Boolean": schema.put("type", "boolean"); break;
                    case "Decimal": schema.put("type", "number"); break;
                    case "Date":
                        schema.put("type", "string");
                        schema.put("format", "date-time");
                        break;
                    case "ANY": schema.put("type", "object"); break;
                    default: schema.put("type", "string"); break;
                }

                primitiveType.setJsonSchema(schema);
                dataTypes.add(primitiveType);
                dataTypeCache.put(typeRef, primitiveType);
            }
        }
    }
}