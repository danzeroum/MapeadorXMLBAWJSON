// Em: br/com/danzeroum/bpmbaw/mapeadorxml/modelo/bpmn/Assignment.java
package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Assignment {

    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    @XmlElement(name = "from", namespace = BPMN_NAMESPACE)
    private From from;

    @XmlElement(name = "to", namespace = BPMN_NAMESPACE)
    private To to; // <-- CAMPO ADICIONADO

    // Getters e Setters
    public From getFrom() {
        return from;
    }

    public void setFrom(From from) {
        this.from = from;
    }

    public To getTo() { // <-- GETTER/SETTER ADICIONADOS
        return to;
    }

    public void setTo(To to) {
        this.to = to;
    }
}