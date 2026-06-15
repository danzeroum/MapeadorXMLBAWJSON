package br.com.danzeroum.processveritas.web.dto.response;

import br.com.danzeroum.processveritas.domain.model.AuditLogEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AuditLogResponse {

    private Long id;
    private UUID actorId;
    private String actorEmail;
    private String action;
    private String resource;
    private String detail;
    private String ipAddress;
    private OffsetDateTime occurredAt;

    public static AuditLogResponse from(AuditLogEntity e) {
        AuditLogResponse r = new AuditLogResponse();
        r.id = e.getId();
        r.actorId = e.getActorId();
        r.actorEmail = e.getActorEmail();
        r.action = e.getAction();
        r.resource = e.getResource();
        r.detail = e.getDetail();
        r.ipAddress = e.getIpAddress();
        r.occurredAt = e.getOccurredAt();
        return r;
    }

    public Long getId() { return id; }
    public UUID getActorId() { return actorId; }
    public String getActorEmail() { return actorEmail; }
    public String getAction() { return action; }
    public String getResource() { return resource; }
    public String getDetail() { return detail; }
    public String getIpAddress() { return ipAddress; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
}
