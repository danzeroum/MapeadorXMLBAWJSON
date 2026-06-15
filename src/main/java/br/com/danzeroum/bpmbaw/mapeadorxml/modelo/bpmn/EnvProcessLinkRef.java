package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "envProcessLinkRef", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
public class EnvProcessLinkRef {

    @XmlAttribute
    private String envId;

    @XmlAttribute
    private String envProcessLinkId;

    // Getters e Setters
    public String getEnvId() {
        return envId;
    }

    public void setEnvId(String envId) {
        this.envId = envId;
    }

    public String getEnvProcessLinkId() {
        return envProcessLinkId;
    }

    public void setEnvProcessLinkId(String envProcessLinkId) {
        this.envProcessLinkId = envProcessLinkId;
    }
}