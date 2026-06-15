package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa um filtro de equipe baseado na Lane (raia) para atribuição de tarefas.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "laneFilter")
public class LaneFilter {

    @XmlAttribute
    private String id;

    @XmlElement
    private int serviceType;

    @XmlElement
    private String teamRef;

    // --- Getters e Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public String getTeamRef() {
        return teamRef;
    }

    public void setTeamRef(String teamRef) {
        this.teamRef = teamRef;
    }
}