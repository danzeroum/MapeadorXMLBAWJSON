package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.PortFlow;

import jakarta.xml.bind.annotation.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class Port {
    @XmlElement(name = "flow") private PortFlow flow;
    public PortFlow getFlow() { return flow; }
}