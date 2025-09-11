package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "documentation", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class Documentation {

    @XmlValue
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}