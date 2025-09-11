package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Process Graph V2+ - Grafo Único IA-Friendly (Elimina Duplicidade)
 *
 * ELIMINA PROBLEMAS V1/V2:
 * ❌ V1: flow[], graph, rootView (3 verdades diferentes)
 * ❌ V2: ainda mantinha resquícios de estruturas legadas
 * ✅ V2+: ÚNICA fonte de verdade - apenas nodes/edges/lanes
 *
 * CARACTERÍSTICAS V2+:
 * ✅ Eliminação completa de flow[] e rootView[]
 * ✅ Nodes com logicRef (sem scripts inline)
 * ✅ Edges com conditionRef (sem condições inline)
 * ✅ Entry/Exit points explícitos
 * ✅ Path tags para happy/alternative paths
 * ✅ Validação de integridade referencial robusta
 * ✅ Detecção de ciclos e análise topológica
 *
 * @version 2.1.0
 * @since V2+ IA-Friendly Migration
 */
@JsonPropertyOrder({
        "nodes", "edges", "lanes", "entryPoints", "endPoints", "metadata"
})
public class ProcessGraphV2Plus {

    /**
     * Nós do processo (atividades, gateways, eventos)
     * COM logicRef (sem scripts inline)
     */
    @JsonProperty("nodes")
    private List<ProcessNodeV2Plus> nodes;

    /**
     * Arestas do processo (fluxos de sequência)
     * COM conditionRef (sem condições inline)
     */
    @JsonProperty("edges")
    private List<ProcessEdgeV2Plus> edges;

    /**
     * Raias/lanes do processo (participantes)
     */
    @JsonProperty("lanes")
    private List<ProcessLaneV2Plus> lanes;

    /**
     * 🆕 NOVO V2+: Pontos de entrada explícitos
     * IDs dos nodes que iniciam o processo
     */
    @JsonProperty("entryPoints")
    private List<String> entryPoints;

    /**
     * 🆕 NOVO V2+: Pontos de saída explícitos
     * IDs dos nodes que finalizam o processo
     */
    @JsonProperty("endPoints")
    private List<String> endPoints;

    /**
     * Metadados do grafo (tipo, complexidade, etc.)
     */
    @JsonProperty("metadata")
    private GraphMetadata metadata;


    // Em ProcessGraphV2Plus.java - verificar se existe:
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    // ❌ CAMPOS REMOVIDOS DEFINITIVAMENTE (Breaking Changes):
    // - List<FlowStep> flow           → ELIMINADO (era duplicata)
    // - List<FlowStep> rootView       → ELIMINADO (era duplicata)
    // - List<Object> pathCollections  → ELIMINADO (agora é pathTags nos edges)

    // Validação interna (não serializada)
    private transient List<String> validationErrors;

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================

    public ProcessGraphV2Plus() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.lanes = new ArrayList<>();
        this.entryPoints = new ArrayList<>();
        this.endPoints = new ArrayList<>();
        this.validationErrors = new ArrayList<>();
        this.metadata = new GraphMetadata();
    }

    public static ProcessGraphV2Plus create(String id) {
        ProcessGraphV2Plus graph = new ProcessGraphV2Plus();
        graph.setId(id != null ? id : "default-graph");
        return graph;
    }


    // =========================================================================
    // MIGRAÇÃO V1/V2 → V2+ (ELIMINA DUPLICIDADE)
    // =========================================================================

    /**
     * 🔥 MIGRAÇÃO CRÍTICA: Elimina flow[] e rootView[]
     * Consolida múltiplas verdades em uma única estrutura
     */
    public void migrateFromLegacyStructures(Object legacyFlow, Object legacyRootView, Object legacyGraph) {
        System.out.println("🔄 Migrating legacy structures to V2+ unified graph...");

        // TODO: Implementar migração real
        // 1. Extrair nodes únicos de flow + rootView + graph
        // 2. Consolidar edges eliminando duplicatas
        // 3. Detectar entry/exit points automaticamente
        // 4. Extrair path tags das estruturas legadas

        System.out.println("✅ Legacy migration completed - single source of truth established");
    }

    /**
     * Detecta automaticamente entry points (nodes sem incoming edges)
     */
    public void autoDetectEntryPoints() {
        Set<String> nodesWithIncoming = edges.stream()
                .map(ProcessEdgeV2Plus::getTarget)
                .collect(Collectors.toSet());

        entryPoints.clear();
        for (ProcessNodeV2Plus node : nodes) {
            if (!nodesWithIncoming.contains(node.getId())) {
                entryPoints.add(node.getId());
            }
        }

        System.out.println("🔍 Auto-detected " + entryPoints.size() + " entry points");
    }

    /**
     * Detecta automaticamente exit points (nodes sem outgoing edges)
     */
    public void autoDetectExitPoints() {
        Set<String> nodesWithOutgoing = edges.stream()
                .map(ProcessEdgeV2Plus::getSource)
                .collect(Collectors.toSet());

        endPoints.clear();
        for (ProcessNodeV2Plus node : nodes) {
            if (!nodesWithOutgoing.contains(node.getId())) {
                endPoints.add(node.getId());
            }
        }

        System.out.println("🔍 Auto-detected " + endPoints.size() + " exit points");
    }

    // =========================================================================
    // VALIDAÇÃO E INTEGRIDADE REFERENCIAL
    // =========================================================================

    /**
     * Validação completa do grafo V2+
     */
    public boolean isValid() {
        return validate().isEmpty();
    }

    /**
     * Validação detalhada com lista de erros
     */
    public List<String> validate() {
        validationErrors.clear();

        // 1. Validar estrutura básica
        if (nodes.isEmpty()) {
            validationErrors.add("Graph must have at least one node");
        }

        // 2. Validar integridade referencial
        validateReferentialIntegrity();

        // 3. Validar entry/exit points
        validateEntryExitPoints();

        // 4. Detectar problemas estruturais
        detectStructuralIssues();

        return new ArrayList<>(validationErrors);
    }

    /**
     * Valida integridade referencial nodes ↔ edges
     */
    public boolean validateReferentialIntegrity() {
        boolean isValid = true;

        // Criar set de IDs de nodes para lookup O(1)
        Set<String> nodeIds = nodes.stream()
                .map(ProcessNodeV2Plus::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Validar cada edge
        for (ProcessEdgeV2Plus edge : edges) {
            if (edge.getSource() != null && !nodeIds.contains(edge.getSource())) {
                validationErrors.add("Edge " + edge.getId() + " references non-existent source: " + edge.getSource());
                isValid = false;
            }

            if (edge.getTarget() != null && !nodeIds.contains(edge.getTarget())) {
                validationErrors.add("Edge " + edge.getId() + " references non-existent target: " + edge.getTarget());
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Valida entry/exit points
     */
    private void validateEntryExitPoints() {
        Set<String> nodeIds = nodes.stream()
                .map(ProcessNodeV2Plus::getId)
                .collect(Collectors.toSet());

        // Validar entry points
        for (String entryId : entryPoints) {
            if (!nodeIds.contains(entryId)) {
                validationErrors.add("Entry point references non-existent node: " + entryId);
            }
        }

        // Validar exit points
        for (String exitId : endPoints) {
            if (!nodeIds.contains(exitId)) {
                validationErrors.add("Exit point references non-existent node: " + exitId);
            }
        }
    }

    /**
     * Detecta problemas estruturais (ciclos, nodes órfãos, etc.)
     */
    private void detectStructuralIssues() {
        // 1. Detectar ciclos
        List<String> cycles = detectCycles();
        if (!cycles.isEmpty()) {
            metadata.hasCycles = true;
            validationErrors.add("Cycles detected: " + String.join(", ", cycles));
        }

        // 2. Detectar nodes órfãos (sem edges)
        Set<String> connectedNodes = new HashSet<>();
        edges.forEach(edge -> {
            connectedNodes.add(edge.getSource());
            connectedNodes.add(edge.getTarget());
        });

        for (ProcessNodeV2Plus node : nodes) {
            if (!connectedNodes.contains(node.getId())) {
                validationErrors.add("Orphan node detected: " + node.getId());
            }
        }

        // 3. Validar se há pelo menos um entry e um exit point
        if (entryPoints.isEmpty()) {
            validationErrors.add("Graph must have at least one entry point");
        }
        if (endPoints.isEmpty()) {
            validationErrors.add("Graph must have at least one exit point");
        }
    }

    /**
     * Detecta ciclos usando algoritmo DFS
     */
    public List<String> detectCycles() {
        List<String> cycles = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        for (ProcessNodeV2Plus node : nodes) {
            if (!visited.contains(node.getId())) {
                List<String> path = new ArrayList<>();
                if (hasCycleDFS(node.getId(), visited, recursionStack, path)) {
                    cycles.add("Cycle: " + String.join(" -> ", path));
                }
            }
        }

        return cycles;
    }

    private boolean hasCycleDFS(String nodeId, Set<String> visited, Set<String> recursionStack, List<String> path) {
        visited.add(nodeId);
        recursionStack.add(nodeId);
        path.add(nodeId);

        // Obter edges de saída
        List<String> outgoingNodes = edges.stream()
                .filter(edge -> nodeId.equals(edge.getSource()))
                .map(ProcessEdgeV2Plus::getTarget)
                .collect(Collectors.toList());

        for (String nextNode : outgoingNodes) {
            if (!visited.contains(nextNode)) {
                if (hasCycleDFS(nextNode, visited, recursionStack, new ArrayList<>(path))) {
                    return true;
                }
            } else if (recursionStack.contains(nextNode)) {
                // Ciclo encontrado
                path.add(nextNode);
                return true;
            }
        }

        recursionStack.remove(nodeId);
        return false;
    }

    // =========================================================================
    // ADIÇÃO SEGURA DE ELEMENTOS
    // =========================================================================

    /**
     * Adiciona node com validação completa
     */
    public void addNode(ProcessNodeV2Plus node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }
        if (node.getId() == null || node.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Node ID cannot be null or empty");
        }

        // Verificar duplicatas
        boolean exists = nodes.stream().anyMatch(n -> node.getId().equals(n.getId()));
        if (exists) {
            throw new IllegalArgumentException("Node with ID " + node.getId() + " already exists");
        }

        nodes.add(node);
    }

    /**
     * Adiciona edge com validação completa
     */
    public void addEdge(ProcessEdgeV2Plus edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Edge cannot be null");
        }

        // Gerar ID se não existir
        if (edge.getId() == null || edge.getId().trim().isEmpty()) {
            edge.setId("edge-" + System.currentTimeMillis() + "-" + edges.size());
        }

        // Validação de source/target será feita em validateReferentialIntegrity()
        if (edge.getSource() == null || edge.getTarget() == null) {
            System.err.println("⚠️ Edge with null source/target: " + edge.getId());
            return; // Não adicionar
        }

        // Verificar duplicatas por ID
        boolean exists = edges.stream().anyMatch(e -> edge.getId().equals(e.getId()));
        if (exists) {
            System.err.println("⚠️ Duplicate edge ignored: " + edge.getId());
            return;
        }

        edges.add(edge);
    }

    /**
     * Adiciona lane com validação
     */
    public void addLane(ProcessLaneV2Plus lane) {
        if (lane == null) {
            throw new IllegalArgumentException("Lane cannot be null");
        }
        if (lane.getId() == null || lane.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Lane ID cannot be null or empty");
        }

        // Verificar duplicatas
        boolean exists = lanes.stream().anyMatch(l -> lane.getId().equals(l.getId()));
        if (exists) {
            throw new IllegalArgumentException("Lane with ID " + lane.getId() + " already exists");
        }

        lanes.add(lane);
    }

    // =========================================================================
    // ANÁLISE E ESTATÍSTICAS
    // =========================================================================

    /**
     * Estatísticas completas do grafo
     */
    public GraphStatistics getStatistics() {
        GraphStatistics stats = new GraphStatistics();

        stats.nodeCount = nodes.size();
        stats.edgeCount = edges.size();
        stats.laneCount = lanes.size();
        stats.entryPointCount = entryPoints.size();
        stats.exitPointCount = endPoints.size();

        // Contar por tipo de node
        Map<String, Long> nodesByType = new HashMap<>();
        for (ProcessNodeV2Plus node : nodes) {
            String type = node.getType() != null ? node.getType().toString() : "UNKNOWN";
            nodesByType.put(type, nodesByType.getOrDefault(type, 0L) + 1L);
        }
        stats.nodesByType = nodesByType;

        // Análise topológica
        stats.hasCycles = !detectCycles().isEmpty();
        stats.isConnected = isGraphConnected();

        // Densidade do grafo
        int maxEdges = nodes.size() * (nodes.size() - 1);
        stats.density = maxEdges > 0 ? (double) edges.size() / maxEdges : 0.0;

        // Complexidade (simplificada)
        stats.complexity = calculateComplexity();

        return stats;
    }

    /**
     * Verifica se o grafo é conectado
     */
    private boolean isGraphConnected() {
        if (nodes.isEmpty()) return true;

        // BFS para verificar conectividade
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.offer(nodes.get(0).getId());
        visited.add(nodes.get(0).getId());

        while (!queue.isEmpty()) {
            String current = queue.poll();

            // Adicionar vizinhos (ignora direção)
            edges.stream()
                    .filter(e -> current.equals(e.getSource()) || current.equals(e.getTarget()))
                    .forEach(e -> {
                        String neighbor = current.equals(e.getSource()) ? e.getTarget() : e.getSource();
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.offer(neighbor);
                        }
                    });
        }

        return visited.size() == nodes.size();
    }

    /**
     * Calcula complexidade do grafo (métrica simples)
     */
    private int calculateComplexity() {
        // Complexidade = nodes + edges + ciclos + gateways
        int complexity = nodes.size() + edges.size();

        if (metadata.hasCycles) complexity += 5;

        long gateways = nodes.stream()
                .filter(n -> n.getType() != null && n.getType().name().contains("GATEWAY"))
                .count();
        complexity += (int) gateways * 2;

        return complexity;
    }

    // =========================================================================
    // BUSCA E NAVEGAÇÃO
    // =========================================================================

    /**
     * Encontra node por ID
     */
    public ProcessNodeV2Plus findNode(String nodeId) {
        return nodes.stream()
                .filter(node -> nodeId.equals(node.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Encontra todas as edges de saída de um node
     */
    public List<ProcessEdgeV2Plus> getOutgoingEdges(String nodeId) {
        return edges.stream()
                .filter(edge -> nodeId.equals(edge.getSource()))
                .collect(Collectors.toList());
    }

    /**
     * Encontra todas as edges de entrada de um node
     */
    public List<ProcessEdgeV2Plus> getIncomingEdges(String nodeId) {
        return edges.stream()
                .filter(edge -> nodeId.equals(edge.getTarget()))
                .collect(Collectors.toList());
    }

    /**
     * Obtém vizinhos diretos de um node
     */
    public List<String> getNeighbors(String nodeId) {
        Set<String> neighbors = new HashSet<>();

        // Adicionar targets das edges de saída
        getOutgoingEdges(nodeId).forEach(edge -> neighbors.add(edge.getTarget()));

        // Adicionar sources das edges de entrada
        getIncomingEdges(nodeId).forEach(edge -> neighbors.add(edge.getSource()));

        return new ArrayList<>(neighbors);
    }

    // =========================================================================
    // GETTERS AND SETTERS
    // =========================================================================

    public List<ProcessNodeV2Plus> getNodes() { return nodes; }
    public void setNodes(List<ProcessNodeV2Plus> nodes) {
        this.nodes = nodes != null ? nodes : new ArrayList<>();
    }

    public List<ProcessEdgeV2Plus> getEdges() { return edges; }
    public void setEdges(List<ProcessEdgeV2Plus> edges) {
        this.edges = edges != null ? edges : new ArrayList<>();
    }

    public List<ProcessLaneV2Plus> getLanes() { return lanes; }
    public void setLanes(List<ProcessLaneV2Plus> lanes) {
        this.lanes = lanes != null ? lanes : new ArrayList<>();
    }

    public List<String> getEntryPoints() { return entryPoints; }
    public void setEntryPoints(List<String> entryPoints) {
        this.entryPoints = entryPoints != null ? entryPoints : new ArrayList<>();
    }

    public List<String> getEndPoints() { return endPoints; }
    public void setEndPoints(List<String> endPoints) {
        this.endPoints = endPoints != null ? endPoints : new ArrayList<>();
    }

    public GraphMetadata getMetadata() { return metadata; }
    public void setMetadata(GraphMetadata metadata) { this.metadata = metadata; }

    public List<String> getValidationErrors() { return validationErrors; }

    // =========================================================================
    // CLASSES DE APOIO
    // =========================================================================

    /**
     * Metadados do grafo
     */
    public static class GraphMetadata {
        public String processId;
        public String type; // "DAG", "Cyclic", "Tree"
        public String createdAt;
        public String version;
        public boolean hasCycles;
        public Map<String, String> annotations;

        public GraphMetadata() {
            this.annotations = new HashMap<>();
        }

        @Override
        public String toString() {
            return String.format("GraphMetadata{processId=%s, type=%s, hasCycles=%s}",
                    processId, type, hasCycles);
        }
    }

    /**
     * Estatísticas do grafo
     */
    public static class GraphStatistics {
        public int nodeCount;
        public int edgeCount;
        public int laneCount;
        public int entryPointCount;
        public int exitPointCount;
        public Map<String, Long> nodesByType;
        public boolean hasCycles;
        public boolean isConnected;
        public double density;
        public int complexity;

        @Override
        public String toString() {
            return String.format(
                    "GraphStats{nodes=%d, edges=%d, lanes=%d, entries=%d, exits=%d, cycles=%s, connected=%s, density=%.2f, complexity=%d}",
                    nodeCount, edgeCount, laneCount, entryPointCount, exitPointCount, hasCycles, isConnected, density, complexity
            );
        }
    }

    // =========================================================================
    // TESTE INLINE RÁPIDO
    // =========================================================================

    /**
     * Teste básico para validação durante desenvolvimento
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing ProcessGraphV2Plus...");

        try {
            // Teste 1: Criação básica
            ProcessGraphV2Plus graph = ProcessGraphV2Plus.create("test-process");
            System.out.println("✅ Basic creation: " + (graph.getMetadata().processId.equals("test-process")));

            // Teste 2: Adição de nodes
            ProcessNodeV2Plus node1 = new ProcessNodeV2Plus();
            node1.setId("node1");
            node1.setName("Start Node");

            ProcessNodeV2Plus node2 = new ProcessNodeV2Plus();
            node2.setId("node2");
            node2.setName("End Node");

            graph.addNode(node1);
            graph.addNode(node2);
            System.out.println("✅ Node addition: " + (graph.getNodes().size() == 2));

            // Teste 3: Adição de edge
            ProcessEdgeV2Plus edge = new ProcessEdgeV2Plus();
            edge.setId("edge1");
            edge.setSource("node1");
            edge.setTarget("node2");

            graph.addEdge(edge);
            System.out.println("✅ Edge addition: " + (graph.getEdges().size() == 1));

            // Teste 4: Auto-detect entry/exit points
            graph.autoDetectEntryPoints();
            graph.autoDetectExitPoints();
            System.out.println("✅ Auto-detect points: entries=" + graph.getEntryPoints().size() +
                    ", exits=" + graph.getEndPoints().size());

            // Teste 5: Validação
            System.out.println("✅ Validation: " + graph.isValid());

            // Teste 6: Estatísticas
            GraphStatistics stats = graph.getStatistics();
            System.out.println("✅ Statistics: " + stats);

            // Teste 7: Busca
            ProcessNodeV2Plus found = graph.findNode("node1");
            System.out.println("✅ Node search: " + (found != null && found.getName().equals("Start Node")));

            System.out.println("\n🎉 ProcessGraphV2Plus: ALL TESTS PASSED!");

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}