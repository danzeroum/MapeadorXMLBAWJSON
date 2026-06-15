package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa a tag vazia <stayOnPageEventDefinition/>, que é uma extensão da IBM
 * para um IntermediateThrowEvent em um Coach Flow.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "stayOnPageEventDefinition", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
public class StayOnPageEventDefinition {
    // Esta classe pode ficar vazia, pois a tag no XML não tem atributos ou filhos.
    // Sua simples presença no objeto pai já é a informação que precisamos.
}