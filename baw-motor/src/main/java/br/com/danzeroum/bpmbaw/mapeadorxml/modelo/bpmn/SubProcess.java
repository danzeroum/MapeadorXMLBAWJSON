package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class SubProcess extends FlowNode {

    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    @XmlAttribute
    private boolean triggeredByEvent;

    // CORREÇÃO: A lista é de 'Object' para acomodar todos os tipos de elementos.
    @XmlElements({
            @XmlElement(name = "startEvent", namespace = BPMN_NAMESPACE, type = StartEvent.class),
            @XmlElement(name = "task", namespace = BPMN_NAMESPACE, type = Task.class),
            @XmlElement(name = "userTask", namespace = BPMN_NAMESPACE, type = Task.class),
            @XmlElement(name = "serviceTask", namespace = BPMN_NAMESPACE, type = Task.class),
            @XmlElement(name = "scriptTask", namespace = BPMN_NAMESPACE, type = ScriptTask.class),
            @XmlElement(name = "exclusiveGateway", namespace = BPMN_NAMESPACE, type = ExclusiveGateway.class),
            @XmlElement(name = "endEvent", namespace = BPMN_NAMESPACE, type = EndEvent.class),
            @XmlElement(name = "subProcess", namespace = BPMN_NAMESPACE, type = SubProcess.class),
            @XmlElement(name = "textAnnotation", namespace = BPMN_NAMESPACE, type = TextAnnotation.class),
            @XmlElement(name = "boundaryEvent", namespace = BPMN_NAMESPACE, type = BoundaryEvent.class),
            @XmlElement(name = "callActivity", namespace = BPMN_NAMESPACE, type = CallActivity.class)
    })
    private List<Object> flowElements; // Nome alterado

    @XmlElement(name = "sequenceFlow", namespace = BPMN_NAMESPACE)
    private List<SequenceFlow> sequenceFlows;

    @XmlAttribute
    private String processRef;

    @XmlElement(name = "laneSet", namespace = BPMN_NAMESPACE)
    private LaneSet laneSet;

    // Getters e Setters
    public LaneSet getLaneSet() { return laneSet; }
    public void setLaneSet(LaneSet laneSet) { this.laneSet = laneSet; }

    // CORREÇÃO: Getter e Setter atualizados
    public List<Object> getFlowElements() { return flowElements; }
    public void setFlowElements(List<Object> flowElements) { this.flowElements = flowElements; }

    public List<SequenceFlow> getSequenceFlows() { return sequenceFlows; }
    public void setSequenceFlows(List<SequenceFlow> sequenceFlows) { this.sequenceFlows = sequenceFlows; }
    public String getProcessRef() { return processRef; }
    public boolean isTriggeredByEvent() { return triggeredByEvent; }
    public void setTriggeredByEvent(boolean triggeredByEvent) { this.triggeredByEvent = triggeredByEvent; }
}