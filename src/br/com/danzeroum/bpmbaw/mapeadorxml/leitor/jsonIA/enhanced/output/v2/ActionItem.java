package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.ArrayList;
import java.util.List;

/**
 * Action Item - Specific improvement recommendations
 */
public class ActionItem {
    private String id;
    private String title;
    private String description;
    private ActionPriority priority;
    private ActionCategory category;
    private List<String> affectedElements;
    private String expectedImpact;
    private String implementation;
    private ActionMetadata metadata;

    public ActionItem() {
        this.affectedElements = new ArrayList<>();
        this.priority = ActionPriority.MEDIUM;
    }

    // Factory methods
    public static ActionItem createHighPriority(String title, String description, String expectedImpact) {
        ActionItem action = new ActionItem();
        action.setTitle(title);
        action.setDescription(description);
        action.setPriority(ActionPriority.HIGH);
        action.setExpectedImpact(expectedImpact);
        return action;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ActionPriority getPriority() { return priority; }
    public void setPriority(ActionPriority priority) { this.priority = priority; }

    public ActionCategory getCategory() { return category; }
    public void setCategory(ActionCategory category) { this.category = category; }

    public List<String> getAffectedElements() { return affectedElements; }
    public void setAffectedElements(List<String> affectedElements) {
        this.affectedElements = affectedElements != null ? affectedElements : new ArrayList<>();
    }

    public String getExpectedImpact() { return expectedImpact; }
    public void setExpectedImpact(String expectedImpact) { this.expectedImpact = expectedImpact; }

    public String getImplementation() { return implementation; }
    public void setImplementation(String implementation) { this.implementation = implementation; }

    public ActionMetadata getMetadata() { return metadata; }
    public void setMetadata(ActionMetadata metadata) { this.metadata = metadata; }

    public enum ActionPriority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum ActionCategory {
        STRUCTURE, DOCUMENTATION, SECURITY, PERFORMANCE, COMPLIANCE, MAINTAINABILITY
    }

    public static class ActionMetadata {
        private String estimatedEffort;
        private String skillsRequired;
        private List<String> dependencies;
        private String targetVersion;

        public ActionMetadata() {
            this.dependencies = new ArrayList<>();
        }

        // Getters and setters
        public String getEstimatedEffort() { return estimatedEffort; }
        public void setEstimatedEffort(String estimatedEffort) { this.estimatedEffort = estimatedEffort; }

        public String getSkillsRequired() { return skillsRequired; }
        public void setSkillsRequired(String skillsRequired) { this.skillsRequired = skillsRequired; }

        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) {
            this.dependencies = dependencies != null ? dependencies : new ArrayList<>();
        }

        public String getTargetVersion() { return targetVersion; }
        public void setTargetVersion(String targetVersion) { this.targetVersion = targetVersion; }
    }
}