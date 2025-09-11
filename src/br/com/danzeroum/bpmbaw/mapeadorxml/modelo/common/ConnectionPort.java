package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Representa um ponto de conexão (porta) em um link,
 * definido por sua localização visual (locationId) e tipo (portType).
 * Usado em links como StartLink.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionPort {

    @XmlAttribute
    private String locationId;

    @XmlAttribute
    private int portType;

    // --- Getters e Setters ---
    public String getLocationId() { return locationId; }
    public void setLocationId(String locationId) { this.locationId = locationId; }

    public int getPortType() { return portType; }
    public void setPortType(int portType) { this.portType = portType; }
}