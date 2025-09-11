package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Representa uma propriedade (parâmetro) dentro da definição de um Objeto de Negócio (twClass).
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {

    @XmlElement private String name;
    @XmlElement private String description;
    @XmlElement private String classRef;
    @XmlElement private boolean arrayProperty;
    @XmlElement private String propertyDefault;
    @XmlElement private boolean propertyRequired;
    @XmlElement private boolean propertyHidden;
    @XmlElement private Annotation annotation;

    /**
     * Classe interna que representa a anotação de metadados XML para uma propriedade.
     * Corresponde à tag <annotation type="com.lombardisoftware.core.xml.XMLFieldAnnotation">.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Annotation {

        @XmlAttribute private String type;
        @XmlAttribute private String version;

        @XmlElement private String exclude;
        @XmlElement private String nodeType;
        @XmlElement private String name;
        @XmlElement private String namespace;
        @XmlElement private String typeName;
        @XmlElement private String typeNamespace;
        @XmlElement private String minOccurs;
        @XmlElement private String maxOccurs;
        @XmlElement private String nillable;
        @XmlElement private String order;
        @XmlElement private String wrapArray;
        @XmlElement private String arrayTypeName;
        @XmlElement private String arrayTypeAnonymous;
        @XmlElement private String arrayItemName;
        @XmlElement private String arrayItemWildcard;
        @XmlElement private String wildcard;
        @XmlElement private String wildcardVariety;
        @XmlElement private String wildcardMode;
        @XmlElement private String wildcardNamespace;
        @XmlElement private String parentModelGroupCompositor;
        @XmlElement private String timeZone;

        // --- Getters e Setters para Annotation ---
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getExclude() { return exclude; }
        public void setExclude(String exclude) { this.exclude = exclude; }
        public String getNodeType() { return nodeType; }
        public void setNodeType(String nodeType) { this.nodeType = nodeType; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getNamespace() { return namespace; }
        public void setNamespace(String namespace) { this.namespace = namespace; }
        public String getTypeName() { return typeName; }
        public void setTypeName(String typeName) { this.typeName = typeName; }
        public String getTypeNamespace() { return typeNamespace; }
        public void setTypeNamespace(String typeNamespace) { this.typeNamespace = typeNamespace; }
        public String getMinOccurs() { return minOccurs; }
        public void setMinOccurs(String minOccurs) { this.minOccurs = minOccurs; }
        public String getMaxOccurs() { return maxOccurs; }
        public void setMaxOccurs(String maxOccurs) { this.maxOccurs = maxOccurs; }
        public String getNillable() { return nillable; }
        public void setNillable(String nillable) { this.nillable = nillable; }
        public String getOrder() { return order; }
        public void setOrder(String order) { this.order = order; }
        public String getWrapArray() { return wrapArray; }
        public void setWrapArray(String wrapArray) { this.wrapArray = wrapArray; }
        public String getArrayTypeName() { return arrayTypeName; }
        public void setArrayTypeName(String arrayTypeName) { this.arrayTypeName = arrayTypeName; }
        public String getArrayTypeAnonymous() { return arrayTypeAnonymous; }
        public void setArrayTypeAnonymous(String arrayTypeAnonymous) { this.arrayTypeAnonymous = arrayTypeAnonymous; }
        public String getArrayItemName() { return arrayItemName; }
        public void setArrayItemName(String arrayItemName) { this.arrayItemName = arrayItemName; }
        public String getArrayItemWildcard() { return arrayItemWildcard; }
        public void setArrayItemWildcard(String arrayItemWildcard) { this.arrayItemWildcard = arrayItemWildcard; }
        public String getWildcard() { return wildcard; }
        public void setWildcard(String wildcard) { this.wildcard = wildcard; }
        public String getWildcardVariety() { return wildcardVariety; }
        public void setWildcardVariety(String wildcardVariety) { this.wildcardVariety = wildcardVariety; }
        public String getWildcardMode() { return wildcardMode; }
        public void setWildcardMode(String wildcardMode) { this.wildcardMode = wildcardMode; }
        public String getWildcardNamespace() { return wildcardNamespace; }
        public void setWildcardNamespace(String wildcardNamespace) { this.wildcardNamespace = wildcardNamespace; }
        public String getParentModelGroupCompositor() { return parentModelGroupCompositor; }
        public void setParentModelGroupCompositor(String parentModelGroupCompositor) { this.parentModelGroupCompositor = parentModelGroupCompositor; }
        public String getTimeZone() { return timeZone; }
        public void setTimeZone(String timeZone) { this.timeZone = timeZone; }
    }

    // --- Getters e Setters para Property ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getClassRef() { return classRef; }
    public void setClassRef(String classRef) { this.classRef = classRef; }
    public boolean isArrayProperty() { return arrayProperty; }
    public void setArrayProperty(boolean arrayProperty) { this.arrayProperty = arrayProperty; }
    public String getPropertyDefault() { return propertyDefault; }
    public void setPropertyDefault(String propertyDefault) { this.propertyDefault = propertyDefault; }
    public boolean isPropertyRequired() { return propertyRequired; }
    public void setPropertyRequired(boolean propertyRequired) { this.propertyRequired = propertyRequired; }
    public boolean isPropertyHidden() { return propertyHidden; }
    public void setPropertyHidden(boolean propertyHidden) { this.propertyHidden = propertyHidden; }
    public Annotation getAnnotation() { return annotation; }
    public void setAnnotation(Annotation annotation) { this.annotation = annotation; }
}