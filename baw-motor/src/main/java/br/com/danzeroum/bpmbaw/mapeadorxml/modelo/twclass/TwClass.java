package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa um Objeto de Negócio (Business Object) no IBM BAW.
 * É o contêiner de mais alto nível para a definição de um tipo de dado complexo.
 */
@XmlRootElement(name = "twClass")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwClass {

    @XmlAttribute private String id;
    @XmlAttribute private String name;

    @XmlElement private long lastModified;
    @XmlElement private String lastModifiedBy;
    @XmlElement private String tenantId;
    @XmlElement private String classId;
    @XmlElement private int type;
    @XmlElement private boolean isSystem;
    @XmlElement private boolean shared;
    @XmlElement private boolean isShadow;
    @XmlElement private boolean globalLifetime;
    @XmlElement private String internalName;
    @XmlElement private String extensionType;
    @XmlElement private String saveServiceRef;
    @XmlElement private String bpmn2Data;
    @XmlElement private String externalId;
    @XmlElement private String dependencySummary;
    @XmlElement private String jsonData;
    @XmlElement private String dataName;
    @XmlElement private boolean allowAdditionalProperties;
    @XmlElement private String description;
    @XmlElement private String guid;
    @XmlElement private String versionId;

    @XmlElement
    private Definition definition;

    // --- Getters e Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public boolean isSystem() { return isSystem; }
    public void setSystem(boolean isSystem) { this.isSystem = isSystem; }

    public boolean isShared() { return shared; }
    public void setShared(boolean shared) { this.shared = shared; }

    public boolean isShadow() { return isShadow; }
    public void setShadow(boolean isShadow) { this.isShadow = isShadow; }

    public boolean isGlobalLifetime() { return globalLifetime; }
    public void setGlobalLifetime(boolean globalLifetime) { this.globalLifetime = globalLifetime; }

    public String getInternalName() { return internalName; }
    public void setInternalName(String internalName) { this.internalName = internalName; }

    public String getExtensionType() { return extensionType; }
    public void setExtensionType(String extensionType) { this.extensionType = extensionType; }

    public String getSaveServiceRef() { return saveServiceRef; }
    public void setSaveServiceRef(String saveServiceRef) { this.saveServiceRef = saveServiceRef; }

    public String getBpmn2Data() { return bpmn2Data; }
    public void setBpmn2Data(String bpmn2Data) { this.bpmn2Data = bpmn2Data; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getDependencySummary() { return dependencySummary; }
    public void setDependencySummary(String dependencySummary) { this.dependencySummary = dependencySummary; }

    public String getJsonData() { return jsonData; }
    public void setJsonData(String jsonData) { this.jsonData = jsonData; }

    public String getDataName() { return dataName; }
    public void setDataName(String dataName) { this.dataName = dataName; }

    public boolean isAllowAdditionalProperties() { return allowAdditionalProperties; }
    public void setAllowAdditionalProperties(boolean allowAdditionalProperties) { this.allowAdditionalProperties = allowAdditionalProperties; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }

    public String getVersionId() { return versionId; }
    public void setVersionId(String versionId) { this.versionId = versionId; }

    public Definition getDefinition() { return definition; }
    public void setDefinition(Definition definition) { this.definition = definition; }
}