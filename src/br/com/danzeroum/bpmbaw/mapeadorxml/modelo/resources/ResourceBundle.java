package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.resources;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceBundle {
    @XmlElement private String resourceBundleId;
    @XmlElement private String resourceBundleGroupId;
    @XmlElement private String localeLanguage;
    @XmlElement private String localeCountry;
    @XmlElement private String localeVariant;
    @XmlElement private String guid;
    @XmlElement private String versionId;

    @XmlElement(name = "resourceBundleKey")
    private List<ResourceBundleKey> resourceBundleKeys;

    // ... Getters e Setters

    public String getResourceBundleId() {
        return resourceBundleId;
    }

    public void setResourceBundleId(String resourceBundleId) {
        this.resourceBundleId = resourceBundleId;
    }

    public String getResourceBundleGroupId() {
        return resourceBundleGroupId;
    }

    public void setResourceBundleGroupId(String resourceBundleGroupId) {
        this.resourceBundleGroupId = resourceBundleGroupId;
    }

    public String getLocaleLanguage() {
        return localeLanguage;
    }

    public void setLocaleLanguage(String localeLanguage) {
        this.localeLanguage = localeLanguage;
    }

    public String getLocaleCountry() {
        return localeCountry;
    }

    public void setLocaleCountry(String localeCountry) {
        this.localeCountry = localeCountry;
    }

    public String getLocaleVariant() {
        return localeVariant;
    }

    public void setLocaleVariant(String localeVariant) {
        this.localeVariant = localeVariant;
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

    public List<ResourceBundleKey> getResourceBundleKeys() {
        return resourceBundleKeys;
    }

    public void setResourceBundleKeys(List<ResourceBundleKey> resourceBundleKeys) {
        this.resourceBundleKeys = resourceBundleKeys;
    }
}