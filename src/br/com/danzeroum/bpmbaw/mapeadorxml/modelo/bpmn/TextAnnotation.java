package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Representa uma Anotação de Texto (<textAnnotation>) no diagrama BPMN 2.0,
 * usada para comentários e documentação visual.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TextAnnotation {

    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String textFormat;

    @XmlElement(name = "text", namespace = BPMN_NAMESPACE)
    private String text;

    @XmlElement(name = "extensionElements", namespace = BPMN_NAMESPACE)
    private ExtensionElements extensionElements;

    // --- Getters e Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTextFormat() { return textFormat; }
    public void setTextFormat(String textFormat) { this.textFormat = textFormat; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public ExtensionElements getExtensionElements() { return extensionElements; }
    public void setExtensionElements(ExtensionElements extensionElements) { this.extensionElements = extensionElements; }
}