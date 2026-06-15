package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.Expression;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa um campo rastreado (Tracked Field) no IBM BAW,
 * usado para monitorar o valor de variáveis de processo ao longo do tempo.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "trackedField")
public class TrackedField {

    @XmlAttribute
    private String id;

    @XmlElement
    private String name;

    @XmlElement
    private int type;

    @XmlElement(name = "expression")
    private Expression expression;

    // --- Getters e Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public Expression getExpression() { return expression; }
    public void setExpression(Expression expression) { this.expression = expression; }



}