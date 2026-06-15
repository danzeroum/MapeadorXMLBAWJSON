// Em: br/com/danzeroum/bpmbaw/mapeadorxml/modelo/bpmn/SequenceFlow.java
package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.Expression;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class SequenceFlow {

    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String sourceRef;

    @XmlAttribute
    private String targetRef;

    @XmlElement(name = "conditionExpression", namespace = BPMN_NAMESPACE)
    private Expression conditionExpression;

    @XmlElement(name = "extensionElements", namespace = BPMN_NAMESPACE)
    private ExtensionElements extensionElements; // <- Agora referencia a classe externa correta

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSourceRef() { return sourceRef; }
    public void setSourceRef(String sourceRef) { this.sourceRef = sourceRef; }
    public String getTargetRef() { return targetRef; }
    public void setTargetRef(String targetRef) { this.targetRef = targetRef; }
    public Expression getConditionExpression() { return conditionExpression; }
    public void setConditionExpression(Expression conditionExpression) { this.conditionExpression = conditionExpression; }
    public ExtensionElements getExtensionElements() { return extensionElements; }
    public void setExtensionElements(ExtensionElements extensionElements) { this.extensionElements = extensionElements; }
}