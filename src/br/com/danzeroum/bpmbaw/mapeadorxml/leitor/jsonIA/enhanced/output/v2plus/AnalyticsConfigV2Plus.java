package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.*;

/**
 * Analytics Config V2+ - Configuração de Analytics IA-Friendly
 *
 * @version 2.1.0
 * @since V2+ IA-Friendly Migration
 */
@JsonPropertyOrder({"kpis", "metrics", "dashboards", "reports", "metadata"})
public class AnalyticsConfigV2Plus {

    @JsonProperty("kpis")
    private List<KPI> kpis;

    @JsonProperty("metrics")
    private List<ProcessMetric> metrics;

    @JsonProperty("dashboards")
    private List<Dashboard> dashboards;

    @JsonProperty("reports")
    private List<AnalyticsReport> reports;

    @JsonProperty("metadata")
    private AnalyticsMetadata metadata;

    public AnalyticsConfigV2Plus() {
        this.kpis = new ArrayList<KPI>();
        this.metrics = new ArrayList<ProcessMetric>();
        this.dashboards = new ArrayList<Dashboard>();
        this.reports = new ArrayList<AnalyticsReport>();
        this.metadata = new AnalyticsMetadata();
    }

    // Getters and Setters
    public List<KPI> getKpis() { return kpis; }
    public void setKpis(List<KPI> kpis) {
        this.kpis = kpis != null ? kpis : new ArrayList<KPI>();
    }

    public List<ProcessMetric> getMetrics() { return metrics; }
    public void setMetrics(List<ProcessMetric> metrics) {
        this.metrics = metrics != null ? metrics : new ArrayList<ProcessMetric>();
    }

    public List<Dashboard> getDashboards() { return dashboards; }
    public void setDashboards(List<Dashboard> dashboards) {
        this.dashboards = dashboards != null ? dashboards : new ArrayList<Dashboard>();
    }

    public List<AnalyticsReport> getReports() { return reports; }
    public void setReports(List<AnalyticsReport> reports) {
        this.reports = reports != null ? reports : new ArrayList<AnalyticsReport>();
    }

    public AnalyticsMetadata getMetadata() { return metadata; }
    public void setMetadata(AnalyticsMetadata metadata) {
        this.metadata = metadata != null ? metadata : new AnalyticsMetadata();
    }

    public static class KPI {
        public String id;
        public String name;
        public String description;
        public String formula;
        public String unit;
        public double target;
        public double threshold;
        public KPIType type;
    }

    public static class ProcessMetric {
        public String name;
        public String description;
        public MetricType type;
        public String aggregation;
        public String timeframe;
        public Map<String, Object> configuration = new HashMap<String, Object>();
    }

    public static class Dashboard {
        public String id;
        public String name;
        public String description;
        public List<String> widgets = new ArrayList<String>();
        public String layout;
        public boolean isDefault = false;
    }

    public static class AnalyticsReport {
        public String id;
        public String name;
        public String description;
        public ReportType type;
        public String schedule;
        public List<String> recipients = new ArrayList<String>();
    }

    public static class AnalyticsMetadata {
        public String version = "2.1.0";
        public boolean enabled = true;
        public String dataRetention = "2years";
        public String timezone = "America/Sao_Paulo";
        public Map<String, Object> customProperties = new HashMap<String, Object>();
    }

    public enum KPIType {
        PERFORMANCE, QUALITY, EFFICIENCY, COMPLIANCE, SATISFACTION
    }

    public enum MetricType {
        COUNTER, GAUGE, HISTOGRAM, TIMER, RATE
    }

    public enum ReportType {
        OPERATIONAL, EXECUTIVE, COMPLIANCE, PERFORMANCE, TREND_ANALYSIS
    }

    @Override
    public String toString() {
        return String.format("AnalyticsConfigV2Plus{kpis=%d, metrics=%d, dashboards=%d, reports=%d}",
                kpis.size(), metrics.size(), dashboards.size(), reports.size());
    }
}