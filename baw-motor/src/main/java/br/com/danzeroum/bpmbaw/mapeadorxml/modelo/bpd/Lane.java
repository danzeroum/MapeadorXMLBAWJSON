package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Representa uma Lane (raia) em um Pool de um BPD.
 * Uma Lane organiza os objetos de fluxo associados a um participante específico.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "lane")
public class Lane {

    // --- Atributo ---
    @XmlAttribute
    private String id;

    // --- Elementos Simples ---
    @XmlElement
    private String name;

    @XmlElement
    private int height;

    @XmlElement
    private int laneColor;

    @XmlElement
    private boolean systemLane;

    @XmlElement
    private String attachedParticipant;

    // --- Lista de Objetos de Fluxo ---
    @XmlElement(name = "flowObject")
    private List<FlowObject> flowObjects;


    // --- Getters e Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLaneColor() {
        return laneColor;
    }

    public void setLaneColor(int laneColor) {
        this.laneColor = laneColor;
    }

    public boolean isSystemLane() {
        return systemLane;
    }

    public void setSystemLane(boolean systemLane) {
        this.systemLane = systemLane;
    }

    public String getAttachedParticipant() {
        return attachedParticipant;
    }

    public void setAttachedParticipant(String attachedParticipant) {
        this.attachedParticipant = attachedParticipant;
    }

    public List<FlowObject> getFlowObjects() {
        return flowObjects;
    }

    public void setFlowObjects(List<FlowObject> flowObjects) {
        this.flowObjects = flowObjects;
    }
}