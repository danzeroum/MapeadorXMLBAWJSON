package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Representa uma variável de um processo (BPD) ou serviço no IBM BAW.
 * Esta classe mapeia as tags encontradas no arquivo XML (.twx) de exportação.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessVariable {

    // --- Atributos existentes ---

    @XmlAttribute
    private String name;

    @XmlElement
    private String processVariableId;

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
    private String description; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String processId;

    @XmlElement
    private int namespace;

    @XmlElement
    private int seq;

    @XmlElement
    private boolean isTransient;

    @XmlElement
    private boolean hasDefault;

    @XmlElement
    private String defaultValue; // Usado como String para acomodar isNull="true"

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

    public String getProcessVariableId() {
        return processVariableId;
    }

    public void setProcessVariableId(String processVariableId) {
        this.processVariableId = processVariableId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public int getNamespace() {
        return namespace;
    }

    public void setNamespace(int namespace) {
        this.namespace = namespace;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean aTransient) {
        isTransient = aTransient;
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