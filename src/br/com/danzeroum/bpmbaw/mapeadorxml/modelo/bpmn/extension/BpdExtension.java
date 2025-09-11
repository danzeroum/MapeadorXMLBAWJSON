package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class BpdExtension {

    private static final String WLE_NAMESPACE = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/wle";

    @XmlAttribute
    private String instanceName;

    @XmlAttribute
    private boolean dueDateEnabled;

    @XmlElement(name = "dueDateSettings", namespace = WLE_NAMESPACE)
    private DueDateSettings dueDateSettings;

    @XmlElement(name = "workSchedule", namespace = WLE_NAMESPACE)
    private WorkSchedule workSchedule;

    // Getters e Setters
    public String getInstanceName() { return instanceName; }
    public void setInstanceName(String instanceName) { this.instanceName = instanceName; }
    public boolean isDueDateEnabled() { return dueDateEnabled; }
    public void setDueDateEnabled(boolean dueDateEnabled) { this.dueDateEnabled = dueDateEnabled; }
    public DueDateSettings getDueDateSettings() { return dueDateSettings; }
    public void setDueDateSettings(DueDateSettings dueDateSettings) { this.dueDateSettings = dueDateSettings; }
    public WorkSchedule getWorkSchedule() { return workSchedule; }
    public void setWorkSchedule(WorkSchedule workSchedule) { this.workSchedule = workSchedule; }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DueDateSettings {
        @XmlElement(name = "dueDate", namespace = WLE_NAMESPACE)
        private DueDate dueDate;

        public DueDate getDueDate() { return dueDate; }
        public void setDueDate(DueDate dueDate) { this.dueDate = dueDate; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class WorkSchedule {
        @XmlElement(name = "timeScheduleType", namespace = WLE_NAMESPACE)
        private int timeScheduleType;

        @XmlElement(name = "timezoneType", namespace = WLE_NAMESPACE)
        private int timezoneType;

        @XmlElement(name = "holidayScheduleType", namespace = WLE_NAMESPACE)
        private int holidayScheduleType;

        // Getters e Setters
        public int getTimeScheduleType() { return timeScheduleType; }
        public void setTimeScheduleType(int timeScheduleType) { this.timeScheduleType = timeScheduleType; }
        public int getTimezoneType() { return timezoneType; }
        public void setTimezoneType(int timezoneType) { this.timezoneType = timezoneType; }
        public int getHolidayScheduleType() { return holidayScheduleType; }
        public void setHolidayScheduleType(int holidayScheduleType) { this.holidayScheduleType = holidayScheduleType; }
    }
}