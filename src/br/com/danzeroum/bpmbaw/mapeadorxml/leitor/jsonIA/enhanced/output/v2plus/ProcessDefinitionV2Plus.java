package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BpdParameter;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.PrivateVariable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * VERSÃO FINAL - ProcessDefinitionV2Plus
 * Contém a estrutura central do processo, com a definição de variáveis
 * semanticamente rica como uma classe interna estática para manter a compatibilidade
 * e resolver os erros de compilação.
 *
 * @version 3.1.0
 */
public class ProcessDefinitionV2Plus {

    @JsonProperty("$id")
    private String id;
    private String name;
    private String description;
    private String version;

    private ProcessVariablesV2Plus variables;
    private ProcessGraphV2Plus graph;
    private List<ProcessConditionV2Plus> conditions;
    private ProcessMappingsV2Plus mappings;
    private ProcessLogicV2Plus logic;

    // =========================================================================
    // CONSTRUTORES E FACTORY
    // =========================================================================

    public ProcessDefinitionV2Plus() {
        this.variables = new ProcessVariablesV2Plus();
        this.conditions = new ArrayList<>();
        this.mappings = new ProcessMappingsV2Plus();
        this.logic = new ProcessLogicV2Plus();
    }

    public static ProcessDefinitionV2Plus create(String processId) {
        ProcessDefinitionV2Plus definition = new ProcessDefinitionV2Plus();
        definition.setId(processId);
        definition.setName("Process " + processId);
        definition.setGraph(ProcessGraphV2Plus.create(processId));
        return definition;
    }

    // =========================================================================
    // GETTERS E SETTERS PRINCIPAIS
    // =========================================================================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public ProcessVariablesV2Plus getVariables() { return variables != null ? variables : new ProcessVariablesV2Plus(); }
    public void setVariables(ProcessVariablesV2Plus variables) { this.variables = variables; }
    public ProcessGraphV2Plus getGraph() { return graph; }
    public void setGraph(ProcessGraphV2Plus graph) { this.graph = graph; }
    public List<ProcessConditionV2Plus> getConditions() { return conditions; }
    public void setConditions(List<ProcessConditionV2Plus> conditions) { this.conditions = conditions; }
    public ProcessMappingsV2Plus getMappings() { return mappings; }
    public void setMappings(ProcessMappingsV2Plus mappings) { this.mappings = mappings; }
    public ProcessLogicV2Plus getLogic() { return logic; }
    public void setLogic(ProcessLogicV2Plus logic) { this.logic = logic; }

    // =========================================================================
    // MÉTODOS DE VALIDAÇÃO E ESTATÍSTICAS
    // =========================================================================

    public boolean isValid() {
        if (id == null || id.trim().isEmpty()) return false;
        if (variables == null || !variables.validate()) return false;
        if (graph == null) return false;
        return true;
    }

    public List<String> validateDetailed() {
        List<String> errors = new ArrayList<>();
        if (id == null || id.trim().isEmpty()) errors.add("ProcessDefinition ID is required");
        if (variables == null) errors.add("Variables are required");
        else if (!variables.validate()) errors.add("Variables validation failed");
        if (graph == null) errors.add("Process graph is required");
        return errors;
    }

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
    // CLASSE INTERNA ESTÁTICA PARA DEFINIÇÃO DE VARIÁVEIS (CONFORME RECOMENDADO)
    // =========================================================================
    @JsonPropertyOrder({"name", "typeRef", "cardinality", "nullable", "description"})
    public static class VariableDefinitionV2Plus {
        private String name;
        private String typeRef;
        private String cardinality;
        private boolean nullable;
        private String description;

        public VariableDefinitionV2Plus(String name, String typeRef, String cardinality, boolean nullable, String description) {
            this.name = name;
            this.typeRef = typeRef;
            this.cardinality = cardinality;
            this.nullable = nullable;
            this.description = description;
        }

        /**
         * Factory method para converter um BpdParameter (variável de entrada/saída do BPD).
         */
        public static VariableDefinitionV2Plus fromBpdParameter(BpdParameter param) {
            return new VariableDefinitionV2Plus(
                    param.getName(),
                    convertClassIdToTypeRef(param.getClassId()),
                    param.isArrayOf() ? "many" : "one",
                    !param.isHasDefault(), // Lógica de nulidade: se tem default, não é nulo.
                    extractDescription(param.getDocumentation())
            );
        }

        /**
         * Factory method para converter uma PrivateVariable de um Pool.
         */
        public static VariableDefinitionV2Plus fromPrivateVariable(PrivateVariable pVar) {
            // CORREÇÃO: A classe PrivateVariable não possui o método getDescription().
            // Usamos um texto padrão ou verificamos outros campos se aplicável.
            String desc = (pVar.getName() != null) ? "Private variable: " + pVar.getName() : "Private process variable.";
            return new VariableDefinitionV2Plus(
                    pVar.getName(),
                    convertClassIdToTypeRef(pVar.getClassId()),
                    pVar.isArrayOf() ? "many" : "one",
                    !pVar.isHasDefault(),
                    desc
            );
        }

        // CORREÇÃO: Este método agora está dentro da classe interna, onde é chamado.
        private static String extractDescription(String documentation) {
            return (documentation != null && !documentation.trim().isEmpty()) ? documentation : "No description provided.";
        }

        public static String convertClassIdToTypeRef(String classId) {
            if (classId == null || classId.trim().isEmpty()) return "dt:String@1";
            return "dt:" + classId + "@1";
        }

        public boolean validate() {
            return name != null && !name.trim().isEmpty() &&
                    typeRef != null && !typeRef.trim().isEmpty() &&
                    cardinality != null && ("one".equals(cardinality) || "many".equals(cardinality)) &&
                    description != null;
        }

        // Getters e Setters
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
    }

    // =========================================================================
    // CLASSE INTERNA PARA ESTATÍSTICAS
    // =========================================================================

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
}