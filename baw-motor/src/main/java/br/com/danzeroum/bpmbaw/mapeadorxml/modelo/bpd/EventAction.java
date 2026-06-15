package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

// Classe simples apenas para detectar a presença da tag <EventAction>
@XmlAccessorType(XmlAccessType.FIELD)
public class EventAction {
    @XmlAttribute
    private String id;
}