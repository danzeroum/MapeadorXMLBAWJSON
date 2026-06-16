package br.com.danzeroum.processveritas.web.controller;

import br.com.danzeroum.processveritas.service.UsageService;
import br.com.danzeroum.processveritas.web.dto.response.UsageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usage")
public class UsageController {

    @Autowired
    private UsageService usageService;

    @GetMapping
    public ResponseEntity<List<UsageResponse>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID userId,
            @AuthenticationPrincipal Jwt jwt) {

        List<UsageResponse> results;

        if (userId != null) {
            results = usageService.listForUser(userId).stream()
                    .map(UsageResponse::from)
                    .collect(Collectors.toList());
        } else {
            LocalDate effectiveFrom = from != null ? from : LocalDate.now().minusMonths(12).withDayOfMonth(1);
            LocalDate effectiveTo = to != null ? to : LocalDate.now();
            results = usageService.listByPeriod(effectiveFrom, effectiveTo).stream()
                    .map(UsageResponse::from)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(results);
    }

    @GetMapping("/me")
    public ResponseEntity<List<UsageResponse>> myUsage(@AuthenticationPrincipal Jwt jwt) {
        String userIdStr = jwt.getClaim("userId");
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
        if (userId == null) return ResponseEntity.ok(List.of());
        List<UsageResponse> results = usageService.listForUser(userId).stream()
                .map(UsageResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }
}
