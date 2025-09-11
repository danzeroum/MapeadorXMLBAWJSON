package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg;

import javax.xml.bind.annotation.*;

/**
 * Representa uma tag <dependency>, uma dependência de Toolkit.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Dependency {
    @XmlAttribute private int rank;
    @XmlAttribute private boolean isManaged;
    @XmlAttribute private String id;

    @XmlElement(name = "project", namespace = "")
    private PackageProject project;

    @XmlElement(name = "branch", namespace = "")
    private Branch branch;

    @XmlElement(name = "snapshot", namespace = "")
    private Snapshot snapshot;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean isManaged() {
        return isManaged;
    }

    public void setManaged(boolean managed) {
        isManaged = managed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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