package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Representa um bloco de metadados, que consiste em uma
 * lista de entradas (entries) de chave-valor.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "metadata")
public class Metadata {

    @XmlElement(name = "entry")
    private List<Entry> entries;


    // --- Getters e Setters ---
    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}