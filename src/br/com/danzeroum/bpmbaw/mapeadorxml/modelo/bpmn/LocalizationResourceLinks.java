package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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