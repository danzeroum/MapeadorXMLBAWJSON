package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Representa uma Lane (raia) dentro de um LaneSet no diagrama BPMN 2.0.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Lane {

    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    private static final String IBM_BPM_NAMESPACE = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/wle";

    @XmlAttribute
    private String id;
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String partitionElementRef;

    @XmlAttribute(name = "isSystemLane", namespace = IBM_BPM_NAMESPACE)
    private boolean isSystemLane;

    @XmlElement(name = "flowNodeRef", namespace = BPMN_NAMESPACE)
    private List<String> flowNodeRefs;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPartitionElementRef() { return partitionElementRef; }
    public void setPartitionElementRef(String partitionElementRef) { this.partitionElementRef = partitionElementRef; }
    public boolean isSystemLane() { return isSystemLane; }
    public void setSystemLane(boolean systemLane) { this.isSystemLane = systemLane; }
    public List<String> getFlowNodeRefs() { return flowNodeRefs; }
    public void setFlowNodeRefs(List<String> flowNodeRefs) { this.flowNodeRefs = flowNodeRefs; }
}