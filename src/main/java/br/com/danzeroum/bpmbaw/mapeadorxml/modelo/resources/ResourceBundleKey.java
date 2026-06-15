package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.resources;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceBundleKey {
    @XmlElement private String resourceBundleKeyId;
    @XmlElement private String resourceBundleId;
    @XmlElement private String key;
    @XmlElement private String value;
    @XmlElement private String guid;
    @XmlElement private String versionId;

    public String getResourceBundleKeyId() {
        return resourceBundleKeyId;
    }

    public void setResourceBundleKeyId(String resourceBundleKeyId) {
        this.resourceBundleKeyId = resourceBundleKeyId;
    }

    public String getResourceBundleId() {
        return resourceBundleId;
    }

    public void setResourceBundleId(String resourceBundleId) {
        this.resourceBundleId = resourceBundleId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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