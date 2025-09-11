package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.BawAnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;

import java.util.*;

/**
 * Generates process metrics and executive-level reports for IBM BAW processes.
 * Provides insights on complexity, maintainability, and AI readiness.
 */
public class ProcessMetricsService {

    /**
     * Generates comprehensive metrics for a process report.
     */
    public ProcessMetricsReport generateMetricsReport(JsonReportV2 report) {
        ProcessMetricsReport metricsReport = new ProcessMetricsReport();

        if (report.getArtifacts() == null || report.getArtifacts().isEmpty()) {
            return metricsReport;
        }

        // Calculate metrics for each artifact
        for (JsonReportV2.Artifact artifact : report.getArtifacts()) {
            ArtifactMetrics metrics = calculateArtifactMetrics(artifact);
            metricsReport.addArtifactMetrics(metrics);
        }

        // Calculate aggregated metrics
        calculateAggregatedMetrics(metricsReport);

        return metricsReport;
    }

    /**
     * Calculates detailed metrics for a single artifact.
     */
    private ArtifactMetrics calculateArtifactMetrics(JsonReportV2.Artifact artifact) {
        ArtifactMetrics metrics = new ArtifactMetrics(artifact.getId(), artifact.getName(), artifact.getType());

        // Structural metrics
        calculateStructuralMetrics(artifact, metrics);

        // Complexity metrics
        calculateComplexityMetrics(artifact, metrics);

        // Data metrics
        calculateDataMetrics(artifact, metrics);

        // Quality metrics
        calculateQualityMetrics(artifact, metrics);

        // AI readiness metrics
        calculateAiReadinessMetrics(artifact, metrics);

        return metrics;
    }

    /**
     * Calculates structural metrics (nodes, edges, flows).
     */
    private void calculateStructuralMetrics(JsonReportV2.Artifact artifact, ArtifactMetrics metrics) {
        if (artifact.getGraph() != null) {
            metrics.nodeCount = artifact.getGraph().getNodes() != null ?
                    artifact.getGraph().getNodes().size() : 0;
            metrics.edgeCount = artifact.getGraph().getEdges() != null ?
                    artifact.getGraph().getEdges().size() : 0;
            metrics.gatewayCount = artifact.getGraph().getGateways() != null ?
                    artifact.getGraph().getGateways().size() : 0;
            metrics.conditionCount = artifact.getGraph().getConditions() != null ?
                    artifact.getGraph().getConditions().size() : 0;

            metrics.entryPointCount = artifact.getGraph().getEntryPoints() != null ?
                    artifact.getGraph().getEntryPoints().size() : 0;
            metrics.exitPointCount = artifact.getGraph().getEndPoints() != null ?
                    artifact.getGraph().getEndPoints().size() : 0;
        }

        if (artifact.getFlow() != null) {
            metrics.flowStepCount = artifact.getFlow().size();

            // Count different types of steps
            for (JsonReportV2.FlowStep step : artifact.getFlow()) {
                String stepType = step.getType();
                if ("CallActivity".equals(stepType) || "SubProcess".equals(stepType)) {
                    metrics.subprocessCallCount++;
                } else if ("ScriptTask".equals(stepType)) {
                    metrics.scriptTaskCount++;
                } else if ("FormTask".equals(stepType)) {
                    metrics.userTaskCount++;
                }

                // Count script lines
                if (step.getScript() != null && !step.getScript().trim().isEmpty()) {
                    metrics.totalScriptLines += step.getScript().split("\n").length;
                }
            }
        }

        if (artifact.getRootView() != null) {
            metrics.rootViewStepCount = artifact.getRootView().size();

            // Calculate max depth
            for (JsonReportV2.FlowStep step : artifact.getRootView()) {
                if (step.getSubflowDepth() > metrics.maxNestingDepth) {
                    metrics.maxNestingDepth = step.getSubflowDepth();
                }
            }
        }
    }

    /**
     * Calculates complexity metrics.
     */
    private void calculateComplexityMetrics(JsonReportV2.Artifact artifact, ArtifactMetrics metrics) {
        // Cyclomatic complexity approximation
        metrics.cyclomaticComplexity = calculateCyclomaticComplexity(artifact);

        // Decision point complexity
        metrics.decisionPointComplexity = calculateDecisionPointComplexity(artifact);

        // Interface complexity (parameters)
        if (artifact.getVariables() != null) {
            int inputCount = artifact.getVariables().getInput() != null ?
                    artifact.getVariables().getInput().size() : 0;
            int outputCount = artifact.getVariables().getOutput() != null ?
                    artifact.getVariables().getOutput().size() : 0;

            metrics.interfaceComplexity = inputCount + outputCount;
        }

        // Overall complexity score
        metrics.overallComplexityScore = calculateOverallComplexityScore(metrics);
    }

    /**
     * Calculates data-related metrics.
     */
    private void calculateDataMetrics(JsonReportV2.Artifact artifact, ArtifactMetrics metrics) {
        if (artifact.getVariables() != null) {
            metrics.inputVariableCount = artifact.getVariables().getInput() != null ?
                    artifact.getVariables().getInput().size() : 0;
            metrics.outputVariableCount = artifact.getVariables().getOutput() != null ?
                    artifact.getVariables().getOutput().size() : 0;

            // Try to get private variable count
            try {
                @SuppressWarnings("unchecked")
                List<JsonReportV2.VariableInfo> privateVars =
                        (List<JsonReportV2.VariableInfo>) artifact.getVariables().getClass()
                                .getMethod("getPrivate").invoke(artifact.getVariables());
                metrics.privateVariableCount = privateVars != null ? privateVars.size() : 0;
            } catch (Exception e1) {
                try {
                    @SuppressWarnings("unchecked")
                    List<JsonReportV2.VariableInfo> privateVars =
                            (List<JsonReportV2.VariableInfo>) artifact.getVariables().getClass()
                                    .getMethod("getPrivite").invoke(artifact.getVariables());
                    metrics.privateVariableCount = privateVars != null ? privateVars.size() : 0;
                } catch (Exception e2) {
                    metrics.privateVariableCount = 0;
                }
            }
        }

        // Count parameter mappings
        if (artifact.getFlow() != null) {
            for (JsonReportV2.FlowStep step : artifact.getFlow()) {
                if (step.getParameterMapping() != null) {
                    metrics.parameterMappingCount++;

                    if (step.getParameterMapping().getInput() != null) {
                        metrics.inputMappingCount += step.getParameterMapping().getInput().size();
                    }
                    if (step.getParameterMapping().getOutput() != null) {
                        metrics.outputMappingCount += step.getParameterMapping().getOutput().size();
                    }
                }
            }
        }
    }

    /**
     * Calculates quality-related metrics.
     */
    private void calculateQualityMetrics(JsonReportV2.Artifact artifact, ArtifactMetrics metrics) {
        // Naming quality
        metrics.namingQualityScore = calculateNamingQualityScore(artifact);

        // Documentation coverage (based on named vs unnamed elements)
        metrics.documentationCoverage = calculateDocumentationCoverage(artifact);

        // Modularity score (based on subprocess usage vs monolithic design)
        metrics.modularityScore = calculateModularityScore(artifact);

        // Maintainability index
        metrics.maintainabilityIndex = calculateMaintainabilityIndex(metrics);
    }

    /**
     * Calculates AI readiness metrics.
     */
    private void calculateAiReadinessMetrics(JsonReportV2.Artifact artifact, ArtifactMetrics metrics) {
        // Language consistency (English vs mixed)
        metrics.languageConsistencyScore = calculateLanguageConsistencyScore(artifact);

        // Structure clarity (how clear the process structure is)
        metrics.structureClarityScore = calculateStructureClarityScore(artifact);

        // Expression complexity (how complex are the embedded expressions)
        metrics.expressionComplexityScore = calculateExpressionComplexityScore(artifact);

        // Overall AI readiness
        metrics.aiReadinessScore = calculateOverallAiReadinessScore(metrics);
    }

    // ========== COMPLEXITY CALCULATION METHODS ==========

    private int calculateCyclomaticComplexity(JsonReportV2.Artifact artifact) {
        int edges = artifact.getGraph() != null && artifact.getGraph().getEdges() != null ?
                artifact.getGraph().getEdges().size() : 0;
        int nodes = artifact.getGraph() != null && artifact.getGraph().getNodes() != null ?
                artifact.getGraph().getNodes().size() : 0;
        int components = 1; // Assuming single connected component

        // M = E - N + 2P (where P = number of connected components)
        return Math.max(1, edges - nodes + (2 * components));
    }

    private int calculateDecisionPointComplexity(JsonReportV2.Artifact artifact) {
        int complexity = 1; // Base complexity

        if (artifact.getGraph() != null && artifact.getGraph().getGateways() != null) {
            for (JsonReportV2.Gateway gateway : artifact.getGraph().getGateways()) {
                // Count outgoing flows for each gateway
                int outgoingFlows = countOutgoingFlows(gateway.getId(), artifact);
                complexity += Math.max(0, outgoingFlows - 1);
            }
        }

        return complexity;
    }

    private int countOutgoingFlows(String nodeId, JsonReportV2.Artifact artifact) {
        int count = 0;
        if (artifact.getGraph() != null && artifact.getGraph().getEdges() != null) {
            for (JsonReportV2.Edge edge : artifact.getGraph().getEdges()) {
                if (nodeId.equals(edge.getSource())) {
                    count++;
                }
            }
        }
        return count;
    }

    private double calculateOverallComplexityScore(ArtifactMetrics metrics) {
        // Weighted complexity score (0-100, lower is better)
        double structuralWeight = 0.3;
        double cyclomaticWeight = 0.3;
        double decisionWeight = 0.2;
        double interfaceWeight = 0.2;

        double structuralScore = Math.min(100, (metrics.nodeCount * 2) + (metrics.gatewayCount * 5));
        double cyclomaticScore = Math.min(100, metrics.cyclomaticComplexity * 10);
        double decisionScore = Math.min(100, metrics.decisionPointComplexity * 8);
        double interfaceScore = Math.min(100, metrics.interfaceComplexity * 3);

        return (structuralScore * structuralWeight) +
                (cyclomaticScore * cyclomaticWeight) +
                (decisionScore * decisionWeight) +
                (interfaceScore * interfaceWeight);
    }

    // ========== QUALITY CALCULATION METHODS ==========

    private double calculateNamingQualityScore(JsonReportV2.Artifact artifact) {
        int totalElements = 0;
        int namedElements = 0;
        int englishNamedElements = 0;

        // Check artifact name
        totalElements++;
        if (artifact.getName() != null && !artifact.getName().trim().isEmpty()) {
            namedElements++;
            if (isEnglishName(artifact.getName())) {
                englishNamedElements++;
            }
        }

        // Check node names
        if (artifact.getGraph() != null && artifact.getGraph().getNodes() != null) {
            for (JsonReportV2.Node node : artifact.getGraph().getNodes()) {
                totalElements++;
                if (node.getName() != null && !node.getName().trim().isEmpty() &&
                        !node.getName().equals(node.getId())) {
                    namedElements++;
                    if (isEnglishName(node.getName())) {
                        englishNamedElements++;
                    }
                }
            }
        }

        // Check flow step names
        if (artifact.getFlow() != null) {
            for (JsonReportV2.FlowStep step : artifact.getFlow()) {
                totalElements++;
                if (step.getName() != null && !step.getName().trim().isEmpty()) {
                    namedElements++;
                    if (isEnglishName(step.getName())) {
                        englishNamedElements++;
                    }
                }
            }
        }

        if (totalElements == 0) return 100.0;

        // Score based on naming coverage and language consistency
        double namingCoverage = (double) namedElements / totalElements;
        double languageConsistency = namedElements > 0 ? (double) englishNamedElements / namedElements : 1.0;

        return (namingCoverage * 0.6 + languageConsistency * 0.4) * 100.0;
    }

    private double calculateDocumentationCoverage(JsonReportV2.Artifact artifact) {
        // Simplified documentation coverage based on descriptive naming
        return calculateNamingQualityScore(artifact) * 0.8; // Assume naming quality correlates with documentation
    }

    private double calculateModularityScore(JsonReportV2.Artifact artifact) {
        if (artifact.getFlow() == null || artifact.getFlow().isEmpty()) return 100.0;

        int totalSteps = artifact.getFlow().size();
        int modularSteps = 0;

        for (JsonReportV2.FlowStep step : artifact.getFlow()) {
            if ("CallActivity".equals(step.getType()) || "SubProcess".equals(step.getType())) {
                modularSteps++;
            }
        }

        // Score based on ratio of modular vs monolithic design
        double modularityRatio = (double) modularSteps / totalSteps;

        // Ideal modularity is around 20-40% subprocess calls
        if (modularityRatio >= 0.2 && modularityRatio <= 0.4) {
            return 100.0;
        } else if (modularityRatio < 0.2) {
            return modularityRatio * 500.0; // Penalize too monolithic
        } else {
            return Math.max(0, 100.0 - ((modularityRatio - 0.4) * 200.0)); // Penalize too fragmented
        }
    }

    private double calculateMaintainabilityIndex(ArtifactMetrics metrics) {
        // Simplified maintainability index (0-100, higher is better)
        double complexityPenalty = Math.min(50, metrics.overallComplexityScore / 2);
        double qualityBonus = metrics.namingQualityScore * 0.3;
        double modularityBonus = metrics.modularityScore * 0.2;

        return Math.max(0, 100 - complexityPenalty + qualityBonus + modularityBonus);
    }

    // ========== AI READINESS CALCULATION METHODS ==========

    private double calculateLanguageConsistencyScore(JsonReportV2.Artifact artifact) {
        return calculateNamingQualityScore(artifact); // Reuse naming quality logic
    }

    private double calculateStructureClarityScore(JsonReportV2.Artifact artifact) {
        double score = 100.0;

        // Penalize excessive nesting
        if (artifact.getRootView() != null) {
            int maxDepth = 0;
            for (JsonReportV2.FlowStep step : artifact.getRootView()) {
                if (step.getSubflowDepth() > maxDepth) {
                    maxDepth = step.getSubflowDepth();
                }
            }

            if (maxDepth > 3) {
                score -= (maxDepth - 3) * 20; // Penalize each level beyond 3
            }
        }

        // Penalize too many gateways
        int gatewayCount = artifact.getGraph() != null && artifact.getGraph().getGateways() != null ?
                artifact.getGraph().getGateways().size() : 0;
        if (gatewayCount > 5) {
            score -= (gatewayCount - 5) * 10;
        }

        return Math.max(0, score);
    }

    private double calculateExpressionComplexityScore(JsonReportV2.Artifact artifact) {
        double score = 100.0;
        int complexExpressions = 0;
        int totalExpressions = 0;

        // Check conditions
        if (artifact.getGraph() != null && artifact.getGraph().getConditions() != null) {
            for (JsonReportV2.EdgeCondition condition : artifact.getGraph().getConditions()) {
                if (condition.getExpression() != null && !condition.getExpression().trim().isEmpty()) {
                    totalExpressions++;
                    if (isComplexExpression(condition.getExpression())) {
                        complexExpressions++;
                    }
                }
            }
        }

        // Check scripts
        if (artifact.getFlow() != null) {
            for (JsonReportV2.FlowStep step : artifact.getFlow()) {
                if (step.getScript() != null && !step.getScript().trim().isEmpty()) {
                    totalExpressions++;
                    if (isComplexScript(step.getScript())) {
                        complexExpressions++;
                    }
                }
            }
        }

        if (totalExpressions > 0) {
            double complexityRatio = (double) complexExpressions / totalExpressions;
            score = Math.max(0, 100 - (complexityRatio * 100));
        }

        return score;
    }

    private double calculateOverallAiReadinessScore(ArtifactMetrics metrics) {
        // Weighted AI readiness score (0-100, higher is better)
        return (metrics.languageConsistencyScore * 0.3) +
                (metrics.structureClarityScore * 0.3) +
                (metrics.expressionComplexityScore * 0.2) +
                (metrics.namingQualityScore * 0.2);
    }

    // ========== HELPER METHODS ==========

    private boolean isEnglishName(String name) {
        if (name == null) return false;
        // Simple heuristic: check for Portuguese characters
        return !name.matches(".*[àáâãäåèéêëìíîïòóôõöùúûüçñÀÁÂÃÄÅÈÉÊËÌÍÎÏÒÓÔÕÖÙÚÛÜÇÑ].*");
    }

    private boolean isComplexExpression(String expression) {
        if (expression == null) return false;

        // Heuristics for complex expressions
        return expression.length() > 100 ||
                (expression.contains("&&") && expression.contains("||")) ||
                expression.split("\\(").length > 4 ||
                expression.contains("for(") ||
                expression.contains("while(");
    }

    private boolean isComplexScript(String script) {
        if (script == null) return false;

        // Heuristics for complex scripts
        return script.length() > 500 ||
                script.split("\n").length > 15 ||
                script.split("if\\s*\\(").length > 3 ||
                script.contains("try") && script.contains("catch");
    }

    /**
     * Calculates aggregated metrics across all artifacts.
     */
    private void calculateAggregatedMetrics(ProcessMetricsReport report) {
        List<ArtifactMetrics> allMetrics = report.getArtifactMetrics();
        if (allMetrics.isEmpty()) return;

        AggregatedMetrics aggregated = new AggregatedMetrics();

        // Sum up totals
        for (ArtifactMetrics metrics : allMetrics) {
            aggregated.totalArtifacts++;
            aggregated.totalNodes += metrics.nodeCount;
            aggregated.totalEdges += metrics.edgeCount;
            aggregated.totalGateways += metrics.gatewayCount;
            aggregated.totalSubprocessCalls += metrics.subprocessCallCount;
            aggregated.totalScriptTasks += metrics.scriptTaskCount;
            aggregated.totalUserTasks += metrics.userTaskCount;
            aggregated.totalScriptLines += metrics.totalScriptLines;
            aggregated.totalVariables += (metrics.inputVariableCount +
                    metrics.outputVariableCount +
                    metrics.privateVariableCount);

            // Track maximums
            aggregated.maxNestingDepth = Math.max(aggregated.maxNestingDepth, metrics.maxNestingDepth);
            aggregated.maxCyclomaticComplexity = Math.max(aggregated.maxCyclomaticComplexity, metrics.cyclomaticComplexity);

            // Accumulate for averages
            aggregated.sumComplexityScore += metrics.overallComplexityScore;
            aggregated.sumAiReadinessScore += metrics.aiReadinessScore;
            aggregated.sumMaintainabilityIndex += metrics.maintainabilityIndex;

            // Count quality levels
            if (metrics.aiReadinessScore >= 80) aggregated.highQualityArtifacts++;
            else if (metrics.aiReadinessScore >= 60) aggregated.mediumQualityArtifacts++;
            else aggregated.lowQualityArtifacts++;
        }

        // Calculate averages
        aggregated.avgComplexityScore = aggregated.sumComplexityScore / allMetrics.size();
        aggregated.avgAiReadinessScore = aggregated.sumAiReadinessScore / allMetrics.size();
        aggregated.avgMaintainabilityIndex = aggregated.sumMaintainabilityIndex / allMetrics.size();
        aggregated.avgNodesPerArtifact = (double) aggregated.totalNodes / allMetrics.size();

        report.setAggregatedMetrics(aggregated);
    }

    // ========== RESULT CLASSES ==========

    /**
     * Contains metrics for a single artifact.
     */
    public static class ArtifactMetrics {
        // Basic info
        public final String artifactId;
        public final String artifactName;
        public final String artifactType;

        // Structural metrics
        public int nodeCount = 0;
        public int edgeCount = 0;
        public int gatewayCount = 0;
        public int conditionCount = 0;
        public int entryPointCount = 0;
        public int exitPointCount = 0;
        public int flowStepCount = 0;
        public int rootViewStepCount = 0;
        public int maxNestingDepth = 0;

        // Activity metrics
        public int subprocessCallCount = 0;
        public int scriptTaskCount = 0;
        public int userTaskCount = 0;
        public int totalScriptLines = 0;

        // Complexity metrics
        public int cyclomaticComplexity = 0;
        public int decisionPointComplexity = 0;
        public int interfaceComplexity = 0;
        public double overallComplexityScore = 0.0;

        // Data metrics
        public int inputVariableCount = 0;
        public int outputVariableCount = 0;
        public int privateVariableCount = 0;
        public int parameterMappingCount = 0;
        public int inputMappingCount = 0;
        public int outputMappingCount = 0;

        // Quality metrics
        public double namingQualityScore = 0.0;
        public double documentationCoverage = 0.0;
        public double modularityScore = 0.0;
        public double maintainabilityIndex = 0.0;

        // AI readiness metrics
        public double languageConsistencyScore = 0.0;
        public double structureClarityScore = 0.0;
        public double expressionComplexityScore = 0.0;
        public double aiReadinessScore = 0.0;

        public ArtifactMetrics(String artifactId, String artifactName, String artifactType) {
            this.artifactId = artifactId;
            this.artifactName = artifactName;
            this.artifactType = artifactType;
        }

        public ComplexityLevel getComplexityLevel() {
            if (overallComplexityScore <= 30) return ComplexityLevel.LOW;
            if (overallComplexityScore <= 60) return ComplexityLevel.MEDIUM;
            if (overallComplexityScore <= 80) return ComplexityLevel.HIGH;
            return ComplexityLevel.VERY_HIGH;
        }

        public QualityLevel getQualityLevel() {
            if (aiReadinessScore >= 80) return QualityLevel.HIGH;
            if (aiReadinessScore >= 60) return QualityLevel.MEDIUM;
            if (aiReadinessScore >= 40) return QualityLevel.LOW;
            return QualityLevel.VERY_LOW;
        }

        @Override
        public String toString() {
            return String.format("ArtifactMetrics{name='%s', complexity=%s, quality=%s, nodes=%d, ai-ready=%.1f}",
                    artifactName, getComplexityLevel(), getQualityLevel(), nodeCount, aiReadinessScore);
        }
    }

    /**
     * Contains aggregated metrics across all artifacts.
     */
    public static class AggregatedMetrics {
        // Totals
        public int totalArtifacts = 0;
        public int totalNodes = 0;
        public int totalEdges = 0;
        public int totalGateways = 0;
        public int totalSubprocessCalls = 0;
        public int totalScriptTasks = 0;
        public int totalUserTasks = 0;
        public int totalScriptLines = 0;
        public int totalVariables = 0;

        // Maximums
        public int maxNestingDepth = 0;
        public int maxCyclomaticComplexity = 0;

        // Averages
        public double avgComplexityScore = 0.0;
        public double avgAiReadinessScore = 0.0;
        public double avgMaintainabilityIndex = 0.0;
        public double avgNodesPerArtifact = 0.0;

        // Quality distribution
        public int highQualityArtifacts = 0;
        public int mediumQualityArtifacts = 0;
        public int lowQualityArtifacts = 0;

        // Internal sums for calculation
        double sumComplexityScore = 0.0;
        double sumAiReadinessScore = 0.0;
        double sumMaintainabilityIndex = 0.0;

        public double getQualityDistributionPercentage(QualityLevel level) {
            if (totalArtifacts == 0) return 0.0;

            int count = 0;
            switch (level) {
                case HIGH: count = highQualityArtifacts; break;
                case MEDIUM: count = mediumQualityArtifacts; break;
                case LOW: case VERY_LOW: count = lowQualityArtifacts; break;
            }

            return (double) count / totalArtifacts * 100.0;
        }

        @Override
        public String toString() {
            return String.format("AggregatedMetrics{artifacts=%d, avgComplexity=%.1f, avgAI-Ready=%.1f, highQuality=%d}",
                    totalArtifacts, avgComplexityScore, avgAiReadinessScore, highQualityArtifacts);
        }
    }

    /**
     * Complete metrics report with individual and aggregated metrics.
     */
    public static class ProcessMetricsReport {
        private final List<ArtifactMetrics> artifactMetrics = new ArrayList<>();
        private AggregatedMetrics aggregatedMetrics;
        private final Date generatedAt = new Date();

        public void addArtifactMetrics(ArtifactMetrics metrics) {
            artifactMetrics.add(metrics);
        }

        public void setAggregatedMetrics(AggregatedMetrics aggregated) {
            this.aggregatedMetrics = aggregated;
        }

        // Getters
        public List<ArtifactMetrics> getArtifactMetrics() { return Collections.unmodifiableList(artifactMetrics); }
        public AggregatedMetrics getAggregatedMetrics() { return aggregatedMetrics; }
        public Date getGeneratedAt() { return generatedAt; }

        /**
         * Gets the top N most complex artifacts.
         */
        public List<ArtifactMetrics> getMostComplexArtifacts(int limit) {
            return artifactMetrics.stream()
                    .sorted((a, b) -> Double.compare(b.overallComplexityScore, a.overallComplexityScore))
                    .limit(limit)
                    .collect(java.util.stream.Collectors.toList());
        }

        /**
         * Gets the top N least AI-ready artifacts.
         */
        public List<ArtifactMetrics> getLeastAiReadyArtifacts(int limit) {
            return artifactMetrics.stream()
                    .sorted((a, b) -> Double.compare(a.aiReadinessScore, b.aiReadinessScore))
                    .limit(limit)
                    .collect(java.util.stream.Collectors.toList());
        }

        /**
         * Gets artifacts that need immediate attention.
         */
        public List<ArtifactMetrics> getProblematicArtifacts() {
            return artifactMetrics.stream()
                    .filter(a -> a.getComplexityLevel() == ComplexityLevel.VERY_HIGH ||
                            a.getQualityLevel() == QualityLevel.VERY_LOW)
                    .collect(java.util.stream.Collectors.toList());
        }

        /**
         * Generates an executive summary.
         */
        public ExecutiveSummary generateExecutiveSummary() {
            return new ExecutiveSummary(this);
        }

        @Override
        public String toString() {
            return String.format("ProcessMetricsReport{artifacts=%d, generated=%s, summary=%s}",
                    artifactMetrics.size(), generatedAt, aggregatedMetrics);
        }
    }

    /**
     * Executive-level summary for management reports.
     */
    public static class ExecutiveSummary {
        private final ProcessMetricsReport report;
        private final OverallAssessment overallAssessment;
        private final List<String> keyFindings = new ArrayList<>();
        private final List<String> recommendations = new ArrayList<>();

        public ExecutiveSummary(ProcessMetricsReport report) {
            this.report = report;
            this.overallAssessment = calculateOverallAssessment();
            generateKeyFindings();
            generateRecommendations();
        }

        private OverallAssessment calculateOverallAssessment() {
            if (report.getAggregatedMetrics() == null) return OverallAssessment.UNKNOWN;

            AggregatedMetrics metrics = report.getAggregatedMetrics();
            double avgAiReadiness = metrics.avgAiReadinessScore;
            double avgComplexity = metrics.avgComplexityScore;

            boolean highQuality = avgAiReadiness >= 70;
            boolean lowComplexity = avgComplexity <= 50;

            if (highQuality && lowComplexity) return OverallAssessment.EXCELLENT;
            if (highQuality || lowComplexity) return OverallAssessment.GOOD;
            if (avgAiReadiness >= 50 && avgComplexity <= 70) return OverallAssessment.FAIR;
            return OverallAssessment.NEEDS_IMPROVEMENT;
        }

        private void generateKeyFindings() {
            AggregatedMetrics metrics = report.getAggregatedMetrics();
            if (metrics == null) return;

            keyFindings.add(String.format("Process portfolio contains %d artifacts with an average AI readiness score of %.1f%%",
                    metrics.totalArtifacts, metrics.avgAiReadinessScore));

            keyFindings.add(String.format("%.1f%% of artifacts are high quality, %.1f%% are medium quality, %.1f%% need improvement",
                    metrics.getQualityDistributionPercentage(QualityLevel.HIGH),
                    metrics.getQualityDistributionPercentage(QualityLevel.MEDIUM),
                    metrics.getQualityDistributionPercentage(QualityLevel.LOW)));

            if (metrics.maxNestingDepth > 3) {
                keyFindings.add(String.format("Maximum process nesting depth of %d levels detected - exceeds recommended limit",
                        metrics.maxNestingDepth));
            }

            if (metrics.avgComplexityScore > 70) {
                keyFindings.add("High average complexity score indicates processes may be difficult to maintain and understand");
            }

            List<ArtifactMetrics> problematic = report.getProblematicArtifacts();
            if (!problematic.isEmpty()) {
                keyFindings.add(String.format("%d artifacts require immediate attention due to very high complexity or very low quality",
                        problematic.size()));
            }
        }

        private void generateRecommendations() {
            AggregatedMetrics metrics = report.getAggregatedMetrics();
            if (metrics == null) return;

            // AI readiness recommendations
            if (metrics.avgAiReadinessScore < 70) {
                recommendations.add("Standardize process naming to English and improve documentation coverage");
            }

            // Complexity recommendations
            if (metrics.avgComplexityScore > 60) {
                recommendations.add("Break down complex processes into smaller, more manageable sub-processes");
            }

            // Nesting recommendations
            if (metrics.maxNestingDepth > 3) {
                recommendations.add("Reduce process nesting depth by flattening hierarchical structures");
            }

            // Script recommendations
            if (metrics.totalScriptLines > metrics.totalArtifacts * 50) {
                recommendations.add("Consider externalizing complex JavaScript logic to dedicated services");
            }

            // Quality recommendations
            if (metrics.highQualityArtifacts < metrics.totalArtifacts * 0.5) {
                recommendations.add("Implement process governance standards to improve overall quality");
            }

            // General recommendations
            recommendations.add("Establish regular process review cycles to maintain quality and reduce technical debt");
        }

        // Getters
        public OverallAssessment getOverallAssessment() { return overallAssessment; }
        public List<String> getKeyFindings() { return Collections.unmodifiableList(keyFindings); }
        public List<String> getRecommendations() { return Collections.unmodifiableList(recommendations); }
        public ProcessMetricsReport getReport() { return report; }

        @Override
        public String toString() {
            return String.format("ExecutiveSummary{assessment=%s, findings=%d, recommendations=%d}",
                    overallAssessment, keyFindings.size(), recommendations.size());
        }
    }

    // ========== ENUMS ==========

    public enum ComplexityLevel {
        LOW("Low"), MEDIUM("Medium"), HIGH("High"), VERY_HIGH("Very High");

        private final String displayName;
        ComplexityLevel(String displayName) { this.displayName = displayName; }
        @Override public String toString() { return displayName; }
    }

    public enum QualityLevel {
        VERY_LOW("Very Low"), LOW("Low"), MEDIUM("Medium"), HIGH("High");

        private final String displayName;
        QualityLevel(String displayName) { this.displayName = displayName; }
        @Override public String toString() { return displayName; }
    }

    public enum OverallAssessment {
        EXCELLENT("Excellent - Ready for AI processing"),
        GOOD("Good - Minor improvements needed"),
        FAIR("Fair - Moderate improvements needed"),
        NEEDS_IMPROVEMENT("Needs Improvement - Significant work required"),
        UNKNOWN("Unknown - Insufficient data");

        private final String description;
        OverallAssessment(String description) { this.description = description; }
        @Override public String toString() { return description; }
    }
}