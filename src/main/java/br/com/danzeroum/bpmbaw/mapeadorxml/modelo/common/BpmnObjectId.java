package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common; // ou um pacote comum

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class BpmnObjectId {
    @XmlAttribute
    private String id;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}