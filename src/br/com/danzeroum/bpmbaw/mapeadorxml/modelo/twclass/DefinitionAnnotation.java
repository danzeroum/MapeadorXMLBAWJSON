package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Representa a anotação de metadados XML para a definição de um Objeto de Negócio.
 * Corresponde à tag <annotation type="com.lombardisoftware.core.xml.XMLTypeAnnotation">.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DefinitionAnnotation {

    @XmlAttribute private String type;
    @XmlAttribute private String version;

    @XmlElement private String exclude;
    @XmlElement private String anonymous;
    @XmlElement private String local;
    @XmlElement private String name;
    @XmlElement private String namespace;
    @XmlElement private String elementName;
    @XmlElement private String elementNamespace;
    @XmlElement private String protoTypeName;
    @XmlElement private String baseTypeName;
    @XmlElement private String specialType;
    @XmlElement private String contentTypeVariety;
    @XmlElement private String xscRef;

    // --- Getters e Setters ---

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getExclude() { return exclude; }
    public void setExclude(String exclude) { this.exclude = exclude; }
    public String getAnonymous() { return anonymous; }
    public void setAnonymous(String anonymous) { this.anonymous = anonymous; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
    public String getElementName() { return elementName; }
    public void setElementName(String elementName) { this.elementName = elementName; }
    public String getElementNamespace() { return elementNamespace; }
    public void setElementNamespace(String elementNamespace) { this.elementNamespace = elementNamespace; }
    public String getProtoTypeName() { return protoTypeName; }
    public void setProtoTypeName(String protoTypeName) { this.protoTypeName = protoTypeName; }
    public String getBaseTypeName() { return baseTypeName; }
    public void setBaseTypeName(String baseTypeName) { this.baseTypeName = baseTypeName; }
    public String getSpecialType() { return specialType; }
    public void setSpecialType(String specialType) { this.specialType = specialType; }
    public String getContentTypeVariety() { return contentTypeVariety; }
    public void setContentTypeVariety(String contentTypeVariety) { this.contentTypeVariety = contentTypeVariety; }
    public String getXscRef() { return xscRef; }
    public void setXscRef(String xscRef) { this.xscRef = xscRef; }
}