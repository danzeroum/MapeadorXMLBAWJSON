package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.ArrayList;
import java.util.List;

/**
 * Analysis Issue - Problems found during analysis
 */
public class AnalysisIssue {
    private String id;
    private IssueSeverity severity;
    private String category;
    private String title;
    private String description;
    private String location;
    private List<String> affectedElements;
    private String recommendation;
    private IssueMetadata metadata;

    public AnalysisIssue() {
        this.affectedElements = new ArrayList<>();
        this.severity = IssueSeverity.INFO;
    }

    // Factory methods
    public static AnalysisIssue createError(String title, String description, String location) {
        AnalysisIssue issue = new AnalysisIssue();
        issue.setSeverity(IssueSeverity.ERROR);
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setLocation(location);
        return issue;
    }

    public static AnalysisIssue createWarning(String title, String description, String location) {
        AnalysisIssue issue = new AnalysisIssue();
        issue.setSeverity(IssueSeverity.WARNING);
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setLocation(location);
        return issue;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public IssueSeverity getSeverity() { return severity; }
    public void setSeverity(IssueSeverity severity) { this.severity = severity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<String> getAffectedElements() { return affectedElements; }
    public void setAffectedElements(List<String> affectedElements) {
        this.affectedElements = affectedElements != null ? affectedElements : new ArrayList<>();
    }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public IssueMetadata getMetadata() { return metadata; }
    public void setMetadata(IssueMetadata metadata) { this.metadata = metadata; }

    public enum IssueSeverity {
        INFO, WARNING, ERROR, CRITICAL
    }

    public static class IssueMetadata {
        private String detectedAt;
        private String detectedBy;
        private String ruleId;
        private boolean autoFixable;

        // Getters and setters
        public String getDetectedAt() { return detectedAt; }
        public void setDetectedAt(String detectedAt) { this.detectedAt = detectedAt; }

        public String getDetectedBy() { return detectedBy; }
        public void setDetectedBy(String detectedBy) { this.detectedBy = detectedBy; }

        public String getRuleId() { return ruleId; }
        public void setRuleId(String ruleId) { this.ruleId = ruleId; }

        public boolean isAutoFixable() { return autoFixable; }
        public void setAutoFixable(boolean autoFixable) { this.autoFixable = autoFixable; }
    }
}