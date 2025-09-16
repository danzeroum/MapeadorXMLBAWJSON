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
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors.TWXToV2PlusMasterExtractor.createMinimalDefinition;

public class EnhancedBawAnalysisFacadeV2Plus {

    private static final String VERSION = "2.9.0-final-extraction";

    public static EnhancedStructuredProcessReportV2 analyzeProcessWithV2Plus(AnalysisConfig config) throws Exception {
        System.out.println("🚀 Enhanced BAW Analysis Facade V2+ - Version " + VERSION);

        if (!validateConfigurationDetailed(config)) {
            throw new IllegalArgumentException("Invalid analysis configuration");
        }

        // ETAPA 1: Carregar todos os artefatos em memória
        ProcessLoaderV2Plus loader = loadTWXDataCompletelyFixed(config);
        // A busca pelo artefato principal foi movida para dentro do método de extração para mais flexibilidade
        Object mainArtifact = loader.getArtefatoDoCache(config.getProcessId());

        if (mainArtifact == null) {
            throw new Exception("Artifact principal com ID '" + config.getProcessId() + "' não foi encontrado no cache.");
        }

        // ETAPA 2: Passar o loader e o artefato principal para os extratores
        ProcessDefinitionV2Plus processDefinition = extractWithV2PlusExtractorsCompletelyFixed(mainArtifact, loader, config);

        // *** NOVA ETAPA DE EXTRAÇÃO DE UI ***
        System.out.println("🎨 Extracting UI components...");
        TWXToV2PlusUIExtractor uiExtractor = new TWXToV2PlusUIExtractor(loader);
        ProcessUIV2Plus ui = uiExtractor.extractUI(mainArtifact);
        // *** FIM DA NOVA ETAPA ***

        // ETAPA 3: Montar o relatório final
        EnhancedStructuredProcessReportV2 report = createCompleteReportV2PlusCompletelyFixed(processDefinition, config, loader);

        // Adiciona a UI extraída ao relatório
        report.setUi(ui);

        validateAndEnrichReportFixed(report);
        printDetailedExtractionStatistics(report);

        return report;
    }
    private static ProcessLoaderV2Plus loadTWXDataCompletelyFixed(AnalysisConfig config) {
        System.out.println("📂 Loading TWX data...");
        try {
            ProcessLoaderV2Plus loader = new ProcessLoaderV2Plus(config.getExtractionPath());

            // --- INÍCIO DA CORREÇÃO ESTRATÉGICA ---
            // Pré-carrega todos os artefatos do projeto e toolkits para garantir que as dependências estejam disponíveis.
            loader.carregarTodosOsArtefatosDoProjeto();
            // --- FIM DA CORREÇÃO ESTRATÉGICA ---

            // A linha abaixo ainda é útil para garantir que o processo principal está no cache,
            // mas o grosso do trabalho de dependência já foi feito.
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
        } else if (processObj instanceof Definitions) {
            // Se for um processo BPMN moderno, ele não terá um BPD legado.
            // Podemos criar um Bpd "virtual" a partir das Definitions para compatibilidade.
            System.out.println("✅ Modern BPMN process detected. Creating compatible BPD structure.");
            return createBpdFromDefinitions((Definitions) processObj);
        }
        System.err.println("⚠️ Could not find a valid BPD inside the loaded artifact.");
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
                // Verifica se o BPD legado contém dados BPMN 2.0 modernos
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

        // Simula a estrutura, mas o conteúdo do BPMN 2.0 é o que importa
        try {
            JAXBContext context = JAXBContext.newInstance(Definitions.class);
            java.io.StringWriter sw = new java.io.StringWriter();
            context.createMarshaller().marshal(definitions, sw);
            bpd.setBpmn2Data(sw.toString());
        } catch(Exception e) {
            bpd.setBpmn2Data(""); // Garante que não seja nulo
        }

        BusinessProcessDiagram diagram = new BusinessProcessDiagram();
        diagram.setId(definitions.getProcess().getId());
        diagram.setName(definitions.getProcess().getName());
        bpd.setBusinessProcessDiagram(diagram);

        return bpd;
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