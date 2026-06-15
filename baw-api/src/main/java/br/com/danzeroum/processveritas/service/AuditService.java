package br.com.danzeroum.processveritas.service;

import br.com.danzeroum.processveritas.domain.model.AuditLogEntity;
import br.com.danzeroum.processveritas.domain.repository.AuditLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    @Autowired
    private AuditLogRepository auditRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Async
    public void log(UUID actorId, String actorEmail, String action, String resource, Object detail, String ipAddress) {
        AuditLogEntity entry = new AuditLogEntity();
        entry.setActorId(actorId);
        entry.setActorEmail(actorEmail != null ? actorEmail : "unknown");
        entry.setAction(action);
        entry.setResource(resource);
        entry.setIpAddress(ipAddress);

        if (detail != null) {
            try {
                entry.setDetail(objectMapper.writeValueAsString(detail));
            } catch (JsonProcessingException e) {
                log.warn("Could not serialize audit detail for action={}: {}", action, e.getMessage());
                entry.setDetail("{\"error\":\"serialization failed\"}");
            }
        }

        try {
            auditRepo.save(entry);
        } catch (Exception e) {
            log.error("Failed to persist audit log for action={} actor={}: {}", action, actorEmail, e.getMessage());
        }
    }
}
