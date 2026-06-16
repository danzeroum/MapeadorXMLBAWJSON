package br.com.danzeroum.processveritas.web.controller;

import br.com.danzeroum.processveritas.domain.model.MethodologyEntity;
import br.com.danzeroum.processveritas.service.AuditService;
import br.com.danzeroum.processveritas.service.MethodologyService;
import br.com.danzeroum.processveritas.web.dto.request.CreateMethodologyRequest;
import br.com.danzeroum.processveritas.web.dto.response.MethodologyResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/methodology")
public class MethodologyController {

    @Autowired
    private MethodologyService methodologyService;

    @Autowired
    private AuditService auditService;

    @GetMapping
    public ResponseEntity<Page<MethodologyResponse>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(methodologyService.list(pageable).map(MethodologyResponse::from));
    }

    @GetMapping("/active")
    public ResponseEntity<MethodologyResponse> getActive() {
        return ResponseEntity.ok(MethodologyResponse.from(methodologyService.getActive()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MethodologyResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(MethodologyResponse.from(methodologyService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MethodologyResponse> create(
            @Valid @RequestBody CreateMethodologyRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {

        String userIdStr = jwt.getClaim("userId");
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
        MethodologyEntity methodology = methodologyService.create(request, userId);
        auditService.log(userId, jwt.getSubject(), "CREATE_METHODOLOGY", "methodology:" + methodology.getId(),
                Map.of("version", methodology.getVersion()), httpRequest.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(MethodologyResponse.from(methodology));
    }
}
