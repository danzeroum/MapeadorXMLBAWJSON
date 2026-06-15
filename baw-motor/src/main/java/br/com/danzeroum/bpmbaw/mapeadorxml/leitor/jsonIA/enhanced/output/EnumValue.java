package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output;

import java.util.Map;

/**
 * Enum value with i18n
 */
class EnumValue {
    private String key;
    private Map<String, String> display;

    // Getters and setters...
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getDisplay() {
        return display;
    }

    public void setDisplay(Map<String, String> display) {
        this.display = display;
    }
}
