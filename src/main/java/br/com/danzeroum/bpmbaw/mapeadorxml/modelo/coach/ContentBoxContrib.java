package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LegacyCoachContentBoxContrib")
public class ContentBoxContrib {

    @XmlElement
    private String contentBoxId;

    // Esta anotação dupla é necessária para lidar com a estrutura aninhada
    // <contentBoxContrib><contributions>...</contributions></contentBoxContrib>
    @XmlElementWrapper(name = "contentBoxContrib")
    @XmlElement(name = "contributions")
    private List<LayoutItem> contributions;

    public String getContentBoxId() {
        return contentBoxId;
    }

    public void setContentBoxId(String contentBoxId) {
        this.contentBoxId = contentBoxId;
    }

    public List<LayoutItem> getContributions() {
        return contributions;
    }

    public void setContributions(List<LayoutItem> contributions) {
        this.contributions = contributions;
    }
}