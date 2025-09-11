package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

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