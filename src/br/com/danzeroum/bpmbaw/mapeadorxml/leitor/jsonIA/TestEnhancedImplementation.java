package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.facade.EnhancedBawAnalysisFacade;

// TestEnhancedImplementation.java
public class TestEnhancedImplementation {

    public static void main(String[] args) {
        testBasicFunctionality();
        testEnhancedFeatures();
        testErrorHandling();
    }

    private static void testBasicFunctionality() {
        System.out.println("🧪 Testing basic functionality...");

        try {
            AnalysisConfig config = AnalysisConfig.builder()
                    .projectName("Test_Basic")
                    .processId("25.ff08e50c-7c7e-4e9b-93f9-74d64ae97187")
                    .activityName("Test Process")
                    .extractionPath("C:\\CodigoJava\\Projetos\\Gestao_de_Recondicionamentos_Caetano_Retail")
                    .outputFileName("test_basic_enhanced.json")
                    .enableDetailedLogging(true)
                    .build();

            ImprovedBawAnalysisMain.executeJsonReportGeneration(config);
            System.out.println("✅ Basic functionality test passed");

        } catch (Exception e) {
            System.err.println("❌ Basic functionality test failed: " + e.getMessage());
        }
    }

    private static void testEnhancedFeatures() {
        System.out.println("\n🧪 Testing enhanced features...");

        try {
            EnhancedBawAnalysisFacade facade = new EnhancedBawAnalysisFacade();

            AnalysisConfig config = AnalysisConfig.builder()
                    .projectName("Test_Enhanced")
                    .processId("25.ff08e50c-7c7e-4e9b-93f9-74d64ae97187")
                    .activityName("Enhanced Test")
                    .extractionPath("C:\\CodigoJava\\Projetos\\Gestao_de_Recondicionamentos_Caetano_Retail")
                    .outputFileName("test_enhanced_features.json")
                    .enableDetailedLogging(true)
                    .build();

            facade.executeEnhancedAnalysis(config);
            System.out.println("✅ Enhanced features test passed");

        } catch (Exception e) {
            System.err.println("❌ Enhanced features test failed: " + e.getMessage());
        }
    }

    private static void testErrorHandling() {
        System.out.println("\n🧪 Testing error handling...");

        try {
            AnalysisConfig invalidConfig = AnalysisConfig.builder()
                    .projectName("Invalid_Test")
                    .processId("invalid.process.id")
                    .activityName("Invalid Test")
                    .extractionPath("C:\\invalid\\path")
                    .outputFileName("test_error.json")
                    .build();

            ImprovedBawAnalysisMain.executeJsonReportGeneration(invalidConfig);
            System.out.println("❌ Error handling test should have failed");

        } catch (Exception e) {
            System.out.println("✅ Error handling test passed (expected error: " + e.getClass().getSimpleName() + ")");
        }
    }
}