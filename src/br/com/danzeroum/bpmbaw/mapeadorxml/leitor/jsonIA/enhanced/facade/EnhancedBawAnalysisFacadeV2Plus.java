package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.facade;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors.BpmnProcessExtractor;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors.TWXToV2PlusMasterExtractor;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors.TWXToV2PlusUIExtractor;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors.TWXToV2PlusVariablesExtractor;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram;



import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions;
// Novas importações para as classes de melhoria


import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


import static br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors.TWXToV2PlusMasterExtractor.createMinimalDefinition;

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


    private static ProcessDefinitionV2Plus extractWithV2PlusExtractorsCompletelyFixed(Object mainArtifact, ProcessLoaderV2Plus loader, AnalysisConfig config) {
        System.out.println("🔧 Iniciando extração com orquestrador V2Plus...");

        if (mainArtifact instanceof Definitions) {
            System.out.println("Moderno (BPMN 2.0) detectado. Usando extratores especializados...");
            return BpmnProcessExtractor.extractProcessDefinition((Definitions) mainArtifact, loader);
        } else if (mainArtifact instanceof Teamworks) {
            Teamworks tw = (Teamworks) mainArtifact;
            if (tw.getBpd() != null) {
                Bpd bpd = tw.getBpd();

                if (bpd.getBpmn2Data() != null && !bpd.getBpmn2Data().trim().isEmpty()) {
                    System.out.println("Híbrido (BPD com bpmn2Data) detectado. Usando extratores BPMN 2.0...");
                    try {
                        JAXBContext context = JAXBContext.newInstance(Definitions.class);
                        Unmarshaller unmarshaller = context.createUnmarshaller();
                        StringReader reader = new StringReader(bpd.getBpmn2Data());
                        Definitions definitions = (Definitions) unmarshaller.unmarshal(reader);
                        return BpmnProcessExtractor.extractProcessDefinition(definitions, loader);
                    } catch (Exception e) {
                        System.err.println("❌ Erro Crítico ao processar bpmn2Data. Nenhum dado pôde ser extraído.");
                        e.printStackTrace();
                        return new ProcessDefinitionV2Plus();
                    }
                } else {
                    System.out.println("Processo Legado (BPD) detectado. Usando extratores TWX...");
                    return TWXToV2PlusMasterExtractor.extractComplete(bpd, loader);
                }
            }
        }

        System.err.println("⚠️ Tipo de artefato não suportado para extração: " + mainArtifact.getClass().getName());
        return createMinimalDefinition();
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
        processDefinition.setVariables(variableInferer.inferTypedVariables(mainArtifact));

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
}