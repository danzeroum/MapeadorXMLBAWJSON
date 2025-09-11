package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.BpmnObjectId;
/**
 * Representa a referência ao ID do cenário de simulação atual.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "currentSimulationScenarioId")
public class CurrentSimulationScenarioId {

    // 2. Altera o tipo do campo
    @XmlElement(name = "BpmnObjectId")
    private BpmnObjectId bpmnObjectId;

    public BpmnObjectId getBpmnObjectId() {
        return bpmnObjectId;
    }

    public void setBpmnObjectId(BpmnObjectId bpmnObjectId) {
        this.bpmnObjectId = bpmnObjectId;
    }
}