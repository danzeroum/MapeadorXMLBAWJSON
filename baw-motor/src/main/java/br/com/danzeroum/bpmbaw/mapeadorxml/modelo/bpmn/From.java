package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class From {
    @XmlValue
    private String expression;
    @XmlAttribute
    private String evaluatesToTypeRef;
    // Getters e Setters...

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getEvaluatesToTypeRef() {
        return evaluatesToTypeRef;
    }

    public void setEvaluatesToTypeRef(String evaluatesToTypeRef) {
        this.evaluatesToTypeRef = evaluatesToTypeRef;
    }
}