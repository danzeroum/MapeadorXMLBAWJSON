package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common; // pacote comum

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Expression {
    @XmlAttribute
    private String id;

    @XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
    private String type;

    @XmlValue
    private String content;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getExpression() { return content; }
    public void setExpression(String expression) { this.content = expression; }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}