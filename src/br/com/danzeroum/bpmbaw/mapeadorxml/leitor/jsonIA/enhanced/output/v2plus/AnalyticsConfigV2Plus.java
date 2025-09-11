package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

/**
 * Analytics Configuration V2+ - KPIs e métricas de analytics
 *
 * @version 2.1.0
 * @since V2+ IA-Friendly Migration
 */
public class AnalyticsConfigV2Plus {

    @JsonProperty("id")
    private String id;

    @JsonProperty("enabled")
    private boolean enabled = true;

    @JsonProperty("level")
    private String level = "BASIC";

    @JsonProperty("enabledMetrics")
    private List<String> enabledMetrics;

    @JsonProperty("kpis")
    private List<KPIDefinitionV2Plus> kpis;

    @JsonProperty("dashboards")
    private List<DashboardConfigV2Plus> dashboards;

    @JsonProperty("retention")
    private RetentionPolicyV2Plus retention;

    @JsonProperty("metadata")
    private AnalyticsMetadata metadata;

    public AnalyticsConfigV2Plus() {
        this.enabledMetrics = new ArrayList<String>();
        this.kpis = new ArrayList<KPIDefinitionV2Plus>();
        this.dashboards = new ArrayList<DashboardConfigV2Plus>();
        this.retention = new RetentionPolicyV2Plus();
        this.metadata = new AnalyticsMetadata();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public List<String> getEnabledMetrics() { return enabledMetrics; }
    public void setEnabledMetrics(List<String> enabledMetrics) {
        this.enabledMetrics = enabledMetrics != null ? enabledMetrics : new ArrayList<String>();
    }

    public List<KPIDefinitionV2Plus> getKpis() { return kpis; }
    public void setKpis(List<KPIDefinitionV2Plus> kpis) {
        this.kpis = kpis != null ? kpis : new ArrayList<KPIDefinitionV2Plus>();
    }

    public List<DashboardConfigV2Plus> getDashboards() { return dashboards; }
    public void setDashboards(List<DashboardConfigV2Plus> dashboards) {
        this.dashboards = dashboards != null ? dashboards : new ArrayList<DashboardConfigV2Plus>();
    }

    public RetentionPolicyV2Plus getRetention() { return retention; }
    public void setRetention(RetentionPolicyV2Plus retention) {
        this.retention = retention != null ? retention : new RetentionPolicyV2Plus();
    }

    public AnalyticsMetadata getMetadata() { return metadata; }
    public void setMetadata(AnalyticsMetadata metadata) {
        this.metadata = metadata != null ? metadata : new AnalyticsMetadata();
    }

    /**
     * Definição de KPI V2Plus
     */
    public static class KPIDefinitionV2Plus {
        private String id;
        private String name;
        private String description;
        private String metric;
        private String aggregation;
        private Map<String, Object> targets;

        public KPIDefinitionV2Plus() {
            this.targets = new HashMap<String, Object>();
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getMetric() { return metric; }
        public void setMetric(String metric) { this.metric = metric; }

        public String getAggregation() { return aggregation; }
        public void setAggregation(String aggregation) { this.aggregation = aggregation; }

        public Map<String, Object> getTargets() { return targets; }
        public void setTargets(Map<String, Object> targets) {
            this.targets = targets != null ? targets : new HashMap<String, Object>();
        }
    }

    /**
     * Configuração de Dashboard V2Plus
     */
    public static class DashboardConfigV2Plus {
        private String id;
        private String name;
        private String type;
        private List<String> widgets;
        private Map<String, Object> layout;

        public DashboardConfigV2Plus() {
            this.widgets = new ArrayList<String>();
            this.layout = new HashMap<String, Object>();
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public List<String> getWidgets() { return widgets; }
        public void setWidgets(List<String> widgets) {
            this.widgets = widgets != null ? widgets : new ArrayList<String>();
        }

        public Map<String, Object> getLayout() { return layout; }
        public void setLayout(Map<String, Object> layout) {
            this.layout = layout != null ? layout : new HashMap<String, Object>();
        }
    }

    /**
     * Política de retenção V2Plus
     */
    public static class RetentionPolicyV2Plus {
        private String period = "1year";
        private boolean autoArchive = true;
        private String archiveLocation;
        private Map<String, String> rules;

        public RetentionPolicyV2Plus() {
            this.rules = new HashMap<String, String>();
        }

        // Getters and Setters
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }

        public boolean isAutoArchive() { return autoArchive; }
        public void setAutoArchive(boolean autoArchive) { this.autoArchive = autoArchive; }

        public String getArchiveLocation() { return archiveLocation; }
        public void setArchiveLocation(String archiveLocation) { this.archiveLocation = archiveLocation; }

        public Map<String, String> getRules() { return rules; }
        public void setRules(Map<String, String> rules) {
            this.rules = rules != null ? rules : new HashMap<String, String>();
        }
    }

    /**
     * Metadados de analytics V2Plus
     */
    public static class AnalyticsMetadata {
        public String version = "2.1.0";
        public String provider;
        public String lastSync;
        public Map<String, Object> customProperties = new HashMap<String, Object>();
    }

    @Override
    public String toString() {
        return String.format("AnalyticsConfigV2Plus{enabled=%s, level=%s, metrics=%d, kpis=%d}",
                enabled, level, enabledMetrics.size(), kpis.size());
    }
}