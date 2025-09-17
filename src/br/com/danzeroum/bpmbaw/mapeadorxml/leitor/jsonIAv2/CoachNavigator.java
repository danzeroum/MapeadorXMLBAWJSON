package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;



import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.FormTask;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.GlobalUserTask;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.ContentBoxContrib;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.TWComponent;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Navegador especialista que analisa a estrutura de Coaches, tanto em processos legados
 * quanto em processos BPMN modernos, e recolhe os IDs de todas as Coach Views utilizadas. (Versão Final Corrigida)
 */
public class CoachNavigator {

    private final CoachReportPrinter printer;
    private final ProcessLoader loader;
    private JAXBContext coachLayoutContext;
    private final Set<String> discoveredCoachViewIds = new HashSet<>();

    public CoachNavigator(CoachReportPrinter printer, ProcessLoader loader) {
        this.printer = printer;
        this.loader = loader;
        try {
            this.coachLayoutContext = JAXBContext.newInstance(CoachLayout.class);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar JAXBContext para CoachLayout", e);
        }
    }

    public void runAnalysis(String artifactId) {
        System.out.println("[LOG] Fase 1: Analisando o processo para descobrir Coach Views...");
        visitArtifactToDiscoverCoachViews(artifactId);

        System.out.println("[LOG] Fase 2: Imprimindo relatório principal e detalhes das " + discoveredCoachViewIds.size() + " Coach Views encontradas.");
        imprimirRelatorioCompleto(artifactId);
    }

    private void imprimirRelatorioCompleto(String artifactId) {
        Object artifact = loader.getArtefatoDoCache(artifactId);
        if (artifact == null) return;

        if (artifact instanceof Teamworks) {
            Teamworks tw = (Teamworks) artifact;
            if (tw.getProcess() != null) {
                // Imprime Coaches de Serviços Legados (baseados em <item>)
                if (tw.getProcess().getItems() != null) {
                    tw.getProcess().getItems().forEach(item -> imprimirDetalhesDoItem(tw.getProcess().getName(), item));
                }
                // Imprime Coaches de Serviços Modernos (baseados em <coachflow>)
                if (tw.getProcess().getCoachflow() != null && tw.getProcess().getCoachflow().getDefinitions() != null) {
                    imprimirDetalhesBpmn(tw.getProcess().getName(), tw.getProcess().getCoachflow().getDefinitions());
                }
            }
        }

        printer.imprimirDetalhesDeTodasAsCoachViews(loader, discoveredCoachViewIds);
    }

    private void imprimirDetalhesDoItem(String serviceName, Item item) {
        if ("CoachNG".equalsIgnoreCase(item.getTWComponentName())) {
            TWComponent component = item.getTwComponent();
            if (component != null && component.getLayoutData() != null && !component.getLayoutData().isEmpty()) {
                try {
                    printer.imprimirScriptsPreExecucao(item, "");
                    CoachLayout layout = parseLayoutData(component.getLayoutData());
                    printer.imprimirLayoutCoach(layout, serviceName, item.getName(), "", discoveredCoachViewIds);
                    printer.imprimirEventosDeBoundary(item, "");
                } catch (Exception e) {
                    System.err.println("  [ERRO DE PARSING] Falha ao ler a estrutura do Coach '" + item.getName() + "'. O XML do layout pode estar mal formado. Causa: " + e.getMessage());
                }
            }
        }
    }

    /**
     * CORRIGIDO: Este método agora navega pela implementação do fluxo BPMN para encontrar a FormTask
     * que contém a definição do Coach.
     */
    private void imprimirDetalhesBpmn(String processName, Definitions definitions) {
        GlobalUserTask userTask = definitions.getGlobalUserTask();
        if (userTask != null && userTask.getImplementation() != null && userTask.getImplementation().getFlowElements() != null) {
            // Itera nos elementos do fluxo para encontrar a tarefa de formulário (Coach)
            for (Object flowElement : userTask.getImplementation().getFlowElements()) {
                if (flowElement instanceof FormTask) {
                    FormTask formTask = (FormTask) flowElement;
                    if (formTask.getFormDefinition() != null &&
                            formTask.getFormDefinition().getCoachDefinition() != null &&
                            formTask.getFormDefinition().getCoachDefinition().getLayout() != null) {

                        // Chama o método de impressão correto com o layout moderno
                        printer.imprimirLayoutCoach(
                                formTask.getFormDefinition().getCoachDefinition().getLayout(),
                                processName,
                                formTask.getName(), // Usa o nome do FormTask como nome do Coach
                                "",
                                discoveredCoachViewIds
                        );
                    }
                }
            }
        }
    }


    /**
     * CORRIGIDO: Este método agora navega corretamente pela estrutura de um <coachflow>
     * para encontrar e processar as Coach Views de um serviço moderno.
     */
    public void visitArtifactToDiscoverCoachViews(String artifactId) {
        Object artifact = loader.getArtefatoDoCache(artifactId);
        if (artifact == null) return;

        if (artifact instanceof Teamworks) {
            Teamworks tw = (Teamworks) artifact;
            if (tw.getProcess() != null) {
                // Lógica para Coaches em Serviços Legados (permanece a mesma)
                if (tw.getProcess().getItems() != null) {
                    tw.getProcess().getItems().forEach(this::discoverCoachViewsInLegacyItem);
                }
                // Lógica CORRIGIDA para Coaches em Serviços Modernos (dentro de <coachflow>)
                if (tw.getProcess().getCoachflow() != null && tw.getProcess().getCoachflow().getDefinitions() != null) {
                    GlobalUserTask userTask = tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask();
                    if (userTask != null && userTask.getImplementation() != null && userTask.getImplementation().getFlowElements() != null) {
                        for (Object flowElement : userTask.getImplementation().getFlowElements()) {
                            if (flowElement instanceof FormTask) {
                                FormTask formTask = (FormTask) flowElement;
                                if (formTask.getFormDefinition() != null &&
                                        formTask.getFormDefinition().getCoachDefinition() != null &&
                                        formTask.getFormDefinition().getCoachDefinition().getLayout() != null &&
                                        formTask.getFormDefinition().getCoachDefinition().getLayout().getLayoutItems() != null) {

                                    for (LayoutItem item : formTask.getFormDefinition().getCoachDefinition().getLayout().getLayoutItems()) {
                                        collectCoachViewIdsFromModernLayoutItem(item);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void discoverCoachViewsInLegacyItem(Item item) {
        if ("CoachNG".equalsIgnoreCase(item.getTWComponentName())) {
            TWComponent component = item.getTwComponent();
            if (component != null && component.getLayoutData() != null && !component.getLayoutData().isEmpty()) {
                try {
                    CoachLayout layout = parseLayoutData(component.getLayoutData());
                    if (layout != null && layout.getItems() != null) {
                        layout.getItems().forEach(this::collectCoachViewIdsFromLegacyLayoutItem);
                    }
                } catch (Exception e) {
                    // Ignora erros de parsing nesta fase
                }
            }
        }
    }

    private void collectCoachViewIdsFromLegacyLayoutItem(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item) {
        if (item == null) return;
        if (item.getViewUUID() != null) {
            loader.loadArtefatoSeNaoExistir(item.getViewUUID());
            discoveredCoachViewIds.add(item.getViewUUID());
        }
        if (item.getContentBoxContributions() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem.ContentBoxContribution contrib : item.getContentBoxContributions()) {
                if (contrib.getContributions() != null) {
                    contrib.getContributions().forEach(this::collectCoachViewIdsFromLegacyLayoutItem);
                }
            }
        }
    }

    /**
     * NOVO MÉTODO: Navega recursivamente na estrutura de um Coach moderno para coletar os IDs das Coach Views.
     */
    private void collectCoachViewIdsFromModernLayoutItem(LayoutItem item) {
        if (item == null) return;
        if (item.getViewUUID() != null && !item.getViewUUID().isEmpty()) {
            loader.loadArtefatoSeNaoExistir(item.getViewUUID());
            discoveredCoachViewIds.add(item.getViewUUID());
        }
        if (item.getContentBoxContribs() != null) {
            for (ContentBoxContrib contrib : item.getContentBoxContribs()) {
                if (contrib.getContributions() != null) {
                    contrib.getContributions().forEach(this::collectCoachViewIdsFromModernLayoutItem);
                }
            }
        }
    }

    private CoachLayout parseLayoutData(String layoutXml) throws Exception {
        if (layoutXml == null || layoutXml.trim().isEmpty()) return null;
        String unescapedXml = layoutXml.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");
        Unmarshaller unmarshaller = this.coachLayoutContext.createUnmarshaller();
        return (CoachLayout) unmarshaller.unmarshal(new StringReader(unescapedXml));
    }
}