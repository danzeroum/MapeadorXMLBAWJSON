package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.BpmnObjectId;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa uma anotação de texto (Note) em um diagrama BPD legado.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "note")
public class Note {

    @XmlAttribute
    private String id;

    @XmlElement
    private String name;

    @XmlElement
    private String documentation;

    @XmlElement
    private Position position;

    @XmlElement
    private Dimension dimension;

    @XmlElement
    private LaneObjectId laneObjectId;

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

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public LaneObjectId getLaneObjectId() {
        return laneObjectId;
    }

    public void setLaneObjectId(LaneObjectId laneObjectId) {
        this.laneObjectId = laneObjectId;
    }

    /**
     * Classe interna para mapear a tag <laneObjectId>.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class LaneObjectId {
        @XmlElement(name = "BpmnObjectId")
        private BpmnObjectId bpmnObjectId;

        public BpmnObjectId getBpmnObjectId() {
            return bpmnObjectId;
        }

        public void setBpmnObjectId(BpmnObjectId bpmnObjectId) {
            this.bpmnObjectId = bpmnObjectId;
        }
    }
}