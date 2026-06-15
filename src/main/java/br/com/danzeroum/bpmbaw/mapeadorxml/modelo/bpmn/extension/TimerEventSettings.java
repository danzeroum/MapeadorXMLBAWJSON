package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class TimerEventSettings {

    private static final String WLE_NAMESPACE = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/wle";

    @XmlElement(name = "relativeTime", namespace = WLE_NAMESPACE)
    private int relativeTime;

    @XmlElement(name = "relativeTimeResolution", namespace = WLE_NAMESPACE)
    private String relativeTimeResolution;

    // Getters e Setters
    public int getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(int relativeTime) {
        this.relativeTime = relativeTime;
    }

    public String getRelativeTimeResolution() {
        return relativeTimeResolution;
    }

    public void setRelativeTimeResolution(String relativeTimeResolution) {
        this.relativeTimeResolution = relativeTimeResolution;
    }
}