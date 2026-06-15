package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output;

import java.util.List;

/**
 * Process enumeration
 */
class ProcessEnum {
    private String id;
    private String name;
    private List<EnumValue> values;

    // Getters and setters...
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EnumValue> getValues() {
        return values;
    }

    public void setValues(List<EnumValue> values) {
        this.values = values;
    }
}
