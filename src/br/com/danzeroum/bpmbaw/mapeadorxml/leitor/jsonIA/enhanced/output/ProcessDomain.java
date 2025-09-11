package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output;

import java.util.List;
import java.util.Map;

/**
 * Process domain information
 */
class ProcessDomain {
    private String name;
    private String description;
    private Map<String, String> glossary;
    private List<StructuredBusinessRule> businessRules;
    private List<ProcessEnum> enums;

    // Getters and setters...
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getGlossary() {
        return glossary;
    }

    public void setGlossary(Map<String, String> glossary) {
        this.glossary = glossary;
    }

    public List<StructuredBusinessRule> getBusinessRules() {
        return businessRules;
    }

    public void setBusinessRules(List<StructuredBusinessRule> businessRules) {
        this.businessRules = businessRules;
    }

    public List<ProcessEnum> getEnums() {
        return enums;
    }

    public void setEnums(List<ProcessEnum> enums) {
        this.enums = enums;
    }
}
