package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions; // Importa do pacote bpmn
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "coachflow")
public class CoachFlow {

    @XmlElement(name = "definitions", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private Definitions definitions;

    public Definitions getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Definitions definitions) {
        this.definitions = definitions;
    }
}