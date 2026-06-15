package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "layout", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
public class Layout {

    @XmlElement(name = "layoutItem", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private List<LayoutItem> layoutItems;

    public List<LayoutItem> getLayoutItems() {
        return layoutItems;
    }

    public void setLayoutItems(List<LayoutItem> layoutItems) {
        this.layoutItems = layoutItems;
    }
}