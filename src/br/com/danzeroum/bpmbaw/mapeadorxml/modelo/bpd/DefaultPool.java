package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

// Importa a classe BpmnObjectId centralizada

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.BpmnObjectId;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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