package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * VERSÃO CORRIGIDA - ProcessDefinitionV2Plus
 * Definição de processo compatível com Java 8 e IBM BAW antigo/novo
 *
 * CORREÇÕES PRINCIPAIS:
 * - Listas com tipos corretos
 * - Construtor público
 * - Métodos validate() implementados
 * - Compatibilidade com V2Plus extractors
 */
public class ProcessDefinitionV2Plus {

    @JsonProperty("$id")
    private String id;

    private String name;
    private String description;
    private String version;

    // CORRIGIDO: Usar tipos específicos nas listas
    private ProcessVariablesV2Plus variables;
    private ProcessGraphV2Plus graph;
    private List<ProcessConditionV2Plus> conditions;
    private ProcessMappingsV2Plus mappings;
    private ProcessLogicV2Plus logic;

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    /**
     * Constructor público obrigatório para V2Plus
     */
    public ProcessDefinitionV2Plus() {
        this.variables = new ProcessVariablesV2Plus();
        this.conditions = new ArrayList<>();
        this.mappings = new ProcessMappingsV2Plus();
        this.logic = new ProcessLogicV2Plus();
    }

    /**
     * Constructor com ID
     */
    public ProcessDefinitionV2Plus(String id) {
        this();
        this.id = id;
    }

    /**
     * Factory method para criação consistente
     */
    public static ProcessDefinitionV2Plus create(String processId) {
        ProcessDefinitionV2Plus definition = new ProcessDefinitionV2Plus();
        definition.setId(processId);
        definition.setName("Process " + processId);
        definition.setGraph(ProcessGraphV2Plus.create(processId));
        return definition;
    }

    // =========================================================================
    // GETTERS AND SETTERS
    // =========================================================================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public ProcessVariablesV2Plus getVariables() {
        return variables != null ? variables : new ProcessVariablesV2Plus();
    }

    public void setVariables(ProcessVariablesV2Plus variables) {
        this.variables = variables != null ? variables : new ProcessVariablesV2Plus();
    }

    public ProcessGraphV2Plus getGraph() {
        return graph != null ? graph : ProcessGraphV2Plus.create(this.id != null ? this.id : "default");
    }

    public void setGraph(ProcessGraphV2Plus graph) {
        this.graph = graph;
    }

    public List<ProcessConditionV2Plus> getConditions() {
        return conditions != null ? conditions : new ArrayList<>();
    }

    public void setConditions(List<ProcessConditionV2Plus> conditions) {
        this.conditions = conditions != null ? conditions : new ArrayList<>();
    }

    public ProcessMappingsV2Plus getMappings() {
        return mappings != null ? mappings : new ProcessMappingsV2Plus();
    }

    public void setMappings(ProcessMappingsV2Plus mappings) {
        this.mappings = mappings != null ? mappings : new ProcessMappingsV2Plus();
    }

    public ProcessLogicV2Plus getLogic() {
        return logic != null ? logic : new ProcessLogicV2Plus();
    }

    public void setLogic(ProcessLogicV2Plus logic) {
        this.logic = logic != null ? logic : new ProcessLogicV2Plus();
    }

    // =========================================================================
    // BUSINESS METHODS
    // =========================================================================

    /**
     * Adiciona variável de entrada - CORRIGIDO
     */
    public void addInputVariable(String name, String typeRef, String cardinality, boolean nullable, String description) {
        if (variables == null) {
            variables = new ProcessVariablesV2Plus();
        }
        variables.addInputVariable(name, typeRef, cardinality, nullable, description);
    }

    /**
     * Adiciona variável de saída - CORRIGIDO
     */
    public void addOutputVariable(String name, String typeRef, String cardinality, boolean nullable, String description) {
        if (variables == null) {
            variables = new ProcessVariablesV2Plus();
        }
        variables.addOutputVariable(name, typeRef, cardinality, nullable, description);
    }

    /**
     * Adiciona variável privada - CORRIGIDO
     */
    public void addPrivateVariable(String name, String typeRef, String cardinality, boolean nullable, String description) {
        if (variables == null) {
            variables = new ProcessVariablesV2Plus();
        }
        variables.addPrivateVariable(name, typeRef, cardinality, nullable, description);
    }

    /**
     * Método VALIDATE obrigatório para V2Plus
     */
    public boolean validate() {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        if (variables == null || !variables.validate()) {
            return false;
        }
        if (graph == null) {
            return false;
        }
        return true;
    }

    /**
     * Método isValid() - alias para validate()
     */
    public boolean isValid() {
        return validate();
    }

    /**
     * Obtém estatísticas da definição
     */
    public ProcessDefinitionStats getStats() {
        ProcessDefinitionStats stats = new ProcessDefinitionStats();

        if (variables != null) {
            stats.inputVariables = variables.getInput().size();
            stats.outputVariables = variables.getOutput().size();
            stats.privateVariables = variables.getPrivateVars().size();
        }

        if (graph != null) {
            stats.nodeCount = graph.getNodes() != null ? graph.getNodes().size() : 0;
            stats.edgeCount = graph.getEdges() != null ? graph.getEdges().size() : 0;
        }

        if (logic != null) {
            stats.scriptCount = logic.getItems() != null ? logic.getItems().size() : 0;
            stats.validationCount = logic.getValidations() != null ? logic.getValidations().size() : 0;
        }

        if (conditions != null) {
            stats.conditionCount = conditions.size();
        }

        return stats;
    }

    // =========================================================================
    // INNER CLASSES
    // =========================================================================

    /**
     * Estatísticas da definição do processo
     */
    public static class ProcessDefinitionStats {
        public int inputVariables;
        public int outputVariables;
        public int privateVariables;
        public int nodeCount;
        public int edgeCount;
        public int scriptCount;
        public int validationCount;
        public int conditionCount;

        @Override
        public String toString() {
            return String.format(
                    "ProcessDefinitionStats{variables: %d+%d+%d, nodes: %d, edges: %d, scripts: %d, validations: %d, conditions: %d}",
                    inputVariables, outputVariables, privateVariables,
                    nodeCount, edgeCount, scriptCount, validationCount, conditionCount
            );
        }
    }

    /**
     * Validação detalhada que retorna lista de erros específicos
     */
    public List<String> validateDetailed() {
        List<String> errors = new ArrayList<>();

        if (id == null || id.trim().isEmpty()) {
            errors.add("ProcessDefinition ID is required");
        }

        if (variables == null) {
            errors.add("Variables are required");
        } else if (!variables.validate()) {
            errors.add("Variables validation failed");
            // Adicionar detalhes específicos das variáveis se necessário
            if (variables.getInput().isEmpty() && variables.getOutput().isEmpty()) {
                errors.add("At least one input or output variable is required");
            }
        }

        if (graph == null) {
            errors.add("Process graph is required");
        }

        return errors;
    }


    /**
     * Método genérico addVariable para compatibilidade
     * Determina automaticamente se é input, output ou private
     */
    public void addVariable(String category, String name, String typeRef, boolean nullable) {
        String description = "Auto-generated variable: " + name;
        String cardinality = "one"; // Default cardinality

        switch (category.toLowerCase()) {
            case "input":
                addInputVariable(name, typeRef, cardinality, nullable, description);
                break;
            case "output":
                addOutputVariable(name, typeRef, cardinality, nullable, description);
                break;
            case "private":
            default:
                addPrivateVariable(name, typeRef, cardinality, nullable, description);
                break;
        }
    }

    /**
     * Definição de variável V2Plus - CORRIGIDA
     */
    public static class VariableDefinitionV2Plus {
        private String name;
        private String typeRef;
        private String cardinality;
        private boolean nullable;
        private String description;

        public VariableDefinitionV2Plus() {
            this.cardinality = "one";
            this.nullable = false;
        }

        public VariableDefinitionV2Plus(String name, String typeRef, String cardinality, boolean nullable, String description) {
            this.name = name;
            this.typeRef = typeRef;
            this.cardinality = cardinality != null ? cardinality : "one";
            this.nullable = nullable;
            this.description = description;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getTypeRef() { return typeRef; }
        public void setTypeRef(String typeRef) { this.typeRef = typeRef; }

        public String getCardinality() { return cardinality; }
        public void setCardinality(String cardinality) { this.cardinality = cardinality; }

        public boolean isNullable() { return nullable; }
        public void setNullable(boolean nullable) { this.nullable = nullable; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        /**
         * Validação da variável
         */
        public boolean validate() {
            return name != null && !name.trim().isEmpty() &&
                    typeRef != null && !typeRef.trim().isEmpty();
        }
    }
}