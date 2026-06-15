package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model;

import java.util.ArrayList;
import java.util.List;

public class IntegrationPoint {
    private String id;
    private String name;
    private IntegrationType type;
    private String endpoint;
    private String description;
    private List<String> inputParameters = new ArrayList<>();
    private List<String> outputParameters = new ArrayList<>();
    private String source;

    public enum IntegrationType {
        DATABASE, WEB_SERVICE, REST_API, EMAIL, FILE_SYSTEM, MESSAGE_QUEUE, EXTERNAL_SYSTEM
    }

    public IntegrationPoint() {}

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public IntegrationType getType() { return type; }
    public void setType(IntegrationType type) { this.type = type; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getInputParameters() { return inputParameters; }
    public void setInputParameters(List<String> inputParameters) { this.inputParameters = inputParameters; }

    public List<String> getOutputParameters() { return outputParameters; }
    public void setOutputParameters(List<String> outputParameters) { this.outputParameters = outputParameters; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}