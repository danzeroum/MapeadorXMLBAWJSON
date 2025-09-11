package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataObject {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String itemSubjectRef;

    @XmlAttribute
    private boolean isCollection;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getItemSubjectRef() { return itemSubjectRef; }
    public void setItemSubjectRef(String itemSubjectRef) { this.itemSubjectRef = itemSubjectRef; }
    public boolean isCollection() { return isCollection; }
    public void setCollection(boolean collection) { isCollection = collection; }
}