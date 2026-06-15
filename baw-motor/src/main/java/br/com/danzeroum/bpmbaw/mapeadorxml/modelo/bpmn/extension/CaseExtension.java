package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class CaseExtension {

    @XmlElement(name = "caseFolder", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process/case")
    private CaseFolder caseFolder;

    // Getter e Setter
    public CaseFolder getCaseFolder() { return caseFolder; }
    public void setCaseFolder(CaseFolder caseFolder) { this.caseFolder = caseFolder; }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CaseFolder {
        @XmlAttribute
        private String id;

        // Getter e Setter
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }
}