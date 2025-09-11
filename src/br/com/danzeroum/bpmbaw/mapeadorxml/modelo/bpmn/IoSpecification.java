package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn;

import javax.xml.bind.annotation.*;
import java.util.List; // Importar a classe List

@XmlAccessorType(XmlAccessType.FIELD)
public class IoSpecification {

    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    // --- CAMPOS ADICIONADOS ---
    @XmlElement(name = "dataInput", namespace = BPMN_NAMESPACE)
    private List<DataInput> dataInputs;

    // (Opcional, mas bom para completude)
    @XmlElement(name = "dataOutput", namespace = BPMN_NAMESPACE)
    private List<DataOutput> dataOutputs;
    // -------------------------

    @XmlElement(name = "inputSet", namespace = BPMN_NAMESPACE)
    private InputSet inputSet;

    @XmlElement(name = "outputSet", namespace = BPMN_NAMESPACE)
    private OutputSet outputSet;

    @XmlElement(name = "extensionElements", namespace = BPMN_NAMESPACE)
    private ExtensionElements extensionElements;


    // Getters e Setters
    public List<DataInput> getDataInputs() {
        return dataInputs;
    }

    public void setDataInputs(List<DataInput> dataInputs) {
        this.dataInputs = dataInputs;
    }

    public List<DataOutput> getDataOutputs() {
        return dataOutputs;
    }

    public void setDataOutputs(List<DataOutput> dataOutputs) {
        this.dataOutputs = dataOutputs;
    }

    public InputSet getInputSet() { return inputSet; }
    public void setInputSet(InputSet inputSet) { this.inputSet = inputSet; }
    public OutputSet getOutputSet() { return outputSet; }
    public void setOutputSet(OutputSet outputSet) { this.outputSet = outputSet; }
    public ExtensionElements getExtensionElements() {
        return extensionElements;
    }

    public void setExtensionElements(ExtensionElements extensionElements) {
        this.extensionElements = extensionElements;
    }

    // Classes internas para Input e Output sets
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class InputSet {
        @XmlAttribute
        private String id;
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class OutputSet {
        @XmlAttribute
        private String id;
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }
}