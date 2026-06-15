package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Process Domain V2 - Enhanced with URN IDs and validation
 * Versão melhorada com identificadores estáveis e estrutura rigorosa
 */
public class ProcessDomainV2 {

    @JsonProperty("$id")
    private String id;

    @JsonProperty("schemaVersion")
    private String schemaVersion = "2.0.0";

    private String name;
    private String description;
    private String category;
    private Map<String, String> glossary;
    private List<StructuredBusinessRuleV2> businessRules;
    private List<ProcessEnumV2> enums;
    private DomainMetadata metadata;

    public ProcessDomainV2() {
        this.glossary = new HashMap<>();
        this.businessRules = new ArrayList<>();
        this.enums = new ArrayList<>();
    }

    // Factory method para criar com validação
    public static ProcessDomainV2 create(String name, String description) {
        ProcessDomainV2 domain = new ProcessDomainV2();
        domain.setId(generateDomainId(name));
        domain.setName(name);
        domain.setDescription(description);
        domain.metadata = new DomainMetadata();
        return domain;
    }

    private static String generateDomainId(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Domain name cannot be null or empty");
        }
        String normalized = name.toLowerCase().replaceAll("[^a-z0-9]", "-");
        return "urn:pv:domain:" + normalized + ":2";
    }

    // Validation methods
    public boolean isValid() {
        return id != null && !id.trim().isEmpty() &&
                name != null && !name.trim().isEmpty() &&
                description != null && !description.trim().isEmpty();
    }

    // Getters and setters with validation
    public String getId() { return id; }
    public void setId(String id) {
        if (id != null && !id.matches("^urn:pv:domain:[a-z0-9-]+:[0-9]+$")) {
            throw new IllegalArgumentException("Invalid domain ID format. Must follow URN pattern: urn:pv:domain:{name}:{version}");
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

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Domain name cannot be null or empty");
        }
        this.name = name;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Map<String, String> getGlossary() { return glossary; }
    public void setGlossary(Map<String, String> glossary) {
        this.glossary = glossary != null ? glossary : new HashMap<>();
    }

    public List<StructuredBusinessRuleV2> getBusinessRules() { return businessRules; }
    public void setBusinessRules(List<StructuredBusinessRuleV2> businessRules) {
        this.businessRules = businessRules != null ? businessRules : new ArrayList<>();
    }

    public List<ProcessEnumV2> getEnums() { return enums; }
    public void setEnums(List<ProcessEnumV2> enums) {
        this.enums = enums != null ? enums : new ArrayList<>();
    }

    public DomainMetadata getMetadata() { return metadata; }
    public void setMetadata(DomainMetadata metadata) { this.metadata = metadata; }

    // Helper methods
    public void addGlossaryTerm(String term, String definition) {
        if (term == null || definition == null) {
            throw new IllegalArgumentException("Term and definition cannot be null");
        }
        this.glossary.put(term, definition);
    }

    public void addBusinessRule(StructuredBusinessRuleV2 rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Business rule cannot be null");
        }
        this.businessRules.add(rule);
    }

    public void addEnum(ProcessEnumV2 enumDef) {
        if (enumDef == null) {
            throw new IllegalArgumentException("Enum definition cannot be null");
        }
        this.enums.add(enumDef);
    }

    /**
     * Structured Business Rule V2 - Enhanced with provenance
     */
    public static class StructuredBusinessRuleV2 {
        @JsonProperty("$id")
        private String id;

        private String name;
        private String description;
        private String condition;
        private String action;
        private RulePriority priority;
        private List<String> tags;
        private RuleProvenance provenance;

        public StructuredBusinessRuleV2() {
            this.tags = new ArrayList<>();
            this.priority = RulePriority.MEDIUM;
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

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }

        public RulePriority getPriority() { return priority; }
        public void setPriority(RulePriority priority) { this.priority = priority; }

        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags != null ? tags : new ArrayList<>(); }

        public RuleProvenance getProvenance() { return provenance; }
        public void setProvenance(RuleProvenance provenance) { this.provenance = provenance; }
    }

    /**
     * Process Enum V2 - Enhanced with metadata
     */
    public static class ProcessEnumV2 {
        @JsonProperty("$id")
        private String id;

        private String name;
        private String description;
        private List<EnumValue> values;
        private EnumMetadata metadata;

        public ProcessEnumV2() {
            this.values = new ArrayList<>();
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public List<EnumValue> getValues() { return values; }
        public void setValues(List<EnumValue> values) { this.values = values != null ? values : new ArrayList<>(); }

        public EnumMetadata getMetadata() { return metadata; }
        public void setMetadata(EnumMetadata metadata) { this.metadata = metadata; }

        public static class EnumValue {
            private String key;
            private String value;
            private String description;
            private boolean deprecated;

            // Getters and setters
            public String getKey() { return key; }
            public void setKey(String key) { this.key = key; }

            public String getValue() { return value; }
            public void setValue(String value) { this.value = value; }

            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }

            public boolean isDeprecated() { return deprecated; }
            public void setDeprecated(boolean deprecated) { this.deprecated = deprecated; }
        }

        public static class EnumMetadata {
            private String dataType;
            private boolean extensible;
            private String version;

            // Getters and setters
            public String getDataType() { return dataType; }
            public void setDataType(String dataType) { this.dataType = dataType; }

            public boolean isExtensible() { return extensible; }
            public void setExtensible(boolean extensible) { this.extensible = extensible; }

            public String getVersion() { return version; }
            public void setVersion(String version) { this.version = version; }
        }
    }

    /**
     * Rule Priority Enum
     */
    public enum RulePriority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    /**
     * Rule Provenance - tracks rule origin
     */
    public static class RuleProvenance {
        private String source;
        private String author;
        private String lastModified;
        private String extractionMethod;

        // Getters and setters
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public String getLastModified() { return lastModified; }
        public void setLastModified(String lastModified) { this.lastModified = lastModified; }

        public String getExtractionMethod() { return extractionMethod; }
        public void setExtractionMethod(String extractionMethod) { this.extractionMethod = extractionMethod; }
    }

    /**
     * Domain Metadata
     */
    public static class DomainMetadata {
        private String version;
        private String owner;
        private String lastUpdated;
        private List<String> tags;
        private Map<String, String> customAttributes;

        public DomainMetadata() {
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
    }
}