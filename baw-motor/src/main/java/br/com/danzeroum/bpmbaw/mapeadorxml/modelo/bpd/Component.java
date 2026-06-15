package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Representa o componente de uma atividade em um BPD.
 * Contém a lógica de execução, como loops, condições, e a implementação detalhada.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "component")
public class Component {

    // --- Campos de Configuração ---
    @XmlElement private int loopType;
    @XmlElement private int loopMaximum;
    @XmlElement private int startQuantity;
    @XmlElement private boolean isAutoflowable;
    @XmlElement(name = "MIOrdering") private int miOrdering;
    @XmlElement(name = "MIFlowCondition") private int miFlowCondition;
    @XmlElement private boolean cancelRemainingInstances;
    @XmlElement private int implementationType;
    @XmlElement private boolean isConditional;
    @XmlElement private int bpmnTaskType;
    @XmlElement private String activityOptionType;
    @XmlElement private String activityExecutionType;
    @XmlElement private String activityExecutionTypePreviousValue;
    @XmlElement private boolean isHidden;
    @XmlElement private boolean isRepeatable;
    @XmlElement private int transactionalBehavior;
    @XmlElement private boolean isRobotTask;

    // --- CAMPOS ADICIONADOS PARA EVENTOS ---
    @XmlElement
    private String eventType; // Usado para identificar o tipo de evento (1=Start, 2=End, etc.)
    @XmlElement
    private String eventAction; // Usado para identificar a ação de um evento (ex: UCA)


    // --- Objetos Aninhados ---
    @XmlElement
    private MetricSettings metricSettings;

    @XmlElement
    private Implementation implementation;

    @XmlElement
    private SimulationScenarioConfig simulationScenarioConfig;

    @XmlElement
    private Preconditions preconditions;
    // --- Getters e Setters ---

    public Preconditions getPreconditions() { return preconditions; }
    public void setPreconditions(Preconditions preconditions) { this.preconditions = preconditions; }


    public int getLoopType() { return loopType; }
    public void setLoopType(int loopType) { this.loopType = loopType; }

    public int getLoopMaximum() { return loopMaximum; }
    public void setLoopMaximum(int loopMaximum) { this.loopMaximum = loopMaximum; }

    public int getStartQuantity() { return startQuantity; }
    public void setStartQuantity(int startQuantity) { this.startQuantity = startQuantity; }

    public boolean isAutoflowable() { return isAutoflowable; }
    public void setAutoflowable(boolean autoflowable) { isAutoflowable = autoflowable; }

    public int getMiOrdering() { return miOrdering; }
    public void setMiOrdering(int miOrdering) { this.miOrdering = miOrdering; }

    public int getMiFlowCondition() { return miFlowCondition; }
    public void setMiFlowCondition(int miFlowCondition) { this.miFlowCondition = miFlowCondition; }

    public boolean isCancelRemainingInstances() { return cancelRemainingInstances; }
    public void setCancelRemainingInstances(boolean cancelRemainingInstances) { this.cancelRemainingInstances = cancelRemainingInstances; }

    public int getImplementationType() { return implementationType; }
    public void setImplementationType(int implementationType) { this.implementationType = implementationType; }

    public boolean isConditional() { return isConditional; }
    public void setConditional(boolean conditional) { isConditional = conditional; }

    public int getBpmnTaskType() { return bpmnTaskType; }
    public void setBpmnTaskType(int bpmnTaskType) { this.bpmnTaskType = bpmnTaskType; }

    public String getActivityOptionType() { return activityOptionType; }
    public void setActivityOptionType(String activityOptionType) { this.activityOptionType = activityOptionType; }

    public String getActivityExecutionType() { return activityExecutionType; }
    public void setActivityExecutionType(String activityExecutionType) { this.activityExecutionType = activityExecutionType; }

    public String getActivityExecutionTypePreviousValue() { return activityExecutionTypePreviousValue; }
    public void setActivityExecutionTypePreviousValue(String activityExecutionTypePreviousValue) { this.activityExecutionTypePreviousValue = activityExecutionTypePreviousValue; }

    public boolean isHidden() { return isHidden; }
    public void setHidden(boolean hidden) { isHidden = hidden; }

    public boolean isRepeatable() { return isRepeatable; }
    public void setRepeatable(boolean repeatable) { this.isRepeatable = repeatable; }

    public int getTransactionalBehavior() { return transactionalBehavior; }
    public void setTransactionalBehavior(int transactionalBehavior) { this.transactionalBehavior = transactionalBehavior; }

    public boolean isRobotTask() { return isRobotTask; }
    public void setRobotTask(boolean robotTask) { this.isRobotTask = robotTask; }

    // --- GETTERS E SETTERS ADICIONADOS ---
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getEventAction() { return eventAction; }
    public void setEventAction(String eventAction) { this.eventAction = eventAction; }

    public MetricSettings getMetricSettings() { return metricSettings; }
    public void setMetricSettings(MetricSettings metricSettings) { this.metricSettings = metricSettings; }

    public Implementation getImplementation() { return implementation; }
    public void setImplementation(Implementation implementation) { this.implementation = implementation; }

    public SimulationScenarioConfig getSimulationScenarioConfig() { return simulationScenarioConfig; }
    public void setSimulationScenarioConfig(SimulationScenarioConfig simulationScenarioConfig) { this.simulationScenarioConfig = simulationScenarioConfig; }
}