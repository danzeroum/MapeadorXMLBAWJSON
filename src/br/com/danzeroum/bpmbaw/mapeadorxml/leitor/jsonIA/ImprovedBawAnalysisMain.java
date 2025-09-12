package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.BawAnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.facade.EnhancedBawAnalysisFacadeV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.util.AnalysisLogger;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportGeneratorV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportNavigatorV3;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Main orchestrator for IBM BAW process analysis V3.
 * Provides comprehensive analysis with quality reports, validation, and metrics.
 */
public class ImprovedBawAnalysisMain {








    // CORREÇÃO 3: Método executeJsonReportGenerationInicio com debugging
    public static void executeJsonReportGenerationInicio(AnalysisConfig config) throws Exception {
        System.out.println("🚀 Starting Enhanced V2 Analysis...");
        System.out.println("🔧 Configuration:");
        System.out.println("   Project: " + config.getProjectName());
        System.out.println("   Process ID: " + config.getProcessId());
        System.out.println("   Extraction Path: " + config.getExtractionPath());
        System.out.println("   Output Directory: " + config.getOutputDirectory());
        System.out.println("   Output File Name: " + config.getOutputFileName());
        System.out.println("   Full Output Path: " + config.getOutputFilePath());

        // Verificar se diretório de extração existe
        File extractionDir = new File(config.getExtractionPath());
        if (!extractionDir.exists()) {
            System.err.println("❌ ERRO: Diretório de extração não existe: " + config.getExtractionPath());
            return;
        }

        try {
            EnhancedBawAnalysisFacadeV2 facade = new EnhancedBawAnalysisFacadeV2();
            EnhancedStructuredProcessReportV2 report = facade.executeEnhancedAnalysisV2(config);

            System.out.println("✅ Análise concluída com sucesso!");
            System.out.println("📄 Report ID: " + report.getId());

            // Verificação final
            File outputFile = new File(config.getOutputFilePath());
            if (outputFile.exists()) {
                System.out.println("✅ SUCESSO: Arquivo criado em: " + outputFile.getAbsolutePath());
                System.out.println("📊 Tamanho: " + (outputFile.length() / 1024) + " KB");
            } else {
                System.err.println("❌ FALHA: Arquivo não encontrado em: " + outputFile.getAbsolutePath());

                // Debug do diretório
                File parentDir = outputFile.getParentFile();
                if (parentDir != null && parentDir.exists()) {
                    System.err.println("📁 Conteúdo do diretório:");
                    File[] files = parentDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            System.err.println("   - " + file.getName() + " (" + file.length() + " bytes)");
                        }
                    } else {
                        System.err.println("   (diretório vazio ou inacessível)");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ ERRO durante a análise:");
            System.err.println("   Tipo: " + e.getClass().getSimpleName());
            System.err.println("   Mensagem: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Executes comprehensive JSON report generation with all analysis features.
     *
     * @param config Analysis configuration
     * @throws Exception if analysis fails
     */
    public static void executeJsonReportGeneration(AnalysisConfig config) throws Exception {
        String sessionId = UUID.randomUUID().toString().substring(0, 8);
        AnalysisLogger logger = null;

        try {
            // Initialize logging
            logger = initializeLogger(config, sessionId);
            logger.startSession(config.getProjectName(), config.getProcessId());

            System.out.println("=== IBM BAW Process Analysis V3 ===");
            System.out.println("Session ID: " + sessionId);
            System.out.println("Configuration: " + config);

            // Ensure output directory exists
            ensureOutputDirectoryExists(config, logger);

            // Execute main analysis pipeline
            AnalysisResults results = executeAnalysisPipeline(config, logger);

            // Generate comprehensive reports
            generateComprehensiveReports(config, results, logger);

            // Print success summary
            printSuccessSummary(config, results, logger);

        } catch (Exception e) {
            System.err.println("❌ Analysis failed: " + e.getMessage());
            if (logger != null) {
                logger.error("MAIN", "Analysis execution failed", e);
            }
            e.printStackTrace();
            throw e;
        } finally {
            if (logger != null) {
                logger.endSession();
                logger.close();
            }
        }
    }

    /**
     * Initializes the logging system based on configuration.
     */
    private static AnalysisLogger initializeLogger(AnalysisConfig config, String sessionId) throws Exception {
        if (config.isDetailedLoggingEnabled()) {
            // Create log file
            String logFileName = String.format("%s_%s_analysis.log",
                    BawAnalysisConfig.generateTimestamp(), sessionId);
            String logFilePath = new File(config.getOutputDirectory(), logFileName).getPath();

            AnalysisLogger fileLogger = AnalysisLogger.createFileLogger(logFilePath, sessionId, true);
            System.out.println("📝 Detailed logging enabled: " + logFilePath);
            return fileLogger;
        } else {
            return AnalysisLogger.createConsoleLogger(sessionId, false);
        }
    }

    /**
     * Ensures output directory structure exists.
     */
    private static void ensureOutputDirectoryExists(AnalysisConfig config, AnalysisLogger logger) {
        BawAnalysisConfig.ensureOutputDirectoryExists();
        File outputDir = new File(config.getOutputDirectory());
        if (!outputDir.exists()) {
            boolean created = outputDir.mkdirs();
            logger.info("MAIN", "Created output directory: " + outputDir.getAbsolutePath() + " (success: " + created + ")");
        }
    }

    /**
     * Executes the main analysis pipeline.
     */
    private static AnalysisResults executeAnalysisPipeline(AnalysisConfig config, AnalysisLogger logger) throws Exception {
        logger.milestone("PIPELINE", "Starting analysis pipeline");
        logger.startTimer("total_pipeline");

        // Initialize core components
        logger.info("MAIN", "Initializing ProcessLoader...");
        logger.startTimer("loader_init");
        ProcessLoaderV2Plus loader = new ProcessLoaderV2Plus(config.getExtractionPath(),
                new PrintWriter(System.out)); // TODO: Integrate with AnalysisLogger
        logger.stopTimer("loader_init");

        // Load process tree
        logger.info("MAIN", "Loading process tree for: " + config.getProcessId());
        logger.startTimer("process_loading");
        loader.loadProcessInMemory(config.getProcessId());
        logger.stopTimer("process_loading");
        logger.logMemoryUsage("MAIN");

        // Initialize report generator
        logger.info("MAIN", "Creating report generator...");
        JsonReportGeneratorV2 generator;
        try (PrintWriter writer = new PrintWriter(new FileWriter(config.getOutputFilePath()))) {
            generator = new JsonReportGeneratorV2(
                    writer,
                    config.getProjectName(),
                    config.getProjectName() + ".twx"
            );

            // Initialize navigator
            logger.info("MAIN", "Initializing V3 navigator...");
            JsonReportNavigatorV3 navigator = new JsonReportNavigatorV3(generator, loader);

            // Populate main report
            logger.info("MAIN", "Populating main report...");
            logger.startTimer("report_population");
            navigator.populateReport(config.getProcessId());
            logger.stopTimer("report_population");

            // Generate JSON output
            logger.info("MAIN", "Generating final JSON...");
            logger.startTimer("json_generation");
            generator.generate();
            logger.stopTimer("json_generation");

            // Collect results
            AnalysisResults results = new AnalysisResults();
            results.navigator = navigator;
            results.generator = generator;
            results.loader = loader;
            results.report = generator.getReport();

            logger.stopTimer("total_pipeline");
            logger.milestone("PIPELINE", "Analysis pipeline completed");

            return results;
        }
    }

    /**
     * Generates comprehensive analysis reports.
     */
    private static void generateComprehensiveReports(AnalysisConfig config, AnalysisResults results,
                                                     AnalysisLogger logger) throws Exception {
        logger.milestone("REPORTS", "Starting comprehensive report generation");

        // Initialize analysis services
        VariableEnricherService variableEnricher = new VariableEnricherService(results.generator, results.loader);
        QualityReportGeneratorService qualityService = new QualityReportGeneratorService(variableEnricher);
        BpmnStructureValidatorService validationService = new BpmnStructureValidatorService();
        ProcessMetricsService metricsService = new ProcessMetricsService();

        // Generate quality report
        logger.info("REPORTS", "Generating quality analysis report...");
        logger.startTimer("quality_report");
        QualityReportGeneratorService.QualityAnalysisReport qualityReport =
                qualityService.generateQualityReport(results.report);
        saveQualityReport(qualityReport, config, logger);
        logger.stopTimer("quality_report");

        // Generate validation report
        logger.info("REPORTS", "Generating BPMN structure validation...");
        logger.startTimer("validation_report");
        BpmnStructureValidatorService.BpmnValidationReport validationReport =
                generateValidationReport(results.report, validationService, logger);
        saveValidationReport(validationReport, config, logger);
        logger.stopTimer("validation_report");

        // Generate metrics report
        logger.info("REPORTS", "Generating process metrics...");
        logger.startTimer("metrics_report");
        ProcessMetricsService.ProcessMetricsReport metricsReport =
                metricsService.generateMetricsReport(results.report);
        saveMetricsReport(metricsReport, config, logger);
        logger.stopTimer("metrics_report");

        // Log analysis summaries
        logAnalysisSummaries(results, qualityReport, validationReport, metricsReport, logger);

        logger.milestone("REPORTS", "Comprehensive reports completed");
    }

    /**
     * Generates BPMN validation report for all artifacts.
     */
    private static BpmnStructureValidatorService.BpmnValidationReport generateValidationReport(
            JsonReportV2 report, BpmnStructureValidatorService validationService, AnalysisLogger logger) {

        BpmnStructureValidatorService.BpmnValidationReport validationReport =
                new BpmnStructureValidatorService.BpmnValidationReport();

        if (report.getArtifacts() != null) {
            for (JsonReportV2.Artifact artifact : report.getArtifacts()) {
                logger.debug("VALIDATION", "Validating artifact: " + artifact.getName());
                BpmnStructureValidatorService.BpmnValidationResult result =
                        validationService.validateBpmnStructure(artifact);
                validationReport.addResult(result);

                if (result.hasErrors()) {
                    logger.warn("VALIDATION", "Validation errors found in: " + artifact.getName());
                }
            }
        }

        return validationReport;
    }

    // ========== REPORT SAVING METHODS ==========

    /**
     * Saves quality analysis report to file.
     */
    private static void saveQualityReport(QualityReportGeneratorService.QualityAnalysisReport qualityReport,
                                          AnalysisConfig config, AnalysisLogger logger) throws Exception {
        String qualityReportPath = config.getOutputFilePath().replace(".json", "_quality_report.txt");

        try (PrintWriter reportWriter = new PrintWriter(new FileWriter(qualityReportPath))) {
            writeQualityReportHeader(reportWriter, config);
            writeQualityReportContent(reportWriter, qualityReport);
        }

        logger.info("REPORTS", "Quality report saved: " + qualityReportPath);
    }

    /**
     * Saves validation report to file.
     */
    private static void saveValidationReport(BpmnStructureValidatorService.BpmnValidationReport validationReport,
                                             AnalysisConfig config, AnalysisLogger logger) throws Exception {
        String validationReportPath = config.getOutputFilePath().replace(".json", "_validation_report.txt");

        try (PrintWriter reportWriter = new PrintWriter(new FileWriter(validationReportPath))) {
            writeValidationReportHeader(reportWriter, config);
            writeValidationReportContent(reportWriter, validationReport);
        }

        logger.info("REPORTS", "Validation report saved: " + validationReportPath);
    }

    /**
     * Saves metrics report to file.
     */
    private static void saveMetricsReport(ProcessMetricsService.ProcessMetricsReport metricsReport,
                                          AnalysisConfig config, AnalysisLogger logger) throws Exception {
        String metricsReportPath = config.getOutputFilePath().replace(".json", "_metrics_report.html");

        try (PrintWriter reportWriter = new PrintWriter(new FileWriter(metricsReportPath))) {
            writeMetricsReportHtml(reportWriter, metricsReport, config);
        }

        logger.info("REPORTS", "Metrics report saved: " + metricsReportPath);
    }

    // ========== REPORT CONTENT WRITERS ==========

    private static void writeQualityReportHeader(PrintWriter writer, AnalysisConfig config) {
        writer.println("===============================================");
        writer.println("IBM BAW QUALITY ANALYSIS REPORT FOR AI");
        writer.println("===============================================");
        writer.println("Project: " + config.getProjectName());
        writer.println("Process: " + config.getProcessId());
        writer.println("Generated: " + BawAnalysisConfig.generateTimestamp());
        writer.println();
    }

    private static void writeQualityReportContent(PrintWriter writer,
                                                  QualityReportGeneratorService.QualityAnalysisReport qualityReport) {
        writer.println("OVERALL ASSESSMENT: " + qualityReport.calculateOverallScore());
        writer.println();

        // Issues section
        writer.println("IDENTIFIED ISSUES:");
        writer.println("==================");
        if (qualityReport.getIssues().isEmpty()) {
            writer.println("No quality issues found.");
        } else {
            for (QualityReportGeneratorService.QualityIssue issue : qualityReport.getIssues()) {
                writer.println(String.format("[%s] %s", issue.getSeverity(), issue.getDescription()));
            }
        }
        writer.println();

        // Recommendations section
        writer.println("AI OPTIMIZATION RECOMMENDATIONS:");
        writer.println("=================================");
        if (qualityReport.getRecommendations().isEmpty()) {
            writer.println("No specific recommendations at this time.");
        } else {
            for (String recommendation : qualityReport.getRecommendations()) {
                writer.println("→ " + recommendation);
            }
        }
    }

    private static void writeValidationReportHeader(PrintWriter writer, AnalysisConfig config) {
        writer.println("===============================================");
        writer.println("IBM BAW STRUCTURE VALIDATION REPORT");
        writer.println("===============================================");
        writer.println("Project: " + config.getProjectName());
        writer.println("Process: " + config.getProcessId());
        writer.println("Generated: " + BawAnalysisConfig.generateTimestamp());
        writer.println();
    }

    private static void writeValidationReportContent(PrintWriter writer,
                                                     BpmnStructureValidatorService.BpmnValidationReport validationReport) {
        writer.println("VALIDATION SUMMARY:");
        writer.println("==================");
        writer.println("Total Artifacts: " + validationReport.getResults().size());
        writer.println("Valid Artifacts: " + validationReport.getValidArtifactCount());
        writer.println("Invalid Artifacts: " + validationReport.getInvalidArtifactCount());
        writer.println();

        if (validationReport.hasAnyErrors()) {
            writer.println("VALIDATION ISSUES:");
            writer.println("==================");
            for (BpmnStructureValidatorService.BpmnValidationResult result : validationReport.getResults()) {
                if (result.hasErrors() || result.hasWarnings()) {
                    writer.println("Artifact: " + result.getArtifactName());
                    writer.println("Summary: " + result.getSummary());
                    for (BpmnStructureValidatorService.ValidationIssue issue : result.getIssues()) {
                        writer.println("  " + issue);
                    }
                    writer.println();
                }
            }
        }
    }

    private static void writeMetricsReportHtml(PrintWriter writer,
                                               ProcessMetricsService.ProcessMetricsReport metricsReport,
                                               AnalysisConfig config) {
        writer.println("<!DOCTYPE html>");
        writer.println("<html><head><title>Process Metrics Report</title>");
        writer.println("<style>");
        writer.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        writer.println("h1, h2 { color: #2c3e50; }");
        writer.println("table { border-collapse: collapse; width: 100%; margin: 10px 0; }");
        writer.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        writer.println("th { background-color: #f2f2f2; }");
        writer.println(".metric-good { color: #27ae60; }");
        writer.println(".metric-warning { color: #f39c12; }");
        writer.println(".metric-error { color: #e74c3c; }");
        writer.println(".summary-box { background: #ecf0f1; padding: 15px; margin: 10px 0; border-radius: 5px; }");
        writer.println("</style></head><body>");

        writer.println("<h1>Process Metrics Report</h1>");
        writer.println("<p><strong>Project:</strong> " + config.getProjectName() + "</p>");
        writer.println("<p><strong>Process:</strong> " + config.getProcessId() + "</p>");
        writer.println("<p><strong>Generated:</strong> " + BawAnalysisConfig.generateTimestamp() + "</p>");

        // Executive Summary
        ProcessMetricsService.ExecutiveSummary summary = metricsReport.generateExecutiveSummary();
        writer.println("<div class='summary-box'>");
        writer.println("<h2>Executive Summary</h2>");
        writer.println("<p><strong>Overall Assessment:</strong> " + summary.getOverallAssessment() + "</p>");
        writer.println("</div>");

        // Aggregated Metrics
        if (metricsReport.getAggregatedMetrics() != null) {
            ProcessMetricsService.AggregatedMetrics aggregated = metricsReport.getAggregatedMetrics();
            writer.println("<h2>Portfolio Overview</h2>");
            writer.println("<table>");
            writer.println("<tr><th>Metric</th><th>Value</th></tr>");
            writer.println("<tr><td>Total Artifacts</td><td>" + aggregated.totalArtifacts + "</td></tr>");
            writer.println("<tr><td>Average Complexity Score</td><td>" + String.format("%.1f", aggregated.avgComplexityScore) + "</td></tr>");
            writer.println("<tr><td>Average AI Readiness</td><td>" + String.format("%.1f%%", aggregated.avgAiReadinessScore) + "</td></tr>");
            writer.println("<tr><td>High Quality Artifacts</td><td>" + aggregated.highQualityArtifacts + "</td></tr>");
            writer.println("</table>");
        }

        // Individual Artifact Metrics
        writer.println("<h2>Individual Artifact Analysis</h2>");
        writer.println("<table>");
        writer.println("<tr><th>Artifact</th><th>Type</th><th>Complexity</th><th>AI Readiness</th><th>Quality Level</th></tr>");
        for (ProcessMetricsService.ArtifactMetrics metrics : metricsReport.getArtifactMetrics()) {
            String complexityClass = metrics.getComplexityLevel() == ProcessMetricsService.ComplexityLevel.HIGH ||
                    metrics.getComplexityLevel() == ProcessMetricsService.ComplexityLevel.VERY_HIGH ?
                    "metric-error" : "metric-good";
            String qualityClass = metrics.getQualityLevel() == ProcessMetricsService.QualityLevel.HIGH ?
                    "metric-good" :
                    (metrics.getQualityLevel() == ProcessMetricsService.QualityLevel.VERY_LOW ?
                            "metric-error" : "metric-warning");

            writer.println("<tr>");
            writer.println("<td>" + metrics.artifactName + "</td>");
            writer.println("<td>" + metrics.artifactType + "</td>");
            writer.println("<td class='" + complexityClass + "'>" + metrics.getComplexityLevel() + "</td>");
            writer.println("<td>" + String.format("%.1f%%", metrics.aiReadinessScore) + "</td>");
            writer.println("<td class='" + qualityClass + "'>" + metrics.getQualityLevel() + "</td>");
            writer.println("</tr>");
        }
        writer.println("</table>");

        writer.println("</body></html>");
    }

    // ========== SUMMARY AND LOGGING METHODS ==========

    /**
     * Logs comprehensive analysis summaries.
     */
    private static void logAnalysisSummaries(AnalysisResults results,
                                             QualityReportGeneratorService.QualityAnalysisReport qualityReport,
                                             BpmnStructureValidatorService.BpmnValidationReport validationReport,
                                             ProcessMetricsService.ProcessMetricsReport metricsReport,
                                             AnalysisLogger logger) {

        // Log navigation summary
        JsonReportNavigatorV3.NavigationAnalysisSummary navSummary = results.navigator.getAnalysisSummary();
        logger.info("SUMMARY", "Navigation Analysis: " + navSummary);

        // Log quality summary
        logger.info("SUMMARY", "Quality Analysis: " + qualityReport);
        logger.logQualityAnalysis("OVERALL", qualityReport.calculateOverallScore().toString(),
                qualityReport.getIssues().size());

        // Log validation summary
        logger.info("SUMMARY", "Validation Analysis: " + validationReport);

        // Log metrics summary
        logger.info("SUMMARY", "Metrics Analysis: " + metricsReport);

        // Log complexity analysis
        JsonReportNavigatorV3.ProcessComplexityAnalysis complexity = results.navigator.analyzeProcessComplexity();
        if (complexity.isHighlyComplex()) {
            logger.warn("SUMMARY", "HIGH COMPLEXITY DETECTED");
            logger.warn("SUMMARY", "Maximum nesting depth: " + complexity.getMaxNestingDepth());
            logger.warn("SUMMARY", "Total subprocesses: " + complexity.getTotalSubprocesses());
            logger.warn("SUMMARY", "Complex artifacts: " + complexity.getComplexArtifacts().size());
        }
    }

    /**
     * Prints success summary to console.
     */
    /**
     * CORREÇÃO SIMPLES - Prints success summary to console.
     * Esta versão funciona sem depender de métodos que podem não existir.
     */
    /**
     * VERSÃO FINAL CORRIGIDA - Prints success summary to console.
     * Compatível com Java 8 e métodos disponíveis na classe Artifact.
     */
    private static void printSuccessSummary(AnalysisConfig config, AnalysisResults results, AnalysisLogger logger) {
        System.out.println("\n=== 🎉 ANALYSIS COMPLETED SUCCESSFULLY ===");

        // Basic info
        System.out.println("📄 Main Report: " + new File(config.getOutputFilePath()).getAbsolutePath());
        System.out.println("📊 Quality Report: " + config.getOutputFilePath().replace(".json", "_quality_report.txt"));
        System.out.println("✅ Validation Report: " + config.getOutputFilePath().replace(".json", "_validation_report.txt"));
        System.out.println("📈 Metrics Report: " + config.getOutputFilePath().replace(".json", "_metrics_report.html"));

        // Quick stats - versão segura usando apenas métodos que existem
        System.out.println("\n📋 Quick Statistics:");

        // Estatísticas básicas do report
        if (results.report != null && results.report.getArtifacts() != null) {
            int artifactCount = results.report.getArtifacts().size();
            System.out.println("   • Artifacts Analyzed: " + artifactCount);

            // Contar elementos usando métodos que existem
            int totalNodes = 0;
            int totalEdges = 0;
            int totalFlowSteps = 0;
            int totalGateways = 0;

            for (JsonReportV2.Artifact artifact : results.report.getArtifacts()) {
                // Contar nós do grafo
                if (artifact.getGraph() != null && artifact.getGraph().getNodes() != null) {
                    totalNodes += artifact.getGraph().getNodes().size();
                }

                // Contar edges do grafo
                if (artifact.getGraph() != null && artifact.getGraph().getEdges() != null) {
                    totalEdges += artifact.getGraph().getEdges().size();
                }

                // Contar gateways
                if (artifact.getGraph() != null && artifact.getGraph().getGateways() != null) {
                    totalGateways += artifact.getGraph().getGateways().size();
                }

                // Contar flow steps
                if (artifact.getFlow() != null) {
                    totalFlowSteps += artifact.getFlow().size();
                }
            }

            System.out.println("   • Total Nodes: " + totalNodes);
            System.out.println("   • Total Edges: " + totalEdges);
            System.out.println("   • Total Flow Steps: " + totalFlowSteps);
            System.out.println("   • Total Gateways: " + totalGateways);
        } else {
            System.out.println("   • Artifacts Analyzed: Unknown (report not available)");
        }

        // Cache statistics
        if (results.loader != null) {
           int cacheSize = results.loader.getCacheDeArtefatos().size();
            System.out.println("   • Cached Artifacts: " + cacheSize);
            if (cacheSize > 1) {
                System.out.println("   • Dependencies Loaded: " + (cacheSize - 1));
            }
        }

        // Process complexity indicators
        if (results.report != null && results.report.getArtifacts() != null) {
            int complexArtifacts = 0;
            int maxDepth = 0;

            for (JsonReportV2.Artifact artifact : results.report.getArtifacts()) {
                // Check complexity based on flow size
                if (artifact.getFlow() != null && artifact.getFlow().size() > 20) {
                    complexArtifacts++;
                }

                // Check max nesting depth
                if (artifact.getRootView() != null) {
                    for (JsonReportV2.FlowStep step : artifact.getRootView()) {
                        if (step.getSubflowDepth() > maxDepth) {
                            maxDepth = step.getSubflowDepth();
                        }
                    }
                }
            }

            if (complexArtifacts > 0) {
                System.out.println("   • Complex Artifacts: " + complexArtifacts);
            }
            if (maxDepth > 0) {
                System.out.println("   • Maximum Nesting Depth: " + maxDepth);
            }
        }

        // Warnings summary
        if (logger.getWarnings() != null && !logger.getWarnings().isEmpty()) {
            System.out.println("\n⚠️  Warnings: " + logger.getWarnings().size());
            int displayCount = Math.min(3, logger.getWarnings().size());
            for (int i = 0; i < displayCount; i++) {
                System.out.println("   • " + logger.getWarnings().get(i));
            }
            if (logger.getWarnings().size() > 3) {
                System.out.println("   • ... and " + (logger.getWarnings().size() - 3) + " more (see detailed log)");
            }
        }

        // Errors summary
        if (logger.getErrors() != null && !logger.getErrors().isEmpty()) {
            System.out.println("\n❌ Errors: " + logger.getErrors().size());
            for (String error : logger.getErrors()) {
                System.out.println("   • " + error);
            }
        } else {
            System.out.println("\n✅ No errors detected");
        }

        System.out.println("\n✨ Analysis session completed successfully!");

        // Output files info
        System.out.println("\n📁 Generated Files:");
        File outputFile = new File(config.getOutputFilePath());
        if (outputFile.exists()) {
            System.out.println("   📄 " + outputFile.getName() + " (" + (outputFile.length() / 1024) + " KB)");
        }

        // Check for additional files
        String baseName = config.getOutputFilePath().replace(".json", "");
        File qualityFile = new File(baseName + "_quality_report.txt");
        File validationFile = new File(baseName + "_validation_report.txt");
        File metricsFile = new File(baseName + "_metrics_report.html");

        if (qualityFile.exists()) {
            System.out.println("   📊 Quality Report (" + (qualityFile.length() / 1024) + " KB)");
        }
        if (validationFile.exists()) {
            System.out.println("   ✅ Validation Report (" + (validationFile.length() / 1024) + " KB)");
        }
        if (metricsFile.exists()) {
            System.out.println("   📈 Metrics Report (" + (metricsFile.length() / 1024) + " KB)");
        }

        if (config.isDetailedLoggingEnabled()) {
            System.out.println("   📝 Detailed analysis log available");
        }

        System.out.println("\n🎯 Next Steps:");
        System.out.println("   1. Review the quality report for AI optimization recommendations");
        System.out.println("   2. Check validation report for structural issues");
        System.out.println("   3. Open metrics report in browser for executive summary");
    }
    /**
     * Convenience method with the original method signature for compatibility.
     */
    public static void executarGeracaoJsonCompleto(AnalysisConfig config) throws Exception {
        System.out.println("Starting improved JSON report generation for AI analysis...");

        executeJsonReportGeneration(config);
    }

    /**
     * Alternative configuration for different process.
     */

    /**
     * Test configuration with minimal settings.
     */
    public static void executarAnalisisTeste(AnalysisConfig config) throws Exception {


        executeJsonReportGeneration(config);
    }

    /**
     * Command-line interface for flexible execution.
     */
    public static void executeFromCommandLine(String[] args) throws Exception {
        if (args.length < 3) {
            printUsage();
            return;
        }

        AnalysisConfig.AnalysisConfigBuilder builder = AnalysisConfig.builder()
                .projectName(args[0])
                .processId(args[1])
                .extractionPath(args[2]);

        // Parse optional arguments
        for (int i = 3; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--activity=")) {
                builder.activityName(arg.substring(11));
            } else if (arg.startsWith("--output=")) {
                builder.outputFileName(arg.substring(9));
            } else if (arg.startsWith("--depth=")) {
                builder.rootViewDepth(Integer.parseInt(arg.substring(8)));
            } else if ("--verbose".equals(arg)) {
                builder.enableDetailedLogging(true);
            } else if ("--quiet".equals(arg)) {
                builder.enableDetailedLogging(false);
            }
        }

        // Set default output filename if not provided
        if (args.length < 5 || !hasOutputArg(args)) {
            builder.outputFileName(BawAnalysisConfig.generateTimestamp() + "_analysis.json");
        }

        AnalysisConfig config = builder.build();
        executeJsonReportGeneration(config);
    }

    private static boolean hasOutputArg(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--output=")) {
                return true;
            }
        }
        return false;
    }

    private static void printUsage() {
        System.out.println("Usage: ImprovedBawAnalysisMain <project_name> <process_id> <extraction_path> [options]");
        System.out.println("Options:");
        System.out.println("  --activity=<name>     Activity name for the process");
        System.out.println("  --output=<filename>   Output JSON filename");
        System.out.println("  --depth=<number>      Root view depth (default: 1)");
        System.out.println("  --verbose             Enable detailed logging");
        System.out.println("  --quiet               Disable detailed logging");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java ImprovedBawAnalysisMain \"MyProject\" \"1.abc123\" \"C:\\path\\to\\twx\"");
        System.out.println("  java ImprovedBawAnalysisMain \"MyProject\" \"1.abc123\" \"C:\\path\\to\\twx\" --verbose --depth=2");
    }




    // CORREÇÃO para as linhas 851 e 8
    // Main simplificado para testar apenas o debug
    public static void main(String[] args) {
        try {
            String basePath = "C:\\CodigoJava\\Projetos\\Gestao_de_Recondicionamentos_Caetano_Retail";
            String fullPath = basePath + "\\Gestao_de_Recondicionamentos_Caetano_Retail";

            String correctPath = null;
            if (new File(fullPath).exists() && new File(fullPath).isDirectory()) {
                correctPath = fullPath;
            } else if (new File(basePath).exists() && new File(basePath).isDirectory()) {
                correctPath = basePath;
            } else {
                System.err.println("❌ Nenhum caminho válido encontrado!");
                return;
            }

            System.out.println("✅ Usando caminho: " + correctPath);

            // Descomente se debug mostrar sucesso

        AnalysisConfig config = AnalysisConfig.builder()
                .projectName("Gestao_de_Recondicionamentos_Caetano_Retail")
                .processId("25.ff08e50c-7c7e-4e9b-93f9-74d64ae97187")
                .activityName("Recondicionamentos - Novo pedido")
                .extractionPath(correctPath)
                .outputFileName(BawAnalysisConfig.generateTimestamp() + "_processoLegado_v3.json")
                .outputDirectory("C:\\CodigoJava\\MapeadorXMLBAWJSON\\output")
                .rootViewDepth(1)
                .enableDetailedLogging(true)
                .build();

        executeJsonReportGenerationInicio(config);


        } catch (Exception e) {
            System.err.println("❌ Execution failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    /**
     * Container for analysis results to pass between methods.
     */
    private static class AnalysisResults {
        JsonReportNavigatorV3 navigator;
        JsonReportGeneratorV2 generator;
        ProcessLoaderV2Plus loader;
        JsonReportV2 report;
    }

    /**

    private static void generateDetailedReports(AnalysisConfig config, AnalysisResults results,
                                                AnalysisLogger logger) throws Exception {
        logger.milestone("DETAILED_REPORTS", "Starting detailed report generation");
        logger.startTimer("detailed_reports_total");

        try {
            // 1. Initialize all analysis services
            logger.info("REPORTS", "Initializing analysis services...");

            VariableEnricherService variableEnricher = new VariableEnricherService(results.generator, results.loader);
            QualityReportGeneratorService qualityService = new QualityReportGeneratorService(variableEnricher);
            BpmnStructureValidatorService validationService = new BpmnStructureValidatorService();
            ProcessMetricsService metricsService = new ProcessMetricsService();

            // 2. Generate Quality Analysis Report
            logger.info("REPORTS", "Generating quality analysis report...");
            logger.startTimer("quality_report");
            try {
                QualityReportGeneratorService.QualityAnalysisReport qualityReport =
                        qualityService.generateQualityReport(results.report);
                saveQualityReport(qualityReport, config, logger);
                logger.stopTimer("quality_report");
                logger.info("REPORTS", "Quality report completed");
            } catch (Exception e) {
                logger.error("REPORTS", "Error generating quality report", e);
                // Continue with other reports
            }

            // 3. Generate BPMN Structure Validation Report
            logger.info("REPORTS", "Generating BPMN structure validation...");
            logger.startTimer("validation_report");
            try {
                BpmnStructureValidatorService.BpmnValidationReport validationReport =
                        generateValidationReport(results.report, validationService, logger);
                saveValidationReport(validationReport, config, logger);
                logger.stopTimer("validation_report");
                logger.info("REPORTS", "Validation report completed");
            } catch (Exception e) {
                logger.error("REPORTS", "Error generating validation report", e);
                // Continue with other reports
            }

            // 4. Generate Process Metrics Report
            logger.info("REPORTS", "Generating process metrics report...");
            logger.startTimer("metrics_report");
            try {
                ProcessMetricsService.ProcessMetricsReport metricsReport =
                        metricsService.generateMetricsReport(results.report);
                saveMetricsReport(metricsReport, config, logger);
                logger.stopTimer("metrics_report");
                logger.info("REPORTS", "Metrics report completed");
            } catch (Exception e) {
                logger.error("REPORTS", "Error generating metrics report", e);
                // Continue with other reports
            }

            // 5. Generate Executive Summary Report
            logger.info("REPORTS", "Generating executive summary...");
            logger.startTimer("executive_summary");
            try {
                generateExecutiveSummary(config, results, logger);
                logger.stopTimer("executive_summary");
                logger.info("REPORTS", "Executive summary completed");
            } catch (Exception e) {
                logger.error("REPORTS", "Error generating executive summary", e);
            }

            // 6. Generate Technical Analysis Report
            logger.info("REPORTS", "Generating technical analysis...");
            logger.startTimer("technical_analysis");
            try {
                generateTechnicalAnalysisReport(config, results, logger);
                logger.stopTimer("technical_analysis");
                logger.info("REPORTS", "Technical analysis completed");
            } catch (Exception e) {
                logger.error("REPORTS", "Error generating technical analysis", e);
            }

            // 7. Generate AI Readiness Report
            logger.info("REPORTS", "Generating AI readiness assessment...");
            logger.startTimer("ai_readiness");
            try {
                generateAIReadinessReport(config, results, logger);
                logger.stopTimer("ai_readiness");
                logger.info("REPORTS", "AI readiness report completed");
            } catch (Exception e) {
                logger.error("REPORTS", "Error generating AI readiness report", e);
            }

            // 8. Generate Compliance & Security Report
            logger.info("REPORTS", "Generating compliance and security report...");
            logger.startTimer("compliance_security");
            try {
                generateComplianceSecurityReport(config, results, logger);
                logger.stopTimer("compliance_security");
                logger.info("REPORTS", "Compliance & security report completed");
            } catch (Exception e) {
                logger.error("REPORTS", "Error generating compliance report", e);
            }

            // 9. Generate Migration Assessment Report
            logger.info("REPORTS", "Generating migration assessment...");
            logger.startTimer("migration_assessment");
            try {
                generateMigrationAssessmentReport(config, results, logger);
                logger.stopTimer("migration_assessment");
                logger.info("REPORTS", "Migration assessment completed");
            } catch (Exception e) {
                logger.error("REPORTS", "Error generating migration assessment", e);
            }

            // 10. Generate Consolidated HTML Dashboard
            logger.info("REPORTS", "Generating consolidated HTML dashboard...");
            logger.startTimer("html_dashboard");
            try {
                generateConsolidatedDashboard(config, results, logger);
                logger.stopTimer("html_dashboard");
                logger.info("REPORTS", "HTML dashboard completed");
            } catch (Exception e) {
                logger.error("REPORTS", "Error generating HTML dashboard", e);
            }

            // 11. Log final summaries
            logDetailedReportsSummary(config, results, logger);

        } catch (Exception e) {
            logger.error("DETAILED_REPORTS", "Critical error in detailed reports generation", e);
            throw e;
        } finally {
            logger.stopTimer("detailed_reports_total");
            logger.milestone("DETAILED_REPORTS", "Detailed reports generation completed");
        }
    }

     */
    /**
     * Recupera artefato do cache - CORRIGIDO
     */
    private static Object retrieveArtifactFromCache(String artifactId) {
        try {
            // CORRIGIDO: Usar método implementado
            return getArtefatoDoCache(artifactId);
        } catch (Exception e) {
            System.err.println("⚠️ Error retrieving artifact from cache: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtém manifest principal - CORRIGIDO
     */
    private static Map<String, Object> getProcessManifest() {
        try {
            // CORRIGIDO: Usar método implementado e fazer cast seguro
            Object manifest = getMainManifest();
            if (manifest instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> manifestMap = (Map<String, Object>) manifest;
                return manifestMap;
            }
            return new HashMap<String, Object>();
        } catch (Exception e) {
            System.err.println("⚠️ Error getting main manifest: " + e.getMessage());
            return new HashMap<String, Object>();
        }
    }

    /**
     * Cache de artefatos - IMPLEMENTAÇÃO ADICIONADA
     */
    private static final Map<String, Object> ARTIFACT_CACHE = new HashMap<String, Object>();

    /**
     * Método getCacheDeArtefatos() - IMPLEMENTAÇÃO
     */
    private static Map<String, Object> getCacheDeArtefatos() {
        return ARTIFACT_CACHE;
    }

    /**
     * Método getMainManifest() - IMPLEMENTAÇÃO
     */
    private static Object getMainManifest() {
        Map<String, Object> manifest = new HashMap<String, Object>();
        manifest.put("version", "1.0.0");
        manifest.put("timestamp", System.currentTimeMillis());
        manifest.put("generator", "ImprovedBawAnalysisMain");
        manifest.put("description", "BAW Analysis Main Manifest");
        return manifest;
    }

    /**
     * Método getArtefatoDoCache() - IMPLEMENTAÇÃO
     */
    private static Object getArtefatoDoCache(String key) {
        if (key == null) {
            return null;
        }
        return getCacheDeArtefatos().get(key);
    }

    /**
     * Adiciona artefato ao cache
     */
    private static void addArtefatoAoCache(String key, Object artifact) {
        if (key != null && artifact != null) {
            getCacheDeArtefatos().put(key, artifact);
        }
    }

    /**
     * Limpa cache de artefatos
     */
    private static void clearCacheDeArtefatos() {
        getCacheDeArtefatos().clear();
    }

}