// Em: br/com/danzeroum/bpmbaw/mapeadorxml/leitor/jsonIA/enhanced/extractors/BpmnToV2PlusVariablesExtractor.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessDefinitionV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessVariablesV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Extrator de Variáveis BPMN 2.0 (Versão Final e Aprimorada)
 * Focado em extrair APENAS declarações de variáveis (Inputs, Outputs, DataObjects)
 * e IGNORAR mapeamentos de atividades, deixando essa responsabilidade para o extrator de Mappings.
 * @version 3.2 - SubProcess DataObject Extraction Fix
 */
public class BpmnToV2PlusVariablesExtractor {

    public static ProcessVariablesV2Plus extractVariables(Definitions definitions) {
        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();
        if (definitions == null) {
            return variables;
        }

        // 1. Extrai variáveis do Processo Principal
        if (definitions.getProcess() != null) {
            extractVariablesFromProcess(definitions.getProcess(), variables);
        }

        // 2. Extrai variáveis da GlobalUserTask (comum em Coach Flows)
        if (definitions.getGlobalUserTask() != null) {
            System.out.println("    [LOG-BPMN-VARS] Encontrada GlobalUserTask, extraindo suas variáveis de I/O.");
            extractVariablesFromIoSpecification(definitions.getGlobalUserTask().getIoSpecification(), variables);
        }
        return variables;
    }

    private static void extractVariablesFromProcess(Process process, ProcessVariablesV2Plus variables) {
        if (process == null) return;
        System.out.println("    [LOG-BPMN-VARS] Procurando variáveis em IoSpecification e DataObjects para o processo: " + process.getName());

        // Extrai variáveis de Input/Output do processo principal
        extractVariablesFromIoSpecification(process.getIoSpecification(), variables);

        // Extrai DataObjects do nível principal como variáveis privadas
        int dataObjectCount = (process.getDataObjects() != null) ? process.getDataObjects().size() : 0;
        System.out.println("    [LOG-BPMN-VARS] Encontrados " + dataObjectCount + " DataObjects no nível principal.");
        extractDataObjectsAsPrivateVars(process.getDataObjects(), variables);

        // Busca recursivamente em SubProcessos por mais DataObjects
        if (process.getFlowElements() != null) {
            for (Object element : process.getFlowElements()) {
                if (element instanceof SubProcess) {
                    System.out.println("    [LOG-BPMN-VARS] Entrando em SubProcesso aninhado para buscar mais variáveis: " + ((SubProcess) element).getName());
                    extractVariablesFromSubProcess((SubProcess) element, variables);
                }
            }
        }
    }

    private static void extractVariablesFromSubProcess(SubProcess subProcess, ProcessVariablesV2Plus variables) {
        if (subProcess == null || subProcess.getFlowElements() == null) return;

        // Filtra a lista 'flowElements' para obter apenas os objetos do tipo DataObject.
        List<DataObject> dataObjectsInSubProcess = subProcess.getFlowElements().stream()
                .filter(DataObject.class::isInstance)
                .map(DataObject.class::cast)
                .collect(Collectors.toList());

        if (!dataObjectsInSubProcess.isEmpty()) {
            System.out.println("    [LOG-BPMN-VARS-SUB] Encontrados " + dataObjectsInSubProcess.size() + " DataObjects no SubProcesso: " + subProcess.getName());
        }

        extractDataObjectsAsPrivateVars(dataObjectsInSubProcess, variables);

        // Continua a busca recursiva por outros SubProcessos aninhados.
        for (Object element : subProcess.getFlowElements()) {
            if (element instanceof SubProcess) {
                extractVariablesFromSubProcess((SubProcess) element, variables);
            }
        }
    }

    private static void extractVariablesFromIoSpecification(IoSpecification ioSpec, ProcessVariablesV2Plus variables) {
        if (ioSpec == null) return;

        // Inputs
        if (ioSpec.getDataInputs() != null) {
            for (DataInput input : ioSpec.getDataInputs()) {

                // ===== CORREÇÃO APLICADA AQUI =====
                // A assinatura correta é: (String name, String type, String description, boolean isList)
                // O argumento de cardinalidade ("many"/"one") foi removido.
                variables.addInputVariable(
                        input.getName(),
                        ProcessDefinitionV2Plus.VariableDefinitionV2Plus.convertClassIdToTypeRef(input.getItemSubjectRef()),
                        "Variável de entrada do processo.",
                        (input.getIsCollection() != null && input.getIsCollection())
                );
            }
        }

        // Outputs
        if (ioSpec.getDataOutputs() != null) {
            for (DataOutput output : ioSpec.getDataOutputs()) {

                // ===== CORREÇÃO APLICADA AQUI =====
                // A assinatura correta é: (String name, String type, String description, boolean isList)
                variables.addOutputVariable(
                        output.getName(),
                        ProcessDefinitionV2Plus.VariableDefinitionV2Plus.convertClassIdToTypeRef(output.getItemSubjectRef()),
                        "Variável de saída do processo.",
                        (output.getIsCollection() != null && output.getIsCollection())
                );
            }
        }
    }

    private static void extractDataObjectsAsPrivateVars(List<DataObject> dataObjects, ProcessVariablesV2Plus variables) {
        if (dataObjects != null) {
            for (DataObject dataObject : dataObjects) {

                // ===== CORREÇÃO APLICADA AQUI =====
                // A assinatura correta é: (String name, String type, String description, boolean isList)
                variables.addPrivateVariable(
                        dataObject.getName(),
                        ProcessDefinitionV2Plus.VariableDefinitionV2Plus.convertClassIdToTypeRef(dataObject.getItemSubjectRef()),
                        "Variável privada (DataObject).",
                        dataObject.isCollection()
                );
            }
        }
    }

}