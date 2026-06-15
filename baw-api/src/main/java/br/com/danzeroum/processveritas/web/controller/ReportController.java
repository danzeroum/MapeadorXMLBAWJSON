package br.com.danzeroum.processveritas.web.controller;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2.EnhancedStructuredProcessReportV2;
import br.com.danzeroum.processveritas.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/runs/{id}/report")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<EnhancedStructuredProcessReportV2> getFullReport(@PathVariable UUID id) {
        try {
            EnhancedStructuredProcessReportV2 report = reportService.getFullReport(id);
            return ResponseEntity.ok(report);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving full report for runId={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve report", e);
        }
    }

    @GetMapping("/ai-score")
    public ResponseEntity<String> getAiScore(@PathVariable UUID id) {
        return getSection(id, "aiReadinessScore");
    }

    @GetMapping("/graph")
    public ResponseEntity<String> getGraph(@PathVariable UUID id) {
        return getSection(id, "processGraph");
    }

    @GetMapping("/data-types")
    public ResponseEntity<String> getDataTypes(@PathVariable UUID id) {
        return getSection(id, "dataTypes");
    }

    @GetMapping("/logic")
    public ResponseEntity<String> getLogic(@PathVariable UUID id) {
        return getSection(id, "logic");
    }

    @GetMapping("/issues")
    public ResponseEntity<String> getIssues(@PathVariable UUID id) {
        return getSection(id, "issues");
    }

    @GetMapping("/security")
    public ResponseEntity<String> getSecurity(@PathVariable UUID id) {
        return getSection(id, "security");
    }

    @GetMapping("/integrity")
    public ResponseEntity<String> getIntegrity(@PathVariable UUID id) {
        return getSection(id, "integrity");
    }

    @GetMapping("/metrics")
    public ResponseEntity<String> getMetrics(@PathVariable UUID id) {
        return getSection(id, "graphStatistics");
    }

    private ResponseEntity<String> getSection(UUID id, String section) {
        try {
            String json = reportService.getReportSection(id, section);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving section='{}' for runId={}: {}", section, id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve report section: " + section, e);
        }
    }
}
