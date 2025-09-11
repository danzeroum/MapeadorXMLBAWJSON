package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LegacyCoachLayoutItem")
public class LayoutItem {

    private static final String NAMESPACE = "http://www.ibm.com/bpm/CoachDesignerNG";

    @XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
    private String xsiType;

    @XmlAttribute
    private String version;

    @XmlElement(namespace = NAMESPACE)
    private String layoutItemId;

    @XmlElement(namespace = NAMESPACE)
    private String viewUUID;

    @XmlElement(namespace = NAMESPACE)
    private String binding;

    @XmlElement(name = "configData", namespace = NAMESPACE)
    private List<ConfigData> configData;

    @XmlElement(name = "contentBoxContrib", namespace = NAMESPACE)
    private List<ContentBoxContribution> contentBoxContributions;

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ContentBoxContribution {
        @XmlElement(namespace = NAMESPACE)
        private String contentBoxId;

        @XmlElement(name = "contributions", namespace = NAMESPACE)
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

    public String getXsiType() {
        return xsiType;
    }

    public void setXsiType(String xsiType) {
        this.xsiType = xsiType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLayoutItemId() {
        return layoutItemId;
    }

    public void setLayoutItemId(String layoutItemId) {
        this.layoutItemId = layoutItemId;
    }

    public String getViewUUID() {
        return viewUUID;
    }

    public void setViewUUID(String viewUUID) {
        this.viewUUID = viewUUID;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public List<ConfigData> getConfigData() {
        return configData;
    }

    public void setConfigData(List<ConfigData> configData) {
        this.configData = configData;
    }

    public List<ContentBoxContribution> getContentBoxContributions() {
        return contentBoxContributions;
    }

    public void setContentBoxContributions(List<ContentBoxContribution> contentBoxContributions) {
        this.contentBoxContributions = contentBoxContributions;
    }


}