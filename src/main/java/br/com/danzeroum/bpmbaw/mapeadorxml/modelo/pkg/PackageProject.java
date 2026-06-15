package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg;

import jakarta.xml.bind.annotation.*;

/**
 * Representa a tag <project> dentro de <target> ou <dependency>.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PackageProject {
    @XmlAttribute private String id;
    @XmlAttribute private String name;
    @XmlAttribute private String description;
    @XmlAttribute private String shortName;
    @XmlAttribute private boolean isToolkit;
    @XmlAttribute private boolean isHidden;
    @XmlAttribute private boolean isSystem;
    @XmlAttribute private String solutionID;
    @XmlAttribute private String solutionServerName;
    @XmlAttribute private String solutionPrefix;
    @XmlAttribute private String type;
    @XmlAttribute private boolean isTemplate;
    @XmlAttribute private boolean isIconSet;
    @XmlAttribute private String caseDisplayName;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean isToolkit() {
        return isToolkit;
    }

    public void setToolkit(boolean toolkit) {
        isToolkit = toolkit;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public String getSolutionID() {
        return solutionID;
    }

    public void setSolutionID(String solutionID) {
        this.solutionID = solutionID;
    }

    public String getSolutionServerName() {
        return solutionServerName;
    }

    public void setSolutionServerName(String solutionServerName) {
        this.solutionServerName = solutionServerName;
    }

    public String getSolutionPrefix() {
        return solutionPrefix;
    }

    public void setSolutionPrefix(String solutionPrefix) {
        this.solutionPrefix = solutionPrefix;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean template) {
        isTemplate = template;
    }

    public boolean isIconSet() {
        return isIconSet;
    }

    public void setIconSet(boolean iconSet) {
        isIconSet = iconSet;
    }

    public String getCaseDisplayName() {
        return caseDisplayName;
    }

    public void setCaseDisplayName(String caseDisplayName) {
        this.caseDisplayName = caseDisplayName;
    }
}