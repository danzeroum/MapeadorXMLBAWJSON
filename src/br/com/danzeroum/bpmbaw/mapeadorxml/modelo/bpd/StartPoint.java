package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutData;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representa o evento de início (Start Event) de um processo ou serviço no IBM BAW.
 * Contém principalmente informações de layout visual.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "startPoint")
public class StartPoint {

    @XmlElement
    private LayoutData layoutData;


    // --- Getters e Setters ---

    public LayoutData getLayoutData() {
        return layoutData;
    }

    public void setLayoutData(LayoutData layoutData) {
        this.layoutData = layoutData;
    }
}