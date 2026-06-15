package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * Representa uma entrada individual de metadados,
 * composta por um par de chave (key) e valor (value).
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Entry {

    @XmlElement
    private String key;

    @XmlElement
    private String value;


    // --- Getters e Setters ---
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