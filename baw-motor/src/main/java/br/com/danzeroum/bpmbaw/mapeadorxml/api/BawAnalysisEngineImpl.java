package br.com.danzeroum.bpmbaw.mapeadorxml.api;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.facade.EnhancedBawAnalysisFacadeV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2.EnhancedStructuredProcessReportV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link BawAnalysisEngine}.
 * Translates {@link AnalysisRequest} to the internal {@link AnalysisConfig}
 * and delegates to {@link EnhancedBawAnalysisFacadeV2}.
 *
 * Thread-safe: EnhancedBawAnalysisFacadeV2 holds no shared mutable state between calls.
 */
public class BawAnalysisEngineImpl implements BawAnalysisEngine {

    private static final Logger log = LoggerFactory.getLogger(BawAnalysisEngineImpl.class);

    private final EnhancedBawAnalysisFacadeV2 facade = new EnhancedBawAnalysisFacadeV2();

    @Override
    public EnhancedStructuredProcessReportV2 analyze(AnalysisRequest request) throws AnalysisException {
        try {
            validate(request);
        } catch (ValidationException e) {
            throw new AnalysisException("Invalid request: " + e.getMessage(), e);
        }
        AnalysisConfig config = toConfig(request);
        log.info("Starting analysis: project={}, processId={}", request.getProjectName(), request.getProcessId());
        try {
            EnhancedStructuredProcessReportV2 report = facade.executeEnhancedAnalysisV2(config);
            log.info("Analysis complete: score={}", report != null && report.getAiReadinessScore() != null
                    ? report.getAiReadinessScore().getOverallScore() : "N/A");
            return report;
        } catch (Exception e) {
            throw new AnalysisException("Analysis failed for process " + request.getProcessId(), e);
        }
    }

    @Override
    public void validate(AnalysisRequest request) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            throw new ValidationException(List.of("request must not be null"));
        }
        if (request.getProjectName() == null || request.getProjectName().isBlank()) {
            errors.add("projectName is required");
        }
        if (request.getProcessId() == null || request.getProcessId().isBlank()) {
            errors.add("processId is required");
        }
        if (request.getExtractionPath() == null) {
            errors.add("extractionPath is required");
        } else if (!request.getExtractionPath().toFile().isDirectory()) {
            errors.add("extractionPath does not exist or is not a directory: " + request.getExtractionPath());
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private AnalysisConfig toConfig(AnalysisRequest req) {
        AnalysisRequest.AnalysisOptions opts = req.getOptions();
        return AnalysisConfig.builder()
                .projectName(req.getProjectName())
                .processId(req.getProcessId())
                .extractionPath(req.getExtractionPath().toString())
                .outputFileName(opts.getOutputFileName())
                .build();
    }
}
