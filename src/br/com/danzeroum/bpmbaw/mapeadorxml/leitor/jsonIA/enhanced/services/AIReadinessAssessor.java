// AIReadinessAssessor.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.services;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model.AIReadinessScore;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model.BusinessContext;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;

public class AIReadinessAssessor {

    public AIReadinessScore assess(JsonReportV2 report, BusinessContext businessContext) {
        AIReadinessScore score = new AIReadinessScore();

        // Implementação simples inicial
        score.setStructureScore(assessStructure(report));
        score.setDocumentationScore(assessDocumentation(businessContext));
        score.setComplexityScore(assessComplexity(report));
        score.setStandardizationScore(70.0); // Valor fixo inicial

        // Calcular score geral
        double overall = (score.getStructureScore() + score.getDocumentationScore() +
                score.getComplexityScore() + score.getStandardizationScore()) / 4.0;
        score.setOverallScore(overall);

        // Gerar recomendações básicas
        generateBasicRecommendations(score);

        return score;
    }

    private double assessStructure(JsonReportV2 report) {
        double score = 70.0; // Base score

        if (report.getArtifacts() != null) {
            for (JsonReportV2.Artifact artifact : report.getArtifacts()) {
                if (artifact.getGraph() != null) {
                    if (artifact.getGraph().getEntryPoints() != null && !artifact.getGraph().getEntryPoints().isEmpty()) {
                        score += 5.0;
                    }
                    if (artifact.getGraph().getEndPoints() != null && !artifact.getGraph().getEndPoints().isEmpty()) {
                        score += 5.0;
                    }
                }
            }
        }

        return Math.min(100.0, score);
    }

    private double assessDocumentation(BusinessContext businessContext) {
        double score = 50.0;

        if (businessContext != null) {
            if (!businessContext.getDomainTerms().isEmpty()) score += 20.0;
            if (!businessContext.getMainEntities().isEmpty()) score += 15.0;
            if (!businessContext.getBusinessRules().isEmpty()) score += 15.0;
        }

        return Math.min(100.0, score);
    }

    private double assessComplexity(JsonReportV2 report) {
        int totalSteps = 0;
        int totalGateways = 0;

        if (report.getArtifacts() != null) {
            for (JsonReportV2.Artifact artifact : report.getArtifacts()) {
                if (artifact.getFlow() != null) {
                    totalSteps += artifact.getFlow().size();
                }
                if (artifact.getGraph() != null && artifact.getGraph().getGateways() != null) {
                    totalGateways += artifact.getGraph().getGateways().size();
                }
            }
        }

        // Menor complexidade = maior score de AI readiness
        if (totalSteps <= 20 && totalGateways <= 5) return 90.0;
        if (totalSteps <= 50 && totalGateways <= 10) return 70.0;
        if (totalSteps <= 100 && totalGateways <= 20) return 50.0;
        return 30.0;
    }

    private void generateBasicRecommendations(AIReadinessScore score) {
        if (score.getStructureScore() < 70) {
            score.getRecommendations().add("Improve process structure with clear entry/exit points");
        }
        if (score.getDocumentationScore() < 70) {
            score.getRecommendations().add("Add more business context and documentation");
        }
        if (score.getComplexityScore() < 70) {
            score.getRecommendations().add("Simplify complex process flows");
        }

        // Identificar pontos fortes
        if (score.getStructureScore() >= 80) {
            score.getStrengths().add("Well-structured process flow");
        }
        if (score.getDocumentationScore() >= 80) {
            score.getStrengths().add("Rich business context");
        }
        if (score.getComplexityScore() >= 80) {
            score.getStrengths().add("Manageable complexity");
        }
    }
}