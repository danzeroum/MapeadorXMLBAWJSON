package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "amdDependency")
public class AmdDependency {

    @XmlElement
    private String moduleId;

    @XmlElement
    private String functionArgument;

    public String getModuleId() {
        return moduleId;
    }

    public String getFunctionArgument() {
        return functionArgument;
    }
}