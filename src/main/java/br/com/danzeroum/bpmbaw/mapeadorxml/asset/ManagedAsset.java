package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.asset;

import jakarta.xml.bind.annotation.*;

/**
 * Representa um Arquivo Gerenciado (Managed Asset) no IBM BAW,
 * como um arquivo de imagem, CSS, ou HTML.
 */
@XmlRootElement(name = "managedAsset")
@XmlAccessorType(XmlAccessType.FIELD)
public class ManagedAsset {

    @XmlAttribute
    private String id;
    @XmlAttribute
    private String name;

    @XmlElement private long lastModified;
    @XmlElement private String lastModifiedBy;
    @XmlElement private String managedAssetId;
    @XmlElement private String assetUuid;
    @XmlElement private String mimeType;
    @XmlElement private String assetTypeCode;
    @XmlElement private int length;
    @XmlElement private long localLastModification;
    @XmlElement private boolean isDocumentationFile;
    @XmlElement private String guid;
    @XmlElement private String versionId;

    // --- Getters e Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    public String getManagedAssetId() { return managedAssetId; }
    public void setManagedAssetId(String managedAssetId) { this.managedAssetId = managedAssetId; }
    public String getAssetUuid() { return assetUuid; }
    public void setAssetUuid(String assetUuid) { this.assetUuid = assetUuid; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public String getAssetTypeCode() { return assetTypeCode; }
    public void setAssetTypeCode(String assetTypeCode) { this.assetTypeCode = assetTypeCode; }
    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }
    public long getLocalLastModification() { return localLastModification; }
    public void setLocalLastModification(long localLastModification) { this.localLastModification = localLastModification; }
    public boolean isDocumentationFile() { return isDocumentationFile; }
    public void setDocumentationFile(boolean documentationFile) { isDocumentationFile = documentationFile; }
    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }
    public String getVersionId() { return versionId; }
    public void setVersionId(String versionId) { this.versionId = versionId; }
}