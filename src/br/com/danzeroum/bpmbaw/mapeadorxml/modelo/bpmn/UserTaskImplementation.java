package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "userTaskImplementation", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
public class UserTaskImplementation {

    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    @XmlElements({
            @XmlElement(name = "startEvent", namespace = BPMN_NAMESPACE, type = StartEvent.class),
            @XmlElement(name = "endEvent", namespace = BPMN_NAMESPACE, type = EndEvent.class),
            @XmlElement(name = "callActivity", namespace = BPMN_NAMESPACE, type = CallActivity.class),
            @XmlElement(name = "scriptTask", namespace = BPMN_NAMESPACE, type = ScriptTask.class),
            @XmlElement(name = "exclusiveGateway", namespace = BPMN_NAMESPACE, type = ExclusiveGateway.class),
            @XmlElement(name = "subProcess", namespace = BPMN_NAMESPACE, type = SubProcess.class),
            @XmlElement(name = "task", namespace = BPMN_NAMESPACE, type = Task.class),
            @XmlElement(name = "dataObject", namespace = BPMN_NAMESPACE, type = DataObject.class),
            @XmlElement(name = "formTask", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process", type = FormTask.class),
            @XmlElement(name = "intermediateThrowEvent", namespace = BPMN_NAMESPACE, type = IntermediateThrowEvent.class)
    })
    private List<Object> flowElements;

    @XmlElement(name = "sequenceFlow", namespace = BPMN_NAMESPACE)
    private List<SequenceFlow> sequenceFlows;

    @XmlElement(name = "htmlHeaderTag", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
    private List<HtmlHeaderTag> htmlHeaderTags;


    public List<Object> getFlowElements() { return flowElements; }
    public void setFlowElements(List<Object> flowElements) { this.flowElements = flowElements; }
    public List<SequenceFlow> getSequenceFlows() { return sequenceFlows; }
    public void setSequenceFlows(List<SequenceFlow> sequenceFlows) { this.sequenceFlows = sequenceFlows; }

    public List<HtmlHeaderTag> getHtmlHeaderTags() {
        return htmlHeaderTags;
    }

    public void setHtmlHeaderTags(List<HtmlHeaderTag> htmlHeaderTags) {
        this.htmlHeaderTags = htmlHeaderTags;
    }
}