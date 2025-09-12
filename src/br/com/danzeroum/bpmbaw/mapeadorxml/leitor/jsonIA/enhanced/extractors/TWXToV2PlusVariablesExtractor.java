package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Pool;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BpdParameter;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.PrivateVariable;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.Property;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * VERSÃO CONTEXT-AWARE - TWXToV2PlusVariablesExtractor
 * Extrai variáveis e agora também os Data Types, utilizando o ProcessLoader
 * para resolver as referências de tipo (classId).
 */
public class TWXToV2PlusVariablesExtractor {

    private final ProcessLoaderV2Plus loader;
    private final Set<String> processedDataTypeIds = new HashSet<>();

    // Construtor que recebe o loader
    public TWXToV2PlusVariablesExtractor(ProcessLoaderV2Plus loader) {
        this.loader = loader;
    }

    /**
     * Extrai variáveis de um BusinessProcessDiagram - CORRIGIDO
     * A correção move a lógica de extração de BpdParameter para o nível correto do BPD,
     * em vez de procurar erroneamente dentro de cada Pool.
     */
    public static ProcessVariablesV2Plus extractVariables(BusinessProcessDiagram bpd) {
        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();

        if (bpd == null) {
            addFallbackVariables(variables);
            return variables;
        }

        try {
            // Tenta obter a lista de parâmetros do BPD.
            // O cast é necessário porque o método pode não existir em versões mais antigas do modelo.
            Object bpdParamsObj = bpd.getClass().getMethod("getBpdParameters").invoke(bpd);

            if (bpdParamsObj instanceof List) {
                List<?> bpdParameters = (List<?>) bpdParamsObj;
                System.out.println("   📄 Found " + bpdParameters.size() + " BPD parameters to process.");

                for (Object paramObj : bpdParameters) {
                    if (paramObj instanceof BpdParameter) {
                        BpdParameter param = (BpdParameter) paramObj;
                        // Converte o parâmetro para a nossa estrutura V2Plus
                        ProcessDefinitionV2Plus.VariableDefinitionV2Plus varDef = convertBpdParameterToVariable(param);

                        // Adiciona à lista de input ou output
                        if (param.getParameterType() == 1) { // 1 = Input
                            variables.addInputVariable(varDef.getName(), varDef.getTypeRef(), varDef.getCardinality(), varDef.isNullable(), varDef.getDescription());
                        } else { // 2 = Output
                            variables.addOutputVariable(varDef.getName(), varDef.getTypeRef(), varDef.getCardinality(), varDef.isNullable(), varDef.getDescription());
                        }
                    }
                }
            }

            // A lógica para variáveis privadas dentro dos Pools já está correta.
            if (bpd.getPools() != null) {
                for (Pool pool : bpd.getPools()) {
                    if (pool.getPrivateVariables() != null) {
                        for (PrivateVariable pVar : pool.getPrivateVariables()) {
                            ProcessDefinitionV2Plus.VariableDefinitionV2Plus varDef = createVarDefFromPrivateVariable(pVar);
                            variables.addPrivateVariable(varDef.getName(), varDef.getTypeRef(), varDef.getCardinality(), varDef.isNullable(), varDef.getDescription());
                        }
                    }
                }
            }

            // Adiciona variáveis padrão apenas se nenhuma for encontrada.
            if (variables.getTotalVariableCount() == 0) {
                addDefaultVariables(variables);
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error extracting variables, using fallback: " + e.getMessage());
            addFallbackVariables(variables);
        }

        return variables;
    }


    private static void addDefaultVariables(ProcessVariablesV2Plus variables) {
        if (variables.getTotalVariableCount() == 0) {
            addFallbackVariables(variables);
        }
    }
    /**
     * NOVO MÉTODO AUXILIAR: Converte BpdParameter para VariableDefinitionV2Plus
     */
    private static ProcessDefinitionV2Plus.VariableDefinitionV2Plus convertBpdParameterToVariable(BpdParameter param) {
        String typeRef = convertClassIdToTypeRef(param.getClassId());
        String cardinality = param.isArrayOf() ? "many" : "one";
        return new ProcessDefinitionV2Plus.VariableDefinitionV2Plus(
                param.getName(),
                typeRef,
                cardinality,
                !param.isHasDefault(),
                param.getDocumentation()
        );
    }

    /**
     * NOVO MÉTODO AUXILIAR: Converte PrivateVariable para VariableDefinitionV2Plus
     */
    private static ProcessDefinitionV2Plus.VariableDefinitionV2Plus createVarDefFromPrivateVariable(PrivateVariable pVar) {
        return new ProcessDefinitionV2Plus.VariableDefinitionV2Plus(
                pVar.getName(),
                convertClassIdToTypeRef(pVar.getClassId()),
                pVar.isArrayOf() ? "many" : "one",
                !pVar.isHasDefault(),
                "Private variable"
        );
    }
    /**
     * Extrai as definições de tipo de dados (DataTypes) a partir das variáveis já extraídas.
     */
    public List<DataTypeDefinitionV2Plus> extractDataTypeDefinitions(ProcessVariablesV2Plus variables) {
        List<DataTypeDefinitionV2Plus> dataTypes = new ArrayList<>();
        processedDataTypeIds.clear(); // Limpa para garantir nova execução

        // Adiciona tipos primitivos padrão para garantir que sempre existam
        addPrimitiveDataTypes(dataTypes);

        // Processa todos os typeRefs de todas as variáveis
        variables.getInput().forEach(var -> processVariableType(var.getTypeRef(), dataTypes));
        variables.getOutput().forEach(var -> processVariableType(var.getTypeRef(), dataTypes));
        variables.getPrivateVars().forEach(var -> processVariableType(var.getTypeRef(), dataTypes));

        return dataTypes;
    }

    private void processVariableType(String typeRef, List<DataTypeDefinitionV2Plus> dataTypes) {
        if (typeRef == null || processedDataTypeIds.contains(typeRef)) {
            return;
        }

        String classId = typeRef.replace("dt:", "").replace("@1", ""); // Simplifica o typeRef para o ID do loader

        Object artifact = loader.getArtefatoDoCache(classId);
        if (artifact instanceof Teamworks && ((Teamworks) artifact).getTwClass() != null) {
            TwClass twClass = ((Teamworks) artifact).getTwClass();

            DataTypeDefinitionV2Plus dataType = new DataTypeDefinitionV2Plus();
            dataType.setId(typeRef); // Usa o typeRef como ID
            dataType.setName(twClass.getName());
            dataType.setDescription(twClass.getDescription());

            // Processa as propriedades do TwClass
            if (twClass.getDefinition() != null && twClass.getDefinition().getProperties() != null) {
                for (Property prop : twClass.getDefinition().getProperties()) {
                    // Recursivamente processa os tipos das propriedades para garantir que todas as dependências sejam adicionadas
                    processVariableType(convertClassIdToTypeRef(prop.getClassRef()), dataTypes);
                }
            }
            dataTypes.add(dataType);
            processedDataTypeIds.add(typeRef);
        }
    }


    // --- Métodos de conversão ---

    private ProcessDefinitionV2Plus.VariableDefinitionV2Plus createVarDefFromBpdParameter(BpdParameter param) {
        return new ProcessDefinitionV2Plus.VariableDefinitionV2Plus(
                param.getName(),
                convertClassIdToTypeRef(param.getClassId()),
                param.isArrayOf() ? "many" : "one",
                !param.isHasDefault(),
                param.getDocumentation()
        );
    }


    private static String convertClassIdToTypeRef(String classId) {
        if (classId == null) return "dt:string@1";
        switch (classId) {
            case "String": return "dt:string@1";
            case "Integer": return "dt:integer@1";
            case "Boolean": return "dt:boolean@1";
            case "Decimal": return "dt:decimal@1";
            case "Date": return "dt:date@1";
            default: return "dt:" + classId + "@1";
        }
    }

    private void addPrimitiveDataTypes(List<DataTypeDefinitionV2Plus> dataTypes) {
        if (processedDataTypeIds.add("dt:string@1")) {
            dataTypes.add(new DataTypeDefinitionV2Plus("dt:string@1", "String", "String primitive type"));
        }
        if (processedDataTypeIds.add("dt:integer@1")) {
            dataTypes.add(new DataTypeDefinitionV2Plus("dt:integer@1", "Integer", "Integer primitive type"));
        }
        if (processedDataTypeIds.add("dt:boolean@1")) {
            dataTypes.add(new DataTypeDefinitionV2Plus("dt:boolean@1", "Boolean", "Boolean primitive type"));
        }
        if (processedDataTypeIds.add("dt:decimal@1")) {
            dataTypes.add(new DataTypeDefinitionV2Plus("dt:decimal@1", "Decimal", "Decimal primitive type"));
        }
        if (processedDataTypeIds.add("dt:date@1")) {
            dataTypes.add(new DataTypeDefinitionV2Plus("dt:date@1", "Date", "Date primitive type"));
        }
        if (processedDataTypeIds.add("dt:object@1")) {
            dataTypes.add(new DataTypeDefinitionV2Plus("dt:object@1", "Object", "Generic object type"));
        }
    }


    private static void addFallbackVariables(ProcessVariablesV2Plus variables) {
        variables.addInputVariable("defaultInput", "dt:string@1", "one", true, "Default input variable");
        variables.addOutputVariable("defaultOutput", "dt:string@1", "one", true, "Default output variable");
        variables.addPrivateVariable("defaultPrivate", "dt:object@1", "one", true, "Default private variable");
    }
}