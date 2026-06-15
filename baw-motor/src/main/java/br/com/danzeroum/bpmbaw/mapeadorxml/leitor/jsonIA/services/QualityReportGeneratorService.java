package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.BawAnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;

import java.util.*;

/**
 * Generates quality reports for AI analysis optimization.
 * Identifies structural issues, naming inconsistencies, and complexity problems.
 */
public class QualityReportGeneratorService {

    private final VariableEnricherService variableEnricher;

    public QualityReportGeneratorService(VariableEnricherService variableEnricher) {
        this.variableEnricher = variableEnricher;
    }

    /**
     * Generates a comprehensive quality report for AI interpretation.
     */
    public QualityAnalysisReport generateQualityReport(JsonReportV2 report) {
        QualityAnalysisReport qualityReport = new QualityAnalysisReport();

        if (report.getArtifacts() == null || report.getArtifacts().isEmpty()) {
            qualityReport.addIssue(QualitySeverity.HIGH, "NO_ARTIFACTS",
                    "No artifacts found for analysis");
            return qualityReport;
        }

        // Analyze each artifact
        for (JsonReportV2.Artifact artifact : report.getArtifacts()) {
            analyzeArtifactQuality(artifact, qualityReport);
        }

        // Generate overall recommendations
        generateRecommendations(qualityReport);

        return qualityReport;
    }

    /**
     * Analyzes quality issues in a single artifact.
     */
    private void analyzeArtifactQuality(JsonReportV2.Artifact artifact, QualityAnalysisReport report) {
        String artifactId = artifact.getId();

        // 1. Naming consistency analysis
        analyzeNamingConsistency(artifact, report);

        // 2. Structural complexity analysis
        analyzeStructuralComplexity(artifact, report);

        // 3. Script quality analysis
        analyzeScriptQuality(artifact, report);

        // 4. Variable mapping analysis
        analyzeVariableMappings(artifact, report);

        // 5. Flow complexity analysis
        analyzeFlowComplexity(artifact, report);
    }

    /**
     * Analyzes naming consistency issues.
     */
    private void analyzeNamingConsistency(JsonReportV2.Artifact artifact, QualityAnalysisReport report) {
        // Check artifact name
        if (hasPortugueseCharacters(artifact.getName())) {
            report.addIssue(QualitySeverity.MEDIUM, "MIXED_LANGUAGE_ARTIFACT_NAME",
                    String.format("Artifact '%s' contains Portuguese characters", artifact.getName()));
        }

        // Check flow step names
        if (artifact.getFlow() != null) {
            for (JsonReportV2.FlowStep step : artifact.getFlow()) {
                if (hasPortugueseCharacters(step.getName())) {
                    report.addIssue(QualitySeverity.LOW, "MIXED_LANGUAGE_STEP_NAME",
                            String.format("Step '%s' in artifact '%s' contains Portuguese characters",
                                    step.getName(), artifact.getName()));
                }
            }
        }

        // Check variable names
        if (artifact.getVariables() != null) {
            checkVariableNaming(artifact.getVariables().getInput(), "input", artifact.getName(), report);
            checkVariableNaming(artifact.getVariables().getOutput(), "output", artifact.getName(), report);
        }
    }

    /**
     * Analyzes structural complexity issues.
     */
    private void analyzeStructuralComplexity(JsonReportV2.Artifact artifact, QualityAnalysisReport report) {
        if (artifact.getRootView() == null) return;

        int maxDepth = 0;
        int subprocessCount = 0;
        int totalSteps = artifact.getRootView().size();

        for (JsonReportV2.FlowStep step : artifact.getRootView()) {
            if (step.getSubflowDepth() > maxDepth) {
                maxDepth = step.getSubflowDepth();
            }
            if (step.getCalledArtifactId() != null && !step.getCalledArtifactId().trim().isEmpty()) {
                subprocessCount++;
            }
        }

        // Check complexity thresholds
        if (maxDepth > 3) {
            report.addIssue(QualitySeverity.HIGH, "EXCESSIVE_NESTING",
                    String.format("Artifact '%s' has excessive nesting depth: %d levels",
                            artifact.getName(), maxDepth));
        }

        if (totalSteps > 50) {
            report.addIssue(QualitySeverity.MEDIUM, "LARGE_PROCESS",
                    String.format("Artifact '%s' has %d steps, consider breaking into smaller processes",
                            artifact.getName(), totalSteps));
        }

        if (subprocessCount > 10) {
            report.addIssue(QualitySeverity.MEDIUM, "MANY_SUBPROCESSES",
                    String.format("Artifact '%s' calls %d subprocesses, consider consolidation",
                            artifact.getName(), subprocessCount));
        }
    }

    /**
     * Analyzes script quality issues.
     */
    private void analyzeScriptQuality(JsonReportV2.Artifact artifact, QualityAnalysisReport report) {
        if (artifact.getFlow() == null) return;

        for (JsonReportV2.FlowStep step : artifact.getFlow()) {
            if (step.getScript() != null && !step.getScript().trim().isEmpty()) {
                analyzeScript(step.getScript(), step.getName(), artifact.getName(), report);
            }
        }

        // Use the VariableEnricherService for deeper script analysis
        VariableEnricherService.ScriptAnalysisSummary scriptAnalysis =
                variableEnricher.analyzeEmbeddedScripts(artifact);

        if (scriptAnalysis.hasIssues()) {
            for (String problematicScript : scriptAnalysis.getProblematicScripts()) {
                report.addIssue(QualitySeverity.MEDIUM, "COMPLEX_EMBEDDED_SCRIPT",
                        problematicScript + " in artifact: " + artifact.getName());
            }

            for (String mixedVar : scriptAnalysis.getMixedLanguageVariables()) {
                report.addIssue(QualitySeverity.LOW, "MIXED_LANGUAGE_VARIABLE",
                        String.format("Variable '%s' uses mixed language in artifact '%s'",
                                mixedVar, artifact.getName()));
            }
        }
    }

    /**
     * Analyzes variable mapping quality.
     */
    private void analyzeVariableMappings(JsonReportV2.Artifact artifact, QualityAnalysisReport report) {
        if (artifact.getFlow() == null) return;

        for (JsonReportV2.FlowStep step : artifact.getFlow()) {
            if (step.getParameterMapping() != null) {
                analyzeParameterMapping(step.getParameterMapping(), step.getName(),
                        artifact.getName(), report);
            }
        }
    }

    /**
     * Analyzes flow complexity (gateways, conditions, etc.).
     */
    private void analyzeFlowComplexity(JsonReportV2.Artifact artifact, QualityAnalysisReport report) {
        if (artifact.getGraph() == null) return;

        // Count gateways
        int gatewayCount = artifact.getGraph().getGateways() != null ?
                artifact.getGraph().getGateways().size() : 0;

        // Count complex conditions
        int complexConditionCount = 0;
        if (artifact.getGraph().getConditions() != null) {
            for (JsonReportV2.EdgeCondition condition : artifact.getGraph().getConditions()) {
                if (isComplexCondition(condition.getExpression())) {
                    complexConditionCount++;
                }
            }
        }

        if (gatewayCount > 5) {
            report.addIssue(QualitySeverity.MEDIUM, "MANY_GATEWAYS",
                    String.format("Artifact '%s' has %d gateways, consider simplifying decision logic",
                            artifact.getName(), gatewayCount));
        }

        if (complexConditionCount > 3) {
            report.addIssue(QualitySeverity.MEDIUM, "COMPLEX_CONDITIONS",
                    String.format("Artifact '%s' has %d complex conditions, consider externalizing logic",
                            artifact.getName(), complexConditionCount));
        }
    }

    /**
     * Generates AI-specific recommendations based on identified issues.
     */
    private void generateRecommendations(QualityAnalysisReport report) {
        Map<String, Integer> issueCounts = report.getIssueCountsByType();

        // Language consistency recommendations
        if (issueCounts.getOrDefault("MIXED_LANGUAGE_ARTIFACT_NAME", 0) > 0 ||
                issueCounts.getOrDefault("MIXED_LANGUAGE_STEP_NAME", 0) > 0) {
            report.addRecommendation("STANDARDIZE_NAMING",
                    "Standardize all process and step names to English for better AI interpretation");
        }

        // Complexity recommendations
        if (issueCounts.getOrDefault("EXCESSIVE_NESTING", 0) > 0) {
            report.addRecommendation("REDUCE_NESTING",
                    "Break down deeply nested processes into smaller, focused sub-processes");
        }

        // Script recommendations
        if (issueCounts.getOrDefault("COMPLEX_EMBEDDED_SCRIPT", 0) > 0) {
            report.addRecommendation("EXTERNALIZE_LOGIC",
                    "Move complex JavaScript logic to separate service tasks or external services");
        }

        // Variable recommendations
        if (issueCounts.getOrDefault("MIXED_LANGUAGE_VARIABLE", 0) > 0) {
            report.addRecommendation("STANDARDIZE_VARIABLES",
                    "Use consistent English naming for all variables and parameters");
        }
    }

    // ========== HELPER METHODS ==========

    private boolean hasPortugueseCharacters(String text) {
        if (text == null) return false;
        return text.matches(".*[àáâãäåèéêëìíîïòóôõöùúûüçñÀÁÂÃÄÅÈÉÊËÌÍÎÏÒÓÔÕÖÙÚÛÜÇÑ].*");
    }

    private void checkVariableNaming(List<JsonReportV2.VariableInfo> variables, String type,
                                     String artifactName, QualityAnalysisReport report) {
        if (variables == null) return;

        for (JsonReportV2.VariableInfo var : variables) {
            if (hasPortugueseCharacters(var.getName())) {
                report.addIssue(QualitySeverity.LOW, "MIXED_LANGUAGE_VARIABLE",
                        String.format("%s variable '%s' in artifact '%s' contains Portuguese characters",
                                type, var.getName(), artifactName));
            }
        }
    }

    private void analyzeScript(String script, String stepName, String artifactName,
                               QualityAnalysisReport report) {
        // Check script length
        if (script.length() > 500) {
            report.addIssue(QualitySeverity.MEDIUM, "LONG_SCRIPT",
                    String.format("Step '%s' in artifact '%s' has a long script (%d chars)",
                            stepName, artifactName, script.length()));
        }

        // Check complexity indicators
        String[] complexityIndicators = {"!=", "&&", "||", "for(", "while(", "if(.*){.*}.*else"};
        int complexityScore = 0;
        for (String indicator : complexityIndicators) {
            if (script.contains(indicator)) {
                complexityScore++;
            }
        }

        if (complexityScore > 3) {
            report.addIssue(QualitySeverity.MEDIUM, "COMPLEX_SCRIPT_LOGIC",
                    String.format("Step '%s' in artifact '%s' has complex script logic",
                            stepName, artifactName));
        }
    }

    private void analyzeParameterMapping(JsonReportV2.ParameterMapping mapping, String stepName,
                                         String artifactName, QualityAnalysisReport report) {
        // Check for unclear mappings
        if (mapping.getInput() != null) {
            for (JsonReportV2.Mapping inputMapping : mapping.getInput()) {
                if (isUnclearMapping(inputMapping)) {
                    report.addIssue(QualitySeverity.LOW, "UNCLEAR_PARAMETER_MAPPING",
                            String.format("Unclear input mapping in step '%s' of artifact '%s': %s -> %s",
                                    stepName, artifactName, inputMapping.getSource(), inputMapping.getTarget()));
                }
            }
        }
    }

    private boolean isComplexCondition(String expression) {
        if (expression == null) return false;
        return expression.length() > 100 ||
                expression.contains("&&") && expression.contains("||") ||
                expression.split("\\(").length > 3;
    }

    private boolean isUnclearMapping(JsonReportV2.Mapping mapping) {
        if (mapping.getSource() == null || mapping.getTarget() == null) return true;

        // Check for very different naming patterns
        String source = mapping.getSource().toLowerCase();
        String target = mapping.getTarget().toLowerCase();

        return !source.contains(target.substring(0, Math.min(3, target.length()))) &&
                !target.contains(source.substring(0, Math.min(3, source.length())));
    }

    // ========== RESULT CLASSES ==========

    /**
     * Represents the overall quality analysis report.
     */
    public static class QualityAnalysisReport {
        private final List<QualityIssue> issues = new ArrayList<>();
        private final List<String> recommendations = new ArrayList<>();
        private final Map<String, Integer> issueCounts = new HashMap<>();

        public void addIssue(QualitySeverity severity, String type, String description) {
            issues.add(new QualityIssue(severity, type, description));
            issueCounts.merge(type, 1, Integer::sum);
        }

        public void addRecommendation(String type, String recommendation) {
            recommendations.add(type + ": " + recommendation);
        }

        public List<QualityIssue> getIssues() { return Collections.unmodifiableList(issues); }
        public List<String> getRecommendations() { return Collections.unmodifiableList(recommendations); }
        public Map<String, Integer> getIssueCountsByType() { return Collections.unmodifiableMap(issueCounts); }

        public boolean hasHighSeverityIssues() {
            return issues.stream().anyMatch(issue -> issue.getSeverity() == QualitySeverity.HIGH);
        }

        public QualityScore calculateOverallScore() {
            int highCount = (int) issues.stream().filter(i -> i.getSeverity() == QualitySeverity.HIGH).count();
            int mediumCount = (int) issues.stream().filter(i -> i.getSeverity() == QualitySeverity.MEDIUM).count();
            int lowCount = (int) issues.stream().filter(i -> i.getSeverity() == QualitySeverity.LOW).count();

            // Simple scoring algorithm
            int totalDeductions = (highCount * 10) + (mediumCount * 5) + (lowCount * 1);
            int score = Math.max(0, 100 - totalDeductions);

            if (score >= 90) return QualityScore.EXCELLENT;
            if (score >= 75) return QualityScore.GOOD;
            if (score >= 50) return QualityScore.FAIR;
            return QualityScore.POOR;
        }

        @Override
        public String toString() {
            return String.format("QualityReport{issues=%d, recommendations=%d, score=%s}",
                    issues.size(), recommendations.size(), calculateOverallScore());
        }
    }

    /**
     * Represents a single quality issue.
     */
    public static class QualityIssue {
        private final QualitySeverity severity;
        private final String type;
        private final String description;

        public QualityIssue(QualitySeverity severity, String type, String description) {
            this.severity = severity;
            this.type = type;
            this.description = description;
        }

        public QualitySeverity getSeverity() { return severity; }
        public String getType() { return type; }
        public String getDescription() { return description; }

        @Override
        public String toString() {
            return String.format("[%s] %s: %s", severity, type, description);
        }
    }

    /**
     * Quality severity levels.
     */
    public enum QualitySeverity {
        LOW("Low"), MEDIUM("Medium"), HIGH("High");

        private final String displayName;
        QualitySeverity(String displayName) { this.displayName = displayName; }
        @Override public String toString() { return displayName; }
    }

    /**
     * Overall quality score.
     */
    public enum QualityScore {
        EXCELLENT("Excellent"), GOOD("Good"), FAIR("Fair"), POOR("Poor");

        private final String displayName;
        QualityScore(String displayName) { this.displayName = displayName; }
        @Override public String toString() { return displayName; }
    }
}