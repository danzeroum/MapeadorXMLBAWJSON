package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class InlineScript {
    @XmlAttribute private String name;
    @XmlElement private String coachViewInlineScriptId;
    @XmlElement private String coachViewId;
    @XmlElement private String scriptType;
    @XmlElement private String scriptBlock;
    @XmlElement private int seq;
    @XmlElement private String guid;
    @XmlElement private String versionId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoachViewInlineScriptId() {
        return coachViewInlineScriptId;
    }

    public void setCoachViewInlineScriptId(String coachViewInlineScriptId) {
        this.coachViewInlineScriptId = coachViewInlineScriptId;
    }

    public String getCoachViewId() {
        return coachViewId;
    }

    public void setCoachViewId(String coachViewId) {
        this.coachViewId = coachViewId;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getScriptBlock() {
        return scriptBlock;
    }

    public void setScriptBlock(String scriptBlock) {
        this.scriptBlock = scriptBlock;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
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