package br.com.danzeroum.processveritas.web.dto.response;

import br.com.danzeroum.processveritas.domain.model.RunEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RunResponse {

    private UUID id;
    private String processName;
    private String processId;
    private String status;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private Long durationMs;
    private String errorMessage;
    private OffsetDateTime createdAt;
    private String authorEmail;

    public static RunResponse from(RunEntity run) {
        RunResponse r = new RunResponse();
        r.id = run.getId();
        r.processName = run.getProcessName();
        r.processId = run.getProcessId();
        r.status = run.getStatus().name();
        r.startedAt = run.getStartedAt();
        r.completedAt = run.getCompletedAt();
        r.durationMs = run.getDurationMs();
        r.errorMessage = run.getErrorMessage();
        r.createdAt = run.getCreatedAt();
        r.authorEmail = run.getAuthor() != null ? run.getAuthor().getEmail() : null;
        return r;
    }

    public UUID getId() { return id; }
    public String getProcessName() { return processName; }
    public String getProcessId() { return processId; }
    public String getStatus() { return status; }
    public OffsetDateTime getStartedAt() { return startedAt; }
    public OffsetDateTime getCompletedAt() { return completedAt; }
    public Long getDurationMs() { return durationMs; }
    public String getErrorMessage() { return errorMessage; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public String getAuthorEmail() { return authorEmail; }
}
