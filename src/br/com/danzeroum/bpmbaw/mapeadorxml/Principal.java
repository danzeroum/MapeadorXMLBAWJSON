package br.com.danzeroum.bpmbaw.mapeadorxml;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services.EnvironmentVariableService;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services.ResourceBundleService;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonEnvironmentNavigator;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.PackageObject;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.danzeroum.bpmbaw.mapeadorxml.util.FormatadorDeDataUtil.getTimestampAtualFormatado;

public class Principal {

    // --- CONFIGURE SEUS CAMINHOS AQUI ---
   // private static final String CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\MapeadorXmlBAW\\Gestao_de_Recondicionamentos_Caetano_Retail";
    private static final String CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\MapeadorXmlBAW\\Click2Check38";
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

       // executarGeracaoJsonCompletoV1();
        executarGeracaoJsonCompleto();

    }




    private static void executarGeracaoJsonCompletoV1() throws Exception {
        System.out.println("Iniciando geração de relatório JSON completo para IA...");
        String nomeProjetoAnalisar  = "";
        String processoId           = "";
        String nomeAtividade        = "";
        String CAMINHO_EXTRACAO_TWX = "";
        String saida                = "";

       processoId = "25.ff08e50c-7c7e-4e9b-93f9-74d64ae97187"; nomeAtividade =  "Recondicionamentos - Novo pedido" ;    nomeProjetoAnalisar = "Gestao_de_Recondicionamentos_Caetano_Retail"; CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\Projetos"+ File.separator + nomeProjetoAnalisar; saida = "processoLegadoUiReport.json";
       // processoId = "25.acb58aeb-77bd-432b-93c4-e9dd1cb80991"; nomeAtividade = "Processo Pedido Recondicionamento";  nomeProjetoAnalisar = "Click2Check412";CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\Projetos"+ File.separator + nomeProjetoAnalisar; saida = "processoRefatoradoUiReport.json";

        //String arquivoDeSaida = PASTA_SAIDA + File.separator +getTimestampAtualFormatado()+ "_relatorioIa_nomeAtividade-" + nomeAtividade + "_projeto-"  +nomeProjetoAnalisar+ ".json";
        String arquivoDeSaida = PASTA_SAIDA + File.separator +saida;

        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoDeSaida))) {

            PrintWriter logger = new PrintWriter(System.out);
            ProcessLoader loader = new ProcessLoader(CAMINHO_EXTRACAO_TWX, logger);
            loader.loadProcessInMemory(processoId);

            JsonReportGenerator generator = new JsonReportGenerator(writer, nomeProjetoAnalisar, nomeProjetoAnalisar + ".twx");
            JsonReportNavigator reportNavigator = new JsonReportNavigator(generator, loader);
            JsonCoachNavigator coachNavigator = new JsonCoachNavigator(generator, loader);
            JsonEnvironmentNavigator envNavigator = new JsonEnvironmentNavigator(generator, CAMINHO_EXTRACAO_TWX);
            JsonResourceBundleNavigator rbNavigator = new JsonResourceBundleNavigator(generator, CAMINHO_EXTRACAO_TWX);
            JsonParticipantNavigator participantNavigator = new JsonParticipantNavigator(generator, loader);

            // --- ORDEM DE EXECUÇÃO DOS NAVEGADORES ---

            // Etapa 1: Popula as definições centrais (participantes)
            System.out.println("\n[LOG-PRINCIPAL] Etapa 2: Executando o navegador de participantes...");
            participantNavigator.runAnalysis();

            // Etapa 2: Popula os artefatos que consomem as definições
            System.out.println("[LOG-PRINCIPAL] Etapa 3: Executando o navegador de fluxo de relatório...");
            reportNavigator.populateReport(processoId);

            // ... (resto das chamadas: coachNavigator, envNavigator, rbNavigator, e generator.generate()) ...

            System.out.println("[LOG-PRINCIPAL] Etapa 4: Executando o navegador de UI (Coaches)...");
            coachNavigator.runAnalysis();

            System.out.println("[LOG-PRINCIPAL] Etapa 5: Executando o navegador de Variáveis de Ambiente...");
            envNavigator.runAnalysis();

            System.out.println("[LOG-PRINCIPAL] Etapa 6: Executando o navegador de Resource Bundles...");
            rbNavigator.runAnalysis();

            System.out.println("[LOG-PRINCIPAL] Etapa 7: Gerando o arquivo JSON final...");
            generator.generate();


            System.out.println("\nRelatório JSON completo gerado com sucesso!");
            System.out.println("Arquivo salvo em: " + new File(arquivoDeSaida).getAbsolutePath());
        }

    }
    private static void executarGeracaoJsonCompleto() throws Exception {
        System.out.println("Iniciando geração de relatório JSON completo para IA...");
        String nomeProjetoAnalisar  = "";
        String processoId           = "";
        String nomeAtividade        = "";
        String CAMINHO_EXTRACAO_TWX = "";
        String saida                = "";

        //processoId = "25.ff08e50c-7c7e-4e9b-93f9-74d64ae97187"; nomeAtividade =  "Recondicionamentos - Novo pedido" ;    nomeProjetoAnalisar = "Gestao_de_Recondicionamentos_Caetano_Retail"; CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\Projetos"+ File.separator + nomeProjetoAnalisar; saida = getTimestampAtualFormatado() +"processoLegado.json";
        processoId = "25.acb58aeb-77bd-432b-93c4-e9dd1cb80991"; nomeAtividade = "Processo Pedido Recondicionamento";  nomeProjetoAnalisar = "Click2Check412";CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\Projetos"+ File.separator + nomeProjetoAnalisar; saida = getTimestampAtualFormatado() +"processoRefatoradoUiReport.json";

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

