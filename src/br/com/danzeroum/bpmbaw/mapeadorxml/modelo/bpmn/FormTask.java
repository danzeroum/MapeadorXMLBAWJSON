package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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