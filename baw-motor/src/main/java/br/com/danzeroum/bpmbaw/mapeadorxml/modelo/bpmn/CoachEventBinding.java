// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/modelo/bpmn/CoachEventBinding.java
package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "coachEventBinding", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
public class CoachEventBinding {

    private static final String PROCESS_NAMESPACE = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process";

    @XmlAttribute
    private String id;

    // --- CAMPOS ADICIONADOS PARA CORREÇÃO ---
    @XmlElement(name = "controlId", namespace = PROCESS_NAMESPACE)
    private String controlId;

    @XmlElement(name = "name", namespace = PROCESS_NAMESPACE)
    private String name;

    @XmlElement(name = "script", namespace = PROCESS_NAMESPACE)
    private String script;
    // --- FIM DOS CAMPOS ADICIONADOS ---

    @XmlElement(name = "coachEventPath", namespace = PROCESS_NAMESPACE)
    private String coachEventPath;

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoachEventPath() {
        return coachEventPath;
    }

    public void setCoachEventPath(String coachEventPath) {
        this.coachEventPath = coachEventPath;
    }

    // --- MÉTODOS ADICIONADOS PARA CORREÇÃO ---
    public String getControlId() {
        return controlId;
    }

    public void setControlId(String controlId) {
        this.controlId = controlId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
    // --- FIM DOS MÉTODOS ADICIONADOS ---
}