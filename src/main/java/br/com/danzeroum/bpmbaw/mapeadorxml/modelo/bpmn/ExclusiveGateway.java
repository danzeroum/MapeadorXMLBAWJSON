package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class ExclusiveGateway extends FlowNode {
    @XmlAttribute(name = "default")
    private String defaultFlow;

    public String getDefaultFlow() { return defaultFlow; }
}