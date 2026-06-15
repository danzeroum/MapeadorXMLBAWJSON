package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * Representa um nó de fluxo genérico (tarefa, evento, gateway).
 * Versão corrigida para mapear os namespaces de incoming/outgoing explicitamente.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class FlowNode {

    // Define a URL do namespace para ser reutilizada
    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    @XmlAttribute
    protected String id;

    @XmlAttribute
    protected String name;

    @XmlElement(name = "incoming", namespace = BPMN_NAMESPACE)
    protected List<String> incoming;

    @XmlElement(name = "outgoing", namespace = BPMN_NAMESPACE)
    protected List<String> outgoing;

    @XmlElement(name = "extensionElements", namespace = BPMN_NAMESPACE)
    private ExtensionElements extensionElements;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getIncoming() { return incoming; }
    public void setIncoming(List<String> incoming) { this.incoming = incoming; }
    public List<String> getOutgoing() { return outgoing; }
    public void setOutgoing(List<String> outgoing) { this.outgoing = outgoing; }
    public ExtensionElements getExtensionElements() { return extensionElements; }
    public void setExtensionElements(ExtensionElements extensionElements) { this.extensionElements = extensionElements; }


}