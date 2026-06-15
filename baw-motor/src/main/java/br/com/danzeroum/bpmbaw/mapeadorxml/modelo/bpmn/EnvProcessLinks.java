package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "envProcessLinks", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
public class EnvProcessLinks {

    @XmlElement(name = "envProcessLinkRef", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
    private List<EnvProcessLinkRef> envProcessLinkRefs;

    public List<EnvProcessLinkRef> getEnvProcessLinkRefs() {
        return envProcessLinkRefs;
    }

    public void setEnvProcessLinkRefs(List<EnvProcessLinkRef> envProcessLinkRefs) {
        this.envProcessLinkRefs = envProcessLinkRefs;
    }
}