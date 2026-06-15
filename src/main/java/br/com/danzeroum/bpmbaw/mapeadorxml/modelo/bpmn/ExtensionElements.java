package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension.BpdExtension;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension.CaseExtension;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension.UserTaskSettings;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class ExtensionElements {

    private static final String IBM_BPM_NAMESPACE = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/wle";
    private static final String IBM_TW_NAMESPACE = "http://www.ibm.com/bpm/coachdesignerng";
    private static final String IBM_CASE_NAMESPACE = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/case";

    @XmlElement(name = "default", namespace = IBM_TW_NAMESPACE)
    private String defaultFlow;

    @XmlElement(name = "deleteTaskOnCompletion", namespace = IBM_BPM_NAMESPACE)
    private boolean deleteTaskOnCompletion;

    @XmlElement(name = "userTaskSettings", namespace = IBM_BPM_NAMESPACE)
    private UserTaskSettings userTaskSettings;

    @XmlElement(name = "activityType", namespace = IBM_BPM_NAMESPACE)
    private String activityType;

    // --- ADIÇÕES PARA EXTENSÕES DO PROCESSO ---
    @XmlElement(name = "bpdExtension", namespace = IBM_BPM_NAMESPACE)
    private BpdExtension bpdExtension;

    @XmlElement(name = "caseExtension", namespace = IBM_CASE_NAMESPACE)
    private CaseExtension caseExtension;

    @XmlElement(name = "isConvergedProcess", namespace = IBM_CASE_NAMESPACE)
    private boolean isConvergedProcess;

    @XmlElement(name = "userTaskImplementation", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
    private UserTaskImplementation userTaskImplementation;

    @XmlElement(name = "sequenceFlowImplementation", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
    private SequenceFlowImplementation sequenceFlowImplementation;

    @XmlElement(name = "coachEventBinding", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
    private CoachEventBinding coachEventBinding;

    @XmlElement(name = "linkVisualInfo", namespace = "http://www.ibm.com/xmlns/prod/bpm/graph")
    private LinkVisualInfo linkVisualInfo;

    @XmlElement(name = "localizationResourceLinks", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
    private LocalizationResourceLinks localizationResourceLinks;

    @XmlElement(name = "envProcessLinks", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
    private EnvProcessLinks envProcessLinks;

    @XmlElement(name = "defaultValue", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
    private DefaultValue defaultValue;


    // Getters e Setters
    public String getDefaultFlow() { return defaultFlow; }
    public void setDefaultFlow(String defaultFlow) { this.defaultFlow = defaultFlow; }
    public boolean isDeleteTaskOnCompletion() { return deleteTaskOnCompletion; }
    public void setDeleteTaskOnCompletion(boolean deleteTaskOnCompletion) { this.deleteTaskOnCompletion = deleteTaskOnCompletion; }
    public UserTaskSettings getUserTaskSettings() { return userTaskSettings; }
    public void setUserTaskSettings(UserTaskSettings userTaskSettings) { this.userTaskSettings = userTaskSettings; }
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public BpdExtension getBpdExtension() { return bpdExtension; }
    public void setBpdExtension(BpdExtension bpdExtension) { this.bpdExtension = bpdExtension; }
    public CaseExtension getCaseExtension() { return caseExtension; }
    public void setCaseExtension(CaseExtension caseExtension) { this.caseExtension = caseExtension; }
    public boolean isConvergedProcess() { return isConvergedProcess; }
    public void setConvergedProcess(boolean isConvergedProcess) { this.isConvergedProcess = isConvergedProcess; }

    public UserTaskImplementation getUserTaskImplementation() {
        return userTaskImplementation;
    }

    public void setUserTaskImplementation(UserTaskImplementation userTaskImplementation) {
        this.userTaskImplementation = userTaskImplementation;
    }

    public SequenceFlowImplementation getSequenceFlowImplementation() {
        return sequenceFlowImplementation;
    }

    public void setSequenceFlowImplementation(SequenceFlowImplementation sequenceFlowImplementation) {
        this.sequenceFlowImplementation = sequenceFlowImplementation;
    }

    public CoachEventBinding getCoachEventBinding() {
        return coachEventBinding;
    }

    public void setCoachEventBinding(CoachEventBinding coachEventBinding) {
        this.coachEventBinding = coachEventBinding;
    }

    public LinkVisualInfo getLinkVisualInfo() {
        return linkVisualInfo;
    }

    public void setLinkVisualInfo(LinkVisualInfo linkVisualInfo) {
        this.linkVisualInfo = linkVisualInfo;
    }

    public LocalizationResourceLinks getLocalizationResourceLinks() {
        return localizationResourceLinks;
    }

    public void setLocalizationResourceLinks(LocalizationResourceLinks localizationResourceLinks) {
        this.localizationResourceLinks = localizationResourceLinks;
    }

    public EnvProcessLinks getEnvProcessLinks() {
        return envProcessLinks;
    }

    public void setEnvProcessLinks(EnvProcessLinks envProcessLinks) {
        this.envProcessLinks = envProcessLinks;
    }

    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(DefaultValue defaultValue) {
        this.defaultValue = defaultValue;
    }
}