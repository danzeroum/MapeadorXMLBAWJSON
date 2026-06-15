package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.*;

/**
 * AI Readiness Score V2 - Auditável e Enterprise-Grade
 * Implementa metodologia transparente para avaliação de processos
 */
public class AIReadinessScoreV2 {
    private double overallScore;
    private double structureScore;
    private double documentationScore;
    private double complexityScore;
    private double standardizationScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> recommendations;

    // P0 - Metodologia auditável
    private ScoreMethodology methodology;
    private Map<String, Double> targetScores;
    private List<ActionItem> actionItems;
    private ScoreHistory history;

    public AIReadinessScoreV2() {
        this.strengths = new ArrayList<>();
        this.weaknesses = new ArrayList<>();
        this.recommendations = new ArrayList<>();
        this.actionItems = new ArrayList<>();
        this.targetScores = new HashMap<>();
    }

    // Factory method para criar score com metodologia completa
    public static AIReadinessScoreV2 calculateScore(ProcessGraphV2 graph,
                                                    List<DataTypeDefinitionV2> dataTypes,
                                                    ProcessLogicV2 logic,
                                                    SecurityConfigV2 security) {
        AIReadinessScoreV2 score = new AIReadinessScoreV2();

        // Configurar metodologia
        score.methodology = ScoreMethodology.createDefault();

        // Calcular scores individuais
        score.structureScore = calculateStructureScore(graph);
        score.documentationScore = calculateDocumentationScore(dataTypes, logic);
        score.complexityScore = calculateComplexityScore(graph);
        score.standardizationScore = calculateStandardizationScore(dataTypes, security);

        // Calcular score geral usando pesos da metodologia
        score.overallScore = score.methodology.calculateOverallScore(
                score.structureScore,
                score.documentationScore,
                score.complexityScore,
                score.standardizationScore
        );

        // Gerar recomendações baseadas nos scores
        score.generateRecommendations();

        // Definir targets para próximas versões
        score.setTargetScores();

        return score;
    }

    private static double calculateStructureScore(ProcessGraphV2 graph) {
        if (graph == null || graph.getNodes() == null || graph.getNodes().isEmpty()) {
            return 0.0;
        }

        double score = 0.0;

        // Integridade referencial (40%)
        boolean hasIntegrity = graph.validateReferentialIntegrity();
        score += hasIntegrity ? 40.0 : 0.0;

        // Validação de nodes (30%)
        int validNodes = 0;
        for (ProcessNodeV2 node : graph.getNodes()) {
            if (node.getId() != null && !node.getId().trim().isEmpty() &&
                    node.getType() != null) {
                validNodes++;
            }
        }
        double nodeValidityRatio = (double) validNodes / graph.getNodes().size();
        score += nodeValidityRatio * 30.0;

        // Consistência de edges (30%)
        if (graph.getEdges() != null && !graph.getEdges().isEmpty()) {
            int validEdges = 0;
            for (ProcessEdgeV2 edge : graph.getEdges()) {
                if (edge.getSource() != null && edge.getTarget() != null) {
                    validEdges++;
                }
            }
            double edgeValidityRatio = (double) validEdges / graph.getEdges().size();
            score += edgeValidityRatio * 30.0;
        } else {
            score += 30.0; // No edges is valid for simple processes
        }

        return Math.min(100.0, score);
    }

    private static double calculateDocumentationScore(List<DataTypeDefinitionV2> dataTypes, ProcessLogicV2 logic) {
        double score = 0.0;
        int totalElements = 0;
        int documentedElements = 0;

        // Score para data types (50%)
        if (dataTypes != null && !dataTypes.isEmpty()) {
            for (DataTypeDefinitionV2 dataType : dataTypes) {
                totalElements++;
                if (dataType.getDescription() != null &&
                        !dataType.getDescription().trim().isEmpty() &&
                        dataType.getDescription().length() > 10) {
                    documentedElements++;
                }
            }
        }

        // Score para logic scripts (50%)
        if (logic != null && logic.getScripts() != null && !logic.getScripts().isEmpty()) {
            for (ProcessLogicV2.LogicScriptV2 script : logic.getScripts()) {
                totalElements++;
                if (script.getDescription() != null &&
                        !script.getDescription().trim().isEmpty() &&
                        script.getDescription().length() > 10) {
                    documentedElements++;
                }
            }
        }

        if (totalElements > 0) {
            score = ((double) documentedElements / totalElements) * 100.0;
        } else {
            score = 50.0; // Neutral score for processes without elements
        }

        return Math.min(100.0, score);
    }

    private static double calculateComplexityScore(ProcessGraphV2 graph) {
        if (graph == null || graph.getNodes() == null || graph.getNodes().isEmpty()) {
            return 100.0; // Simple processes get high score
        }

        // Calcular complexidade ciclomática baseada em decisões
        int decisionNodes = 0;
        int totalNodes = graph.getNodes().size();

        for (ProcessNodeV2 node : graph.getNodes()) {
            if (node.getType() != null &&
                    (node.getType().toString().contains("Gateway") ||
                            node.getType().toString().contains("Decision"))) {
                decisionNodes++;
            }
        }

        // Complexidade = decisões + 1 (método McCabe)
        int cyclomaticComplexity = decisionNodes + 1;

        // Score inverso: menor complexidade = maior score
        double complexityRatio = Math.min(1.0, (double) cyclomaticComplexity / 20.0); // 20 = limite alto
        double score = (1.0 - complexityRatio) * 100.0;

        // Penalizar processos muito grandes
        if (totalNodes > 50) {
            score *= 0.9;
        }
        if (totalNodes > 100) {
            score *= 0.8;
        }

        return Math.max(0.0, Math.min(100.0, score));
    }

    private static double calculateStandardizationScore(List<DataTypeDefinitionV2> dataTypes, SecurityConfigV2 security) {
        double score = 0.0;

        // Score para consistência de naming (60%)
        if (dataTypes != null && !dataTypes.isEmpty()) {
            int consistentNames = 0;
            for (DataTypeDefinitionV2 dataType : dataTypes) {
                if (dataType.getName() != null &&
                        dataType.getName().matches("^[a-z][a-zA-Z0-9]*$")) { // camelCase
                    consistentNames++;
                }
            }
            score += ((double) consistentNames / dataTypes.size()) * 60.0;
        } else {
            score += 30.0; // Neutral score
        }

        // Score para configuração de segurança (40%)
        if (security != null) {
            int securityFeatures = 0;

            if (security.getPiiFields() != null && !security.getPiiFields().isEmpty()) {
                securityFeatures++;
            }
            if (security.getClassification() != null) {
                securityFeatures++;
            }
            if (security.getAudit() != null) {
                securityFeatures++;
            }

            score += ((double) securityFeatures / 3.0) * 40.0;
        }

        return Math.min(100.0, score);
    }

    private void generateRecommendations() {
        // Gerar recomendações baseadas nos scores
        if (structureScore < 70) {
            recommendations.add("Improve process structure integrity");
            weaknesses.add("Low structure score: " + structureScore);

            ActionItem action = new ActionItem();
            action.setTitle("Improve Structure");
            action.setDescription("Enhance process graph integrity and node validation");
            action.setPriority(ActionItem.ActionPriority.HIGH);
            action.setCategory(ActionItem.ActionCategory.STRUCTURE);
            action.setExpectedImpact("Increase structure score by 10-15 points");
            actionItems.add(action);
        }

        if (documentationScore < 70) {
            recommendations.add("Enhance process documentation");
            weaknesses.add("Insufficient documentation: " + documentationScore);

            ActionItem action = new ActionItem();
            action.setTitle("Improve Documentation");
            action.setDescription("Add comprehensive descriptions and metadata");
            action.setPriority(ActionItem.ActionPriority.MEDIUM);
            action.setCategory(ActionItem.ActionCategory.DOCUMENTATION);
            action.setExpectedImpact("Increase documentation score by 15-20 points");
            actionItems.add(action);
        }

        if (complexityScore < 70) {
            recommendations.add("Reduce process complexity");
            weaknesses.add("High complexity detected: " + complexityScore);

            ActionItem action = new ActionItem();
            action.setTitle("Reduce Complexity");
            action.setDescription("Simplify process flows and reduce cyclomatic complexity");
            action.setPriority(ActionItem.ActionPriority.HIGH);
            action.setCategory(ActionItem.ActionCategory.MAINTAINABILITY);
            action.setExpectedImpact("Increase complexity score by 10-12 points");
            actionItems.add(action);
        }

        if (standardizationScore < 70) {
            recommendations.add("Improve naming and type standardization");
            weaknesses.add("Poor standardization: " + standardizationScore);

            ActionItem action = new ActionItem();
            action.setTitle("Standardize Naming");
            action.setDescription("Apply consistent naming conventions and type standards");
            action.setPriority(ActionItem.ActionPriority.MEDIUM);
            action.setCategory(ActionItem.ActionCategory.MAINTAINABILITY);
            action.setExpectedImpact("Increase standardization score by 12-15 points");
            actionItems.add(action);
        }

        // Identificar pontos fortes
        if (structureScore >= 80) {
            strengths.add("Excellent process structure");
        }
        if (documentationScore >= 80) {
            strengths.add("Well documented process");
        }
        if (complexityScore >= 80) {
            strengths.add("Optimal complexity level");
        }
        if (standardizationScore >= 80) {
            strengths.add("High standardization compliance");
        }

        // Se todos os scores são altos
        if (overallScore >= 85) {
            strengths.add("AI-Ready process with enterprise-grade quality");
        }
    }

    private void setTargetScores() {
        targetScores = new HashMap<>();
        targetScores.put("structure", Math.min(100.0, structureScore + 10));
        targetScores.put("documentation", Math.min(100.0, documentationScore + 15));
        targetScores.put("complexity", Math.min(100.0, complexityScore + 10));
        targetScores.put("standardization", Math.min(100.0, standardizationScore + 12));
        targetScores.put("overall", Math.min(100.0, overallScore + 12));
    }

    // Getters and setters
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

    public ScoreMethodology getMethodology() { return methodology; }
    public void setMethodology(ScoreMethodology methodology) { this.methodology = methodology; }

    public Map<String, Double> getTargetScores() { return targetScores; }
    public void setTargetScores(Map<String, Double> targetScores) { this.targetScores = targetScores; }

    public List<ActionItem> getActionItems() { return actionItems; }
    public void setActionItems(List<ActionItem> actionItems) { this.actionItems = actionItems; }

    public ScoreHistory getHistory() { return history; }
    public void setHistory(ScoreHistory history) { this.history = history; }
}
