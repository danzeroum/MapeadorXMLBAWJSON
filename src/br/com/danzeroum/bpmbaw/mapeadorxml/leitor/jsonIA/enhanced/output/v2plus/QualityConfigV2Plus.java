package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.*;

/**
 * Quality Config V2+ - Configuração de Qualidade IA-Friendly
 *
 * @version 2.1.0
 * @since V2+ IA-Friendly Migration
 */
@JsonPropertyOrder({"rules", "metrics", "thresholds", "checks", "metadata"})
public class QualityConfigV2Plus {

    @JsonProperty("rules")
    private List<QualityRule> rules;

    @JsonProperty("metrics")
    private QualityMetrics metrics;

    @JsonProperty("thresholds")
    private QualityThresholds thresholds;

    @JsonProperty("checks")
    private List<QualityCheck> checks;

    @JsonProperty("metadata")
    private QualityMetadata metadata;

    public QualityConfigV2Plus() {
        this.rules = new ArrayList<QualityRule>();
        this.metrics = new QualityMetrics();
        this.thresholds = new QualityThresholds();
        this.checks = new ArrayList<QualityCheck>();
        this.metadata = new QualityMetadata();
    }

    // Getters and Setters
    public List<QualityRule> getRules() { return rules; }
    public void setRules(List<QualityRule> rules) {
        this.rules = rules != null ? rules : new ArrayList<QualityRule>();
    }

    public QualityMetrics getMetrics() { return metrics; }
    public void setMetrics(QualityMetrics metrics) {
        this.metrics = metrics != null ? metrics : new QualityMetrics();
    }

    public QualityThresholds getThresholds() { return thresholds; }
    public void setThresholds(QualityThresholds thresholds) {
        this.thresholds = thresholds != null ? thresholds : new QualityThresholds();
    }

    public List<QualityCheck> getChecks() { return checks; }
    public void setChecks(List<QualityCheck> checks) {
        this.checks = checks != null ? checks : new ArrayList<QualityCheck>();
    }

    public QualityMetadata getMetadata() { return metadata; }
    public void setMetadata(QualityMetadata metadata) {
        this.metadata = metadata != null ? metadata : new QualityMetadata();
    }

    public static class QualityRule {
        public String id;
        public String name;
        public String description;
        public String category;
        public QualitySeverity severity;
        public boolean enabled = true;
    }

    public static class QualityMetrics {
        public double complexity = 0.0;
        public double maintainability = 0.0;
        public double testability = 0.0;
        public double performance = 0.0;
        public double security = 0.0;
    }

    public static class QualityThresholds {
        public double maxComplexity = 10.0;
        public double minMaintainability = 8.0;
        public double minTestability = 7.0;
        public double minPerformance = 8.0;
        public double minSecurity = 9.0;
    }

    public static class QualityCheck {
        public String name;
        public String description;
        public boolean passed;
        public String details;
        public double score;
    }

    public static class QualityMetadata {
        public String version = "2.1.0";
        public String lastChecked;
        public double overallScore = 0.0;
        public Map<String, Object> customProperties = new HashMap<String, Object>();
    }

    public enum QualitySeverity {
        INFO, WARNING, ERROR, CRITICAL
    }

    @Override
    public String toString() {
        return String.format("QualityConfigV2Plus{rules=%d, checks=%d, score=%.2f}",
                rules.size(), checks.size(), metadata.overallScore);
    }
}