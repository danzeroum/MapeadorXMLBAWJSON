package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogicScriptV2 {
    private String id;
    private String name;
    private String language;
    private String version;
    private String description;
    private List<String> inputs;
    private List<String> outputs;
    private ScriptSource source;
    private ScriptProvenance provenance;
    private ExecutionPolicy executionPolicy;
    private List<String> duplicatesOf;
    private double similarityScore;

    public LogicScriptV2() {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.duplicatesOf = new ArrayList<>();
    }

    // Factory method para criar script com proveniência
    public static LogicScriptV2 createFromFlowStep(String stepId, String stepName, String script) {
        LogicScriptV2 logicScript = new LogicScriptV2();

        logicScript.id = "lg:" + stepId.replace(".", "_");
        logicScript.name = stepName + " Logic";
        logicScript.language = "javascript";
        logicScript.version = "1.0.0";
        logicScript.description = "Extracted logic from process step: " + stepName;

        // Criar source
        logicScript.source = new ScriptSource();
        logicScript.source.setRepository("legacy-inline");
        logicScript.source.setInlineCode(script);
        logicScript.source.setCodeChecksum(calculateChecksum(script));

        // Criar proveniência
        logicScript.provenance = new ScriptProvenance();
        logicScript.provenance.setSourceStepId(stepId);
        logicScript.provenance.setExtractionMethod("automated-flow-analysis");
        logicScript.provenance.setExtractedAt(java.time.Instant.now().toString());

        // Política de execução padrão
        logicScript.executionPolicy = ExecutionPolicy.createDefault();

        return logicScript;
    }

    private static String calculateChecksum(String code) {
        // Implementação simplificada - usar SHA-256 real em produção
        return "sha256-" + Integer.toHexString(code.hashCode());
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }

    public List<String> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<String> outputs) {
        this.outputs = outputs;
    }

    public ScriptSource getSource() {
        return source;
    }

    public void setSource(ScriptSource source) {
        this.source = source;
    }

    public ScriptProvenance getProvenance() {
        return provenance;
    }

    public void setProvenance(ScriptProvenance provenance) {
        this.provenance = provenance;
    }

    public ExecutionPolicy getExecutionPolicy() {
        return executionPolicy;
    }

    public void setExecutionPolicy(ExecutionPolicy executionPolicy) {
        this.executionPolicy = executionPolicy;
    }

    public List<String> getDuplicatesOf() {
        return duplicatesOf;
    }

    public void setDuplicatesOf(List<String> duplicatesOf) {
        this.duplicatesOf = duplicatesOf;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(double similarityScore) {
        this.similarityScore = similarityScore;
    }

    // Classes internas
    public static class ScriptSource {
        private String repository;
        private String inlineCode;
        private String codeChecksum;
        private String astFingerprint;

        // Getters and setters
        public String getRepository() {
            return repository;
        }

        public void setRepository(String repository) {
            this.repository = repository;
        }

        public String getInlineCode() {
            return inlineCode;
        }

        public void setInlineCode(String inlineCode) {
            this.inlineCode = inlineCode;
        }

        public String getCodeChecksum() {
            return codeChecksum;
        }

        public void setCodeChecksum(String codeChecksum) {
            this.codeChecksum = codeChecksum;
        }

        public String getAstFingerprint() {
            return astFingerprint;
        }

        public void setAstFingerprint(String astFingerprint) {
            this.astFingerprint = astFingerprint;
        }
    }

    public static class ScriptProvenance {
        private String sourceStepId;
        private String sourceFile;
        private Integer sourceLine;
        private String extractionMethod;
        private String extractedAt;
        private String lastModified;

        // Getters and setters
        public String getSourceStepId() {
            return sourceStepId;
        }

        public void setSourceStepId(String sourceStepId) {
            this.sourceStepId = sourceStepId;
        }

        public String getSourceFile() {
            return sourceFile;
        }

        public void setSourceFile(String sourceFile) {
            this.sourceFile = sourceFile;
        }

        public Integer getSourceLine() {
            return sourceLine;
        }

        public void setSourceLine(Integer sourceLine) {
            this.sourceLine = sourceLine;
        }

        public String getExtractionMethod() {
            return extractionMethod;
        }

        public void setExtractionMethod(String extractionMethod) {
            this.extractionMethod = extractionMethod;
        }

        public String getExtractedAt() {
            return extractedAt;
        }

        public void setExtractedAt(String extractedAt) {
            this.extractedAt = extractedAt;
        }

        public String getLastModified() {
            return lastModified;
        }

        public void setLastModified(String lastModified) {
            this.lastModified = lastModified;
        }
    }

    public static class ExecutionPolicy {
        private String timeout;
        private String maxMemory;
        private List<String> allowedAPIs;

        public static ExecutionPolicy createDefault() {
            ExecutionPolicy policy = new ExecutionPolicy();
            policy.timeout = "PT30S"; // 30 seconds
            policy.maxMemory = "64MB";
            policy.allowedAPIs = Arrays.asList("Math", "Date", "String", "Number");
            return policy;
        }

        // Getters and setters
        public String getTimeout() {
            return timeout;
        }

        public void setTimeout(String timeout) {
            this.timeout = timeout;
        }

        public String getMaxMemory() {
            return maxMemory;
        }

        public void setMaxMemory(String maxMemory) {
            this.maxMemory = maxMemory;
        }

        public List<String> getAllowedAPIs() {
            return allowedAPIs;
        }

        public void setAllowedAPIs(List<String> allowedAPIs) {
            this.allowedAPIs = allowedAPIs;
        }
    }
}
