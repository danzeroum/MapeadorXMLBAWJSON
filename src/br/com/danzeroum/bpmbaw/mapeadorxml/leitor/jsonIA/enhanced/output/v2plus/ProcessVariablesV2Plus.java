package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * VERSÃO CORRIGIDA - ProcessVariablesV2Plus
 * Container para variáveis de processo compatível com V2Plus extractors
 *
 * CORREÇÕES:
 * - Listas tipadas corretamente
 * - Métodos add com assinatura correta
 * - Validação implementada
 */
public class ProcessVariablesV2Plus {

    @JsonProperty("typeId")
    private String typeId;

    @JsonProperty("typeRef")
    private String typeRef;

    @JsonProperty("cardinality")
    private String cardinality;

    // CORRIGIDO: Usar tipo específico nas listas
    private List<ProcessDefinitionV2Plus.VariableDefinitionV2Plus> input;
    private List<ProcessDefinitionV2Plus.VariableDefinitionV2Plus> output;
    private List<ProcessDefinitionV2Plus.VariableDefinitionV2Plus> privateVars;
    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    public ProcessVariablesV2Plus() {
        this.input = new ArrayList<>();
        this.output = new ArrayList<>();
        this.privateVars = new ArrayList<>();
    }

    // =========================================================================
    // GETTERS AND SETTERS - CORRIGIDOS
    // =========================================================================

    public String getTypeRef() { return typeRef; }
    public void setTypeRef(String typeRef) { this.typeRef = typeRef; }

    public String getCardinality() { return cardinality; }
    public void setCardinality(String cardinality) { this.cardinality = cardinality; }

    public List<ProcessDefinitionV2Plus.VariableDefinitionV2Plus> getInput() {
        return input != null ? input : new ArrayList<>();
    }

    public void setInput(List<ProcessDefinitionV2Plus.VariableDefinitionV2Plus> input) {
        this.input = input != null ? input : new ArrayList<>();
    }

    public List<ProcessDefinitionV2Plus.VariableDefinitionV2Plus> getOutput() {
        return output != null ? output : new ArrayList<>();
    }

    public void setOutput(List<ProcessDefinitionV2Plus.VariableDefinitionV2Plus> output) {
        this.output = output != null ? output : new ArrayList<>();
    }

    public List<ProcessDefinitionV2Plus.VariableDefinitionV2Plus> getPrivateVars() {
        return privateVars != null ? privateVars : new ArrayList<>();
    }

    public void setPrivateVars(List<ProcessDefinitionV2Plus.VariableDefinitionV2Plus> privateVars) {
        this.privateVars = privateVars != null ? privateVars : new ArrayList<>();
    }

    // =========================================================================
    // BUSINESS METHODS - ASSINATURA CORRIGIDA
    // =========================================================================

    /**
     * Adiciona variável de entrada - CORRIGIDO com 5 parâmetros
     */
    public void addInputVariable(String name, String typeRef, String cardinality, boolean nullable, String description) {
        if (input == null) {
            input = new ArrayList<>();
        }
        ProcessDefinitionV2Plus.VariableDefinitionV2Plus variable =
                new ProcessDefinitionV2Plus.VariableDefinitionV2Plus(name, typeRef, cardinality, nullable, description);
        input.add(variable);
    }

    /**
     * Adiciona variável de saída - CORRIGIDO com 5 parâmetros
     */
    public void addOutputVariable(String name, String typeRef, String cardinality, boolean nullable, String description) {
        if (output == null) {
            output = new ArrayList<>();
        }
        ProcessDefinitionV2Plus.VariableDefinitionV2Plus variable =
                new ProcessDefinitionV2Plus.VariableDefinitionV2Plus(name, typeRef, cardinality, nullable, description);
        output.add(variable);
    }

    /**
     * Adiciona variável privada - CORRIGIDO com 5 parâmetros
     */
    public void addPrivateVariable(String name, String typeRef, String cardinality, boolean nullable, String description) {
        if (privateVars == null) {
            privateVars = new ArrayList<>();
        }
        ProcessDefinitionV2Plus.VariableDefinitionV2Plus variable =
                new ProcessDefinitionV2Plus.VariableDefinitionV2Plus(name, typeRef, cardinality, nullable, description);
        privateVars.add(variable);
    }

    // =========================================================================
    // UTILITY METHODS
    // =========================================================================

    /**
     * Método validate obrigatório
     */
    public boolean validate() {
        // Validar que listas não são null
        if (input == null || output == null || privateVars == null) {
            return false;
        }

        // Validar cada variável de entrada
        for (ProcessDefinitionV2Plus.VariableDefinitionV2Plus var : input) {
            if (var == null || !var.validate()) {
                return false;
            }
        }

        // Validar cada variável de saída
        for (ProcessDefinitionV2Plus.VariableDefinitionV2Plus var : output) {
            if (var == null || !var.validate()) {
                return false;
            }
        }

        // Validar cada variável privada
        for (ProcessDefinitionV2Plus.VariableDefinitionV2Plus var : privateVars) {
            if (var == null || !var.validate()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Conta total de variáveis
     */
    public int getTotalVariableCount() {
        return getInput().size() + getOutput().size() + getPrivateVars().size();
    }

    /**
     * Busca variável por nome
     */
    public ProcessDefinitionV2Plus.VariableDefinitionV2Plus findVariable(String name) {
        if (name == null) return null;

        // Buscar em input
        for (ProcessDefinitionV2Plus.VariableDefinitionV2Plus var : getInput()) {
            if (name.equals(var.getName())) {
                return var;
            }
        }

        // Buscar em output
        for (ProcessDefinitionV2Plus.VariableDefinitionV2Plus var : getOutput()) {
            if (name.equals(var.getName())) {
                return var;
            }
        }

        // Buscar em private
        for (ProcessDefinitionV2Plus.VariableDefinitionV2Plus var : getPrivateVars()) {
            if (name.equals(var.getName())) {
                return var;
            }
        }

        return null;
    }

    /**
     * Verifica se variável existe
     */
    public boolean hasVariable(String name) {
        return findVariable(name) != null;
    }

    @Override
    public String toString() {
        return String.format("ProcessVariablesV2Plus{input: %d, output: %d, private: %d}",
                getInput().size(), getOutput().size(), getPrivateVars().size());
    }
}