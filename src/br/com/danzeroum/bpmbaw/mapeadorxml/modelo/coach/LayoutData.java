package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.ErrorLink;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Representa dados de layout para um componente no diagrama do processo.
 * Esta classe foi adaptada para ser flexível e mapear diferentes estruturas
 * da tag <layoutData> encontradas no XML do BAW.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LayoutData {

    // --- Atributos de Coordenadas (comuns a todas as variações) ---
    @XmlAttribute
    private int x;

    @XmlAttribute
    private int y;

    // --- Campo para a estrutura com <errorLink> aninhado (ex: em startPoint) ---
    @XmlElement
    private ErrorLink errorLink;

    // --- Campos para a estrutura sem <errorLink> (ex: em startLink) ---
    @XmlElement
    private ControlPoints controlPoints;

    @XmlElement
    private boolean showEndState;

    @XmlElement
    private boolean showName;


    // --- Getters e Setters ---

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ErrorLink getErrorLink() {
        return errorLink;
    }

    public void setErrorLink(ErrorLink errorLink) {
        this.errorLink = errorLink;
    }

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