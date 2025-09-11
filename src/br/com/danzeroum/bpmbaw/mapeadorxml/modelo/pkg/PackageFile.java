package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg;

import javax.xml.bind.annotation.*;

/**
 * Representa uma tag <file> na lista de correlação de arquivos.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PackageFile {
    @XmlAttribute private String path;
    @XmlAttribute private String id;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}