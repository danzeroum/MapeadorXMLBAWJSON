package br.com.danzeroum.bpmbaw.mapeadorxml.modelo;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessDefinitionV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessVariablesV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BpdParameter;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Pool;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.PrivateVariable;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;

import java.util.List;
import java.util.stream.Collectors;

/**
 * (VERSÃO CORRIGIDA)
 * Infere e preenche os detalhes completos das variáveis do processo.
 * Agora suporta tanto processos legados (Bpd) quanto modernos (Definitions).
 */
public class VariableInferer {

    /**
     * Ponto de entrada polimórfico. Delega para o método correto com base no tipo do artefato.
     * @param artifact O objeto do artefato (pode ser Bpd, Definitions, etc.).
     * @return Um objeto ProcessVariablesV2Plus com os dados completos e tipados.
     */
    public ProcessVariablesV2Plus inferTypedVariables(Object artifact) {
        if (artifact instanceof Bpd) {
            return inferFromBpd((Bpd) artifact);
        } else if (artifact instanceof Definitions) {
            return inferFromDefinitions((Definitions) artifact);
        }
        // Retorna vazio se o tipo não for suportado
        return new ProcessVariablesV2Plus();
    }

    /**
     * Extrai variáveis de um processo legado (BPD).
     * @param bpd O objeto Bpd extraído do XML.
     */
    private ProcessVariablesV2Plus inferFromBpd(Bpd bpd) {
        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();
        if (bpd == null) {
            return variables;
        }

        // 1. Processa Parâmetros de Entrada/Saída do Processo
        if (bpd.getBpdParameters() != null) {
            for (BpdParameter param : bpd.getBpdParameters()) {
                ProcessDefinitionV2Plus.VariableDefinitionV2Plus varDef =
                        ProcessDefinitionV2Plus.VariableDefinitionV2Plus.fromBpdParameter(param);

                if (param.getParameterType() == 1) { // 1 = Input
                    variables.addInputVariable(varDef.getName(), varDef.getTypeRef(), varDef.getCardinality(), varDef.isNullable(), varDef.getDescription());
                } else { // Output
                    variables.addOutputVariable(varDef.getName(), varDef.getTypeRef(), varDef.getCardinality(), varDef.isNullable(), varDef.getDescription());
                }
            }
        }

        // 2. Processa Variáveis Privadas (Locais) dos Pools
        if (bpd.getBusinessProcessDiagram() != null && bpd.getBusinessProcessDiagram().getPools() != null) {
            for (Pool pool : bpd.getBusinessProcessDiagram().getPools()) {
                if (pool.getPrivateVariables() != null) {
                    for (PrivateVariable pVar : pool.getPrivateVariables()) {
                        ProcessDefinitionV2Plus.VariableDefinitionV2Plus varDef =
                                ProcessDefinitionV2Plus.VariableDefinitionV2Plus.fromPrivateVariable(pVar);
                        variables.addPrivateVariable(varDef.getName(), varDef.getTypeRef(), varDef.getCardinality(), varDef.isNullable(), varDef.getDescription());
                    }
                }
            }
        }
        return variables;
    }

    /**
     * Extrai variáveis de um processo moderno (BPMN 2.0).
     * @param definitions O objeto Definitions extraído do XML.
     */
    private ProcessVariablesV2Plus inferFromDefinitions(Definitions definitions) {
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

    private void extractVariablesFromProcess(Process process, ProcessVariablesV2Plus variables) {
        if (process == null) return;

        extractVariablesFromIoSpecification(process.getIoSpecification(), variables);
        extractDataObjectsAsPrivateVars(process.getDataObjects(), variables);

        if (process.getFlowElements() != null) {
            for (Object element : process.getFlowElements()) {
                if (element instanceof SubProcess) {
                    extractVariablesFromSubProcess((SubProcess) element, variables);
                }
            }
        }
    }

    private void extractVariablesFromSubProcess(SubProcess subProcess, ProcessVariablesV2Plus variables) {
        if (subProcess == null || subProcess.getFlowElements() == null) return;

        List<DataObject> dataObjectsInSubProcess = subProcess.getFlowElements().stream()
                .filter(DataObject.class::isInstance)
                .map(DataObject.class::cast)
                .collect(Collectors.toList());
        extractDataObjectsAsPrivateVars(dataObjectsInSubProcess, variables);

        for (Object element : subProcess.getFlowElements()) {
            if (element instanceof SubProcess) {
                extractVariablesFromSubProcess((SubProcess) element, variables);
            }
        }
    }

    private void extractVariablesFromIoSpecification(IoSpecification ioSpec, ProcessVariablesV2Plus variables) {
        if (ioSpec == null) return;

        if (ioSpec.getDataInputs() != null) {
            for (DataInput input : ioSpec.getDataInputs()) {
                variables.addInputVariable(
                        input.getName(),
                        ProcessDefinitionV2Plus.VariableDefinitionV2Plus.convertClassIdToTypeRef(input.getItemSubjectRef()),
                        (input.getIsCollection() != null && input.getIsCollection()) ? "many" : "one",
                        false,
                        "Variável de entrada do processo."
                );
            }
        }

        if (ioSpec.getDataOutputs() != null) {
            for (DataOutput output : ioSpec.getDataOutputs()) {
                variables.addOutputVariable(
                        output.getName(),
                        ProcessDefinitionV2Plus.VariableDefinitionV2Plus.convertClassIdToTypeRef(output.getItemSubjectRef()),
                        (output.getIsCollection() != null && output.getIsCollection()) ? "many" : "one",
                        true,
                        "Variável de saída do processo."
                );
            }
        }
    }

    private void extractDataObjectsAsPrivateVars(List<DataObject> dataObjects, ProcessVariablesV2Plus variables) {
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