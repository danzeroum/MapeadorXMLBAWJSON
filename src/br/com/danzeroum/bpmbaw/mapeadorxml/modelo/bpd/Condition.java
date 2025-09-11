package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BpdStandaloneCondition")
public class Condition {

    // Mapeia a tag <expression>
    @XmlElement
    private String expression;

    public String getExpression() {
        return expression;
    }
}