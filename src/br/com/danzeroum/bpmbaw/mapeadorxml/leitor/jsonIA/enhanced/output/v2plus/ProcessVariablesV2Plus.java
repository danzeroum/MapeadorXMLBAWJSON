package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * ProcessVariablesV2Plus - Container para todas as variáveis do processo
 *
 * Esta classe gerencia coleções de variáveis organizadas por escopo:
 * - Input Variables: Variáveis de entrada do processo
 * - Output Variables: Variáveis de saída do processo
 * - Private Variables: Variáveis internas/locais
 *
 * @version 3.0.0 - Completamente refatorada
 * @author Enhanced BAW Analysis System
 */
@JsonPropertyOrder({
        "inputs", "outputs", "privates", "inputVariables",
        "outputVariables", "privateVariables", "metadata"
})
public class ProcessVariablesV2Plus {

    // =========================================================================
    // COLEÇÕES DE VARIÁVEIS - Nomes canônicos do formato final
    // =========================================================================

    @JsonProperty("inputs")
    private List<ProcessVariableV2Plus> inputs;

    @JsonProperty("outputs")
    private List<ProcessVariableV2Plus> outputs;

    @JsonProperty("privates")
    private List<ProcessVariableV2Plus> privates;

    // =========================================================================
    // COLEÇÕES LEGADAS - Para compatibilidade com extractors existentes
    // =========================================================================

    @JsonProperty("inputVariables")
    private List<ProcessVariableV2Plus> inputVariables;

    @JsonProperty("outputVariables")
    private List<ProcessVariableV2Plus> outputVariables;

    @JsonProperty("privateVariables")
    private List<ProcessVariableV2Plus> privateVariables;

    // =========================================================================
    // METADATA
    // =========================================================================

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    /**
     * Constructor padrão - inicializa todas as listas
     */
    public ProcessVariablesV2Plus() {
        // Listas canônicas
        this.inputs = new ArrayList<ProcessVariableV2Plus>();
        this.outputs = new ArrayList<ProcessVariableV2Plus>();
        this.privates = new ArrayList<ProcessVariableV2Plus>();

        // Listas legadas
        this.inputVariables = new ArrayList<ProcessVariableV2Plus>();
        this.outputVariables = new ArrayList<ProcessVariableV2Plus>();
        this.privateVariables = new ArrayList<ProcessVariableV2Plus>();

        this.metadata = new HashMap<String, Object>();
    }

    // =========================================================================
    // GETTERS E SETTERS - Formato Canônico
    // =========================================================================

    public List<ProcessVariableV2Plus> getInputs() {
        return inputs != null ? inputs : new ArrayList<ProcessVariableV2Plus>();
    }

    public void setInputs(List<ProcessVariableV2Plus> inputs) {
        this.inputs = inputs;
        // Sincronizar com formato legado
        this.inputVariables = inputs;
    }

    public List<ProcessVariableV2Plus> getOutputs() {
        return outputs != null ? outputs : new ArrayList<ProcessVariableV2Plus>();
    }

    public void setOutputs(List<ProcessVariableV2Plus> outputs) {
        this.outputs = outputs;
        // Sincronizar com formato legado
        this.outputVariables = outputs;
    }

    public List<ProcessVariableV2Plus> getPrivates() {
        return privates != null ? privates : new ArrayList<ProcessVariableV2Plus>();
    }

    public void setPrivates(List<ProcessVariableV2Plus> privates) {
        this.privates = privates;
        // Sincronizar com formato legado
        this.privateVariables = privates;
    }

    // =========================================================================
    // GETTERS E SETTERS - Formato Legado (Compatibilidade)
    // =========================================================================

    public List<ProcessVariableV2Plus> getInputVariables() {
        // Retornar a lista canônica se a legada estiver vazia
        if ((inputVariables == null || inputVariables.isEmpty()) && inputs != null) {
            return inputs;
        }
        return inputVariables != null ? inputVariables : new ArrayList<ProcessVariableV2Plus>();
    }

    public void setInputVariables(List<ProcessVariableV2Plus> inputVariables) {
        this.inputVariables = inputVariables;
        // Sincronizar com formato canônico
        this.inputs = inputVariables;
    }

    public List<ProcessVariableV2Plus> getOutputVariables() {
        // Retornar a lista canônica se a legada estiver vazia
        if ((outputVariables == null || outputVariables.isEmpty()) && outputs != null) {
            return outputs;
        }
        return outputVariables != null ? outputVariables : new ArrayList<ProcessVariableV2Plus>();
    }

    public void setOutputVariables(List<ProcessVariableV2Plus> outputVariables) {
        this.outputVariables = outputVariables;
        // Sincronizar com formato canônico
        this.outputs = outputVariables;
    }

    public List<ProcessVariableV2Plus> getPrivateVariables() {
        // Retornar a lista canônica se a legada estiver vazia
        if ((privateVariables == null || privateVariables.isEmpty()) && privates != null) {
            return privates;
        }
        return privateVariables != null ? privateVariables : new ArrayList<ProcessVariableV2Plus>();
    }

    public void setPrivateVariables(List<ProcessVariableV2Plus> privateVariables) {
        this.privateVariables = privateVariables;
        // Sincronizar com formato canônico
        this.privates = privateVariables;
    }

    public Map<String, Object> getMetadata() {
        if (metadata == null) {
            metadata = new HashMap<String, Object>();
        }
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    // =========================================================================
    // MÉTODOS DE ADIÇÃO
    // =========================================================================

    /**
     * Adiciona variável de entrada
     */
    public void addInputVariable(ProcessVariableV2Plus variable) {
        if (variable == null) return;

        if (inputs == null) inputs = new ArrayList<ProcessVariableV2Plus>();
        if (inputVariables == null) inputVariables = new ArrayList<ProcessVariableV2Plus>();

        inputs.add(variable);
        inputVariables.add(variable);
    }

    /**
     * Adiciona variável de entrada com parâmetros
     */
    public void addInputVariable(String name, String type, String description, boolean isList) {
        ProcessVariableV2Plus var = new ProcessVariableV2Plus(name, type, description, isList);
        addInputVariable(var);
    }

    /**
     * Adiciona variável de saída
     */
    public void addOutputVariable(ProcessVariableV2Plus variable) {
        if (variable == null) return;

        if (outputs == null) outputs = new ArrayList<ProcessVariableV2Plus>();
        if (outputVariables == null) outputVariables = new ArrayList<ProcessVariableV2Plus>();

        outputs.add(variable);
        outputVariables.add(variable);
    }

    /**
     * Adiciona variável de saída com parâmetros
     */
    public void addOutputVariable(String name, String type, String description, boolean isList) {
        ProcessVariableV2Plus var = new ProcessVariableV2Plus(name, type, description, isList);
        addOutputVariable(var);
    }

    /**
     * Adiciona variável privada
     */
    public void addPrivateVariable(ProcessVariableV2Plus variable) {
        if (variable == null) return;

        if (privates == null) privates = new ArrayList<ProcessVariableV2Plus>();
        if (privateVariables == null) privateVariables = new ArrayList<ProcessVariableV2Plus>();

        privates.add(variable);
        privateVariables.add(variable);
    }

    /**
     * Adiciona variável privada com parâmetros
     */
    public void addPrivateVariable(String name, String type, String description, boolean isList) {
        ProcessVariableV2Plus var = new ProcessVariableV2Plus(name, type, description, isList);
        addPrivateVariable(var);
    }

    // =========================================================================
    // MÉTODOS DE NORMALIZAÇÃO
    // =========================================================================

    /**
     * Sincroniza listas canônicas com legadas
     */
    public void synchronizeLists() {
        // Se as listas canônicas estão vazias mas as legadas têm dados
        if ((inputs == null || inputs.isEmpty()) && inputVariables != null && !inputVariables.isEmpty()) {
            inputs = new ArrayList<ProcessVariableV2Plus>(inputVariables);
        }

        if ((outputs == null || outputs.isEmpty()) && outputVariables != null && !outputVariables.isEmpty()) {
            outputs = new ArrayList<ProcessVariableV2Plus>(outputVariables);
        }

        if ((privates == null || privates.isEmpty()) && privateVariables != null && !privateVariables.isEmpty()) {
            privates = new ArrayList<ProcessVariableV2Plus>(privateVariables);
        }

        // Vice-versa
        if ((inputVariables == null || inputVariables.isEmpty()) && inputs != null && !inputs.isEmpty()) {
            inputVariables = new ArrayList<ProcessVariableV2Plus>(inputs);
        }

        if ((outputVariables == null || outputVariables.isEmpty()) && outputs != null && !outputs.isEmpty()) {
            outputVariables = new ArrayList<ProcessVariableV2Plus>(outputs);
        }

        if ((privateVariables == null || privateVariables.isEmpty()) && privates != null && !privates.isEmpty()) {
            privateVariables = new ArrayList<ProcessVariableV2Plus>(privates);
        }
    }

    /**
     * Normaliza tipos de todas as variáveis
     */
    public void normalizeAllVariableTypes() {
        // Normalizar inputs
        for (ProcessVariableV2Plus var : getInputs()) {
            var.normalizeTypeRef();
        }

        // Normalizar outputs
        for (ProcessVariableV2Plus var : getOutputs()) {
            var.normalizeTypeRef();
        }

        // Normalizar privates
        for (ProcessVariableV2Plus var : getPrivates()) {
            var.normalizeTypeRef();
        }
    }

    // =========================================================================
    // MÉTODOS DE BUSCA
    // =========================================================================

    /**
     * Busca variável por nome em todas as listas
     */
    public ProcessVariableV2Plus findVariable(String name) {
        if (name == null) return null;

        // Buscar em inputs
        for (ProcessVariableV2Plus var : getInputs()) {
            if (name.equals(var.getName())) return var;
        }

        // Buscar em outputs
        for (ProcessVariableV2Plus var : getOutputs()) {
            if (name.equals(var.getName())) return var;
        }

        // Buscar em privates
        for (ProcessVariableV2Plus var : getPrivates()) {
            if (name.equals(var.getName())) return var;
        }

        return null;
    }

    /**
     * Verifica se variável existe
     */
    public boolean hasVariable(String name) {
        return findVariable(name) != null;
    }

    /**
     * Retorna todas as variáveis em uma lista única
     */
    public List<ProcessVariableV2Plus> getAllVariables() {
        List<ProcessVariableV2Plus> all = new ArrayList<ProcessVariableV2Plus>();
        all.addAll(getInputs());
        all.addAll(getOutputs());
        all.addAll(getPrivates());
        return all;
    }

    // =========================================================================
    // ESTATÍSTICAS
    // =========================================================================

    /**
     * Conta total de variáveis
     */
    public int getTotalVariableCount() {
        return getInputs().size() + getOutputs().size() + getPrivates().size();
    }

    /**
     * Retorna estatísticas das variáveis
     */
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<String, Integer>();
        stats.put("inputs", getInputs().size());
        stats.put("outputs", getOutputs().size());
        stats.put("privates", getPrivates().size());
        stats.put("total", getTotalVariableCount());

        // Contar por tipo
        int lists = 0;
        int singles = 0;
        for (ProcessVariableV2Plus var : getAllVariables()) {
            if (var.isList()) {
                lists++;
            } else {
                singles++;
            }
        }
        stats.put("lists", lists);
        stats.put("singles", singles);

        return stats;
    }

    // =========================================================================
    // VALIDAÇÃO
    // =========================================================================

    /**
     * Valida todas as variáveis
     */
    public boolean validate() {
        // Sincronizar primeiro
        synchronizeLists();

        // Validar inputs
        for (ProcessVariableV2Plus var : getInputs()) {
            if (!var.validate()) {
                addMetadata("validation_error", "Invalid input variable: " + var.getName());
                return false;
            }
        }

        // Validar outputs
        for (ProcessVariableV2Plus var : getOutputs()) {
            if (!var.validate()) {
                addMetadata("validation_error", "Invalid output variable: " + var.getName());
                return false;
            }
        }

        // Validar privates
        for (ProcessVariableV2Plus var : getPrivates()) {
            if (!var.validate()) {
                addMetadata("validation_error", "Invalid private variable: " + var.getName());
                return false;
            }
        }

        return true;
    }

    /**
     * Adiciona metadado
     */
    public void addMetadata(String key, Object value) {
        if (metadata == null) {
            metadata = new HashMap<String, Object>();
        }
        metadata.put(key, value);
    }

    // =========================================================================
    // MÉTODOS UTILITÁRIOS
    // =========================================================================

    /**
     * Limpa todas as variáveis
     */
    public void clear() {
        if (inputs != null) inputs.clear();
        if (outputs != null) outputs.clear();
        if (privates != null) privates.clear();
        if (inputVariables != null) inputVariables.clear();
        if (outputVariables != null) outputVariables.clear();
        if (privateVariables != null) privateVariables.clear();
    }

    /**
     * Verifica se está vazio
     */
    public boolean isEmpty() {
        return getTotalVariableCount() == 0;
    }

    @Override
    public String toString() {
        Map<String, Integer> stats = getStatistics();
        return String.format("ProcessVariablesV2Plus{inputs=%d, outputs=%d, privates=%d, total=%d}",
                stats.get("inputs"), stats.get("outputs"),
                stats.get("privates"), stats.get("total"));
    }
}