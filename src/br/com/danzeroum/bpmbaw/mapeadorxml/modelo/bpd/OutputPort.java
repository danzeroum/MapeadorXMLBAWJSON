package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.PortFlow; // Importa a classe certa
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "outputPort")
public class OutputPort {
    @XmlAttribute private String id;
    @XmlElement private String positionId;
    @XmlElement(name = "flow")
    private PortFlow flow; // <<< Deve ser do tipo PortFlow

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPositionId() { return positionId; }
    public void setPositionId(String positionId) { this.positionId = positionId; }
    public PortFlow getFlow() { return flow; }
    public void setFlow(PortFlow flow) { this.flow = flow; }
}