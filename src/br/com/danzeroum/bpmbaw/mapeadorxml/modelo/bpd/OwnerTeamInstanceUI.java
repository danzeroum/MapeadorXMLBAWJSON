package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

// --- Classe para a tag <ownerTeamInstanceUI> ---
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ownerTeamInstanceUI")
public class OwnerTeamInstanceUI {
    @XmlAttribute private String id;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}