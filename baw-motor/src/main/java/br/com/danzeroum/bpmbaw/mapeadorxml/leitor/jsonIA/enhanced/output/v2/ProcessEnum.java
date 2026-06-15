package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;


import java.util.ArrayList;
import java.util.List;

public class ProcessEnum {
    private String id;
    private String name;
    private List<EnumValue> values;

    public ProcessEnum() {
        this.values = new ArrayList<>();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<EnumValue> getValues() { return values; }
    public void setValues(List<EnumValue> values) { this.values = values; }
}
