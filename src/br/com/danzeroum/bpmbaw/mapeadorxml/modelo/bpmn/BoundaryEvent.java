package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class BoundaryEvent extends FlowNode {

    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    @XmlAttribute
    private boolean cancelActivity;

    @XmlAttribute
    private String attachedToRef;

    @XmlElement(name = "timerEventDefinition", namespace = BPMN_NAMESPACE)
    private TimerEventDefinition timerEventDefinition;

    // Getters e Setters
    public boolean isCancelActivity() {
        return cancelActivity;
    }

    public void setCancelActivity(boolean cancelActivity) {
        this.cancelActivity = cancelActivity;
    }

    public String getAttachedToRef() {
        return attachedToRef;
    }

    public void setAttachedToRef(String attachedToRef) {
        this.attachedToRef = attachedToRef;
    }

    public TimerEventDefinition getTimerEventDefinition() {
        return timerEventDefinition;
    }

    public void setTimerEventDefinition(TimerEventDefinition timerEventDefinition) {
        this.timerEventDefinition = timerEventDefinition;
    }
}