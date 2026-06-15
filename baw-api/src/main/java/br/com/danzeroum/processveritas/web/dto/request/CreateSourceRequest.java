package br.com.danzeroum.processveritas.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateSourceRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    private String host;
    private String credentials;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public String getCredentials() { return credentials; }
    public void setCredentials(String credentials) { this.credentials = credentials; }
}
