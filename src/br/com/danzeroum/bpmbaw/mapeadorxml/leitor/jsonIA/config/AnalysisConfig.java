package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config;

import java.io.File;

/**
 * VERSÃO CORRIGIDA - Analysis Configuration for V2Plus compatibility
 * Provides both constructor and setter-based configuration for enhanced flexibility.
 *
 * Compatible with Java 8 and V2Plus enhanced extractors.
 */
public class AnalysisConfig {
    private String projectName;
    private String processId;
    private String activityName;
    private String extractionPath;
    private String outputFileName;
    private String outputDirectory = BawAnalysisConfig.DEFAULT_OUTPUT_DIRECTORY;
    private int rootViewDepth = BawAnalysisConfig.DEFAULT_ROOT_VIEW_DEPTH;
    private int maxExecutionPaths = BawAnalysisConfig.MAX_ALTERNATIVE_PATHS;
    private boolean enableDetailedLogging = true;

    // ========== CONSTRUCTORS ==========

    /**
     * Default public constructor - REQUIRED for V2Plus compatibility
     */
    public AnalysisConfig() {
        // Default constructor for setter-based configuration
    }

    /**
     * Full constructor for quick configuration
     */
    public AnalysisConfig(String projectName, String processId, String extractionPath,
                          String outputDirectory, String outputFileName) {
        this.projectName = projectName;
        this.processId = processId;
        this.extractionPath = extractionPath;
        this.outputDirectory = outputDirectory != null ? outputDirectory : BawAnalysisConfig.DEFAULT_OUTPUT_DIRECTORY;
        this.outputFileName = outputFileName;
    }

    // ========== BUILDER PATTERN (Optional) ==========

    public static AnalysisConfigBuilder builder() {
        return new AnalysisConfigBuilder();
    }

    // ========== GETTERS ==========

    public String getProjectName() { return projectName; }
    public String getProcessId() { return processId; }
    public String getActivityName() { return activityName; }
    public String getExtractionPath() { return extractionPath; }
    public String getOutputFileName() { return outputFileName; }
    public String getOutputDirectory() { return outputDirectory; }
    public int getRootViewDepth() { return rootViewDepth; }
    public int getMaxExecutionPaths() { return maxExecutionPaths; }
    public boolean isDetailedLoggingEnabled() { return enableDetailedLogging; }

    // ========== SETTERS - REQUIRED for V2Plus ==========

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public void setExtractionPath(String extractionPath) {
        this.extractionPath = extractionPath;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory != null ? outputDirectory : BawAnalysisConfig.DEFAULT_OUTPUT_DIRECTORY;
    }

    public void setRootViewDepth(int rootViewDepth) {
        this.rootViewDepth = rootViewDepth;
    }

    public void setMaxExecutionPaths(int maxExecutionPaths) {
        this.maxExecutionPaths = maxExecutionPaths;
    }

    public void setEnableDetailedLogging(boolean enableDetailedLogging) {
        this.enableDetailedLogging = enableDetailedLogging;
    }

    // ========== UTILITY METHODS ==========

    /**
     * Gets the full output file path.
     */
    public String getOutputFilePath() {
        return new File(outputDirectory, outputFileName).getPath();
    }

    /**
     * Validates the configuration - ENHANCED for V2Plus
     */
    public void validate() throws IllegalArgumentException {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name is required");
        }
        if (processId == null || processId.trim().isEmpty()) {
            throw new IllegalArgumentException("Process ID is required");
        }
        if (extractionPath == null || !BawAnalysisConfig.isValidTwxPath(extractionPath)) {
            throw new IllegalArgumentException("Valid extraction path is required");
        }
        if (outputFileName == null || outputFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Output file name is required");
        }
        if (rootViewDepth < 0 || rootViewDepth > BawAnalysisConfig.MAX_RECURSION_DEPTH) {
            throw new IllegalArgumentException("Root view depth must be between 0 and " +
                    BawAnalysisConfig.MAX_RECURSION_DEPTH);
        }
    }

    @Override
    public String toString() {
        return String.format("AnalysisConfig{project='%s', process='%s', activity='%s', " +
                        "path='%s', output='%s', depth=%d}",
                projectName, processId, activityName, extractionPath,
                outputFileName, rootViewDepth);
    }

    // ========== BUILDER CLASS ==========

    public static class AnalysisConfigBuilder {
        private final AnalysisConfig config = new AnalysisConfig();

        public AnalysisConfigBuilder projectName(String projectName) {
            config.projectName = projectName;
            return this;
        }

        public AnalysisConfigBuilder processId(String processId) {
            config.processId = processId;
            return this;
        }

        public AnalysisConfigBuilder activityName(String activityName) {
            config.activityName = activityName;
            return this;
        }

        public AnalysisConfigBuilder extractionPath(String extractionPath) {
            config.extractionPath = extractionPath;
            return this;
        }

        public AnalysisConfigBuilder outputFileName(String outputFileName) {
            config.outputFileName = outputFileName;
            return this;
        }

        public AnalysisConfigBuilder outputDirectory(String outputDirectory) {
            config.outputDirectory = outputDirectory;
            return this;
        }

        public AnalysisConfigBuilder rootViewDepth(int depth) {
            config.rootViewDepth = depth;
            return this;
        }

        public AnalysisConfigBuilder maxExecutionPaths(int maxPaths) {
            config.maxExecutionPaths = maxPaths;
            return this;
        }

        public AnalysisConfigBuilder enableDetailedLogging(boolean enable) {
            config.enableDetailedLogging = enable;
            return this;
        }

        /**
         * Builds and validates the configuration.
         */
        public AnalysisConfig build() {
            config.validate();
            return config;
        }
    }
}