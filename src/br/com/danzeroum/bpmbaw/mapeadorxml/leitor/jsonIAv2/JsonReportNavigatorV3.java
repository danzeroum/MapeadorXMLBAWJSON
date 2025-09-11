package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services.*;

import java.util.*;

/**
 * JsonReportNavigatorV3 - Corrected version compatible with JsonReportGeneratorV2.
 * Orchestrates the analysis of IBM BAW processes using specialized services.
 * Compatible with Java 8 and uses only available methods in JsonReportGeneratorV2.
 */
public class JsonReportNavigatorV3 {

    private final JsonReportGeneratorV2 generator;
    private final ProcessLoader loader;

    // Specialized services
    private final DependencyExtractorService dependencyExtractor;
    private final ExecutionPathGeneratorService pathGenerator;
    private final RootViewBuilderService rootViewBuilder;
    private final VariableEnricherService variableEnricher;

    // Analysis tracking
    private long analysisStartTime;
    private int processedArtifactCount = 0;

    /**
     * Constructor - initializes all specialized services.
     */
    public JsonReportNavigatorV3(JsonReportGeneratorV2 generator, ProcessLoader loader) {
        this.generator = generator;
        this.loader = loader;

        // Initialize specialized services
        this.dependencyExtractor = new DependencyExtractorService();
        this.pathGenerator = new ExecutionPathGeneratorService();
        this.rootViewBuilder = new RootViewBuilderService(loader);
        this.variableEnricher = new VariableEnricherService(generator, loader);
    }

    /**
     * VERSÃO SIMPLES E SEGURA - Main method to populate the report with process analysis.
     * Fixes ConcurrentModificationException with simple approach.
     */
    public void populateReport(String rootProcessId) {
        try {
            analysisStartTime = System.currentTimeMillis();

            System.out.println("[NAVIGATOR-V3] Starting analysis for process: " + rootProcessId);

            // CORREÇÃO SIMPLES: Converter para List imediatamente
            Map<String, Object> allArtifacts = loader.getCacheDeArtefatos();
            List<String> artifactIds = new ArrayList<>(allArtifacts.keySet()); // Copia as chaves

            System.out.println("[NAVIGATOR-V3] Processing " + artifactIds.size() + " loaded artifacts");

            // Processar usando a lista de IDs (não o Map diretamente)
            for (String artifactId : artifactIds) {
                try {
                    Object artifactData = allArtifacts.get(artifactId);
                    if (artifactData != null) {
                        processArtifact(artifactId, artifactData);
                        processedArtifactCount++;
                    }
                } catch (Exception e) {
                    System.err.println("[NAVIGATOR-V3] Error processing artifact " + artifactId + ": " + e.getMessage());
                    // Continue with other artifacts
                }
            }

            // Post-processing: generate execution paths and root views
            postProcessAllArtifacts();

            long endTime = System.currentTimeMillis();
            System.out.println("[NAVIGATOR-V3] Analysis completed in " + (endTime - analysisStartTime) + "ms");
            System.out.println("[NAVIGATOR-V3] Processed " + processedArtifactCount + " artifacts successfully");

        } catch (Exception e) {
            System.err.println("[NAVIGATOR-V3] Error populating report: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to populate report", e);
        }
    }
    /**
     * CORRECTED - Process a single artifact and add it to the report.
     * Creates artifact directly instead of using non-existent createArtifact method.
     */
    private void processArtifact(String artifactId, Object artifactData) {
        // Find artifact info from loader
        ProcessLoader.ArtifactLocation location = loader.findArtifactLocation(artifactId);

        String artifactName = location != null ? location.objectInfo.getName() : artifactId;
        String artifactType = location != null ? location.objectInfo.getType() : "Unknown";

        System.out.println("[NAVIGATOR-V3] Processing: " + artifactName + " (Type: " + artifactType + ")");

        // CORRECTED: Create artifact manually and add to report
        JsonReportV2.Artifact artifact = createArtifactManually(artifactId, artifactName, artifactType);
        generator.getReport().getArtifacts().add(artifact);

        // Process based on artifact type
        if (artifactData instanceof br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions) {
            processBpmnDefinitions(artifactData, artifact);
        } else if (artifactData instanceof br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks) {
            processTeamworksArtifact(artifactData, artifact);
        } else {
            System.out.println("[NAVIGATOR-V3] Unknown artifact type: " + artifactData.getClass().getSimpleName());
        }

        // Extract dependencies for this artifact
        extractDependencies(artifactData, artifact);
    }

    /**
     * CORRECTED - Creates an artifact manually since createArtifact doesn't exist.
     */
    private JsonReportV2.Artifact createArtifactManually(String artifactId, String artifactName, String artifactType) {
        JsonReportV2.Artifact artifact = new JsonReportV2.Artifact();
        artifact.setId(artifactId);
        artifact.setName(artifactName);
        artifact.setType(artifactType);

        // Initialize collections
        artifact.setParticipants(new ArrayList<String>());
        artifact.setVariables(new JsonReportV2.Variables());
        artifact.setFlow(new ArrayList<JsonReportV2.FlowStep>());
        artifact.setRootView(new ArrayList<JsonReportV2.FlowStep>());
        artifact.setGraph(new JsonReportV2.Graph());

        return artifact;
    }

    /**
     * Process BPMN Definitions (modern processes).
     */
    private void processBpmnDefinitions(Object artifactData, JsonReportV2.Artifact artifact) {
        br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions definitions =
                (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions) artifactData;

        BpmnProcessorService bpmnProcessor = new BpmnProcessorService(generator, loader, variableEnricher);
        bpmnProcessor.processDefinitions(definitions, artifact);

        System.out.println("[NAVIGATOR-V3] Processed BPMN process: " + artifact.getName());
    }

    /**
     * Process Teamworks artifacts (legacy processes, services, etc.).
     */
    private void processTeamworksArtifact(Object artifactData, JsonReportV2.Artifact artifact) {
        br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks teamworks =
                (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks) artifactData;

        if (teamworks.getProcess() != null) {
            // Legacy service process
            TeamworksProcessorService teamworksProcessor = new TeamworksProcessorService(generator, loader, variableEnricher);
            teamworksProcessor.processLegacyService(teamworks.getProcess(), artifact);
            System.out.println("[NAVIGATOR-V3] Processed legacy service: " + artifact.getName());

        } else if (teamworks.getBpd() != null) {
            // Legacy BPD process
            BpdProcessorService bpdProcessor = new BpdProcessorService(generator, loader);
            bpdProcessor.processBpd(teamworks.getBpd(), artifact);
            System.out.println("[NAVIGATOR-V3] Processed legacy BPD: " + artifact.getName());

        } else if (teamworks.getTwClass() != null) {
            // Business object
            BusinessObjectProcessorService boProcessor = new BusinessObjectProcessorService(variableEnricher);
            boProcessor.processTwClass(teamworks.getTwClass());
            System.out.println("[NAVIGATOR-V3] Processed business object: " + artifact.getName());

        } else if (teamworks.getCoachView() != null) {
            // UI component
            UiProcessorService uiProcessor = new UiProcessorService(generator);
            uiProcessor.processCoachView(teamworks.getCoachView());
            System.out.println("[NAVIGATOR-V3] Processed Coach View: " + artifact.getName());

        } else {
            System.out.println("[NAVIGATOR-V3] Teamworks artifact with no recognizable content: " + artifact.getName());
        }
    }

    /**
     * Extract dependencies for an artifact.
     */
    private void extractDependencies(Object artifactData, JsonReportV2.Artifact artifact) {
        try {
            Set<String> dependencies = dependencyExtractor.extractDependencies(artifactData);

            if (!dependencies.isEmpty()) {
                System.out.println("[NAVIGATOR-V3] Found " + dependencies.size() +
                        " dependencies for " + artifact.getName() + ": " + dependencies);

                // Store dependencies in artifact participants for now
                // (since there's no dedicated dependencies field in the model)
                for (String dep : dependencies) {
                    if (!artifact.getParticipants().contains(dep)) {
                        artifact.getParticipants().add("dependency:" + dep);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[NAVIGATOR-V3] Error extracting dependencies for " +
                    artifact.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Post-processing: generate execution paths and root views for all artifacts.
     */
    private void postProcessAllArtifacts() {
        System.out.println("[NAVIGATOR-V3] Starting post-processing...");

        JsonReportV2 report = generator.getReport();
        if (report.getArtifacts() == null || report.getArtifacts().isEmpty()) {
            System.out.println("[NAVIGATOR-V3] No artifacts to post-process");
            return;
        }

        // Generate execution paths
        generateExecutionPathsForAllArtifacts(report);

        // Build root views
        buildRootViewsForAllArtifacts(report);

        // Final variable enrichment
        enrichAllVariables(report);

        System.out.println("[NAVIGATOR-V3] Post-processing completed");
    }

    /**
     * CORRECTED - Generate execution paths for all artifacts.
     */
    private void generateExecutionPathsForAllArtifacts(JsonReportV2 report) {
        System.out.println("[NAVIGATOR-V3] Generating execution paths...");

        for (JsonReportV2.Artifact artifact : report.getArtifacts()) {
            try {
                List<JsonReportV2.ExecutionPath> paths = pathGenerator.generateExecutionPaths(artifact);

                if (!paths.isEmpty()) {
                    System.out.println("[NAVIGATOR-V3] Generated " + paths.size() +
                            " execution paths for: " + artifact.getName());

                    // Add paths to the main report's execution paths list
                    report.getExecutionPaths().addAll(paths);
                }

            } catch (Exception e) {
                System.err.println("[NAVIGATOR-V3] Error generating paths for " +
                        artifact.getName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Build root views for all artifacts.
     */
    private void buildRootViewsForAllArtifacts(JsonReportV2 report) {
        System.out.println("[NAVIGATOR-V3] Building root views...");

        for (JsonReportV2.Artifact artifact : report.getArtifacts()) {
            try {
                rootViewBuilder.buildRootView(artifact, artifact.getId());

                int rootViewSize = artifact.getRootView() != null ? artifact.getRootView().size() : 0;
                if (rootViewSize > 0) {
                    System.out.println("[NAVIGATOR-V3] Built root view for: " + artifact.getName() +
                            " (" + rootViewSize + " steps)");
                }

            } catch (Exception e) {
                System.err.println("[NAVIGATOR-V3] Error building root view for " +
                        artifact.getName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Final variable enrichment for all artifacts.
     */
    private void enrichAllVariables(JsonReportV2 report) {
        System.out.println("[NAVIGATOR-V3] Enriching variables...");

        for (JsonReportV2.Artifact artifact : report.getArtifacts()) {
            try {
                variableEnricher.enrichAllVariablesInArtifact(artifact);
                variableEnricher.augmentVariablesFromFlowMappings(artifact);

            } catch (Exception e) {
                System.err.println("[NAVIGATOR-V3] Error enriching variables for " +
                        artifact.getName() + ": " + e.getMessage());
            }
        }

        System.out.println("[NAVIGATOR-V3] Variable enrichment completed");
    }

    // ========== ANALYSIS SUMMARY METHODS ==========

    /**
     * CORRECTED - Creates an enhanced analysis summary with all required metrics.
     */
    public NavigationAnalysisSummary getAnalysisSummary() {
        JsonReportV2 report = generator.getReport();

        int totalArtifacts = 0;
        int totalDependencies = 0;
        int totalExecutionPaths = 0;
        int maxNestingDepth = 0;
        Map<String, Integer> artifactTypeCount = new HashMap<>();
        List<String> complexArtifacts = new ArrayList<>();

        if (report.getArtifacts() != null) {
            totalArtifacts = report.getArtifacts().size();

            for (JsonReportV2.Artifact artifact : report.getArtifacts()) {
                // Count CallActivity nodes as dependencies
                if (artifact.getGraph() != null && artifact.getGraph().getNodes() != null) {
                    for (JsonReportV2.Node node : artifact.getGraph().getNodes()) {
                        if ("CallActivity".equals(node.getType())) {
                            totalDependencies++;
                        }
                    }
                }

                // Count artifact types
                String type = artifact.getType() != null ? artifact.getType() : "Unknown";
                artifactTypeCount.merge(type, 1, Integer::sum);

                // Check nesting depth in root view
                if (artifact.getRootView() != null) {
                    for (JsonReportV2.FlowStep step : artifact.getRootView()) {
                        if (step.getSubflowDepth() != null && step.getSubflowDepth() > maxNestingDepth) {
                            maxNestingDepth = step.getSubflowDepth();
                        }
                    }
                }

                // Identify complex artifacts
                boolean isComplex = isArtifactComplex(artifact);
                if (isComplex) {
                    complexArtifacts.add(artifact.getName() != null ? artifact.getName() : artifact.getId());
                }
            }
        }

        // Count execution paths from the report
        if (report.getExecutionPaths() != null) {
            totalExecutionPaths = report.getExecutionPaths().size();
        }

        // Calculate analysis time
        long analysisTime = analysisStartTime > 0 ? System.currentTimeMillis() - analysisStartTime : 0;

        return new NavigationAnalysisSummary(
                totalArtifacts,
                totalDependencies,
                totalExecutionPaths,
                analysisTime,
                artifactTypeCount,
                complexArtifacts,
                maxNestingDepth
        );
    }

    /**
     * Determines if an artifact is complex based on various metrics.
     */
    private boolean isArtifactComplex(JsonReportV2.Artifact artifact) {
        // Complex if many flow steps
        if (artifact.getFlow() != null && artifact.getFlow().size() > 20) {
            return true;
        }

        // Complex if many gateways
        if (artifact.getGraph() != null && artifact.getGraph().getGateways() != null &&
                artifact.getGraph().getGateways().size() > 5) {
            return true;
        }

        // Complex if deep nesting
        if (artifact.getRootView() != null) {
            for (JsonReportV2.FlowStep step : artifact.getRootView()) {
                if (step.getSubflowDepth() != null && step.getSubflowDepth() > 2) {
                    return true;
                }
            }
        }

        // Complex if many variables
        if (artifact.getVariables() != null) {
            int varCount = 0;
            if (artifact.getVariables().getInput() != null) {
                varCount += artifact.getVariables().getInput().size();
            }
            if (artifact.getVariables().getOutput() != null) {
                varCount += artifact.getVariables().getOutput().size();
            }
            if (varCount > 15) {
                return true;
            }
        }

        return false;
    }

    /**
     * CORRECTED - Analyzes process complexity across all artifacts.
     */
    public ProcessComplexityAnalysis analyzeProcessComplexity() {
        JsonReportV2 report = generator.getReport();

        int totalSubprocesses = 0;
        int maxNestingDepth = 0;
        List<String> complexArtifactNames = new ArrayList<>();
        Map<String, Integer> complexityMetrics = new HashMap<>();

        if (report.getArtifacts() != null) {
            for (JsonReportV2.Artifact artifact : report.getArtifacts()) {
                String artifactName = artifact.getName() != null ? artifact.getName() : artifact.getId();
                int artifactComplexity = calculateArtifactComplexity(artifact);

                // Count subprocess calls
                if (artifact.getFlow() != null) {
                    for (JsonReportV2.FlowStep step : artifact.getFlow()) {
                        if (step.getCalledArtifactId() != null && !step.getCalledArtifactId().trim().isEmpty()) {
                            totalSubprocesses++;
                        }
                    }
                }

                // Track max nesting depth
                if (artifact.getRootView() != null) {
                    for (JsonReportV2.FlowStep step : artifact.getRootView()) {
                        if (step.getSubflowDepth() != null && step.getSubflowDepth() > maxNestingDepth) {
                            maxNestingDepth = step.getSubflowDepth();
                        }
                    }
                }

                complexityMetrics.put(artifactName, artifactComplexity);

                // Consider artifact complex if score > threshold
                if (artifactComplexity > 10) {
                    complexArtifactNames.add(artifactName);
                }
            }
        }

        return new ProcessComplexityAnalysis(
                totalSubprocesses,
                maxNestingDepth,
                complexArtifactNames,
                complexityMetrics
        );
    }

    /**
     * Calculates complexity score for a single artifact.
     */
    private int calculateArtifactComplexity(JsonReportV2.Artifact artifact) {
        int complexity = 0;

        // Flow size contributes to complexity
        if (artifact.getFlow() != null) {
            complexity += artifact.getFlow().size() / 10; // Every 10 steps = +1 complexity

            // Subprocess calls add complexity
            for (JsonReportV2.FlowStep step : artifact.getFlow()) {
                if (step.getCalledArtifactId() != null && !step.getCalledArtifactId().trim().isEmpty()) {
                    complexity += 2;
                }
            }
        }

        // Gateway complexity
        if (artifact.getGraph() != null && artifact.getGraph().getGateways() != null) {
            complexity += artifact.getGraph().getGateways().size() * 3;
        }

        // Nesting depth complexity
        if (artifact.getRootView() != null) {
            for (JsonReportV2.FlowStep step : artifact.getRootView()) {
                if (step.getSubflowDepth() != null && step.getSubflowDepth() > 0) {
                    complexity += step.getSubflowDepth();
                }
            }
        }

        // Variable complexity
        if (artifact.getVariables() != null) {
            int varCount = 0;
            if (artifact.getVariables().getInput() != null) {
                varCount += artifact.getVariables().getInput().size();
            }
            if (artifact.getVariables().getOutput() != null) {
                varCount += artifact.getVariables().getOutput().size();
            }
            complexity += varCount / 5; // Every 5 variables = +1 complexity
        }

        return complexity;
    }

    // ========== GETTERS ==========

    public JsonReportGeneratorV2 getGenerator() { return generator; }
    public ProcessLoader getLoader() { return loader; }

    // ========== INNER CLASSES (same as before) ==========

    /**
     * Enhanced NavigationAnalysisSummary with complete metrics.
     */
    public static class NavigationAnalysisSummary {
        private final int totalArtifacts;
        private final int totalDependencies;
        private final int totalExecutionPaths;
        private final long analysisTimeMs;
        private final Map<String, Integer> artifactTypeCount;
        private final List<String> complexArtifacts;
        private final int maxNestingDepth;

        public NavigationAnalysisSummary(int totalArtifacts, int totalDependencies, int totalExecutionPaths,
                                         long analysisTimeMs, Map<String, Integer> artifactTypeCount,
                                         List<String> complexArtifacts, int maxNestingDepth) {
            this.totalArtifacts = totalArtifacts;
            this.totalDependencies = totalDependencies;
            this.totalExecutionPaths = totalExecutionPaths;
            this.analysisTimeMs = analysisTimeMs;
            this.artifactTypeCount = new HashMap<>(artifactTypeCount != null ? artifactTypeCount : new HashMap<String, Integer>());
            this.complexArtifacts = new ArrayList<>(complexArtifacts != null ? complexArtifacts : new ArrayList<String>());
            this.maxNestingDepth = maxNestingDepth;
        }

        // Getters
        public int getTotalArtifacts() { return totalArtifacts; }
        public int getTotalDependencies() { return totalDependencies; }
        public int getTotalExecutionPaths() { return totalExecutionPaths; }
        public long getAnalysisTimeMs() { return analysisTimeMs; }
        public Map<String, Integer> getArtifactTypeCount() { return Collections.unmodifiableMap(artifactTypeCount); }
        public List<String> getComplexArtifacts() { return Collections.unmodifiableList(complexArtifacts); }
        public int getMaxNestingDepth() { return maxNestingDepth; }

        // Utility methods
        public boolean hasComplexArtifacts() { return !complexArtifacts.isEmpty(); }
        public boolean isHighlyComplex() { return maxNestingDepth > 3 || complexArtifacts.size() > 5; }
        public String getFormattedAnalysisTime() { return formatDuration(analysisTimeMs); }

        @Override
        public String toString() {
            return String.format("NavigationAnalysis{artifacts=%d, dependencies=%d, paths=%d, time=%s, complex=%s}",
                    totalArtifacts, totalDependencies, totalExecutionPaths,
                    getFormattedAnalysisTime(), isHighlyComplex() ? "YES" : "NO");
        }

        private String formatDuration(long milliseconds) {
            if (milliseconds < 1000) {
                return milliseconds + "ms";
            } else if (milliseconds < 60000) {
                return String.format("%.1fs", milliseconds / 1000.0);
            } else {
                long minutes = milliseconds / 60000;
                long seconds = (milliseconds % 60000) / 1000;
                return String.format("%dm %ds", minutes, seconds);
            }
        }
    }

    /**
     * Process complexity analysis result.
     */
    public static class ProcessComplexityAnalysis {
        private final int totalSubprocesses;
        private final int maxNestingDepth;
        private final List<String> complexArtifacts;
        private final Map<String, Integer> complexityMetrics;

        public ProcessComplexityAnalysis(int totalSubprocesses, int maxNestingDepth,
                                         List<String> complexArtifacts, Map<String, Integer> complexityMetrics) {
            this.totalSubprocesses = totalSubprocesses;
            this.maxNestingDepth = maxNestingDepth;
            this.complexArtifacts = new ArrayList<>(complexArtifacts != null ? complexArtifacts : new ArrayList<String>());
            this.complexityMetrics = new HashMap<>(complexityMetrics != null ? complexityMetrics : new HashMap<String, Integer>());
        }

        // Getters
        public int getTotalSubprocesses() { return totalSubprocesses; }
        public int getMaxNestingDepth() { return maxNestingDepth; }
        public List<String> getComplexArtifacts() { return Collections.unmodifiableList(complexArtifacts); }
        public Map<String, Integer> getComplexityMetrics() { return Collections.unmodifiableMap(complexityMetrics); }

        // Utility methods
        public boolean isHighlyComplex() {
            return maxNestingDepth > 3 || totalSubprocesses > 10 || complexArtifacts.size() > 3;
        }

        public double getAverageComplexity() {
            if (complexityMetrics.isEmpty()) return 0.0;
            return complexityMetrics.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
        }

        public String getMostComplexArtifact() {
            return complexityMetrics.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("None");
        }

        @Override
        public String toString() {
            return String.format("ProcessComplexity{subprocesses=%d, maxDepth=%d, complexArtifacts=%d, avgComplexity=%.1f}",
                    totalSubprocesses, maxNestingDepth, complexArtifacts.size(), getAverageComplexity());
        }
    }
}