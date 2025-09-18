package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.normalizers;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import java.util.*;
import java.util.stream.Collectors;

public class GraphNormalizer {

    public void promoteCanonicalTypeToType(ProcessDefinitionV2Plus pd) {
        if (pd.getGraph() == null || pd.getGraph().getNodes() == null) return;

        for (ProcessNodeV2Plus n : pd.getGraph().getNodes()) {
            if (n.getProperties() != null) {
                // Corrigido: getProperties() retorna Map<String, Object>
                Object canonObj = n.getProperties().get("canonicalType");
                if (canonObj != null) {
                    String canon = canonObj.toString();
                    if (!canon.isEmpty()) {
                        n.setType(canon);
                    }
                }
            }
        }
    }

    public void recomputeEntryAndEndPoints(ProcessDefinitionV2Plus pd) {
        if (pd.getGraph() == null || pd.getGraph().getNodes() == null) return;

        Set<String> starts = new HashSet<String>();
        Set<String> ends = new HashSet<String>();

        for (ProcessNodeV2Plus node : pd.getGraph().getNodes()) {
            if ("StartEvent".equals(node.getType())) {
                starts.add(node.getId());
            } else if ("EndEvent".equals(node.getType())) {
                ends.add(node.getId());
            }
        }

        pd.getGraph().setEntryPoints(new ArrayList<String>(starts));
        pd.getGraph().setEndPoints(new ArrayList<String>(ends));
    }
}