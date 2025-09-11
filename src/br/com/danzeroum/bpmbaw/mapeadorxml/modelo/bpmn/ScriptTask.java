package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.*;

/**
 * Representa uma Tarefa de Script no BPMN 2.0.
 * Herda de Task e adiciona o conteúdo do script.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ScriptTask extends Task {

    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    // --- Atributos Específicos ---
    @XmlAttribute
    private String scriptFormat;

    @XmlAttribute
    private String defaultFlow;

    // --- Elemento Principal ---
    @XmlElement(name = "script", namespace = BPMN_NAMESPACE)
    private String script;

    // --- Getters e Setters ---
    public String getScriptFormat() { return scriptFormat; }
    public void setScriptFormat(String scriptFormat) { this.scriptFormat = scriptFormat; }

    public String getDefaultFlow() { return defaultFlow; }
    public void setDefaultFlow(String defaultFlow) { this.defaultFlow = defaultFlow; }

    public String getScript() { return script; }
    public void setScript(String script) { this.script = script; }
}