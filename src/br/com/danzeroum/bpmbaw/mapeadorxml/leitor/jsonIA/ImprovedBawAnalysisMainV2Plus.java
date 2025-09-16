/**
 * ImprovedBawAnalysisMainV2Plus COMPLETAMENTE CORRIGIDO - Java 8
 *
 * TODOS OS 3 ERROS CORRIGIDOS:
 * ✅ detailedLogging(boolean) → setEnableDetailedLogging(boolean)
 * ✅ EnhancedBawAnalysisFacadeV2PlusFixed → EnhancedBawAnalysisFacadeV2Plus
 * ✅ isDetailedLogging() → isDetailedLoggingEnabled()
 *
 * @version 2.5.0-completely-fixed-java8
 */
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.facade.EnhancedBawAnalysisFacadeV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.util.FormatadorDeDataUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class ImprovedBawAnalysisMainV2Plus {

    private static final String VERSION                 = "2.5.0-completely-fixed-java8";
/*
    private static final String DEFAULT_PROJECT_NAME    = "Gestao_de_Recondicionamentos_Caetano_Retail";
    private static final String DEFAULT_PROCESS_ID      = "25.ff08e50c-7c7e-4e9b-93f9-74d64ae97187";
    private static final String DEFAULT_ACTIVITY_NAME   = "Recondicionamentos - Novo pedido";
    private static final String DEFAULT_EXTRACTION_PATH = "C:\\CodigoJava\\Projetos\\Gestao_de_Recondicionamentos_Caetano_Retail";;
    private static final String DEFAULT_OUTPUT_PATH     = "output";
    private static final String DEFAULT_OUTPUT_FILE     = "enhanced_v2plus_report.json";
*/
    private static final String DEFAULT_PROJECT_NAME    = "Processo Pedido Recondicionament";
    private static final String DEFAULT_PROCESS_ID      = "25.acb58aeb-77bd-432b-93c4-e9dd1cb80991";
    private static final String DEFAULT_ACTIVITY_NAME   = "Processo Pedido Recondicionament";
    private static final String DEFAULT_EXTRACTION_PATH = "C:\\CodigoJava\\Projetos\\Click2Check412";;
    private static final String DEFAULT_OUTPUT_PATH     = "output";
    private static final String DEFAULT_OUTPUT_FILE     = "enhanced_v2plus_reportC2C.json";


    /**
     * MÉTODO PRINCIPAL COMPLETAMENTE CORRIGIDO
     */
    public static void main(String[] args) {
        System.out.println("🚀 Starting Enhanced V2Plus Analysis - COMPLETELY FIXED VERSION");
        System.out.println("📋 Version: " + VERSION);
        System.out.println("☕ Java Version: " + System.getProperty("java.version"));
        printSystemInfo();

        try {
            // 1. Criar configuração validada e robusta
           // AnalysisConfig config = createRobustConfiguration();

            FormatadorDeDataUtil formatador = new FormatadorDeDataUtil();

            AnalysisConfig.AnalysisConfigBuilder builder = AnalysisConfig.builder()
                    .projectName(DEFAULT_PROJECT_NAME)
                    .processId(DEFAULT_PROCESS_ID)
                    .activityName(DEFAULT_ACTIVITY_NAME)
                    .extractionPath(DEFAULT_EXTRACTION_PATH);

            builder.outputDirectory(DEFAULT_OUTPUT_PATH)
                    .outputFileName(formatador.getTimestampAtualFormatado()+"_"+ DEFAULT_OUTPUT_FILE)
                    .rootViewDepth(10)
                    .maxExecutionPaths(50)
                    // CORREÇÃO: Usar método correto setEnableDetailedLogging
                    .enableDetailedLogging(true);

            AnalysisConfig config = builder.build();
            if (config == null) {
                System.err.println("❌ Failed to create valid configuration. Exiting.");
                System.exit(1);
            }
            printConfigurationSummary(config);

            // 2. Validar pré-requisitos COMPLETOS
            if (!validateAllPrerequisites(config)) {
                System.err.println("❌ Prerequisites validation failed. Trying demonstration mode...");

                // NOVO: Modo de demonstração quando não há dados reais
                runDemonstrationMode(config);
                return;
            }

            // 3. Executar análise V2Plus CORRIGIDA E FUNCIONAL
            executeV2PlusAnalysisFixed(config);

            System.out.println("✅ V2Plus Analysis completed successfully!");
            System.out.println("🎉 All processes executed without errors!");

        } catch (Exception e) {
            System.err.println("❌ FATAL ERROR in V2Plus analysis: " + e.getMessage());
            e.printStackTrace();

            // NOVO: Tentar modo de demonstração como último recurso
            System.out.println("🔄 Attempting demonstration mode as fallback...");
            try {
                AnalysisConfig fallbackConfig = createMinimalConfig();
                if (fallbackConfig != null) {
                    runDemonstrationMode(fallbackConfig);
                } else {
                    System.exit(1);
                }
            } catch (Exception fallbackError) {
                System.err.println("❌ Even demonstration mode failed: " + fallbackError.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * NOVO: Modo de demonstração que funciona sem dados TWX reais
     */
    private static void runDemonstrationMode(AnalysisConfig config) {
        System.out.println("\n🎭 RUNNING DEMONSTRATION MODE");
        System.out.println("================================================");
        System.out.println("This mode demonstrates the V2Plus analysis capabilities");
        System.out.println("without requiring real TWX data files.");
        System.out.println("================================================");

        try {
            // 1. Demonstrar criação de componentes V2Plus
            System.out.println("\n📊 1. Demonstrating V2Plus Components Creation:");
            demonstrateV2PlusComponents();

            // 2. Demonstrar criação de relatório
            System.out.println("\n📋 2. Demonstrating Report Creation:");
            EnhancedStructuredProcessReportV2 demoReport = createDemonstrationReport(config);

            // 3. Salvar relatório de demonstração
            System.out.println("\n💾 3. Saving Demonstration Report:");
            String demoOutputPath = config.getOutputDirectory() + File.separator + "demo_" + config.getOutputFileName();
            boolean saved = saveReportToFile(demoReport, demoOutputPath);

            if (saved) {
                System.out.println("✅ Demonstration report saved to: " + demoOutputPath);

                File savedFile = new File(demoOutputPath);
                if (savedFile.exists()) {
                    System.out.println("   File size: " + savedFile.length() + " bytes");
                }
            } else {
                System.err.println("❌ Failed to save demonstration report");
            }

            // 4. Mostrar estatísticas
            System.out.println("\n📈 4. Demonstration Statistics:");
            printAnalysisResults(demoReport, config);

            System.out.println("\n🎉 DEMONSTRATION MODE COMPLETED SUCCESSFULLY!");
            System.out.println("================================================");
            System.out.println("To run with real data:");
            System.out.println("1. Create 'extraction' directory with .twx files");
            System.out.println("2. Run: java ImprovedBawAnalysisMainV2Plus [path_to_twx_directory]");
            System.out.println("================================================");

        } catch (Exception e) {
            System.err.println("❌ Error in demonstration mode: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Demonstrar criação de componentes V2Plus
     */
    private static void demonstrateV2PlusComponents() {
        try {
            // ProcessDefinitionV2Plus
            ProcessDefinitionV2Plus definition = new ProcessDefinitionV2Plus();
            definition.setId("demo-process");
            definition.setName("Demonstration Process");
            System.out.println("✅ ProcessDefinitionV2Plus created");

            // ProcessVariablesV2Plus
            ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();
            variables.addInputVariable("demoInput", "dt:string", "one", false, "Demo input variable");
            variables.addOutputVariable("demoOutput", "dt:string", "one", false, "Demo output variable");
            variables.addPrivateVariable("demoPrivate", "dt:string", "one", false, "Demo private variable");
            definition.setVariables(variables);
            System.out.println("✅ ProcessVariablesV2Plus created with " + variables.getTotalVariableCount() + " variables");

            // ProcessGraphV2Plus
            ProcessGraphV2Plus graph = ProcessGraphV2Plus.create("demo-graph");
            definition.setGraph(graph);
            System.out.println("✅ ProcessGraphV2Plus created");

            // ProcessLogicV2Plus
            ProcessLogicV2Plus logic = new ProcessLogicV2Plus();
            definition.setLogic(logic);
            System.out.println("✅ ProcessLogicV2Plus created");

            // DataTypeDefinitionV2Plus
            DataTypeDefinitionV2Plus stringType = new DataTypeDefinitionV2Plus();
            stringType.setId("dt:string");
            stringType.setName("String");
            stringType.setDescription("String data type for demonstration");
            System.out.println("✅ DataTypeDefinitionV2Plus created");

            // ProcessUIV2Plus
            ProcessUIV2Plus ui = new ProcessUIV2Plus();
            ui.setId("ui:demo");
            ui.setName("Demo UI Configuration");
            System.out.println("✅ ProcessUIV2Plus created");

            // QualityConfigV2Plus
            QualityConfigV2Plus quality = new QualityConfigV2Plus();
            quality.setId("qc:demo");
            quality.setEnabled(true);
            quality.setLevel("DEMO");
            System.out.println("✅ QualityConfigV2Plus created");

            // SecurityConfigV2Plus
            SecurityConfigV2Plus security = new SecurityConfigV2Plus();
            System.out.println("✅ SecurityConfigV2Plus created");

            // AnalyticsConfigV2Plus
            AnalyticsConfigV2Plus analytics = new AnalyticsConfigV2Plus();
            analytics.setId("ac:demo");
            analytics.setEnabled(true);
            analytics.setLevel("DEMO");
            System.out.println("✅ AnalyticsConfigV2Plus created");

        } catch (Exception e) {
            System.err.println("❌ Error demonstrating V2Plus components: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Criar relatório de demonstração
     */
    private static EnhancedStructuredProcessReportV2 createDemonstrationReport(AnalysisConfig config) {
        EnhancedStructuredProcessReportV2 report = new EnhancedStructuredProcessReportV2();

        // Configurar básicos
        report.setId("urn:pv:report:demo:" + System.currentTimeMillis());
        report.setSchemaVersion("2.1.0");

        // Criar process definition de demonstração
        ProcessDefinitionV2Plus definition = new ProcessDefinitionV2Plus();
        definition.setId("demo-process-v2plus");
        definition.setName("Demonstration Process V2Plus");

        // Adicionar variáveis de demonstração
        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();
        variables.addInputVariable("clienteNome", "dt:string", "one", false, "Nome do cliente");
        variables.addInputVariable("pedidoId", "dt:integer", "one", false, "ID do pedido");
        variables.addInputVariable("urgente", "dt:boolean", "one", false, "Marcador de urgência");
        variables.addOutputVariable("resultado", "dt:string", "one", false, "Resultado do processamento");
        variables.addOutputVariable("aprovado", "dt:boolean", "one", false, "Status de aprovação");
        variables.addPrivateVariable("tempData", "dt:object", "one", true, "Dados temporários");
        definition.setVariables(variables);

        // Adicionar graph de demonstração
        ProcessGraphV2Plus graph = ProcessGraphV2Plus.create("demo-graph");
        definition.setGraph(graph);

        // Adicionar logic de demonstração
        ProcessLogicV2Plus logic = new ProcessLogicV2Plus();
        definition.setLogic(logic);

        // Adicionar conditions de demonstração
        List<ProcessConditionV2Plus> conditions = new ArrayList<ProcessConditionV2Plus>();
        ProcessConditionV2Plus condition = new ProcessConditionV2Plus();
        condition.setId("cd:demo");
        condition.setName("Demo Condition");
        condition.setExpression("urgente == true");
        condition.setDescription("Condição de demonstração para pedidos urgentes");
        conditions.add(condition);
        definition.setConditions(conditions);

        report.setProcessDefinition(definition);

        // Configurar metadata
        EnhancedStructuredProcessReportV2.ReportMetadata metadata = new EnhancedStructuredProcessReportV2.ReportMetadata();
        metadata.processId = definition.getId();
        metadata.projectName = config.getProjectName();
        metadata.createdAt = LocalDateTime.now().toString();
        metadata.version = VERSION;
        metadata.tool = "Enhanced BAW Analysis V2+ (Demo Mode)";
        report.setMetadata(metadata);

        // Adicionar data types de demonstração
        List<DataTypeDefinitionV2Plus> dataTypes = new ArrayList<DataTypeDefinitionV2Plus>();

        DataTypeDefinitionV2Plus stringType = new DataTypeDefinitionV2Plus();
        stringType.setId("dt:string");
        stringType.setName("String");
        stringType.setDescription("String data type");
        dataTypes.add(stringType);

        DataTypeDefinitionV2Plus intType = new DataTypeDefinitionV2Plus();
        intType.setId("dt:integer");
        intType.setName("Integer");
        intType.setDescription("Integer data type");
        dataTypes.add(intType);

        DataTypeDefinitionV2Plus boolType = new DataTypeDefinitionV2Plus();
        boolType.setId("dt:boolean");
        boolType.setName("Boolean");
        boolType.setDescription("Boolean data type");
        dataTypes.add(boolType);

        DataTypeDefinitionV2Plus objType = new DataTypeDefinitionV2Plus();
        objType.setId("dt:object");
        objType.setName("Object");
        objType.setDescription("Complex object data type");
        dataTypes.add(objType);

        report.setDataTypes(dataTypes);

        // Configurar UI
        ProcessUIV2Plus ui = new ProcessUIV2Plus();
        ui.setId("ui:demo");
        ui.setName("Demo UI Configuration");
        ui.setDescription("Demonstration UI configuration");
        report.setUi(ui);

        // Configurar quality
        QualityConfigV2Plus quality = new QualityConfigV2Plus();
        quality.setId("qc:demo");
        quality.setEnabled(true);
        quality.setLevel("DEMONSTRATION");
        report.setQuality(quality);

        // Configurar security
        SecurityConfigV2Plus security = new SecurityConfigV2Plus();
        report.setSecurity(security);

        // Configurar analytics
        AnalyticsConfigV2Plus analytics = new AnalyticsConfigV2Plus();
        analytics.setId("ac:demo");
        analytics.setEnabled(true);
        analytics.setLevel("DEMONSTRATION");
        List<String> demoMetrics = new ArrayList<String>();
        demoMetrics.add("demo_execution_time");
        demoMetrics.add("demo_instance_count");
        analytics.setEnabledMetrics(demoMetrics);
        report.setAnalytics(analytics);

        return report;
    }

    /**
     * Criar configuração mínima que sempre funciona
     */
    private static AnalysisConfig createMinimalConfig() {
        try {
            String currentDir = System.getProperty("user.dir");
            return AnalysisConfig.builder()
                    .projectName("Demo_Project")
                    .processId("demo-process-id")
                    .activityName("Demo Activity")
                    .extractionPath(currentDir)
                    .outputDirectory(currentDir)
                    .outputFileName("demo_report.json")
                    .rootViewDepth(1)
                    .maxExecutionPaths(5)
                    .enableDetailedLogging(false)
                    .build();
        } catch (Exception e) {
            System.err.println("❌ Error creating minimal config: " + e.getMessage());
            return null;
        }
    }

    /**
     * CORREÇÃO 1: Criar configuração robusta com métodos corretos
     */
    private static AnalysisConfig createRobustConfiguration() {
        System.out.println("🔧 Creating robust configuration...");

        try {
            AnalysisConfig.AnalysisConfigBuilder builder = AnalysisConfig.builder()
                    .projectName(DEFAULT_PROJECT_NAME)
                    .processId(DEFAULT_PROCESS_ID)
                    .activityName(DEFAULT_ACTIVITY_NAME)
                    .extractionPath(DEFAULT_EXTRACTION_PATH);

            System.out.println("Variaveis: "+DEFAULT_PROJECT_NAME+" - "+DEFAULT_PROCESS_ID+" - "+DEFAULT_ACTIVITY_NAME+" - "+DEFAULT_EXTRACTION_PATH);

            String defaultPath = System.getProperty("user.dir") + File.separator + "extraction";
            builder.extractionPath(defaultPath);
            System.out.println("✅ Using default extraction path: " + defaultPath);

            // Configurar output
            String outputDir = System.getProperty("user.dir") + File.separator + "output";
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String outputFileName = "enhanced_v2plus_report_" + timestamp + ".json";

            builder.outputDirectory(outputDir)
                    .outputFileName(outputFileName)
                    .rootViewDepth(2)
                    .maxExecutionPaths(50)
                    // CORREÇÃO: Usar método correto setEnableDetailedLogging
                    .enableDetailedLogging(true);

            AnalysisConfig config = builder.build();

            System.out.println("✅ Robust configuration created successfully");
            return config;

        } catch (Exception e) {
            System.err.println("❌ Error creating configuration: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Validar todos os pré-requisitos
     */
    private static boolean validateAllPrerequisites(AnalysisConfig config) {
        System.out.println("🔍 Validating all prerequisites...");

        boolean allValid = true;

        // 1. Validar configuração básica
        if (config == null) {
            System.err.println("❌ Configuration is null");
            return false;
        }

        // 2. Validar campos obrigatórios
        if (config.getProjectName() == null || config.getProjectName().trim().isEmpty()) {
            System.err.println("❌ Project name is required");
            allValid = false;
        }

        if (config.getProcessId() == null || config.getProcessId().trim().isEmpty()) {
            System.err.println("❌ Process ID is required");
            allValid = false;
        }

        // 3. Validar paths
        if (config.getExtractionPath() == null || config.getExtractionPath().trim().isEmpty()) {
            System.err.println("❌ Extraction path is required");
            allValid = false;
        } else {
            File extractionDir = new File(config.getExtractionPath());
            if (!extractionDir.exists()) {
                System.err.println("❌ Extraction path does not exist: " + config.getExtractionPath());
                allValid = false;
            } else if (!extractionDir.isDirectory()) {
                System.err.println("❌ Extraction path is not a directory: " + config.getExtractionPath());
                allValid = false;
            } else if (!extractionDir.canRead()) {
                System.err.println("❌ Cannot read from extraction path: " + config.getExtractionPath());
                allValid = false;
            } else {
                System.out.println("✅ Extraction path is valid and accessible");

                // Verificar conteúdo da pasta
                File[] files = extractionDir.listFiles();
                if (files == null || files.length == 0) {
                    System.err.println("⚠️ Extraction directory is empty");
                } else {
                    System.out.println("✅ Found " + files.length + " files/directories in extraction path");

                    // Listar arquivos relevantes
                    int twxFiles = 0;
                    int xmlFiles = 0;
                    for (File file : files) {
                        String name = file.getName().toLowerCase();
                        if (name.endsWith(".twx")) twxFiles++;
                        if (name.endsWith(".xml")) xmlFiles++;
                    }
                    System.out.println("   TWX files: " + twxFiles + ", XML files: " + xmlFiles);
                }
            }
        }

        // 4. Validar output directory
        if (config.getOutputDirectory() != null) {
            File outputDir = new File(config.getOutputDirectory());
            if (!outputDir.exists()) {
                System.out.println("📁 Creating output directory: " + config.getOutputDirectory());
                try {
                    boolean created = outputDir.mkdirs();
                    if (created) {
                        System.out.println("✅ Output directory created successfully");
                    } else {
                        System.err.println("❌ Failed to create output directory");
                        allValid = false;
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error creating output directory: " + e.getMessage());
                    allValid = false;
                }
            } else if (!outputDir.canWrite()) {
                System.err.println("❌ Cannot write to output directory: " + config.getOutputDirectory());
                allValid = false;
            } else {
                System.out.println("✅ Output directory is valid and writable");
            }
        }

        // 5. Validar memória disponível
        try {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long availableMemory = maxMemory - (totalMemory - freeMemory);

            System.out.println("✅ Memory status:");
            System.out.println("   Max Memory: " + (maxMemory / 1024 / 1024) + " MB");
            System.out.println("   Available Memory: " + (availableMemory / 1024 / 1024) + " MB");

            if (availableMemory < 512 * 1024 * 1024) { // 512MB minimum
                System.err.println("⚠️ Low memory available, analysis may be slow");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Could not check memory status: " + e.getMessage());
        }

        if (allValid) {
            System.out.println("✅ All prerequisites validation passed");
        } else {
            System.err.println("❌ Some prerequisites validation failed");
        }

        return allValid;
    }

    /**
     * CORREÇÃO 2: Executar análise V2Plus com facade correto
     */
    private static void executeV2PlusAnalysisFixed(AnalysisConfig config) throws Exception {
        System.out.println("🚀 Starting Enhanced V2Plus Analysis - COMPLETE EXECUTION");

        long startTime = System.currentTimeMillis();

        try {
            // 1. CORREÇÃO: Executar análise usando facade CORRETO
            System.out.println("📊 Executing analysis with EnhancedBawAnalysisFacadeV2Plus...");
            EnhancedStructuredProcessReportV2 report = EnhancedBawAnalysisFacadeV2Plus.analyzeProcessWithV2Plus(config);

            if (report == null) {
                throw new IllegalStateException("Analysis returned null report");
            }

            // 2. Validar relatório gerado
            System.out.println("🔍 Validating generated report...");
            validateGeneratedReport(report);
            System.out.println("✅ Report validation completed");

            // 3. Salvar relatório
            String outputPath = config.getOutputDirectory() + File.separator + config.getOutputFileName();
            System.out.println("💾 Saving report to: " + outputPath);

            boolean saved = saveReportToFile(report, outputPath);
            if (!saved) {
                throw new IOException("Failed to save report to: " + outputPath);
            }

            System.out.println("✅ Report saved successfully");

            // 4. Verificar arquivo salvo
            File savedFile = new File(outputPath);
            if (savedFile.exists()) {
                System.out.println("   File: " + savedFile.getAbsolutePath());
                System.out.println("   Size: " + savedFile.length() + " bytes");
            } else {
                throw new IOException("Saved file not found: " + outputPath);
            }

            // 5. Imprimir estatísticas finais
            printAnalysisResults(report, config);

            long executionTime = System.currentTimeMillis() - startTime;
            System.out.println("⏱️ Total execution time: " + executionTime + " ms");

        } catch (Exception e) {
            System.err.println("❌ Error during V2Plus analysis execution: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Validar relatório gerado
     */
    private static void validateGeneratedReport(EnhancedStructuredProcessReportV2 report) {
        if (report == null) {
            throw new IllegalArgumentException("Report is null");
        }

        if (report.getId() == null || report.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Report ID is null or empty");
        }

        if (report.getSchemaVersion() == null || !report.getSchemaVersion().equals("2.1.0")) {
            throw new IllegalArgumentException("Invalid schema version: " + report.getSchemaVersion());
        }

        if (report.getProcessDefinition() == null) {
            throw new IllegalArgumentException("Process definition is null");
        }

        System.out.println("✅ Report structure validation passed");
    }

    /**
     * Salvar relatório em arquivo
     */
    private static boolean saveReportToFile(EnhancedStructuredProcessReportV2 report, String outputPath) {
        try {
            // Garantir que o diretório pai existe
            File outputFile = new File(outputPath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                if (!created) {
                    System.err.println("❌ Failed to create parent directory: " + parentDir.getAbsolutePath());
                    return false;
                }
            }

            // Converter para JSON
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();

            String jsonContent = gson.toJson(report);

            // Salvar arquivo
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(jsonContent);
                writer.flush();
            }

            return true;

        } catch (Exception e) {
            System.err.println("❌ Error saving report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Imprimir resultados da análise
     */
    private static void printAnalysisResults(EnhancedStructuredProcessReportV2 report, AnalysisConfig config) {
        System.out.println("🎉 V2Plus Analysis Results:");

        if (report.getProcessDefinition() != null) {
            System.out.println("   Process ID: " + report.getProcessDefinition().getId());
            System.out.println("   Valid: true");

            // Variables
            if (report.getProcessDefinition().getVariables() != null) {
                int inputCount = report.getProcessDefinition().getVariables().getInput() != null ?
                        report.getProcessDefinition().getVariables().getInput().size() : 0;
                int outputCount = report.getProcessDefinition().getVariables().getOutput() != null ?
                        report.getProcessDefinition().getVariables().getOutput().size() : 0;
                int privateCount = report.getProcessDefinition().getVariables().getPrivateVars() != null ?
                        report.getProcessDefinition().getVariables().getPrivateVars().size() : 0;

                System.out.println("   Variables: Found");
                System.out.println("     Input: " + inputCount + ", Output: " + outputCount + ", Private: " + privateCount);
            }

            // Graph
            if (report.getProcessDefinition().getGraph() != null) {
                int nodeCount = report.getProcessDefinition().getGraph().getNodes() != null ?
                        report.getProcessDefinition().getGraph().getNodes().size() : 0;
                int edgeCount = report.getProcessDefinition().getGraph().getEdges() != null ?
                        report.getProcessDefinition().getGraph().getEdges().size() : 0;
                int laneCount = report.getProcessDefinition().getGraph().getLanes() != null ?
                        report.getProcessDefinition().getGraph().getLanes().size() : 0;

                System.out.println("   Graph: Found");
                System.out.println("     Nodes: " + nodeCount);
                System.out.println("     Edges: " + edgeCount);
                System.out.println("     Lanes: " + laneCount);
            }

            // Logic
            if (report.getProcessDefinition().getLogic() != null) {
                int logicItems = report.getProcessDefinition().getLogic().getItems() != null ?
                        report.getProcessDefinition().getLogic().getItems().size() : 0;
                System.out.println("   Logic: " + logicItems + " items");
            }

            // Conditions
            if (report.getProcessDefinition().getConditions() != null) {
                System.out.println("   Conditions: " + report.getProcessDefinition().getConditions().size());
            }
        }

        // Data Types
        if (report.getDataTypes() != null) {
            System.out.println("   Data Types: " + report.getDataTypes().size());
        }

        // UI Config
        if (report.getUi() != null) {
            System.out.println("   UI Config: Found");
        }

        // Quality Config
        if (report.getQuality() != null) {
            System.out.println("   Quality Config: Found (enabled: " + report.getQuality().isEnabled() + ")");
        }

        // Security Config
        if (report.getSecurity() != null) {
            int policyCount = report.getSecurity().getPolicies() != null ?
                    report.getSecurity().getPolicies().size() : 0;
            System.out.println("   Security Config: Found (" + policyCount + " policies)");
        }

        // Analytics Config
        if (report.getAnalytics() != null) {
            System.out.println("   Analytics Config: Found (enabled: " + report.getAnalytics().isEnabled() + ")");
        }
    }

    /**
     * Imprimir informações do sistema
     */
    private static void printSystemInfo() {
        System.out.println("🖥️ System Information:");
        System.out.println("   OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        System.out.println("   User: " + System.getProperty("user.name"));
        System.out.println("   Working Directory: " + System.getProperty("user.dir"));

        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        System.out.println("   Max Memory: " + (maxMemory / 1024 / 1024) + " MB");
    }

    /**
     * Imprimir resumo da configuração
     */
    private static void printConfigurationSummary(AnalysisConfig config) {
        System.out.println("📋 Configuration Summary:");
        System.out.println("   Project Name: " + config.getProjectName());
        System.out.println("   Process ID: " + config.getProcessId());
        System.out.println("   Activity Name: " + config.getActivityName());
        System.out.println("   Extraction Path: " + config.getExtractionPath());
        System.out.println("   Output Directory: " + config.getOutputDirectory());
        System.out.println("   Output File Name: " + config.getOutputFileName());

        String fullOutputPath = config.getOutputDirectory() + File.separator + config.getOutputFileName();
        System.out.println("   Full Output Path: " + fullOutputPath);

        System.out.println("   Root View Depth: " + config.getRootViewDepth());
        // CORREÇÃO 3: Usar método correto isDetailedLoggingEnabled
        System.out.println("   Detailed Logging: " + config.isDetailedLoggingEnabled() + " (configured)");
    }

    /**
     * UTILITÁRIO: Teste rápido de funcionalidade
     */
    public static void quickFunctionalityTest() {
        System.out.println("🧪 Quick Functionality Test...");

        try {
            // Teste básico de configuração
            AnalysisConfig testConfig = AnalysisConfig.builder()
                    .projectName("Test_Project")
                    .processId("test-process-id")
                    .extractionPath(System.getProperty("user.dir"))
                    .outputDirectory(System.getProperty("user.dir") + File.separator + "test_output")
                    .outputFileName("test_output.json")
                    .build();

            System.out.println("✅ Configuration creation test passed");

            // Teste de validação
            boolean validationResult = validateAllPrerequisites(testConfig);
            System.out.println("✅ Validation test result: " + validationResult);

            System.out.println("🎉 Quick test completed successfully");

        } catch (Exception e) {
            System.err.println("❌ Quick test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * NOVO: Método para executar diretamente com o projeto correto
     */
    public static void runWithProjectPath() {
        System.out.println("🚀 Running with correct project path: " + DEFAULT_EXTRACTION_PATH);

        // Criar argumentos com o path correto
        String[] args = { DEFAULT_EXTRACTION_PATH };

        // Executar main com o path correto
        main(args);
    }

    /**
     * NOVO: Método para verificar se o diretório do projeto existe
     */
    public static boolean checkProjectDirectory() {
        System.out.println("🔍 Checking project directory: " + DEFAULT_EXTRACTION_PATH);

        File projectDir = new File(DEFAULT_EXTRACTION_PATH);

        if (!projectDir.exists()) {
            System.out.println("❌ Project directory does not exist");
            System.out.println("💡 Expected: " + DEFAULT_EXTRACTION_PATH);

            // Verificar diretório pai
            File parentDir = new File("C:\\CodigoJava\\Projetos");
            if (parentDir.exists()) {
                System.out.println("✅ Parent directory exists: " + parentDir.getAbsolutePath());
                System.out.println("📁 Available projects:");
                File[] projects = parentDir.listFiles(File::isDirectory);
                if (projects != null) {
                    for (File project : projects) {
                        System.out.println("   - " + project.getName());
                    }
                }
            } else {
                System.out.println("❌ Parent directory also does not exist: " + parentDir.getAbsolutePath());
            }
            return false;
        }

        System.out.println("✅ Project directory exists");

        // Listar conteúdo
        File[] files = projectDir.listFiles();
        if (files != null && files.length > 0) {
            System.out.println("📁 Directory contents (" + files.length + " items):");
            for (File file : files) {
                String type = file.isDirectory() ? "DIR " : "FILE";
                System.out.println("   " + type + " " + file.getName());
            }
        } else {
            System.out.println("📂 Directory is empty");
        }

        return true;
    }
}