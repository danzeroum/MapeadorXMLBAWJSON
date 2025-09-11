package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "layoutItem", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
public class LayoutItem {

    @XmlAttribute
    private String version;

    @XmlElement(name = "id", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private String id;

    @XmlElement(name = "layoutItemId", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private String layoutItemId;

    @XmlElement(name = "viewUUID", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private String viewUUID;

    @XmlElement(name = "binding", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private String binding;

    @XmlElement(name = "configData", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private List<ConfigData> configData;

    @XmlElement(name = "contentBoxContrib", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private List<ContentBoxContrib> contentBoxContribs;

    // Getters e Setters
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLayoutItemId() { return layoutItemId; }
    public void setLayoutItemId(String layoutItemId) { this.layoutItemId = layoutItemId; }

    public String getViewUUID() { return viewUUID; }
    public void setViewUUID(String viewUUID) { this.viewUUID = viewUUID; }

    public String getBinding() { return binding; }
    public void setBinding(String binding) { this.binding = binding; }

    public List<ConfigData> getConfigData() { return configData; }
    public void setConfigData(List<ConfigData> configData) { this.configData = configData; }

    public List<ContentBoxContrib> getContentBoxContribs() { return contentBoxContribs; }
    public void setContentBoxContribs(List<ContentBoxContrib> contentBoxContribs) { this.contentBoxContribs = contentBoxContribs; }


}