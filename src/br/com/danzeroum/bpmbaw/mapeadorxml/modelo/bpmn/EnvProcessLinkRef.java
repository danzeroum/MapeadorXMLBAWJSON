package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

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