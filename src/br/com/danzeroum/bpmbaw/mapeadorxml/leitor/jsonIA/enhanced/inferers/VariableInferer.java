package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.inferers;

/**
 * Infere e normaliza informações de variáveis, como tipos de dados e cardinalidade,
 * para o formato canônico V2+.
 */
public class VariableInferer {

    /**
     * Mapeia um typeId do modelo antigo para o formato typeRef canônico (ex: "dt:string@1").
     *
     * @param typeId O identificador de tipo legado (ex: "String", "canonical-recondicionamento").
     * @return O typeRef normalizado no formato V2+.
     */
    public String mapTypeIdToTypeRef(String typeId) {
        if (typeId == null || typeId.isEmpty()) {
            return "dt:any@1"; // Tipo padrão para segurança
        }

        // Remove o prefixo "canonical-" para obter o nome limpo
        if (typeId.startsWith("canonical-")) {
            String name = typeId.substring("canonical-".length());
            return "dt:" + name + "@1";
        }

        // Mapeia tipos primitivos conhecidos
        switch (typeId) {
            case "String":  return "dt:string@1";
            case "Integer": return "dt:integer@1";
            case "Boolean": return "dt:boolean@1";
            case "Date":    return "dt:date@1";
            case "Decimal": return "dt:decimal@1";
            case "Time":    return "dt:time@1";
            default:        return "dt:" + typeId.toLowerCase() + "@1"; // Fallback para outros tipos
        }
    }

    /**
     * Determina a cardinalidade da variável com base no atributo 'isList'.
     *
     * @param isList Verdadeiro se a variável for uma lista.
     * @return "many" se for lista, "one" caso contrário.
     */
    public String determineCardinality(boolean isList) {
        return isList ? "many" : "one";
    }
}