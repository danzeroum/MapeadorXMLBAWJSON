package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class UcaMessageEventDefinition {

    @XmlAttribute
    private String id;

    @XmlElement(namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private String script;

    @XmlElement(namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/wle")
    private boolean consumeMessage;

    @XmlElement(namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/wle")
    private boolean durableSubscription;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getScript() { return script; }
    public void setScript(String script) { this.script = script; }
    public boolean isConsumeMessage() { return consumeMessage; }
    public void setConsumeMessage(boolean consumeMessage) { this.consumeMessage = consumeMessage; }
    public boolean isDurableSubscription() { return durableSubscription; }
    public void setDurableSubscription(boolean durableSubscription) { this.durableSubscription = durableSubscription; }
}