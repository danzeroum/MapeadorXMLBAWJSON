package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import java.util.ArrayList;
import java.util.List;

/**
 * ProcessMappingsV2Plus - CORRIGIDO
 */
public class ProcessMappingsV2Plus {

    private List<InputMappingV2Plus> inputMappings;
    private List<OutputMappingV2Plus> outputMappings;
    private String exprLang; // ADICIONADO para método setExprLang

    public ProcessMappingsV2Plus() {
        this.inputMappings = new ArrayList<>();
        this.outputMappings = new ArrayList<>();
        this.exprLang = "cel"; // Default
    }

    // CORRIGIDO: Método setExprLang
    public void setExprLang(String exprLang) {
        this.exprLang = exprLang;
    }

    public String getExprLang() {
        return exprLang;
    }

    // CORRIGIDO: addInputMapping com 3 parâmetros
    public void addInputMapping(String sourceField, String targetField, String description) {
        if (inputMappings == null) {
            inputMappings = new ArrayList<>();
        }
        InputMappingV2Plus mapping = new InputMappingV2Plus(sourceField, targetField, description);
        inputMappings.add(mapping);
    }

    // CORRIGIDO: addOutputMapping com 4 parâmetros
    public void addOutputMapping(String sourceField, String targetField, String alias, String description) {
        if (outputMappings == null) {
            outputMappings = new ArrayList<>();
        }
        OutputMappingV2Plus mapping = new OutputMappingV2Plus(sourceField, targetField, alias, description);
        outputMappings.add(mapping);
    }

    // Sobrecarga para compatibilidade com 2 parâmetros
    public void addInputMapping(String sourceField, String targetField) {
        addInputMapping(sourceField, targetField, "Auto-generated mapping");
    }

    public void addOutputMapping(String sourceField, String targetField) {
        addOutputMapping(sourceField, targetField, sourceField, "Auto-generated mapping");
    }

    // Getters e setters existentes...
    public List<InputMappingV2Plus> getInputMappings() {
        return inputMappings != null ? inputMappings : new ArrayList<>();
    }

    public void setInputMappings(List<InputMappingV2Plus> inputMappings) {
        this.inputMappings = inputMappings != null ? inputMappings : new ArrayList<>();
    }

    public List<OutputMappingV2Plus> getOutputMappings() {
        return outputMappings != null ? outputMappings : new ArrayList<>();
    }

    public void setOutputMappings(List<OutputMappingV2Plus> outputMappings) {
        this.outputMappings = outputMappings != null ? outputMappings : new ArrayList<>();
    }
}