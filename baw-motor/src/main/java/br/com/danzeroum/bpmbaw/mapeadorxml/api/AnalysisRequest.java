package br.com.danzeroum.bpmbaw.mapeadorxml.api;

import java.nio.file.Path;

/**
 * Public request contract for the BAW analysis engine.
 * The API translates this to the internal AnalysisConfig without leaking engine internals.
 */
public class AnalysisRequest {

    private String projectName;
    private String processId;
    private Path extractionPath;
    private AnalysisOptions options;

    private AnalysisRequest() {}

    public static Builder builder() {
        return new Builder();
    }

    public String getProjectName() { return projectName; }
    public String getProcessId() { return processId; }
    public Path getExtractionPath() { return extractionPath; }
    public AnalysisOptions getOptions() { return options != null ? options : AnalysisOptions.defaults(); }

    public static final class Builder {
        private final AnalysisRequest req = new AnalysisRequest();

        public Builder projectName(String v) { req.projectName = v; return this; }
        public Builder processId(String v) { req.processId = v; return this; }
        public Builder extractionPath(Path v) { req.extractionPath = v; return this; }
        public Builder options(AnalysisOptions v) { req.options = v; return this; }

        public AnalysisRequest build() {
            if (req.projectName == null || req.projectName.isBlank())
                throw new IllegalArgumentException("projectName is required");
            if (req.processId == null || req.processId.isBlank())
                throw new IllegalArgumentException("processId is required");
            if (req.extractionPath == null)
                throw new IllegalArgumentException("extractionPath is required");
            return req;
        }
    }

    public static final class AnalysisOptions {
        private boolean generateHtml = false;
        private boolean generateText = false;
        private String outputFileName = "report.json";

        public static AnalysisOptions defaults() {
            return new AnalysisOptions();
        }

        public boolean isGenerateHtml() { return generateHtml; }
        public boolean isGenerateText() { return generateText; }
        public String getOutputFileName() { return outputFileName; }

        public AnalysisOptions generateHtml(boolean v) { this.generateHtml = v; return this; }
        public AnalysisOptions generateText(boolean v) { this.generateText = v; return this; }
        public AnalysisOptions outputFileName(String v) { this.outputFileName = v; return this; }
    }
}
