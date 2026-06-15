package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.CoachDefinition;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "formDefinition", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
public class FormDefinition {

    @XmlElement(name = "coachDefinition", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private CoachDefinition coachDefinition;

    // --- CAMPOS ADICIONADOS PARA CORREÇÃO ---
    @XmlAttribute(name = "serviceRef")
    private String serviceRef;

    @XmlAttribute(name = "serviceName")
    private String serviceName;


    public CoachDefinition getCoachDefinition() {
        return coachDefinition;
    }

    public void setCoachDefinition(CoachDefinition coachDefinition) {
        this.coachDefinition = coachDefinition;
    }

    // --- MÉTODOS ADICIONADOS PARA CORREÇÃO ---
    public String getServiceRef() {
        return serviceRef;
    }

    public void setServiceRef(String serviceRef) {
        this.serviceRef = serviceRef;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}