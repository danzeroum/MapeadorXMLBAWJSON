package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class Task extends FlowNode {
    // Atributo customizado da IBM para o tipo de tarefa
    @XmlAttribute(name = "taskType", namespace = "http://www.ibm.com/bpm/coachdesignerng")
    private String taskType;

    public String getTaskType() { return taskType; }
}