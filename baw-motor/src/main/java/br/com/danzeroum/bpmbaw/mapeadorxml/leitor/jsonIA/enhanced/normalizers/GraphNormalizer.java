package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.normalizers;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import java.util.*;

public class GraphNormalizer {

    public void promoteCanonicalTypeToType(ProcessDefinitionV2Plus pd) {
        if (pd.getGraph() == null || pd.getGraph().getNodes() == null) return;

        for (ProcessNodeV2Plus n : pd.getGraph().getNodes()) {
            if (n.getProperties() != null) {
                Object canonObj = n.getProperties().get("canonicalType");
                if (canonObj != null) {
                    String canonStr = canonObj.toString();
                    if (!canonStr.isEmpty()) {
                        // Converter String para NodeType enum
                        ProcessNodeV2Plus.NodeType nodeType = convertToNodeType(canonStr);
                        n.setType(nodeType);
                    }
                }
            }
        }
    }

    private ProcessNodeV2Plus.NodeType convertToNodeType(String typeStr) {
        // Mapear strings para enum NodeType
        if (typeStr == null) return ProcessNodeV2Plus.NodeType.UNKNOWN;

        switch (typeStr) {
            case "StartEvent":
            case "START_EVENT":
                return ProcessNodeV2Plus.NodeType.START_EVENT;
            case "EndEvent":
            case "END_EVENT":
                return ProcessNodeV2Plus.NodeType.END_EVENT;
            case "UserTask":
            case "USER_TASK":
                return ProcessNodeV2Plus.NodeType.USER_TASK;
            case "ScriptTask":
            case "SCRIPT":
                return ProcessNodeV2Plus.NodeType.SCRIPT;
            case "ExclusiveGateway":
            case "GATEWAY":
                return ProcessNodeV2Plus.NodeType.EXCLUSIVE_GATEWAY;
            case "SubProcess":
            case "SUBPROCESS":
                return ProcessNodeV2Plus.NodeType.SUBPROCESS;
            case "ServiceTask":
            case "SYSTEM_TASK":
                return ProcessNodeV2Plus.NodeType.TASK;
            default:
                return ProcessNodeV2Plus.NodeType.UNKNOWN;
        }
    }

    public void recomputeEntryAndEndPoints(ProcessDefinitionV2Plus pd) {
        if (pd.getGraph() == null || pd.getGraph().getNodes() == null) return;

        Set<String> starts = new HashSet<String>();
        Set<String> ends = new HashSet<String>();

        for (ProcessNodeV2Plus node : pd.getGraph().getNodes()) {
            if (node.getType() == ProcessNodeV2Plus.NodeType.START_EVENT) {
                starts.add(node.getId());
            } else if (node.getType() == ProcessNodeV2Plus.NodeType.END_EVENT) {
                ends.add(node.getId());
            }
        }

        pd.getGraph().setEntryPoints(new ArrayList<String>(starts));
        pd.getGraph().setEndPoints(new ArrayList<String>(ends));
    }

    // Adicionar métodos que estavam faltando
    public void fixEndPoints(ProcessDefinitionV2Plus pd) {
        recomputeEntryAndEndPoints(pd);
    }

    public void normalizeNodeTypes(ProcessDefinitionV2Plus pd) {
        promoteCanonicalTypeToType(pd);
    }
}