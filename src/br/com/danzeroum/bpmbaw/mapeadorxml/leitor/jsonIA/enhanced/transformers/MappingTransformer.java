package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.transformers;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.InputMappingV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.OutputMappingV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessMappingsV2Plus;
import java.util.ArrayList;
import java.util.List;

/**
 * Transforma mapeamentos de dados do formato legado (JavaScript) para o
 * formato moderno e declarativo V2+ (CEL).
 */
public class MappingTransformer {

    /**
     * Converte uma expressão de mapeamento de JavaScript para CEL.
     * Realiza substituições simples para funções e prefixos comuns.
     *
     * @param js A string de código JavaScript a ser convertida.
     * @return A string convertida para o formato CEL.
     */
    public String jsToCel(String js) {
        if (js == null || js.isEmpty()) return "";

        String s = js.trim();

        // Conversões de funções
        s = s.replaceAll("\\bNumber\\((.*?)\\)", "toNumber($1)");
        s = s.replaceAll("\\bString\\((.*?)\\)", "toString($1)");
        s = s.replaceAll("\\bBoolean\\((.*?)\\)", "toBool($1)");

        // Remove prefixos tw
        s = s.replace("tw.local.", "");
        s = s.replace("tw.system.", "system.");
        s = s.replace("tw.env.", "env.");

        return s;
    }

    /**
     * Consolida os mapeamentos de um artefato de processo em uma única
     * estrutura ProcessMappingsV2Plus.
     *
     * @param artifact O artefato do processo (atualmente um placeholder).
     * @return Um objeto ProcessMappingsV2Plus consolidado.
     */
    public ProcessMappingsV2Plus consolidateFromFlow(Object artifact) {
        ProcessMappingsV2Plus m = new ProcessMappingsV2Plus();
        m.setExprLang("cel");

        List<InputMappingV2Plus> inputs = new ArrayList<>();
        List<OutputMappingV2Plus> outputs = new ArrayList<>();

        // TODO: Implementar a extração real dos mapeamentos de artifact.getFlow()
        // Por enquanto, a estrutura é criada vazia, pronta para ser populada.

        m.setInputs(inputs);
        m.setOutputs(outputs);

        return m;
    }
}