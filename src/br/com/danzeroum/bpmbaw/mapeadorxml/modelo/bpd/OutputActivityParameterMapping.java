package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representa o mapeamento de um parâmetro de saída de uma atividade
 * no IBM BAW. Define como o valor de uma variável da atividade (a "fonte")
 * é mapeado de volta para uma variável do processo pai (o "alvo").
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "outputActivityParameterMapping")
public class OutputActivityParameterMapping {

    @XmlAttribute
    private String id;

    @XmlElement
    private String name;

    @XmlElement
    private String classId;

    @XmlElement
    private String value;

    @XmlElement
    private String parameterId;


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

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getParameterId() {
        return parameterId;
    }

    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
    }
}