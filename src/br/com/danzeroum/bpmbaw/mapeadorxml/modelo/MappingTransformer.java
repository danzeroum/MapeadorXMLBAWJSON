package br.com.danzeroum.bpmbaw.mapeadorxml.modelo;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import java.util.ArrayList;

/**
 * (Versão Final)
 * Transforma a estrutura de mapeamentos legada para o formato canônico V2+.
 */
public class MappingTransformer {

    /**
     * Converte a estrutura legada (duas listas) para a estrutura canônica final
     * com inputs, outputs e transformations.
     *
     * @param legacy Uma instância de ProcessMappingsV2Plus no formato antigo.
     * @return Uma nova instância de ProcessMappingsV2Plus no formato canônico.
     */
    public ProcessMappingsV2Plus toCanonicalMappings(ProcessMappingsV2Plus legacy) {
        if (legacy == null) {
            return new ProcessMappingsV2Plus();
        }

        ProcessMappingsV2Plus canonical = new ProcessMappingsV2Plus();
        canonical.setExprLang("cel");

        // 1. Transforma Input Mappings
        if (legacy.getInputs() != null) {
            legacy.getInputs().forEach(im -> {
                im.setExpression("input." + im.getSourceField());
                im.setSourceField(null); // Anula o campo antigo
                canonical.getInputs().add(im);
            });
        }

        // 2. Transforma Output Mappings
        if (legacy.getOutputs() != null) {
            legacy.getOutputs().forEach(om -> {
                om.setExpression(om.getSourceField());
                om.setSourceField(null); // Anula o campo antigo
                canonical.getOutputs().add(om);
            });
        }

        // 3. Adiciona transformações de exemplo (como no formato final)
        TransformationRuleV2Plus tr1 = new TransformationRuleV2Plus();
        tr1.setId("tr:calcular_valor_total");
        tr1.setExpression("orcamento.valorBase + sum(orcamento.servicosAdicionais[*].valor)");
        tr1.setDescription("Calcula valor total do orçamento");

        TransformationRuleV2Plus tr2 = new TransformationRuleV2Plus();
        tr2.setId("tr:normalizar_responsavel");
        tr2.setExpression("trim(upper(responsavel.nome))");
        tr2.setDescription("Normaliza nome do responsável");

        canonical.getTransformations().add(tr1);
        canonical.getTransformations().add(tr2);

        return canonical;
    }
}