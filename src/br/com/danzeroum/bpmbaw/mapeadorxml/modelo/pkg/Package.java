package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Representa o arquivo de manifesto (package.xml) de uma exportação .twx.
 */
@XmlRootElement(name = "package", namespace = "http://lombardisoftware.com/schema/teamworks/7.0.0/package.xsd")
@XmlAccessorType(XmlAccessType.FIELD)
public class Package {

    @XmlAttribute
    private String buildId;
    @XmlAttribute
    private String buildVersion;
    @XmlAttribute
    private String buildDescription;
    @XmlAttribute
    private String fixPack;
    @XmlAttribute
    private boolean containsECM;
    @XmlAttribute
    private boolean containsBPMN2;

    @XmlElement(name = "target")
    private Target target;

    @XmlElementWrapper(name = "dependencies")
    @XmlElement(name = "dependency")
    private List<Dependency> dependencies;

    @XmlElementWrapper(name = "objects")
    @XmlElement(name = "object")
    private List<PackageObject> objects;

    @XmlElementWrapper(name = "files")
    @XmlElement(name = "file")
    private List<PackageFile> files;

    @XmlElement
    private String governanceAssignments; // Mapeia a tag vazia

    @XmlElement
    private String migrationPolicies; // Mapeia a tag vazia

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    public String getBuildDescription() {
        return buildDescription;
    }

    public void setBuildDescription(String buildDescription) {
        this.buildDescription = buildDescription;
    }

    public String getFixPack() {
        return fixPack;
    }

    public void setFixPack(String fixPack) {
        this.fixPack = fixPack;
    }

    public boolean isContainsECM() {
        return containsECM;
    }

    public void setContainsECM(boolean containsECM) {
        this.containsECM = containsECM;
    }

    public boolean isContainsBPMN2() {
        return containsBPMN2;
    }

    public void setContainsBPMN2(boolean containsBPMN2) {
        this.containsBPMN2 = containsBPMN2;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public List<PackageObject> getObjects() {
        return objects;
    }

    public void setObjects(List<PackageObject> objects) {
        this.objects = objects;
    }

    public List<PackageFile> getFiles() {
        return files;
    }

    public void setFiles(List<PackageFile> files) {
        this.files = files;
    }

    public String getGovernanceAssignments() {
        return governanceAssignments;
    }

    public void setGovernanceAssignments(String governanceAssignments) {
        this.governanceAssignments = governanceAssignments;
    }

    public String getMigrationPolicies() {
        return migrationPolicies;
    }

    public void setMigrationPolicies(String migrationPolicies) {
        this.migrationPolicies = migrationPolicies;
    }
}