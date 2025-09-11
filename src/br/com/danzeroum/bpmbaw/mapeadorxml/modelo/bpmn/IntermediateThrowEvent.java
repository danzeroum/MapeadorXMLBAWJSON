package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "intermediateThrowEvent", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class IntermediateThrowEvent extends FlowNode {

    @XmlElement(name = "stayOnPageEventDefinition", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
    private StayOnPageEventDefinition stayOnPageEventDefinition;

    public StayOnPageEventDefinition getStayOnPageEventDefinition() {
        return stayOnPageEventDefinition;
    }

    public void setStayOnPageEventDefinition(StayOnPageEventDefinition stayOnPageEventDefinition) {
        this.stayOnPageEventDefinition = stayOnPageEventDefinition;
    }
}