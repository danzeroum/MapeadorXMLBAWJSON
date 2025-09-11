package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass;

import javax.xml.bind.annotation.*;

/**
 * Representa o validador associado a um Objeto de Negócio (twClass),
 * que pode incluir uma definição de schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "validator")
public class Validator {

    @XmlElement private String className;
    @XmlElement private String errorMessage;
    @XmlElement private String webWidgetJavaClass;
    @XmlElement private String externalType;

    @XmlElement
    private ConfigData configData;

    // --- Getters e Setters ---

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getWebWidgetJavaClass() { return webWidgetJavaClass; }
    public void setWebWidgetJavaClass(String webWidgetJavaClass) { this.webWidgetJavaClass = webWidgetJavaClass; }

    public String getExternalType() { return externalType; }
    public void setExternalType(String externalType) { this.externalType = externalType; }

    public ConfigData getConfigData() { return configData; }
    public void setConfigData(ConfigData configData) { this.configData = configData; }


    // --- Classes Internas para a estrutura aninhada ---

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ValidatorConfigData")
    public static class ConfigData {
        @XmlElement private Schema schema;
        public Schema getSchema() { return schema; }
        public void setSchema(Schema schema) { this.schema = schema; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Schema {
        @XmlElement private SimpleType simpleType;
        public SimpleType getSimpleType() { return simpleType; }
        public void setSimpleType(SimpleType simpleType) { this.simpleType = simpleType; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SimpleType {
        @XmlAttribute private String name;
        @XmlElement private Restriction restriction;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Restriction getRestriction() { return restriction; }
        public void setRestriction(Restriction restriction) { this.restriction = restriction; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Restriction {
        @XmlAttribute private String base;
        public String getBase() { return base; }
        public void setBase(String base) { this.base = base; }
    }
}