package br.com.danzeroum.processveritas.web.dto.request;

import jakarta.validation.constraints.NotNull;

public class UpdateSettingRequest {

    @NotNull
    private String value;

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
