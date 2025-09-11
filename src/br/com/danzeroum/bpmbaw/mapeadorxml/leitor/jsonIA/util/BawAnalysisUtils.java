
// ==================================================
// UTILITY CLASSES
// ==================================================

package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.util;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.BawAnalysisConfig;

/**
 * Utility methods for IBM BAW analysis.
 * Centralizes common operations and calculations.
 */
public class BawAnalysisUtils {

    /**
     * Normalizes an ID by removing whitespace and extracting clean part.
     */
    public static String normalizeId(String id) {
        if (id == null) return null;
        String normalized = id.replaceAll("\\s+", "").trim();
        return normalized.contains("/") ?
                normalized.substring(normalized.lastIndexOf('/') + 1) :
                normalized;
    }

    /**
     * Gets a human-readable display name for an artifact type.
     */
    public static String getDisplayNameForType(String type) {
        return BawAnalysisConfig.getDisplayNameForType(type);
    }

    /**
     * Checks if a data type is primitive.
     */
    public static boolean isPrimitiveType(String typeName) {
        return BawAnalysisConfig.isPrimitiveType(typeName);
    }

    /**
     * Safely extracts the root part of a variable path.
     * Example: "customer.address.street[0]" → "customer"
     */
    public static String extractRootVariableName(String variablePath) {
        if (variablePath == null || variablePath.trim().isEmpty()) {
            return null;
        }

        String cleaned = variablePath.trim();

        // Remove common expression wrappers
        if (cleaned.startsWith("${") && cleaned.endsWith("}")) {
            cleaned = cleaned.substring(2, cleaned.length() - 1);
        }
        if (cleaned.startsWith("tw(") && cleaned.endsWith(")")) {
            cleaned = cleaned.substring(3, cleaned.length() - 1);
        }

        // Extract root before dot or bracket
        int dotIndex = cleaned.indexOf('.');
        int bracketIndex = cleaned.indexOf('[');

        int splitIndex = -1;
        if (dotIndex > 0 && bracketIndex > 0) {
            splitIndex = Math.min(dotIndex, bracketIndex);
        } else if (dotIndex > 0) {
            splitIndex = dotIndex;
        } else if (bracketIndex > 0) {
            splitIndex = bracketIndex;
        }

        return splitIndex > 0 ? cleaned.substring(0, splitIndex) : cleaned;
    }

    /**
     * Validates that a string is a valid identifier (for variable names, etc.).
     */
    public static boolean isValidIdentifier(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return false;
        }

        // Basic validation: starts with letter or underscore, contains only alphanumeric and underscore
        return identifier.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }

    /**
     * Safely limits a string to a maximum length with ellipsis.
     */
    public static String limitString(String input, int maxLength) {
        if (input == null) return null;
        if (input.length() <= maxLength) return input;
        return input.substring(0, maxLength - 3) + "...";
    }

    /**
     * Formats a duration in milliseconds to human-readable format.
     */
    public static String formatDuration(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        } else if (milliseconds < 60000) {
            return String.format("%.1fs", milliseconds / 1000.0);
        } else {
            long minutes = milliseconds / 60000;
            long seconds = (milliseconds % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }
}