package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

// --- Classe para a tag <defaultInstanceUI> ---
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "defaultInstanceUI")
public class DefaultInstanceUI {
    @XmlAttribute private String id;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}