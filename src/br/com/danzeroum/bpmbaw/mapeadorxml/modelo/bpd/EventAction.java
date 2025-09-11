package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

// Classe simples apenas para detectar a presença da tag <EventAction>
@XmlAccessorType(XmlAccessType.FIELD)
public class EventAction {
    @XmlAttribute
    private String id;
}