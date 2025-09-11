package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "definitions")
@XmlAccessorType(XmlAccessType.FIELD)
public class Definitions {
    @XmlAttribute
    private String id;

    @XmlElement(name = "process")
    private Process process;

    @XmlElement(name = "globalUserTask", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private GlobalUserTask globalUserTask;
    // Outros elementos como <collaboration> e <BPMNDiagram> podem ser adicionados aqui

    // Getters
    public String getId() { return id; }
    public Process getProcess() { return process; }
    public GlobalUserTask getGlobalUserTask() {
        return globalUserTask;
    }

    public void setGlobalUserTask(GlobalUserTask globalUserTask) {
        this.globalUserTask = globalUserTask;
    }
}