package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout; // <-- 1. IMPORT ADICIONADO

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "globalUserTask", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class GlobalUserTask {

    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    private static final String COACHFLOW_NAMESPACE = "http://www.ibm.com/bpm/coachflow";

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlElement(name = "documentation", namespace = BPMN_NAMESPACE)
    private Documentation documentation;

    @XmlElement(name = "extensionElements", namespace = BPMN_NAMESPACE)
    private ExtensionElements extensionElements;

    @XmlElement(name = "ioSpecification", namespace = BPMN_NAMESPACE)
    private IoSpecification ioSpecification;

    // <-- 2. CAMPO 'coach' E ANOTAÇÃO ADICIONADOS
    @XmlElement(name = "coach", namespace = COACHFLOW_NAMESPACE)
    private CoachLayout coach;


    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Documentation getDocumentation() {
        return documentation;
    }

    public void setDocumentation(Documentation documentation) {
        this.documentation = documentation;
    }

    public IoSpecification getIoSpecification() {
        return ioSpecification;
    }

    public void setIoSpecification(IoSpecification ioSpecification) {
        this.ioSpecification = ioSpecification;
    }

    public UserTaskImplementation getImplementation() {
        if (extensionElements != null) {
            return extensionElements.getUserTaskImplementation();
        }
        return null;
    }

    // Este método agora irá compilar corretamente
    public CoachLayout getCoach() {
        return coach;
    }
}