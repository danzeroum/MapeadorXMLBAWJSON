package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutData;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.ConnectionPort;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "startLink")
public class StartLink {

    @XmlElement
    private ConnectionPort fromPort; // Usando a nova classe

    @XmlElement
    private ConnectionPort toPort;   // Usando a nova classe

    @XmlElement
    private LayoutData layoutData;

    // --- Getters e Setters ---
    public ConnectionPort getFromPort() { return fromPort; }
    public void setFromPort(ConnectionPort fromPort) { this.fromPort = fromPort; }

    public ConnectionPort getToPort() { return toPort; }
    public void setToPort(ConnectionPort toPort) { this.toPort = toPort; }

    public LayoutData getLayoutData() { return layoutData; }
    public void setLayoutData(LayoutData layoutData) { this.layoutData = layoutData; }
}