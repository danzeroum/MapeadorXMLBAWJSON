package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * Representa a raiz de uma Definição de Processo de Negócio (BPD) completa.
 * É o elemento de mais alto nível que contém toda a configuração,
 * estrutura visual, lógica e metadados de um processo.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "BusinessProcessDiagram")
public class BusinessProcessDiagram {

    @XmlAttribute
    private String id;

    // --- Elementos Simples e de Configuração ---
    @XmlElement private String name;
    @XmlElement private String documentation;
    @XmlElement private String author;
    @XmlElement private boolean isTrackingEnabled;
    @XmlElement private boolean isCriticalPathEnabled;
    @XmlElement private boolean isSpcEnabled;
    @XmlElement private boolean isDueDateEnabled;
    @XmlElement private boolean isAtRiskCalcEnabled;
    @XmlElement private long creationDate;
    @XmlElement private long modificationDate;
    @XmlElement private String participantRef;
    @XmlElement private String instanceNameExpression;
    @XmlElement private int dueDateType;
    @XmlElement private int dueDateTime;
    @XmlElement private int dueDateTimeResolution;
    @XmlElement private String dueDateTimeTOD;
    @XmlElement private String dueDateCustom;
    @XmlElement private int timeScheduleType;
    @XmlElement private int holidayScheduleType;
    @XmlElement private int timezoneType;
    @XmlElement private String executionProfile;
    @XmlElement(name = "isSBOSyncEnabled") private boolean isSboSyncEnabled;
    @XmlElement private boolean allowContentOperations;
    @XmlElement private boolean isLegacyCaseMigrated;
    @XmlElement private boolean hasCaseObjectParams;

    // --- Elementos Complexos ---
    @XmlElement private Metadata metadata;
    @XmlElement private Dimension dimension;
    @XmlElement private MetricSettings metricSettings;
    @XmlElement private CurrentSimulationScenarioId currentSimulationScenarioId;
    @XmlElement private OfficeIntegration officeIntegration;
    @XmlElement private DefaultPool defaultPool;
    @XmlElement private DefaultInstanceUI defaultInstanceUI;
    @XmlElement private OwnerTeamInstanceUI ownerTeamInstanceUI;

    // --- Listas de Elementos Complexos ---
    @XmlElement(name = "simulationScenario")
    private List<SimulationScenario> simulationScenarios;

    @XmlElement(name = "flow")
    private List<Flow> flows;

    @XmlElement(name = "pool")
    private List<Pool> pools;
    @XmlElement(name = "note")
    private List<Note> notes;

    // --- Getters e Setters (resumidos para brevidade) ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDocumentation() { return documentation; }
    public void setDocumentation(String documentation) { this.documentation = documentation; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public boolean isTrackingEnabled() { return isTrackingEnabled; }
    public void setTrackingEnabled(boolean trackingEnabled) { isTrackingEnabled = trackingEnabled; }
    public long getCreationDate() { return creationDate; }
    public void setCreationDate(long creationDate) { this.creationDate = creationDate; }
    public long getModificationDate() { return modificationDate; }
    public void setModificationDate(long modificationDate) { this.modificationDate = modificationDate; }
    public Metadata getMetadata() { return metadata; }
    public void setMetadata(Metadata metadata) { this.metadata = metadata; }
    public Dimension getDimension() { return dimension; }
    public void setDimension(Dimension dimension) { this.dimension = dimension; }
    public DefaultPool getDefaultPool() { return defaultPool; }
    public void setDefaultPool(DefaultPool defaultPool) { this.defaultPool = defaultPool; }
    public List<Flow> getFlows() { return flows; }
    public void setFlows(List<Flow> flows) { this.flows = flows; }
    public List<Pool> getPools() { return pools; }
    public void setPools(List<Pool> pools) { this.pools = pools; }

    // ... (Getters e Setters para todos os outros campos)

    public boolean isCriticalPathEnabled() {
        return isCriticalPathEnabled;
    }

    public void setCriticalPathEnabled(boolean criticalPathEnabled) {
        isCriticalPathEnabled = criticalPathEnabled;
    }

    public boolean isSpcEnabled() {
        return isSpcEnabled;
    }

    public void setSpcEnabled(boolean spcEnabled) {
        isSpcEnabled = spcEnabled;
    }

    public boolean isDueDateEnabled() {
        return isDueDateEnabled;
    }

    public void setDueDateEnabled(boolean dueDateEnabled) {
        isDueDateEnabled = dueDateEnabled;
    }

    public boolean isAtRiskCalcEnabled() {
        return isAtRiskCalcEnabled;
    }

    public void setAtRiskCalcEnabled(boolean atRiskCalcEnabled) {
        isAtRiskCalcEnabled = atRiskCalcEnabled;
    }

    public String getParticipantRef() {
        return participantRef;
    }

    public void setParticipantRef(String participantRef) {
        this.participantRef = participantRef;
    }

    public String getInstanceNameExpression() {
        return instanceNameExpression;
    }

    public void setInstanceNameExpression(String instanceNameExpression) {
        this.instanceNameExpression = instanceNameExpression;
    }

    public int getDueDateType() {
        return dueDateType;
    }

    public void setDueDateType(int dueDateType) {
        this.dueDateType = dueDateType;
    }

    public int getDueDateTime() {
        return dueDateTime;
    }

    public void setDueDateTime(int dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    public int getDueDateTimeResolution() {
        return dueDateTimeResolution;
    }

    public void setDueDateTimeResolution(int dueDateTimeResolution) {
        this.dueDateTimeResolution = dueDateTimeResolution;
    }

    public String getDueDateTimeTOD() {
        return dueDateTimeTOD;
    }

    public void setDueDateTimeTOD(String dueDateTimeTOD) {
        this.dueDateTimeTOD = dueDateTimeTOD;
    }

    public String getDueDateCustom() {
        return dueDateCustom;
    }

    public void setDueDateCustom(String dueDateCustom) {
        this.dueDateCustom = dueDateCustom;
    }

    public int getTimeScheduleType() {
        return timeScheduleType;
    }

    public void setTimeScheduleType(int timeScheduleType) {
        this.timeScheduleType = timeScheduleType;
    }

    public int getHolidayScheduleType() {
        return holidayScheduleType;
    }

    public void setHolidayScheduleType(int holidayScheduleType) {
        this.holidayScheduleType = holidayScheduleType;
    }

    public int getTimezoneType() {
        return timezoneType;
    }

    public void setTimezoneType(int timezoneType) {
        this.timezoneType = timezoneType;
    }

    public String getExecutionProfile() {
        return executionProfile;
    }

    public void setExecutionProfile(String executionProfile) {
        this.executionProfile = executionProfile;
    }

    public boolean isSboSyncEnabled() {
        return isSboSyncEnabled;
    }

    public void setSboSyncEnabled(boolean sboSyncEnabled) {
        isSboSyncEnabled = sboSyncEnabled;
    }

    public boolean isAllowContentOperations() {
        return allowContentOperations;
    }

    public void setAllowContentOperations(boolean allowContentOperations) {
        this.allowContentOperations = allowContentOperations;
    }

    public boolean isLegacyCaseMigrated() {
        return isLegacyCaseMigrated;
    }

    public void setLegacyCaseMigrated(boolean legacyCaseMigrated) {
        isLegacyCaseMigrated = legacyCaseMigrated;
    }

    public boolean isHasCaseObjectParams() {
        return hasCaseObjectParams;
    }

    public void setHasCaseObjectParams(boolean hasCaseObjectParams) {
        this.hasCaseObjectParams = hasCaseObjectParams;
    }

    public MetricSettings getMetricSettings() {
        return metricSettings;
    }

    public void setMetricSettings(MetricSettings metricSettings) {
        this.metricSettings = metricSettings;
    }

    public CurrentSimulationScenarioId getCurrentSimulationScenarioId() {
        return currentSimulationScenarioId;
    }

    public void setCurrentSimulationScenarioId(CurrentSimulationScenarioId currentSimulationScenarioId) {
        this.currentSimulationScenarioId = currentSimulationScenarioId;
    }

    public OfficeIntegration getOfficeIntegration() {
        return officeIntegration;
    }

    public void setOfficeIntegration(OfficeIntegration officeIntegration) {
        this.officeIntegration = officeIntegration;
    }

    public DefaultInstanceUI getDefaultInstanceUI() {
        return defaultInstanceUI;
    }

    public void setDefaultInstanceUI(DefaultInstanceUI defaultInstanceUI) {
        this.defaultInstanceUI = defaultInstanceUI;
    }

    public OwnerTeamInstanceUI getOwnerTeamInstanceUI() {
        return ownerTeamInstanceUI;
    }

    public void setOwnerTeamInstanceUI(OwnerTeamInstanceUI ownerTeamInstanceUI) {
        this.ownerTeamInstanceUI = ownerTeamInstanceUI;
    }

    public List<SimulationScenario> getSimulationScenarios() {
        return simulationScenarios;
    }

    public void setSimulationScenarios(List<SimulationScenario> simulationScenarios) {
        this.simulationScenarios = simulationScenarios;
    }
    public List<Note> getNotes() { return notes; }
    public void setNotes(List<Note> notes) { this.notes = notes; }
}