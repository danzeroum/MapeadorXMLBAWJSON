package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Classe raiz para mapear o XML completo dentro da tag <layoutData> de um Coach.
 * (Versão Corrigida e Simplificada)
 */
@XmlRootElement(name = "layout", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
@XmlAccessorType(XmlAccessType.FIELD)
public class CoachLayout {

    @XmlElement(name = "layoutItem", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private List<LayoutItem> items;

    public List<LayoutItem> getItems() {
        return items;
    }

    public void setItems(List<LayoutItem> items) {
        this.items = items;
    }
}