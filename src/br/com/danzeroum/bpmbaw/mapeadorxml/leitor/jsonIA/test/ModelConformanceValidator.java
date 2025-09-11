package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.*;

/**
 * Validador de conformidade com modelo proposto
 */
public class ModelConformanceValidator {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Valida se JSON gerado está 100% conforme modelo proposto
     */
    public static ValidationResult validateConformance(String jsonFilePath) throws Exception {
        JsonNode jsonNode = mapper.readTree(new File(jsonFilePath));
        List<String> errors = new ArrayList<>();

        // 1. Validar estrutura raiz
        validateRootStructure(jsonNode, errors);

        // 2. Validar processDefinition
        validateProcessDefinition(jsonNode.get("processDefinition"), errors);

        // 3. Validar dataTypes
        validateDataTypes(jsonNode.get("dataTypes"), errors);

        // 4. Validar seções V2+
        validateV2PlusSections(jsonNode, errors);

        return new ValidationResult(errors.isEmpty(), errors);
    }

    private static void validateRootStructure(JsonNode root, List<String> errors) {
        // Campos obrigatórios conforme modelo
        String[] requiredFields = {"$schema", "schemaVersion", "$id", "metadata",
                "processDefinition", "dataTypes", "ui", "quality",
                "security", "analytics"};

        for (String field : requiredFields) {
            if (!root.has(field)) {
                errors.add("Missing required field: " + field);
            }
        }

        // Validar valores específicos
        if (root.has("schemaVersion") && !"2.1.0".equals(root.get("schemaVersion").asText())) {
            errors.add("schemaVersion should be '2.1.0'");
        }
    }

    private static void validateProcessDefinition(JsonNode processDef, List<String> errors) {
        if (processDef == null) {
            errors.add("processDefinition is required");
            return;
        }

        // Validar estrutura interna
        String[] requiredSections = {"variables", "graph", "conditions", "mappings", "logic"};
        for (String section : requiredSections) {
            if (!processDef.has(section)) {
                errors.add("processDefinition missing: " + section);
            }
        }

        // Validar mappings
        if (processDef.has("mappings")) {
            JsonNode mappings = processDef.get("mappings");
            if (!mappings.has("exprLang")) {
                errors.add("mappings missing 'exprLang' field");
            } else if (!"cel".equals(mappings.get("exprLang").asText())) {
                errors.add("mappings exprLang should be 'cel'");
            }
        }

        // Validar conditions
        if (processDef.has("conditions")) {
            JsonNode conditions = processDef.get("conditions");
            if (conditions.isArray()) {
                for (JsonNode condition : conditions) {
                    if (!condition.has("expr")) {
                        errors.add("condition missing 'expr' field (should not be 'expression')");
                    }
                    if (!condition.has("exprLang")) {
                        errors.add("condition missing 'exprLang' field (should not be 'language')");
                    }
                }
            }
        }
    }

    private static void validateDataTypes(JsonNode dataTypes, List<String> errors) {
        if (dataTypes == null || !dataTypes.isArray()) {
            errors.add("dataTypes should be an array");
            return;
        }

        // Verificar tipos básicos obrigatórios
        Set<String> foundTypes = new HashSet<>();
        for (JsonNode dataType : dataTypes) {
            if (dataType.has("id")) {
                foundTypes.add(dataType.get("id").asText());
            }
        }

        String[] requiredTypes = {"dt:string@1", "dt:object@1", "dt:recondicionamento@1", "dt:orcamento@1"};
        for (String requiredType : requiredTypes) {
            if (!foundTypes.contains(requiredType)) {
                errors.add("Missing required dataType: " + requiredType);
            }
        }
    }

    private static void validateV2PlusSections(JsonNode root, List<String> errors) {
        // Validar ui
        if (!root.has("ui")) {
            errors.add("Missing 'ui' section (required in V2+)");
        }

        // Validar quality
        if (!root.has("quality")) {
            errors.add("Missing 'quality' section (required in V2+)");
        }

        // Validar security
        if (!root.has("security")) {
            errors.add("Missing 'security' section (required in V2+)");
        }

        // Validar analytics
        if (!root.has("analytics")) {
            errors.add("Missing 'analytics' section (required in V2+)");
        }
    }

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }

        public void printResults() {
            if (valid) {
                System.out.println("✅ Model conformance: PASSED");
            } else {
                System.out.println("❌ Model conformance: FAILED");
                errors.forEach(error -> System.out.println("  - " + error));
            }
        }
    }
}