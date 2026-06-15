package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension.TimerEventSettings;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class TimerEventDefinition {

    @XmlAttribute
    private String id;

    // Mapeia o bloco <extensionElements> dentro do timer
    @XmlElement(name = "extensionElements", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private ExtensionElements extensionElements;

    /**
     * Classe interna para as extensões específicas do Timer.
     */
    // CORREÇÃO: Adicionada anotação @XmlType para evitar conflito de nome
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "tTimerEventExtensionElements")
    public static class ExtensionElements {
        @XmlElement(name = "timerEventSettings", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/wle")
        private TimerEventSettings timerEventSettings;

        public TimerEventSettings getTimerEventSettings() {
            return timerEventSettings;
        }

        public void setTimerEventSettings(TimerEventSettings timerEventSettings) {
            this.timerEventSettings = timerEventSettings;
        }
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ExtensionElements getExtensionElements() {
        return extensionElements;
    }

    public void setExtensionElements(ExtensionElements extensionElements) {
        this.extensionElements = extensionElements;
    }
}