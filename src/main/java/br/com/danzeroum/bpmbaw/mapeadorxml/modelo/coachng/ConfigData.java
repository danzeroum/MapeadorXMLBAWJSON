package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "configData", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
@XmlType(name = "CoachNGConfigData") // <-- Adicione esta linha para dar um nome único
public class ConfigData {
    @XmlElement(name = "id", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private String id;

    @XmlElement(name = "optionName", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private String optionName;

    @XmlElement(name = "value", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private String value;

    @XmlElement(name = "valueType", namespace = "http://www.ibm.com/bpm/CoachDesignerNG")
    private String valueType;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOptionName() { return optionName; }
    public void setOptionName(String optionName) { this.optionName = optionName; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getValueType() { return valueType; }
    public void setValueType(String valueType) { this.valueType = valueType; }
}