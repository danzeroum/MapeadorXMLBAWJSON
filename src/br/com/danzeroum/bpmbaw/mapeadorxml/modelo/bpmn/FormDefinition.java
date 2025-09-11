package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.CoachDefinition; // Importa a classe do coachng
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "formDefinition", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
public class FormDefinition {

    @XmlElement(name = "coachDefinition", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private CoachDefinition coachDefinition;

    public CoachDefinition getCoachDefinition() {
        return coachDefinition;
    }

    public void setCoachDefinition(CoachDefinition coachDefinition) {
        this.coachDefinition = coachDefinition;
    }
}