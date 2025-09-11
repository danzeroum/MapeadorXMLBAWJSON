package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Representa uma única linha de configuração (<configData>) para um LayoutItem no Coach.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LegacyCoachConfigData") // <-- Adicione esta linha para dar um nome único
public class ConfigData {


    private static final String NAMESPACE = "http://www.ibm.com/bpm/CoachDesignerNG";

    @XmlElement private String id; // <-- CAMPO ADICIONADO
    @XmlElement(namespace = NAMESPACE)
    private String optionName;

    @XmlElement(namespace = NAMESPACE)
    private String value;

    @XmlElement(namespace = NAMESPACE)
    private String valueType;


    // Getters e Setters
    public String getId() { return id; } // <-- GETTER ADICIONADO
    public String getOptionName() { return optionName; }
    public String getValue() { return value; }
    public String getValueType() { return valueType; }
}