package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension.ActivityPerformer;
import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class CallActivity extends FlowNode {

    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    private static final String IBM_BPM_NAMESPACE = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/wle";

    @XmlAttribute
    private String calledElement;

    @XmlAttribute(name = "default")
    private String defaultFlow;

    @XmlElement(name = "dataInputAssociation", namespace = BPMN_NAMESPACE)
    private List<DataInputAssociation> dataInputAssociations;

    @XmlElement(name = "activityPerformer", namespace = IBM_BPM_NAMESPACE)
    private List<ActivityPerformer> activityPerformers;

    // ADIÇÃO: Mapeamento do elemento <performer>
    @XmlElement(name = "performer", namespace = BPMN_NAMESPACE)
    private Performer performer;

    @XmlElement(name = "dataOutputAssociation", namespace = BPMN_NAMESPACE)
    private List<DataOutputAssociation> dataOutputAssociations; // <-- CAMPO ADICIONADO


    // Getters e Setters
    public String getCalledElement() { return calledElement; }
    public void setCalledElement(String calledElement) { this.calledElement = calledElement; }

    public String getDefaultFlow() { return defaultFlow; }
    public void setDefaultFlow(String defaultFlow) { this.defaultFlow = defaultFlow; }

    public List<DataInputAssociation> getDataInputAssociations() { return dataInputAssociations; }
    public void setDataInputAssociations(List<DataInputAssociation> dataInputAssociations) { this.dataInputAssociations = dataInputAssociations; }

    public List<ActivityPerformer> getActivityPerformers() { return activityPerformers; }
    public void setActivityPerformers(List<ActivityPerformer> activityPerformers) { this.activityPerformers = activityPerformers; }

    public Performer getPerformer() { return performer; }
    public void setPerformer(Performer performer) { this.performer = performer; }

    public List<DataOutputAssociation> getDataOutputAssociations() { // <-- GETTER/SETTER ADICIONADOS
        return dataOutputAssociations;
    }

    public void setDataOutputAssociations(List<DataOutputAssociation> dataOutputAssociations) {
        this.dataOutputAssociations = dataOutputAssociations;
    }
}