package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.inferers;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * VariableInferer - Inferência inteligente de tipos e propriedades de variáveis
 *
 * Esta classe é responsável por:
 * - Mapear typeId legado para typeRef canônico
 * - Inferir cardinality baseado em propriedades
 * - Classificar variáveis por escopo (input/output/private)
 * - Detectar tipos complexos e suas propriedades
 * - Normalizar nomenclaturas e formatos
 *
 * @version 3.0.0 - Versão completa Java 8
 * @author Enhanced BAW Analysis System
 */
public class VariableInferer {

    // =========================================================================
    // CONSTANTES E PADRÕES
    // =========================================================================

    private static final Map<String, String> PRIMITIVE_TYPE_MAPPING;
    private static final Map<String, String> CANONICAL_TYPE_MAPPING;
    private static final Set<String> COLLECTION_INDICATORS;
    private static final Set<String> INPUT_INDICATORS;
    private static final Set<String> OUTPUT_INDICATORS;

    static {
        // Mapeamento de tipos primitivos
        PRIMITIVE_TYPE_MAPPING = new HashMap<String, String>();
        PRIMITIVE_TYPE_MAPPING.put("String", "dt:string@1");
        PRIMITIVE_TYPE_MAPPING.put("string", "dt:string@1");
        PRIMITIVE_TYPE_MAPPING.put("text", "dt:string@1");
        PRIMITIVE_TYPE_MAPPING.put("Integer", "dt:integer@1");
        PRIMITIVE_TYPE_MAPPING.put("integer", "dt:integer@1");
        PRIMITIVE_TYPE_MAPPING.put("int", "dt:integer@1");
        PRIMITIVE_TYPE_MAPPING.put("Long", "dt:long@1");
        PRIMITIVE_TYPE_MAPPING.put("long", "dt:long@1");
        PRIMITIVE_TYPE_MAPPING.put("Boolean", "dt:boolean@1");
        PRIMITIVE_TYPE_MAPPING.put("boolean", "dt:boolean@1");
        PRIMITIVE_TYPE_MAPPING.put("bool", "dt:boolean@1");
        PRIMITIVE_TYPE_MAPPING.put("Date", "dt:date@1");
        PRIMITIVE_TYPE_MAPPING.put("date", "dt:date@1");
        PRIMITIVE_TYPE_MAPPING.put("DateTime", "dt:datetime@1");
        PRIMITIVE_TYPE_MAPPING.put("datetime", "dt:datetime@1");
        PRIMITIVE_TYPE_MAPPING.put("Time", "dt:time@1");
        PRIMITIVE_TYPE_MAPPING.put("time", "dt:time@1");
        PRIMITIVE_TYPE_MAPPING.put("Decimal", "dt:decimal@1");
        PRIMITIVE_TYPE_MAPPING.put("decimal", "dt:decimal@1");
        PRIMITIVE_TYPE_MAPPING.put("Double", "dt:decimal@1");
        PRIMITIVE_TYPE_MAPPING.put("double", "dt:decimal@1");
        PRIMITIVE_TYPE_MAPPING.put("Float", "dt:decimal@1");
        PRIMITIVE_TYPE_MAPPING.put("float", "dt:decimal@1");
        PRIMITIVE_TYPE_MAPPING.put("JSON", "dt:json@1");
        PRIMITIVE_TYPE_MAPPING.put("json", "dt:json@1");
        PRIMITIVE_TYPE_MAPPING.put("Object", "dt:object@1");
        PRIMITIVE_TYPE_MAPPING.put("object", "dt:object@1");
        PRIMITIVE_TYPE_MAPPING.put("Any", "dt:any@1");
        PRIMITIVE_TYPE_MAPPING.put("any", "dt:any@1");

        // Mapeamento de tipos canônicos comuns
        CANONICAL_TYPE_MAPPING = new HashMap<String, String>();
        CANONICAL_TYPE_MAPPING.put("canonical-employee", "dt:employee@1");
        CANONICAL_TYPE_MAPPING.put("canonical-namevaluepair", "dt:namevaluepair@1");
        CANONICAL_TYPE_MAPPING.put("canonical-recondicionamento", "dt:recondicionamento@1");
        CANONICAL_TYPE_MAPPING.put("canonical-orcamento", "dt:orcamento@1");
        CANONICAL_TYPE_MAPPING.put("canonical-viatura", "dt:viatura@1");
        CANONICAL_TYPE_MAPPING.put("canonical-instalacao", "dt:instalacao@1");
        CANONICAL_TYPE_MAPPING.put("canonical-transporteviatura", "dt:transporteviatura@1");
        CANONICAL_TYPE_MAPPING.put("canonical-pedidoantecipacao", "dt:pedidoantecipacao@1");
        CANONICAL_TYPE_MAPPING.put("canonical-historicodatas", "dt:historicodatas@1");
        CANONICAL_TYPE_MAPPING.put("canonical-datetime-sc", "dt:datetime-sc@1");
        CANONICAL_TYPE_MAPPING.put("canonical-grcrpricingtableconfig", "dt:grcrpricingtableconfig@1");
        CANONICAL_TYPE_MAPPING.put("canonical-validation", "dt:validation@1");
        CANONICAL_TYPE_MAPPING.put("canonical-errorlist", "dt:errorlist@1");

        // Indicadores de coleção
        COLLECTION_INDICATORS = new HashSet<String>();
        COLLECTION_INDICATORS.add("list");
        COLLECTION_INDICATORS.add("array");
        COLLECTION_INDICATORS.add("collection");
        COLLECTION_INDICATORS.add("items");
        COLLECTION_INDICATORS.add("elements");
        COLLECTION_INDICATORS.add("set");
        COLLECTION_INDICATORS.add("map");

        // Indicadores de variáveis de entrada
        INPUT_INDICATORS = new HashSet<String>();
        INPUT_INDICATORS.add("input");
        INPUT_INDICATORS.add("entrada");
        INPUT_INDICATORS.add("request");
        INPUT_INDICATORS.add("param");
        INPUT_INDICATORS.add("arg");
        INPUT_INDICATORS.add("in_");
        INPUT_INDICATORS.add("recondicionamento");
        INPUT_INDICATORS.add("viatura");
        INPUT_INDICATORS.add("instalacao");

        // Indicadores de variáveis de saída
        OUTPUT_INDICATORS = new HashSet<String>();
        OUTPUT_INDICATORS.add("output");
        OUTPUT_INDICATORS.add("saida");
        OUTPUT_INDICATORS.add("result");
        OUTPUT_INDICATORS.add("response");
        OUTPUT_INDICATORS.add("return");
        OUTPUT_INDICATORS.add("out_");
        OUTPUT_INDICATORS.add("orcamento");
        OUTPUT_INDICATORS.add("validation");
        OUTPUT_INDICATORS.add("final");
    }

    // =========================================================================
    // MÉTODOS PRINCIPAIS DE INFERÊNCIA
    // =========================================================================

    /**
     * Mapeia typeId para typeRef canônico
     * @param typeId O ID do tipo (legado ou moderno)
     * @return O typeRef canônico no formato dt:*@1
     */
    public String mapTypeIdToTypeRef(String typeId) {
        if (typeId == null || typeId.trim().isEmpty()) {
            return "dt:any@1";
        }

        String normalized = typeId.trim();

        // Verificar se já é um typeRef
        if (normalized.startsWith("dt:")) {
            return normalized;
        }

        // Verificar mapeamento direto de tipos canônicos
        if (CANONICAL_TYPE_MAPPING.containsKey(normalized)) {
            return CANONICAL_TYPE_MAPPING.get(normalized);
        }

        // Verificar tipos canônicos genéricos
        if (normalized.startsWith("canonical-")) {
            String typeName = normalized.substring("canonical-".length());
            return "dt:" + typeName.toLowerCase() + "@1";
        }

        // Verificar tipos primitivos
        if (PRIMITIVE_TYPE_MAPPING.containsKey(normalized)) {
            return PRIMITIVE_TYPE_MAPPING.get(normalized);
        }

        // Verificar TWClass/BusinessObject
        if (normalized.startsWith("twclass_")) {
            String className = normalized.substring("twclass_".length());
            return "dt:" + className.toLowerCase() + "@1";
        }

        // Tipo customizado - converter para lowercase
        return "dt:" + normalized.toLowerCase().replace(" ", "_") + "@1";
    }

    /**
     * Determina cardinality baseado em propriedades
     * @param isList Se é uma lista/array
     * @return "one" ou "many"
     */
    public String determineCardinality(boolean isList) {
        return isList ? "many" : "one";
    }

    /**
     * Determina cardinality baseado no nome da variável
     * @param variableName Nome da variável
     * @return "one" ou "many"
     */
    public String inferCardinalityFromName(String variableName) {
        if (variableName == null) return "one";

        String lowerName = variableName.toLowerCase();

        // Verificar indicadores de coleção no nome
        for (String indicator : COLLECTION_INDICATORS) {
            if (lowerName.contains(indicator)) {
                return "many";
            }
        }

        // Verificar sufixos plurais
        if (lowerName.endsWith("s") || lowerName.endsWith("es") ||
                lowerName.endsWith("ies") || lowerName.endsWith("aos") ||
                lowerName.endsWith("oes")) {
            // Exceções para palavras que terminam em 's' mas são singulares
            if (!lowerName.endsWith("status") && !lowerName.endsWith("address") &&
                    !lowerName.endsWith("process") && !lowerName.endsWith("class")) {
                return "many";
            }
        }

        return "one";
    }

    /**
     * Classifica o escopo da variável (input/output/private)
     * @param variableName Nome da variável
     * @return "input", "output" ou "private"
     */
    public String inferVariableScope(String variableName) {
        if (variableName == null) return "private";

        String lowerName = variableName.toLowerCase();

        // Verificar indicadores de entrada
        for (String indicator : INPUT_INDICATORS) {
            if (lowerName.contains(indicator) || lowerName.startsWith(indicator)) {
                return "input";
            }
        }

        // Verificar indicadores de saída
        for (String indicator : OUTPUT_INDICATORS) {
            if (lowerName.contains(indicator) || lowerName.startsWith(indicator)) {
                return "output";
            }
        }

        // Padrões especiais
        if (lowerName.contains("request") || lowerName.startsWith("req_")) {
            return "input";
        }

        if (lowerName.contains("response") || lowerName.startsWith("res_") ||
                lowerName.contains("result")) {
            return "output";
        }

        // Variáveis auxiliares e temporárias
        if (lowerName.contains("aux") || lowerName.contains("temp") ||
                lowerName.contains("local") || lowerName.contains("private")) {
            return "private";
        }

        return "private";
    }

    /**
     * Infere se a variável é nullable baseado no nome e tipo
     * @param variableName Nome da variável
     * @param typeId ID do tipo
     * @return true se nullable, false caso contrário
     */
    public boolean inferNullable(String variableName, String typeId) {
        if (variableName == null) return true;

        String lowerName = variableName.toLowerCase();

        // Variáveis obrigatórias geralmente não são nullable
        if (lowerName.contains("required") || lowerName.contains("mandatory") ||
                lowerName.contains("obrigatorio")) {
            return false;
        }

        // IDs geralmente não são nullable
        if (lowerName.endsWith("id") || lowerName.equals("id") ||
                lowerName.contains("identifier")) {
            return false;
        }

        // Variáveis opcionais são nullable
        if (lowerName.contains("optional") || lowerName.contains("opcional") ||
                lowerName.endsWith("opt")) {
            return true;
        }

        // Por padrão, considerar nullable para flexibilidade
        return true;
    }

    /**
     * Gera uma descrição automática para a variável
     * @param variableName Nome da variável
     * @param typeRef Referência do tipo
     * @param scope Escopo da variável
     * @return Descrição gerada
     */
    public String generateDescription(String variableName, String typeRef, String scope) {
        if (variableName == null) return "Variable without name";

        StringBuilder desc = new StringBuilder();

        // Adicionar escopo
        if ("input".equals(scope)) {
            desc.append("Input parameter ");
        } else if ("output".equals(scope)) {
            desc.append("Output result ");
        } else {
            desc.append("Internal variable ");
        }

        // Adicionar nome formatado
        String formattedName = formatVariableName(variableName);
        desc.append("for ").append(formattedName);

        // Adicionar tipo se conhecido
        if (typeRef != null && !typeRef.equals("dt:any@1")) {
            String typeName = extractTypeNameFromRef(typeRef);
            desc.append(" of type ").append(typeName);
        }

        return desc.toString();
    }

    /**
     * Normaliza uma variável completa
     * @param variable A variável a ser normalizada
     */
    public void normalizeVariable(ProcessVariableV2Plus variable) {
        if (variable == null) return;

        // Normalizar typeRef
        if (variable.getTypeRef() == null || variable.getTypeRef().isEmpty()) {
            String typeId = variable.getTypeId();
            if (typeId == null || typeId.isEmpty()) {
                typeId = variable.getType();
            }
            variable.setTypeRef(mapTypeIdToTypeRef(typeId));
        }

        // Normalizar cardinality
        if (variable.getCardinality() == null || variable.getCardinality().isEmpty()) {
            if (variable.isList()) {
                variable.setCardinality("many");
            } else {
                variable.setCardinality(inferCardinalityFromName(variable.getName()));
            }
        }

        // Inferir nullable se não definido
        if (variable.getNullable() == null) {
            variable.setNullable(inferNullable(variable.getName(), variable.getTypeId()));
        }

        // Gerar descrição se não existir
        if (variable.getDescription() == null || variable.getDescription().isEmpty()) {
            String scope = inferVariableScope(variable.getName());
            variable.setDescription(generateDescription(variable.getName(),
                    variable.getTypeRef(),
                    scope));
        }

        // Adicionar metadados de escopo
        variable.addMetadata("inferred_scope", inferVariableScope(variable.getName()));
        variable.addMetadata("normalized_at", new Date().toString());
    }

    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================

    /**
     * Formata o nome da variável para exibição
     * @param variableName Nome original
     * @return Nome formatado
     */
    private String formatVariableName(String variableName) {
        if (variableName == null) return "";

        // Converter camelCase para texto legível
        String formatted = variableName.replaceAll("([a-z])([A-Z])", "$1 $2");

        // Converter snake_case para texto legível
        formatted = formatted.replace("_", " ");

        // Capitalizar primeira letra
        if (formatted.length() > 0) {
            formatted = Character.toUpperCase(formatted.charAt(0)) +
                    formatted.substring(1).toLowerCase();
        }

        return formatted;
    }

    /**
     * Extrai o nome do tipo de um typeRef
     * @param typeRef Referência do tipo (dt:nome@versao)
     * @return Nome do tipo
     */
    private String extractTypeNameFromRef(String typeRef) {
        if (typeRef == null || !typeRef.startsWith("dt:")) {
            return "unknown";
        }

        String withoutPrefix = typeRef.substring(3); // Remove "dt:"
        int atIndex = withoutPrefix.indexOf('@');

        if (atIndex > 0) {
            return withoutPrefix.substring(0, atIndex);
        }

        return withoutPrefix;
    }

    /**
     * Verifica se um tipo é primitivo
     * @param typeId ID do tipo
     * @return true se primitivo
     */
    public boolean isPrimitiveType(String typeId) {
        if (typeId == null) return false;
        return PRIMITIVE_TYPE_MAPPING.containsKey(typeId);
    }

    /**
     * Verifica se um tipo é canônico
     * @param typeId ID do tipo
     * @return true se canônico
     */
    public boolean isCanonicalType(String typeId) {
        if (typeId == null) return false;
        return typeId.startsWith("canonical-") || CANONICAL_TYPE_MAPPING.containsKey(typeId);
    }

    /**
     * Retorna todos os tipos primitivos suportados
     * @return Set com os nomes dos tipos primitivos
     */
    public Set<String> getSupportedPrimitiveTypes() {
        return new HashSet<String>(PRIMITIVE_TYPE_MAPPING.keySet());
    }

    /**
     * Retorna todos os tipos canônicos conhecidos
     * @return Set com os nomes dos tipos canônicos
     */
    public Set<String> getKnownCanonicalTypes() {
        return new HashSet<String>(CANONICAL_TYPE_MAPPING.keySet());
    }
}