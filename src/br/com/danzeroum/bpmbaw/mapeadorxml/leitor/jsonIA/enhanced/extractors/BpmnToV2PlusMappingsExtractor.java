// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/jsonIA/enhanced/extractors/BpmnToV2PlusMappingsExtractor.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessMappingsV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;

import java.util.List;

/**
 * Extrator de Mapeamentos BPMN 2.0 (Versão Final com Busca Recursiva)
 * Realiza uma busca profunda em todos os elementos, incluindo SubProcesses aninhados,
 * para garantir a extração completa de todos os mapeamentos de dados.
 * @version 2.0 - Recursive Mapping Extraction
 */
public class BpmnToV2PlusMappingsExtractor {

    public static ProcessMappingsV2Plus extractMappings(Process process) {
        ProcessMappingsV2Plus mappings = new ProcessMappingsV2Plus();
        if (process == null) {
            return mappings;
        }

        // Inicia a busca recursiva a partir dos elementos de fluxo do processo principal.
        extractMappingsFromFlowElements(process.getFlowElements(), mappings);

        // Define a linguagem de expressão padrão para processos modernos
        mappings.setExprLang("cel");
        return mappings;
    }

    /**
     * MÉTODO RECURSIVO: Itera sobre os elementos de fluxo para encontrar mapeamentos.
     * Se encontra um SubProcess, ele chama a si mesmo para analisar os elementos internos.
     */
    private static void extractMappingsFromFlowElements(List<Object> elements, ProcessMappingsV2Plus mappings) {
        if (elements == null) {
            return;
        }

        for (Object element : elements) {
            if (element instanceof CallActivity) {
                extractMappingsFromCallActivity((CallActivity) element, mappings);
            } else if (element instanceof SubProcess) {
                // Ponto chave da recursão: se encontrar um subprocesso,
                // chama o mesmo método para analisar os elementos DENTRO dele.
                SubProcess subProcess = (SubProcess) element;
                extractMappingsFromFlowElements(subProcess.getFlowElements(), mappings);
            }
        }
    }

    private static void extractMappingsFromCallActivity(CallActivity callActivity, ProcessMappingsV2Plus mappings) {
        if (callActivity == null) return;

        // Mapeamentos de Entrada (DataInputAssociation)
        if (callActivity.getDataInputAssociations() != null) {
            for (DataInputAssociation dia : callActivity.getDataInputAssociations()) {
                if (dia.getAssignment() != null && dia.getAssignment().getFrom() != null) {
                    String source = dia.getAssignment().getFrom().getExpression();
                    String target = dia.getTargetRef();
                    // Evita adicionar mapeamentos nulos ou vazios
                    if (source != null && !source.trim().isEmpty() && target != null && !target.trim().isEmpty()) {
                        String description = "Mapeamento de entrada para a atividade: " + callActivity.getName();
                        mappings.addInputMapping(source, target, description);
                    }
                }
            }
        }

        // Mapeamentos de Saída (DataOutputAssociation)
        if (callActivity.getDataOutputAssociations() != null) {
            for (DataOutputAssociation doa : callActivity.getDataOutputAssociations()) {
                if (doa.getAssignment() != null && doa.getAssignment().getTo() != null) {
                    String source = doa.getSourceRef();
                    String target = doa.getAssignment().getTo().getContent();
                    // Evita adicionar mapeamentos nulos ou vazios
                    if (source != null && !source.trim().isEmpty() && target != null && !target.trim().isEmpty()) {
                        String alias = target; // O alias pode ser o próprio target
                        String description = "Mapeamento de saída da atividade: " + callActivity.getName();
                        mappings.addOutputMapping(source, target, alias, description);
                    }
                }
            }
        }
    }
}