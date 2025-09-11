package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Process {

    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlElement(name = "extensionElements", namespace = BPMN_NAMESPACE)
    private ExtensionElements extensionElements;

    @XmlElement(name = "ioSpecification", namespace = BPMN_NAMESPACE)
    private IoSpecification ioSpecification;

    // CORREÇÃO: A lista é de 'Object' para acomodar todos os tipos de elementos, incluindo TextAnnotation.
    @XmlElements({
            @XmlElement(name = "startEvent", namespace = BPMN_NAMESPACE, type = StartEvent.class),
            @XmlElement(name = "task", namespace = BPMN_NAMESPACE, type = Task.class),
            @XmlElement(name = "userTask", namespace = BPMN_NAMESPACE, type = Task.class),
            @XmlElement(name = "serviceTask", namespace = BPMN_NAMESPACE, type = Task.class),
            @XmlElement(name = "scriptTask", namespace = BPMN_NAMESPACE, type = ScriptTask.class),
            @XmlElement(name = "exclusiveGateway", namespace = BPMN_NAMESPACE, type = ExclusiveGateway.class),
            @XmlElement(name = "endEvent", namespace = BPMN_NAMESPACE, type = EndEvent.class),
            @XmlElement(name = "subProcess", namespace = BPMN_NAMESPACE, type = SubProcess.class),
            @XmlElement(name = "callActivity", namespace = BPMN_NAMESPACE, type = CallActivity.class),
            @XmlElement(name = "textAnnotation", namespace = BPMN_NAMESPACE, type = TextAnnotation.class), // Não é um FlowNode
            @XmlElement(name = "boundaryEvent", namespace = BPMN_NAMESPACE, type = BoundaryEvent.class)
    })
    private List<Object> flowElements; // Nome alterado para clareza

    @XmlElement(name = "sequenceFlow", namespace = BPMN_NAMESPACE)
    private List<SequenceFlow> sequenceFlows;

    @XmlElement(name = "laneSet", namespace = BPMN_NAMESPACE)
    private LaneSet laneSet;

    @XmlElement(name = "dataObject", namespace = BPMN_NAMESPACE)
    private List<DataObject> dataObjects;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public IoSpecification getIoSpecification() { return ioSpecification; }
    public void setIoSpecification(IoSpecification ioSpecification) { this.ioSpecification = ioSpecification; }

    // CORREÇÃO: Getter e Setter atualizados
    public List<Object> getFlowElements() { return flowElements; }
    public void setFlowElements(List<Object> flowElements) { this.flowElements = flowElements; }

    public List<SequenceFlow> getSequenceFlows() { return sequenceFlows; }
    public void setSequenceFlows(List<SequenceFlow> sequenceFlows) { this.sequenceFlows = sequenceFlows; }
    public LaneSet getLaneSet() { return laneSet; }
    public void setLaneSet(LaneSet laneSet) { this.laneSet = laneSet; }
    public ExtensionElements getExtensionElements() { return extensionElements; }
    public void setExtensionElements(ExtensionElements extensionElements) { this.extensionElements = extensionElements; }
    public List<DataObject> getDataObjects() { return dataObjects; }
    public void setDataObjects(List<DataObject> dataObjects) { this.dataObjects = dataObjects; }
}