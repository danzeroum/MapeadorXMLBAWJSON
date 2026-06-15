package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutData;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.StartLink;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.StartPoint;

import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * Representa a definição completa de um processo (BPD) ou serviço no IBM BAW.
 * Esta é a classe raiz que contém todos os outros componentes do processo,
 * como variáveis, itens, links e configurações.
 */
@XmlRootElement(name = "process")
@XmlAccessorType(XmlAccessType.FIELD)
public class Process {

    // --- Atributos Originais ---
    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    // --- Elementos Simples (Originais e Adicionados) ---
    @XmlElement private long lastModified;
    @XmlElement private String lastModifiedBy;
    @XmlElement private String tenantId;
    @XmlElement private String processId;
    @XmlElement private String image;
    @XmlElement private String tabGroup;
    @XmlElement private String startingProcessItemId;
    @XmlElement private boolean isRootProcess;
    @XmlElement private int processType;
    @XmlElement private boolean isErrorHandlerEnabled;
    @XmlElement private String errorHandlerItemId;
    @XmlElement private boolean isLoggingVariables;
    @XmlElement private boolean isTransactional;
    @XmlElement private String processTimingLevel;
    @XmlElement private String participantRef;
    @XmlElement private String exposedType;
    @XmlElement private boolean isTrackingEnabled;
    @XmlElement private String xmlData;
    @XmlElement private boolean cachingType;
    @XmlElement private String itemLabel;
    @XmlElement private int cacheLength;
    @XmlElement private boolean mobileReady;
    @XmlElement private boolean sboSyncEnabled;
    @XmlElement private String externalId;
    @XmlElement private boolean isSecured;
    @XmlElement private boolean isAjaxExposed;
    @XmlElement private boolean isInvokedAsynchronously;
    @XmlElement private boolean isTransactionalFlow;
    @XmlElement private String description;
    @XmlElement private String guid;
    @XmlElement private String versionId;
    @XmlElement private String dependencySummary;
    @XmlElement private String jsonData;
    @XmlElement private String field1;
    @XmlElement private String field2;
    @XmlElement private String field3; // String para acomodar "0" e isNull
    @XmlElement private String field4;
    @XmlElement private boolean field5;
    @XmlElement private String clobField1;
    @XmlElement private String blobField1;
    @XmlElement(name = "coachflow") private CoachFlow coachflow;
    // --- Elementos Complexos (Listas) ---
    @XmlElement(name = "processParameter")
    private List<ProcessParameter> processParameters;

    @XmlElement(name = "processVariable")
    private List<ProcessVariable> processVariables;

    @XmlElement(name = "item")
    private List<Item> items;

    @XmlElement(name = "link")
    private List<Link> links;

    // --- Elementos Complexos (Objetos Únicos) ---
    @XmlElement
    private LayoutData layoutData;

    @XmlElement
    private StartPoint startPoint;

    @XmlElement
    private StartLink startLink;


    // --- Getters e Setters para todos os campos ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getTabGroup() { return tabGroup; }
    public void setTabGroup(String tabGroup) { this.tabGroup = tabGroup; }

    public String getStartingProcessItemId() { return startingProcessItemId; }
    public void setStartingProcessItemId(String startingProcessItemId) { this.startingProcessItemId = startingProcessItemId; }

    public boolean isRootProcess() { return isRootProcess; }
    public void setRootProcess(boolean rootProcess) { isRootProcess = rootProcess; }

    public int getProcessType() { return processType; }
    public void setProcessType(int processType) { this.processType = processType; }

    public boolean isErrorHandlerEnabled() { return isErrorHandlerEnabled; }
    public void setErrorHandlerEnabled(boolean errorHandlerEnabled) { isErrorHandlerEnabled = errorHandlerEnabled; }

    public String getErrorHandlerItemId() { return errorHandlerItemId; }
    public void setErrorHandlerItemId(String errorHandlerItemId) { this.errorHandlerItemId = errorHandlerItemId; }

    public boolean isLoggingVariables() { return isLoggingVariables; }
    public void setLoggingVariables(boolean loggingVariables) { isLoggingVariables = loggingVariables; }

    public boolean isTransactional() { return isTransactional; }
    public void setTransactional(boolean transactional) { isTransactional = transactional; }

    public String getProcessTimingLevel() { return processTimingLevel; }
    public void setProcessTimingLevel(String processTimingLevel) { this.processTimingLevel = processTimingLevel; }

    public String getParticipantRef() { return participantRef; }
    public void setParticipantRef(String participantRef) { this.participantRef = participantRef; }

    public String getExposedType() { return exposedType; }
    public void setExposedType(String exposedType) { this.exposedType = exposedType; }

    public boolean isTrackingEnabled() { return isTrackingEnabled; }
    public void setTrackingEnabled(boolean trackingEnabled) { isTrackingEnabled = trackingEnabled; }

    public String getXmlData() { return xmlData; }
    public void setXmlData(String xmlData) { this.xmlData = xmlData; }

    public boolean isCachingType() { return cachingType; }
    public void setCachingType(boolean cachingType) { this.cachingType = cachingType; }

    public String getItemLabel() { return itemLabel; }
    public void setItemLabel(String itemLabel) { this.itemLabel = itemLabel; }

    public int getCacheLength() { return cacheLength; }
    public void setCacheLength(int cacheLength) { this.cacheLength = cacheLength; }

    public boolean isMobileReady() { return mobileReady; }
    public void setMobileReady(boolean mobileReady) { this.mobileReady = mobileReady; }

    public boolean isSboSyncEnabled() { return sboSyncEnabled; }
    public void setSboSyncEnabled(boolean sboSyncEnabled) { this.sboSyncEnabled = sboSyncEnabled; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public boolean isSecured() { return isSecured; }
    public void setSecured(boolean secured) { isSecured = secured; }

    public boolean isAjaxExposed() { return isAjaxExposed; }
    public void setAjaxExposed(boolean ajaxExposed) { isAjaxExposed = ajaxExposed; }

    public boolean isInvokedAsynchronously() { return isInvokedAsynchronously; }
    public void setInvokedAsynchronously(boolean invokedAsynchronously) { this.isInvokedAsynchronously = invokedAsynchronously; }

    public boolean isTransactionalFlow() { return isTransactionalFlow; }
    public void setTransactionalFlow(boolean transactionalFlow) { this.isTransactionalFlow = transactionalFlow; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }

    public String getVersionId() { return versionId; }
    public void setVersionId(String versionId) { this.versionId = versionId; }

    public String getDependencySummary() { return dependencySummary; }
    public void setDependencySummary(String dependencySummary) { this.dependencySummary = dependencySummary; }

    public String getJsonData() { return jsonData; }
    public void setJsonData(String jsonData) { this.jsonData = jsonData; }

    public String getField1() { return field1; }
    public void setField1(String field1) { this.field1 = field1; }

    public String getField2() { return field2; }
    public void setField2(String field2) { this.field2 = field2; }

    public String getField3() { return field3; }
    public void setField3(String field3) { this.field3 = field3; }

    public String getField4() { return field4; }
    public void setField4(String field4) { this.field4 = field4; }

    public boolean getField5() { return field5; }
    public void setField5(boolean field5) { this.field5 = field5; }

    public String getClobField1() { return clobField1; }
    public void setClobField1(String clobField1) { this.clobField1 = clobField1; }

    public String getBlobField1() { return blobField1; }
    public void setBlobField1(String blobField1) { this.blobField1 = blobField1; }

    public List<ProcessParameter> getProcessParameters() { return processParameters; }
    public void setProcessParameters(List<ProcessParameter> processParameters) { this.processParameters = processParameters; }

    public List<ProcessVariable> getProcessVariables() { return processVariables; }
    public void setProcessVariables(List<ProcessVariable> processVariables) { this.processVariables = processVariables; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public List<Link> getLinks() { return links; }
    public void setLinks(List<Link> links) { this.links = links; }

    public LayoutData getLayoutData() { return layoutData; }
    public void setLayoutData(LayoutData layoutData) { this.layoutData = layoutData; }

    public StartPoint getStartPoint() { return startPoint; }
    public void setStartPoint(StartPoint startPoint) { this.startPoint = startPoint; }


    public CoachFlow getCoachflow() {
        return coachflow;
    }

    public void setCoachflow(CoachFlow coachflow) {
        this.coachflow = coachflow;
    }

    public StartLink getStartLink() { return startLink; }
    public void setStartLink(StartLink startLink) { this.startLink = startLink; }
}