package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg;

import javax.xml.bind.annotation.*;

/**
 * Representa uma tag <object> na lista de inventário de artefatos.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PackageObject {
    @XmlAttribute private String id;
    @XmlAttribute private String versionId;
    @XmlAttribute private String name;
    @XmlAttribute private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}