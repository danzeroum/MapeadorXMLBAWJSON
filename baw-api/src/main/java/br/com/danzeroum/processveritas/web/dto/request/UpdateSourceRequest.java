package br.com.danzeroum.processveritas.web.dto.request;

public class UpdateSourceRequest {

    private String name;
    private String host;
    private String credentials;
    private String status;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public String getCredentials() { return credentials; }
    public void setCredentials(String credentials) { this.credentials = credentials; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
