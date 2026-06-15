package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa um parâmetro de entrada (Input Parameter) em um BPD,
 * que é exposto para receber dados quando o processo é iniciado.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "inputParameter")
public class InputParameter {

    @XmlAttribute
    private String id;

    @XmlElement
    private String bpdParameterId;

    @XmlElement
    private boolean isProcessInstanceCorrelator;

    // --- Getters e Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBpdParameterId() {
        return bpdParameterId;
    }

    public void setBpdParameterId(String bpdParameterId) {
        this.bpdParameterId = bpdParameterId;
    }

    public boolean isProcessInstanceCorrelator() {
        return isProcessInstanceCorrelator;
    }

    public void setProcessInstanceCorrelator(boolean processInstanceCorrelator) {
        isProcessInstanceCorrelator = processInstanceCorrelator;
    }
}