package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Representa um parâmetro (variável) dentro de uma definição de processo (BPD) no IBM BAW.
 * Esta classe mapeia as tags encontradas no arquivo XML (.twx) de exportação.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessParameter {

    // --- Atributos existentes ---

    @XmlAttribute
    private String name;

    @XmlElement
    private String processParameterId;

    @XmlElement
    private int parameterType;

    @XmlElement
    private boolean isArrayOf;

    @XmlElement
    private String classId;

    // --- Atributos adicionados com base no XML ---

    @XmlElement
    private String lastModified; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String lastModifiedBy; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String tenantId; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String processId;

    @XmlElement
    private int seq;

    @XmlElement
    private boolean hasDefault;

    @XmlElement
    private String defaultValue; // Usado como String para acomodar isNull="true"

    @XmlElement
    private boolean isLocked;

    @XmlElement
    private String description; // Usado como String para acomodar isNull="true"

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

    public String getProcessParameterId() {
        return processParameterId;
    }

    public void setProcessParameterId(String processParameterId) {
        this.processParameterId = processParameterId;
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

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
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

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
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