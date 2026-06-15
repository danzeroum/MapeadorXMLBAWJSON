package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model;

import java.util.ArrayList;
import java.util.List;


public class AIReadinessScore {
    private double overallScore = 0.0;
    private double structureScore = 0.0;
    private double documentationScore = 0.0;
    private double complexityScore = 0.0;
    private double standardizationScore = 0.0;
    private List<String> strengths = new ArrayList<>();
    private List<String> weaknesses = new ArrayList<>();
    private List<String> recommendations = new ArrayList<>();

    public AIReadinessScore() {}

    // Getters e Setters
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

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}