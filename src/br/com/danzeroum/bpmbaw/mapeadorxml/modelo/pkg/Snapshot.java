package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg;

import javax.xml.bind.annotation.*;

/**
 * Representa a tag <snapshot> dentro de <target> ou <dependency>.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Snapshot {
    @XmlAttribute private String id;
    @XmlAttribute private String name;
    @XmlAttribute private String acronym;
    @XmlAttribute private String originalCreationDate;
    @XmlAttribute private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getOriginalCreationDate() {
        return originalCreationDate;
    }

    public void setOriginalCreationDate(String originalCreationDate) {
        this.originalCreationDate = originalCreationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}