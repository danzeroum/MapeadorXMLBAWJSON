package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Representa uma condição dentro de um componente Switch de um serviço legado.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SwitchCondition {

    @XmlElement
    private String endStateId;

    @XmlElement
    private String condition;

    // --- Getters e Setters ---

    public String getEndStateId() {
        return endStateId;
    }

    public void setEndStateId(String endStateId) {
        this.endStateId = endStateId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}