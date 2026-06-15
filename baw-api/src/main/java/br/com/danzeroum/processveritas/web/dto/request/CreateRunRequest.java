package br.com.danzeroum.processveritas.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateRunRequest {

    @NotBlank
    private String processName;

    @NotBlank
    private String processId;

    public String getProcessName() { return processName; }
    public void setProcessName(String processName) { this.processName = processName; }
    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }
}
