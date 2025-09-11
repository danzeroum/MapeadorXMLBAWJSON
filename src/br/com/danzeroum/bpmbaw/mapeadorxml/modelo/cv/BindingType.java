package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class BindingType {
    @XmlAttribute private String name;
    @XmlElement private String coachViewBindingTypeId;
    @XmlElement private String coachViewId;
    @XmlElement private boolean isList;
    @XmlElement private String classId;
    @XmlElement private String guid;
    @XmlElement private String versionId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoachViewBindingTypeId() {
        return coachViewBindingTypeId;
    }

    public void setCoachViewBindingTypeId(String coachViewBindingTypeId) {
        this.coachViewBindingTypeId = coachViewBindingTypeId;
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

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
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