package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ProcessPrePosts")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessPrePost {

    @XmlElement
    private String lastModified; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String lastModifiedBy; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String tenantId; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String processItemPrePostId;

    @XmlElement
    private String processItemId;

    @XmlElement
    private int location;

    @XmlElement
    private String script; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String guid;

    @XmlElement
    private String versionId;


    // --- Getters e Setters ---

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getProcessItemPrePostId() {
        return processItemPrePostId;
    }

    public void setProcessItemPrePostId(String processItemPrePostId) {
        this.processItemPrePostId = processItemPrePostId;
    }

    public String getProcessItemId() {
        return processItemId;
    }

    public void setProcessItemId(String processItemId) {
        this.processItemId = processItemId;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
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