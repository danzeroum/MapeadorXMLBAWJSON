package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representa um evento de limite (Boundary Event) associado a um Coach.
 * Define o que acontece quando uma ação específica na interface (ex: um botão) é acionada.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CoachNGBoundaryEvents")
public class CoachNGBoundaryEvents {

    @XmlElement private String lastModified;
    @XmlElement private String lastModifiedBy;
    @XmlElement private String tenantId;
    @XmlElement private String coachNGBoundaryEventId;
    @XmlElement private String coachNGId;
    @XmlElement private int seq;
    @XmlElement private String endStateId;
    @XmlElement private String eventLabel;
    @XmlElement private String viewPath;
    @XmlElement private boolean isValidateBoundaryEvent;
    @XmlElement private int fireValidation;
    @XmlElement private String guid;
    @XmlElement private String versionId;

    // --- Getters e Setters ---
    // (Getters e setters para todos os campos acima)


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

    public String getCoachNGBoundaryEventId() {
        return coachNGBoundaryEventId;
    }

    public void setCoachNGBoundaryEventId(String coachNGBoundaryEventId) {
        this.coachNGBoundaryEventId = coachNGBoundaryEventId;
    }

    public String getCoachNGId() {
        return coachNGId;
    }

    public void setCoachNGId(String coachNGId) {
        this.coachNGId = coachNGId;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getEndStateId() {
        return endStateId;
    }

    public void setEndStateId(String endStateId) {
        this.endStateId = endStateId;
    }

    public String getEventLabel() {
        return eventLabel;
    }

    public void setEventLabel(String eventLabel) {
        this.eventLabel = eventLabel;
    }

    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    public boolean isValidateBoundaryEvent() {
        return isValidateBoundaryEvent;
    }

    public void setValidateBoundaryEvent(boolean validateBoundaryEvent) {
        isValidateBoundaryEvent = validateBoundaryEvent;
    }

    public int getFireValidation() {
        return fireValidation;
    }

    public void setFireValidation(int fireValidation) {
        this.fireValidation = fireValidation;
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