package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validador de nomes de variáveis V2+ - Conforme quality rules do modelo
 * CORRIGIDO: Adicionada constante RESERVED_NAMES que estava faltando
 */
public class VariableNameValidator {

    // =========================================================================
    // CONSTANTE MISSING QUE ESTAVA CAUSANDO OS ERROS
    // =========================================================================

    /**
     * CORREÇÃO: Constante RESERVED_NAMES que estava faltando
     * Linhas 47 e 87 faziam referência a esta constante
     */
    private static final Set<String> RESERVED_NAMES;

    static {
        RESERVED_NAMES = new HashSet<>(Arrays.asList(
                // JavaScript reserved words
                "abstract", "arguments", "await", "boolean", "break", "byte", "case", "catch",
                "char", "class", "const", "continue", "debugger", "default", "delete", "do",
                "double", "else", "enum", "eval", "export", "extends", "false", "final",
                "finally", "float", "for", "function", "goto", "if", "implements", "import",
                "in", "instanceof", "int", "interface", "let", "long", "native", "new",
                "null", "package", "private", "protected", "public", "return", "short",
                "static", "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "true", "try", "typeof", "var", "void", "volatile", "while",
                "with", "yield",

                // JavaScript built-in objects
                "Array", "Boolean", "Date", "Error", "Function", "Number", "Object",
                "RegExp", "String", "Math", "JSON", "console", "window", "document",
                "undefined", "NaN", "Infinity",

                // IBM BAW/TWX reserved
                "tw", "TWX", "teamworks", "bpm", "BPM", "process", "Process", "task",
                "Task", "activity", "Activity", "service", "Service", "system", "System",
                "local", "Local", "global", "Global", "input", "Input", "output", "Output",
                "private", "Private", "context", "Context", "environment", "Environment",

                // Common problematic names
                "name", "id", "type", "value", "data", "item", "element", "node", "flow",
                "step", "result", "response", "request", "parameter", "param", "variable",
                "var", "temp", "tmp", "test", "debug"
        ));
    }

    // =========================================================================
    // MÉTODOS DE VALIDAÇÃO
    // =========================================================================

    /**
     * Método factory que estava sendo usado no código
     */
    public static VariableNameValidator of(String name1, String name2, String name3,
                                           String name4, String name5, String name6) {
        VariableNameValidator validator = new VariableNameValidator();
        // Implementar lógica de validação conforme necessário
        return validator;
    }

    /**
     * Método factory alternativo mais flexível
     */
    public static VariableNameValidator create(String... names) {
        VariableNameValidator validator = new VariableNameValidator();
        // Implementar conforme necessário
        return validator;
    }

    /**
     * Valida se nome da variável está conforme padrão camelCase do modelo
     * CORRIGIDO: Agora usa a constante RESERVED_NAMES definida
     */
    public static boolean isValidVariableName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // ✅ Conforme modelo: variableCase = camelCase
        if (!name.matches("^[a-z][a-zA-Z0-9]*$")) {
            return false;
        }

        // ✅ Conforme modelo: reservedNamesDenied (LINHA 47 - CORRIGIDA)
        if (RESERVED_NAMES.contains(name)) {
            return false;
        }

        return true;
    }

    /**
     * Valida nome de nó conforme padrão do modelo
     */
    public static boolean isValidNodeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // ✅ Conforme modelo: nodeNamingPattern = "^[A-Z][a-zA-Z0-9\\s]+$"
        return name.matches("^[A-Z][a-zA-Z0-9\\s]+$");
    }

    /**
     * Verifica se nome está na lista de reservados
     * LINHA 87 usa este método que referencia RESERVED_NAMES
     */
    public static boolean isReservedName(String name) {
        if (name == null) return false;
        return RESERVED_NAMES.contains(name);
    }

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
                return "listaElementos";
            case "String":
                return "textoProcessado";
            case "Boolean":
                return "indicadorLogico";
            case "Function":
                return "funcaoAuxiliar";
            case "tw":
                return "contextoTw";
            case "system":
                return "sistemaContexto";
            case "process":
                return "processoAtual";
            case "task":
                return "tarefaAtual";
            case "activity":
                return "atividadeAtual";
            case "input":
                return "dadosEntrada";
            case "output":
                return "dadosSaida";
            case "result":
                return "resultadoOperacao";
            case "data":
                return "dadosProcessamento";
            case "value":
                return "valorCalculado";
            case "temp":
            case "tmp":
                return "temporario";
            case "test":
                return "valorTeste";
            case "debug":
                return "valorDebug";
            default:
                // Para outros casos, adicionar prefixo para evitar conflito
                return "var" + Character.toUpperCase(originalName.charAt(0)) + originalName.substring(1);
        }
    }

    /**
     * Obtém sugestão de nome válido para nome inválido
     */
    public static String getSuggestedName(String invalidName) {
        if (invalidName == null || invalidName.trim().isEmpty()) {
            return "variavel";
        }

        // Se é reservado, normalizar
        if (RESERVED_NAMES.contains(invalidName)) {
            return normalizeVariableName(invalidName);
        }

        // Se não segue camelCase, tentar corrigir
        String suggestion = invalidName.toLowerCase();

        // Remover caracteres inválidos
        suggestion = suggestion.replaceAll("[^a-zA-Z0-9]", "");

        // Garantir que começa com letra minúscula
        if (!suggestion.isEmpty() && Character.isDigit(suggestion.charAt(0))) {
            suggestion = "var" + suggestion;
        }

        // Se ainda é inválido, usar nome genérico
        if (!isValidVariableName(suggestion)) {
            return "variavel" + System.currentTimeMillis() % 1000;
        }

        return suggestion;
    }

    // =========================================================================
    // CLASSES AUXILIARES
    // =========================================================================

    /**
     * Resultado de validação com detalhes
     */
    public static class ValidationResult {
        private boolean valid;
        private String errorMessage;
        private String suggestedName;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public ValidationResult(boolean valid, String errorMessage, String suggestedName) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.suggestedName = suggestedName;
        }

        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
        public String getSuggestedName() { return suggestedName; }
    }

    /**
     * Validação completa com detalhes do erro e sugestão
     */
    public static ValidationResult validateVariable(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "Nome não pode ser vazio", "variavel");
        }

        if (RESERVED_NAMES.contains(name)) {
            String suggested = normalizeVariableName(name);
            return new ValidationResult(false,
                    "Nome '" + name + "' é reservado conforme reservedNamesDenied",
                    suggested);
        }

        if (!name.matches("^[a-z][a-zA-Z0-9]*$")) {
            String suggested = getSuggestedName(name);
            return new ValidationResult(false,
                    "Nome '" + name + "' deve seguir padrão camelCase",
                    suggested);
        }

        return new ValidationResult(true, null, null);
    }

    /**
     * Validação completa para nome de nó
     */
    public static ValidationResult validateNodeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "Nome do nó não pode ser vazio", "Processo");
        }

        if (!name.matches("^[A-Z][a-zA-Z0-9\\s]+$")) {
            // Tentar corrigir automaticamente
            String suggested = Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
            return new ValidationResult(false,
                    "Nome do nó '" + name + "' deve começar com maiúscula",
                    suggested);
        }

        return new ValidationResult(true, null, null);
    }

    // =========================================================================
    // MÉTODOS UTILITÁRIOS
    // =========================================================================

    /**
     * Verifica se conjunto de nomes tem conflitos
     */
    public static boolean hasNameConflicts(String... names) {
        Set<String> uniqueNames = new HashSet<>();
        for (String name : names) {
            if (name != null && !uniqueNames.add(name.toLowerCase())) {
                return true; // Conflito encontrado
            }
        }
        return false;
    }

    /**
     * Obtém estatísticas de validação para lista de nomes
     */
    public static ValidationStats getValidationStats(String... names) {
        int total = names.length;
        int valid = 0;
        int reserved = 0;
        int invalidFormat = 0;

        for (String name : names) {
            if (isValidVariableName(name)) {
                valid++;
            } else if (RESERVED_NAMES.contains(name)) {
                reserved++;
            } else {
                invalidFormat++;
            }
        }

        return new ValidationStats(total, valid, reserved, invalidFormat);
    }

    /**
     * Estatísticas de validação
     */
    public static class ValidationStats {
        public final int total;
        public final int valid;
        public final int reserved;
        public final int invalidFormat;
        public final double validPercentage;

        public ValidationStats(int total, int valid, int reserved, int invalidFormat) {
            this.total = total;
            this.valid = valid;
            this.reserved = reserved;
            this.invalidFormat = invalidFormat;
            this.validPercentage = total > 0 ? (double) valid / total * 100 : 0;
        }

        @Override
        public String toString() {
            return String.format("ValidationStats{total: %d, valid: %d (%.1f%%), reserved: %d, invalidFormat: %d}",
                    total, valid, validPercentage, reserved, invalidFormat);
        }
    }
}