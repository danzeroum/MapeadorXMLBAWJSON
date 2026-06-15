package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg;

import jakarta.xml.bind.annotation.*;

/**
 * Representa a tag <target>, o alvo principal da exportação.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Target {
    @XmlElement(name = "project", namespace = "") // Namespace vazio para sobrescrever o do pai
    private PackageProject project;

    @XmlElement(name = "branch", namespace = "")
    private Branch branch;

    @XmlElement(name = "snapshot", namespace = "")
    private Snapshot snapshot;

    public PackageProject getProject() {
        return project;
    }

    public void setProject(PackageProject project) {
        this.project = project;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = snapshot;
    }
}