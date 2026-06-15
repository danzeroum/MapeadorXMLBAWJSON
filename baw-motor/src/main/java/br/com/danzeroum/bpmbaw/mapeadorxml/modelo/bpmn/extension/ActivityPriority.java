package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension;


import jakarta.xml.bind.annotation.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivityPriority {
    @XmlElement(name = "priority", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/wle")
    private String priority;
    // Getters e Setters

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}