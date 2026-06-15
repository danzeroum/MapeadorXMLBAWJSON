@XmlSchema(
        namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL",
        elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns = {
                @XmlNs(prefix = "bpmn2", namespaceURI = "http://www.omg.org/spec/BPMN/20100524/MODEL"),
                @XmlNs(prefix = "bpmndi", namespaceURI = "http://www.omg.org/spec/BPMN/20100524/DI"),
                @XmlNs(prefix = "dc", namespaceURI = "http://www.omg.org/spec/DD/20100524/DC"),
                @XmlNs(prefix = "di", namespaceURI = "http://www.omg.org/spec/DD/20100524/DI"),
                @XmlNs(prefix = "tw", namespaceURI = "http://www.ibm.com/bpm/coachdesignerng") // Namespace customizado da IBM
        }
)

package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;