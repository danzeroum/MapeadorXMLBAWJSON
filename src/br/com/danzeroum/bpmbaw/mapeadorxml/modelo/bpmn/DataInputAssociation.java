package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataInputAssociation {
    @XmlElement(name = "targetRef", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private String targetRef;

    @XmlElement(name = "assignment", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private Assignment assignment;

    // Getters e Setters...


    public String getTargetRef() {
        return targetRef;
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }
}