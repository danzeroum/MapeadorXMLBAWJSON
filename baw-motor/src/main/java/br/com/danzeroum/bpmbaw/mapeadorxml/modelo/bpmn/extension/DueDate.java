package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension;

import jakarta.xml.bind.annotation.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class DueDate {
    @XmlAttribute private String unit;
    @XmlAttribute private String timeOfDay;
    @XmlValue private int value;
    // Getters e Setters

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}