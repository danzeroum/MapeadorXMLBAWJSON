package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

// Mapeia a tag raiz <p:package>
//@XmlRootElement(name = "package", namespace = "http://lombardisoftware.com/schema/teamworks/7.0.0/package.xsd")
//@XmlAccessorType(XmlAccessType.FIELD)
public class Package_delete {

    // Mapeia a tag <objects> que envolve a lista de objetos
    @XmlElementWrapper(name = "objects")
    @XmlElement(name = "object")
    private List<TwObject> objects;

    public List<TwObject> getObjects() {
        return objects;
    }
}