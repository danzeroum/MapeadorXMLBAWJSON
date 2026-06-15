package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Representa o conteúdo de uma tag <TWComponent>, que descreve a
 * implementação específica de um item de processo. Esta classe é polimórfica
 * para acomodar diferentes tipos de componentes como Scripts, Subprocessos e Coaches.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TWComponent {

    // --- Campos genéricos e de Auditoria ---
    @XmlElement private String lastModified;
    @XmlElement private String lastModifiedBy;
    @XmlElement private String tenantId;
    @XmlElement private String guid;
    @XmlElement private String versionId;

    // --- Campos para Scripts ---
    @XmlElement private String scriptId;
    @XmlElement private String script;

    // --- Campos para Pontos de Saída (Exit Points) ---
    @XmlElement private String exitPointId;
    @XmlElement private boolean haltProcess;

    // --- Campos para Subprocessos ---
    @XmlElement private String subProcessId;
    @XmlElement private String attachedProcessRef;
    @XmlElement(name = "parameterMapping") private List<ParameterMapping> parameterMapping;

    // --- Campos para Coaches (Client-Side Human Services) ---
    @XmlElement private String coachNGId;
    @XmlElement private String title;
    @XmlElement private String layoutData; // O layout do Coach é um XML grande, então String é o ideal

    @XmlElement(name = "CoachNGBoundaryEvents")
    private List<CoachNGBoundaryEvents> coachNGBoundaryEvents;

    @XmlElement(name = "SwitchCondition")
    private List<SwitchCondition> switchConditions;


    // --- Getters e Setters para todos os campos ---

    public String getLastModified() { return lastModified; }
    public void setLastModified(String lastModified) { this.lastModified = lastModified; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }

    public String getVersionId() { return versionId; }
    public void setVersionId(String versionId) { this.versionId = versionId; }

    public String getScriptId() { return scriptId; }
    public void setScriptId(String scriptId) { this.scriptId = scriptId; }

    public String getScript() { return script; }
    public void setScript(String script) { this.script = script; }

    public String getExitPointId() { return exitPointId; }
    public void setExitPointId(String exitPointId) { this.exitPointId = exitPointId; }

    public boolean isHaltProcess() { return haltProcess; }
    public void setHaltProcess(boolean haltProcess) { this.haltProcess = haltProcess; }

    public String getSubProcessId() { return subProcessId; }
    public void setSubProcessId(String subProcessId) { this.subProcessId = subProcessId; }

    public String getAttachedProcessRef() { return attachedProcessRef; }
    public void setAttachedProcessRef(String attachedProcessRef) { this.attachedProcessRef = attachedProcessRef; }

    public List<ParameterMapping> getParameterMapping() { return parameterMapping; }
    public void setParameterMapping(List<ParameterMapping> parameterMapping) { this.parameterMapping = parameterMapping; }

    public String getCoachNGId() { return coachNGId; }
    public void setCoachNGId(String coachNGId) { this.coachNGId = coachNGId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLayoutData() { return layoutData; }
    public void setLayoutData(String layoutData) { this.layoutData = layoutData; }

    public List<CoachNGBoundaryEvents> getCoachNGBoundaryEvents() { return coachNGBoundaryEvents; }
    public void setCoachNGBoundaryEvents(List<CoachNGBoundaryEvents> coachNGBoundaryEvents) { this.coachNGBoundaryEvents = coachNGBoundaryEvents; }

    public List<SwitchCondition> getSwitchConditions() {
        return switchConditions;
    }

    public void setSwitchConditions(List<SwitchCondition> switchConditions) {
        this.switchConditions = switchConditions;
    }

}