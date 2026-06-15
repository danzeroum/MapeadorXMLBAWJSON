package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * Representa um LaneSet, que é um contêiner para um ou mais Lanes.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LaneSet {

    @XmlAttribute
    private String id;

    @XmlElement(name = "lane", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private List<Lane> lanes;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<Lane> getLanes() { return lanes; }
    public void setLanes(List<Lane> lanes) { this.lanes = lanes; }
}