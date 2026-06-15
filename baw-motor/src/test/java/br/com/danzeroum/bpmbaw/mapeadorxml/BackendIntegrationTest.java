package br.com.danzeroum.bpmbaw.mapeadorxml;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.facade.EnhancedBawAnalysisFacadeV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2.EnhancedStructuredProcessReportV2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class BackendIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void analyzeFixture_producesValidReport() throws Exception {
        URL resourceUrl = getClass().getClassLoader().getResource("sample-twx");
        assertThat(resourceUrl).as("sample-twx fixture not found on classpath").isNotNull();
        String extractionPath = Paths.get(resourceUrl.toURI()).toString();

        AnalysisConfig config = AnalysisConfig.builder()
                .projectName("Test Project")
                .processId("2064.abcdef01")
                .extractionPath(extractionPath)
                .outputDirectory(tempDir.toString())
                .outputFileName("test-report.json")
                .enableDetailedLogging(false)
                .build();

        EnhancedBawAnalysisFacadeV2 facade = new EnhancedBawAnalysisFacadeV2();
        EnhancedStructuredProcessReportV2 report = facade.executeEnhancedAnalysisV2(config);

        assertThat(report).isNotNull();

        // AI score must be within valid range — never 100 for a real process (P1-6 regression guard)
        assertThat(report.getAiReadinessScore()).isNotNull();
        assertThat(report.getAiReadinessScore().getOverallScore())
                .as("overallScore must be 0–100")
                .isBetween(0.0, 100.0);

        // Integrity checksum must be real SHA-256, never a hashCode stub (P1-3 regression guard)
        assertThat(report.getIntegrity()).isNotNull();
        assertThat(report.getIntegrity().getOverallChecksum())
                .as("overallChecksum must be a real SHA-256")
                .startsWith("sha256-")
                .doesNotContain("mock");

        // Process graph must have nodes extracted from the BPD fixture
        assertThat(report.getProcessGraph()).isNotNull();
        assertThat(report.getProcessGraph().getNodes())
                .as("graph must contain at least the 3 fixture nodes (start, task, end)")
                .isNotEmpty();
    }
}
