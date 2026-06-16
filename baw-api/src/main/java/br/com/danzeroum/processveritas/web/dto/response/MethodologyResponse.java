package br.com.danzeroum.processveritas.web.dto.response;

import br.com.danzeroum.processveritas.domain.model.MethodologyEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class MethodologyResponse {

    private UUID id;
    private String version;
    private String weights;
    private boolean active;
    private OffsetDateTime createdAt;

    public static MethodologyResponse from(MethodologyEntity e) {
        MethodologyResponse r = new MethodologyResponse();
        r.id = e.getId();
        r.version = e.getVersion();
        r.weights = e.getWeights();
        r.active = e.isActive();
        r.createdAt = e.getCreatedAt();
        return r;
    }

    public UUID getId() { return id; }
    public String getVersion() { return version; }
    public String getWeights() { return weights; }
    public boolean isActive() { return active; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
