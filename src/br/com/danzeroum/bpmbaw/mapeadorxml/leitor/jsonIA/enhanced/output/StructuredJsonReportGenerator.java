// StructuredJsonReportGenerator.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.FileWriter;
import java.util.*;

/**
 * Gerador de JSON estruturado seguindo as melhores práticas AI-friendly
 */
public class StructuredJsonReportGenerator {

    /**
     * Gera JSON estruturado a partir do enhanced analysis result
     */
    public void generateStructuredJson(EnhancedAnalysisResult result, AnalysisConfig config) throws Exception {
        System.out.println("🔄 Starting structured JSON generation...");

        StructuredProcessReport structuredReport = new StructuredProcessReport();
        System.out.println("✅ StructuredProcessReport created");

        setupSchemaAndProvenance(structuredReport, config, result);
        System.out.println("✅ Schema and provenance setup completed");

        setupDomainAndGlossary(structuredReport, result.getBusinessContext());
        System.out.println("✅ and glossary setup completed");

        // 3. Data types
        setupDataTypes(structuredReport, result.getStandardReport());

        // 4. Process graph normalizado
        setupProcessGraph(structuredReport, result.getStandardReport());

        // 5. Logic externalized
        setupLogicAndValidations(structuredReport, result.getStandardReport());

        // 6. UI e I18n
        setupUIAndInternationalization(structuredReport);

        // 7. Security e metadata
        setupSecurityAndMetrics(structuredReport, result);

        // Salvar JSON estruturado
        saveStructuredJson(structuredReport, config);
        System.out.println("🎉 Structured JSON saved successfully");

    }

    private void setupSchemaAndProvenance(StructuredProcessReport report, AnalysisConfig config, EnhancedAnalysisResult result) {
        // Schema info
        report.setSchema("https://processveritas.io/schema/enhanced-process/v1.json");
        report.setSchemaVersion("1.0.0");

        // Provenance
        ProcessProvenance provenance = new ProcessProvenance();
        provenance.setSourceTwx(config.getProjectName() + ".twx");
        provenance.setExportedAt(new Date().toString());
        provenance.setTool("IBM BAW Enhanced Analysis V4");
        provenance.setContentHash("sha256-" + generateContentHash(result));

        // Analysis stats
        AnalysisStats stats = new AnalysisStats();
        if (result.getStandardReport() != null && result.getStandardReport().getArtifacts() != null) {
            stats.setTotalArtifacts(result.getStandardReport().getArtifacts().size());

            int totalNodes = 0;
            int totalEdges = 0;
            int complexArtifacts = 0;

            for (JsonReportV2.Artifact artifact : result.getStandardReport().getArtifacts()) {
                if (artifact.getGraph() != null) {
                    if (artifact.getGraph().getNodes() != null) {
                        totalNodes += artifact.getGraph().getNodes().size();
                    }
                    if (artifact.getGraph().getEdges() != null) {
                        totalEdges += artifact.getGraph().getEdges().size();
                    }
                }

                // Considerar complexo se tem mais de 20 steps
                if (artifact.getFlow() != null && artifact.getFlow().size() > 20) {
                    complexArtifacts++;
                }
            }

            stats.setTotalNodes(totalNodes);
            stats.setTotalEdges(totalEdges);
            stats.setComplexArtifacts(complexArtifacts);
        }

        provenance.setAnalysisStats(stats);
        report.setProvenance(provenance);
    }

    private void setupDomainAndGlossary(StructuredProcessReport report, BusinessContext businessContext) {
        ProcessDomain domain = new ProcessDomain();

        if (businessContext != null) {
            domain.setName(businessContext.getDomain() != null ? businessContext.getDomain() : "Business Process");
            domain.setDescription("Extracted domain information from process analysis");

            // Glossary
            Map<String, String> glossary = new HashMap<>();
            if (businessContext.getMainEntities() != null) {
                for (String entity : businessContext.getMainEntities()) {
                    glossary.put(entity, "Domain entity: " + entity);
                }
            }
            domain.setGlossary(glossary);

            // Business rules
            List<StructuredBusinessRule> structuredRules = new ArrayList<>();
            if (businessContext.getBusinessRules() != null) {
                for (BusinessRule rule : businessContext.getBusinessRules()) {
                    StructuredBusinessRule structuredRule = new StructuredBusinessRule();
                    structuredRule.setId("br:" + rule.getName().toLowerCase().replace(" ", "_"));
                    structuredRule.setName(rule.getName());
                    structuredRule.setDescription(rule.getDescription() != null ? rule.getDescription() : rule.getCondition());
                    structuredRule.setCategory("BUSINESS_LOGIC");
                    structuredRules.add(structuredRule);
                }
            }
            domain.setBusinessRules(structuredRules);

            // Enums básicos
            List<ProcessEnum> enums = new ArrayList<>();
            ProcessEnum yesNoEnum = new ProcessEnum();
            yesNoEnum.setId("en:yes_no");
            yesNoEnum.setName("Yes/No Options");

            List<EnumValue> values = new ArrayList<>();
            EnumValue yesValue = new EnumValue();
            yesValue.setKey("YES");
            yesValue.setDisplay(createI18nMap("Sim", "Yes"));
            values.add(yesValue);

            EnumValue noValue = new EnumValue();
            noValue.setKey("NO");
            noValue.setDisplay(createI18nMap("Não", "No"));
            values.add(noValue);

            yesNoEnum.setValues(values);
            enums.add(yesNoEnum);
            domain.setEnums(enums);
        }

        report.setDomain(domain);
    }

    private void setupDataTypes(StructuredProcessReport report, JsonReportV2 standardReport) {
        System.out.println("🔍 DEBUG: setupDataTypes called");

        List<DataTypeDefinition> dataTypes = new ArrayList<>();

        try {
            // 🛡️ VERIFICAÇÃO DEFENSIVA
            if (standardReport == null) {
                System.out.println("⚠️ StandardReport is null - creating fallback data types");
                createFallbackDataTypes(dataTypes);
                report.setDataTypes(dataTypes);
                return;
            }

            System.out.println("🔍 StandardReport available");

            // Verificar business objects
            if (standardReport.getBusinessObjects() == null) {
                System.out.println("⚠️ BusinessObjects is null - extracting from variables");
                extractDataTypesFromVariables(dataTypes, standardReport);
            } else {
                System.out.println("🔍 BusinessObjects available, checking definitions...");

                // 🚨 LINHA 228 PROBLEMÁTICA - CORRIGIR AQUI
                try {
                    // Tentar acessar businessObjects.definitions com proteção
                    Object businessObjects = standardReport.getBusinessObjects();
                    System.out.println("🔍 BusinessObjects type: " + businessObjects.getClass().getSimpleName());

                    // Verificar se tem método getDefinitions()
                    java.lang.reflect.Method getDefinitionsMethod = null;
                    try {
                        getDefinitionsMethod = businessObjects.getClass().getMethod("getDefinitions");
                        System.out.println("✅ getDefinitions() method found");
                    } catch (NoSuchMethodException e) {
                        System.out.println("⚠️ getDefinitions() method not found - trying alternative approach");
                    }

                    if (getDefinitionsMethod != null) {
                        Object definitions = getDefinitionsMethod.invoke(businessObjects);

                        if (definitions != null && definitions instanceof java.util.List) {
                            @SuppressWarnings("unchecked")
                            java.util.List<Object> definitionsList = (java.util.List<Object>) definitions;
                            System.out.println("✅ Found " + definitionsList.size() + " business object definitions");

                            processBusinessObjectDefinitions(dataTypes, definitionsList);
                        } else {
                            System.out.println("⚠️ Definitions is null or not a List - using fallback");
                            extractDataTypesFromVariables(dataTypes, standardReport);
                        }
                    } else {
                        System.out.println("⚠️ No getDefinitions() method - using fallback");
                        extractDataTypesFromVariables(dataTypes, standardReport);
                    }

                } catch (Exception e) {
                    System.err.println("❌ Error accessing business objects: " + e.getMessage());
                    e.printStackTrace();
                    extractDataTypesFromVariables(dataTypes, standardReport);
                }
            }

            // Garantir que sempre temos pelo menos um data type
            if (dataTypes.isEmpty()) {
                System.out.println("⚠️ No data types extracted - creating fallback");
                createFallbackDataTypes(dataTypes);
            }

            System.out.println("✅ setupDataTypes completed with " + dataTypes.size() + " data types");

        } catch (Exception e) {
            System.err.println("❌ Critical error in setupDataTypes: " + e.getMessage());
            e.printStackTrace();

            // Fallback de emergência
            dataTypes.clear();
            createFallbackDataTypes(dataTypes);
        }

        report.setDataTypes(dataTypes);
    }

    private void createFallbackDataTypes(List<DataTypeDefinition> dataTypes) {
        System.out.println("🆘 Creating fallback data types...");

        // Data type básico
        DataTypeDefinition fallbackType = new DataTypeDefinition();
        fallbackType.setId("dt:generic");
        fallbackType.setName("GenericBusinessObject");
        fallbackType.setVersion("1.0.0");

        Map<String, Object> schema = new HashMap<>();
        schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
        schema.put("type", "object");
        schema.put("description", "Fallback data type when no definitions available");
        fallbackType.setSchema(schema);

        dataTypes.add(fallbackType);

        // Data type para strings
        DataTypeDefinition stringType = new DataTypeDefinition();
        stringType.setId("dt:string");
        stringType.setName("String");
        stringType.setVersion("1.0.0");

        Map<String, Object> stringSchema = new HashMap<>();
        stringSchema.put("type", "string");
        stringSchema.put("description", "Basic string type");
        stringType.setSchema(stringSchema);

        dataTypes.add(stringType);

        System.out.println("✅ Created " + dataTypes.size() + " fallback data types");
    }

    private void extractDataTypesFromVariables(List<DataTypeDefinition> dataTypes, JsonReportV2 standardReport) {
        System.out.println("🔄 Extracting data types from artifact variables...");

        Set<String> uniqueTypes = new HashSet<>();

        if (standardReport.getArtifacts() != null) {
            for (JsonReportV2.Artifact artifact : standardReport.getArtifacts()) {
                if (artifact.getVariables() != null) {
                    if (artifact.getVariables().getInput() != null) {
                        for (JsonReportV2.VariableInfo var : artifact.getVariables().getInput()) {
                            if (var.getTypeId() != null) {
                                uniqueTypes.add(var.getTypeId());
                            }
                        }
                    }
                    if (artifact.getVariables().getOutput() != null) {
                        for (JsonReportV2.VariableInfo var : artifact.getVariables().getOutput()) {
                            if (var.getTypeId() != null) {
                                uniqueTypes.add(var.getTypeId());
                            }
                        }
                    }
                }
            }
        }

        System.out.println("🔍 Found " + uniqueTypes.size() + " unique type IDs from variables");

        // Criar definições de tipo estruturadas
        for (String typeId : uniqueTypes) {
            if (typeId != null && !typeId.trim().isEmpty()) {
                DataTypeDefinition dataType = new DataTypeDefinition();

                String cleanTypeId = typeId.startsWith("canonical-") ?
                        typeId.replace("canonical-", "") : typeId;

                dataType.setId("dt:" + cleanTypeId + "@1");
                dataType.setName(capitalizeFirst(cleanTypeId));
                dataType.setVersion("1.0.0");

                // Schema básico
                Map<String, Object> schema = new HashMap<>();
                schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
                schema.put("type", "object");
                schema.put("description", "Generated schema for " + cleanTypeId);

                dataType.setSchema(schema);
                dataTypes.add(dataType);
            }
        }

        System.out.println("✅ Created " + dataTypes.size() + " data types from variables");
    }

    private void processBusinessObjectDefinitions(List<DataTypeDefinition> dataTypes, java.util.List<Object> definitions) {
        System.out.println("🔄 Processing " + definitions.size() + " business object definitions...");

        for (Object definitionObj : definitions) {
            try {
                // Usar reflection para acessar campos
                String typeId = getFieldValue(definitionObj, "typeId", String.class);
                String typeName = getFieldValue(definitionObj, "typeName", String.class);

                if (typeId != null && typeName != null) {
                    DataTypeDefinition dataType = new DataTypeDefinition();
                    dataType.setId("dt:" + typeId);
                    dataType.setName(typeName);
                    dataType.setVersion("1.0.0");

                    // Schema básico
                    Map<String, Object> schema = new HashMap<>();
                    schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
                    schema.put("type", "object");
                    schema.put("description", "Business object: " + typeName);

                    dataType.setSchema(schema);
                    dataTypes.add(dataType);

                    System.out.println("✅ Processed: " + typeName + " (" + typeId + ")");
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error processing definition: " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getFieldValue(Object obj, String fieldName, Class<T> expectedType) {
        try {
            java.lang.reflect.Method getter = obj.getClass().getMethod("get" +
                    fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
            Object value = getter.invoke(obj);

            if (value != null && expectedType.isAssignableFrom(value.getClass())) {
                return (T) value;
            }
        } catch (Exception e) {
            // Silently ignore reflection errors
        }
        return null;
    }


    private void setupProcessGraph(StructuredProcessReport report, JsonReportV2 standardReport) {
        ProcessGraph processGraph = new ProcessGraph();

        List<ProcessNode> nodes = new ArrayList<>();
        List<ProcessEdge> edges = new ArrayList<>();
        List<ProcessLane> lanes = new ArrayList<>();

        if (standardReport != null && standardReport.getArtifacts() != null) {
            for (JsonReportV2.Artifact artifact : standardReport.getArtifacts()) {

                // Converter flow steps para nodes
                if (artifact.getFlow() != null) {
                    for (JsonReportV2.FlowStep step : artifact.getFlow()) {
                        ProcessNode node = new ProcessNode();
                        node.setId(step.getStepId());
                        node.setName(step.getName());
                        node.setType(normalizeNodeType(step.getType()));

                        // Adicionar descrição se for script
                        if (step.getScript() != null && !step.getScript().trim().isEmpty()) {
                            node.setDescription("Script task: " + step.getName());
                            node.setLogicRef("lg:" + step.getStepId().replace(".", "_"));
                        }

                        nodes.add(node);
                    }
                }

                // Converter graph edges
                if (artifact.getGraph() != null && artifact.getGraph().getEdges() != null) {
                    for (JsonReportV2.Edge edge : artifact.getGraph().getEdges()) {
                        ProcessEdge processEdge = new ProcessEdge();
                        processEdge.setId(edge.getId());
                        processEdge.setSource(edge.getSource());
                        processEdge.setTarget(edge.getTarget());
                        processEdge.setLabel(edge.getLabel() != null ? edge.getLabel() : "Transition");
                        edges.add(processEdge);
                    }
                }
            }
        }

        processGraph.setNodes(nodes);
        processGraph.setEdges(edges);
        processGraph.setLanes(lanes);

        report.setProcessGraph(processGraph);
    }

    private void setupLogicAndValidations(StructuredProcessReport report, JsonReportV2 standardReport) {
        ProcessLogic logic = new ProcessLogic();
        List<LogicScript> scripts = new ArrayList<>();
        List<ValidationRule> validations = new ArrayList<>();

        if (standardReport != null && standardReport.getArtifacts() != null) {
            for (JsonReportV2.Artifact artifact : standardReport.getArtifacts()) {
                if (artifact.getFlow() != null) {
                    for (JsonReportV2.FlowStep step : artifact.getFlow()) {
                        if (step.getScript() != null && !step.getScript().trim().isEmpty()) {
                            LogicScript script = new LogicScript();
                            script.setId("lg:" + step.getStepId().replace(".", "_"));
                            script.setName(step.getName() + " Logic");
                            script.setLanguage("javascript");
                            script.setVersion("1.0.0");

                            // Extrair inputs/outputs básicos do script
                            script.setInputs(extractScriptInputs(step.getScript()));
                            script.setOutputs(extractScriptOutputs(step.getScript()));

                            // Fonte do script
                            ScriptSource source = new ScriptSource();
                            source.setRepository("legacy-inline");
                            source.setInlineCode(step.getScript());
                            script.setSource(source);

                            scripts.add(script);
                        }
                    }
                }
            }
        }

        // Adicionar validação básica
        ValidationRule basicValidation = new ValidationRule();
        basicValidation.setId("vl:process_completion");
        basicValidation.setName("Process Completion Validation");
        basicValidation.setRule("All required fields must be completed before process ends");
        basicValidation.setSeverity("error");
        basicValidation.setCategory("COMPLETION");
        validations.add(basicValidation);

        logic.setScripts(scripts);
        logic.setValidations(validations);
        report.setLogic(logic);
    }

    private void setupUIAndInternationalization(StructuredProcessReport report) {
        ProcessUI ui = new ProcessUI();

        // I18n básico
        I18nConfig i18n = new I18nConfig();
        i18n.setDefaultLocale("pt-BR");

        Map<String, Map<String, String>> bundles = new HashMap<>();
        Map<String, String> ptBrBundle = new HashMap<>();
        ptBrBundle.put("process.title", "Processo de Recondicionamentos");
        ptBrBundle.put("validation.required", "Campo obrigatório");
        bundles.put("pt-BR", ptBrBundle);

        Map<String, String> enBundle = new HashMap<>();
        enBundle.put("process.title", "Reconditioning Process");
        enBundle.put("validation.required", "Required field");
        bundles.put("en", enBundle);

        i18n.setBundles(bundles);
        ui.setI18n(i18n);

        report.setUi(ui);
    }

    private void setupSecurityAndMetrics(StructuredProcessReport report, EnhancedAnalysisResult result) {
        // Security
        SecurityConfig security = new SecurityConfig();
        security.setScriptSandbox(true);
        security.setAllowedAPIs(Arrays.asList("Math", "Date", "String", "Number"));
        security.setProhibitedAPIs(Arrays.asList("eval", "Function", "require", "process"));
        security.setPiiFields(Arrays.asList("cliente.nome", "cliente.email", "cliente.telefone"));
        security.setDataClassification("INTERNAL");
        security.setAuditLevel("FULL");
        report.setSecurity(security);

        // Metrics (do enhanced result)
        if (result.getAiReadinessScore() != null) {
            report.setAiReadinessScore(result.getAiReadinessScore());
        }

        if (result.getBusinessContext() != null) {
            report.setBusinessContext(result.getBusinessContext());
        }
    }

    private void saveStructuredJson(StructuredProcessReport report, AnalysisConfig config) throws Exception {
        String structuredPath = config.getOutputFilePath().replace(".json", "_structured_v4.json");

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try (FileWriter writer = new FileWriter(structuredPath)) {
            mapper.writeValue(writer, report);
        }

        System.out.println("✅ Structured JSON generated: " + structuredPath);
    }

    // ===============================================
    // HELPER METHODS
    // ===============================================

    private String generateContentHash(EnhancedAnalysisResult result) {
        // Simplified hash generation
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private Map<String, String> createI18nMap(String ptBr, String en) {
        Map<String, String> map = new HashMap<>();
        map.put("pt-BR", ptBr);
        map.put("en", en);
        return map;
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String normalizeNodeType(String originalType) {
        if (originalType == null) return "Task";

        switch (originalType.toLowerCase()) {
            case "subprocess": return "SubProcess";
            case "script": return "ScriptTask";
            case "servicecall": return "ServiceTask";
            case "usertask": return "UserTask";
            case "gateway": return "Gateway";
            case "startevent": return "StartEvent";
            case "endevent": return "EndEvent";
            default: return "Task";
        }
    }

    private List<String> extractScriptInputs(String script) {
        if (script == null || script.trim().isEmpty()) {
            return new ArrayList<>(); // 🛡️ Proteção contra null
        }
        List<String> inputs = new ArrayList<>();
        // Simple regex to find tw.local.variableName patterns
        try {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("tw\\.local\\.([a-zA-Z_][a-zA-Z0-9_]*)");
            java.util.regex.Matcher matcher = pattern.matcher(script);

            Set<String> uniqueInputs = new HashSet<>();
            while (matcher.find()) {
                String variable = matcher.group(1);
                if (!variable.equals("parameters") && !variable.equals("it")) { // Skip common output variables
                    uniqueInputs.add(variable);
                }
            }

            inputs.addAll(uniqueInputs);
        }catch (Exception e) {
                System.err.println("⚠️ Failed to extract inputs from script: " + e.getMessage());
                return inputs; // Retorna lista vazia em vez de falhar
            }
        return inputs;
    }

    private List<String> extractScriptOutputs(String script) {
        List<String> outputs = new ArrayList<>();
        // Look for assignment patterns like tw.local.variable = 
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("tw\\.local\\.([a-zA-Z_][a-zA-Z0-9_]*)\\s*=");
        java.util.regex.Matcher matcher = pattern.matcher(script);

        Set<String> uniqueOutputs = new HashSet<>();
        while (matcher.find()) {
            uniqueOutputs.add(matcher.group(1));
        }

        outputs.addAll(uniqueOutputs);
        return outputs;
    }
}

// ===============================================
// MODEL CLASSES FOR STRUCTURED JSON
// ===============================================

