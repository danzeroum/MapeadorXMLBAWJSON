package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa o mapeamento de um parâmetro de entrada de uma atividade
 * no IBM BAW. Define como o valor de uma variável do processo pai (a "fonte")
 * é mapeado para uma variável da atividade (o "alvo").
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "inputActivityParameterMapping")
public class InputActivityParameterMapping {

    @XmlAttribute
    private String id;

    @XmlElement
    private String name;

    @XmlElement
    private String classId;

    @XmlElement
    private boolean input;

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

    public boolean isInput() {
        return input;
    }

    public void setInput(boolean input) {
        this.input = input;
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