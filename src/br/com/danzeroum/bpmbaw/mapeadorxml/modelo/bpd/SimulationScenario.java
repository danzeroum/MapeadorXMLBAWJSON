package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.BpmnObjectId;
/**
 * Representa um cenário de simulação completo no IBM BAW,
 * contendo parâmetros de execução, duração e eventos de início.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "simulationScenario")
public class SimulationScenario {

    @XmlAttribute
    private String id;

    @XmlElement
    private String name;

    @XmlElement
    private int simNumInstances;

    @XmlElement
    private int simMinutesBetween;

    @XmlElement
    private int maxInstances;

    @XmlElement
    private boolean useMaxInstances;

    @XmlElement
    private boolean continueFromReal;

    @XmlElement
    private boolean useParticipantCalendars;

    @XmlElement
    private boolean useDuration;

    @XmlElement
    private int duration;

    @XmlElement
    private long startTime;

    @XmlElement
    private StartEventId startEventId;


    // --- Getters e Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSimNumInstances() { return simNumInstances; }
    public void setSimNumInstances(int simNumInstances) { this.simNumInstances = simNumInstances; }

    public int getSimMinutesBetween() { return simMinutesBetween; }
    public void setSimMinutesBetween(int simMinutesBetween) { this.simMinutesBetween = simMinutesBetween; }

    public int getMaxInstances() { return maxInstances; }
    public void setMaxInstances(int maxInstances) { this.maxInstances = maxInstances; }

    public boolean isUseMaxInstances() { return useMaxInstances; }
    public void setUseMaxInstances(boolean useMaxInstances) { this.useMaxInstances = useMaxInstances; }

    public boolean isContinueFromReal() { return continueFromReal; }
    public void setContinueFromReal(boolean continueFromReal) { this.continueFromReal = continueFromReal; }

    public boolean isUseParticipantCalendars() { return useParticipantCalendars; }
    public void setUseParticipantCalendars(boolean useParticipantCalendars) { this.useParticipantCalendars = useParticipantCalendars; }

    public boolean isUseDuration() { return useDuration; }
    public void setUseDuration(boolean useDuration) { this.useDuration = useDuration; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public StartEventId getStartEventId() { return startEventId; }
    public void setStartEventId(StartEventId startEventId) { this.startEventId = startEventId; }

    @XmlElement(name = "BpmnObjectId")
    private BpmnObjectId bpmnObjectId;

    public BpmnObjectId getBpmnObjectId() {
        return bpmnObjectId;
    }

    public void setBpmnObjectId(BpmnObjectId bpmnObjectId) {
        this.bpmnObjectId = bpmnObjectId;
    }
    // --- Classe Interna para a tag <startEventId> ---

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class StartEventId {

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