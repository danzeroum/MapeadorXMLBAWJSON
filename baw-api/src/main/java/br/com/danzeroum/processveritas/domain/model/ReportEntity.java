package br.com.danzeroum.processveritas.domain.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "reports")
public class ReportEntity {

    @Id
    private UUID runId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "run_id")
    private RunEntity run;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "schema_ver", nullable = false)
    private String schemaVer = "2.0";

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getRunId() { return runId; }
    public RunEntity getRun() { return run; }
    public void setRun(RunEntity run) { this.run = run; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public String getSchemaVer() { return schemaVer; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
