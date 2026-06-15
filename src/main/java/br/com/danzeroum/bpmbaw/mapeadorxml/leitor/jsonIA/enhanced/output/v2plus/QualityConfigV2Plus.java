package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

/**
 * Quality Configuration V2+ - Regras de qualidade e lint rules
 *
 * @version 2.1.0
 * @since V2+ IA-Friendly Migration
 */
public class QualityConfigV2Plus {

    @JsonProperty("id")
    private String id;

    @JsonProperty("enabled")
    private boolean enabled = true;

    @JsonProperty("level")
    private String level = "STANDARD";

    @JsonProperty("metrics")
    private List<QualityMetricV2Plus> metrics;

    @JsonProperty("rules")
    private List<QualityRuleV2Plus> rules;

    @JsonProperty("thresholds")
    private Map<String, Double> thresholds;

    @JsonProperty("metadata")
    private QualityMetadata metadata;

    public QualityConfigV2Plus() {
        this.metrics = new ArrayList<QualityMetricV2Plus>();
        this.rules = new ArrayList<QualityRuleV2Plus>();
        this.thresholds = new HashMap<String, Double>();
        this.metadata = new QualityMetadata();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public List<QualityMetricV2Plus> getMetrics() { return metrics; }
    public void setMetrics(List<QualityMetricV2Plus> metrics) {
        this.metrics = metrics != null ? metrics : new ArrayList<QualityMetricV2Plus>();
    }

    public List<QualityRuleV2Plus> getRules() { return rules; }
    public void setRules(List<QualityRuleV2Plus> rules) {
        this.rules = rules != null ? rules : new ArrayList<QualityRuleV2Plus>();
    }

    public Map<String, Double> getThresholds() { return thresholds; }
    public void setThresholds(Map<String, Double> thresholds) {
        this.thresholds = thresholds != null ? thresholds : new HashMap<String, Double>();
    }

    public QualityMetadata getMetadata() { return metadata; }
    public void setMetadata(QualityMetadata metadata) {
        this.metadata = metadata != null ? metadata : new QualityMetadata();
    }

    /**
     * Métrica de qualidade V2Plus
     */
    public static class QualityMetricV2Plus {
        private String name;
        private boolean enabled = true;
        private double threshold;
        private String description;
        private QualityLevel level = QualityLevel.INFO;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public double getThreshold() { return threshold; }
        public void setThreshold(double threshold) { this.threshold = threshold; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public QualityLevel getLevel() { return level; }
        public void setLevel(QualityLevel level) { this.level = level; }
    }

    /**
     * Regra de qualidade V2Plus
     */
    public static class QualityRuleV2Plus {
        private String id;
        private String name;
        private String description;
        private boolean enabled = true;
        private QualityLevel severity = QualityLevel.WARNING;
        private Map<String, Object> configuration;

        public QualityRuleV2Plus() {
            this.configuration = new HashMap<String, Object>();
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public QualityLevel getSeverity() { return severity; }
        public void setSeverity(QualityLevel severity) { this.severity = severity; }

        public Map<String, Object> getConfiguration() { return configuration; }
        public void setConfiguration(Map<String, Object> configuration) {
            this.configuration = configuration != null ? configuration : new HashMap<String, Object>();
        }
    }

    /**
     * Metadados de qualidade V2Plus
     */
    public static class QualityMetadata {
        public String version = "2.1.0";
        public String lastReview;
        public String reviewer;
        public Map<String, Object> customProperties = new HashMap<String, Object>();
    }

    /**
     * Níveis de qualidade
     */
    public enum QualityLevel {
        INFO, WARNING, ERROR, CRITICAL
    }

    @Override
    public String toString() {
        return String.format("QualityConfigV2Plus{enabled=%s, level=%s, metrics=%d, rules=%d}",
                enabled, level, metrics.size(), rules.size());
    }
}
