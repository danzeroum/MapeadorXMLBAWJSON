package br.com.danzeroum.processveritas.web.dto.response;

import br.com.danzeroum.processveritas.domain.model.PlatformSettingEntity;

import java.time.OffsetDateTime;

public class SettingResponse {

    private String key;
    private String value;
    private OffsetDateTime updatedAt;

    public static SettingResponse from(PlatformSettingEntity e) {
        SettingResponse r = new SettingResponse();
        r.key = e.getKey();
        r.value = e.getValue();
        r.updatedAt = e.getUpdatedAt();
        return r;
    }

    public String getKey() { return key; }
    public String getValue() { return value; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
