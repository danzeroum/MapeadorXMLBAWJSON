package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ControlPoints;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa um link de erro (Error Boundary Event) conectando
 * uma atividade a um evento de tratamento de erro no IBM BAW.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "errorLink")
public class ErrorLink {

    @XmlElement
    private ControlPoints controlPoints;

    @XmlElement
    private boolean showEndState;

    @XmlElement
    private boolean showName;


    // --- Getters e Setters ---

    public ControlPoints getControlPoints() {
        return controlPoints;
    }

    public void setControlPoints(ControlPoints controlPoints) {
        this.controlPoints = controlPoints;
    }

    public boolean isShowEndState() {
        return showEndState;
    }

    public void setShowEndState(boolean showEndState) {
        this.showEndState = showEndState;
    }

    public boolean isShowName() {
        return showName;
    }

    public void setShowName(boolean showName) {
        this.showName = showName;
    }
}