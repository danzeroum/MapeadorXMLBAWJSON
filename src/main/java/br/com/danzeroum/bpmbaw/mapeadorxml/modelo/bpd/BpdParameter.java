package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa um parâmetro (variável de entrada ou saída) de uma
 * Definição de Processo de Negócio (BPD) no IBM BAW.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "bpdParameter")
public class BpdParameter {

    @XmlAttribute
    private String name;

    @XmlElement
    private String lastModified;

    @XmlElement
    private String lastModifiedBy;

    @XmlElement
    private String tenantId;

    @XmlElement
    private String bpdParameterId;

    @XmlElement
    private String bpdId;

    @XmlElement
    private int parameterType;

    @XmlElement
    private boolean isArrayOf;

    @XmlElement
    private String classId;

    @XmlElement
    private int seq;

    @XmlElement
    private String documentation;

    @XmlElement
    private boolean hasDefault;

    @XmlElement
    private String defaultValue;

    @XmlElement
    private boolean isReadOnly;

    @XmlElement
    private String guid;

    @XmlElement
    private String versionId;


    // --- Getters e Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getBpdParameterId() {
        return bpdParameterId;
    }

    public void setBpdParameterId(String bpdParameterId) {
        this.bpdParameterId = bpdParameterId;
    }

    public String getBpdId() {
        return bpdId;
    }

    public void setBpdId(String bpdId) {
        this.bpdId = bpdId;
    }

    public int getParameterType() {
        return parameterType;
    }

    public void setParameterType(int parameterType) {
        this.parameterType = parameterType;
    }

    public boolean isArrayOf() {
        return isArrayOf;
    }

    public void setArrayOf(boolean arrayOf) {
        isArrayOf = arrayOf;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public boolean isHasDefault() {
        return hasDefault;
    }

    public void setHasDefault(boolean hasDefault) {
        this.hasDefault = hasDefault;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
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