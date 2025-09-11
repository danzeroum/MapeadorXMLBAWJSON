package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

// Importa a classe centralizada que criamos

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.PortFlow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representa uma porta de entrada (input port) de uma atividade no diagrama,
 * indicando onde um fluxo de sequência termina. (Versão Refatorada)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "inputPort")
public class InputPort {

    @XmlAttribute
    private String id;

    @XmlElement
    private String positionId;

    @XmlElement
    private boolean input;

    // --- CAMPO ATUALIZADO ---
    // Agora utiliza a classe PortFlow reutilizável
    @XmlElement(name = "flow")
    private PortFlow flow;

    // --- Getters e Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public boolean isInput() {
        return input;
    }

    public void setInput(boolean input) {
        this.input = input;
    }

    // --- GETTER/SETTER ATUALIZADO ---
    public PortFlow getFlow() {
        return flow;
    }

    public void setFlow(PortFlow flow) {
        this.flow = flow;
    }

    // --- CLASSE INTERNA REMOVIDA ---
    // A 'public static class Flow' que existia aqui foi removida.
}