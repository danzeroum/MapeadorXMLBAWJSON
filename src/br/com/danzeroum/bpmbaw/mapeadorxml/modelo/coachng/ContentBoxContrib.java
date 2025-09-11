package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "contentBoxContrib", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
public class ContentBoxContrib {

    @XmlElement(name = "id", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private String id;

    @XmlElement(name = "contentBoxId", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private String contentBoxId;

    // O JAXB irá mapear a tag <contributions> para esta lista de LayoutItems
    @XmlElement(name = "contributions", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private List<LayoutItem> contributions;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContentBoxId() { return contentBoxId; }
    public void setContentBoxId(String contentBoxId) { this.contentBoxId = contentBoxId; }

    public List<LayoutItem> getContributions() { return contributions; }
    public void setContributions(List<LayoutItem> contributions) { this.contributions = contributions; }
}