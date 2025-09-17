package br.com.danzeroum.bpmbaw.mapeadorxml;

import br.com.danzeroum.bpmbaw.mapeadorxml.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIdcoach.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.PackageObject;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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

        executarGeracaoJsonCompletoIdCoach();
       // executarGeracaoJsonCompletoV1();

    }


    /**
     * NOVO MÉTODO: Orquestra a geração do relatório JSON para IA.
     */
    private static void executarGeracaoJson() throws Exception {
        System.out.println("Iniciando geração de relatório JSON para IA...");
        String nomeProjetoAnalisar = "";

        nomeProjetoAnalisar = "Gestao_de_Recondicionamentos_Caetano_Retail";
        //nomeProjetoAnalisar = "Click2Check38";

        String CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\MapeadorXmlBAW"+ File.separator + nomeProjetoAnalisar;
        String processoId = "1.4aafa50d-c534-44e3-9108-7ec816290ef6";
        String arquivoDeSaida = PASTA_SAIDA + File.separator + "relatorio_ia_" + processoId + "_" + getTimestampAtualFormatado() + ".json";

        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoDeSaida))) {

            // FASE 1: Carregar todos os artefatos em memória
            PrintWriter logger = new PrintWriter(System.out);
            ProcessLoader_idCoach loader = new ProcessLoader_idCoach(CAMINHO_EXTRACAO_TWX, logger);
            loader.loadProcessInMemory(processoId);

            // FASE 2: Navegar pela estrutura carregada e gerar o JSON
            // (Assumindo que o nome do projeto e do arquivo .twx são fixos por enquanto)
       //     JsonReportGenerator generator = new JsonReportGenerator(writer, nomeProjetoAnalisar, nomeProjetoAnalisar+".twx");
           // JsonReportNavigator navigator = new JsonReportNavigator(generator, loader);

         //   navigator.generateReport(processoId);

            System.out.println("\nRelatório JSON gerado com sucesso!");
            System.out.println("Arquivo salvo em: " + new File(arquivoDeSaida).getAbsolutePath());
        }
    }

    /**
     * NOVO MÉTODO: Orquestra a geração do relatório JSON de Coaches e Coach Views.
     */
    private static void executarGeracaoJsonCoach() throws Exception {
        System.out.println("Iniciando geração de relatório JSON de interfaces (Coaches)...");
        String nomeProjetoAnalisar = "";

       // nomeProjetoAnalisar = "Gestao_de_Recondicionamentos_Caetano_Retail";
        nomeProjetoAnalisar = "Click2Check412";

        String CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\Projetos"+ File.separator + nomeProjetoAnalisar;

        String processoId = "1.4aafa50d-c534-44e3-9108-7ec816290ef6"; // ID do processo raiz
        String arquivoDeSaida = PASTA_SAIDA + File.separator + "relatorio_coach_ia_" + processoId + "_" + getTimestampAtualFormatado() + ".json";

        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoDeSaida))) {

            // FASE 1: Carregar todos os artefatos em memória
            PrintWriter logger = new PrintWriter(System.out);
            ProcessLoader_idCoach loader = new ProcessLoader_idCoach(CAMINHO_EXTRACAO_TWX, logger);
            loader.loadProcessInMemory(processoId);

            // FASE 2: Navegar pela estrutura de UI e gerar o JSON
       //     JsonCoachGenerator generator = new JsonCoachGenerator(writer);
       //     JsonCoachNavigator navigator = new JsonCoachNavigator(generator, loader);

         //   navigator.runAnalysis();

            System.out.println("\nRelatório JSON de interfaces gerado com sucesso!");
            System.out.println("Arquivo salvo em: " + new File(arquivoDeSaida).getAbsolutePath());
        }
    }

    /**
     * NOVO MÉTODO UNIFICADO: Orquestra a geração do relatório JSON completo (Fluxo + UI).
     */
    private static void executarGeracaoJsonCompletoIdCoach() throws Exception {
        System.out.println("Iniciando geração de relatório JSON completo para IA...");
        String nomeProjetoAnalisar = "";
        String processoId          = "";
        String nomeAtividade       = "";

      processoId = "25.ff08e50c-7c7e-4e9b-93f9-74d64ae97187"; nomeAtividade =  "Recondicionamentos - Novo pedido" ;    nomeProjetoAnalisar = "Gestao_de_Recondicionamentos_Caetano_Retail";
     //  processoId = "25.aa635726-5364-45c9-a831-04e11f92fa55"; nomeAtividade = "Click2Check";  nomeProjetoAnalisar = "Click2Check412";



        String CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\Projetos"+ File.separator + nomeProjetoAnalisar;

       // String CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\MapeadorXmlBAW"+ File.separator + nomeProjetoAnalisar;
        String arquivoDeSaida = PASTA_SAIDA + File.separator +getTimestampAtualFormatado()+ "_relatorioCompletoIa_nomeAtividade-" + nomeAtividade + "_projeto-"  +nomeProjetoAnalisar+ ".json";

        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoDeSaida))) {
            PrintWriter logger = new PrintWriter(System.out, true);
            ProcessLoader_idCoach loader = new ProcessLoader_idCoach(CAMINHO_EXTRACAO_TWX, logger);

            System.out.println("[LOG-PRINCIPAL] Fase 1: Carregando todos os artefatos relevantes...");
            List<String> tiposParaCarregar = Arrays.asList("bpd", "process", "participant");
            List<String> todosOsArtefatosIds = loader.getMainManifest().getObjects().stream()
                    .filter(obj -> tiposParaCarregar.contains(obj.getType()))
                    .map(PackageObject::getId)
                    .collect(Collectors.toList());
            loader.loadAllProcessesInMemory(todosOsArtefatosIds);

            JsonReportGenerator_idCoach generator = new JsonReportGenerator_idCoach(writer, nomeProjetoAnalisar, nomeProjetoAnalisar + ".twx");
            JsonCoachNavigator_idCoach coachNavigator = new JsonCoachNavigator_idCoach(generator, loader);
            JsonReportNavigator_idCoach reportNavigator = new JsonReportNavigator_idCoach(generator, loader, coachNavigator);
            JsonEnvironmentNavigator_idCoach envNavigator = new JsonEnvironmentNavigator_idCoach(generator, CAMINHO_EXTRACAO_TWX);
            JsonResourceBundleNavigator_idCoach rbNavigator = new JsonResourceBundleNavigator_idCoach(generator, CAMINHO_EXTRACAO_TWX);
            JsonParticipantNavigator_idCoach participantNavigator = new JsonParticipantNavigator_idCoach(generator, loader);

            System.out.println("\n[LOG-PRINCIPAL] Etapa 2: Executando o navegador de participantes...");
            participantNavigator.runAnalysis();

            System.out.println("[LOG-PRINCIPAL] Etapa 3: Executando o navegador de UI (Coaches e Coach Views)...");
            coachNavigator.runAnalysis();

            System.out.println("[LOG-PRINCIPAL] Etapa 4: Executando o navegador de fluxo de relatório...");
            reportNavigator.populateReport(processoId);

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

        processoId = "25.ff08e50c-7c7e-4e9b-93f9-74d64ae97187"; nomeAtividade =  "Recondicionamentos - Novo pedido" ;    nomeProjetoAnalisar = "Gestao_de_Recondicionamentos_Caetano_Retail"; CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\Projetos"+ File.separator + nomeProjetoAnalisar; saida = getTimestampAtualFormatado() +"processoLegado.json";
        //processoId = "25.acb58aeb-77bd-432b-93c4-e9dd1cb80991"; nomeAtividade = "Processo Pedido Recondicionamento";  nomeProjetoAnalisar = "Click2Check412";CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\Projetos"+ File.separator + nomeProjetoAnalisar; saida = getTimestampAtualFormatado() +"processoRefatoradoUiReport.json";

        //String arquivoDeSaida = PASTA_SAIDA + File.separator +getTimestampAtualFormatado()+ "_relatorioIa_nomeAtividade-" + nomeAtividade + "_projeto-"  +nomeProjetoAnalisar+ ".json";
        String arquivoDeSaida = PASTA_SAIDA + File.separator +saida;

        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoDeSaida))) {
// Etapa 2: Popula os artefatos que consomem as definições
            System.out.println("[LOG-PRINCIPAL] Etapa 1: Executando o navegador de fluxo de relatório...");
            PrintWriter logger = new PrintWriter(System.out);
            ProcessLoader loader = new ProcessLoader(CAMINHO_EXTRACAO_TWX, logger);
            loader.loadProcessInMemory(processoId);
            JsonReportGeneratorV2 generator = new JsonReportGeneratorV2(writer, nomeProjetoAnalisar, nomeProjetoAnalisar + ".twx");
            JsonReportNavigatorV2 reportNavigator = new JsonReportNavigatorV2(generator, loader);
            reportNavigator.populateReport(processoId);
            generator.generate();


            System.out.println("\nRelatório JSON completo gerado com sucesso!");
            System.out.println("Arquivo salvo em: " + new File(arquivoDeSaida).getAbsolutePath());
        }

    }
    /* ===========================================================
          RELATÓRIO 1: Coaches / Coach Views (HTML direto)
          =========================================================== */
    private static void executarAnaliseDeCoachesHtml() throws Exception {
        System.out.println("Iniciando análise focada em Coaches e Coach Views (HTML)...");
        String nomeProjetoAnalisar  = "";
        String processoId           = "";
        String nomeAtividade        = "";
        String CAMINHO_EXTRACAO_TWX = "";
        String saida                = "";

        processoId = "25.ff08e50c-7c7e-4e9b-93f9-74d64ae97187"; nomeAtividade =  "Recondicionamentos - Novo pedido" ;    nomeProjetoAnalisar = "Gestao_de_Recondicionamentos_Caetano_Retail"; CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\Projetos"+ File.separator + nomeProjetoAnalisar; saida = getTimestampAtualFormatado() +"processoLegado.json";
      //  processoId = "25.acb58aeb-77bd-432b-93c4-e9dd1cb80991"; nomeAtividade = "Processo Pedido Recondicionamento";  nomeProjetoAnalisar = "Click2Check412";CAMINHO_EXTRACAO_TWX = "C:\\CodigoJava\\Projetos"+ File.separator + nomeProjetoAnalisar; saida = getTimestampAtualFormatado() +"processoRefatoradoUiReport.json";

        //String arquivoDeSaida = PASTA_SAIDA + File.separator +getTimestampAtualFormatado()+ "_relatorioIa_nomeAtividade-" + nomeAtividade + "_projeto-"  +nomeProjetoAnalisar+ ".json";
        String arquivoDeSaida = PASTA_SAIDA + File.separator +saida;
        // 1) Loader e cache
        ProcessLoader_idCoach loader = new ProcessLoader_idCoach(CAMINHO_EXTRACAO_TWX, new PrintWriter(System.out));
        loader.loadProcessInMemory(processoId);
        System.out.println("Carregamento concluído. " + loader.getCacheDeArtefatos().size() + " artefatos em cache.");

        // 2) Arquivo de saída
        String arquivoSaida = PASTA_SAIDA + File.separator + ts() + "_relatorio_coaches_" + processoId + ".html";
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoSaida))) {

            // 3) Printer HTML e Navigator específicos para Coaches
            CoachReportPrinterHtml_idCoach printer = new CoachReportPrinterHtml_idCoach(writer, loader, NOMES_PADRAO);
            printer.beginHtmlDocument("Análise de Interfaces do Processo e Componentes", processoId);

            CoachNavigatorHtml_idCoach navigator = new CoachNavigatorHtml_idCoach(printer, loader, NOMES_PADRAO);
            navigator.runAnalysis(processoId);

            printer.finishHtmlDocument();
        }

        System.out.println("Análise de Coaches concluída com sucesso!");
        System.out.println("Relatório salvo em: " + new File(arquivoSaida).getAbsolutePath());
    }



    /* ===========================================================
       Utils
       =========================================================== */

    private static String ts() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

}

