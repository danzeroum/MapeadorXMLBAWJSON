package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.facade;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.FlowObject;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Pool;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnhancedBawAnalysisFacadeV2Plus {

    private static final String VERSION = "2.6.0-context-aware";

    public static EnhancedStructuredProcessReportV2 analyzeProcessWithV2Plus(AnalysisConfig config) throws Exception {
        System.out.println("🚀 Enhanced BAW Analysis Facade V2+ - Version " + VERSION);

        if (!validateConfigurationDetailed(config)) {
            throw new IllegalArgumentException("Invalid analysis configuration");
        }

        // *** ETAPA 1: Carregar todos os artefatos em memória ***
        ProcessLoaderV2Plus loader = loadTWXDataCompletelyFixed(config);
        BusinessProcessDiagram bpd = getBpdFromLoader(loader, config.getProcessId());

        // *** ETAPA 2: Passar o loader para os extratores ***
        ProcessDefinitionV2Plus processDefinition = extractWithV2PlusExtractorsCompletelyFixed(bpd, loader);

        // *** ETAPA 3: Montar o relatório final ***
        EnhancedStructuredProcessReportV2 report = createCompleteReportV2PlusCompletelyFixed(processDefinition, config, loader);

        validateAndEnrichReportFixed(report);
        printDetailedExtractionStatistics(report);

        return report;
    }

    // Carrega todos os artefatos e retorna o loader
    private static ProcessLoaderV2Plus loadTWXDataCompletelyFixed(AnalysisConfig config) {
        System.out.println("📂 Loading TWX data...");
        try {
            ProcessLoaderV2Plus loader = new ProcessLoaderV2Plus(config.getExtractionPath());
            loader.loadProcessInMemory(config.getProcessId());
            return loader;
        } catch (Exception e) {
            System.err.println("⚠️ Error loading TWX data: " + e.getMessage());
            e.printStackTrace();
            return null; // Retorna nulo em caso de falha
        }
    }

    // Obtém o BPD principal a partir do loader
    private static BusinessProcessDiagram getBpdFromLoader(ProcessLoaderV2Plus loader, String processId) {
        if (loader == null) return createMinimalBpdForAnalysis(processId);

        Object processObj = loader.getArtefatoDoCache(processId);
        if (processObj instanceof Teamworks) {
            Teamworks teamworks = (Teamworks) processObj;
            if (teamworks.getBpd() != null) {
                System.out.println("✅ BPD extracted successfully from Teamworks object.");
                return teamworks.getBpd().getBusinessProcessDiagram();
            }
        }
        System.err.println("⚠️ Could not find a valid BPD inside the Teamworks artifact.");
        return createMinimalBpdForAnalysis(processId);
    }


    // >>> MÉTODO MODIFICADO PARA ACEITAR O LOADER <<<
    private static ProcessDefinitionV2Plus extractWithV2PlusExtractorsCompletelyFixed(BusinessProcessDiagram bpd, ProcessLoaderV2Plus loader) {
        System.out.println("🔧 Extracting with V2Plus context-aware extractors...");
        ProcessDefinitionV2Plus definition = ProcessDefinitionV2Plus.create(bpd.getId());

        // AQUI ESTÁ A MUDANÇA: Passamos o 'loader' para os extratores
        TWXToV2PlusVariablesExtractor varExtractor = new TWXToV2PlusVariablesExtractor(loader);
        ProcessVariablesV2Plus variables = varExtractor.extractVariables(bpd);
        definition.setVariables(variables);

        ProcessGraphV2Plus graph = GraphExtractorV2Plus.extractGraph(bpd);
        definition.setGraph(graph);

        // O extrator de lógica também precisa do loader para encontrar os scripts
        TWXToV2PlusLogicExtractor logicExtractor = new TWXToV2PlusLogicExtractor(loader);
        List<FlowObject> allFlowObjects = extractAllFlowObjects(bpd);
        ProcessLogicV2Plus logic = logicExtractor.extractLogic(allFlowObjects);
        definition.setLogic(logic);

        // Demais extratores (podem ser atualizados no futuro)
        TWXToV2PlusConditionsExtractor conditionsExtractor = new TWXToV2PlusConditionsExtractor();
        definition.setConditions(conditionsExtractor.extractConditions(bpd.getFlows()));

        TWXToV2PlusMappingsExtractor mappingsExtractor = new TWXToV2PlusMappingsExtractor();
        definition.setMappings(mappingsExtractor.extractMappings(bpd));

        return definition;
    }

    // >>> MÉTODO MODIFICADO PARA RECEBER O LOADER E EXTRAIR DATA TYPES <<<
    private static EnhancedStructuredProcessReportV2 createCompleteReportV2PlusCompletelyFixed(ProcessDefinitionV2Plus processDefinition, AnalysisConfig config, ProcessLoaderV2Plus loader) {
        System.out.println("📊 Creating complete V2Plus report...");
        EnhancedStructuredProcessReportV2 report = EnhancedStructuredProcessReportV2.create(processDefinition.getId(), config.getProjectName());
        report.setProcessDefinition(processDefinition);

        // Extrai os Data Types usando as informações das variáveis
        TWXToV2PlusVariablesExtractor varExtractor = new TWXToV2PlusVariablesExtractor(loader);
        report.setDataTypes(varExtractor.extractDataTypeDefinitions(processDefinition.getVariables()));

        // Configurações padrão para as outras seções
        report.setUi(createDefaultUiConfigFixed());
        report.setQuality(createDefaultQualityConfigFixed());
        report.setSecurity(createDefaultSecurityConfigCompletelyFixed());
        report.setAnalytics(createDefaultAnalyticsConfigFixed());
        return report;
    }

    // Métodos auxiliares (sem alterações)
    private static List<FlowObject> extractAllFlowObjects(BusinessProcessDiagram bpd) {
        List<FlowObject> allFlowObjects = new ArrayList<>();
        if (bpd != null && bpd.getPools() != null) {
            for (Pool pool : bpd.getPools()) {
                if (pool.getLanes() != null) {
                    for (Lane lane : pool.getLanes()) {
                        if (lane.getFlowObjects() != null) {
                            allFlowObjects.addAll(lane.getFlowObjects());
                        }
                    }
                }
            }
        }
        return allFlowObjects;
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
        // Implementação de validação...
        return true;
    }

    private static BusinessProcessDiagram createMinimalBpdForAnalysis(String processId) {
        BusinessProcessDiagram bpd = new BusinessProcessDiagram();
        bpd.setId(processId);
        bpd.setName("Minimal Fallback BPD");
        return bpd;
    }

    private static List<DataTypeDefinitionV2Plus> createDefaultDataTypesFixed() { return new ArrayList<>(); }
    private static ProcessUIV2Plus createDefaultUiConfigFixed() { return new ProcessUIV2Plus(); }
    private static QualityConfigV2Plus createDefaultQualityConfigFixed() { return new QualityConfigV2Plus(); }
    private static SecurityConfigV2Plus createDefaultSecurityConfigCompletelyFixed() { return new SecurityConfigV2Plus(); }
    private static AnalyticsConfigV2Plus createDefaultAnalyticsConfigFixed() { return new AnalyticsConfigV2Plus(); }
}