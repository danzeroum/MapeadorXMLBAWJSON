package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "linkVisualInfo", namespace = "http://www.ibm.com/xmlns/prod/bpm/graph")
public class LinkVisualInfo {

    private static final String GRAPH_NAMESPACE = "http://www.ibm.com/xmlns/prod/bpm/graph";

    @XmlElement(name = "sourcePortLocation", namespace = GRAPH_NAMESPACE)
    private String sourcePortLocation;

    @XmlElement(name = "targetPortLocation", namespace = GRAPH_NAMESPACE)
    private String targetPortLocation;

    @XmlElement(name = "showLabel", namespace = GRAPH_NAMESPACE)
    private Boolean showLabel;

    @XmlElement(name = "showCoachControlLabel", namespace = GRAPH_NAMESPACE)
    private Boolean showCoachControlLabel;

    @XmlElement(name = "labelPosition", namespace = GRAPH_NAMESPACE)
    private Double labelPosition;

    @XmlElement(name = "saveExecutionContext", namespace = GRAPH_NAMESPACE)
    private Boolean saveExecutionContext;

    // Getters e Setters
    public String getSourcePortLocation() {
        return sourcePortLocation;
    }

    public void setSourcePortLocation(String sourcePortLocation) {
        this.sourcePortLocation = sourcePortLocation;
    }

    public String getTargetPortLocation() {
        return targetPortLocation;
    }

    public void setTargetPortLocation(String targetPortLocation) {
        this.targetPortLocation = targetPortLocation;
    }

    public Boolean getShowLabel() {
        return showLabel;
    }

    public void setShowLabel(Boolean showLabel) {
        this.showLabel = showLabel;
    }

    public Boolean getShowCoachControlLabel() {
        return showCoachControlLabel;
    }

    public void setShowCoachControlLabel(Boolean showCoachControlLabel) {
        this.showCoachControlLabel = showCoachControlLabel;
    }

    public Double getLabelPosition() {
        return labelPosition;
    }

    public void setLabelPosition(Double labelPosition) {
        this.labelPosition = labelPosition;
    }

    public Boolean getSaveExecutionContext() {
        return saveExecutionContext;
    }

    public void setSaveExecutionContext(Boolean saveExecutionContext) {
        this.saveExecutionContext = saveExecutionContext;
    }
}