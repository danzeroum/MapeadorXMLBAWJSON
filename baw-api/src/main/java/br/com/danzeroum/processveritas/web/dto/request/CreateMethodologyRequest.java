package br.com.danzeroum.processveritas.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateMethodologyRequest {

    @NotBlank
    private String version;

    @NotBlank
    private String weights;

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getWeights() { return weights; }
    public void setWeights(String weights) { this.weights = weights; }
}
