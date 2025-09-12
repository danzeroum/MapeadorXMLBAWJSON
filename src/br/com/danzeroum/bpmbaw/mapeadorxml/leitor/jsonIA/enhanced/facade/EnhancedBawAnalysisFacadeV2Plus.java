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

    private static final String VERSION = "2.9.0-final-extraction";

    public static EnhancedStructuredProcessReportV2 analyzeProcessWithV2Plus(AnalysisConfig config) throws Exception {
        System.out.println("🚀 Enhanced BAW Analysis Facade V2+ - Version " + VERSION);

        if (!validateConfigurationDetailed(config)) {
            throw new IllegalArgumentException("Invalid analysis configuration");
        }

        // ETAPA 1: Carregar todos os artefatos em memória
        ProcessLoaderV2Plus loader = loadTWXDataCompletelyFixed(config);
        Bpd bpd = getBpdFromLoader(loader, config.getProcessId());

        // ETAPA 2: Passar o loader e o BPD para os extratores
        ProcessDefinitionV2Plus processDefinition = extractWithV2PlusExtractorsCompletelyFixed(bpd, loader, config);

        // ETAPA 3: Montar o relatório final
        EnhancedStructuredProcessReportV2 report = createCompleteReportV2PlusCompletelyFixed(processDefinition, config, loader);

        validateAndEnrichReportFixed(report);
        printDetailedExtractionStatistics(report);

        return report;
    }

    private static ProcessLoaderV2Plus loadTWXDataCompletelyFixed(AnalysisConfig config) {
        System.out.println("📂 Loading TWX data...");
        try {
            ProcessLoaderV2Plus loader = new ProcessLoaderV2Plus(config.getExtractionPath());
            loader.loadProcessInMemory(config.getProcessId());
            return loader;
        } catch (Exception e) {
            System.err.println("⚠️ Error loading TWX data: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static Bpd getBpdFromLoader(ProcessLoaderV2Plus loader, String processId) {
        if (loader == null) return createMinimalBpdForAnalysis(processId);

        Object processObj = loader.getArtefatoDoCache(processId);
        if (processObj instanceof Teamworks) {
            Teamworks teamworks = (Teamworks) processObj;
            if (teamworks.getBpd() != null) {
                System.out.println("✅ BPD extracted successfully from Teamworks object.");
                return teamworks.getBpd();
            }
        }
        System.err.println("⚠️ Could not find a valid BPD inside the Teamworks artifact.");
        return createMinimalBpdForAnalysis(processId);
    }

    /**
     * CORREÇÃO: Orquestra a passagem dos objetos corretos (Bpd ou BusinessProcessDiagram)
     * para cada extrator.
     */
    private static ProcessDefinitionV2Plus extractWithV2PlusExtractorsCompletelyFixed(Bpd bpd, ProcessLoaderV2Plus loader, AnalysisConfig config) {
        System.out.println("🔧 Extracting with V2Plus context-aware extractors...");

        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();
        if (diagram == null) {
            System.err.println("⚠️ BPD object does not contain a BusinessProcessDiagram. Aborting extraction.");
            return new ProcessDefinitionV2Plus();
        }

        ProcessDefinitionV2Plus definition = ProcessDefinitionV2Plus.create(diagram.getId());

        // O extrator de variáveis precisa do 'Bpd' completo para acessar os parâmetros.
        TWXToV2PlusVariablesExtractor varExtractor = new TWXToV2PlusVariablesExtractor(loader);
        ProcessVariablesV2Plus variables = varExtractor.extractVariables(bpd);
        definition.setVariables(variables);

        // Os demais extratores trabalham com o 'BusinessProcessDiagram'.
        ProcessGraphV2Plus graph = GraphExtractorV2Plus.extractGraph(diagram);
        definition.setGraph(graph);

        TWXToV2PlusLogicExtractor logicExtractor = new TWXToV2PlusLogicExtractor(loader);
        List<FlowObject> allFlowObjects = extractAllFlowObjects(diagram);
        ProcessLogicV2Plus logic = logicExtractor.extractLogic(allFlowObjects);
        definition.setLogic(logic);

        TWXToV2PlusConditionsExtractor conditionsExtractor = new TWXToV2PlusConditionsExtractor();
        definition.setConditions(conditionsExtractor.extractConditions(diagram.getFlows()));

        definition.setMappings(TWXToV2PlusMappingsExtractor.extractMappings(diagram));

        return definition;
    }

    private static EnhancedStructuredProcessReportV2 createCompleteReportV2PlusCompletelyFixed(ProcessDefinitionV2Plus processDefinition, AnalysisConfig config, ProcessLoaderV2Plus loader) {
        System.out.println("📊 Creating complete V2Plus report...");
        EnhancedStructuredProcessReportV2 report = EnhancedStructuredProcessReportV2.create(processDefinition.getId(), config.getProjectName());
        report.setProcessDefinition(processDefinition);

        TWXToV2PlusVariablesExtractor varExtractor = new TWXToV2PlusVariablesExtractor(loader);
        report.setDataTypes(varExtractor.extractDataTypeDefinitions(processDefinition.getVariables()));

        report.setUi(createDefaultUiConfigFixed());
        report.setQuality(createDefaultQualityConfigFixed());
        report.setSecurity(createDefaultSecurityConfigCompletelyFixed());
        report.setAnalytics(createDefaultAnalyticsConfigFixed());
        return report;
    }

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
        return true;
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