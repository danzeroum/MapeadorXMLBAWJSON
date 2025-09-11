// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/modelo/envar/EnvironmentVariableSet.java
package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.envar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "environmentVariableSet")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnvironmentVariableSet {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlElement(name = "envVar")
    private List<EnvironmentVariable> envVars;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<EnvironmentVariable> getEnvVars() { return envVars; }
    public void setEnvVars(List<EnvironmentVariable> envVars) { this.envVars = envVars; }
}