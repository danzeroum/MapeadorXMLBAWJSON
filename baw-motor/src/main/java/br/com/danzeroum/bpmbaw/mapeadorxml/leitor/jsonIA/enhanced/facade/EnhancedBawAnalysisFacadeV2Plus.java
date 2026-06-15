package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.facade;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.inferers.VariableInferer;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.normalizers.GraphNormalizer;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions;
import java.util.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.*;


import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions;
// Novas importações para as classes de melhoria


import jakarta.xml.bind.JAXBContext;
import java.util.*;

public class EnhancedBawAnalysisFacadeV2Plus {

    private static final String VERSION = "2.9.1-iter1-integrated";

    public static EnhancedStructuredProcessReportV2 analyzeProcessWithV2Plus(AnalysisConfig config) throws Exception {
        System.out.println("🚀 Enhanced BAW Analysis Facade V2+ - Version " + VERSION);
        System.out.println("========================================================"); // Log Adicionado
        System.out.println("[LOG-FACADE] Iniciando análise para o processo ID: " + config.getProcessId()); // Log Adicionado

        if (!validateConfigurationDetailed(config)) {
            throw new IllegalArgumentException("Invalid analysis configuration");
        }

        // ETAPA 1: Carregar todos os artefatos do projeto e seus toolkits em memória.
        // Isso garante que todas as dependências estejam disponíveis para os extratores.
        ProcessLoaderV2Plus loader = loadTWXDataCompletelyFixed(config);
        Object mainArtifact = loader.getArtefatoDoCache(config.getProcessId());

        if (mainArtifact == null) {
            System.err.println("[LOG-FACADE-ERRO] Artefato principal com ID '" + config.getProcessId() + "' não foi encontrado no cache. A análise não pode continuar.");
            throw new Exception("Artifact principal com ID '" + config.getProcessId() + "' não foi encontrado no cache.");
        }
        System.out.println("[LOG-FACADE] Artefato principal carregado com sucesso. Tipo: " + mainArtifact.getClass().getName());

        // ETAPA 2: Extrair a definição base do processo (grafo, lógica, etc.)
        // usando os extratores especializados para o tipo de artefato (legado ou moderno).
        ProcessDefinitionV2Plus processDefinition = extractWithV2PlusExtractorsCompletelyFixed(mainArtifact, loader, config);
        System.out.println("[LOG-FACADE] ProcessDefinition extraída. Resumo: " + processDefinition.getStats());

        // ETAPA 3: Extrair a definição da Interface de Usuário (Coaches).
        System.out.println("🎨 Extracting UI components...");
        TWXToV2PlusUIExtractor uiExtractor = new TWXToV2PlusUIExtractor(loader);
        ProcessUIV2Plus ui = uiExtractor.extractUI(mainArtifact);
        System.out.println("[LOG-FACADE] Extração de UI concluída. Componentes: " + (ui.getComponents() != null ? ui.getComponents().size() : 0));

        // ETAPA 4: Montar o relatório final. Este método agora orquestra as novas
        // classes de enriquecimento (VariableInferer, DataTypesBuilder, GraphNormalizer)
        // para refinar os dados extraídos e alinhá-los ao formato final.
        EnhancedStructuredProcessReportV2 report = createCompleteReportV2PlusCompletelyFixed(processDefinition, config, loader, mainArtifact);

        // Adiciona a UI extraída ao relatório.
        report.setUi(ui);

        // ETAPA 5: Validações finais e impressão de estatísticas.
        validateAndEnrichReportFixed(report);
        printDetailedExtractionStatistics(report);
        System.out.println("========================================================"); // Log Adicionado

        return report;
    }

    private static ProcessLoaderV2Plus loadTWXDataCompletelyFixed(AnalysisConfig config) {
        System.out.println("📂 Loading TWX data...");
        try {
            ProcessLoaderV2Plus loader = new ProcessLoaderV2Plus(config.getExtractionPath());



            loader.carregarTodosOsArtefatosDoProjeto();




            loader.loadProcessInMemory(config.getProcessId());
            return loader;
        } catch (Exception e) {
            System.err.println("⚠️ Error loading TWX data: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static Bpd getBpdFromArtifact(Object mainArtifact, String processId) {
        if (mainArtifact instanceof Teamworks && ((Teamworks) mainArtifact).getBpd() != null) {
            return ((Teamworks) mainArtifact).getBpd();







        } else if (mainArtifact instanceof Definitions) {



            return createBpdFromDefinitions((Definitions) mainArtifact);
        }
        // Fallback: Se não for um tipo conhecido, cria um BPD mínimo para evitar null pointer.
        return createMinimalBpdForAnalysis(processId);
    }


    // Em: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/jsonIA/enhanced/facade/EnhancedBawAnalysisFacadeV2Plus.java

    private static ProcessDefinitionV2Plus extractWithV2PlusExtractorsCompletelyFixed(
            Object mainArtifact, ProcessLoaderV2Plus loader, AnalysisConfig config) {

        // Cria uma definição de processo baseada no ID do artefato principal
        ProcessDefinitionV2Plus definition = ProcessDefinitionV2Plus.create(config.getProcessId());

        // ===== CORREÇÃO APLICADA AQUI =====
        // O bloco inteiro de extração de variáveis foi substituído por esta lógica de despacho,
        // que seleciona o extrator correto com base no tipo do artefato.

        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus(); // Inicializa um contêiner vazio

        if (mainArtifact instanceof Teamworks && ((Teamworks) mainArtifact).getBpd() != null) {
            // Cenário 1: É um BPD legado dentro de um artefato Teamworks
            System.out.println("   -> Usando TWXToV2PlusVariablesExtractor para BPD legado.");
            TWXToV2PlusVariablesExtractor varExtractor = new TWXToV2PlusVariablesExtractor(loader);
            variables = varExtractor.extractVariables(((Teamworks) mainArtifact).getBpd());

        } else if (mainArtifact instanceof Definitions) {
            // Cenário 2: É um processo BPMN 2.0 moderno
            System.out.println("   -> Usando BpmnToV2PlusVariablesExtractor para processo BPMN.");
            variables = BpmnToV2PlusVariablesExtractor.extractVariables((Definitions) mainArtifact);

        } else {
            // Cenário de fallback: Se o tipo não for reconhecido, loga um aviso
            System.err.println("   -> AVISO: Não foi possível extrair variáveis de um artefato do tipo: " + mainArtifact.getClass().getName());
        }

        // Define as variáveis extraídas (já categorizadas) na definição do processo.
        definition.setVariables(variables);

        // O restante da lógica para extrair grafo, condições e mapeamentos continua...
        // =======================================================================

        // Extrair grafo
        ProcessGraphV2Plus graph = extractGraphFromArtifact(mainArtifact, loader);
        definition.setGraph(graph);

        // Normalizar grafo
        GraphNormalizer graphNormalizer = new GraphNormalizer();
        graphNormalizer.promoteCanonicalTypeToType(definition);
        graphNormalizer.recomputeEntryAndEndPoints(definition);

        // Criar conditions usando ProcessConditionV2Plus
        if (definition.getGraph() != null) {
            List<ProcessConditionV2Plus> conditions = extractConditionsFromGraph(definition.getGraph());
            attachConditionRefsToEdges(definition.getGraph(), conditions);
            definition.setConditions(conditions);
        }

        // Criar mapeamentos básicos
        ProcessMappingsV2Plus mappings = createBasicMappings();
        definition.setMappings(mappings);

        // Normalizar tipos de variáveis
        normalizeVariableTypes(definition.getVariables());

        return definition;
    }

    private static ProcessGraphV2Plus extractGraphFromArtifact(Object mainArtifact, ProcessLoaderV2Plus loader) {
        ProcessGraphV2Plus graph = new ProcessGraphV2Plus();

        // Extrair nodes e edges do artefato
        if (mainArtifact instanceof Teamworks) {
            Teamworks tw = (Teamworks) mainArtifact;
            if (tw.getBpd() != null && tw.getBpd().getBusinessProcessDiagram() != null) {
                // Extrair do BPD
                graph = convertBpdToGraph(tw.getBpd());
            }
        } else if (mainArtifact instanceof Definitions) {
            // Extrair de BPMN
            Definitions def = (Definitions) mainArtifact;
            graph = convertBpmnToGraph(def);
        }

        // Se ainda estiver vazio, criar estrutura mínima
        if (graph.getNodes() == null || graph.getNodes().isEmpty()) {
            graph = createMinimalGraph();
        }

        return graph;
    }
    private static void normalizeVariableTypes(ProcessVariablesV2Plus vars) {
        if (vars == null) return;

        VariableInferer inferer = new VariableInferer();

        // Normalizar input variables
        if (vars.getInputVariables() != null) {
            for (ProcessVariableV2Plus v : vars.getInputVariables()) {
                normalizeVariable(v, inferer);
            }
        }

        // Normalizar output variables
        if (vars.getOutputVariables() != null) {
            for (ProcessVariableV2Plus v : vars.getOutputVariables()) {
                normalizeVariable(v, inferer);
            }
        }

        // Normalizar private variables
        if (vars.getPrivateVariables() != null) {
            for (ProcessVariableV2Plus v : vars.getPrivateVariables()) {
                normalizeVariable(v, inferer);
            }
        }
    }

    // Método auxiliar para normalizar uma variável individual
    private static void normalizeVariable(ProcessVariableV2Plus var, VariableInferer inferer) {
        // Mapear typeId para typeRef
        if (var.getTypeRef() == null && var.getType() != null) {
            var.setTypeRef(inferer.mapTypeIdToTypeRef(var.getType()));
        }

        // Definir cardinality baseado em isList
        if (var.getCardinality() == null) {
            var.setCardinality(var.isList() ? "many" : "one");
        }

        // Se não tiver descrição, adicionar uma padrão
        if (var.getDescription() == null || var.getDescription().isEmpty()) {
            var.setDescription("Variable " + var.getName());
        }
    }

    // Método para extrair conditions do grafo
    // Método extractConditionsFromGraph corrigido (linha ~172)
    private static List<ProcessConditionV2Plus> extractConditionsFromGraph(ProcessGraphV2Plus graph) {
        Map<String, ProcessConditionV2Plus> byKey = new LinkedHashMap<String, ProcessConditionV2Plus>();

        if (graph == null || graph.getEdges() == null) {
            return new ArrayList<ProcessConditionV2Plus>();
        }

        for (ProcessEdgeV2Plus edge : graph.getEdges()) {
            String label = edge.getLabel();
            if (label == null || label.trim().isEmpty() || "Untitled".equals(label)) {
                continue;
            }

            String key = edge.getSource() + "->" + label.trim();

            if (!byKey.containsKey(key)) {
                ProcessConditionV2Plus condition = new ProcessConditionV2Plus();
                condition.setId("cond_" + Math.abs(key.hashCode()));
                condition.setName(label.trim());
                condition.setExpression("label == \"" + label.trim().replace("\"","\\\"") + "\"");

                // Corrigir: usar enum ao invés de string
                condition.setLanguage(ProcessConditionV2Plus.ExpressionLanguage.CEL);
                // OU se não existir enum, comentar a linha:
                // condition.setLanguage("cel");

                byKey.put(key, condition);
            }
        }

        return new ArrayList<ProcessConditionV2Plus>(byKey.values());
    }
    // Método para anexar conditionRef às edges
    private static void attachConditionRefsToEdges(ProcessGraphV2Plus graph, List<ProcessConditionV2Plus> conditions) {
        if (graph == null || graph.getEdges() == null) return;

        for (ProcessEdgeV2Plus edge : graph.getEdges()) {
            if (edge.getLabel() == null || "Untitled".equals(edge.getLabel())) continue;

            String key = edge.getSource() + "->" + edge.getLabel().trim();
            String id = "cond_" + Math.abs(key.hashCode());

            // Adicionar conditionRef à edge
            if (edge.getProperties() == null) {
                edge.setProperties(new HashMap<String, Object>());
            }
            edge.getProperties().put("conditionRef", id);
        }
    }




    private static ProcessMappingsV2Plus createBasicMappings() {
        ProcessMappingsV2Plus mappings = new ProcessMappingsV2Plus();
        mappings.setExprLang("cel");

        // Inicializar listas vazias
        mappings.setInputs(new ArrayList<InputMappingV2Plus>());
        mappings.setOutputs(new ArrayList<OutputMappingV2Plus>());
        mappings.setTransformations(new ArrayList<TransformationRuleV2Plus>());

        return mappings;
    }

// NOVO MÉTODO AUXILIAR (ADICIONAR NO FINAL DA CLASSE):
    /**
     * Normaliza os tipos de todas as variáveis (input, output, private)
     * na definição de processo, convertendo typeId para typeRef.
     *
     * @param vars O objeto que contém as definições de variáveis.
     */
    private static void normalizeProcessVariables(ProcessVariablesV2Plus vars) {
        VariableInferer inferer = new VariableInferer();

        // Normalizar inputs
        if (vars.getInputs() != null) {
            for (ProcessVariableV2Plus v : vars.getInputs()) {
                if (v.getTypeRef() == null && v.getTypeId() != null) {
                    v.setTypeRef(inferer.mapTypeIdToTypeRef(v.getTypeId()));
                }
                if (v.getCardinality() == null) {
                    v.setCardinality(inferer.determineCardinality(v.isList()));
                }
            }
        }

        // Normalizar outputs
        if (vars.getOutputs() != null) {
            for (ProcessVariableV2Plus v : vars.getOutputs()) {
                if (v.getTypeRef() == null && v.getTypeId() != null) {
                    v.setTypeRef(inferer.mapTypeIdToTypeRef(v.getTypeId()));
                }
                if (v.getCardinality() == null) {
                    v.setCardinality(inferer.determineCardinality(v.isList()));
                }
            }
        }

        // Normalizar privates
        if (vars.getPrivates() != null) {
            for (ProcessVariableV2Plus v : vars.getPrivates()) {
                if (v.getTypeRef() == null && v.getTypeId() != null) {
                    v.setTypeRef(inferer.mapTypeIdToTypeRef(v.getTypeId()));
                }
                if (v.getCardinality() == null) {
                    v.setCardinality(inferer.determineCardinality(v.isList()));
                }
            }
        }
    }
    private static Bpd createBpdFromDefinitions(Definitions definitions) {
        if (definitions == null || definitions.getProcess() == null) {
            return null;
        }
        Bpd bpd = new Bpd();
        bpd.setId(definitions.getProcess().getId());
        bpd.setName(definitions.getProcess().getName());


        try {
            JAXBContext context = JAXBContext.newInstance(Definitions.class);
            java.io.StringWriter sw = new java.io.StringWriter();
            context.createMarshaller().marshal(definitions, sw);
            bpd.setBpmn2Data(sw.toString());
        } catch(Exception e) {
            bpd.setBpmn2Data("");
        }

        BusinessProcessDiagram diagram = new BusinessProcessDiagram();
        diagram.setId(definitions.getProcess().getId());
        diagram.setName(definitions.getProcess().getName());
        bpd.setBusinessProcessDiagram(diagram);

        return bpd;
    }


    private static EnhancedStructuredProcessReportV2 createCompleteReportV2PlusCompletelyFixed(
            ProcessDefinitionV2Plus processDefinition, AnalysisConfig config, ProcessLoaderV2Plus loader, Object mainArtifact) {

        EnhancedStructuredProcessReportV2 report = EnhancedStructuredProcessReportV2.create(processDefinition.getId(), config.getProjectName());

        // Etapas 1 e 2 (Variáveis, Grafo, Condições, Mapeamentos - já estáveis)
        // ...
        VariableInferer variableInferer = new VariableInferer();
        //processDefinition.setVariables(variableInferer.einferTypedVariable(mainArtifact));

       // DataTypesBuilder dataTypesBuilder = new DataTypesBuilder();
       // report.setDataTypes(dataTypesBuilder.synthesizeDomainTypes(mainArtifact));
        // NOVA ETAPA: Usamos o extrator para construir as definições completas de DataType
        System.out.println("🧬 Synthesizing Data Types from variables...");
        TWXToV2PlusVariablesExtractor varExtractorForTypes = new TWXToV2PlusVariablesExtractor(loader);
        List<DataTypeDefinitionV2Plus> dataTypes = varExtractorForTypes.extractDataTypeDefinitions(processDefinition.getVariables());
        report.setDataTypes(dataTypes);
        System.out.println("[LOG-FACADE] Tipos de dados (DataTypes) sintetizados: " + dataTypes.size());
        // --- FIM DA ALTERAÇÃO ---

        GraphNormalizer graphNormalizer = new GraphNormalizer();
        graphNormalizer.fixEndPoints(processDefinition);
        graphNormalizer.normalizeNodeTypes(processDefinition);
        ConditionAdapter conditionAdapter = new ConditionAdapter();
        conditionAdapter.attachConditionRefs(processDefinition.getGraph(), processDefinition.getConditions());
        MappingTransformer mappingTransformer = new MappingTransformer();
        processDefinition.setMappings(mappingTransformer.toCanonicalMappings(processDefinition.getMappings()));

        // --- INÍCIO DAS MELHORIAS DA ITERAÇÃO 3 ---

        // Etapa 5: Enriquecer metadados e proveniência
        System.out.println("   -> Enriching metadata and provenance...");
        ProvenanceEnricher provenanceEnricher = new ProvenanceEnricher();
        provenanceEnricher.enrichMetadata(report, config.getProcessId());

        // Etapa 6: Adicionar blocos de governança usando templates
        System.out.println("   -> Applying governance templates (BusinessContext, Quality, Security, Analytics)...");
        GovernanceTemplates templates = new GovernanceTemplates();
        // report.setBusinessContext(templates.defaultBusinessContext("recondicionamento")); // Descomentar quando a classe BusinessContext existir
        report.setQuality(templates.defaultQuality());
        report.setSecurity(templates.defaultSecurity());
        report.setAnalytics(templates.defaultAnalytics());

        // Etapa 7: Gerar a estrutura final da UI (a UI extraída pelo uiExtractor será mesclada aqui no futuro)
        System.out.println("   -> Building final UI structure (hints and i18n)...");
        UiHintsI18nGenerator uiGenerator = new UiHintsI18nGenerator();
        report.setUi(uiGenerator.buildHintsAndI18n(processDefinition));

        // --- FIM DAS MELHORIAS DA ITERAÇÃO 3 ---

        report.setProcessDefinition(processDefinition);

        System.out.println("   ✅ Report structure created successfully.");
        return report;
    }

    private static void validateAndEnrichReportFixed(EnhancedStructuredProcessReportV2 report) {
        if (report.getMetadata() == null) {
            report.setMetadata(new EnhancedStructuredProcessReportV2.ReportMetadata());
        }
        report.getMetadata().tool = "Enhanced BAW Analysis V2+";
    }

    private static void printDetailedExtractionStatistics(EnhancedStructuredProcessReportV2 report) {
        System.out.println("\n📊 EXTRACTION STATISTICS:");
        if (report.getProcessDefinition() != null) {
            ProcessDefinitionV2Plus.ProcessDefinitionStats stats = report.getProcessDefinition().getStats();
            System.out.println("   Variables: " + (stats.inputVariables + stats.outputVariables + stats.privateVariables));
            System.out.println("   Graph Nodes: " + stats.nodeCount);
            System.out.println("   Graph Edges: " + stats.edgeCount);
            System.out.println("   Conditions: " + stats.conditionCount);
            System.out.println("   Logic Items: " + stats.scriptCount);
        }
        System.out.println("   Data Types: " + (report.getDataTypes() != null ? report.getDataTypes().size() : 0));
        System.out.println("═══════════════════════════════════════");
    }

    private static boolean validateConfigurationDetailed(AnalysisConfig config) {
        return true; // Simplificado para o exemplo
    }

    private static Bpd createMinimalBpdForAnalysis(String processId) {
        Bpd bpd = new Bpd();
        bpd.setId(processId);
        bpd.setName("Minimal Fallback BPD");
        bpd.setBusinessProcessDiagram(new BusinessProcessDiagram());
        return bpd;
    }

    private static ProcessUIV2Plus createDefaultUiConfigFixed() { return new ProcessUIV2Plus(); }
    private static QualityConfigV2Plus createDefaultQualityConfigFixed() { return new QualityConfigV2Plus(); }
    private static SecurityConfigV2Plus createDefaultSecurityConfigCompletelyFixed() { return new SecurityConfigV2Plus(); }
    private static AnalyticsConfigV2Plus createDefaultAnalyticsConfigFixed() { return new AnalyticsConfigV2Plus(); }
    // Método para converter BPD para Graph
    private static ProcessGraphV2Plus convertBpdToGraph(Bpd bpd) {
        ProcessGraphV2Plus graph = new ProcessGraphV2Plus();

        List<ProcessNodeV2Plus> nodes = new ArrayList<ProcessNodeV2Plus>();
        List<ProcessEdgeV2Plus> edges = new ArrayList<ProcessEdgeV2Plus>();
        List<ProcessLaneV2Plus> lanes = new ArrayList<ProcessLaneV2Plus>();

        // Converter lanes
        if (bpd.getBusinessProcessDiagram().getPools() != null) {
            for (Object laneObj : bpd.getBusinessProcessDiagram().getPools()) {
                ProcessLaneV2Plus lane = new ProcessLaneV2Plus();
                lane.setId("lane_" + laneObj.hashCode());
                lane.setName(laneObj.toString());
                lanes.add(lane);
            }
        }

        // Adicionar nodes e edges básicos
        ProcessNodeV2Plus startNode = new ProcessNodeV2Plus();
        startNode.setId("n_start_default");
        startNode.setType(ProcessNodeV2Plus.NodeType.START_EVENT);
        startNode.setName("Start");
        nodes.add(startNode);

        ProcessNodeV2Plus endNode = new ProcessNodeV2Plus();
        endNode.setId("n_end_default");
        endNode.setType(ProcessNodeV2Plus.NodeType.END_EVENT);
        endNode.setName("End");
        nodes.add(endNode);

        graph.setNodes(nodes);
        graph.setEdges(edges);
        graph.setLanes(lanes);
        graph.setEntryPoints(Arrays.asList("n_start_default"));
        graph.setEndPoints(Arrays.asList("n_end_default"));

        return graph;
    }

    // Método para converter BPMN para Graph
    private static ProcessGraphV2Plus convertBpmnToGraph(Definitions definitions) {
        ProcessGraphV2Plus graph = new ProcessGraphV2Plus();

        // Implementação similar ao convertBpdToGraph
        // mas extraindo de Definitions BPMN

        return convertBpdToGraph(null); // Simplificado
    }

    // Método para criar grafo mínimo
    private static ProcessGraphV2Plus createMinimalGraph() {
        ProcessGraphV2Plus graph = new ProcessGraphV2Plus();

        List<ProcessNodeV2Plus> nodes = new ArrayList<ProcessNodeV2Plus>();

        ProcessNodeV2Plus startNode = new ProcessNodeV2Plus();
        startNode.setId("n_start");
        startNode.setType(ProcessNodeV2Plus.NodeType.START_EVENT);
        startNode.setName("Start");
        nodes.add(startNode);

        ProcessNodeV2Plus endNode = new ProcessNodeV2Plus();
        endNode.setId("n_end");
        endNode.setType(ProcessNodeV2Plus.NodeType.END_EVENT);
        endNode.setName("End");
        nodes.add(endNode);

        List<ProcessEdgeV2Plus> edges = new ArrayList<ProcessEdgeV2Plus>();
        ProcessEdgeV2Plus edge = new ProcessEdgeV2Plus();
        edge.setId("e_start_to_end");
        edge.setSource("n_start");
        edge.setTarget("n_end");
        edges.add(edge);

        graph.setNodes(nodes);
        graph.setEdges(edges);
        graph.setEntryPoints(Arrays.asList("n_start"));
        graph.setEndPoints(Arrays.asList("n_end"));

        return graph;
    }


}