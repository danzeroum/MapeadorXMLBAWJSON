
package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension;


import jakarta.xml.bind.annotation.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivityDueDate {
    @XmlElement(name = "dueDate", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/wle")
    private DueDate dueDate;
    // Getters e Setters


    public DueDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(DueDate dueDate) {
        this.dueDate = dueDate;
    }
}