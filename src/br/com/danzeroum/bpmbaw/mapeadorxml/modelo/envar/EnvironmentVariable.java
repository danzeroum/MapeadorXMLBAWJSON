// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/modelo/envar/EnvironmentVariable.java
package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.envar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class EnvironmentVariable {

    @XmlAttribute
    private String name;

    @XmlElement
    private String envVarId;

    @XmlElement
    private String defaultValue;

    @XmlElement
    private String description;

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEnvVarId() { return envVarId; }
    public void setEnvVarId(String envVarId) { this.envVarId = envVarId; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}