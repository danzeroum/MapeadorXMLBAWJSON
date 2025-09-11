package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.*;

/**
 * Security Config V2+ - Configuração de Segurança IA-Friendly
 *
 * @version 2.1.0
 * @since V2+ IA-Friendly Migration
 */
@JsonPropertyOrder({"policies", "permissions", "dataClassification", "auditConfig", "metadata"})
public class SecurityConfigV2Plus {

    @JsonProperty("policies")
    private List<SecurityPolicy> policies;

    @JsonProperty("permissions")
    private List<Permission> permissions;

    @JsonProperty("dataClassification")
    private DataClassification dataClassification;

    @JsonProperty("auditConfig")
    private AuditConfiguration auditConfig;

    @JsonProperty("metadata")
    private SecurityMetadata metadata;

    public SecurityConfigV2Plus() {
        this.policies = new ArrayList<SecurityPolicy>();
        this.permissions = new ArrayList<Permission>();
        this.dataClassification = new DataClassification();
        this.auditConfig = new AuditConfiguration();
        this.metadata = new SecurityMetadata();
    }

    // Getters and Setters
    public List<SecurityPolicy> getPolicies() { return policies; }
    public void setPolicies(List<SecurityPolicy> policies) {
        this.policies = policies != null ? policies : new ArrayList<SecurityPolicy>();
    }

    public List<Permission> getPermissions() { return permissions; }
    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions != null ? permissions : new ArrayList<Permission>();
    }

    public DataClassification getDataClassification() { return dataClassification; }
    public void setDataClassification(DataClassification dataClassification) {
        this.dataClassification = dataClassification != null ? dataClassification : new DataClassification();
    }

    public AuditConfiguration getAuditConfig() { return auditConfig; }
    public void setAuditConfig(AuditConfiguration auditConfig) {
        this.auditConfig = auditConfig != null ? auditConfig : new AuditConfiguration();
    }

    public SecurityMetadata getMetadata() { return metadata; }
    public void setMetadata(SecurityMetadata metadata) {
        this.metadata = metadata != null ? metadata : new SecurityMetadata();
    }

    public static class SecurityPolicy {
        public String id;
        public String name;
        public String description;
        public PolicyType type;
        public boolean enforced = true;
        public Map<String, Object> configuration = new HashMap<String, Object>();
    }

    public static class Permission {
        public String resource;
        public String action;
        public List<String> roles = new ArrayList<String>();
        public Map<String, String> conditions = new HashMap<String, String>();
    }

    public static class DataClassification {
        public Map<String, DataSensitivity> fieldClassification = new HashMap<String, DataSensitivity>();
        public DataSensitivity defaultClassification = DataSensitivity.INTERNAL;
    }

    public static class AuditConfiguration {
        public boolean enabled = true;
        public List<String> auditedEvents = new ArrayList<String>();
        public String retentionPeriod = "7years";
        public boolean includeDataChanges = true;
    }

    public static class SecurityMetadata {
        public String version = "2.1.0";
        public String lastReview;
        public String reviewer;
        public SecurityLevel securityLevel = SecurityLevel.STANDARD;
        public Map<String, Object> customProperties = new HashMap<String, Object>();
    }

    public enum PolicyType {
        ACCESS_CONTROL, DATA_PROTECTION, ENCRYPTION, AUTHENTICATION, AUTHORIZATION
    }

    public enum DataSensitivity {
        PUBLIC, INTERNAL, CONFIDENTIAL, RESTRICTED
    }

    public enum SecurityLevel {
        BASIC, STANDARD, HIGH, CRITICAL
    }

    @Override
    public String toString() {
        return String.format("SecurityConfigV2Plus{policies=%d, permissions=%d, level=%s}",
                policies.size(), permissions.size(), metadata.securityLevel);
    }
}