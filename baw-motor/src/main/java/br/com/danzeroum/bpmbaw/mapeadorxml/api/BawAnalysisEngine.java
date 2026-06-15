package br.com.danzeroum.bpmbaw.mapeadorxml.api;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2.EnhancedStructuredProcessReportV2;

/**
 * Public contract for the BAW analysis engine.
 * Implementations must be thread-safe; each call to analyze() is independent.
 */
public interface BawAnalysisEngine {

    /**
     * Runs a full analysis on the extracted TWX at {@code request.extractionPath()}.
     * The extraction path must point to a directory produced by unzipping a .twx file.
     */
    EnhancedStructuredProcessReportV2 analyze(AnalysisRequest request) throws AnalysisException;

    /**
     * Validates the request without running the analysis.
     * Throws {@link ValidationException} listing all constraint violations found.
     */
    void validate(AnalysisRequest request) throws ValidationException;
}
