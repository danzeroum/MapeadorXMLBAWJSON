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
        // Implementation would use Jackson + SHA-256
        // Placeholder for actual implementation
        if (section == null) {
            return "sha256-null";
        }
        return "sha256-" + Integer.toHexString(section.hashCode());
    }

    private static String calculateOverallChecksum(Map<String, String> sections) {
        // Implementation would combine all section checksums
        return "sha256-" + Integer.toHexString(sections.hashCode());
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