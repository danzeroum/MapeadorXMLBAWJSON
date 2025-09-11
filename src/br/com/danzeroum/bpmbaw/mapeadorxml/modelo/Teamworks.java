package br.com.danzeroum.bpmbaw.mapeadorxml.modelo;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.asset.ManagedAsset;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.CoachView;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.participant.Participant;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.resources.ResourceBundleGroup;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.envar.EnvironmentVariableSet; // <-- ADICIONE ESTA IMPORTAÇÃO

/**
 * Representa o elemento raiz <teamworks> de uma exportação do IBM BAW.
 * Esta classe é universal e pode lidar com diferentes tipos de exportação,
 * como um projeto completo (.twx) ou um artefato individual (BPD, Process, Coach View, etc.).
 */
@XmlRootElement(name = "teamworks")
@XmlAccessorType(XmlAccessType.FIELD)
public class Teamworks {

    // --- CAMINHOS DE MAPEAMENTO ---

    // 1. Para um .twx COMPLETO (Process App)
    @XmlElement(name = "project")
    private Project project;

    // 2. Para a exportação de um ÚNICO artefato individual
    @XmlElement(name = "bpd")
    private Bpd bpd; // <-- CAMPO ADICIONADO

    @XmlElement(name = "process")
    private Process process; // <-- CAMPO ADICIONADO

    @XmlElement(name = "coachView")
    private CoachView coachView;

    @XmlElement(name = "twClass")
    private TwClass twClass;

    @XmlElement(name = "participant")
    private Participant participant; // Para exportação de um único participante
    @XmlElement(name = "resourceBundleGroup")
    private ResourceBundleGroup resourceBundleGroup; // Para exportação individual
    @XmlElement(name = "managedAsset")
    private br.com.danzeroum.bpmbaw.mapeadorxml.modelo.asset.ManagedAsset managedAsset;
    @XmlElement(name = "environmentVariableSet")
    private EnvironmentVariableSet environmentVariableSet; // <-- ADICIONE ESTE CAMPO


    // --- Getters e Setters ---

    public EnvironmentVariableSet getEnvironmentVariableSet() {
        return environmentVariableSet;
    }

    public void setEnvironmentVariableSet(EnvironmentVariableSet environmentVariableSet) {
        this.environmentVariableSet = environmentVariableSet;
    }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Bpd getBpd() { return bpd; }
    public void setBpd(Bpd bpd) { this.bpd = bpd; }

    public Process getProcess() { return process; }
    public void setProcess(Process process) { this.process = process; }

    public CoachView getCoachView() { return coachView; }
    public void setCoachView(CoachView coachView) { this.coachView = coachView; }

    public TwClass getTwClass() {
        return twClass;
    }

    public void setTwClass(TwClass twClass) {
        this.twClass = twClass;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public ResourceBundleGroup getResourceBundleGroup() {
        return resourceBundleGroup;
    }


    public void setResourceBundleGroup(ResourceBundleGroup resourceBundleGroup) {
        this.resourceBundleGroup = resourceBundleGroup;
    }

    public ManagedAsset getManagedAsset() {
        return managedAsset;
    }

    public void setManagedAsset(ManagedAsset managedAsset) {
        this.managedAsset = managedAsset;
    }

    /**
     * Representa a tag <project>, contida em um .twx completo.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Project {

        @XmlElement(name = "bpd")
        private List<Bpd> bpdList;

        @XmlElement(name = "process")
        private List<Process> processList;

        @XmlElement(name = "coachView")
        private List<CoachView> coachViewList;

        @XmlElement(name = "twClass")
        private List<TwClass> twClassList;

        @XmlElement(name = "participant")
        private List<Participant> participantList; // Para a lista dentro de um .twx

        @XmlElement(name = "resourceBundleGroup")
        private List<ResourceBundleGroup> resourceBundleGroupList; // Para a lista dentro de um .twx

        @XmlElement(name = "managedAsset")
        private List<ManagedAsset> managedAssetList; // Para a lista dentro de um .twx

        // --- Getters e Setters ---
        public List<Bpd> getBpdList() { return bpdList; }
        public void setBpdList(List<Bpd> bpdList) { this.bpdList = bpdList; }

        public List<Process> getProcessList() { return processList; }
        public void setProcessList(List<Process> processList) { this.processList = processList; }

        public List<CoachView> getCoachViewList() { return coachViewList; }
        public void setCoachViewList(List<CoachView> coachViewList) { this.coachViewList = coachViewList; }

        public List<TwClass> getTwClassList() { return twClassList;
        }

        public void setTwClassList(List<TwClass> twClassList) {
            this.twClassList = twClassList;
        }

        public List<Participant> getParticipantList() {
            return participantList;
        }

        public void setParticipantList(List<Participant> participantList) {
            this.participantList = participantList;
        }

        public List<ResourceBundleGroup> getResourceBundleGroupList() {
            return resourceBundleGroupList;
        }

        public void setResourceBundleGroupList(List<ResourceBundleGroup> resourceBundleGroupList) {
            this.resourceBundleGroupList = resourceBundleGroupList;
        }

        public List<ManagedAsset> getManagedAssetList() {
            return managedAssetList;
        }

        public void setManagedAssetList(List<ManagedAsset> managedAssetList) {
            this.managedAssetList = managedAssetList;
        }
    }
}