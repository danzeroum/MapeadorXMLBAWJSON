package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.factory;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2.ProvenanceV2;

import java.util.Arrays;

/**
 * Creates ProvenanceV2 and report ID values from AnalysisConfig.
 * All methods are stateless.
 */
public final class ProvenanceFactory {

    private static final String TOOL_VERSION = "2.0.0";

    private ProvenanceFactory() {}

    public static String generateReportId(AnalysisConfig config) {
        String processIdNorm = config.getProcessId().toLowerCase().replaceAll("[^a-z0-9]", "-");
        String projectNorm = config.getProjectName().toLowerCase().replaceAll("[^a-z0-9]", "-");
        return "urn:pv:report:" + projectNorm + "-" + processIdNorm + ":2";
    }

    public static ProvenanceV2 createProvenance(AnalysisConfig config) {
        ProvenanceV2 provenance = new ProvenanceV2();
        provenance.setDeterministicRunId(generateDeterministicRunId(config));

        ProvenanceV2.ToolInformation tool = new ProvenanceV2.ToolInformation();
        tool.setName("EnhancedBawAnalysisFacadeV2");
        tool.setVersion(TOOL_VERSION);
        tool.setJavaVersion(System.getProperty("java.version"));
        provenance.setTool(tool);

        ProvenanceV2.SourceInformation source = new ProvenanceV2.SourceInformation();
        source.setExtractionPath(config.getExtractionPath());
        source.setTwxFile(config.getProjectName() + ".twx");
        provenance.setSource(source);

        ProvenanceV2.PipelineInformation pipeline = new ProvenanceV2.PipelineInformation();
        pipeline.setId("ai-trustworthy-analysis");
        pipeline.setSteps(Arrays.asList(
                "Structure Analysis",
                "Security Analysis",
                "AI Readiness Calculation",
                "Index Creation",
                "Integrity Verification"
        ));
        provenance.setPipeline(pipeline);

        return provenance;
    }

    private static String generateDeterministicRunId(AnalysisConfig config) {
        String input = config.getProjectName() + config.getProcessId() + config.getExtractionPath();
        return "run-" + Math.abs(input.hashCode());
    }
}
