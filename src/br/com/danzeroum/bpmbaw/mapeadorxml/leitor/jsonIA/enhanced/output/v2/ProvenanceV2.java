package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ProvenanceV2 {
    private String deterministicRunId;
    private ToolInformation tool;
    private SourceInformation source;
    private PipelineInformation pipeline;
    private PreviousRunDelta previousRun;
    private String exportedAt; // RFC 3339

    public ProvenanceV2() {
    }

    // Factory method para criar provenance determinística
    public static ProvenanceV2 createDeterministic(String projectName, String processId) {
        ProvenanceV2 prov = new ProvenanceV2();
        prov.deterministicRunId = String.format("run-%s-%s-%s",
                Instant.now().toString().substring(0, 19).replace(":", "").replace("-", ""),
                projectName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(),
                processId.substring(0, 8)
        );
        prov.exportedAt = Instant.now().toString(); // RFC 3339
        return prov;
    }

    // Getters and setters
    public String getDeterministicRunId() {
        return deterministicRunId;
    }

    public void setDeterministicRunId(String deterministicRunId) {
        this.deterministicRunId = deterministicRunId;
    }

    public ToolInformation getTool() {
        return tool;
    }

    public void setTool(ToolInformation tool) {
        this.tool = tool;
    }

    public SourceInformation getSource() {
        return source;
    }

    public void setSource(SourceInformation source) {
        this.source = source;
    }

    public PipelineInformation getPipeline() {
        return pipeline;
    }

    public void setPipeline(PipelineInformation pipeline) {
        this.pipeline = pipeline;
    }

    public PreviousRunDelta getPreviousRun() {
        return previousRun;
    }

    public void setPreviousRun(PreviousRunDelta previousRun) {
        this.previousRun = previousRun;
    }

    public String getExportedAt() {
        return exportedAt;
    }

    public void setExportedAt(String exportedAt) {
        // Validate RFC 3339 format
        if (!exportedAt.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[.\\d]*([+-]\\d{2}:\\d{2}|Z)")) {
            throw new IllegalArgumentException("exportedAt must be RFC 3339 format");
        }
        this.exportedAt = exportedAt;
    }

    public static class ToolInformation {
        private String name;
        private String version;
        private String commit;
        private String buildDate;
        private String javaVersion;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getCommit() {
            return commit;
        }

        public void setCommit(String commit) {
            this.commit = commit;
        }

        public String getBuildDate() {
            return buildDate;
        }

        public void setBuildDate(String buildDate) {
            this.buildDate = buildDate;
        }

        public String getJavaVersion() {
            return javaVersion;
        }

        public void setJavaVersion(String javaVersion) {
            this.javaVersion = javaVersion;
        }
    }

    public static class SourceInformation {
        private String twxFile;
        private String twxChecksum;
        private String extractionBranch;
        private String extractionCommit;
        private String extractionPath;

        // Getters and setters
        public String getTwxFile() {
            return twxFile;
        }

        public void setTwxFile(String twxFile) {
            this.twxFile = twxFile;
        }

        public String getTwxChecksum() {
            return twxChecksum;
        }

        public void setTwxChecksum(String twxChecksum) {
            this.twxChecksum = twxChecksum;
        }

        public String getExtractionBranch() {
            return extractionBranch;
        }

        public void setExtractionBranch(String extractionBranch) {
            this.extractionBranch = extractionBranch;
        }

        public String getExtractionCommit() {
            return extractionCommit;
        }

        public void setExtractionCommit(String extractionCommit) {
            this.extractionCommit = extractionCommit;
        }

        public String getExtractionPath() {
            return extractionPath;
        }

        public void setExtractionPath(String extractionPath) {
            this.extractionPath = extractionPath;
        }
    }

    public static class PipelineInformation {
        private String id;
        private List<String> steps;
        private String duration; // ISO 8601 duration
        private Map<String, Object> parameters;

        // Getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<String> getSteps() {
            return steps;
        }

        public void setSteps(List<String> steps) {
            this.steps = steps;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
    }

    public static class PreviousRunDelta {
        private String runId;
        private Map<String, Integer> added;
        private Map<String, Integer> modified;
        private Map<String, Integer> removed;

        // Getters and setters
        public String getRunId() {
            return runId;
        }

        public void setRunId(String runId) {
            this.runId = runId;
        }

        public Map<String, Integer> getAdded() {
            return added;
        }

        public void setAdded(Map<String, Integer> added) {
            this.added = added;
        }

        public Map<String, Integer> getModified() {
            return modified;
        }

        public void setModified(Map<String, Integer> modified) {
            this.modified = modified;
        }

        public Map<String, Integer> getRemoved() {
            return removed;
        }

        public void setRemoved(Map<String, Integer> removed) {
            this.removed = removed;
        }
    }
}
