package br.com.danzeroum.processveritas.web.controller;

import br.com.danzeroum.processveritas.domain.model.AuditLogEntity;
import br.com.danzeroum.processveritas.domain.repository.AuditLogRepository;
import br.com.danzeroum.processveritas.web.dto.response.AuditLogResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit-log")
public class AuditLogController {

    @Autowired
    private AuditLogRepository auditRepo;

    @GetMapping
    public ResponseEntity<Page<AuditLogResponse>> list(
            @RequestParam(required = false) String actor,
            @PageableDefault(size = 20, sort = "occurredAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AuditLogEntity> page;
        if (actor != null && !actor.isBlank()) {
            page = auditRepo.findByActorEmailContainingIgnoreCase(actor, pageable);
        } else {
            page = auditRepo.findAll(pageable);
        }

        return ResponseEntity.ok(page.map(AuditLogResponse::from));
    }
}
