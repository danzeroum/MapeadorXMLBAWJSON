// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/jsonIA/enhanced/extractors/BpmnToV2PlusVariablesExtractor.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessDefinitionV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessVariablesV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;

import java.util.List;
import java.util.stream.Collectors; // Import necessário

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

        if (definitions.getProcess() != null) {
            extractVariablesFromProcess(definitions.getProcess(), variables);
        }

        if (definitions.getGlobalUserTask() != null) {
            extractVariablesFromIoSpecification(definitions.getGlobalUserTask().getIoSpecification(), variables);
        }
        return variables;
    }

    private static void extractVariablesFromProcess(Process process, ProcessVariablesV2Plus variables) {
        if (process == null) return;

        // 1. Extrai variáveis de Input/Output do processo principal
        extractVariablesFromIoSpecification(process.getIoSpecification(), variables);

        // 2. Extrai DataObjects como variáveis privadas
        extractDataObjectsAsPrivateVars(process.getDataObjects(), variables);

        // 3. Busca recursivamente em SubProcessos por mais DataObjects
        if (process.getFlowElements() != null) {
            for (Object element : process.getFlowElements()) {
                if (element instanceof SubProcess) {
                    extractVariablesFromSubProcess((SubProcess) element, variables);
                }
            }
        }
    }

    /**
     * MÉTODO CORRIGIDO
     * Extrai DataObjects de um SubProcesso, filtrando-os da lista geral de flowElements.
     */
    private static void extractVariablesFromSubProcess(SubProcess subProcess, ProcessVariablesV2Plus variables) {
        if (subProcess == null || subProcess.getFlowElements() == null) return;

        // --- INÍCIO DA CORREÇÃO ---
        // Filtra a lista 'flowElements' para obter apenas os objetos do tipo DataObject.
        List<DataObject> dataObjectsInSubProcess = subProcess.getFlowElements().stream()
                .filter(DataObject.class::isInstance)
                .map(DataObject.class::cast)
                .collect(Collectors.toList());

        // Agora, passa a lista filtrada para o método que sabe como processá-la.
        extractDataObjectsAsPrivateVars(dataObjectsInSubProcess, variables);
        // --- FIM DA CORREÇÃO ---

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
                variables.addInputVariable(
                        input.getName(),
                        ProcessDefinitionV2Plus.VariableDefinitionV2Plus.convertClassIdToTypeRef(input.getItemSubjectRef()),
                        (input.getIsCollection() != null && input.getIsCollection()) ? "many" : "one",
                        false, // Inputs de processo geralmente não são nulos
                        "Variável de entrada do processo."
                );
            }
        }

        // Outputs
        if (ioSpec.getDataOutputs() != null) {
            for (DataOutput output : ioSpec.getDataOutputs()) {
                variables.addOutputVariable(
                        output.getName(),
                        ProcessDefinitionV2Plus.VariableDefinitionV2Plus.convertClassIdToTypeRef(output.getItemSubjectRef()),
                        (output.getIsCollection() != null && output.getIsCollection()) ? "many" : "one",
                        true, // Outputs podem ser nulos até serem preenchidos
                        "Variável de saída do processo."
                );
            }
        }
    }

    private static void extractDataObjectsAsPrivateVars(List<DataObject> dataObjects, ProcessVariablesV2Plus variables) {
        if (dataObjects != null) {
            for (DataObject dataObject : dataObjects) {
                variables.addPrivateVariable(
                        dataObject.getName(),
                        ProcessDefinitionV2Plus.VariableDefinitionV2Plus.convertClassIdToTypeRef(dataObject.getItemSubjectRef()),
                        dataObject.isCollection() ? "many" : "one",
                        true, // Variáveis privadas podem começar nulas
                        "Variável privada (DataObject)."
                );
            }
        }
    }
}