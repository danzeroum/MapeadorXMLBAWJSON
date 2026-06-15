package br.com.danzeroum.processveritas.service;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2.EnhancedStructuredProcessReportV2;
import br.com.danzeroum.processveritas.domain.repository.ReportRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private ReportRepository reportRepo;

    @Autowired
    private ObjectMapper objectMapper;

    public EnhancedStructuredProcessReportV2 getFullReport(UUID runId) throws Exception {
        String payload = reportRepo.findById(runId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Report not found for run: " + runId))
                .getPayload();
        return objectMapper.readValue(payload, EnhancedStructuredProcessReportV2.class);
    }

    public String getReportSection(UUID runId, String section) throws Exception {
        String payload = reportRepo.findById(runId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Report not found for run: " + runId))
                .getPayload();

        JsonNode root = objectMapper.readTree(payload);
        JsonNode node = root.get(section);
        if (node == null || node.isNull()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Section '" + section + "' not found in report for run: " + runId);
        }
        log.debug("Returning section='{}' for runId={}", section, runId);
        return objectMapper.writeValueAsString(node);
    }
}
