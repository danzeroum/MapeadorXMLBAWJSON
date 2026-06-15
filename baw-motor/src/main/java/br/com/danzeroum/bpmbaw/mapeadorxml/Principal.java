package br.com.danzeroum.bpmbaw.mapeadorxml;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.AnalysisConfig;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.EnhancedStructuredProcessReportV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.transformers.V2toV2PlusTransformer;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services.EnvironmentVariableService;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services.ResourceBundleService;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonEnvironmentNavigator;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.PackageObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.danzeroum.bpmbaw.mapeadorxml.util.FormatadorDeDataUtil.getTimestampAtualFormatado;

public class Principal {

    // --- CONFIGURE SEUS CAMINHOS AQUI ---
   // private static final String CAMINHO_EXTRACAO_TWX = "C:\\SeuCaminho\\SeuProjeto";
    private static final String CAMINHO_EXTRACAO_TWX = "C:\\SeuCaminho\\SeuProjeto";
    private static final String PASTA_SAIDA = "saida";
    // Conjunto de nomes de Coach Views padrão (serão tratados como “de sistema”)
    private static final Set<String> NOMES_PADRAO = new HashSet<>(Arrays.asList(
            "Image", "Text Area Minimum Size", "Input Decimal", "Button",
            "Text Area", "Text", "Select", "Section", "Date Time Picker",
            "View Responsive CSS", "Vertical Section", "Stack Container",
            "Combo Box", "Tabs", "Input String", "Input Integer",
            "Checkbox", "Table", "Date Picker", "Data Table",
            "Responsive Row", "Output Text", "Check Box", "Horizontal Line",
            "Responsive Column", "Decimal", "Integer", "Horizontal Section", "CSS",
            "Input Group", "Line", "Tab Section", "Modal Section", "Data",
            "Horizontal Layout", "Vertical Layout", "Panel", "Single Select Chosen",
            "Modal Alert"
    ));
    /**
     * Ponto de entrada principal da aplicação.
     * Uso:
     * java Main mapear <nome_do_arquivo.xml>
     * java Main extrair <nome_do_arquivo.xml>
     */
    public static void main(String[] args) throws Exception {
        executarGeracaoJsonCompleto();
        executarGeracaoJsonV2Plus();

    }

    private static void executarGeracaoJsonV2Plus() throws Exception {
        System.out.println("Iniciando geração de relatório JSON no formato V2Plus...");

        String nomeProjetoAnalisar  = "";
        String processoId           = "";
        String nomeAtividade        = "";
        String CAMINHO_EXTRACAO_TWX = "";
        String saida                = "";

        processoId = "<SEU-PROCESS-ID>"; nomeAtividade = "<NOME-DO-PROCESSO>"; nomeProjetoAnalisar = "MeuProcessoBPM"; CAMINHO_EXTRACAO_TWX = "C:\\SeuCaminho\\Projetos"+ File.separator + nomeProjetoAnalisar; saida = getTimestampAtualFormatado() +"processoLegado_V2plus.json";
       // processoId = "<OUTRO-PROCESS-ID>"; nomeAtividade = "<OUTRO-PROCESSO>"; nomeProjetoAnalisar = "OutroProjeto"; CAMINHO_EXTRACAO_TWX = "C:\\SeuCaminho\\Projetos"+ File.separator + nomeProjetoAnalisar; saida = getTimestampAtualFormatado() +"processoRefatorado_V2plus.json";

        //String arquivoDeSaida = PASTA_SAIDA + File.separator +getTimestampAtualFormatado()+ "_relatorioIa_nomeAtividade-" + nomeAtividade + "_projeto-"  +nomeProjetoAnalisar+ ".json";
        String arquivoDeSaida = PASTA_SAIDA + File.separator +saida;

        // --- ETAPA 1: Executar a análise V2 para obter os dados brutos em memória ---
        System.out.println("[ETAPA 1/4] Executando análise V2 para extração de dados...");
        JsonReportV2 reportV2 = extrairDadosComAnalisadorV2(CAMINHO_EXTRACAO_TWX, processoId, nomeProjetoAnalisar);
        System.out.println("[SUCESSO] Análise V2 concluída. " + reportV2.getArtifacts().size() + " artefatos processados.");

        // --- ETAPA 2: Transformar o resultado da V2 para o modelo V2Plus ---
        System.out.println("\n[ETAPA 2/4] Transformando dados do modelo V2 para V2Plus...");
        V2toV2PlusTransformer transformer = new V2toV2PlusTransformer();
        EnhancedStructuredProcessReportV2 reportV2Plus = transformer.transform(reportV2, processoId, nomeProjetoAnalisar);
        System.out.println("[SUCESSO] Transformação para o modelo V2Plus concluída.");

        // --- ETAPA 3: (Opcional) Enriquecer o modelo V2Plus com a Facade ---
        // A facade pode adicionar metadados, templates de governança, etc.
        System.out.println("\n[ETAPA 3/4] Enriquecendo o relatório V2Plus com metadados e templates...");
        AnalysisConfig config = new AnalysisConfig(nomeProjetoAnalisar, processoId, CAMINHO_EXTRACAO_TWX, PASTA_SAIDA, saida);
        // A facade V2Plus pode ser usada aqui para enriquecer o relatório já transformado
        // EnhancedBawAnalysisFacadeV2Plus.enrichReport(reportV2Plus, config); // Exemplo de chamada
        System.out.println("[SUCESSO] Relatório V2Plus enriquecido.");

        // --- ETAPA 4: Serializar e Salvar o Relatório V2Plus Final ---
        System.out.println("\n[ETAPA 4/4] Serializando e salvando o relatório V2Plus final...");
        salvarRelatorioV2Plus(reportV2Plus, arquivoDeSaida);

        System.out.println("\nRelatório JSON V2Plus completo gerado com sucesso!");
        System.out.println("Arquivo salvo em: " + new File(arquivoDeSaida).getAbsolutePath());
    }

    /**
     * Encapsula a lógica de execução da análise V2 para retornar o objeto de relatório.
     */
    private static JsonReportV2 extrairDadosComAnalisadorV2(String caminhoExtracao, String processoId, String nomeProjeto) throws Exception {
        // Usamos um writer em memória, pois só queremos o objeto final, não o arquivo V2.
        StringWriter stringWriter = new StringWriter();
        PrintWriter memoryWriter = new PrintWriter(stringWriter);

        ProcessLoaderV2Plus loader = new ProcessLoaderV2Plus(caminhoExtracao, new PrintWriter(System.out, true));
        loader.loadProcessInMemory(processoId);

        JsonReportGeneratorV2 generator = new JsonReportGeneratorV2(memoryWriter, nomeProjeto, nomeProjeto + ".twx");
        JsonReportNavigatorV2 reportNavigator = new JsonReportNavigatorV2(generator, loader);
        JsonCoachNavigatorV2 coachNavigator = new JsonCoachNavigatorV2(generator, loader);

        reportNavigator.populateReport(processoId);
        coachNavigator.runAnalysis();

        // Serviços adicionais da V2
        EnvironmentVariableService envService = new EnvironmentVariableService(caminhoExtracao);
        generator.getReport().setEnvironmentVariablesUsed(envService.findAndResolveUsedVariables(generator.getReport()));

        ResourceBundleService rbService = new ResourceBundleService(caminhoExtracao);
        generator.getReport().setResourceBundlesUsed(rbService.findAndResolveUsedVariables(generator.getReport()));

        return generator.getReport();
    }

    /**
     * Salva o objeto de relatório V2Plus em um arquivo JSON.
     */
    private static void salvarRelatorioV2Plus(EnhancedStructuredProcessReportV2 report, String caminhoArquivo) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            mapper.writeValue(writer, report);
        }
    }


    private static void executarGeracaoJsonCompleto() throws Exception {
        System.out.println("Iniciando geração de relatório JSON completo para IA V2...");
        String nomeProjetoAnalisar  = "";
        String processoId           = "";
        String nomeAtividade        = "";
        String CAMINHO_EXTRACAO_TWX = "";
        String saida                = "";

        processoId = "<SEU-PROCESS-ID>"; nomeAtividade = "<NOME-DO-PROCESSO>"; nomeProjetoAnalisar = "MeuProcessoBPM"; CAMINHO_EXTRACAO_TWX = "C:\\SeuCaminho\\Projetos"+ File.separator + nomeProjetoAnalisar; saida = getTimestampAtualFormatado() +"processoLegado_V2.json";
        //processoId = "<OUTRO-PROCESS-ID>"; nomeAtividade = "<OUTRO-PROCESSO>"; nomeProjetoAnalisar = "OutroProjeto"; CAMINHO_EXTRACAO_TWX = "C:\\SeuCaminho\\Projetos"+ File.separator + nomeProjetoAnalisar; saida = getTimestampAtualFormatado() +"processoRefatorado_V2.json";

        //String arquivoDeSaida = PASTA_SAIDA + File.separator +getTimestampAtualFormatado()+ "_relatorioIa_nomeAtividade-" + nomeAtividade + "_projeto-"  +nomeProjetoAnalisar+ ".json";
        String arquivoDeSaida = PASTA_SAIDA + File.separator +saida;

        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoDeSaida))) {
            PrintWriter logger         = new PrintWriter(System.out, true);
            ProcessLoaderV2Plus loader = new ProcessLoaderV2Plus(CAMINHO_EXTRACAO_TWX, logger);
            loader.loadProcessInMemory(processoId);

            // Instancia o gerador V2
            JsonReportGeneratorV2 generator       = new JsonReportGeneratorV2(writer, nomeProjetoAnalisar, nomeProjetoAnalisar + ".twx");
            JsonReportNavigatorV2 reportNavigator = new JsonReportNavigatorV2(generator, loader);
            JsonCoachNavigatorV2 coachNavigator   = new JsonCoachNavigatorV2(generator, loader); // Essencial para o Relatório de UI

            // --- ORDEM DE EXECUÇÃO DOS NAVEGADORES ---

            // Etapa 1: Popula o relatório de fluxo do processo
            System.out.println("[LOG-PRINCIPAL] Etapa 1: Executando o navegador de fluxo de relatório...");
            reportNavigator.populateReport(processoId);

            // Etapa 2: Analisa a UI (Coaches)
            System.out.println("[LOG-PRINCIPAL] Etapa 2: Executando o navegador de UI (Coaches)...");
            coachNavigator.runAnalysis();

            // Etapa 3: Variaveis de Ambiente (Environment Variable) ---
            System.out.println("[LOG-PRINCIPAL] Etapa 3: Analisando uso de variáveis de ambiente...");
            EnvironmentVariableService envService                = new EnvironmentVariableService(CAMINHO_EXTRACAO_TWX);
            List<JsonReportV2.EnvironmentVariableUsage> usedVars = envService.findAndResolveUsedVariables(generator.getReport());
            generator.getReport().setEnvironmentVariablesUsed(usedVars);

            // Etapa 4: Recursos de Localização (Resource Bundles) ---
            System.out.println("[LOG-PRINCIPAL] Etapa 4: Analisando uso de recursos de localização...");
            ResourceBundleService rbService                = new ResourceBundleService(CAMINHO_EXTRACAO_TWX);
            List<JsonReportV2.ResourceBundleUsage> usedRbs = rbService.findAndResolveUsedVariables(generator.getReport());
            generator.getReport().setResourceBundlesUsed(usedRbs);


            // Etapa 5: Gera o arquivo JSON final
            System.out.println("[LOG-PRINCIPAL] Etapa 5: Gerando o arquivo JSON final...");
            generator.generate();

            System.out.println("\nRelatório JSON completo gerado com sucesso!");
            System.out.println("Arquivo salvo em: " + new File(arquivoDeSaida).getAbsolutePath());
        }
    }




    /* ===========================================================
       Utils
       =========================================================== */

    private static String getTimestampAtualFormatado() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

}

