package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.*;

/**
 * Representa um fluxo de sequência (Sequence Flow) em um BPD.
 * Versão atualizada para armazenar os IDs de origem e destino.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Flow {

    // --- Campos do XML ---
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String connectionType;
    @XmlElement
    private String name;
    @XmlElement
    private boolean nameVisible;
    @XmlElement
    private MetricSettings metricSettings;
    @XmlElement
    private SimulationScenarioConfig simulationScenarioConfig;
    @XmlElement
    private Connection connection;

    // --- Campos para uso em memória (adicionados na última revisão) ---
    @XmlTransient
    private String sourceObjectId;
    @XmlTransient
    private String targetObjectId;


    // --- Getters e Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getConnectionType() { return connectionType; }
    public void setConnectionType(String connectionType) { this.connectionType = connectionType; }

    public boolean isNameVisible() { return nameVisible; }
    public void setNameVisible(boolean nameVisible) { this.nameVisible = nameVisible; }

    public MetricSettings getMetricSettings() { return metricSettings; }
    public void setMetricSettings(MetricSettings metricSettings) { this.metricSettings = metricSettings; }

    public SimulationScenarioConfig getSimulationScenarioConfig() { return simulationScenarioConfig; }
    public void setSimulationScenarioConfig(SimulationScenarioConfig simulationScenarioConfig) { this.simulationScenarioConfig = simulationScenarioConfig; }

    public Connection getConnection() { return connection; }
    public void setConnection(Connection connection) { this.connection = connection; }

    // --- Getters e Setters para os novos campos (que estavam faltando) ---
    public String getSourceObjectId() { return sourceObjectId; }
    public void setSourceObjectId(String sourceObjectId) { this.sourceObjectId = sourceObjectId; }

    public String getTargetObjectId() { return targetObjectId; }
    public void setTargetObjectId(String targetObjectId) { this.targetObjectId = targetObjectId; }


    // --- Classes Internas ---
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "BpdFlowConnection")
    public static class Connection {
        @XmlElement private int lineType;
        @XmlElement private Condition condition;
        public int getLineType() { return lineType; }
        public void setLineType(int lineType) { this.lineType = lineType; }
        public Condition getCondition() { return condition; }
        public void setCondition(Condition condition) { this.condition = condition; }
    }

    // --- Classe Interna para a tag <condition> (ATUALIZADA) ---
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "BpdFlowCondition")
    public static class Condition {

        @XmlAttribute
        private String id;

        // --- CAMPO ADICIONADO ---
        @XmlElement
        private String expression;

        // --- Getters e Setters ---
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }
    }
}