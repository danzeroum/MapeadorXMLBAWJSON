package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.BpmnObjectId;
/**
 * Representa configurações de cenário de simulação para um item de processo.
 * Esta versão completa inclui a estrutura aninhada de IDs e percentual.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "simulationScenarioConfig")
public class SimulationScenarioConfig {

    @XmlAttribute
    private String id;

    @XmlElement
    private OwningSimulationScenarioId owningSimulationScenarioId;

    @XmlElement
    private int percentage;

    // --- Getters e Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OwningSimulationScenarioId getOwningSimulationScenarioId() {
        return owningSimulationScenarioId;
    }

    public void setOwningSimulationScenarioId(OwningSimulationScenarioId owningSimulationScenarioId) {
        this.owningSimulationScenarioId = owningSimulationScenarioId;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    @XmlElement(name = "BpmnObjectId")
    private BpmnObjectId bpmnObjectId;

    public BpmnObjectId getBpmnObjectId() {
        return bpmnObjectId;
    }

    public void setBpmnObjectId(BpmnObjectId bpmnObjectId) {
        this.bpmnObjectId = bpmnObjectId;
    }

// --- Classe Interna para a tag <owningSimulationScenarioId> ---

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class OwningSimulationScenarioId {

        @XmlElement(name = "BpmnObjectId")
        private BpmnObjectId bpmnObjectId;

        public BpmnObjectId getBpmnObjectId() {
            return bpmnObjectId;
        }

        public void setBpmnObjectId(BpmnObjectId bpmnObjectId) {
            this.bpmnObjectId = bpmnObjectId;
        }
    }



}