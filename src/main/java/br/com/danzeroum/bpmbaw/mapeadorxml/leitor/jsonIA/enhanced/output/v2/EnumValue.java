package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.HashMap;
import java.util.Map;

public class EnumValue {
    private String key;
    private Map<String, String> display;

    public EnumValue() {
        this.display = new HashMap<>();
    }

    // Getters and setters
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public Map<String, String> getDisplay() { return display; }
    public void setDisplay(Map<String, String> display) { this.display = display; }
}