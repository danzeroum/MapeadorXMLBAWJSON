package br.com.danzeroum.processveritas.domain.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "actor_email", nullable = false)
    private String actorEmail;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "resource", nullable = false)
    private String resource;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detail", columnDefinition = "jsonb")
    private String detail;

    @Column(name = "ip_address", columnDefinition = "inet")
    private String ipAddress;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private OffsetDateTime occurredAt = OffsetDateTime.now();

    public Long getId() { return id; }

    public UUID getActorId() { return actorId; }
    public void setActorId(UUID actorId) { this.actorId = actorId; }

    public String getActorEmail() { return actorEmail; }
    public void setActorEmail(String actorEmail) { this.actorEmail = actorEmail; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public OffsetDateTime getOccurredAt() { return occurredAt; }
}
