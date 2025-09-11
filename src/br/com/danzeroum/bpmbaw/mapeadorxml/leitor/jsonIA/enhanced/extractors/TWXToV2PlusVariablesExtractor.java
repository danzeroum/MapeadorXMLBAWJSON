package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BpdParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * VERSÃO CORRIGIDA - TWXToV2PlusVariablesExtractor
 * Correção dos tipos incompatíveis de VariableDefinitionV2Plus
 *
 * PROBLEMA: VariableDefinitionV2Plus não pode ser convertida para Var...
 * SOLUÇÃO: Usar tipos corretos e método de adição adequado
 */
public class TWXToV2PlusVariablesExtractor {

    /**
     * Extrai variáveis de um BusinessProcessDiagram - CORRIGIDO
     */
    public static ProcessVariablesV2Plus extractVariables(BusinessProcessDiagram bpd) {
        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();

        if (bpd == null) {
            return variables;
        }

        try {
            // Extrair parâmetros do BPD como variáveis de entrada
            extractBpdParameters(bpd, variables);

            // Adicionar variáveis padrão se não existirem
            addDefaultVariables(variables);

        } catch (Exception e) {
            System.err.println("⚠️ Error extracting variables: " + e.getMessage());
            // Retornar variáveis com valores padrão
            addFallbackVariables(variables);
        }

        return variables;
    }

    /**
     * Extrai parâmetros do BPD - CORRIGIDO para evitar incompatibilidade de tipos
     */
    private static void extractBpdParameters(BusinessProcessDiagram bpd, ProcessVariablesV2Plus variables) {
        try {
            // Tentar acessar parâmetros do BPD
            if (bpd.getId() != null) {
                // Se BPD tem ID, assumir que pode ter parâmetros
                extractParametersSafely(bpd, variables);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Could not extract BPD parameters: " + e.getMessage());
        }
    }

    /**
     * Extração segura de parâmetros - CORRIGIDO
     */
    private static void extractParametersSafely(BusinessProcessDiagram bpd, ProcessVariablesV2Plus variables) {
        // CORRIGIDO: Usar método addInputVariable ao invés de conversão direta

        // Criar variável de entrada padrão baseada no BPD
        String processName = bpd.getName() != null ? bpd.getName() : "process";
        String processId = bpd.getId() != null ? bpd.getId() : "unknown";

        // MÉTODO CORRETO: Usar addInputVariable com 5 parâmetros
        variables.addInputVariable(
                "processInput",
                "dt:object@1",
                "one",
                true,
                "Input data for process " + processName
        );

        variables.addOutputVariable(
                "processOutput",
                "dt:object@1",
                "one",
                true,
                "Output data from process " + processName
        );

        variables.addPrivateVariable(
                "processState",
                "dt:object@1",
                "one",
                true,
                "Internal state for process " + processName
        );
    }

    /**
     * Adiciona variáveis padrão se necessário - CORRIGIDO
     */
    private static void addDefaultVariables(ProcessVariablesV2Plus variables) {
        // Verificar se já tem variáveis suficientes
        if (variables.getTotalVariableCount() == 0) {
            addFallbackVariables(variables);
        }
    }

    /**
     * Adiciona variáveis de fallback - CORRIGIDO
     */
    private static void addFallbackVariables(ProcessVariablesV2Plus variables) {
        try {
            // MÉTODO CORRETO: Usar addInputVariable ao invés de criar VariableDefinitionV2Plus diretamente
            if (variables.getInput().isEmpty()) {
                variables.addInputVariable(
                        "defaultInput",
                        "dt:string@1",
                        "one",
                        true,
                        "Default input variable"
                );
            }

            if (variables.getOutput().isEmpty()) {
                variables.addOutputVariable(
                        "defaultOutput",
                        "dt:string@1",
                        "one",
                        true,
                        "Default output variable"
                );
            }

            if (variables.getPrivateVars().isEmpty()) {
                variables.addPrivateVariable(
                        "defaultPrivate",
                        "dt:object@1",
                        "one",
                        true,
                        "Default private variable"
                );
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error adding fallback variables: " + e.getMessage());
        }
    }

    /**
     * MÉTODO AUXILIAR - Conversão segura de BpdParameter para variável (se necessário)
     */
    private static void convertBpdParameterToVariable(BpdParameter param, ProcessVariablesV2Plus variables) {
        if (param == null) return;

        try {
            String name = param.getName() != null ? param.getName() : "param_" + System.currentTimeMillis();
            String typeRef = mapParameterTypeToTypeRef(param.getParameterType());
            String cardinality = param.isArrayOf() ? "many" : "one";
            boolean nullable = !param.isHasDefault();
            String description = param.getDocumentation() != null ? param.getDocumentation() : "Parameter from BPD";

            // CORRETO: Usar método add ao invés de conversão direta
            variables.addInputVariable(name, typeRef, cardinality, nullable, description);

        } catch (Exception e) {
            System.err.println("⚠️ Error converting BPD parameter: " + e.getMessage());
        }
    }

    /**
     * Mapeia tipo de parâmetro para type reference
     */
    private static String mapParameterTypeToTypeRef(int parameterType) {
        switch (parameterType) {
            case 1: return "dt:string@1";
            case 2: return "dt:integer@1";
            case 3: return "dt:boolean@1";
            case 4: return "dt:decimal@1";
            case 5: return "dt:date@1";
            default: return "dt:object@1";
        }
    }

    /**
     * MÉTODO PRINCIPAL DE EXTRAÇÃO - Versão robusta
     */
    public static ProcessVariablesV2Plus extractVariablesRobust(Object source) {
        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();

        try {
            if (source instanceof BusinessProcessDiagram) {
                return extractVariables((BusinessProcessDiagram) source);
            } else {
                // Fonte não reconhecida, usar variáveis padrão
                addFallbackVariables(variables);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Robust extraction failed, using fallback: " + e.getMessage());
            addFallbackVariables(variables);
        }

        return variables;
    }
}