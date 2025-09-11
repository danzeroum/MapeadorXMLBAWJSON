// ===============================================
// 6. PROCESS GRAPH V2 - INTEGRIDADE REFERENCIAL
// ===============================================

package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;


import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Flow;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.FlowObject;

import java.util.*;
import java.util.stream.Collectors;

public class ProcessGraphV2 {
    private List<ProcessNodeV2> nodes;
    private List<ProcessEdgeV2> edges;
    private List<ProcessLane> lanes;
    private GraphMetadata metadata;
    private List<String> validationErrors;

    public ProcessGraphV2() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.lanes = new ArrayList<>();
        this.validationErrors = new ArrayList<>();
    }

    // Método para validar integridade referencial
    public boolean validateReferentialIntegrity() {
        validationErrors.clear();
        boolean isValid = true;

        // Criar set de IDs de nodes para lookup O(1)
        Set<String> nodeIds = nodes.stream()
                .map(ProcessNodeV2::getId)
                .collect(Collectors.toSet());

        // Validar cada edge
        for (ProcessEdgeV2 edge : edges) {
            if (!nodeIds.contains(edge.getSource())) {
                validationErrors.add(String.format(
                        "Edge %s references non-existent source node: %s",
                        edge.getId(), edge.getSource()
                ));
                isValid = false;
            }

            if (!nodeIds.contains(edge.getTarget())) {
                validationErrors.add(String.format(
                        "Edge %s references non-existent target node: %s",
                        edge.getId(), edge.getTarget()
                ));
                isValid = false;
            }
        }

        // Detectar ciclos (apenas reportar, não invalidar)
        List<String> cycles = detectCycles();
        if (!cycles.isEmpty()) {
            validationErrors.add("Cycles detected in graph: " + String.join(", ", cycles));
        }

        return isValid;
    }

    // Detectar ciclos usando DFS
    private List<String> detectCycles() {
        List<String> cycles = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        for (ProcessNodeV2 node : nodes) {
            if (!visited.contains(node.getId())) {
                if (hasCycleDFS(node.getId(), visited, recursionStack, new ArrayList<>())) {
                    // Ciclo detectado
                }
            }
        }

        return cycles;
    }

    private boolean hasCycleDFS(String nodeId, Set<String> visited,
                                Set<String> recursionStack, List<String> path) {
        visited.add(nodeId);
        recursionStack.add(nodeId);
        path.add(nodeId);

        // Obter edges de saída
        List<String> outgoingNodes = edges.stream()
                .filter(edge -> edge.getSource().equals(nodeId))
                .map(ProcessEdgeV2::getTarget)
                .collect(Collectors.toList());

        for (String nextNode : outgoingNodes) {
            if (!visited.contains(nextNode)) {
                if (hasCycleDFS(nextNode, visited, recursionStack, new ArrayList<>(path))) {
                    return true;
                }
            } else if (recursionStack.contains(nextNode)) {
                // Ciclo encontrado
                return true;
            }
        }

        recursionStack.remove(nodeId);
        return false;
    }

    // Método para adicionar node com validação
    public void addNode(ProcessNodeV2 node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }
        if (node.getId() == null || node.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Node ID cannot be null or empty");
        }

        // Verificar duplicatas
        boolean exists = nodes.stream().anyMatch(n -> n.getId().equals(node.getId()));
        if (exists) {
            throw new IllegalArgumentException("Node with ID " + node.getId() + " already exists");
        }

        nodes.add(node);
    }

    public void addEdge(ProcessEdgeV2 edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Edge cannot be null");
        }

        if (edge.getId() == null || edge.getId().trim().isEmpty()) {
            // Gerar ID se não existir
            edge.setId("edge-" + System.currentTimeMillis() + "-" + edges.size());
        }

        if (edge.getSource() == null || edge.getTarget() == null) {
            // Log detalhado em vez de falhar
            System.err.println("⚠️ Edge com source/target null: ID=" + edge.getId() +
                    ", Source=" + edge.getSource() +
                    ", Target=" + edge.getTarget());
            return; // Não adicionar, mas também não falhar
        }

        // Verificar duplicatas
        boolean exists = edges.stream().anyMatch(e -> e.getId().equals(edge.getId()));
        if (exists) {
            System.err.println("⚠️ Edge duplicado ignorado: " + edge.getId());
            return;
        }

        edges.add(edge);
    }

    // Estatísticas do grafo
    public GraphStatistics getStatistics() {
        GraphStatistics stats = new GraphStatistics();
        stats.setNodeCount(nodes.size());
        stats.setEdgeCount(edges.size());
        stats.setLaneCount(lanes.size());

        // Contar por tipo - CORRIGIDO PARA JAVA 8
        Map<String, Long> nodesByType = new HashMap<>();
        for (ProcessNodeV2 node : nodes) {
            String type = node.getType().toString();
            nodesByType.put(type, nodesByType.getOrDefault(type, 0L) + 1L);
        }
        stats.setNodesByType(nodesByType);

        // Verificar se é DAG
        stats.setDirectedAcyclicGraph(detectCycles().isEmpty());

        // Calcular densidade
        int maxEdges = nodes.size() * (nodes.size() - 1);
        stats.setDensity(maxEdges > 0 ? (double) edges.size() / maxEdges : 0.0);

        return stats;
    }
    // Getters and setters
    public List<ProcessNodeV2> getNodes() { return nodes; }
    public void setNodes(List<ProcessNodeV2> nodes) {
        this.nodes = nodes != null ? nodes : new ArrayList<>();
    }

    public List<ProcessEdgeV2> getEdges() { return edges; }
    public void setEdges(List<ProcessEdgeV2> edges) {
        this.edges = edges != null ? edges : new ArrayList<>();
    }

    public List<ProcessLane> getLanes() { return lanes; }
    public void setLanes(List<ProcessLane> lanes) {
        this.lanes = lanes != null ? lanes : new ArrayList<>();
    }

    public GraphMetadata getMetadata() { return metadata; }
    public void setMetadata(GraphMetadata metadata) { this.metadata = metadata; }

    public List<String> getValidationErrors() { return validationErrors; }

    // Classes internas
    public static class GraphMetadata {
        private String type; // "DAG", "Cyclic", "Tree"
        private String purpose;
        private Map<String, String> annotations;

        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }

        public Map<String, String> getAnnotations() { return annotations; }
        public void setAnnotations(Map<String, String> annotations) { this.annotations = annotations; }
    }
    private void debugFlowExtraction(List<Flow> flows, List<FlowObject> flowObjects) {
        System.out.println("🔍 DEBUG - Flow Extraction:");
        System.out.println("   Total Flows: " + (flows != null ? flows.size() : 0));
        System.out.println("   Total FlowObjects: " + (flowObjects != null ? flowObjects.size() : 0));

        if (flows != null) {
            int validFlows = 0;
            for (Flow flow : flows) {
                if (flow.getSourceObjectId() != null && flow.getTargetObjectId() != null) {
                    validFlows++;
                }
            }
            System.out.println("   Valid Flows (with source+target): " + validFlows);
        }
    }


}
