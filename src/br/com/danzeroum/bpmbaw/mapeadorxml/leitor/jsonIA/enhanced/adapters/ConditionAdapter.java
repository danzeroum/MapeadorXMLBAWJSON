package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.adapters;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import java.util.*;

public class ConditionAdapter {

    // Método corrigido para receber ProcessGraphV2Plus ao invés de ProcessDefinitionV2Plus
    public List<ConditionV2Plus> extractConditionsFromEdges(ProcessGraphV2Plus graph) {
        Map<String, ConditionV2Plus> byKey = new LinkedHashMap<String, ConditionV2Plus>();

        if (graph == null || graph.getEdges() == null) {
            return new ArrayList<ConditionV2Plus>();
        }

        for (ProcessEdgeV2Plus e : graph.getEdges()) {
            String label = e.getLabel();
            if (label == null || label.trim().isEmpty() || "Untitled".equals(label)) {
                continue;
            }

            String key = e.getSource() + "->" + label.trim();

            if (!byKey.containsKey(key)) {
                ConditionV2Plus c = new ConditionV2Plus();
                c.setId("cond_" + Math.abs(key.hashCode()));
                c.setName(label.trim());
                c.setExpression("label == \"" + label.trim().replace("\"","\\\"") + "\"");
                c.setLanguage("cel");
                byKey.put(key, c);
            }
        }

        return new ArrayList<ConditionV2Plus>(byKey.values());
    }

    public void attachConditionRefs(ProcessGraphV2Plus graph, List<ConditionV2Plus> conds) {
        if (graph == null || graph.getEdges() == null) return;

        for (ProcessEdgeV2Plus e : graph.getEdges()) {
            if (e.getLabel() == null || "Untitled".equals(e.getLabel())) continue;

            String key = e.getSource() + "->" + e.getLabel().trim();
            String id = "cond_" + Math.abs(key.hashCode());
            e.setConditionRef(id);
        }
    }
}