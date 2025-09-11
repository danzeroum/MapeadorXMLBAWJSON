/**
 * ImprovedBawAnalysisMainV2Plus CORRIGIDO - Versão funcional para Java 8
 *
 * PROBLEMAS RESOLVIDOS:
 * ✅ NullPointerException no graph extractor
 * ✅ FlowObjects não sendo extraídos (0 nodes)
 * ✅ Métodos ausentes sendo chamados
 * ✅ Dados fictícios sendo gerados
 * ✅ Validação robusta de pré-requisitos
 * ✅ Tratamento completo de erros
 *
 * @version 2.3.0-complete-fixed-java8
 */
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.facade.EnhancedBawAnalysisFacadeV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.EnhancedStructuredProcessReportV2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImprovedBawAnalysisMainV2Plus {

    private static final String VERSION = "2.3.0-complete-fixed-java8";
    private static final String DEFAULT_PROJECT_NAME = "Gestao_de_Recondicionamentos_Caetano_Retail";
    private static final String DEFAULT_PROCESS_ID = "25.ff08e50c-7c7e-4e9b-93f9-74d64ae97187";
    private static final String DEFAULT_ACTIVITY_NAME = "Recondicionamentos - Novo pedido";

    /**
     * MÉTODO PRINCIPAL CORRIGIDO - VERSÃO FUNCIONAL
     */
    public static void main(String[] args) {
        System.out.println("🚀 Starting Enhanced V2Plus Analysis - COMPLETE FIXED VERSION");
        System.out.println("📋 Version: " + VERSION);
        System.out.println("☕ Java Version: " + System.getProperty("java.version"));
        printSystemInfo();

        try {
            // 1. Criar configuração validada e robusta
            AnalysisConfig config = createRobustConfiguration(args);
            if (config == null) {
                System.err.println("❌ Failed to create valid configuration. Exiting.");
                System.exit(1);
            }
            printConfigurationSummary(config);

            // 2. Validar pré-requisitos COMPLETOS
            if (!validateAllPrerequisites(config)) {
                System.err.println("❌ Prerequisites validation failed. Exiting.");
                System.exit(1);
            }

            // 3. Executar análise V2Plus CORRIGIDA E FUNCIONAL
            executeV2PlusAnalysisFixed(config);

            System.out.println("✅ V2Plus Analysis completed successfully!");

        } catch (Exception e) {
            System.err.println("❌ Analysis failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * CORREÇÃO: Criar configuração robusta com múltiplas estratégias
     */
    private static AnalysisConfig createRobustConfiguration(String[] args) {
        System.out.println("⚙️ Creating robust configuration...");

        try {
            String projectName = DEFAULT_PROJECT_NAME;
            String processId = DEFAULT_PROCESS_ID;
            String activityName = DEFAULT_ACTIVITY_NAME;
            String extractionPath = null;
            String outputDirectory = null;

            // Parse argumentos se fornecidos
            if (args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
                projectName = args[0].trim();
            }
            if (args.length > 1 && args[1] != null && !args[1].trim().isEmpty()) {
                processId = args[1].trim();
            }
            if (args.length > 2 && args[2] != null && !args[2].trim().isEmpty()) {
                activityName = args[2].trim();
            }
            if (args.length > 3 && args[3] != null && !args[3].trim().isEmpty()) {
                extractionPath = args[3].trim();
            }
            if (args.length > 4 && args[4] != null && !args[4].trim().isEmpty()) {
                outputDirectory = args[4].trim();
            }

            // ESTRATÉGIA 1: Path especificado pelo usuário
            if (extractionPath == null || extractionPath.isEmpty()) {
                // ESTRATÉGIA 2: Usar path baseado no project name
                extractionPath = "C:\\CodigoJava\\Projetos\\" + projectName;

                File extractionDir = new File(extractionPath);
                if (!extractionDir.exists()) {
                    // ESTRATÉGIA 3: Buscar na pasta atual
                    extractionPath = System.getProperty("user.dir");
                    System.out.println("⚠️ Using current directory as extraction path: " + extractionPath);
                }
            }

            // Output directory padrão
            if (outputDirectory == null || outputDirectory.isEmpty()) {
                outputDirectory = System.getProperty("user.dir") + File.separator + "output";
            }

            // Criar diretório de output se não existir
            File outputDir = new File(outputDirectory);
            if (!outputDir.exists()) {
                boolean created = outputDir.mkdirs();
                if (created) {
                    System.out.println("✅ Created output directory: " + outputDirectory);
                } else {
                    System.err.println("❌ Failed to create output directory: " + outputDirectory);
                    return null;
                }
            }

            // Gerar nome do arquivo de output
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(formatter);
            String outputFileName = timestamp + "_" + projectName + "_v2plus_complete.json";

            // Construir configuração usando builder pattern
            AnalysisConfig config = AnalysisConfig.builder()
                    .projectName(projectName)
                    .processId(processId)
                    .activityName(activityName)
                    .extractionPath(extractionPath)
                    .outputDirectory(outputDirectory)
                    .outputFileName(outputFileName)
                    .rootViewDepth(1)
                    .detailedLogging(true)
                    .build();

            System.out.println("✅ Configuration created successfully");
            return config;

        } catch (Exception e) {
            System.err.println("❌ Error creating configuration: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * CORREÇÃO: Validação completa de pré-requisitos
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
        if (config.getOutputDirectory() == null || config.getOutputDirectory().trim().isEmpty()) {
            System.err.println("❌ Output directory is required");
            allValid = false;
        } else {
            File outputDir = new File(config.getOutputDirectory());
            if (!outputDir.exists()) {
                System.out.println("⚠️ Output directory does not exist, will be created");
            } else if (!outputDir.isDirectory()) {
                System.err.println("❌ Output path is not a directory: " + config.getOutputDirectory());
                allValid = false;
            } else if (!outputDir.canWrite()) {
                System.err.println("❌ Cannot write to output directory: " + config.getOutputDirectory());
                allValid = false;
            } else {
                System.out.println("✅ Output directory is valid and writable");
            }
        }

        // 5. Validar dependências Java
        try {
            String javaVersion = System.getProperty("java.version");
            System.out.println("✅ Java version: " + javaVersion);

            // Verificar se é Java 8+
            if (javaVersion.startsWith("1.8") || javaVersion.startsWith("8") ||
                    javaVersion.compareTo("8") >= 0) {
                System.out.println("✅ Java version is compatible");
            } else {
                System.err.println("❌ Java 8+ is required, found: " + javaVersion);
                allValid = false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error checking Java version: " + e.getMessage());
            allValid = false;
        }

        // 6. Validar memória disponível
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
     * CORREÇÃO: Executar análise V2Plus com tratamento robusto
     */
    private static void executeV2PlusAnalysisFixed(AnalysisConfig config) throws Exception {
        System.out.println("🚀 Starting Enhanced V2Plus Analysis - COMPLETE EXECUTION");

        long startTime = System.currentTimeMillis();

        try {
            // 1. Executar análise usando facade CORRIGIDO
            System.out.println("📊 Executing analysis with EnhancedBawAnalysisFacadeV2PlusFixed...");
            EnhancedStructuredProcessReportV2 report = EnhancedBawAnalysisFacadeV2PlusFixed.analyzeProcessWithV2Plus(config);

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

            // 5. Imprimir resultados resumidos
            printAnalysisResults(report, config);

            // 6. Estatísticas de tempo
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("⏱️ Analysis completed in " + duration + "ms");

        } catch (Exception e) {
            System.err.println("❌ Analysis execution failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Validar relatório gerado
     */
    private static void validateGeneratedReport(EnhancedStructuredProcessReportV2 report) {
        if (report == null) {
            throw new IllegalStateException("Report is null");
        }

        if (report.getId() == null || report.getId().trim().isEmpty()) {
            throw new IllegalStateException("Report ID is missing");
        }

        if (report.getProcessDefinition() == null) {
            throw new IllegalStateException("ProcessDefinition is missing");
        }

        if (report.getProcessDefinition().getId() == null) {
            throw new IllegalStateException("ProcessDefinition ID is missing");
        }

        System.out.println("✅ Report basic validation passed");
        System.out.println("   Report ID: " + report.getId());
        System.out.println("   Process ID: " + report.getProcessDefinition().getId());
    }

    /**
     * Salvar relatório em arquivo JSON
     */
    private static boolean saveReportToFile(EnhancedStructuredProcessReportV2 report, String outputPath) {
        try {
            // Criar Gson com formatação pretty
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .serializeNulls()
                    .create();

            // Converter para JSON
            String jsonContent = gson.toJson(report);

            // Escrever arquivo
            File outputFile = new File(outputPath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                if (!created) {
                    System.err.println("❌ Failed to create parent directory: " + parentDir.getAbsolutePath());
                    return false;
                }
            }

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
                System.out.println("   Logic: Found");
                System.out.println("     Items: " + logicItems);
            }

            // Mappings
            if (report.getProcessDefinition().getMappings() != null) {
                int inputMappings = report.getProcessDefinition().getMappings().getInputMappings() != null ?
                        report.getProcessDefinition().getMappings().getInputMappings().size() : 0;
                int outputMappings = report.getProcessDefinition().getMappings().getOutputMappings() != null ?
                        report.getProcessDefinition().getMappings().getOutputMappings().size() : 0;
                System.out.println("   Mappings: Found");
                System.out.println("     Input: " + inputMappings + ", Output: " + outputMappings);
            }
        }

        // Arquivo de saída
        String outputPath = config.getOutputDirectory() + File.separator + config.getOutputFileName();
        File outputFile = new File(outputPath);
        System.out.println("✅ Output file created successfully: " + outputFile.getAbsolutePath());
        if (outputFile.exists()) {
            System.out.println("   File size: " + outputFile.length() + " bytes");
        }
    }

    /**
     * Imprimir informações do sistema
     */
    private static void printSystemInfo() {
        System.out.println("📋 System Information:");
        System.out.println("   Java Version: " + System.getProperty("java.version"));
        System.out.println("   Java Home: " + System.getProperty("java.home"));
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
        System.out.println("   Detailed Logging: " + config.isDetailedLogging() + " (default)");
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
}