package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.PortFlow;

import javax.xml.bind.annotation.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class Port {
    @XmlElement(name = "flow") private PortFlow flow;
    public PortFlow getFlow() { return flow; }
}