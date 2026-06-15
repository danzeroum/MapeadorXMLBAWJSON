package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutData;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Item {
    @XmlElement private String processItemId;
    @XmlElement private String name;
    @XmlElement private String tWComponentName;
    @XmlElement(name = "TWComponent") private TWComponent twComponent;
    @XmlElement
    private LayoutData layoutData;
    @XmlElement
    private String lastModified; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String lastModifiedBy; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String tenantId; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String processId;

    @XmlElement
    private String tWComponentId;

    @XmlElement
    private boolean isLogEnabled;

    @XmlElement
    private boolean isTraceEnabled;

    @XmlElement
    private String traceCategory; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String traceLevel; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String traceMessage; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String traceSymbolTable; // Usado como String para acomodar isNull="true"

    @XmlElement
    private boolean isExecutionContextTraced;

    @XmlElement
    private boolean saveExecutionContext;

    @XmlElement
    private String documentation; // Usado como String para acomodar isNull="true"

    @XmlElement
    private boolean isErrorHandlerEnabled;

    @XmlElement
    private String errorHandlerItemId; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String guid;

    @XmlElement
    private String versionId;

    @XmlElement
    private String externalServiceRef; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String externalServiceOp; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String nodeColor; // Usado como String para acomodar isNull="true"

    @XmlElement(name = "processPrePosts")
    private List<ProcessPrePost> processPrePosts;

    public String getLastModified() { return lastModified; }
    public void setLastModified(String lastModified) { this.lastModified = lastModified; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }

    public String getTWComponentId() { return tWComponentId; }
    public void setTWComponentId(String tWComponentId) { this.tWComponentId = tWComponentId; }

    public boolean isLogEnabled() { return isLogEnabled; }
    public void setLogEnabled(boolean logEnabled) { isLogEnabled = logEnabled; }

    public boolean isTraceEnabled() { return isTraceEnabled; }
    public void setTraceEnabled(boolean traceEnabled) { isTraceEnabled = traceEnabled; }

    public String getTraceCategory() { return traceCategory; }
    public void setTraceCategory(String traceCategory) { this.traceCategory = traceCategory; }

    public String getTraceLevel() { return traceLevel; }
    public void setTraceLevel(String traceLevel) { this.traceLevel = traceLevel; }

    public String getTraceMessage() { return traceMessage; }
    public void setTraceMessage(String traceMessage) { this.traceMessage = traceMessage; }

    public String getTraceSymbolTable() { return traceSymbolTable; }
    public void setTraceSymbolTable(String traceSymbolTable) { this.traceSymbolTable = traceSymbolTable; }

    public boolean isExecutionContextTraced() { return isExecutionContextTraced; }
    public void setExecutionContextTraced(boolean executionContextTraced) { isExecutionContextTraced = executionContextTraced; }

    public boolean isSaveExecutionContext() { return saveExecutionContext; }
    public void setSaveExecutionContext(boolean saveExecutionContext) { this.saveExecutionContext = saveExecutionContext; }

    public String getDocumentation() { return documentation; }
    public void setDocumentation(String documentation) { this.documentation = documentation; }

    public boolean isErrorHandlerEnabled() { return isErrorHandlerEnabled; }
    public void setErrorHandlerEnabled(boolean errorHandlerEnabled) { isErrorHandlerEnabled = errorHandlerEnabled; }

    public String getErrorHandlerItemId() { return errorHandlerItemId; }
    public void setErrorHandlerItemId(String errorHandlerItemId) { this.errorHandlerItemId = errorHandlerItemId; }

    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }

    public String getVersionId() { return versionId; }
    public void setVersionId(String versionId) { this.versionId = versionId; }

    public String getExternalServiceRef() { return externalServiceRef; }
    public void setExternalServiceRef(String externalServiceRef) { this.externalServiceRef = externalServiceRef; }

    public String getExternalServiceOp() { return externalServiceOp; }
    public void setExternalServiceOp(String externalServiceOp) { this.externalServiceOp = externalServiceOp; }

    public String getNodeColor() { return nodeColor; }
    public void setNodeColor(String nodeColor) { this.nodeColor = nodeColor; }

    public String getProcessItemId() { return processItemId; }
    public void setProcessItemId(String processItemId) { this.processItemId = processItemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTWComponentName() { return tWComponentName; }
    public void setTWComponentName(String tWComponentName) { this.tWComponentName = tWComponentName; }

    public TWComponent getTwComponent() { return twComponent; }
    public void setTwComponent(TWComponent twComponent) { this.twComponent = twComponent; }

    public LayoutData getLayoutData() { return layoutData; }
    public void setLayoutData(LayoutData layoutData) { this.layoutData = layoutData; }

    public List<ProcessPrePost> getProcessPrePosts() {
        return processPrePosts;
    }

    public void setProcessPrePosts(List<ProcessPrePost> processPrePosts) {
        this.processPrePosts = processPrePosts;
    }
}