package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class To {

    @XmlAttribute(namespace = "http://www.w3.org/2001/XMLSchema-instance")
    private String type;

    @XmlAttribute
    private String evaluatesToTypeRef;

    @XmlValue
    private String content;

    // Getters e Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvaluatesToTypeRef() {
        return evaluatesToTypeRef;
    }

    public void setEvaluatesToTypeRef(String evaluatesToTypeRef) {
        this.evaluatesToTypeRef = evaluatesToTypeRef;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}