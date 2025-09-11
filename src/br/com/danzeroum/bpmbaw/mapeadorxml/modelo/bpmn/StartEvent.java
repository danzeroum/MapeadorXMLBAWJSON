package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension.UcaMessageEventDefinition;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class StartEvent extends FlowNode {

    // ADIÇÃO: Atributo isInterrupting
    @XmlAttribute
    private boolean isInterrupting;

    @XmlElement(name = "ucaMessageEventDefinition", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/wle")
    private UcaMessageEventDefinition ucaMessageEventDefinition;

    // Getters e Setters
    public UcaMessageEventDefinition getUcaMessageEventDefinition() {
        return ucaMessageEventDefinition;
    }

    public void setUcaMessageEventDefinition(UcaMessageEventDefinition ucaMessageEventDefinition) {
        this.ucaMessageEventDefinition = ucaMessageEventDefinition;
    }

    public boolean isInterrupting() {
        return isInterrupting;
    }

    public void setInterrupting(boolean interrupting) {
        isInterrupting = interrupting;
    }
}