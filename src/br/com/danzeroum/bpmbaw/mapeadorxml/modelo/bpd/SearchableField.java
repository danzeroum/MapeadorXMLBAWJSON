package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.Expression;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representa um campo de negócio pesquisável (Searchable Field) no IBM BAW.
 * Permite que variáveis do processo sejam expostas para busca e relatórios.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "searchableField")
public class SearchableField {

    @XmlAttribute
    private String id;

    @XmlElement
    private String name;

    @XmlElement
    private int type;

    @XmlElement(name = "expression")
    private Expression expression;

    // --- Getters e Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }


}