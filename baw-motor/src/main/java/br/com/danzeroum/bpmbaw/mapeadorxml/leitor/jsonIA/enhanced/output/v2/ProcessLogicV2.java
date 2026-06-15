package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Process Logic V2 - Enhanced with provenance and validation
 * Versão melhorada com rastreabilidade completa e políticas de execução
 */
public class ProcessLogicV2 {

    @JsonProperty("$id")
    private String id;

    @JsonProperty("schemaVersion")
    private String schemaVersion = "2.0.0";

    private List<LogicScriptV2> scripts;
    private List<ValidationRuleV2> validations;
    private List<TransformationRule> transformations;
    private LogicMetadata metadata;
    private ExecutionPolicies executionPolicies;

    public ProcessLogicV2() {
        this.scripts = new ArrayList<>();
        this.validations = new ArrayList<>();
        this.transformations = new ArrayList<>();
    }

    // Factory method para criar com validação
    public static ProcessLogicV2 create(String processId) {
        ProcessLogicV2 logic = new ProcessLogicV2();
        logic.setId(generateLogicId(processId));
        logic.metadata = new LogicMetadata();
        logic.executionPolicies = new ExecutionPolicies();
        return logic;
    }

    private static String generateLogicId(String processId) {
        if (processId == null || processId.trim().isEmpty()) {
            throw new IllegalArgumentException("Process ID cannot be null or empty");
        }
        String normalized = processId.toLowerCase().replaceAll("[^a-z0-9]", "-");
        return "urn:pv:logic:" + normalized + ":2";
    }

    // Validation methods
    public boolean isValid() {
        return id != null && !id.trim().isEmpty() &&
                scripts != null && validations != null;
    }

    // Getters and setters with validation
    public String getId() { return id; }
    public void setId(String id) {
        if (id != null && !id.matches("^urn:pv:logic:[a-z0-9-]+:[0-9]+$")) {
            throw new IllegalArgumentException("Invalid logic ID format. Must follow URN pattern: urn:pv:logic:{name}:{version}");
        }
        this.id = id;
    }

    public String getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(String schemaVersion) {
        if (schemaVersion != null && !schemaVersion.matches("[0-9]+\\.[0-9]+\\.[0-9]+")) {
            throw new IllegalArgumentException("Schema version must follow semantic versioning");
        }
        this.schemaVersion = schemaVersion;
    }

    public List<LogicScriptV2> getScripts() { return scripts; }
    public void setScripts(List<LogicScriptV2> scripts) {
        this.scripts = scripts != null ? scripts : new ArrayList<>();
    }

    public List<ValidationRuleV2> getValidations() { return validations; }
    public void setValidations(List<ValidationRuleV2> validations) {
        this.validations = validations != null ? validations : new ArrayList<>();
    }

    public List<TransformationRule> getTransformations() { return transformations; }
    public void setTransformations(List<TransformationRule> transformations) {
        this.transformations = transformations != null ? transformations : new ArrayList<>();
    }

    public LogicMetadata getMetadata() { return metadata; }
    public void setMetadata(LogicMetadata metadata) { this.metadata = metadata; }

    public ExecutionPolicies getExecutionPolicies() { return executionPolicies; }
    public void setExecutionPolicies(ExecutionPolicies executionPolicies) { this.executionPolicies = executionPolicies; }

    // Helper methods
    public void addScript(LogicScriptV2 script) {
        if (script == null) {
            throw new IllegalArgumentException("Script cannot be null");
        }
        this.scripts.add(script);
    }

    public void addValidation(ValidationRuleV2 validation) {
        if (validation == null) {
            throw new IllegalArgumentException("Validation cannot be null");
        }
        this.validations.add(validation);
    }

    public void addTransformation(TransformationRule transformation) {
        if (transformation == null) {
            throw new IllegalArgumentException("Transformation cannot be null");
        }
        this.transformations.add(transformation);
    }

    /**
     * Logic Script V2 - Enhanced with provenance and execution policies
     */
    public static class LogicScriptV2 {
        @JsonProperty("$id")
        private String id;

        private String name;
        private String description;
        private ScriptLanguage language;
        private String content;
        private String checksum;
        private ScriptProvenance provenance;
        private ExecutionContext executionContext;
        private List<String> dependencies;
        private Map<String, Object> parameters;

        public LogicScriptV2() {
            this.dependencies = new ArrayList<>();
            this.parameters = new HashMap<>();
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public ScriptLanguage getLanguage() { return language; }
        public void setLanguage(ScriptLanguage language) { this.language = language; }

        public String getContent() { return content; }
        public void setContent(String content) {
            this.content = content;
            // Auto-calculate checksum when content changes
            if (content != null) {
                this.checksum = calculateChecksum(content);
            }
        }

        public String getChecksum() { return checksum; }
        public void setChecksum(String checksum) { this.checksum = checksum; }

        public ScriptProvenance getProvenance() { return provenance; }
        public void setProvenance(ScriptProvenance provenance) { this.provenance = provenance; }

        public ExecutionContext getExecutionContext() { return executionContext; }
        public void setExecutionContext(ExecutionContext executionContext) { this.executionContext = executionContext; }

        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) {
            this.dependencies = dependencies != null ? dependencies : new ArrayList<>();
        }

        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters != null ? parameters : new HashMap<>();
        }

        private String calculateChecksum(String content) {
            // Simplified checksum calculation - in real implementation use SHA-256
            return "sha256-" + Integer.toHexString(content.hashCode());
        }
    }

    /**
     * Validation Rule V2 - Enhanced with severity and actions
     */
    public static class ValidationRuleV2 {
        @JsonProperty("$id")
        private String id;

        private String name;
        private String description;
        private String condition;
        private ValidationSeverity severity;
        private String errorMessage;
        private List<ValidationAction> actions;
        private RuleMetadata metadata;

        public ValidationRuleV2() {
            this.actions = new ArrayList<>();
            this.severity = ValidationSeverity.ERROR;
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }

        public ValidationSeverity getSeverity() { return severity; }
        public void setSeverity(ValidationSeverity severity) { this.severity = severity; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public List<ValidationAction> getActions() { return actions; }
        public void setActions(List<ValidationAction> actions) {
            this.actions = actions != null ? actions : new ArrayList<>();
        }

        public RuleMetadata getMetadata() { return metadata; }
        public void setMetadata(RuleMetadata metadata) { this.metadata = metadata; }
    }

    /**
     * Transformation Rule - Data transformation logic
     */
    public static class TransformationRule {
        @JsonProperty("$id")
        private String id;

        private String name;
        private String description;
        private String sourceField;
        private String targetField;
        private TransformationType type;
        private String expression;
        private Map<String, Object> parameters;

        public TransformationRule() {
            this.parameters = new HashMap<>();
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getSourceField() { return sourceField; }
        public void setSourceField(String sourceField) { this.sourceField = sourceField; }

        public String getTargetField() { return targetField; }
        public void setTargetField(String targetField) { this.targetField = targetField; }

        public TransformationType getType() { return type; }
        public void setType(TransformationType type) { this.type = type; }

        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }

        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters != null ? parameters : new HashMap<>();
        }
    }

    // Enums
    public enum ScriptLanguage {
        JAVASCRIPT("JavaScript"),
        JAVA("Java"),
        GROOVY("Groovy"),
        PYTHON("Python"),
        RULE_ENGINE("RuleEngine"),
        SQL("SQL"),
        XPATH("XPath"),
        JSONPATH("JSONPath");

        private final String value;

        ScriptLanguage(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum ValidationSeverity {
        INFO, WARNING, ERROR, CRITICAL
    }

    public enum TransformationType {
        MAPPING, CALCULATION, FORMATTING, VALIDATION, ENRICHMENT
    }

    public enum ValidationAction {
        LOG, REJECT, CORRECT, NOTIFY, ESCALATE
    }

    // Supporting classes
    public static class ScriptProvenance {
        private String sourceFile;
        private String extractionMethod;
        private String originalLocation;
        private String author;
        private String lastModified;
        private String version;

        // Getters and setters
        public String getSourceFile() { return sourceFile; }
        public void setSourceFile(String sourceFile) { this.sourceFile = sourceFile; }

        public String getExtractionMethod() { return extractionMethod; }
        public void setExtractionMethod(String extractionMethod) { this.extractionMethod = extractionMethod; }

        public String getOriginalLocation() { return originalLocation; }
        public void setOriginalLocation(String originalLocation) { this.originalLocation = originalLocation; }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public String getLastModified() { return lastModified; }
        public void setLastModified(String lastModified) { this.lastModified = lastModified; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }

    public static class ExecutionContext {
        private String environment;
        private Map<String, String> systemProperties;
        private List<String> requiredPermissions;
        private Integer timeoutMs;
        private Integer maxMemoryMb;

        public ExecutionContext() {
            this.systemProperties = new HashMap<>();
            this.requiredPermissions = new ArrayList<>();
        }

        // Getters and setters
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }

        public Map<String, String> getSystemProperties() { return systemProperties; }
        public void setSystemProperties(Map<String, String> systemProperties) {
            this.systemProperties = systemProperties != null ? systemProperties : new HashMap<>();
        }

        public List<String> getRequiredPermissions() { return requiredPermissions; }
        public void setRequiredPermissions(List<String> requiredPermissions) {
            this.requiredPermissions = requiredPermissions != null ? requiredPermissions : new ArrayList<>();
        }

        public Integer getTimeoutMs() { return timeoutMs; }
        public void setTimeoutMs(Integer timeoutMs) { this.timeoutMs = timeoutMs; }

        public Integer getMaxMemoryMb() { return maxMemoryMb; }
        public void setMaxMemoryMb(Integer maxMemoryMb) { this.maxMemoryMb = maxMemoryMb; }
    }

    public static class RuleMetadata {
        private String category;
        private List<String> tags;
        private String owner;
        private String lastTested;
        private Integer priority;

        public RuleMetadata() {
            this.tags = new ArrayList<>();
        }

        // Getters and setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags != null ? tags : new ArrayList<>(); }

        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }

        public String getLastTested() { return lastTested; }
        public void setLastTested(String lastTested) { this.lastTested = lastTested; }

        public Integer getPriority() { return priority; }
        public void setPriority(Integer priority) { this.priority = priority; }
    }

    public static class LogicMetadata {
        private String version;
        private String owner;
        private String lastUpdated;
        private List<String> tags;
        private Map<String, String> customAttributes;
        private LogicStatistics statistics;

        public LogicMetadata() {
            this.tags = new ArrayList<>();
            this.customAttributes = new HashMap<>();
        }

        // Getters and setters
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }

        public String getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags != null ? tags : new ArrayList<>(); }

        public Map<String, String> getCustomAttributes() { return customAttributes; }
        public void setCustomAttributes(Map<String, String> customAttributes) {
            this.customAttributes = customAttributes != null ? customAttributes : new HashMap<>();
        }

        public LogicStatistics getStatistics() { return statistics; }
        public void setStatistics(LogicStatistics statistics) { this.statistics = statistics; }
    }

    public static class LogicStatistics {
        private int totalScripts;
        private int totalValidations;
        private int totalTransformations;
        private Map<String, Integer> scriptsByLanguage;
        private double averageComplexity;

        public LogicStatistics() {
            this.scriptsByLanguage = new HashMap<>();
        }

        // Getters and setters
        public int getTotalScripts() { return totalScripts; }
        public void setTotalScripts(int totalScripts) { this.totalScripts = totalScripts; }

        public int getTotalValidations() { return totalValidations; }
        public void setTotalValidations(int totalValidations) { this.totalValidations = totalValidations; }

        public int getTotalTransformations() { return totalTransformations; }
        public void setTotalTransformations(int totalTransformations) { this.totalTransformations = totalTransformations; }

        public Map<String, Integer> getScriptsByLanguage() { return scriptsByLanguage; }
        public void setScriptsByLanguage(Map<String, Integer> scriptsByLanguage) {
            this.scriptsByLanguage = scriptsByLanguage != null ? scriptsByLanguage : new HashMap<>();
        }

        public double getAverageComplexity() { return averageComplexity; }
        public void setAverageComplexity(double averageComplexity) { this.averageComplexity = averageComplexity; }
    }

    public static class ExecutionPolicies {
        private boolean allowConcurrentExecution;
        private boolean requireAuthentication;
        private boolean auditExecution;
        private List<String> allowedEnvironments;
        private Map<String, Object> securityConstraints;
        private RetryPolicy retryPolicy;

        public ExecutionPolicies() {
            this.allowedEnvironments = new ArrayList<>();
            this.securityConstraints = new HashMap<>();
        }

        // Getters and setters
        public boolean isAllowConcurrentExecution() { return allowConcurrentExecution; }
        public void setAllowConcurrentExecution(boolean allowConcurrentExecution) {
            this.allowConcurrentExecution = allowConcurrentExecution;
        }

        public boolean isRequireAuthentication() { return requireAuthentication; }
        public void setRequireAuthentication(boolean requireAuthentication) {
            this.requireAuthentication = requireAuthentication;
        }

        public boolean isAuditExecution() { return auditExecution; }
        public void setAuditExecution(boolean auditExecution) { this.auditExecution = auditExecution; }

        public List<String> getAllowedEnvironments() { return allowedEnvironments; }
        public void setAllowedEnvironments(List<String> allowedEnvironments) {
            this.allowedEnvironments = allowedEnvironments != null ? allowedEnvironments : new ArrayList<>();
        }

        public Map<String, Object> getSecurityConstraints() { return securityConstraints; }
        public void setSecurityConstraints(Map<String, Object> securityConstraints) {
            this.securityConstraints = securityConstraints != null ? securityConstraints : new HashMap<>();
        }

        public RetryPolicy getRetryPolicy() { return retryPolicy; }
        public void setRetryPolicy(RetryPolicy retryPolicy) { this.retryPolicy = retryPolicy; }
    }

    public static class RetryPolicy {
        private int maxRetries;
        private long retryDelayMs;
        private boolean exponentialBackoff;
        private List<String> retryableExceptions;

        public RetryPolicy() {
            this.retryableExceptions = new ArrayList<>();
        }

        // Getters and setters
        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

        public long getRetryDelayMs() { return retryDelayMs; }
        public void setRetryDelayMs(long retryDelayMs) { this.retryDelayMs = retryDelayMs; }

        public boolean isExponentialBackoff() { return exponentialBackoff; }
        public void setExponentialBackoff(boolean exponentialBackoff) { this.exponentialBackoff = exponentialBackoff; }

        public List<String> getRetryableExceptions() { return retryableExceptions; }
        public void setRetryableExceptions(List<String> retryableExceptions) {
            this.retryableExceptions = retryableExceptions != null ? retryableExceptions : new ArrayList<>();
        }
    }
}