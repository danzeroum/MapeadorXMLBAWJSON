package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

// Mapeia cada tag <object> e seus atributos
@XmlAccessorType(XmlAccessType.FIELD)
public class TwObject {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String type;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}