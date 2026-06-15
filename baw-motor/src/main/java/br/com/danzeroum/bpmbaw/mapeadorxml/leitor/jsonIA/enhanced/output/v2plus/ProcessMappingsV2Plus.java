package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * (Versão Final e Corrigida)
 * Representa a estrutura de mapeamentos, com listas e métodos auxiliares
 * para adicionar novos mapeamentos de forma segura.
 */
public class ProcessMappingsV2Plus {

    @JsonProperty("exprLang")
    private String exprLang;

    // Os nomes aqui são os do formato final.
    // O parser legado pode usar campos temporários com outros nomes.
    @JsonProperty("inputs")
    private List<InputMappingV2Plus> inputs;

    @JsonProperty("outputs")
    private List<OutputMappingV2Plus> outputs;

    @JsonProperty("transformations")
    private List<TransformationRuleV2Plus> transformations;

    // Campos temporários para o parser legado (podem ser removidos após a transformação)
    private List<InputMappingV2Plus> inputMappings;
    private List<OutputMappingV2Plus> outputMappings;

    public ProcessMappingsV2Plus() {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.transformations = new ArrayList<>();
        // Campos legados
        this.inputMappings = new ArrayList<>();
        this.outputMappings = new ArrayList<>();
        this.exprLang = "cel";
    }

    // --- MÉTODOS ADICIONADOS PARA CORRIGIR O ERRO ---

    public void addInputMapping(String sourceField, String targetField, String description) {
        if (this.inputMappings == null) {
            this.inputMappings = new ArrayList<>();
        }
        this.inputMappings.add(new InputMappingV2Plus(sourceField, targetField, description));
    }

    public void addOutputMapping(String sourceField, String targetField, String alias, String description) {
        if (this.outputMappings == null) {
            this.outputMappings = new ArrayList<>();
        }
        this.outputMappings.add(new OutputMappingV2Plus(sourceField, targetField, alias, description));
    }

    // --- FIM DA ADIÇÃO ---

    // Getters e Setters para a estrutura final
    public String getExprLang() { return exprLang; }
    public void setExprLang(String exprLang) { this.exprLang = exprLang; }

    public List<InputMappingV2Plus> getInputs() { return inputs; }
    public void setInputs(List<InputMappingV2Plus> inputs) { this.inputs = inputs; }

    public List<OutputMappingV2Plus> getOutputs() { return outputs; }
    public void setOutputs(List<OutputMappingV2Plus> outputs) { this.outputs = outputs; }

    public List<TransformationRuleV2Plus> getTransformations() { return transformations; }
    public void setTransformations(List<TransformationRuleV2Plus> transformations) { this.transformations = transformations; }

    // Getters e Setters para os campos legados
    public List<InputMappingV2Plus> getInputMappings() { return inputMappings; }
    public void setInputMappings(List<InputMappingV2Plus> inputMappings) { this.inputMappings = inputMappings; }

    public List<OutputMappingV2Plus> getOutputMappings() { return outputMappings; }
    public void setOutputMappings(List<OutputMappingV2Plus> outputMappings) { this.outputMappings = outputMappings; }
}