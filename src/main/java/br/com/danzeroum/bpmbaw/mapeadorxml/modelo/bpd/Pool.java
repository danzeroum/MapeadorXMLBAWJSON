package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Representa um Pool em um BPD, que é o principal contêiner para
 * raias (lanes), variáveis, parâmetros e campos rastreáveis/pesquisáveis.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "pool")
public class Pool {

    @XmlAttribute
    private String id;

    @XmlElement
    private String name;

    @XmlElement
    private String documentation;

    @XmlElement
    private String restrictedName;

    @XmlElement
    private Dimension dimension;

    @XmlElement
    private boolean autoTrackingEnabled;

    @XmlElement(name = "lane")
    private List<Lane> lanes;

    @XmlElement(name = "inputParameter")
    private List<InputParameter> inputParameters;

    @XmlElement(name = "privateVariable")
    private List<PrivateVariable> privateVariables;

    @XmlElement(name = "trackedField")
    private List<TrackedField> trackedFields;

    @XmlElement(name = "searchableField")
    private List<SearchableField> searchableFields;


    // --- Getters e Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDocumentation() { return documentation; }
    public void setDocumentation(String documentation) { this.documentation = documentation; }

    public String getRestrictedName() { return restrictedName; }
    public void setRestrictedName(String restrictedName) { this.restrictedName = restrictedName; }

    public Dimension getDimension() { return dimension; }
    public void setDimension(Dimension dimension) { this.dimension = dimension; }

    public boolean isAutoTrackingEnabled() { return autoTrackingEnabled; }
    public void setAutoTrackingEnabled(boolean autoTrackingEnabled) { this.autoTrackingEnabled = autoTrackingEnabled; }

    public List<Lane> getLanes() { return lanes; }
    public void setLanes(List<Lane> lanes) { this.lanes = lanes; }

    public List<InputParameter> getInputParameters() { return inputParameters; }
    public void setInputParameters(List<InputParameter> inputParameters) { this.inputParameters = inputParameters; }

    public List<PrivateVariable> getPrivateVariables() { return privateVariables; }
    public void setPrivateVariables(List<PrivateVariable> privateVariables) { this.privateVariables = privateVariables; }

    public List<TrackedField> getTrackedFields() { return trackedFields; }
    public void setTrackedFields(List<TrackedField> trackedFields) { this.trackedFields = trackedFields; }

    public List<SearchableField> getSearchableFields() { return searchableFields; }
    public void setSearchableFields(List<SearchableField> searchableFields) { this.searchableFields = searchableFields; }
}