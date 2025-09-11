package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representa uma variável privada (local) de um BPD no IBM BAW.
 * Essas variáveis são usadas internamente no processo e não são
 * expostas como parâmetros de entrada ou saída.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "privateVariable")
public class PrivateVariable {

    @XmlAttribute
    private String id;

    @XmlElement
    private String name;

    @XmlElement
    private String classId;

    @XmlElement
    private boolean arrayOf;

    @XmlElement
    private boolean hasDefault;

    @XmlElement
    private boolean visibleInSearch;

    @XmlElement
    private boolean isProcessInstanceCorrelator;

    @XmlElement
    private boolean isSharedContext;


    // --- Getters e Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public boolean isArrayOf() {
        return arrayOf;
    }

    public void setArrayOf(boolean arrayOf) {
        this.arrayOf = arrayOf;
    }

    public boolean isHasDefault() {
        return hasDefault;
    }

    public void setHasDefault(boolean hasDefault) {
        this.hasDefault = hasDefault;
    }

    public boolean isVisibleInSearch() {
        return visibleInSearch;
    }

    public void setVisibleInSearch(boolean visibleInSearch) {
        this.visibleInSearch = visibleInSearch;
    }

    public boolean isProcessInstanceCorrelator() {
        return isProcessInstanceCorrelator;
    }

    public void setProcessInstanceCorrelator(boolean processInstanceCorrelator) {
        isProcessInstanceCorrelator = processInstanceCorrelator;
    }

    public boolean isSharedContext() {
        return isSharedContext;
    }

    public void setSharedContext(boolean sharedContext) {
        isSharedContext = sharedContext;
    }
}