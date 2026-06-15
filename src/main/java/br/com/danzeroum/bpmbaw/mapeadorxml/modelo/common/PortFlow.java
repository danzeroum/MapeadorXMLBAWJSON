package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common;
import jakarta.xml.bind.annotation.*;
@XmlAccessorType(XmlAccessType.FIELD)
public class PortFlow {
    @XmlAttribute(name = "ref") private String ref;
    public String getRef() { return ref; }
}