package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.fixes;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessLogicV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.TransformationRuleV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ValidationRuleV2Plus;

/**
 * Correção para os problemas de tipos incompatíveis
 */
public class TypeCompatibilityFix {

    /**
     * USAR ESTAS CONVERSÕES NO ProcessFlowV2Plus:
     */

    // Para o problema de DataTransformationV2Plus -> TransformationRuleV2Plus
    public static TransformationRuleV2Plus convertDataTransformation(
            ProcessLogicV2Plus.DataTransformationV2Plus dataTransform) {

        TransformationRuleV2Plus rule = new TransformationRuleV2Plus();
        rule.setId(dataTransform.getId());
        rule.setName(dataTransform.name);
        rule.setDescription(dataTransform.description);
        // Mapear outros campos conforme necessário
        return rule;
    }

    // Para o problema de ValidationRuleV2Plus -> ValidationRuleV2Plus
    public static ValidationRuleV2Plus convertValidationRule(
            ValidationRuleV2Plus logicValidation) {

        ValidationRuleV2Plus rule = new ValidationRuleV2Plus();
        rule.setId(logicValidation.getId());
        rule.setName(logicValidation.getName());
        rule.setDescription(logicValidation.getDescription());
        // Mapear outros campos conforme necessário
        return rule;
    }
}
