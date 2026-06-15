package br.com.danzeroum.processveritas.service;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2.EnhancedStructuredProcessReportV2;
import br.com.danzeroum.processveritas.domain.model.ReportEntity;
import br.com.danzeroum.processveritas.domain.model.RunEntity;
import br.com.danzeroum.processveritas.domain.model.RunEntity.RunStatus;
import br.com.danzeroum.processveritas.domain.repository.ReportRepository;
import br.com.danzeroum.processveritas.domain.repository.RunRepository;
import br.com.danzeroum.processveritas.motor.BawMotorAdapter;
import br.com.danzeroum.processveritas.motor.TwxUnpackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AnalysisOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AnalysisOrchestrator.class);

    @Autowired
    private RunRepository runRepo;

    @Autowired
    private ReportRepository reportRepo;

    @Autowired
    private BawMotorAdapter motorAdapter;

    @Autowired
    private TwxUnpackService unpackService;

    @Autowired
    private ObjectMapper objectMapper;

    @Async("analysisExecutor")
    public CompletableFuture<Void> processRun(UUID runId, Path extractionPath) {
        RunEntity run = runRepo.findById(runId).orElseThrow(
                () -> new IllegalStateException("Run not found: " + runId));

        run.setStatus(RunStatus.RUNNING);
        run.setStartedAt(OffsetDateTime.now());
        runRepo.save(run);
        log.info("Analysis started for runId={} processName={}", runId, run.getProcessName());

        long start = System.currentTimeMillis();
        try {
            EnhancedStructuredProcessReportV2 report = motorAdapter.analyze(
                    run.getProcessName(), run.getProcessId(), extractionPath);

            run.setStatus(RunStatus.DONE);
            run.setCompletedAt(OffsetDateTime.now());
            run.setDurationMs(System.currentTimeMillis() - start);
            runRepo.save(run);

            ReportEntity reportEntity = new ReportEntity();
            reportEntity.setRun(run);
            reportEntity.setPayload(objectMapper.writeValueAsString(report));
            reportRepo.save(reportEntity);

            log.info("Analysis completed for runId={} durationMs={}", runId, run.getDurationMs());

        } catch (Exception e) {
            log.error("Analysis failed for runId={}: {}", runId, e.getMessage(), e);
            run.setStatus(RunStatus.FAILED);
            run.setCompletedAt(OffsetDateTime.now());
            run.setDurationMs(System.currentTimeMillis() - start);
            run.setErrorMessage(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            runRepo.save(run);
        } finally {
            unpackService.cleanup(runId);
        }

        return CompletableFuture.completedFuture(null);
    }
}
