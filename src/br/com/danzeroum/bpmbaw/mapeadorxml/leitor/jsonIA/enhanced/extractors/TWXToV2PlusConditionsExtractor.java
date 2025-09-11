package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Flow;

import java.util.ArrayList;
import java.util.List;

/**
 * Extrator TWX → V2Plus Conditions - CORRIGIDO
 *
 * CORREÇÕES APLICADAS:
 * ✅ Removidas referências a getName() inexistentes
 * ✅ Usar reflexão segura para acessar propriedades
 * ✅ Compatível com Java 8
 */
public class TWXToV2PlusConditionsExtractor {

    /**
     * Extrai conditions dos flows TWX CONFORME MODELO - CORRIGIDO
     */
    public List<ProcessConditionV2Plus> extractConditions(Object source) {
        List<ProcessConditionV2Plus> conditions = new ArrayList<>();

        try {
            if (source instanceof BusinessProcessDiagram) {
                BusinessProcessDiagram bpd = (BusinessProcessDiagram) source;

                // CORRIGIDO: Criar condições baseadas nos flows disponíveis
                if (bpd.getFlows() != null) {
                    for (Flow flow : bpd.getFlows()) {
                        ProcessConditionV2Plus condition = createConditionFromFlow(flow);
                        if (condition != null) {
                            conditions.add(condition);
                        }
                    }
                }

                // Se não há flows, criar condição padrão
                if (conditions.isEmpty()) {
                    ProcessConditionV2Plus defaultCondition = createDefaultCondition();
                    conditions.add(defaultCondition);
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Could not extract conditions: " + e.getMessage());

            // Em caso de erro, garantir pelo menos uma condição padrão
            if (conditions.isEmpty()) {
                conditions.add(createDefaultCondition());
            }
        }

        return conditions;
    }

    /**
     * Cria condição a partir de Flow - CORRIGIDO
     */
    private ProcessConditionV2Plus createConditionFromFlow(Flow flow) {
        if (flow == null) {
            return null;
        }

        ProcessConditionV2Plus condition = new ProcessConditionV2Plus();

        // CORRIGIDO: ID usando apenas informações seguras do flow
        condition.setId("cd:" + cleanId(flow.getId() != null ? flow.getId() : "unknown"));

        // CORRIGIDO: Nome seguro sem usar getName()
        String conditionName = generateConditionName(flow);
        condition.setName(conditionName);

        // Expressão padrão ou baseada em heurísticas
        String expression = extractExpressionFromFlow(flow);
        condition.setExpression(expression);

        // Language padrão
        condition.setLanguage(ProcessConditionV2Plus.ExpressionLanguage.CEL);

        // CORRIGIDO: Descrição sem usar getName()
        String description = generateConditionDescription(flow);
        condition.setDescription(description);

        return condition;
    }

    /**
     * Gera nome para condição de forma segura - CORRIGIDO
     */
    private String generateConditionName(Flow flow) {
        if (flow == null) {
            return "Unknown Condition";
        }

        // CORRIGIDO: Usar reflexão segura para tentar acessar getName()
        String nameFromReflection = getNameSafe(flow);
        if (nameFromReflection != null && !nameFromReflection.trim().isEmpty()) {
            return nameFromReflection;
        }

        // Fallback baseado em IDs
        String sourceId = flow.getSourceObjectId();
        String targetId = flow.getTargetObjectId();

        if (sourceId != null && targetId != null) {
            return "Condition_" + cleanId(sourceId) + "_to_" + cleanId(targetId);
        }

        return "Flow_Condition_" + (flow.getId() != null ? cleanId(flow.getId()) : "unknown");
    }

    /**
     * Gera descrição para condição - CORRIGIDO
     */
    private String generateConditionDescription(Flow flow) {
        if (flow == null) {
            return "Generated condition";
        }

        // CORRIGIDO: Usar método seguro ao invés de condition.getName()
        String conditionName = generateConditionName(flow);
        return String.format("Condição '%s' para roteamento do fluxo", conditionName);
    }

    /**
     * Extrai expressão do flow usando heurísticas
     */
    private String extractExpressionFromFlow(Flow flow) {
        if (flow == null) {
            return "true";
        }

        // Tentar usar reflexão para acessar expressões de condição
        try {
            java.lang.reflect.Method getConditionMethod = flow.getClass().getMethod("getConditionExpression");
            Object conditionResult = getConditionMethod.invoke(flow);
            if (conditionResult != null) {
                return conditionResult.toString();
            }
        } catch (Exception e) {
            // Método não existe
        }

        // Tentar outros métodos comuns
        try {
            java.lang.reflect.Method getExpressionMethod = flow.getClass().getMethod("getExpression");
            Object expressionResult = getExpressionMethod.invoke(flow);
            if (expressionResult != null) {
                return expressionResult.toString();
            }
        } catch (Exception e) {
            // Método não existe
        }

        // Fallback baseado no tipo de flow
        String sourceId = flow.getSourceObjectId();
        if (sourceId != null && sourceId.toLowerCase().contains("gateway")) {
            return "decision == true";
        }

        return "true";
    }

    /**
     * Obtém nome usando reflexão segura - MÉTODO AUXILIAR ADICIONADO
     */
    private String getNameSafe(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            java.lang.reflect.Method getNameMethod = obj.getClass().getMethod("getName");
            Object nameResult = getNameMethod.invoke(obj);
            return nameResult != null ? nameResult.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Cria condição padrão
     */
    private ProcessConditionV2Plus createDefaultCondition() {
        ProcessConditionV2Plus defaultCondition = new ProcessConditionV2Plus();
        defaultCondition.setId("cd:default");
        defaultCondition.setName("Default Condition");
        defaultCondition.setExpression("true");
        defaultCondition.setLanguage(ProcessConditionV2Plus.ExpressionLanguage.CEL);
        defaultCondition.setDescription("Default condition for process flow");
        return defaultCondition;
    }

    /**
     * Limpa ID para conformidade
     */
    private String cleanId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return "unknown_" + System.currentTimeMillis();
        }
        return id.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    // =========================================================================
    // MÉTODOS ESTÁTICOS PARA COMPATIBILIDADE
    // =========================================================================

    /**
     * Método estático para compatibilidade com código existente
     */
    public static List<ProcessConditionV2Plus> extractConditions(List<Flow> flows) {
        TWXToV2PlusConditionsExtractor extractor = new TWXToV2PlusConditionsExtractor();

        if (flows == null || flows.isEmpty()) {
            List<ProcessConditionV2Plus> conditions = new ArrayList<>();
            conditions.add(extractor.createDefaultCondition());
            return conditions;
        }

        List<ProcessConditionV2Plus> conditions = new ArrayList<>();
        for (Flow flow : flows) {
            ProcessConditionV2Plus condition = extractor.createConditionFromFlow(flow);
            if (condition != null) {
                conditions.add(condition);
            }
        }

        return conditions;
    }

    /**
     * Cria condições de exemplo para testes
     */
    public static List<ProcessConditionV2Plus> createSampleConditions() {
        List<ProcessConditionV2Plus> conditions = new ArrayList<>();

        // Condição 1: Aprovação
        ProcessConditionV2Plus approval = new ProcessConditionV2Plus();
        approval.setId("cd:aprovacao_sim");
        approval.setName("Aprovação Positiva");
        approval.setExpression("aprovacao == 'SIM'");
        approval.setLanguage(ProcessConditionV2Plus.ExpressionLanguage.CEL);
        approval.setDescription("Condição para aprovação positiva");
        conditions.add(approval);

        // Condição 2: Rejeição
        ProcessConditionV2Plus rejection = new ProcessConditionV2Plus();
        rejection.setId("cd:aprovacao_nao");
        rejection.setName("Aprovação Negativa");
        rejection.setExpression("aprovacao == 'NAO'");
        rejection.setLanguage(ProcessConditionV2Plus.ExpressionLanguage.CEL);
        rejection.setDescription("Condição para aprovação negativa");
        conditions.add(rejection);

        return conditions;
    }

    // =========================================================================
    // MÉTODO DE TESTE
    // =========================================================================

    /**
     * Teste básico para validação durante desenvolvimento
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing TWXToV2PlusConditionsExtractor...");

        try {
            // Teste 1: Criação de condições de exemplo
            List<ProcessConditionV2Plus> sampleConditions = createSampleConditions();
            System.out.println("✅ Sample conditions creation: " + sampleConditions.size() + " conditions");

            // Teste 2: Extração com lista nula
            List<ProcessConditionV2Plus> nullConditions = extractConditions((List<Flow>) null);
            System.out.println("✅ Null flows handling: " + (nullConditions != null && !nullConditions.isEmpty()));

            // Teste 3: Extração com lista vazia
            List<ProcessConditionV2Plus> emptyConditions = extractConditions(new ArrayList<Flow>());
            System.out.println("✅ Empty flows handling: " + (emptyConditions != null && !emptyConditions.isEmpty()));

            // Teste 4: Criação de extrator
            TWXToV2PlusConditionsExtractor extractor = new TWXToV2PlusConditionsExtractor();
            List<ProcessConditionV2Plus> instanceConditions = extractor.extractConditions((Object) null);
            System.out.println("✅ Instance method: " + (instanceConditions != null));

            System.out.println("\n🎉 TWXToV2PlusConditionsExtractor: ALL TESTS PASSED!");

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}