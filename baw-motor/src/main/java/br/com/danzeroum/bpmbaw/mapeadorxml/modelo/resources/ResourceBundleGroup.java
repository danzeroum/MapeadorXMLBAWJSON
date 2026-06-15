package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.resources;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "resourceBundleGroup")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceBundleGroup {

    @XmlAttribute
    private String id;
    @XmlAttribute
    private String name;

    @XmlElement private long lastModified;
    @XmlElement private String lastModifiedBy;
    @XmlElement private String resourceBundleGroupId;
    @XmlElement private String description;
    @XmlElement private String guid;
    @XmlElement private String versionId;

    @XmlElement(name = "resourceBundle")
    private List<ResourceBundle> resourceBundles;

    // --- Getters e Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<ResourceBundle> getResourceBundles() { return resourceBundles; }
    public void setResourceBundles(List<ResourceBundle> resourceBundles) { this.resourceBundles = resourceBundles; }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getResourceBundleGroupId() {
        return resourceBundleGroupId;
    }

    public void setResourceBundleGroupId(String resourceBundleGroupId) {
        this.resourceBundleGroupId = resourceBundleGroupId;
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