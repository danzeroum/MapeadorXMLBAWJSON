package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Score Methodology - Transparent scoring algorithm
 */
public class ScoreMethodology {
    private String version;
    private String description;
    private Map<String, Double> weights;
    private Map<String, String> formulas;
    private List<String> contributors;
    private MethodologyMetadata metadata;

    public ScoreMethodology() {
        this.weights = new HashMap<>();
        this.formulas = new HashMap<>();
        this.contributors = new ArrayList<>();
    }

    // Factory method for default methodology
    public static ScoreMethodology createDefault() {
        ScoreMethodology methodology = new ScoreMethodology();
        methodology.version = "2.0.0";
        methodology.description = "AI-Trustworthy Process Analysis Methodology";

        // Set default weights
        methodology.weights.put("structure", 0.3);
        methodology.weights.put("documentation", 0.25);
        methodology.weights.put("complexity", 0.25);
        methodology.weights.put("standardization", 0.2);

        // Set formulas
        methodology.formulas.put("overall", "weighted_average(structure, documentation, complexity, standardization)");
        methodology.formulas.put("structure", "integrityScore * 0.4 + nodeValidityScore * 0.3 + edgeConsistencyScore * 0.3");
        methodology.formulas.put("documentation", "descriptionCompleteness * 0.6 + metadataRichness * 0.4");
        methodology.formulas.put("complexity", "100 - (cyclomaticComplexity / maxComplexity * 100)");
        methodology.formulas.put("standardization", "namingConsistency * 0.5 + typeConsistency * 0.5");

        // Contributors
        methodology.contributors.add("Process Graph Validator");
        methodology.contributors.add("Documentation Analyzer");
        methodology.contributors.add("Complexity Calculator");
        methodology.contributors.add("Standards Checker");

        methodology.metadata = new MethodologyMetadata();
        methodology.metadata.setCreatedBy("AI-Trustworthy Analysis Engine V2");
        methodology.metadata.setLastUpdated("2025-09-09");

        return methodology;
    }

    // Calculate overall score using the methodology
    public double calculateOverallScore(double structure, double documentation, double complexity, double standardization) {
        double overall = 0.0;
        overall += structure * weights.getOrDefault("structure", 0.25);
        overall += documentation * weights.getOrDefault("documentation", 0.25);
        overall += complexity * weights.getOrDefault("complexity", 0.25);
        overall += standardization * weights.getOrDefault("standardization", 0.25);
        return Math.round(overall * 100.0) / 100.0; // Round to 2 decimal places
    }

    // Getters and setters
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, Double> getWeights() { return weights; }
    public void setWeights(Map<String, Double> weights) {
        this.weights = weights != null ? weights : new HashMap<>();
    }

    public Map<String, String> getFormulas() { return formulas; }
    public void setFormulas(Map<String, String> formulas) {
        this.formulas = formulas != null ? formulas : new HashMap<>();
    }

    public List<String> getContributors() { return contributors; }
    public void setContributors(List<String> contributors) {
        this.contributors = contributors != null ? contributors : new ArrayList<>();
    }

    public MethodologyMetadata getMetadata() { return metadata; }
    public void setMetadata(MethodologyMetadata metadata) { this.metadata = metadata; }

    public static class MethodologyMetadata {
        private String createdBy;
        private String lastUpdated;
        private String validationStatus;
        private List<String> references;

        public MethodologyMetadata() {
            this.references = new ArrayList<>();
        }

        // Getters and setters
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

        public String getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

        public String getValidationStatus() { return validationStatus; }
        public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }

        public List<String> getReferences() { return references; }
        public void setReferences(List<String> references) {
            this.references = references != null ? references : new ArrayList<>();
        }
    }
}
