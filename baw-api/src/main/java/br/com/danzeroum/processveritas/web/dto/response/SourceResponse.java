package br.com.danzeroum.processveritas.web.dto.response;

import br.com.danzeroum.processveritas.domain.model.SourceEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class SourceResponse {

    private UUID id;
    private String name;
    private String type;
    private String host;
    private String status;
    private OffsetDateTime createdAt;

    public static SourceResponse from(SourceEntity e) {
        SourceResponse r = new SourceResponse();
        r.id = e.getId();
        r.name = e.getName();
        r.type = e.getType().name();
        r.host = e.getHost();
        r.status = e.getStatus();
        r.createdAt = e.getCreatedAt();
        return r;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getHost() { return host; }
    public String getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
