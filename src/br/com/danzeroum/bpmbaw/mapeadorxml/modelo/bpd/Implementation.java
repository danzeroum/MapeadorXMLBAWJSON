package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Representa a implementação detalhada de uma atividade em um processo (BPD),
 * como uma User Task, System Task ou Subprocesso. Contém configurações de
 * roteamento, prazos, prioridade e os mapeamentos de dados de entrada/saída.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "implementation")
public class Implementation {

    // --- Campos de Configuração ---
    @XmlElement private String attachedActivityId;
    @XmlElement private int sendToType;
    @XmlElement private int taskRouting;
    @XmlElement private int dueDateType;
    @XmlElement private int dueDateTime;
    @XmlElement private int dueDateTimeResolution;
    @XmlElement private String dueDateTimeTOD;
    @XmlElement private int priorityType;
    @XmlElement private String priority;
    @XmlElement private String subject;
    @XmlElement private boolean forceSend;
    @XmlElement private String timeSchedule;
    @XmlElement private int timeScheduleType;
    @XmlElement private String timeZone;
    @XmlElement private int timeZoneType;
    @XmlElement private String holidaySchedule;
    @XmlElement private int holidayScheduleType;
    @XmlElement private boolean isDueDateEnabled;
    @XmlElement private String attachedProcessId;
    // --- Listas de Mapeamento de Parâmetros ---
    @XmlElement(name = "inputActivityParameterMapping")
    private List<InputActivityParameterMapping> inputMappings;

    @XmlElement(name = "outputActivityParameterMapping")
    private List<OutputActivityParameterMapping> outputMappings;

    @XmlElement
    private String subProcessId;


    @XmlElement
    private String eventEmbeddedProcessId;

    @XmlElement(name = "laneFilter")
    private LaneFilter laneFilter;

    @XmlElement(name = "teamFilter")
    private TeamFilter teamFilter;

    @XmlElement
    private String embeddedProcessId;
    // --- Getters e Setters ---
    public String getEmbeddedProcessId() { return embeddedProcessId; }
    public void setEmbeddedProcessId(String embeddedProcessId) { this.embeddedProcessId = embeddedProcessId; }

    public String getEventEmbeddedProcessId() { return eventEmbeddedProcessId; }
    public void setEventEmbeddedProcessId(String eventEmbeddedProcessId) { this.eventEmbeddedProcessId = eventEmbeddedProcessId; }

    public LaneFilter getLaneFilter() { return laneFilter; }
    public void setLaneFilter(LaneFilter laneFilter) { this.laneFilter = laneFilter; }

    public TeamFilter getTeamFilter() { return teamFilter; }
    public void setTeamFilter(TeamFilter teamFilter) { this.teamFilter = teamFilter; }

    public String getSubProcessId() {
        return subProcessId;
    }

    public void setSubProcessId(String subProcessId) {
        this.subProcessId = subProcessId;
    }

    public String getAttachedActivityId() { return attachedActivityId; }
    public void setAttachedActivityId(String attachedActivityId) { this.attachedActivityId = attachedActivityId; }

    public int getSendToType() { return sendToType; }
    public void setSendToType(int sendToType) { this.sendToType = sendToType; }

    public int getTaskRouting() { return taskRouting; }
    public void setTaskRouting(int taskRouting) { this.taskRouting = taskRouting; }

    public int getDueDateType() { return dueDateType; }
    public void setDueDateType(int dueDateType) { this.dueDateType = dueDateType; }

    public int getDueDateTime() { return dueDateTime; }
    public void setDueDateTime(int dueDateTime) { this.dueDateTime = dueDateTime; }

    public int getDueDateTimeResolution() { return dueDateTimeResolution; }
    public void setDueDateTimeResolution(int dueDateTimeResolution) { this.dueDateTimeResolution = dueDateTimeResolution; }

    public String getDueDateTimeTOD() { return dueDateTimeTOD; }
    public void setDueDateTimeTOD(String dueDateTimeTOD) { this.dueDateTimeTOD = dueDateTimeTOD; }

    public int getPriorityType() { return priorityType; }
    public void setPriorityType(int priorityType) { this.priorityType = priorityType; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public boolean isForceSend() { return forceSend; }
    public void setForceSend(boolean forceSend) { this.forceSend = forceSend; }

    public String getTimeSchedule() { return timeSchedule; }
    public void setTimeSchedule(String timeSchedule) { this.timeSchedule = timeSchedule; }

    public int getTimeScheduleType() { return timeScheduleType; }
    public void setTimeScheduleType(int timeScheduleType) { this.timeScheduleType = timeScheduleType; }

    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    public int getTimeZoneType() { return timeZoneType; }
    public void setTimeZoneType(int timeZoneType) { this.timeZoneType = timeZoneType; }

    public String getHolidaySchedule() { return holidaySchedule; }
    public void setHolidaySchedule(String holidaySchedule) { this.holidaySchedule = holidaySchedule; }

    public int getHolidayScheduleType() { return holidayScheduleType; }
    public void setHolidayScheduleType(int holidayScheduleType) { this.holidayScheduleType = holidayScheduleType; }

    public boolean isDueDateEnabled() { return isDueDateEnabled; }
    public void setDueDateEnabled(boolean dueDateEnabled) { isDueDateEnabled = dueDateEnabled; }

    public List<InputActivityParameterMapping> getInputMappings() { return inputMappings; }
    public void setInputMappings(List<InputActivityParameterMapping> inputMappings) { this.inputMappings = inputMappings; }

    public List<OutputActivityParameterMapping> getOutputMappings() { return outputMappings; }
    public void setOutputMappings(List<OutputActivityParameterMapping> outputMappings) { this.outputMappings = outputMappings; }

    public String getAttachedProcessId() { return attachedProcessId; }
    public void setAttachedProcessId(String attachedProcessId) { this.attachedProcessId = attachedProcessId; }

}