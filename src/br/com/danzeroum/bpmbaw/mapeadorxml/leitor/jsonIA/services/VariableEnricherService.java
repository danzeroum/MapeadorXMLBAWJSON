package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportGeneratorV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.Property;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Serviço especializado em enriquecer variáveis, resolver seus tipos de dados complexos
 * e construir as definições de Business Objects para o relatório JSON.
 * Handles variable enrichment and type resolution for process artifacts.
 */
public class VariableEnricherService {

    private static final Set<String> PRIMITIVE_TYPES;
    static {
        Set<String> set = new HashSet<>();
        set.add("String");
        set.add("Integer");
        set.add("Boolean");
        set.add("Decimal");
        set.add("Date");
        set.add("Time");
        set.add("DateTime");
        set.add("ANY");
        PRIMITIVE_TYPES = Collections.unmodifiableSet(set);
    }

    private static final Set<String> STOP_WORDS;
    static {
        Set<String> set = new HashSet<>();
        set.add("true");
        set.add("false");
        set.add("null");
        set.add("and");
        set.add("or");
        set.add("not");
        set.add("if");
        set.add("then");
        set.add("else");
        set.add("return");
        set.add("var");
        set.add("let");
        set.add("function");
        set.add("tw");
        set.add("this");
        STOP_WORDS = Collections.unmodifiableSet(set);
    }

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("\\b([A-Za-z_][A-Za-z0-9_]*)\\b");

    private final JsonReportGeneratorV2 reportGenerator;
    private final ProcessLoaderV2Plus processLoader;

    public VariableEnricherService(JsonReportGeneratorV2 reportGenerator, ProcessLoaderV2Plus processLoader) {
        this.reportGenerator = reportGenerator;
        this.processLoader = processLoader;
    }

    /**
     * Ponto de entrada principal para enriquecer todas as variáveis de um artefato.
     * Enriches all variables in an artifact (input, output, private).
     * @param artifact O artefato JSON cujas variáveis serão enriquecidas.
     */
    public void enrichAllVariablesInArtifact(JsonReportV2.Artifact artifact) {
        if (artifact == null || artifact.getVariables() == null) {
            return;
        }

        // Enrich input variables
        if (artifact.getVariables().getInput() != null) {
            artifact.getVariables().getInput().forEach(var -> enrichVariableInfo(var, new HashSet<>()));
        }

        // Enrich output variables
        if (artifact.getVariables().getOutput() != null) {
            artifact.getVariables().getOutput().forEach(var -> enrichVariableInfo(var, new HashSet<>()));
        }

        // Enrich private variables (handle both getPrivate and getPrivite)
        List<JsonReportV2.VariableInfo> privateVars = getPrivateVariables(artifact);
        privateVars.forEach(var -> enrichVariableInfo(var, new HashSet<>()));
    }

    /**
     * Enriches a single variable with type information - versão pública
     */
    public void enrichVariableInfo(JsonReportV2.VariableInfo variable) {
        enrichVariableInfo(variable, new HashSet<>());
    }

    /**
     * Lógica recursiva principal para resolver o tipo de uma variável.
     * Core variable enrichment logic with type resolution.
     * @param varInfo O objeto da variável a ser enriquecido.
     * @param visitedTypes Um conjunto para evitar recursão infinita em tipos cíclicos.
     */
    private void enrichVariableInfo(JsonReportV2.VariableInfo varInfo, Set<String> visitedTypes) {
        if (varInfo == null || varInfo.getTypeId() == null || varInfo.getTypeId().trim().isEmpty()) {
            return;
        }

        String originalTypeId = varInfo.getTypeId();
        String cleanTypeId = normalizeIdSafe(originalTypeId);

        if (cleanTypeId == null || !visitedTypes.add(cleanTypeId)) {
            return; // Evita processamento duplicado ou ciclos
        }

        // Garante que a dependência (o TwClass do tipo) esteja carregada
        loadArtifactSafe(originalTypeId);
        Object artifact = getArtifactFromCacheSafe(cleanTypeId);

        if (artifact instanceof Teamworks && ((Teamworks) artifact).getTwClass() != null) {
            TwClass twClass = ((Teamworks) artifact).getTwClass();

            if (PRIMITIVE_TYPES.contains(twClass.getName())) {
                varInfo.setTypeId(twClass.getName()); // Simplifica para o nome do tipo primitivo
                return;
            }

            // Para tipos complexos, cria a definição do Business Object
            JsonReportV2.Definition boDefinition = createBusinessObjectDefinition(twClass, originalTypeId, visitedTypes);
            String canonicalId = reportGenerator.addBusinessObjectDefinition(boDefinition);
            varInfo.setTypeId(canonicalId); // Atualiza o typeId da variável para o ID canônico
        }
    }

    /**
     * Método alternativo para enriquecimento - compatibilidade
     */
    private void enrichVariable(JsonReportV2.VariableInfo varInfo, Set<String> visitedTypes) {
        enrichVariableInfo(varInfo, visitedTypes);
    }

    /**
     * Cria a definição estruturada de um Business Object a partir de um TwClass.
     * Creates a business object definition from a TwClass.
     */
    private JsonReportV2.Definition createBusinessObjectDefinition(TwClass twClass, String originalTypeId, Set<String> visitedTypes) {
        JsonReportV2.Definition definition = new JsonReportV2.Definition();
        definition.setTypeId(originalTypeId);
        definition.setTypeName(twClass.getName());

        if (twClass.getDefinition() != null && twClass.getDefinition().getProperties() != null) {
            for (Property prop : twClass.getDefinition().getProperties()) {
                JsonReportV2.PropertyStructure propStruct = createPropertyStructure(prop, visitedTypes);
                definition.getStructure().add(propStruct);
            }
        }
        return definition;
    }

    /**
     * Creates property structure from a TwClass property.
     */
    private JsonReportV2.PropertyStructure createPropertyStructure(Property property, Set<String> visitedTypes) {
        JsonReportV2.PropertyStructure propStructure = new JsonReportV2.PropertyStructure();
        propStructure.setName(property.getName());
        propStructure.setList(property.isArrayProperty());

        // Chamada recursiva para enriquecer o tipo da propriedade
        JsonReportV2.VariableInfo tempVarInfo = new JsonReportV2.VariableInfo();
        tempVarInfo.setTypeId(property.getClassRef());
        enrichVariableInfo(tempVarInfo, new HashSet<>(visitedTypes)); // Usa uma cópia do set de visitados
        propStructure.setTypeRef(tempVarInfo.getTypeId());

        return propStructure;
    }

    /**
     * Augments artifact variables based on parameter mappings from flow steps.
     * Should be called after flow analysis is complete.
     */
    public void augmentVariablesFromFlowMappings(JsonReportV2.Artifact artifact) {
        if (artifact == null || artifact.getFlow() == null) {
            return;
        }

        final String DEFAULT_TYPE = "String";

        for (JsonReportV2.FlowStep step : artifact.getFlow()) {
            JsonReportV2.ParameterMapping mapping = step.getParameterMapping();
            if (mapping == null) continue;

            // Process input mappings - source expressions become input variables
            for (JsonReportV2.Mapping inputMapping : mapping.getInput()) {
                if (inputMapping.getSource() == null) continue;

                List<String> candidateVars = extractVariableNamesFromExpression(inputMapping.getSource());
                for (String varName : candidateVars) {
                    ensureVariablePresent(artifact, varName, VariableDirection.INPUT, DEFAULT_TYPE, false);
                }
            }

            // Process output mappings - target variables become output variables
            for (JsonReportV2.Mapping outputMapping : mapping.getOutput()) {
                String targetVar = outputMapping.getTarget();
                if (targetVar == null || targetVar.trim().isEmpty()) continue;

                String rootVar = extractRootIdentifier(targetVar);
                ensureVariablePresent(artifact, rootVar, VariableDirection.OUTPUT, DEFAULT_TYPE, false);
            }
        }
    }

    /**
     * Ensures a variable is present in the artifact's variable lists.
     */
    private void ensureVariablePresent(JsonReportV2.Artifact artifact, String varName,
                                       VariableDirection direction, String typeGuess, boolean isList) {
        if (artifact == null || varName == null || varName.trim().isEmpty()) {
            return;
        }

        // Check if variable already exists
        if (variableAlreadyExists(artifact, varName)) {
            return;
        }

        // Create new variable
        JsonReportV2.VariableInfo variable = new JsonReportV2.VariableInfo();
        variable.setName(varName);
        variable.setTypeId(typeGuess != null ? typeGuess : "String");
        variable.setList(isList);

        // Try to enrich the variable type
        try {
            enrichVariableInfo(variable, new HashSet<>());
        } catch (Exception e) {
            // If enrichment fails, keep the guessed type
        }

        // Add to appropriate list
        addVariableToArtifact(artifact, variable, direction);
    }

    /**
     * Checks if a variable already exists in any of the artifact's variable lists.
     */
    private boolean variableAlreadyExists(JsonReportV2.Artifact artifact, String varName) {
        return hasVariableInList(artifact.getVariables().getInput(), varName) ||
                hasVariableInList(artifact.getVariables().getOutput(), varName) ||
                hasVariableInList(getPrivateVariables(artifact), varName);
    }

    /**
     * Checks if a variable exists in a specific list.
     */
    private boolean hasVariableInList(List<JsonReportV2.VariableInfo> list, String varName) {
        if (list == null || varName == null) return false;

        return list.stream()
                .anyMatch(v -> v != null && varName.equals(v.getName()));
    }

    /**
     * Adds a variable to the appropriate list in the artifact.
     */
    private void addVariableToArtifact(JsonReportV2.Artifact artifact, JsonReportV2.VariableInfo variable,
                                       VariableDirection direction) {
        switch (direction) {
            case INPUT:
                artifact.getVariables().getInput().add(variable);
                break;
            case OUTPUT:
                artifact.getVariables().getOutput().add(variable);
                break;
            case PRIVATE:
                List<JsonReportV2.VariableInfo> privateVars = getPrivateVariables(artifact);
                if (privateVars != null && !privateVars.isEmpty()) {
                    privateVars.add(variable);
                } else {
                    // Fallback to output if private list not accessible
                    artifact.getVariables().getOutput().add(variable);
                }
                break;
        }
    }

    /**
     * Gets private variables list, handling both getPrivate() and getPrivite() methods.
     */
    @SuppressWarnings("unchecked")
    private List<JsonReportV2.VariableInfo> getPrivateVariables(JsonReportV2.Artifact artifact) {
        if (artifact == null || artifact.getVariables() == null) {
            return Collections.emptyList();
        }

        // Try getPrivate() first
        try {
            List<JsonReportV2.VariableInfo> privateVars = (List<JsonReportV2.VariableInfo>)
                    artifact.getVariables().getClass().getMethod("getPrivate").invoke(artifact.getVariables());
            if (privateVars != null) {
                return privateVars;
            }
        } catch (Exception e1) {
            // Try getPrivite() as fallback
            try {
                List<JsonReportV2.VariableInfo> privateVars = (List<JsonReportV2.VariableInfo>)
                        artifact.getVariables().getClass().getMethod("getPrivite").invoke(artifact.getVariables());
                if (privateVars != null) {
                    return privateVars;
                }
            } catch (Exception e2) {
                System.err.println("AVISO: Não foi possível acessar a lista de variáveis privadas do artefato");
            }
        }
        return Collections.emptyList();
    }

    /**
     * Extracts potential variable names from an expression.
     * Uses regex to find identifier patterns while filtering out keywords.
     */
    private List<String> extractVariableNamesFromExpression(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> variables = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        // Remove string literals to avoid false positives
        String cleanedExpression = expression
                .replaceAll("\"[^\"]*\"", " ")
                .replaceAll("'[^']*'", " ");

        Matcher matcher = IDENTIFIER_PATTERN.matcher(cleanedExpression);
        while (matcher.find()) {
            String identifier = matcher.group(1);

            // Filter out stop words and ensure uniqueness
            if (!STOP_WORDS.contains(identifier.toLowerCase()) && seen.add(identifier)) {
                variables.add(identifier);
            }
        }

        return variables;
    }

    /**
     * Extracts the root identifier from a potentially complex variable path.
     * Example: "customer.address.street" -> "customer"
     */
    private String extractRootIdentifier(String variablePath) {
        if (variablePath == null || variablePath.trim().isEmpty()) {
            return null;
        }

        String cleaned = variablePath.trim();

        // Remove ${...} wrapper if present
        if (cleaned.startsWith("${") && cleaned.endsWith("}")) {
            cleaned = cleaned.substring(2, cleaned.length() - 1);
        }

        // Remove tw(...) wrapper if present
        if (cleaned.startsWith("tw(") && cleaned.endsWith(")")) {
            cleaned = cleaned.substring(3, cleaned.length() - 1);
        }

        // Extract root before dot or bracket
        int dotIndex = cleaned.indexOf('.');
        int bracketIndex = cleaned.indexOf('[');

        int splitIndex = -1;
        if (dotIndex > 0 && bracketIndex > 0) {
            splitIndex = Math.min(dotIndex, bracketIndex);
        } else if (dotIndex > 0) {
            splitIndex = dotIndex;
        } else if (bracketIndex > 0) {
            splitIndex = bracketIndex;
        }

        return splitIndex > 0 ? cleaned.substring(0, splitIndex) : cleaned;
    }

    // Métodos auxiliares para compatibilidade com ambas as versões

    private Object getArtifactFromCacheSafe(String id) {
        if (id == null) return null;

        try {
            // Tentar getArtefatoDoCache primeiro
            return processLoader.getArtefatoDoCache(id);
        } catch (Exception e) {
            try {
                // Tentar com reflection se o método não existir
                java.lang.reflect.Method getCacheMethod = processLoader.getClass()
                        .getMethod("getArtefatoDoCache", String.class);
                return getCacheMethod.invoke(processLoader, id);
            } catch (Exception e2) {
                System.err.println("⚠️ Could not get artifact from cache " + id + ": " + e2.getMessage());
                return null;
            }
        }
    }

    private String normalizeIdSafe(String originalId) {
        if (originalId == null) return null;

        try {
            // Tentar getCleanId primeiro
            return processLoader.getCleanId(originalId);
        } catch (Exception e1) {
            try {
                // Tentar normalizeId como fallback
                return processLoader.normalizeId(originalId);
            } catch (Exception e2) {
                // Último fallback: normalização manual
                String normalized = originalId.replaceAll("\\s+", "").trim();
                return normalized.contains("/") ?
                        normalized.substring(normalized.lastIndexOf('/') + 1) :
                        normalized;
            }
        }
    }

    /**
     * Método auxiliar que tenta diferentes formas de carregar artefato
     */
    private void loadArtifactSafe(String originalId) {
        try {
            // Tentar loadDependentArtifactIfNotExists primeiro
            processLoader.loadDependentArtifactIfNotExists(originalId);
        } catch (Exception e1) {
            try {
                // Tentar loadProcessInMemory como fallback
                processLoader.loadProcessInMemory(originalId);
            } catch (Exception e2) {
                try {
                    // Último fallback: tentar com reflection
                    java.lang.reflect.Method loadMethod = processLoader.getClass()
                            .getMethod("loadArtefatoSeNaoExistir", String.class);
                    loadMethod.invoke(processLoader, originalId);
                } catch (Exception e3) {
                    System.err.println("⚠️ Could not load artifact " + originalId + ": " + e3.getMessage());
                }
            }
        }
    }

    /**
     * Enriches variable information from a Teamworks artifact (TwClass).
     * Método da segunda versão para manter compatibilidade
     */
    private void enrichFromTeamworksArtifact(Teamworks teamworks, JsonReportV2.VariableInfo varInfo,
                                             String originalTypeId, Set<String> visitedTypes) {
        if (teamworks.getTwClass() == null) return;

        TwClass twClass = teamworks.getTwClass();

        // Handle primitive types
        if (twClass.getName() != null && PRIMITIVE_TYPES.contains(twClass.getName())) {
            varInfo.setTypeId(twClass.getName());
            return;
        }

        // Create business object definition for complex types
        JsonReportV2.Definition definition = createBusinessObjectDefinition(twClass, originalTypeId, visitedTypes);
        String canonicalId = reportGenerator.addBusinessObjectDefinition(definition);
        varInfo.setTypeId(canonicalId);
    }

    /**
     * Analisa e cataloga scripts embedded problemáticos
     */
    public ScriptAnalysisSummary analyzeEmbeddedScripts(JsonReportV2.Artifact artifact) {
        List<String> problematicScripts = new ArrayList<>();
        List<String> mixedLanguageVars = new ArrayList<>();

        if (artifact.getFlow() != null) {
            for (JsonReportV2.FlowStep step : artifact.getFlow()) {
                if (step.getScript() != null && !step.getScript().trim().isEmpty()) {
                    String script = step.getScript();

                    // Detecta lógica complexa embedded
                    if (isComplexEmbeddedLogic(script)) {
                        problematicScripts.add("Step: " + step.getName() + " - Complex logic detected");
                    }

                    // Detecta variáveis com nomes mistos português/inglês
                    List<String> mixedVars = detectMixedLanguageVariables(script);
                    mixedLanguageVars.addAll(mixedVars);
                }
            }
        }

        return new ScriptAnalysisSummary(problematicScripts, mixedLanguageVars);
    }

    private boolean isComplexEmbeddedLogic(String script) {
        // Detecta padrões de lógica complexa
        return script.contains("!=") && script.contains("&&") && script.contains("||") ||
                script.length() > 200 ||
                script.split("\n").length > 10;
    }

    private List<String> detectMixedLanguageVariables(String script) {
        List<String> mixedVars = new ArrayList<>();
        Pattern pattern = Pattern.compile("tw\\.local\\.(\\w+)");
        Matcher matcher = pattern.matcher(script);

        while (matcher.find()) {
            String varName = matcher.group(1);
            if (isMixedLanguage(varName)) {
                mixedVars.add(varName);
            }
        }

        return mixedVars;
    }

    private boolean isMixedLanguage(String varName) {
        // Detecta palavras em português misturadas com inglês
        String[] portugueseWords = {"orcamento", "comentarios", "decisao", "auxiliar"};
        String lowerVar = varName.toLowerCase();

        for (String word : portugueseWords) {
            if (lowerVar.contains(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Provides debugging information about variable enrichment.
     */
    public VariableEnrichmentSummary analyzeVariableEnrichment(JsonReportV2.Artifact artifact) {
        if (artifact == null || artifact.getVariables() == null) {
            return new VariableEnrichmentSummary(0, 0, 0, Collections.emptyList());
        }

        int inputCount = artifact.getVariables().getInput() != null ?
                artifact.getVariables().getInput().size() : 0;
        int outputCount = artifact.getVariables().getOutput() != null ?
                artifact.getVariables().getOutput().size() : 0;
        int privateCount = getPrivateVariables(artifact).size();

        List<String> enrichedTypes = new ArrayList<>();

        // Collect enriched type information
        if (artifact.getVariables().getInput() != null) {
            for (JsonReportV2.VariableInfo var : artifact.getVariables().getInput()) {
                if (var.getTypeId() != null && !PRIMITIVE_TYPES.contains(var.getTypeId())) {
                    enrichedTypes.add(var.getName() + ":" + var.getTypeId());
                }
            }
        }

        return new VariableEnrichmentSummary(inputCount, outputCount, privateCount, enrichedTypes);
    }

    // ========== ENUMS AND HELPER CLASSES ==========

    /**
     * Enum for variable direction/category.
     */
    private enum VariableDirection {
        INPUT, OUTPUT, PRIVATE
    }

    /**
     * Summary of script analysis results
     */
    public static class ScriptAnalysisSummary {
        private final List<String> problematicScripts;
        private final List<String> mixedLanguageVariables;

        public ScriptAnalysisSummary(List<String> problematicScripts, List<String> mixedLanguageVariables) {
            this.problematicScripts = new ArrayList<>(problematicScripts);
            this.mixedLanguageVariables = new ArrayList<>(mixedLanguageVariables);
        }

        public List<String> getProblematicScripts() {
            return Collections.unmodifiableList(problematicScripts);
        }

        public List<String> getMixedLanguageVariables() {
            return Collections.unmodifiableList(mixedLanguageVariables);
        }

        public boolean hasIssues() {
            return !problematicScripts.isEmpty() || !mixedLanguageVariables.isEmpty();
        }
    }

    /**
     * Summary of variable enrichment results for debugging.
     */
    public static class VariableEnrichmentSummary {
        private final int inputVariableCount;
        private final int outputVariableCount;
        private final int privateVariableCount;
        private final List<String> enrichedTypes;

        public VariableEnrichmentSummary(int inputCount, int outputCount, int privateCount,
                                         List<String> enrichedTypes) {
            this.inputVariableCount = inputCount;
            this.outputVariableCount = outputCount;
            this.privateVariableCount = privateCount;
            this.enrichedTypes = new ArrayList<>(enrichedTypes);
        }

        public int getInputVariableCount() { return inputVariableCount; }
        public int getOutputVariableCount() { return outputVariableCount; }
        public int getPrivateVariableCount() { return privateVariableCount; }
        public int getTotalVariableCount() {
            return inputVariableCount + outputVariableCount + privateVariableCount;
        }
        public List<String> getEnrichedTypes() {
            return Collections.unmodifiableList(enrichedTypes);
        }

        @Override
        public String toString() {
            return String.format("VariableEnrichment{input=%d, output=%d, private=%d, enriched=%d}",
                    inputVariableCount, outputVariableCount, privateVariableCount,
                    enrichedTypes.size());
        }
    }
}