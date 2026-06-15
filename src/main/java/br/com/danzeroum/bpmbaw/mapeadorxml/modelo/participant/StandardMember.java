package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.participant;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * Representa um membro padrão (<standardMember>) de um grupo de participantes.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class StandardMember {

    @XmlElement(name = "type")
    private String type;

    @XmlElement(name = "name")
    private String name;

    // --- Getters e Setters ---

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}