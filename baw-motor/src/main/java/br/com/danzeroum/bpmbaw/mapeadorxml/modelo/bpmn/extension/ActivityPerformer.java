package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.DataInputAssociation;
import jakarta.xml.bind.annotation.*;

/**
 * Modela a quem uma tarefa é atribuída (o "performer").
 * Pode ser uma Lane, uma Equipe, e pode usar um serviço de filtro para determinar dinamicamente os usuários.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivityPerformer {

    private static final String IBM_BPM_NAMESPACE = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/wle";
    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    @XmlAttribute
    private String distribution;

    @XmlAttribute
    private String name;

    // --- CAMPO ADICIONADO ---
    // Mapeia a referência estática a uma equipe/lane
    @XmlElement(name = "resourceRef", namespace = BPMN_NAMESPACE)
    private String resourceRef;

    @XmlElement(name = "teamFilterService", namespace = IBM_BPM_NAMESPACE)
    private TeamFilterService teamFilterService;

    // --- Getters e Setters ---
    public String getDistribution() { return distribution; }
    public void setDistribution(String distribution) { this.distribution = distribution; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getResourceRef() { return resourceRef; }
    public void setResourceRef(String resourceRef) { this.resourceRef = resourceRef; }

    public TeamFilterService getTeamFilterService() { return teamFilterService; }
    public void setTeamFilterService(TeamFilterService teamFilterService) { this.teamFilterService = teamFilterService; }


    /**
     * Subclasse interna para mapear a tag <teamFilterService>.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TeamFilterService {

        @XmlAttribute
        private String serviceRef;

        // Reutiliza a classe DataInputAssociation que já definimos para CallActivity
        @XmlElement(name = "dataInputAssociation", namespace = BPMN_NAMESPACE)
        private java.util.List<DataInputAssociation> dataInputAssociations;

        // --- Getters e Setters ---
        public String getServiceRef() { return serviceRef; }
        public void setServiceRef(String serviceRef) { this.serviceRef = serviceRef; }

        public java.util.List<DataInputAssociation> getDataInputAssociations() { return dataInputAssociations; }
        public void setDataInputAssociations(java.util.List<DataInputAssociation> dataInputAssociations) { this.dataInputAssociations = dataInputAssociations; }
    }
}