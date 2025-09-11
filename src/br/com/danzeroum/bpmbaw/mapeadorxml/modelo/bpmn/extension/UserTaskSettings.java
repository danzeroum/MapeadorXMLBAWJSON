package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class UserTaskSettings {

    private static final String IBM_BPM_NAMESPACE = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/wle";

    @XmlElement(name = "subject", namespace = IBM_BPM_NAMESPACE)
    private String subject;

    @XmlElement(name = "activityPriority", namespace = IBM_BPM_NAMESPACE)
    private ActivityPriority activityPriority;

    @XmlElement(name = "activityDueDate", namespace = IBM_BPM_NAMESPACE)
    private ActivityDueDate activityDueDate;

    @XmlElement(name = "activityAssignmentType", namespace = IBM_BPM_NAMESPACE)
    private String activityAssignmentType;

    @XmlElement(name = "activityWorkSchedule", namespace = IBM_BPM_NAMESPACE)
    private ActivityWorkSchedule activityWorkSchedule;

    // Getters e Setters...

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ActivityWorkSchedule {
        @XmlElement(name = "timeScheduleType", namespace = IBM_BPM_NAMESPACE)
        private int timeScheduleType;

        @XmlElement(name = "timezoneType", namespace = IBM_BPM_NAMESPACE)
        private int timezoneType;

        @XmlElement(name = "holidayScheduleType", namespace = IBM_BPM_NAMESPACE)
        private int holidayScheduleType;

        public int getTimeScheduleType() {
            return timeScheduleType;
        }

        public void setTimeScheduleType(int timeScheduleType) {
            this.timeScheduleType = timeScheduleType;
        }

        public int getTimezoneType() {
            return timezoneType;
        }

        public void setTimezoneType(int timezoneType) {
            this.timezoneType = timezoneType;
        }

        public int getHolidayScheduleType() {
            return holidayScheduleType;
        }

        public void setHolidayScheduleType(int holidayScheduleType) {
            this.holidayScheduleType = holidayScheduleType;
        }
    }
}