package br.com.danzeroum.bpmbaw.mapeadorxml.modelo;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessConditionV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessEdgeV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessGraphV2Plus;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Adapta e vincula as condições de roteamento (conditions) às arestas (edges) do grafo do processo.
 */
public class ConditionAdapter {

    /**
     * Seta o atributo 'conditionRef' nas arestas (edges) do grafo, vinculando-as
     * a uma condição da lista centralizada. A heurística utiliza o 'name' da condição
     * e o 'label' da aresta para encontrar a correspondência (ex: label "Sim" casa com condição de nome "Sim").
     *
     * @param g O grafo do processo com as arestas a serem atualizadas.
     * @param conds A lista de todas as condições extraídas.
     */
    public void attachConditionRefs(ProcessGraphV2Plus g, List<ProcessConditionV2Plus> conds) {
        if (g == null || g.getEdges() == null || conds == null || conds.isEmpty()) {
            return;
        }

        // Cria um mapa para busca rápida: a chave é o nome da condição normalizado
        // (ex: "Orçamento Aceite?" se torna "orçamentoaceite?")
        Map<String, ProcessConditionV2Plus> conditionMapByName = conds.stream()
                .filter(c -> c.getName() != null && !c.getName().isEmpty())
                .collect(Collectors.toMap(
                        c -> normalizeString(c.getName()),
                        Function.identity(),
                        (existing, replacement) -> existing // Em caso de nomes duplicados, mantém o primeiro
                ));

        for (ProcessEdgeV2Plus edge : g.getEdges()) {
            String label = edge.getLabel();
            if (label != null && !label.isEmpty()) {
                // Normaliza o label da mesma forma que o nome da condição para fazer o "match"
                String normalizedLabel = normalizeString(label);
                if (conditionMapByName.containsKey(normalizedLabel)) {
                    ProcessConditionV2Plus matchedCondition = conditionMapByName.get(normalizedLabel);
                    edge.setConditionRef(matchedCondition.getId());
                }
            }
        }
    }

    /**
     * Normaliza uma string para comparação: converte para minúsculas e remove caracteres não alfanuméricos.
     */
    private String normalizeString(String input) {
        return input.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
}