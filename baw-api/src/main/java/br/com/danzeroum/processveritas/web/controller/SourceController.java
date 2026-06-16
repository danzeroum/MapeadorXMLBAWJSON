package br.com.danzeroum.processveritas.web.controller;

import br.com.danzeroum.processveritas.domain.model.SourceEntity;
import br.com.danzeroum.processveritas.service.AuditService;
import br.com.danzeroum.processveritas.service.SourceService;
import br.com.danzeroum.processveritas.web.dto.request.CreateSourceRequest;
import br.com.danzeroum.processveritas.web.dto.request.UpdateSourceRequest;
import br.com.danzeroum.processveritas.web.dto.response.SourceResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sources")
public class SourceController {

    @Autowired
    private SourceService sourceService;

    @Autowired
    private AuditService auditService;

    @GetMapping
    public ResponseEntity<Page<SourceResponse>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(sourceService.list(pageable).map(SourceResponse::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SourceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(SourceResponse.from(sourceService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SourceResponse> create(
            @Valid @RequestBody CreateSourceRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {

        String userIdStr = jwt.getClaim("userId");
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
        SourceEntity source = sourceService.create(request, userId);
        auditService.log(userId, jwt.getSubject(), "CREATE_SOURCE", "source:" + source.getId(),
                Map.of("name", source.getName(), "type", source.getType().name()),
                httpRequest.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(SourceResponse.from(source));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SourceResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateSourceRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {

        String userIdStr = jwt.getClaim("userId");
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
        SourceEntity source = sourceService.update(id, request);
        auditService.log(userId, jwt.getSubject(), "UPDATE_SOURCE", "source:" + id,
                request, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(SourceResponse.from(source));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {

        String userIdStr = jwt.getClaim("userId");
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
        sourceService.deactivate(id);
        auditService.log(userId, jwt.getSubject(), "DEACTIVATE_SOURCE", "source:" + id,
                null, httpRequest.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }
}
