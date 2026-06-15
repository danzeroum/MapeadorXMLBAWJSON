package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

// Importa a classe BpmnObjectId centralizada

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.BpmnObjectId;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa o pool padrão em um BPD (Business Process Definition). (Versão Refatorada)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "defaultPool")
public class DefaultPool {

    @XmlElement(name = "BpmnObjectId")
    private BpmnObjectId bpmnObjectId; // <-- TIPO DO CAMPO ATUALIZADO


    // --- Getters e Setters ---

    public BpmnObjectId getBpmnObjectId() {
        return bpmnObjectId;
    }

    public void setBpmnObjectId(BpmnObjectId bpmnObjectId) {
        this.bpmnObjectId = bpmnObjectId;
    }


}