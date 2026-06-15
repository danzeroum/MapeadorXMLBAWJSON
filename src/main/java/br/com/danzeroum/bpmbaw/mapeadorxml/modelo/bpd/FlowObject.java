package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Representa um objeto de fluxo genÃ©rico no diagrama de processo (BPD),
 * como uma atividade, gateway ou evento. ContÃ©m sua posiÃ§Ã£o, aparÃªncia,
 * comportamento (component) e pontos de conexÃ£o (ports).
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "flowObject")
public class FlowObject {

    // --- Atributos ---
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String componentType;

    // --- Elementos Simples ---
    @XmlElement
    private String name;
    @XmlElement
    private String iconUrl;
    @XmlElement
    private String dropIconUrl;
    @XmlElement
    private String colorInput;

    // --- Elementos Complexos ---
    @XmlElement
    private Position position;

    @XmlElement
    private Component component;

    // --- Portas de Entrada e SaÃ­da ---
    @XmlElement(name = "inputPort")
    private List<InputPort> inputPorts;

    @XmlElement(name = "outputPort")
    private List<OutputPort> outputPorts;

    // --- CAMPO ADICIONADO PARA EVENTOS DE BORDA ---
    // Mapeia as tags <flowObject> aninhadas, que representam os eventos anexados.
    @XmlElement(name = "flowObject")
    private List<FlowObject> attachedEvents;
    @XmlElement(name = "assignment")
    private List<AssignmentBpd> assignments;

    // --- CAMPOS ADICIONADOS PARA RESOLVER OS ERROS DE COMPILAÇÃO ---
    @XmlTransient
    private String sourceId;

    @XmlTransient
    private String targetId;

    // --- Getters e Setters ORIGINAIS ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getComponentType() { return componentType; }
    public void setComponentType(String componentType) { this.componentType = componentType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public String getDropIconUrl() { return dropIconUrl; }
    public void setDropIconUrl(String dropIconUrl) { this.dropIconUrl = dropIconUrl; }

    public String getColorInput() { return colorInput; }
    public void setColorInput(String colorInput) { this.colorInput = colorInput; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public Component getComponent() { return component; }
    public void setComponent(Component component) { this.component = component; }

    public List<InputPort> getInputPorts() { return inputPorts; }
    public void setInputPorts(List<InputPort> inputPorts) { this.inputPorts = inputPorts; }

    public List<OutputPort> getOutputPorts() { return outputPorts; }
    public void setOutputPorts(List<OutputPort> outputPorts) { this.outputPorts = outputPorts; }

    // --- GETTER E SETTER EXISTENTES ---
    public List<FlowObject> getAttachedEvents() { return attachedEvents; }
    public void setAttachedEvents(List<FlowObject> attachedEvents) { this.attachedEvents = attachedEvents; }

    public List<AssignmentBpd> getAssignments() { return assignments; }
    public void setAssignments(List<AssignmentBpd> assignments) { this.assignments = assignments; }

    // --- MÉTODOS NOVOS PARA RESOLVER OS ERROS DE COMPILAÇÃO ---

    /**
     * MÉTODO ADICIONADO: getSource()
     * Resolve o erro: Cannot resolve method 'getSource' in 'FlowObject' :1824
     */
    public String getSource() {
        // Tenta derivar source das portas de entrada primeiro
        if (inputPorts != null && !inputPorts.isEmpty()) {
            for (InputPort port : inputPorts) {
                if (port.getFlow() != null && port.getId() != null) {
                    return port.getId();
                }
            }
        }
        // Fallback para sourceId manualmente definido
        return sourceId;
    }

    /**
     * MÉTODO ADICIONADO: getTarget()
     * Resolve o erro: Cannot resolve method 'getTarget' in 'FlowObject' :1825
     */
    public String getTarget() {
        // Tenta derivar target das portas de saída primeiro
        if (outputPorts != null && !outputPorts.isEmpty()) {
            for (OutputPort port : outputPorts) {
                if (port.getFlow() != null && port.getId() != null) {
                    return port.getId();
                }
            }
        }
        // Fallback para targetId manualmente definido
        return targetId;
    }

    // Setters para definir source/target manualmente se necessário
    public void setSource(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setTarget(String targetId) {
        this.targetId = targetId;
    }

    // Métodos auxiliares para extrair conexões (VERSÃO CORRIGIDA)
    public List<String> getConnectedTargets() {
        List<String> targets = new ArrayList<>();
        if (outputPorts != null) {
            for (OutputPort port : outputPorts) {
                // Como PortFlow não tem getTargetId(), usamos o ID da porta como indicação
                if (port.getId() != null) {
                    targets.add(port.getId());
                }
            }
        }
        return targets;
    }

    public List<String> getConnectedSources() {
        List<String> sources = new ArrayList<>();
        if (inputPorts != null) {
            for (InputPort port : inputPorts) {
                // Como PortFlow não tem getSourceId(), usamos o ID da porta como indicação
                if (port.getId() != null) {
                    sources.add(port.getId());
                }
            }
        }
        return sources;
    }
}