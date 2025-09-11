package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "coachEventBinding", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
public class CoachEventBinding {

    @XmlAttribute
    private String id;

    @XmlElement(name = "coachEventPath", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
    private String coachEventPath;

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoachEventPath() {
        return coachEventPath;
    }

    public void setCoachEventPath(String coachEventPath) {
        this.coachEventPath = coachEventPath;
    }
}