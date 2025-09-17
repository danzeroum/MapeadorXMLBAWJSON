package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessMappingsV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;

import java.util.List;

/**
 * Extrator de Mapeamentos BPMN 2.0 (Versão Final com Busca Recursiva e Logs)
 * Realiza uma busca profunda em todos os elementos, incluindo SubProcesses aninhados,
 * para garantir a extração completa de todos os mapeamentos de dados.
 * @version 2.1 - Recursive Mapping Extraction with Logging
 */
public class BpmnToV2PlusMappingsExtractor {

    public static ProcessMappingsV2Plus extractMappings(Process process) {
        ProcessMappingsV2Plus mappings = new ProcessMappingsV2Plus();
        if (process == null) {
            System.out.println("[MappingsExtractor] AVISO: O objeto do processo está nulo. Nenhum mapeamento será extraído.");
            return mappings;
        }

        // Inicia a busca recursiva a partir dos elementos de fluxo do processo principal.
        System.out.println("[MappingsExtractor] Iniciando busca por mapeamentos no processo: " + process.getName());
        extractMappingsFromFlowElements(process.getFlowElements(), mappings, " ");

        // Define a linguagem de expressão padrão para processos modernos
        mappings.setExprLang("cel");
        System.out.println("[MappingsExtractor] Busca finalizada. Total de mapeamentos de entrada: " + mappings.getInputMappings().size());
        System.out.println("[MappingsExtractor] Total de mapeamentos de saída: " + mappings.getOutputMappings().size());
        return mappings;
    }

    /**
     * MÉTODO RECURSIVO: Itera sobre os elementos de fluxo para encontrar mapeamentos.
     * Se encontra um SubProcess, ele chama a si mesmo para analisar os elementos internos.
     */
    private static void extractMappingsFromFlowElements(List<Object> elements, ProcessMappingsV2Plus mappings, String indent) {
        if (elements == null) {
            return;
        }
        System.out.println(indent + "-> Analisando " + elements.size() + " elementos de fluxo...");

        for (Object element : elements) {
            if (element instanceof CallActivity) {
                System.out.println(indent + "  Found CallActivity: " + ((CallActivity) element).getName());
                extractMappingsFromCallActivity((CallActivity) element, mappings);
            } else if (element instanceof SubProcess) {
                SubProcess subProcess = (SubProcess) element;
                System.out.println(indent + "  Entrando no SubProcess: " + subProcess.getName());
                // Ponto chave da recursão: chama o mesmo método para os elementos DENTRO do subprocesso.
                extractMappingsFromFlowElements(subProcess.getFlowElements(), mappings, indent + "  ");
            }
        }
    }

    private static void extractMappingsFromCallActivity(CallActivity callActivity, ProcessMappingsV2Plus mappings) {
        if (callActivity == null) return;

        // Mapeamentos de Entrada (DataInputAssociation)
        if (callActivity.getDataInputAssociations() != null) {
            System.out.println("    -> Encontrados " + callActivity.getDataInputAssociations().size() + " mapeamentos de entrada.");
            for (DataInputAssociation dia : callActivity.getDataInputAssociations()) {
                if (dia.getAssignment() != null && dia.getAssignment().getFrom() != null) {
                    String source = dia.getAssignment().getFrom().getExpression();
                    String target = dia.getTargetRef();
                    if (source != null && !source.trim().isEmpty() && target != null && !target.trim().isEmpty()) {
                        String description = "Mapeamento de entrada para a atividade: " + callActivity.getName();
                        mappings.addInputMapping(source, target, description);
                    }
                }
            }
        }

        // Mapeamentos de Saída (DataOutputAssociation)
        if (callActivity.getDataOutputAssociations() != null) {
            System.out.println("    -> Encontrados " + callActivity.getDataOutputAssociations().size() + " mapeamentos de saída.");
            for (DataOutputAssociation doa : callActivity.getDataOutputAssociations()) {
                if (doa.getAssignment() != null && doa.getAssignment().getTo() != null) {
                    String source = doa.getSourceRef();
                    String target = doa.getAssignment().getTo().getContent();
                    if (source != null && !source.trim().isEmpty() && target != null && !target.trim().isEmpty()) {
                        String alias = target;
                        String description = "Mapeamento de saída da atividade: " + callActivity.getName();
                        mappings.addOutputMapping(source, target, alias, description);
                    }
                }
            }
        }
    }
}