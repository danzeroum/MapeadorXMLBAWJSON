package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTypeDefinitionV2 {
    private String id; // URN format: urn:pv:dt:recondicionamento:1
    private String name; // camelCase padronizado
    private String version;
    private String description;
    private String shortDescription;
    private Map<String, Object> schema; // JSON Schema Draft 2020-12
    private String modelChecksum;
    private List<String> tags;
    private DataLineage lineage;

    public DataTypeDefinitionV2() {
    }

    // Factory method para criar com validação
    public static DataTypeDefinitionV2 createCanonical(String canonicalId, String typeName) {
        DataTypeDefinitionV2 dt = new DataTypeDefinitionV2();

        // Gerar ID estável no formato URN
        String cleanId = canonicalId.replace("canonical-", "");
        dt.id = String.format("urn:pv:dt:%s:1", cleanId);

        // Nome em camelCase
        dt.name = toCamelCase(typeName);
        dt.version = "1.0.0";
        dt.description = String.format("Business object: %s", typeName);
        dt.shortDescription = typeName;

        // Schema base JSON Schema Draft 2020-12 - CORRIGIDO PARA JAVA 8
        Map<String, Object> schema = new HashMap<>();
        schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
        schema.put("type", "object");
        schema.put("description", dt.description);
        schema.put("properties", new HashMap<>());
        schema.put("required", new ArrayList<>());
        schema.put("additionalProperties", false);
        dt.schema = schema;

        return dt;
    }
    private static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) return input;

        // Convert to camelCase
        String[] words = input.split("[\\s_-]+");
        StringBuilder result = new StringBuilder(words[0].toLowerCase());

        for (int i = 1; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                result.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase());
            }
        }

        return result.toString();
    }

    // Validation method
    public void validate() {
        if (!isValidUrn(id)) {
            throw new IllegalArgumentException("ID must be valid URN format");
        }
        if (!isValidSemanticVersion(version)) {
            throw new IllegalArgumentException("Version must follow semantic versioning");
        }
        if (!isCamelCase(name)) {
            throw new IllegalArgumentException("Name must be camelCase");
        }
    }

    private boolean isValidUrn(String urn) {
        return urn != null && urn.matches("^urn:pv:dt:[a-z][a-zA-Z0-9]*:[0-9]+$");
    }

    private boolean isValidSemanticVersion(String version) {
        return version != null && version.matches("^[0-9]+\\.[0-9]+\\.[0-9]+$");
    }

    private boolean isCamelCase(String name) {
        return name != null && name.matches("^[a-z][a-zA-Z0-9]*$");
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (!isValidUrn(id)) {
            throw new IllegalArgumentException("Invalid URN format");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!isCamelCase(name)) {
            throw new IllegalArgumentException("Name must be camelCase");
        }
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        if (!isValidSemanticVersion(version)) {
            throw new IllegalArgumentException("Invalid semantic version");
        }
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Map<String, Object> getSchema() {
        return schema;
    }

    public void setSchema(Map<String, Object> schema) {
        this.schema = schema;
    }

    public String getModelChecksum() {
        return modelChecksum;
    }

    public void setModelChecksum(String modelChecksum) {
        this.modelChecksum = modelChecksum;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public DataLineage getLineage() {
        return lineage;
    }

    public void setLineage(DataLineage lineage) {
        this.lineage = lineage;
    }

    public static class DataLineage {
        private String sourceArtifact;
        private String derivedFrom;
        private List<String> dependencies;
        private String lastModified;

        // Getters and setters
        public String getSourceArtifact() {
            return sourceArtifact;
        }

        public void setSourceArtifact(String sourceArtifact) {
            this.sourceArtifact = sourceArtifact;
        }

        public String getDerivedFrom() {
            return derivedFrom;
        }

        public void setDerivedFrom(String derivedFrom) {
            this.derivedFrom = derivedFrom;
        }

        public List<String> getDependencies() {
            return dependencies;
        }

        public void setDependencies(List<String> dependencies) {
            this.dependencies = dependencies;
        }

        public String getLastModified() {
            return lastModified;
        }

        public void setLastModified(String lastModified) {
            this.lastModified = lastModified;
        }
    }
}
