package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa um mapeamento de dados (assignment) em um BPD legado.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "assignment")
public class AssignmentBpd {

    @XmlAttribute
    private String id;

    @XmlElement
    private int assignTime;

    @XmlElement
    private String to;

    @XmlElement
    private String from;

    // --- Getters e Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAssignTime() {
        return assignTime;
    }

    public void setAssignTime(int assignTime) {
        this.assignTime = assignTime;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}