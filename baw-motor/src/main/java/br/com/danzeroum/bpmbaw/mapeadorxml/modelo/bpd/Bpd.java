package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.List;

/**
 * Representa uma Definição de Processo de Negócio (BPD) no IBM BAW.
 * Este é um dos elementos raiz em uma exportação .twx, contendo
 * a lógica do processo, parâmetros e o diagrama visual associado.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "bpd")
public class Bpd {

    // --- Atributos ---
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String name;

    // --- Lista de Parâmetros ---
    @XmlElement(name = "bpdParameter")
    private List<BpdParameter> bpdParameters;

    // --- Campos de Configuração ---
    @XmlElement private long lastModified;
    @XmlElement private String lastModifiedBy;
    @XmlElement private String tenantId;
    @XmlElement private String bpdId;
    @XmlElement private boolean isTrackingEnabled;
    @XmlElement private boolean isSpcEnabled;
    @XmlElement private String restrictedName;
    @XmlElement private boolean isCriticalPathEnabled;
    @XmlElement private String participantRef;
    @XmlElement private String businessDataParticipantRef;
    @XmlElement private String perfMetricParticipantRef;
    @XmlElement private String ownerTeamParticipantRef;
    @XmlElement private String timeScheduleType;
    @XmlElement private String timeScheduleName;
    @XmlElement private String timeScheduleExpression;
    @XmlElement private String holidayScheduleType;
    @XmlElement private String holidayScheduleName;
    @XmlElement private String holidayScheduleExpression;
    @XmlElement private String timezoneType;
    @XmlElement private String timezone;
    @XmlElement private String timezoneExpression;
    @XmlElement private String internalName;
    @XmlElement private String description;
    @XmlElement private String type;
    @XmlElement private String rootBpdId;
    @XmlElement private String parentBpdId;
    @XmlElement private String parentFlowObjectId;
    @XmlElement private String xmlData;
    @XmlElement private String bpmn2Data;
    @XmlElement private String dependencySummary;
    @XmlElement private String jsonData;
    @XmlElement private String migrationData;
    @XmlElement private String rwfData;
    @XmlElement private String rwfStatus;
    @XmlElement private String templateId;
    @XmlElement private String externalId;
    @XmlElement private String guid;
    @XmlElement private String versionId;
    @XmlElement private String field1;
    @XmlElement private String field2;
    @XmlElement private String field3;
    @XmlElement private String field4;
    @XmlElement private boolean field5;
    @XmlElement private String clobField1;
    @XmlElement private String blobField1;

    // --- Objeto do Diagrama Aninhado ---
    @XmlElement(name = "BusinessProcessDiagram")
    private BusinessProcessDiagram businessProcessDiagram;


    // --- Getters e Setters (resumidos para brevidade) ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<BpdParameter> getBpdParameters() { return bpdParameters; }
    public void setBpdParameters(List<BpdParameter> bpdParameters) { this.bpdParameters = bpdParameters; }
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    public BusinessProcessDiagram getBusinessProcessDiagram() { return businessProcessDiagram; }
    public void setBusinessProcessDiagram(BusinessProcessDiagram businessProcessDiagram) { this.businessProcessDiagram = businessProcessDiagram; }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getBpdId() {
        return bpdId;
    }

    public void setBpdId(String bpdId) {
        this.bpdId = bpdId;
    }

    public boolean isTrackingEnabled() {
        return isTrackingEnabled;
    }

    public void setTrackingEnabled(boolean trackingEnabled) {
        isTrackingEnabled = trackingEnabled;
    }

    public boolean isSpcEnabled() {
        return isSpcEnabled;
    }

    public void setSpcEnabled(boolean spcEnabled) {
        isSpcEnabled = spcEnabled;
    }

    public String getRestrictedName() {
        return restrictedName;
    }

    public void setRestrictedName(String restrictedName) {
        this.restrictedName = restrictedName;
    }

    public boolean isCriticalPathEnabled() {
        return isCriticalPathEnabled;
    }

    public void setCriticalPathEnabled(boolean criticalPathEnabled) {
        isCriticalPathEnabled = criticalPathEnabled;
    }

    public String getParticipantRef() {
        return participantRef;
    }

    public void setParticipantRef(String participantRef) {
        this.participantRef = participantRef;
    }

    public String getBusinessDataParticipantRef() {
        return businessDataParticipantRef;
    }

    public void setBusinessDataParticipantRef(String businessDataParticipantRef) {
        this.businessDataParticipantRef = businessDataParticipantRef;
    }

    public String getPerfMetricParticipantRef() {
        return perfMetricParticipantRef;
    }

    public void setPerfMetricParticipantRef(String perfMetricParticipantRef) {
        this.perfMetricParticipantRef = perfMetricParticipantRef;
    }

    public String getOwnerTeamParticipantRef() {
        return ownerTeamParticipantRef;
    }

    public void setOwnerTeamParticipantRef(String ownerTeamParticipantRef) {
        this.ownerTeamParticipantRef = ownerTeamParticipantRef;
    }

    public String getTimeScheduleType() {
        return timeScheduleType;
    }

    public void setTimeScheduleType(String timeScheduleType) {
        this.timeScheduleType = timeScheduleType;
    }

    public String getTimeScheduleName() {
        return timeScheduleName;
    }

    public void setTimeScheduleName(String timeScheduleName) {
        this.timeScheduleName = timeScheduleName;
    }

    public String getTimeScheduleExpression() {
        return timeScheduleExpression;
    }

    public void setTimeScheduleExpression(String timeScheduleExpression) {
        this.timeScheduleExpression = timeScheduleExpression;
    }

    public String getHolidayScheduleType() {
        return holidayScheduleType;
    }

    public void setHolidayScheduleType(String holidayScheduleType) {
        this.holidayScheduleType = holidayScheduleType;
    }

    public String getHolidayScheduleName() {
        return holidayScheduleName;
    }

    public void setHolidayScheduleName(String holidayScheduleName) {
        this.holidayScheduleName = holidayScheduleName;
    }

    public String getHolidayScheduleExpression() {
        return holidayScheduleExpression;
    }

    public void setHolidayScheduleExpression(String holidayScheduleExpression) {
        this.holidayScheduleExpression = holidayScheduleExpression;
    }

    public String getTimezoneType() {
        return timezoneType;
    }

    public void setTimezoneType(String timezoneType) {
        this.timezoneType = timezoneType;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTimezoneExpression() {
        return timezoneExpression;
    }

    public void setTimezoneExpression(String timezoneExpression) {
        this.timezoneExpression = timezoneExpression;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRootBpdId() {
        return rootBpdId;
    }

    public void setRootBpdId(String rootBpdId) {
        this.rootBpdId = rootBpdId;
    }

    public String getParentBpdId() {
        return parentBpdId;
    }

    public void setParentBpdId(String parentBpdId) {
        this.parentBpdId = parentBpdId;
    }

    public String getParentFlowObjectId() {
        return parentFlowObjectId;
    }

    public void setParentFlowObjectId(String parentFlowObjectId) {
        this.parentFlowObjectId = parentFlowObjectId;
    }

    public String getXmlData() {
        return xmlData;
    }

    public void setXmlData(String xmlData) {
        this.xmlData = xmlData;
    }

    public String getBpmn2Data() {
        return bpmn2Data;
    }

    public void setBpmn2Data(String bpmn2Data) {
        this.bpmn2Data = bpmn2Data;
    }

    public String getDependencySummary() {
        return dependencySummary;
    }

    public void setDependencySummary(String dependencySummary) {
        this.dependencySummary = dependencySummary;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getMigrationData() {
        return migrationData;
    }

    public void setMigrationData(String migrationData) {
        this.migrationData = migrationData;
    }

    public String getRwfData() {
        return rwfData;
    }

    public void setRwfData(String rwfData) {
        this.rwfData = rwfData;
    }

    public String getRwfStatus() {
        return rwfStatus;
    }

    public void setRwfStatus(String rwfStatus) {
        this.rwfStatus = rwfStatus;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
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

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    public boolean isField5() {
        return field5;
    }

    public void setField5(boolean field5) {
        this.field5 = field5;
    }

    public String getClobField1() {
        return clobField1;
    }

    public void setClobField1(String clobField1) {
        this.clobField1 = clobField1;
    }

    public String getBlobField1() {
        return blobField1;
    }

    public void setBlobField1(String blobField1) {
        this.blobField1 = blobField1;
    }
}