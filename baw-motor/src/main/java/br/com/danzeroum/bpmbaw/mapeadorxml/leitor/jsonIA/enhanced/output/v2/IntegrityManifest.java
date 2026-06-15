package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.HashMap;
import java.util.Map;

public class IntegrityManifest {
    private Map<String, String> sections;
    private DigitalSignature signature;
    private String overallChecksum;

    public IntegrityManifest() {
    }

    // Factory method para criar manifest
    public static IntegrityManifest create(EnhancedStructuredProcessReportV2 report) {
        IntegrityManifest manifest = new IntegrityManifest();

        // Calcular checksums por seção - CORRIGIDO: usar HashMap em vez de Map.of()
        manifest.sections = new HashMap<>();
        manifest.sections.put("dataTypes", calculateSectionChecksum(report.getDataTypes()));
        manifest.sections.put("processGraph", calculateSectionChecksum(report.getProcessGraph()));
        manifest.sections.put("logic", calculateSectionChecksum(report.getLogic()));
        manifest.sections.put("security", calculateSectionChecksum(report.getSecurity()));
        manifest.sections.put("domain", calculateSectionChecksum(report.getDomain()));
        manifest.sections.put("ui", calculateSectionChecksum(report.getUi()));
        manifest.sections.put("businessContext", calculateSectionChecksum(report.getBusinessContext()));
        manifest.sections.put("aiReadinessScore", calculateSectionChecksum(report.getAiReadinessScore()));

        // Checksum geral
        manifest.overallChecksum = calculateOverallChecksum(manifest.sections);

        return manifest;
    }

    private static String calculateSectionChecksum(Object section) {
        if (section == null) return "sha256-null";
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            byte[] json = mapper.writeValueAsBytes(section);
            java.security.MessageDigest sha = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = sha.digest(json);
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return "sha256-" + hex;
        } catch (Exception e) {
            return "sha256-error";
        }
    }

    private static String calculateOverallChecksum(Map<String, String> sections) {
        try {
            // Deterministic: sort keys before hashing
            StringBuilder combined = new StringBuilder();
            sections.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> combined.append(e.getKey()).append(":").append(e.getValue()).append(";"));
            java.security.MessageDigest sha = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = sha.digest(combined.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return "sha256-" + hex;
        } catch (Exception e) {
            return "sha256-error";
        }
    }

    // Getters and setters
    public Map<String, String> getSections() {
        return sections;
    }

    public void setSections(Map<String, String> sections) {
        this.sections = sections;
    }

    public DigitalSignature getSignature() {
        return signature;
    }

    public void setSignature(DigitalSignature signature) {
        this.signature = signature;
    }

    public String getOverallChecksum() {
        return overallChecksum;
    }

    public void setOverallChecksum(String overallChecksum) {
        this.overallChecksum = overallChecksum;
    }
}