package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "sequenceFlowImplementation", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
public class SequenceFlowImplementation {

    @XmlAttribute
    private String fireValidation;

    @XmlAttribute
    private Boolean sboSyncEnabled;

    // Getters e Setters
    public String getFireValidation() {
        return fireValidation;
    }

    public void setFireValidation(String fireValidation) {
        this.fireValidation = fireValidation;
    }

    public Boolean getSboSyncEnabled() {
        return sboSyncEnabled;
    }

    public void setSboSyncEnabled(Boolean sboSyncEnabled) {
        this.sboSyncEnabled = sboSyncEnabled;
    }
}