package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.fixes;

/**
 * Correção para o construtor do ProcessLoader
 */
public class ProcessLoaderFix {

    /**
     * USAR ESTA ABORDAGEM NO EnhancedBawAnalysisFacadeV2Plus:
     */
    public static Object createProcessLoaderSafely(String extractionPath) {
        try {
            // Tentar construtor com parâmetro
            return new br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoader(extractionPath);
        } catch (Exception e1) {
            try {
                // Tentar construtor sem parâmetro
                return new br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoader();
            } catch (Exception e2) {
                System.err.println("⚠️ Could not create ProcessLoader: " + e2.getMessage());
                return null;
            }
        }
    }
}
