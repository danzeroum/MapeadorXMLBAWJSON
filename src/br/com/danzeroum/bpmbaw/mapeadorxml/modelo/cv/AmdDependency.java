package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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