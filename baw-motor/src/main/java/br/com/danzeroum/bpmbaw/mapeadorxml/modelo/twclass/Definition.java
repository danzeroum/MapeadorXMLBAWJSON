package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Representa a tag <definition> de um Objeto de Negócio (twClass),
 * contendo a lista de propriedades e o validador.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Definition {

    @XmlElement(name = "property")
    private List<Property> properties;

    @XmlElement
    private Validator validator;

    @XmlElement(name = "annotation")
    private DefinitionAnnotation annotation;

    // --- Getters e Setters ---

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public DefinitionAnnotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(DefinitionAnnotation annotation) {
        this.annotation = annotation;
    }
}