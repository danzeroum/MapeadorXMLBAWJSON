package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SecurityConfigV2 {
    private boolean scriptSandbox;
    private List<String> allowedAPIs;
    private List<String> prohibitedAPIs;
    private List<PIIField> piiFields;
    private ClassificationPolicy classification;
    private AuditConfiguration audit;
    private List<SecurityEvent> events;

    public SecurityConfigV2() {
        this.allowedAPIs = new ArrayList<>();
        this.prohibitedAPIs = new ArrayList<>();
        this.piiFields = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    // Factory method para configuração padrão
    public static SecurityConfigV2 createDefault() {
        SecurityConfigV2 config = new SecurityConfigV2();

        config.scriptSandbox = true;
        config.allowedAPIs = Arrays.asList("Math", "Date", "String", "Number");
        config.prohibitedAPIs = Arrays.asList("eval", "Function", "require", "process", "fs", "child_process");

        // Classificação padrão
        config.classification = new ClassificationPolicy();
        config.classification.setLevel(ClassificationLevel.INTERNAL);
        config.classification.setRationale("Contains business process IP but no customer PII");
        config.classification.setOwner("process-architecture-team");

        // Auditoria padrão
        config.audit = new AuditConfiguration();
        config.audit.setLevel(AuditLevel.FULL);
        config.audit.setRetentionPeriod("P7Y"); // 7 years
        config.audit.setCorrelationId(UUID.randomUUID().toString());

        return config;
    }

    // Método para adicionar campo PII
    public void addPIIField(String fieldPath, PIIType type, String basis, String retention, MaskStrategy strategy) {
        PIIField field = new PIIField();
        field.setFieldPath(fieldPath);
        field.setType(type);
        field.setLegalBasis(basis);
        field.setRetentionPeriod(retention);
        field.setMaskStrategy(strategy);
        field.setRiskScore(calculateRiskScore(type, fieldPath));

        piiFields.add(field);
    }

    private int calculateRiskScore(PIIType type, String fieldPath) {
        // Lógica para calcular score de risco baseado no tipo e contexto
        int baseScore = type.getRiskScore();

        // Ajustar baseado no caminho do campo
        if (fieldPath.contains("fiscal") || fieldPath.contains("ssn")) {
            baseScore += 3;
        }
        if (fieldPath.contains("email") || fieldPath.contains("phone")) {
            baseScore += 2;
        }

        return Math.min(baseScore, 10); // Max 10
    }

    // Método para registrar evento de segurança
    public void addSecurityEvent(String actor, String action, String resource, String result) {
        SecurityEvent event = new SecurityEvent();
        event.setTimestamp(Instant.now().toString());
        event.setActor(actor);
        event.setAction(action);
        event.setResource(resource);
        event.setResult(result);
        event.setCorrelationId(audit != null ? audit.getCorrelationId() : "unknown");

        events.add(event);
    }

    // Getters and setters
    public boolean isScriptSandbox() {
        return scriptSandbox;
    }

    public void setScriptSandbox(boolean scriptSandbox) {
        this.scriptSandbox = scriptSandbox;
    }

    public List<String> getAllowedAPIs() {
        return allowedAPIs;
    }

    public void setAllowedAPIs(List<String> allowedAPIs) {
        this.allowedAPIs = allowedAPIs;
    }

    public List<String> getProhibitedAPIs() {
        return prohibitedAPIs;
    }

    public void setProhibitedAPIs(List<String> prohibitedAPIs) {
        this.prohibitedAPIs = prohibitedAPIs;
    }

    public List<PIIField> getPiiFields() {
        return piiFields;
    }

    public void setPiiFields(List<PIIField> piiFields) {
        this.piiFields = piiFields;
    }

    public ClassificationPolicy getClassification() {
        return classification;
    }

    public void setClassification(ClassificationPolicy classification) {
        this.classification = classification;
    }

    public AuditConfiguration getAudit() {
        return audit;
    }

    public void setAudit(AuditConfiguration audit) {
        this.audit = audit;
    }

    public List<SecurityEvent> getEvents() {
        return events;
    }

    public void setEvents(List<SecurityEvent> events) {
        this.events = events;
    }

    // Classes internas
    public static class PIIField {
        private String fieldPath;
        private PIIType type;
        private String legalBasis;
        private String retentionPeriod;
        private MaskStrategy maskStrategy;
        private int riskScore;

        // Getters and setters
        public String getFieldPath() {
            return fieldPath;
        }

        public void setFieldPath(String fieldPath) {
            this.fieldPath = fieldPath;
        }

        public PIIType getType() {
            return type;
        }

        public void setType(PIIType type) {
            this.type = type;
        }

        public String getLegalBasis() {
            return legalBasis;
        }

        public void setLegalBasis(String legalBasis) {
            this.legalBasis = legalBasis;
        }

        public String getRetentionPeriod() {
            return retentionPeriod;
        }

        public void setRetentionPeriod(String retentionPeriod) {
            this.retentionPeriod = retentionPeriod;
        }

        public MaskStrategy getMaskStrategy() {
            return maskStrategy;
        }

        public void setMaskStrategy(MaskStrategy maskStrategy) {
            this.maskStrategy = maskStrategy;
        }

        public int getRiskScore() {
            return riskScore;
        }

        public void setRiskScore(int riskScore) {
            this.riskScore = riskScore;
        }
    }

    public static class ClassificationPolicy {
        private ClassificationLevel level;
        private String rationale;
        private String owner;
        private String reviewDate;

        // Getters and setters
        public ClassificationLevel getLevel() {
            return level;
        }

        public void setLevel(ClassificationLevel level) {
            this.level = level;
        }

        public String getRationale() {
            return rationale;
        }

        public void setRationale(String rationale) {
            this.rationale = rationale;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getReviewDate() {
            return reviewDate;
        }

        public void setReviewDate(String reviewDate) {
            this.reviewDate = reviewDate;
        }
    }

    public static class AuditConfiguration {
        private AuditLevel level;
        private String retentionPeriod;
        private String correlationId;

        // Getters and setters
        public AuditLevel getLevel() {
            return level;
        }

        public void setLevel(AuditLevel level) {
            this.level = level;
        }

        public String getRetentionPeriod() {
            return retentionPeriod;
        }

        public void setRetentionPeriod(String retentionPeriod) {
            this.retentionPeriod = retentionPeriod;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }
    }

    public static class SecurityEvent {
        private String timestamp;
        private String actor;
        private String action;
        private String resource;
        private String result;
        private String correlationId;

        // Getters and setters
        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getActor() {
            return actor;
        }

        public void setActor(String actor) {
            this.actor = actor;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }
    }

    // Enums
    public enum ClassificationLevel {
        PUBLIC(1),
        INTERNAL(2),
        CONFIDENTIAL(3),
        RESTRICTED(4);

        private final int level;

        ClassificationLevel(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    public enum PIIType {
        FULL_NAME(5),
        EMAIL(4),
        PHONE(4),
        FISCAL_NUMBER(8),
        SSN(9),
        ADDRESS(6),
        DATE_OF_BIRTH(7),
        FINANCIAL_INFO(8);

        private final int riskScore;

        PIIType(int riskScore) {
            this.riskScore = riskScore;
        }

        public int getRiskScore() {
            return riskScore;
        }
    }

    public enum MaskStrategy {
        PSEUDONYMIZE,
        ANONYMIZE,
        REDACT,
        ENCRYPT,
        HASH
    }

    public enum AuditLevel {
        NONE,
        BASIC,
        STANDARD,
        FULL,
        FORENSIC
    }
}
