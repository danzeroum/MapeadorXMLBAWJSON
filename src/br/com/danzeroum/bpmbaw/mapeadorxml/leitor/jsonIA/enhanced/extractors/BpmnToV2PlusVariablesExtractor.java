package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessDefinitionV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessVariablesV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
import java.util.List;

/**
 * Extrator de Variáveis BPMN 2.0 (Versão Final)
 * Realiza uma busca profunda em todos os elementos, incluindo UserTasks, CallActivities
 * e SubProcesses para uma extração completa de variáveis.
 * @version 3.0 - Full Content Extraction
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

        extractVariablesFromIoSpecification(process.getIoSpecification(), variables);
        extractDataObjectsAsPrivateVars(process.getDataObjects(), variables);

        if (process.getFlowElements() != null) {
            for (Object element : process.getFlowElements()) {
                if (element instanceof SubProcess) {
                    // Delega para um método que sabe lidar com a estrutura do SubProcess.
                    extractVariablesFromSubProcess((SubProcess) element, variables);
                } else if (element instanceof GlobalUserTask) { // Comum em Serviços Humanos
                    extractVariablesFromIoSpecification(((GlobalUserTask) element).getIoSpecification(), variables);
                } else if (element instanceof CallActivity) {
                    // Extrai variáveis dos mapeamentos da CallActivity
                    extractVariablesFromCallActivity((CallActivity) element, variables);
                }
            }
        }
    }

    private static void extractVariablesFromSubProcess(SubProcess subProcess, ProcessVariablesV2Plus variables) {
        if (subProcess == null) return;
        // Um SubProcess pode conter seus próprios DataObjects e IoSpecification
        // (Esta parte pode ser expandida se SubProcess tiver IoSpecification no seu modelo)

        // A lógica principal é a busca recursiva.
        if (subProcess.getFlowElements() != null) {
            for (Object element : subProcess.getFlowElements()) {
                if (element instanceof SubProcess) {
                    extractVariablesFromSubProcess((SubProcess) element, variables);
                } else if (element instanceof GlobalUserTask) {
                    extractVariablesFromIoSpecification(((GlobalUserTask) element).getIoSpecification(), variables);
                } else if (element instanceof CallActivity) {
                    extractVariablesFromCallActivity((CallActivity) element, variables);
                }
            }
        }
    }

    /**
     * NOVO: Extrai variáveis usadas nos mapeamentos de uma CallActivity.
     */
    private static void extractVariablesFromCallActivity(CallActivity callActivity, ProcessVariablesV2Plus variables) {
        if (callActivity == null) return;

        // Mapeamentos de Entrada (Source)
        if (callActivity.getDataInputAssociations() != null) {
            for (DataInputAssociation dia : callActivity.getDataInputAssociations()) {
                if (dia.getAssignment() != null && dia.getAssignment().getFrom() != null) {
                    String varName = dia.getAssignment().getFrom().getExpression();
                    // Adiciona como variável privada, pois é usada internamente para o mapeamento
                    variables.addPrivateVariable(varName, "dt:ANY@1", "one", true, "Variável de origem para mapeamento em '" + callActivity.getName() + "'");
                }
            }
        }

        // Mapeamentos de Saída (Target)
        if (callActivity.getDataOutputAssociations() != null) {
            for (DataOutputAssociation doa : callActivity.getDataOutputAssociations()) {
                if (doa.getAssignment() != null && doa.getAssignment().getTo() != null) {
                    String varName = doa.getAssignment().getTo().getContent();
                    variables.addPrivateVariable(varName, "dt:ANY@1", "one", true, "Variável de destino para mapeamento em '" + callActivity.getName() + "'");
                }
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
                        false,
                        "Variável de entrada."
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
                        false,
                        "Variável de saída."
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
                        true,
                        "Variável privada (DataObject)."
                );
            }
        }
    }
}