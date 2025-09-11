package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output;

import java.util.List;

/**
 * Process logic
 */
class ProcessLogic {
    private List<LogicScript> scripts;
    private List<ValidationRule> validations;

    // Getters and setters...
    public List<LogicScript> getScripts() {
        return scripts;
    }

    public void setScripts(List<LogicScript> scripts) {
        this.scripts = scripts;
    }

    public List<ValidationRule> getValidations() {
        return validations;
    }

    public void setValidations(List<ValidationRule> validations) {
        this.validations = validations;
    }
}
