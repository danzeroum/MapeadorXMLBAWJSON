package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Business Context V2 - Enhanced business information
 */
public class BusinessContextV2 {

    @JsonProperty("$id")
    private String id;

    @JsonProperty("schemaVersion")
    private String schemaVersion = "2.0.0";

    private String businessDomain;
    private String processOwner;
    private List<String> stakeholders;
    private BusinessMetrics metrics;
    private ComplianceInfo compliance;
    private Map<String, Object> customAttributes;

    public BusinessContextV2() {
        this.stakeholders = new ArrayList<>();
        this.customAttributes = new HashMap<>();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(String schemaVersion) { this.schemaVersion = schemaVersion; }

    public String getBusinessDomain() { return businessDomain; }
    public void setBusinessDomain(String businessDomain) { this.businessDomain = businessDomain; }

    public String getProcessOwner() { return processOwner; }
    public void setProcessOwner(String processOwner) { this.processOwner = processOwner; }

    public List<String> getStakeholders() { return stakeholders; }
    public void setStakeholders(List<String> stakeholders) {
        this.stakeholders = stakeholders != null ? stakeholders : new ArrayList<>();
    }

    public BusinessMetrics getMetrics() { return metrics; }
    public void setMetrics(BusinessMetrics metrics) { this.metrics = metrics; }

    public ComplianceInfo getCompliance() { return compliance; }
    public void setCompliance(ComplianceInfo compliance) { this.compliance = compliance; }

    public Map<String, Object> getCustomAttributes() { return customAttributes; }
    public void setCustomAttributes(Map<String, Object> customAttributes) {
        this.customAttributes = customAttributes != null ? customAttributes : new HashMap<>();
    }

    public static class BusinessMetrics {
        private double volumePerDay;
        private double averageExecutionTime;
        private double successRate;
        private String criticality;

        // Getters and setters
        public double getVolumePerDay() { return volumePerDay; }
        public void setVolumePerDay(double volumePerDay) { this.volumePerDay = volumePerDay; }

        public double getAverageExecutionTime() { return averageExecutionTime; }
        public void setAverageExecutionTime(double averageExecutionTime) { this.averageExecutionTime = averageExecutionTime; }

        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }

        public String getCriticality() { return criticality; }
        public void setCriticality(String criticality) { this.criticality = criticality; }
    }

    public static class ComplianceInfo {
        private List<String> regulations;
        private List<String> certifications;
        private String auditFrequency;

        public ComplianceInfo() {
            this.regulations = new ArrayList<>();
            this.certifications = new ArrayList<>();
        }

        // Getters and setters
        public List<String> getRegulations() { return regulations; }
        public void setRegulations(List<String> regulations) {
            this.regulations = regulations != null ? regulations : new ArrayList<>();
        }

        public List<String> getCertifications() { return certifications; }
        public void setCertifications(List<String> certifications) {
            this.certifications = certifications != null ? certifications : new ArrayList<>();
        }

        public String getAuditFrequency() { return auditFrequency; }
        public void setAuditFrequency(String auditFrequency) { this.auditFrequency = auditFrequency; }
    }
}