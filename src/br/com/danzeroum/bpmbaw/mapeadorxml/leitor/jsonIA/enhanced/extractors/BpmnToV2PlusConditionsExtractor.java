package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessConditionV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.SequenceFlow;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * Extrai as definições de condição a partir dos SequenceFlows de um processo BPMN 2.0.
 * @version 1.0
 */
public class BpmnToV2PlusConditionsExtractor {

    public static List<ProcessConditionV2Plus> extractConditions(Process process) {
        List<ProcessConditionV2Plus> conditions = new ArrayList<>();
        if (process == null || process.getSequenceFlows() == null) {
            return conditions;
        }

        for (SequenceFlow flow : process.getSequenceFlows()) {
            // Verifica se o fluxo possui uma expressão de condição.
            if (flow.getConditionExpression() != null && flow.getConditionExpression().getExpression() != null) {
                String expressionText = flow.getConditionExpression().getExpression().trim();

                // Ignora condições vazias ou triviais como "true".
                if (!expressionText.isEmpty() && !"true".equalsIgnoreCase(expressionText)) {
                    ProcessConditionV2Plus condition = new ProcessConditionV2Plus();

                    // O ID da condição é derivado do ID do fluxo para garantir unicidade.
                    condition.setId("cd:" + flow.getId());
                    condition.setName(flow.getName() != null ? flow.getName() : "Condição para " + flow.getId());
                    condition.setExpression(expressionText);
                    condition.setLanguage(ProcessConditionV2Plus.ExpressionLanguage.CEL); // Assumindo CEL como padrão moderno.
                    condition.setDescription("Condição de roteamento para o fluxo '" + (flow.getName() != null ? flow.getName() : flow.getId()) + "'");

                    conditions.add(condition);
                }
            }
        }
        return conditions;
    }
}