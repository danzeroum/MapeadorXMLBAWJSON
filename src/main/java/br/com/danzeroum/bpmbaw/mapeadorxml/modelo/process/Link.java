package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.ConnectionPort;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutData;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * Representa um link (conexão ou fluxo de sequência) entre dois itens
 * em um diagrama de processo no IBM BAW.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Link {

    // --- Atributo Original ---
    @XmlAttribute
    private String name;

    // --- Elementos Faltantes Adicionados ---

    @XmlElement
    private String lastModified; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String lastModifiedBy; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String tenantId; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String processId;

    @XmlElement
    private String description; // Usado como String para acomodar isNull="true"

    @XmlElement
    private String endStateId;

    @XmlElement
    private String guid;

    @XmlElement
    private String versionId;

    @XmlElement
    private LayoutData layoutData;

    // Mapeando para a classe ConnectionPort que já criamos
    @XmlElement(name = "fromItemPort")
    private ConnectionPort fromItemPort;

    // Mapeando para a classe ConnectionPort que já criamos
    @XmlElement(name = "toItemPort")
    private ConnectionPort toItemPort;

    // --- Elementos Originais (mantidos) ---
    @XmlElement
    private String processLinkId;

    @XmlElement
    private String fromProcessItemId;

    @XmlElement
    private String toProcessItemId;


    // --- Getters e Setters para todos os campos ---

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastModified() { return lastModified; }
    public void setLastModified(String lastModified) { this.lastModified = lastModified; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEndStateId() { return endStateId; }
    public void setEndStateId(String endStateId) { this.endStateId = endStateId; }

    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }

    public String getVersionId() { return versionId; }
    public void setVersionId(String versionId) { this.versionId = versionId; }

    public LayoutData getLayoutData() { return layoutData; }
    public void setLayoutData(LayoutData layoutData) { this.layoutData = layoutData; }

    public ConnectionPort getFromItemPort() { return fromItemPort; }
    public void setFromItemPort(ConnectionPort fromItemPort) { this.fromItemPort = fromItemPort; }

    public ConnectionPort getToItemPort() { return toItemPort; }
    public void setToItemPort(ConnectionPort toItemPort) { this.toItemPort = toItemPort; }

    public String getProcessLinkId() { return processLinkId; }
    public void setProcessLinkId(String processLinkId) { this.processLinkId = processLinkId; }

    public String getFromProcessItemId() { return fromProcessItemId; }
    public void setFromProcessItemId(String fromProcessItemId) { this.fromProcessItemId = fromProcessItemId; }

    public String getToProcessItemId() { return toProcessItemId; }
    public void setToProcessItemId(String toProcessItemId) { this.toProcessItemId = toProcessItemId; }
}