package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * ProcessVariableV2Plus - Representa UMA variável individual do processo
 *
 * Esta classe representa uma única variável com seus metadados.
 * Para container de múltiplas variáveis, use ProcessVariablesV2Plus.
 *
 * @version 3.0.0 - Corrigida e simplificada
 * @author Enhanced BAW Analysis System
 */
@JsonPropertyOrder({
        "id", "name", "type", "typeRef", "typeId",
        "description", "list", "cardinality", "nullable",
        "defaultValue", "required", "metadata"
})
public class ProcessVariableV2Plus {

    // =========================================================================
    // IDENTIFICAÇÃO
    // =========================================================================

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    // =========================================================================
    // TIPAGEM
    // =========================================================================

    @JsonProperty("type")
    private String type;  // Tipo simples: String, Integer, etc.

    @JsonProperty("typeRef")
    private String typeRef;  // Referência canônica: dt:string@1

    @JsonProperty("typeId")
    private String typeId;  // ID do tipo legado: canonical-recondicionamento

    // =========================================================================
    // ESTRUTURA
    // =========================================================================

    @JsonProperty("list")
    private boolean list = false;

    @JsonProperty("cardinality")
    private String cardinality;  // "one" ou "many"

    @JsonProperty("nullable")
    private Boolean nullable;

    @JsonProperty("required")
    private Boolean required;

    // =========================================================================
    // DOCUMENTAÇÃO
    // =========================================================================

    @JsonProperty("description")
    private String description;

    @JsonProperty("defaultValue")
    private Object defaultValue;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    /**
     * Constructor padrão
     */
    public ProcessVariableV2Plus() {
        this.metadata = new HashMap<String, Object>();
        this.nullable = true;
        this.required = false;
        this.list = false;
    }

    /**
     * Constructor com nome e tipo
     */
    public ProcessVariableV2Plus(String name, String type) {
        this();
        this.name = name;
        this.type = type;
        this.id = "var_" + (name != null ? name.toLowerCase().replace(" ", "_") : "unknown");
    }

    /**
     * Constructor completo
     */
    public ProcessVariableV2Plus(String name, String type, String description, boolean isList) {
        this(name, type);
        this.description = description;
        this.list = isList;
        this.cardinality = isList ? "many" : "one";
    }

    // =========================================================================
    // GETTERS E SETTERS
    // =========================================================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(String typeRef) {
        this.typeRef = typeRef;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
        // Atualizar cardinality automaticamente
        if (this.cardinality == null || this.cardinality.isEmpty()) {
            this.cardinality = list ? "many" : "one";
        }
    }

    public String getCardinality() {
        return cardinality;
    }

    public void setCardinality(String cardinality) {
        this.cardinality = cardinality;
        // Sincronizar com list
        this.list = "many".equals(cardinality);
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
        // Se é required, não pode ser nullable
        if (required != null && required) {
            this.nullable = false;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Map<String, Object> getMetadata() {
        if (metadata == null) {
            metadata = new HashMap<String, Object>();
        }
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    // =========================================================================
    // MÉTODOS DE NEGÓCIO
    // =========================================================================

    /**
     * Normaliza o typeRef baseado no type ou typeId
     */
    public void normalizeTypeRef() {
        if (typeRef != null && !typeRef.isEmpty()) {
            return; // Já tem typeRef
        }

        if (typeId != null && !typeId.isEmpty()) {
            // Converter typeId para typeRef
            if (typeId.startsWith("canonical-")) {
                String typeName = typeId.substring("canonical-".length());
                this.typeRef = "dt:" + typeName + "@1";
            } else {
                this.typeRef = "dt:" + typeId.toLowerCase() + "@1";
            }
        } else if (type != null && !type.isEmpty()) {
            // Converter type simples para typeRef
            switch (type) {
                case "String":
                    this.typeRef = "dt:string@1";
                    break;
                case "Integer":
                    this.typeRef = "dt:integer@1";
                    break;
                case "Boolean":
                    this.typeRef = "dt:boolean@1";
                    break;
                case "Date":
                    this.typeRef = "dt:date@1";
                    break;
                case "Decimal":
                case "Double":
                case "Float":
                    this.typeRef = "dt:decimal@1";
                    break;
                default:
                    this.typeRef = "dt:" + type.toLowerCase() + "@1";
            }
        } else {
            // Tipo desconhecido
            this.typeRef = "dt:any@1";
        }
    }

    /**
     * Define se é uma variável de entrada baseado no nome
     */
    public boolean isInputVariable() {
        if (name == null) return false;
        String lowerName = name.toLowerCase();
        return lowerName.contains("input") ||
                lowerName.contains("entrada") ||
                lowerName.startsWith("in_") ||
                (metadata != null && "input".equals(metadata.get("scope")));
    }

    /**
     * Define se é uma variável de saída baseado no nome
     */
    public boolean isOutputVariable() {
        if (name == null) return false;
        String lowerName = name.toLowerCase();
        return lowerName.contains("output") ||
                lowerName.contains("saida") ||
                lowerName.contains("result") ||
                lowerName.startsWith("out_") ||
                (metadata != null && "output".equals(metadata.get("scope")));
    }

    /**
     * Define se é uma variável privada/local
     */
    public boolean isPrivateVariable() {
        return !isInputVariable() && !isOutputVariable();
    }

    /**
     * Adiciona metadado
     */
    public void addMetadata(String key, Object value) {
        if (metadata == null) {
            metadata = new HashMap<String, Object>();
        }
        metadata.put(key, value);
    }

    // =========================================================================
    // VALIDAÇÃO
    // =========================================================================

    /**
     * Valida se a variável está corretamente configurada
     */
    public boolean validate() {
        // Nome é obrigatório
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // Deve ter tipo ou typeRef
        if ((type == null || type.isEmpty()) &&
                (typeRef == null || typeRef.isEmpty()) &&
                (typeId == null || typeId.isEmpty())) {
            return false;
        }

        // Cardinality deve ser válido se definido
        if (cardinality != null && !cardinality.isEmpty()) {
            if (!"one".equals(cardinality) && !"many".equals(cardinality)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Clona a variável
     */
    public ProcessVariableV2Plus clone() {
        ProcessVariableV2Plus clone = new ProcessVariableV2Plus();
        clone.id = this.id;
        clone.name = this.name;
        clone.type = this.type;
        clone.typeRef = this.typeRef;
        clone.typeId = this.typeId;
        clone.list = this.list;
        clone.cardinality = this.cardinality;
        clone.nullable = this.nullable;
        clone.required = this.required;
        clone.description = this.description;
        clone.defaultValue = this.defaultValue;
        if (this.metadata != null) {
            clone.metadata = new HashMap<String, Object>(this.metadata);
        }
        return clone;
    }

    // =========================================================================
    // MÉTODOS UTILITÁRIOS
    // =========================================================================

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProcessVariableV2Plus{");
        sb.append("name='").append(name).append('\'');
        if (type != null) sb.append(", type='").append(type).append('\'');
        if (typeRef != null) sb.append(", typeRef='").append(typeRef).append('\'');
        sb.append(", list=").append(list);
        if (cardinality != null) sb.append(", cardinality='").append(cardinality).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessVariableV2Plus that = (ProcessVariableV2Plus) o;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        if (id != null) return id.hashCode();
        return name != null ? name.hashCode() : 0;
    }
}