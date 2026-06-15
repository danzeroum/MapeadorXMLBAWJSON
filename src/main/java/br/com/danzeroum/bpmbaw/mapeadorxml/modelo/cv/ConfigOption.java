package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigOption {
    @XmlAttribute private String name;
    @XmlElement private String coachViewConfigOptionId;
    @XmlElement private String coachViewId;
    @XmlElement private boolean isList;
    @XmlElement private String propertyType;
    @XmlElement private String label;
    @XmlElement private String classId;
    @XmlElement private boolean isAdaptive;
    @XmlElement private int seq;
    @XmlElement private String description;
    @XmlElement private String guid;
    @XmlElement private String versionId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoachViewConfigOptionId() {
        return coachViewConfigOptionId;
    }

    public void setCoachViewConfigOptionId(String coachViewConfigOptionId) {
        this.coachViewConfigOptionId = coachViewConfigOptionId;
    }

    public String getCoachViewId() {
        return coachViewId;
    }

    public void setCoachViewId(String coachViewId) {
        this.coachViewId = coachViewId;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public boolean isAdaptive() {
        return isAdaptive;
    }

    public void setAdaptive(boolean adaptive) {
        isAdaptive = adaptive;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }
}