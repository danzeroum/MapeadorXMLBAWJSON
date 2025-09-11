package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "htmlHeaderTag", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
public class HtmlHeaderTag {

    private static final String PROCESS_NAMESPACE = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process";

    @XmlAttribute
    private String id;

    @XmlElement(name = "tagName", namespace = PROCESS_NAMESPACE)
    private String tagName;

    @XmlElement(name = "content", namespace = PROCESS_NAMESPACE)
    private String content;

    @XmlElement(name = "enabled", namespace = PROCESS_NAMESPACE)
    private Boolean enabled;

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}