package br.com.danzeroum.processveritas.web.controller;

import br.com.danzeroum.processveritas.domain.model.RunEntity;
import br.com.danzeroum.processveritas.domain.model.RunEntity.RunStatus;
import br.com.danzeroum.processveritas.domain.model.UserEntity;
import br.com.danzeroum.processveritas.domain.repository.UserRepository;
import br.com.danzeroum.processveritas.service.AuditService;
import br.com.danzeroum.processveritas.service.RunService;
import br.com.danzeroum.processveritas.web.dto.response.RunResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/runs")
public class RunController {

    private static final Logger log = LoggerFactory.getLogger(RunController.class);

    @Autowired
    private RunService runService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditService auditService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createRun(
            @RequestParam("processName") String processName,
            @RequestParam("processId") String processId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {

        if (processName == null || processName.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "processName is required"));
        }
        if (processId == null || processId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "processId is required"));
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "file is required"));
        }

        String email = jwt.getSubject();
        UserEntity author = userRepository.findByEmail(email).orElse(null);

        try {
            RunEntity run = runService.createRun(processName, processId, file, author);
            RunResponse response = RunResponse.from(run);
            auditService.log(
                    author != null ? author.getId() : null,
                    email,
                    "CREATE_RUN",
                    "run:" + run.getId(),
                    Map.of("processName", processName, "processId", processId),
                    httpRequest.getRemoteAddr()
            );
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (IOException e) {
            log.error("Failed to unpack TWX file for processId={}: {}", processId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process uploaded file: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Page<RunResponse>> listRuns(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {

        RunStatus runStatus = null;
        if (status != null) {
            try {
                runStatus = RunStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        Page<RunResponse> page = runService.listRuns(runStatus, pageable)
                .map(RunResponse::from);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RunResponse> getRun(@PathVariable UUID id) {
        RunEntity run = runService.getRunById(id);
        return ResponseEntity.ok(RunResponse.from(run));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRun(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {

        runService.deleteRun(id);
        auditService.log(
                null,
                jwt.getSubject(),
                "DELETE_RUN",
                "run:" + id,
                null,
                httpRequest.getRemoteAddr()
        );
        return ResponseEntity.noContent().build();
    }
}
