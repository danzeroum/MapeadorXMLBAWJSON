package br.com.danzeroum.bpmbaw.mapeadorxml.modelo;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessDefinitionV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessNodeV2Plus;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Normaliza e corrige a estrutura do grafo de processo (ProcessGraphV2Plus).
 */
public class GraphNormalizer {

    /**
     * Converte os tipos de nós do formato ENUM (EX: START_EVENT) para o formato
     * PascalCase do JSON final (Ex: StartEvent).
     *
     * @param pd A definição do processo contendo o grafo a ser normalizado.
     */
    public void normalizeNodeTypes(ProcessDefinitionV2Plus pd) {
        if (pd == null || pd.getGraph() == null || pd.getGraph().getNodes() == null) {
            return;
        }

        for (ProcessNodeV2Plus node : pd.getGraph().getNodes()) {
            if (node.getType() == null) continue;

            String originalType = node.getType().name(); // Ex: START_EVENT
            String[] parts = originalType.toLowerCase().split("_");
            StringBuilder finalTypeBuilder = new StringBuilder();

            for (String part : parts) {
                if(part.length() > 0) {
                    finalTypeBuilder.append(Character.toUpperCase(part.charAt(0)))
                            .append(part.substring(1));
                }
            }
            // Adicionamos como uma propriedade customizada que será usada na serialização final.
            node.getProperties().put("canonicalType", finalTypeBuilder.toString());
        }
    }

    /**
     * Recalcula a lista de endPoints para conter apenas IDs de nós do tipo EndEvent,
     * conforme especificado no formato final.
     *
     * @param pd A definição do processo cujo grafo será corrigido.
     */
    public void fixEndPoints(ProcessDefinitionV2Plus pd) {
        if (pd == null || pd.getGraph() == null || pd.getGraph().getNodes() == null) {
            return;
        }

        List<String> correctEndPoints = pd.getGraph().getNodes().stream()
                .filter(node -> node.getType() == ProcessNodeV2Plus.NodeType.END_EVENT)
                .map(ProcessNodeV2Plus::getId)
                .collect(Collectors.toList());

        pd.getGraph().setEndPoints(correctEndPoints);
    }
}