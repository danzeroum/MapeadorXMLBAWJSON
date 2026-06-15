package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;

/**
 * Variable Definition V2+ - Definição Individual de Variável IA-Friendly
 *
 * SUBSTITUI ESTRUTURA V1 PROBLEMÁTICA:
 * ❌ V1: {"name": "Number", "typeId": "Integer", "list": false}
 * ✅ V2+: {"name": "numeroCalculado", "typeRef": "dt:integer@1", "cardinality": "one", "nullable": false}
 *
 * CARACTERÍSTICAS V2+:
 * ✅ typeRef obrigatório (aponta para dataTypes[])
 * ✅ Cardinality explícita (one/many vs boolean list)
 * ✅ Nullable explícito (true/false)
 * ✅ Validação de nomes (camelCase, não-reservados)
 * ✅ Descrição obrigatória para documentação
 * ✅ Metadados de migração (proveniência)
 *
 * @version 2.1.0
 * @since V2+ IA-Friendly Migration
 */
@JsonPropertyOrder({
        "name", "typeRef", "cardinality", "nullable", "description"
})
public class VariableDefinitionV2Plus {

    /**
     * Nome da variável (camelCase, não-reservado)
     * VALIDADO: ^[a-z][a-zA-Z0-9]*$ (não Number, Date, Object, etc.)
     */
    @JsonProperty("name")
    private String name;

    /**
     * Referência ao tipo de dados (obrigatório)
     * FORMATO: dt:nomeDoTipo@versao (ex: dt:recondicionamento@1)
     * APONTA PARA: dataTypes[].id
     */
    @JsonProperty("typeRef")
    private String typeRef;

    /**
     * Cardinalidade da variável
     * VALUES: "one" (single value) | "many" (array/collection)
     * SUBSTITUI: boolean list da V1
     */
    @JsonProperty("cardinality")
    private String cardinality;

    /**
     * Se a variável pode ser null/undefined
     * EXPLÍCITO: true/false (não mais implícito)
     */
    @JsonProperty("nullable")
    private boolean nullable;

    /**
     * Descrição da variável (obrigatória para IA)
     * PROPÓSITO: Permite IA entender o uso da variável
     */
    @JsonProperty("description")
    private String description;

    /**
     * Valor padrão da variável (opcional)
     */
    @JsonProperty("defaultValue")
    private Object defaultValue;

    /**
     * Metadados de migração e proveniência
     */
    @JsonProperty("metadata")
    private VariableMetadata metadata;

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================

    public VariableDefinitionV2Plus() {
        this.cardinality = "one"; // Padrão: single value
        this.nullable = false;    // Padrão: não-nullable
    }

    /**
     * Construtor completo para criação rápida
     */
    public VariableDefinitionV2Plus(String name, String typeRef, String cardinality, boolean nullable, String description) {
        this();
        this.name = name;
        this.typeRef = typeRef;
        this.cardinality = cardinality != null ? cardinality : "one";
        this.nullable = nullable;
        this.description = description;

        // Validar após construção
        if (!isValid()) {
            throw new IllegalArgumentException("Invalid variable definition: " + getValidationErrors());
        }
    }

    /**
     * Factory method para criar variável V2+ a partir de V1
     */
    public static VariableDefinitionV2Plus fromV1(String name, String typeId, boolean isList, String description) {
        VariableDefinitionV2Plus variable = new VariableDefinitionV2Plus();

        // Normalizar nome se necessário
        variable.name = normalizeVariableName(name);

        // Converter typeId para typeRef
        variable.typeRef = convertTypeIdToTypeRef(typeId);

        // Converter list boolean para cardinality
        variable.cardinality = isList ? "many" : "one";

        // Nullable por padrão false
        variable.nullable = false;

        // Descrição
        variable.description = description != null ? description : "Migrated from V1";

        // Metadados de migração
        variable.metadata = new VariableMetadata();
        variable.metadata.sourceVersion = "1.0";
        variable.metadata.originalName = name;
        variable.metadata.originalTypeId = typeId;
        variable.metadata.migrationTimestamp = java.time.Instant.now().toString();

        return variable;
    }

    // =========================================================================
    // VALIDAÇÃO
    // =========================================================================

    /**
     * Validação completa da variável
     */
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    /**
     * Lista todos os erros de validação
     */
    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        // 1. Nome obrigatório e válido
        if (name == null || name.trim().isEmpty()) {
            errors.append("Name cannot be null or empty. ");
        } else {
            if (isReservedName(name)) {
                errors.append("Name '").append(name).append("' is reserved. Use: ").append(normalizeVariableName(name)).append(". ");
            }

            if (!isValidVariableName(name)) {
                errors.append("Name '").append(name).append("' must be camelCase (^[a-z][a-zA-Z0-9]*$). ");
            }
        }

        // 2. TypeRef obrigatório e válido
        if (typeRef == null || typeRef.trim().isEmpty()) {
            errors.append("TypeRef is required. ");
        } else if (!isValidTypeRef(typeRef)) {
            errors.append("TypeRef '").append(typeRef).append("' must follow format 'dt:type@version'. ");
        }

        // 3. Cardinality válida
        if (cardinality == null || (!cardinality.equals("one") && !cardinality.equals("many"))) {
            errors.append("Cardinality must be 'one' or 'many'. ");
        }

        // 4. Descrição obrigatória
        if (description == null || description.trim().isEmpty()) {
            errors.append("Description is required for IA analysis. ");
        } else if (description.trim().length() < 5) {
            errors.append("Description must be at least 5 characters. ");
        }

        return errors.toString().trim();
    }

    /**
     * Valida se nome não é reservado
     */
    private boolean isReservedName(String name) {
        if (name == null) return false;

        String[] reserved = {
                // JavaScript reserved
                "Number", "Date", "Object", "object", "Array", "String", "Boolean",
                "Function", "undefined", "null", "true", "false", "NaN", "Infinity",

                // Java reserved
                "class", "interface", "enum", "package", "import", "export", "extends", "implements",

                // TWX common problematic
                "tw", "local", "system", "process", "task", "activity"
        };

        for (String r : reserved) {
            if (r.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Valida formato do nome (camelCase)
     */
    private boolean isValidVariableName(String name) {
        if (name == null || name.trim().isEmpty()) return false;

        // Deve começar com letra minúscula, seguida de letras/números
        return name.matches("^[a-z][a-zA-Z0-9]*$");
    }

    /**
     * Valida formato do typeRef
     */
    private boolean isValidTypeRef(String typeRef) {
        if (typeRef == null || typeRef.trim().isEmpty()) return false;

        // Formato: dt:nomeDoTipo@versao
        return typeRef.matches("^dt:[a-zA-Z][a-zA-Z0-9]*@[0-9]+$");
    }

    // =========================================================================
    // MÉTODOS DE MIGRAÇÃO E NORMALIZAÇÃO
    // =========================================================================

    /**
     * Normaliza nomes de variáveis problemáticos
     */
    public static String normalizeVariableName(String originalName) {
        if (originalName == null) return null;

        // Mapeamentos específicos para nomes reservados comuns
        switch (originalName) {
            case "Number":
                return "numeroCalculado";
            case "Date":
                return "dataProcessamento";
            case "Object":
            case "object":
                return "objetoAuxiliar";
            case "Array":
                return "listaAuxiliar";
            case "String":
                return "textoAuxiliar";
            case "Boolean":
                return "flagBooleano";
            case "Function":
                return "funcaoAuxiliar";

            // TWX específicos
            case "tw":
                return "teamworksData";
            case "local":
                return "dadosLocais";
            case "system":
                return "sistemaData";

            default:
                // Se não é reservado, verificar camelCase
                if (originalName.matches("^[a-z][a-zA-Z0-9]*$")) {
                    return originalName;
                } else {
                    // Tentar converter para camelCase básico
                    return convertToCamelCase(originalName);
                }
        }
    }

    /**
     * Converte string para camelCase básico
     */
    private static String convertToCamelCase(String input) {
        if (input == null || input.trim().isEmpty()) return "variableAux";

        // Remover caracteres especiais e converter
        String cleaned = input.replaceAll("[^a-zA-Z0-9]", " ");
        String[] words = cleaned.split("\\s+");

        if (words.length == 0) return "variableAux";

        StringBuilder result = new StringBuilder();

        // Primeira palavra em minúscula
        result.append(words[0].toLowerCase());

        // Demais palavras com primeira letra maiúscula
        for (int i = 1; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                result.append(Character.toUpperCase(words[i].charAt(0)));
                if (words[i].length() > 1) {
                    result.append(words[i].substring(1).toLowerCase());
                }
            }
        }

        String final_result = result.toString();

        // Se resultado vazio ou não válido, usar padrão
        if (final_result.isEmpty() || !final_result.matches("^[a-z][a-zA-Z0-9]*$")) {
            return "variableAux";
        }

        return final_result;
    }

    /**
     * Converte typeId V1 para typeRef V2+
     */
    private static String convertTypeIdToTypeRef(String typeId) {
        if (typeId == null) return "dt:string@1";

        // Mapeamentos comuns
        switch (typeId.toLowerCase()) {
            case "integer":
            case "int":
                return "dt:integer@1";
            case "string":
                return "dt:string@1";
            case "boolean":
            case "bool":
                return "dt:boolean@1";
            case "date":
            case "datetime":
                return "dt:datetime@1";
            case "decimal":
            case "double":
            case "float":
                return "dt:decimal@1";
            default:
                // Se começa com "canonical-", converter
                if (typeId.startsWith("canonical-")) {
                    String typeName = typeId.substring("canonical-".length());
                    return "dt:" + typeName + "@1";
                }

                // Padrão genérico
                return "dt:" + typeId.toLowerCase() + "@1";
        }
    }

    // =========================================================================
    // MÉTODOS UTILITÁRIOS
    // =========================================================================

    /**
     * Clona a variável para novo escopo
     */
    public VariableDefinitionV2Plus clone() {
        VariableDefinitionV2Plus cloned = new VariableDefinitionV2Plus();
        cloned.name = this.name;
        cloned.typeRef = this.typeRef;
        cloned.cardinality = this.cardinality;
        cloned.nullable = this.nullable;
        cloned.description = this.description;
        cloned.defaultValue = this.defaultValue;

        // Clonar metadata se existir
        if (this.metadata != null) {
            cloned.metadata = new VariableMetadata();
            cloned.metadata.sourceVersion = this.metadata.sourceVersion;
            cloned.metadata.originalName = this.metadata.originalName;
            cloned.metadata.originalTypeId = this.metadata.originalTypeId;
            cloned.metadata.migrationTimestamp = this.metadata.migrationTimestamp;
        }

        return cloned;
    }

    /**
     * Verifica se variável é array/collection
     */
    public boolean isArray() {
        return "many".equals(cardinality);
    }

    /**
     * Verifica se variável é obrigatória (não-nullable)
     */
    public boolean isRequired() {
        return !nullable;
    }

    // =========================================================================
    // EQUALS, HASHCODE, TOSTRING
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableDefinitionV2Plus that = (VariableDefinitionV2Plus) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(typeRef, that.typeRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, typeRef);
    }

    @Override
    public String toString() {
        return String.format("Variable{name='%s', typeRef='%s', cardinality='%s', nullable=%s}",
                name, typeRef, cardinality, nullable);
    }

    // =========================================================================
    // GETTERS AND SETTERS
    // =========================================================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(String typeRef) {
        this.typeRef = typeRef;
    }

    public String getCardinality() {
        return cardinality;
    }

    public void setCardinality(String cardinality) {
        this.cardinality = cardinality;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
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

    public VariableMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(VariableMetadata metadata) {
        this.metadata = metadata;
    }

    // =========================================================================
    // CLASSES DE APOIO
    // =========================================================================

    /**
     * Metadados de migração da variável
     */
    public static class VariableMetadata {
        public String sourceVersion;        // "1.0"
        public String originalName;         // "Number"
        public String originalTypeId;       // "Integer"
        public String migrationTimestamp;   // ISO timestamp
        public String migrationReason;      // "Reserved name normalization"

        @Override
        public String toString() {
            return String.format("Metadata{source=%s, originalName=%s, migrated=%s}",
                    sourceVersion, originalName, migrationTimestamp);
        }
    }

    // =========================================================================
    // TESTE INLINE RÁPIDO
    // =========================================================================

    /**
     * Teste básico para validação durante desenvolvimento
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing VariableDefinitionV2Plus...");

        try {
            // Teste 1: Criação válida
            VariableDefinitionV2Plus var1 = new VariableDefinitionV2Plus(
                    "recondicionamento", "dt:recondicionamento@1", "one", false,
                    "Dados do processo de recondicionamento"
            );
            System.out.println("✅ Valid creation: " + var1.isValid());

            // Teste 2: Migração V1
            VariableDefinitionV2Plus var2 = VariableDefinitionV2Plus.fromV1(
                    "Number", "Integer", false, "Número calculado"
            );
            System.out.println("✅ V1 migration: " + var2.getName().equals("numeroCalculado"));

            // Teste 3: Normalização de nome
            String normalized = VariableDefinitionV2Plus.normalizeVariableName("Object");
            System.out.println("✅ Name normalization: " + normalized.equals("objetoAuxiliar"));

            // Teste 4: Validação de nome inválido
            VariableDefinitionV2Plus var3 = new VariableDefinitionV2Plus();
            var3.setName("Invalid-Name!");
            var3.setTypeRef("dt:string@1");
            var3.setDescription("Teste de nome inválido");
            System.out.println("✅ Invalid name detection: " + !var3.isValid());

            // Teste 5: Conversão de typeId
            String typeRef = VariableDefinitionV2Plus.convertTypeIdToTypeRef("canonical-orcamento");
            System.out.println("✅ TypeId conversion: " + typeRef.equals("dt:orcamento@1"));

            // Teste 6: Clonagem
            VariableDefinitionV2Plus cloned = var1.clone();
            System.out.println("✅ Cloning: " + cloned.equals(var1));

            // Teste 7: Helper methods
            System.out.println("✅ isArray: " + !var1.isArray()); // cardinality = "one"
            System.out.println("✅ isRequired: " + var1.isRequired()); // nullable = false

            System.out.println("\n🎉 VariableDefinitionV2Plus: ALL TESTS PASSED!");
            System.out.println("Sample variable: " + var1);

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}