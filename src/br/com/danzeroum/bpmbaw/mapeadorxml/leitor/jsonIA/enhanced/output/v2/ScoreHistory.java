package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.ArrayList;
import java.util.List;

/**
 * Score History - Track score changes over time
 */
public class ScoreHistory {
    private List<ScoreSnapshot> snapshots;
    private ScoreTrend trend;

    public ScoreHistory() {
        this.snapshots = new ArrayList<>();
    }

    public void addSnapshot(ScoreSnapshot snapshot) {
        if (snapshot != null) {
            this.snapshots.add(snapshot);
            calculateTrend();
        }
    }

    private void calculateTrend() {
        if (snapshots.size() >= 2) {
            ScoreSnapshot latest = snapshots.get(snapshots.size() - 1);
            ScoreSnapshot previous = snapshots.get(snapshots.size() - 2);

            double change = latest.getOverallScore() - previous.getOverallScore();

            if (change > 1.0) {
                trend = ScoreTrend.IMPROVING;
            } else if (change < -1.0) {
                trend = ScoreTrend.DECLINING;
            } else {
                trend = ScoreTrend.STABLE;
            }
        } else {
            trend = ScoreTrend.BASELINE;
        }
    }

    // Getters and setters
    public List<ScoreSnapshot> getSnapshots() { return snapshots; }
    public void setSnapshots(List<ScoreSnapshot> snapshots) {
        this.snapshots = snapshots != null ? snapshots : new ArrayList<>();
    }

    public ScoreTrend getTrend() { return trend; }
    public void setTrend(ScoreTrend trend) { this.trend = trend; }

    public static class ScoreSnapshot {
        private String timestamp;
        private String version;
        private double overallScore;
        private double structureScore;
        private double documentationScore;
        private double complexityScore;
        private double standardizationScore;
        private String notes;

        // Getters and setters
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public double getOverallScore() { return overallScore; }
        public void setOverallScore(double overallScore) { this.overallScore = overallScore; }

        public double getStructureScore() { return structureScore; }
        public void setStructureScore(double structureScore) { this.structureScore = structureScore; }

        public double getDocumentationScore() { return documentationScore; }
        public void setDocumentationScore(double documentationScore) { this.documentationScore = documentationScore; }

        public double getComplexityScore() { return complexityScore; }
        public void setComplexityScore(double complexityScore) { this.complexityScore = complexityScore; }

        public double getStandardizationScore() { return standardizationScore; }
        public void setStandardizationScore(double standardizationScore) { this.standardizationScore = standardizationScore; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public enum ScoreTrend {
        BASELINE, IMPROVING, STABLE, DECLINING
    }
}