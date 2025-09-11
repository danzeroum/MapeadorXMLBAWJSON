package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.participant;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Representa um Grupo de Participantes (Equipe) no IBM BAW.
 */
@XmlRootElement(name = "participant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Participant {

    @XmlAttribute
    private String id;
    @XmlAttribute
    private String name;

    @XmlElement private long lastModified;
    @XmlElement private String lastModifiedBy;
    @XmlElement private String participantId;
    @XmlElement private int simulationGroupSize;
    @XmlElement private double cost;
    @XmlElement private String description;
    @XmlElement private String guid;
    @XmlElement private String versionId;

    // A anotação @XmlElementWrapper cria a tag <standardMembers> em volta da lista
    @XmlElementWrapper(name = "standardMembers")
    @XmlElement(name = "standardMember")
    private List<StandardMember> standardMembers;

    // Mapeia a tag vazia <teamAssignments/>
    @XmlElement
    private String teamAssignments;

    // --- Getters e Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    public List<StandardMember> getStandardMembers() { return standardMembers; }
    public void setStandardMembers(List<StandardMember> standardMembers) { this.standardMembers = standardMembers; }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public int getSimulationGroupSize() {
        return simulationGroupSize;
    }

    public void setSimulationGroupSize(int simulationGroupSize) {
        this.simulationGroupSize = simulationGroupSize;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
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

    public String getTeamAssignments() {
        return teamAssignments;
    }

    public void setTeamAssignments(String teamAssignments) {
        this.teamAssignments = teamAssignments;
    }
}