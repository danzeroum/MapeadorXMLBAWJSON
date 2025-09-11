package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "coachDefinition", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
public class CoachDefinition {

    @XmlElement(name = "layout", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private Layout layout;

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }
}