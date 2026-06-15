package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dataOutputAssociation", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class DataOutputAssociation {

    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    @XmlElement(name = "sourceRef", namespace = BPMN_NAMESPACE)
    private String sourceRef;

    @XmlElement(name = "assignment", namespace = BPMN_NAMESPACE)
    private Assignment assignment;

    // Getters e Setters
    public String getSourceRef() {
        return sourceRef;
    }

    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }
}