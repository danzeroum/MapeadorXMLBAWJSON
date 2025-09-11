package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config;

import java.util.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Configuration constants and utilities for IBM BAW analysis.
 * Centralizes all configuration values for better maintainability.
 * Compatible with Java 8+
 */
public class BawAnalysisConfig {

    // ========== PATH AND PROCESSING LIMITS ==========

    /** Maximum length for execution paths to prevent infinite loops */
    public static final int MAX_EXECUTION_PATH_LENGTH = 50;

    /** Maximum number of times a node can repeat in a single path */
    public static final int MAX_NODE_REPEATS_IN_PATH = 2;

    /** Default depth for root view expansion */
    public static final int DEFAULT_ROOT_VIEW_DEPTH = 1;

    /** Maximum depth for recursive processing */
    public static final int MAX_RECURSION_DEPTH = 10;

    /** Maximum number of items in BFS processing queue */
    public static final int BFS_QUEUE_LIMIT = 1000;

    /** Maximum number of steps in root view to prevent runaway expansion */
    public static final int MAX_ROOT_VIEW_STEPS = 1000;

    // ========== TYPE MAPPINGS ==========

    /** Human-readable names for IBM BAW node types */
    public static final Map<String, String> NODE_TYPE_DISPLAY_NAMES;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("bpd", "Business Process Diagram");
        map.put("process", "Service Process");
        map.put("formtask", "User Task");
        map.put("scripttask", "Script Task");
        map.put("callactivity", "Call Activity");
        map.put("exclusivegateway", "Decision Gateway");
        map.put("startevent", "Start Event");
        map.put("endevent", "End Event");
        map.put("subprocess", "Subprocess");
        map.put("participant", "Process Participant");
        NODE_TYPE_DISPLAY_NAMES = Collections.unmodifiableMap(map);
    }

    /** Primitive data types that don't need further enrichment */
    public static final Set<String> PRIMITIVE_DATA_TYPES;
    static {
        Set<String> set = new HashSet<>();
        set.add("String");
        set.add("Integer");
        set.add("Boolean");
        set.add("Decimal");
        set.add("Date");
        set.add("Time");
        set.add("DateTime");
        set.add("ANY");
        PRIMITIVE_DATA_TYPES = Collections.unmodifiableSet(set);
    }

    /** File extensions for IBM BAW artifacts */
    public static final Set<String> SUPPORTED_FILE_EXTENSIONS;
    static {
        Set<String> set = new HashSet<>();
        set.add(".xml");
        set.add(".twx");
        set.add(".bpmn");
        set.add(".bpm");
        SUPPORTED_FILE_EXTENSIONS = Collections.unmodifiableSet(set);
    }

    // Adicionar ao BawAnalysisConfig.java

    /** Mapping de nomes problemáticos para versões padronizadas */
    public static final Map<String, String> NORMALIZED_NAMES;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("Avaliar Orçamento", "EvaluateBudget");
        map.put("Iniciar Variaveis", "InitializeVariables");
        map.put("Tratar decisão", "ProcessDecision");
        map.put("Update Reconditioning Data", "UpdateReconditioningData");
        NORMALIZED_NAMES = Collections.unmodifiableMap(map);
    }

    /** Caracteres problemáticos para limpeza */
    public static final Map<String, String> CHARACTER_REPLACEMENTS;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("Ã§", "c");
        map.put("Ã£", "a");
        map.put("Ã©", "e");
        map.put("Ã­", "i");
        map.put("Ã³", "o");
        map.put("Ãº", "u");
        CHARACTER_REPLACEMENTS = Collections.unmodifiableMap(map);
    }

    /** Método para normalizar nomes */
    public static String normalizeProcessName(String name) {
        if (name == null) return null;

        // Primeiro tenta mapeamento direto
        String normalized = NORMALIZED_NAMES.get(name);
        if (normalized != null) return normalized;

        // Senão, aplica regras de limpeza
        String result = name;
        for (Map.Entry<String, String> replacement : CHARACTER_REPLACEMENTS.entrySet()) {
            result = result.replace(replacement.getKey(), replacement.getValue());
        }

        // Remove caracteres especiais e converte para PascalCase
        result = result.replaceAll("[^a-zA-Z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", " ");

        // Converte para PascalCase
        String[] words = result.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                builder.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    builder.append(word.substring(1).toLowerCase());
                }
            }
        }

        return builder.toString();
    }

    // ========== ANALYSIS BEHAVIOR ==========

    /** Maximum number of alternative paths to generate */
    public static final int MAX_ALTERNATIVE_PATHS = 3;

    /** Maximum number of entry points to process for happy paths */
    public static final int MAX_HAPPY_PATH_ENTRY_POINTS = 2;

    /** Default timeout for single artifact processing (milliseconds) */
    public static final long ARTIFACT_PROCESSING_TIMEOUT_MS = 30_000;

    // ========== OUTPUT FORMATTING ==========

    /** Default output directory for reports */
    public static final String DEFAULT_OUTPUT_DIRECTORY = "output";

    /** Date format for timestamped output files */
    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

    /** Default encoding for output files */
    public static final String DEFAULT_OUTPUT_ENCODING = "UTF-8";

    // ========== UTILITY METHODS ==========

    /**
     * Gets the display name for a node type.
     */
    public static String getDisplayNameForType(String type) {
        if (type == null) return "Unknown";
        String displayName = NODE_TYPE_DISPLAY_NAMES.get(type.toLowerCase());
        return displayName != null ? displayName : type;
    }

    /**
     * Checks if a type is primitive (doesn't need enrichment).
     */
    public static boolean isPrimitiveType(String typeName) {
        return typeName != null && PRIMITIVE_DATA_TYPES.contains(typeName);
    }

    /**
     * Generates a timestamp for output files.
     */
    public static String generateTimestamp() {
        return new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
    }

    /**
     * Creates the default output directory if it doesn't exist.
     */
    public static void ensureOutputDirectoryExists() {
        File outputDir = new File(DEFAULT_OUTPUT_DIRECTORY);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    /**
     * Validates that a path exists and is readable.
     */
    public static boolean isValidTwxPath(String path) {
        if (path == null || path.trim().isEmpty()) return false;

        File dir = new File(path);
        return dir.exists() && dir.isDirectory() && dir.canRead();
    }

    /**
     * Normalizes an ID by removing whitespace and extracting the clean part.
     */
    public static String normalizeId(String id) {
        if (id == null) return null;
        String normalized = id.replaceAll("\\s+", "").trim();
        return normalized.contains("/") ?
                normalized.substring(normalized.lastIndexOf('/') + 1) :
                normalized;
    }
}