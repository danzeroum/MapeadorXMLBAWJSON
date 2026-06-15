package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "localizationResourceLinks", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
public class LocalizationResourceLinks {

    @XmlElement(name = "resourceRef", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
    private ResourceRef resourceRef;

    public ResourceRef getResourceRef() {
        return resourceRef;
    }

    public void setResourceRef(ResourceRef resourceRef) {
        this.resourceRef = resourceRef;
    }
}