package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "resourceRef", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
public class ResourceRef {

    private static final String PROCESS_NAMESPACE = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process";

    @XmlElement(name = "resourceBundleGroupID", namespace = PROCESS_NAMESPACE)
    private String resourceBundleGroupID;

    @XmlElement(name = "id", namespace = PROCESS_NAMESPACE)
    private String id;

    // Getters e Setters
    public String getResourceBundleGroupID() {
        return resourceBundleGroupID;
    }

    public void setResourceBundleGroupID(String resourceBundleGroupID) {
        this.resourceBundleGroupID = resourceBundleGroupID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}