// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/jsonIA/enhanced/output/v2plus/ExternalResourceV2Plus.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExternalResourceV2Plus {

    @JsonProperty("key")
    private String key;

    @JsonProperty("value")
    private String value;

    public ExternalResourceV2Plus(String key, String value) {
        this.key = key;
        this.value = value;
    }

    // Getters e Setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}