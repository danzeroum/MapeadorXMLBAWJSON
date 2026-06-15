// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/modelo/coachng/CoachDefinition.java
package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.CoachEventBinding; // Import necessário
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List; // Import necessário

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "coachDefinition", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
public class CoachDefinition {

    @XmlElement(name = "layout", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private Layout layout;

    // --- CAMPO ADICIONADO ---
    @XmlElement(name = "coachEventBinding", namespace = "http://www.ibm.com/xmlns/prod/bpm/bpmn/ext/process")
    private List<CoachEventBinding> eventBindings;


    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    // --- MÉTODO ADICIONADO ---
    public List<CoachEventBinding> getEventBindings() {
        return eventBindings;
    }

    public void setEventBindings(List<CoachEventBinding> eventBindings) {
        this.eventBindings = eventBindings;
    }
}