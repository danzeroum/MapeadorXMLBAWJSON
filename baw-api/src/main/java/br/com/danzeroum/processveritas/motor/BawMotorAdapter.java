package br.com.danzeroum.processveritas.motor;

import br.com.danzeroum.bpmbaw.mapeadorxml.api.AnalysisException;
import br.com.danzeroum.bpmbaw.mapeadorxml.api.AnalysisRequest;
import br.com.danzeroum.bpmbaw.mapeadorxml.api.BawAnalysisEngine;
import br.com.danzeroum.bpmbaw.mapeadorxml.api.BawAnalysisEngineImpl;
import br.com.danzeroum.bpmbaw.mapeadorxml.api.ValidationException;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2.EnhancedStructuredProcessReportV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Spring-managed adapter between the API layer and the BAW analysis motor.
 * Instantiates {@link BawAnalysisEngineImpl} as a singleton bean — the engine is thread-safe.
 */
@Component
public class BawMotorAdapter {

    private static final Logger log = LoggerFactory.getLogger(BawMotorAdapter.class);

    private final BawAnalysisEngine engine = new BawAnalysisEngineImpl();

    public EnhancedStructuredProcessReportV2 analyze(
            String projectName, String processId, Path extractionPath) throws AnalysisException {

        AnalysisRequest request = AnalysisRequest.builder()
                .projectName(projectName)
                .processId(processId)
                .extractionPath(extractionPath)
                .options(AnalysisRequest.AnalysisOptions.defaults().outputFileName("report.json"))
                .build();

        try {
            engine.validate(request);
        } catch (ValidationException e) {
            throw new AnalysisException("Invalid analysis request: " + e.getMessage(), e);
        }

        log.info("Delegating to motor: project={}, process={}", projectName, processId);
        return engine.analyze(request);
    }
}
