package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexManifest {
    private Map<String, IndexDefinition> indices;
    private PaginationConfig pagination;
    private Map<String, Object> materializedViews;

    public IndexManifest() {
        this.indices = new HashMap<>();
        this.materializedViews = new HashMap<>();
    }

    // Factory method para criar indices padrão
    public static IndexManifest createDefault(ProcessGraphV2 graph, List<DataTypeDefinitionV2> dataTypes,
                                              ProcessLogicV2 logic) {
        IndexManifest manifest = new IndexManifest();

        // Index de nodes por ID
        manifest.addIndex("nodeById", IndexType.HASHMAP,
                "$.processGraph.nodes[*].id", "O(1) node lookup");

        // Index de nodes por tipo
        manifest.addIndex("nodesByType", IndexType.GROUPING,
                "$.processGraph.nodes[*].type", "Filter by node type");

        // Index de edges por source
        manifest.addIndex("edgesBySource", IndexType.MULTIMAP,
                "$.processGraph.edges[*].source", "Outbound edge traversal");

        // Index de edges por target
        manifest.addIndex("edgesByTarget", IndexType.MULTIMAP,
                "$.processGraph.edges[*].target", "Inbound edge traversal");

        // Index de data types por nome
        manifest.addIndex("dataTypesByName", IndexType.HASHMAP,
                "$.dataTypes[*].name", "Data type lookup by name");

        // Index de scripts por language
        manifest.addIndex("scriptsByLanguage", IndexType.GROUPING,
                "$.logic.scripts[*].language", "Scripts by programming language");

        // Configurar paginação
        manifest.pagination = PaginationConfig.createDefault();

        // Criar views materializadas
        manifest.createMaterializedViews(graph, dataTypes, logic);

        return manifest;
    }

    // CORRIGIDO: método addIndex agora usa IndexType correto
    private void addIndex(String name, IndexType type, String path, String description) {
        IndexDefinition index = new IndexDefinition(name, type, path, description);
        indices.put(name, index);
    }

    private void createMaterializedViews(ProcessGraphV2 graph, List<DataTypeDefinitionV2> dataTypes,
                                         ProcessLogicV2 logic) {
        // View de estatísticas do grafo
        if (graph != null) {
            Map<String, Object> graphStats = new HashMap<>();
            graphStats.put("nodeCount", graph.getNodes() != null ? graph.getNodes().size() : 0);
            graphStats.put("edgeCount", graph.getEdges() != null ? graph.getEdges().size() : 0);
            materializedViews.put("graphStatistics", graphStats);
        }

        // View de summary de data types
        if (dataTypes != null) {
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalCount", dataTypes.size());
            summary.put("byVersion", groupDataTypesByVersion(dataTypes));
            summary.put("avgDescriptionLength", calculateAvgDescriptionLength(dataTypes));
            materializedViews.put("dataTypesSummary", summary);
        }

        // View de summary de scripts - CORRIGIDO: usar ProcessLogicV2.LogicScriptV2
        if (logic != null && logic.getScripts() != null) {
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalCount", logic.getScripts().size());
            summary.put("byLanguage", groupScriptsByLanguage(logic.getScripts()));
            summary.put("complexityDistribution", analyzeScriptComplexity(logic.getScripts()));
            materializedViews.put("scriptsSummary", summary);
        }
    }

    private Map<String, Long> groupDataTypesByVersion(List<DataTypeDefinitionV2> dataTypes) {
        Map<String, Long> result = new HashMap<>();
        for (DataTypeDefinitionV2 dt : dataTypes) {
            if (dt.getVersion() != null) {
                result.put(dt.getVersion(), result.getOrDefault(dt.getVersion(), 0L) + 1);
            }
        }
        return result;
    }

    private double calculateAvgDescriptionLength(List<DataTypeDefinitionV2> dataTypes) {
        if (dataTypes.isEmpty()) return 0.0;

        int total = 0;
        int count = 0;
        for (DataTypeDefinitionV2 dt : dataTypes) {
            if (dt.getDescription() != null) {
                total += dt.getDescription().length();
                count++;
            }
        }
        return count > 0 ? (double) total / count : 0.0;
    }

    // CORRIGIDO: usar ProcessLogicV2.LogicScriptV2
    private Map<String, Long> groupScriptsByLanguage(List<ProcessLogicV2.LogicScriptV2> scripts) {
        Map<String, Long> result = new HashMap<>();
        for (ProcessLogicV2.LogicScriptV2 script : scripts) {
            if (script.getLanguage() != null) {
                String lang = script.getLanguage().toString();
                result.put(lang, result.getOrDefault(lang, 0L) + 1);
            }
        }
        return result;
    }

    // CORRIGIDO: usar ProcessLogicV2.LogicScriptV2
    private Map<String, Integer> analyzeScriptComplexity(List<ProcessLogicV2.LogicScriptV2> scripts) {
        Map<String, Integer> complexity = new HashMap<>();
        complexity.put("simple", 0);
        complexity.put("moderate", 0);
        complexity.put("complex", 0);

        for (ProcessLogicV2.LogicScriptV2 script : scripts) {
            int size = script.getContent() != null ? script.getContent().length() : 0;

            if (size < 500) {
                complexity.put("simple", complexity.get("simple") + 1);
            } else if (size < 2000) {
                complexity.put("moderate", complexity.get("moderate") + 1);
            } else {
                complexity.put("complex", complexity.get("complex") + 1);
            }
        }

        return complexity;
    }

    // Getters and setters
    public Map<String, IndexDefinition> getIndices() {
        return indices;
    }

    public void setIndices(Map<String, IndexDefinition> indices) {
        this.indices = indices;
    }

    public PaginationConfig getPagination() {
        return pagination;
    }

    public void setPagination(PaginationConfig pagination) {
        this.pagination = pagination;
    }

    public Map<String, Object> getMaterializedViews() {
        return materializedViews;
    }

    public void setMaterializedViews(Map<String, Object> materializedViews) {
        this.materializedViews = materializedViews;
    }
}