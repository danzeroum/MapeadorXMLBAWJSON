package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "formTask", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
public class FormTask extends FlowNode {

    @XmlElement(name = "formDefinition", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
    private FormDefinition formDefinition;

    public FormDefinition getFormDefinition() {
        return formDefinition;
    }

    public void setFormDefinition(FormDefinition formDefinition) {
        this.formDefinition = formDefinition;
    }


}