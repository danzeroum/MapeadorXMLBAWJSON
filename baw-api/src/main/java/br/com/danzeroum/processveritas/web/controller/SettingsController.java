package br.com.danzeroum.processveritas.web.controller;

import br.com.danzeroum.processveritas.domain.model.PlatformSettingEntity;
import br.com.danzeroum.processveritas.service.AuditService;
import br.com.danzeroum.processveritas.service.SettingsService;
import br.com.danzeroum.processveritas.web.dto.request.UpdateSettingRequest;
import br.com.danzeroum.processveritas.web.dto.response.SettingResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private AuditService auditService;

    @GetMapping
    public ResponseEntity<List<SettingResponse>> listAll() {
        List<SettingResponse> settings = settingsService.listAll().stream()
                .map(SettingResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(settings);
    }

    @GetMapping("/{key}")
    public ResponseEntity<SettingResponse> getByKey(@PathVariable String key) {
        return ResponseEntity.ok(SettingResponse.from(settingsService.getByKey(key)));
    }

    @PutMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SettingResponse> upsert(
            @PathVariable String key,
            @Valid @RequestBody UpdateSettingRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {

        String userIdStr = jwt.getClaim("userId");
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
        PlatformSettingEntity setting = settingsService.upsert(key, request, userId);
        auditService.log(userId, jwt.getSubject(), "UPDATE_SETTING", "setting:" + key,
                Map.of("key", key), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(SettingResponse.from(setting));
    }
}
