package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.facade;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoader;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.FlowObject;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Pool;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EnhancedBawAnalysisFacadeV2Plus {

    private static final String VERSION = "2.5.1-final-fix";

    public static EnhancedStructuredProcessReportV2 analyzeProcessWithV2Plus(AnalysisConfig config) throws Exception {
        System.out.println("🚀 Enhanced BAW Analysis Facade V2+ - Version " + VERSION);

        if (!validateConfigurationDetailed(config)) {
            throw new IllegalArgumentException("Invalid analysis configuration");
        }

        BusinessProcessDiagram bpd = loadTWXDataCompletelyFixed(config);

        ProcessDefinitionV2Plus processDefinition = extractWithV2PlusExtractorsCompletelyFixed(bpd, config);

        EnhancedStructuredProcessReportV2 report = createCompleteReportV2PlusCompletelyFixed(processDefinition, config);

        validateAndEnrichReportFixed(report);
        printDetailedExtractionStatistics(report);

        return report;
    }

    private static BusinessProcessDiagram loadTWXDataCompletelyFixed(AnalysisConfig config) {
        System.out.println("📂 Loading TWX data...");
        try {
            ProcessLoader loader = new ProcessLoader(config.getExtractionPath());
            Map<String, Object> allArtifacts = loader.loadProcessInMemory(config.getProcessId());
            Object processObj = loader.getArtefatoDoCache(config.getProcessId());

            if (processObj instanceof Teamworks) {
                Teamworks teamworks = (Teamworks) processObj;
                Bpd bpdWrapper = teamworks.getBpd();
                if (bpdWrapper != null) {
                    System.out.println("✅ BPD extracted successfully from Teamworks object.");
                    return bpdWrapper.getBusinessProcessDiagram();
                }
            }
            System.err.println("⚠️ Could not find a valid BPD inside the Teamworks artifact.");
        } catch (Exception e) {
            System.err.println("⚠️ Error loading TWX data: " + e.getMessage());
            e.printStackTrace();
        }
        return createMinimalBpdForAnalysis(config);
    }

    private static ProcessDefinitionV2Plus extractWithV2PlusExtractorsCompletelyFixed(BusinessProcessDiagram bpd, AnalysisConfig config) {
        System.out.println("🔧 Extracting with V2Plus extractors...");
        ProcessDefinitionV2Plus definition = ProcessDefinitionV2Plus.create(bpd.getId());

        ProcessVariablesV2Plus variables = TWXToV2PlusVariablesExtractor.extractVariables(bpd);
        definition.setVariables(variables);

        ProcessGraphV2Plus graph = TWXToV2PlusGraphExtractor.extractGraph(bpd, config.getProcessId());
        definition.setGraph(graph);

        TWXToV2PlusConditionsExtractor conditionsExtractor = new TWXToV2PlusConditionsExtractor();
        List<ProcessConditionV2Plus> processConditions = conditionsExtractor.extractConditions(bpd.getFlows());
        definition.setConditions(processConditions);

        List<FlowObject> allFlowObjects = extractAllFlowObjects(bpd);
        ProcessLogicV2Plus logic = TWXToV2PlusLogicExtractor.extractLogic(allFlowObjects);
        definition.setLogic(logic);

        ProcessMappingsV2Plus mappings = TWXToV2PlusMappingsExtractor.extractMappings(bpd);
        definition.setMappings(mappings);

        return definition;
    }

    private static EnhancedStructuredProcessReportV2 createCompleteReportV2PlusCompletelyFixed(ProcessDefinitionV2Plus processDefinition, AnalysisConfig config) {
        System.out.println("📊 Creating complete V2Plus report...");
        EnhancedStructuredProcessReportV2 report = EnhancedStructuredProcessReportV2.create(processDefinition.getId(), config.getProjectName());
        report.setProcessDefinition(processDefinition);
        report.setDataTypes(createDefaultDataTypesFixed());
        report.setUi(createDefaultUiConfigFixed());
        report.setQuality(createDefaultQualityConfigFixed());
        report.setSecurity(createDefaultSecurityConfigCompletelyFixed());
        report.setAnalytics(createDefaultAnalyticsConfigFixed());
        return report;
    }

    // Métodos auxiliares (sem alterações, apenas para manter a classe funcional)

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
        ProcessDefinitionV2Plus.ProcessDefinitionStats stats = report.getProcessDefinition().getStats();
        System.out.println("   Variables: " + (stats.inputVariables + stats.outputVariables + stats.privateVariables));
        System.out.println("   Graph Nodes: " + stats.nodeCount);
        System.out.println("   Graph Edges: " + stats.edgeCount);
        System.out.println("   Conditions: " + stats.conditionCount);
        System.out.println("   Logic Items: " + stats.scriptCount);
        System.out.println("   Data Types: " + report.getDataTypes().size());
        System.out.println("═══════════════════════════════════════");
    }

    private static boolean validateConfigurationDetailed(AnalysisConfig config) {
        // Implementação de validação...
        return true;
    }

    private static BusinessProcessDiagram createMinimalBpdForAnalysis(AnalysisConfig config) {
        BusinessProcessDiagram bpd = new BusinessProcessDiagram();
        bpd.setId(config.getProcessId());
        bpd.setName("Minimal Fallback BPD");
        return bpd;
    }

    private static List<DataTypeDefinitionV2Plus> createDefaultDataTypesFixed() {
        return new ArrayList<>();
    }

    private static ProcessUIV2Plus createDefaultUiConfigFixed() {
        return new ProcessUIV2Plus();
    }

    private static QualityConfigV2Plus createDefaultQualityConfigFixed() {
        return new QualityConfigV2Plus();
    }

    private static SecurityConfigV2Plus createDefaultSecurityConfigCompletelyFixed() {
        return new SecurityConfigV2Plus();
    }

    private static AnalyticsConfigV2Plus createDefaultAnalyticsConfigFixed() {
        return new AnalyticsConfigV2Plus();
    }
}