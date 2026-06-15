package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.*;

/**
 * Data Type Definition V2+ - Definição de Tipo IA-Friendly
 *
 * @version 2.1.0
 * @since V2+ IA-Friendly Migration
 */
@JsonPropertyOrder({"id", "name", "jsonSchema", "description", "metadata"})
public class DataTypeDefinitionV2Plus {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("jsonSchema")
    private Map<String, Object> jsonSchema;

    @JsonProperty("description")
    private String description;

    @JsonProperty("metadata")
    private DataTypeMetadata metadata;
    private String baseType;
    private boolean isArray;
    private String type;
    public void setBaseType(String baseType) { this.baseType = baseType; }
    public void setArray(boolean array) { this.isArray = array; }

    public DataTypeDefinitionV2Plus() {
        this.jsonSchema = new HashMap<String, Object>();
        this.metadata = new DataTypeMetadata();
    }

    public DataTypeDefinitionV2Plus(String id, String name, String description) {
        this();
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Map<String, Object> getJsonSchema() { return jsonSchema; }
    public void setJsonSchema(Map<String, Object> jsonSchema) {
        this.jsonSchema = jsonSchema != null ? jsonSchema : new HashMap<String, Object>();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public DataTypeMetadata getMetadata() { return metadata; }
    public void setMetadata(DataTypeMetadata metadata) {
        this.metadata = metadata != null ? metadata : new DataTypeMetadata();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static class DataTypeMetadata {
        public String version = "1.0.0";
        public String createdAt;
        public String author;
        public Map<String, Object> customProperties = new HashMap<String, Object>();
    }

    @Override
    public String toString() {
        return String.format("DataTypeDefinitionV2Plus{id='%s', name='%s'}", id, name);
    }
}