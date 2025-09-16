// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/jsonIA/enhanced/extractors/TWXToV2PlusUIExtractor.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessUIV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.CoachDefinition;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.Layout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.TWComponent;
// Importação corrigida para a classe CoachEventBinding
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.CoachEventBinding;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Extrator especializado para extrair e transformar dados de Coaches (Interfaces de Usuário)
 * para o formato V2Plus. Versão final com extração de eventos corrigida e alinhada ao modelo.
 *
 * @version 1.7.0
 */
public class TWXToV2PlusUIExtractor {

    private final ProcessLoaderV2Plus loader;
    private JAXBContext coachLayoutContext;
    private final Set<String> processedArtifactsForUi;

    public TWXToV2PlusUIExtractor(ProcessLoaderV2Plus loader) {
        if (loader == null) {
            throw new IllegalArgumentException("ProcessLoaderV2Plus cannot be null.");
        }
        this.loader = loader;
        this.processedArtifactsForUi = new HashSet<>();
        try {
            this.coachLayoutContext = JAXBContext.newInstance(CoachLayout.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JAXBContext for CoachLayout", e);
        }
    }

    public ProcessUIV2Plus extractUI(Object artifact) {
        ProcessUIV2Plus ui = new ProcessUIV2Plus();
        String artifactId = getArtifactId(artifact);
        String artifactName = getArtifactName(artifact);

        ui.setId("ui:" + artifactId);
        ui.setName("UI for " + artifactName);
        ui.setDescription("User Interface components and layouts for the process.");

        if (processedArtifactsForUi.contains(artifactId)) {
            return ui;
        }
        processedArtifactsForUi.add(artifactId);

        //System.out.println("\n[LOG-UI] >>> Iniciando extração de UI para o artefato: " + artifactName + " (ID: " + artifactId + ")");

        if (artifact instanceof Teamworks) {
            processTeamworksArtifact((Teamworks) artifact, ui);
        } else if (artifact instanceof Definitions) {
            processDefinitionsArtifact((Definitions) artifact, ui);
        }

        addDefaultTheme(ui);
        //System.out.println("[LOG-UI] <<< Extração de UI finalizada para " + artifactName + ". Total de Layouts: " + ui.getLayouts().size() + ", Total de Componentes: " + ui.getComponents().size());
        return ui;
    }

    private void processTeamworksArtifact(Teamworks tw, ProcessUIV2Plus ui) {
        if (tw.getProcess() == null) return;
        br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process = tw.getProcess();
        if (process.getItems() != null) {
            process.getItems().stream()
                    .filter(item -> "CoachNG".equalsIgnoreCase(item.getTWComponentName()))
                    .forEach(item -> processLegacyCoach(item, ui));
        }
        if (process.getCoachflow() != null && process.getCoachflow().getDefinitions() != null) {
            processModernCoach(process.getCoachflow().getDefinitions(), ui);
        }
    }

    private void processDefinitionsArtifact(Definitions definitions, ProcessUIV2Plus ui) {
        processModernCoach(definitions, ui);
    }

    private void processModernCoach(Definitions definitions, ProcessUIV2Plus ui) {
        boolean foundUiTasks = false;
        Process mainProcess = definitions.getProcess();
        if (mainProcess != null && mainProcess.getFlowElements() != null) {
            foundUiTasks |= findAndProcessUiElements(mainProcess.getFlowElements(), ui, " ");
        }

        GlobalUserTask userTask = definitions.getGlobalUserTask();
        if (userTask != null && userTask.getImplementation() != null && userTask.getImplementation().getFlowElements() != null) {
            foundUiTasks |= findAndProcessUiElements(userTask.getImplementation().getFlowElements(), ui, " ");
        }

        if (!foundUiTasks) {
            //System.out.println("[LOG-UI] -> AVISO: Nenhuma tarefa de UI foi encontrada diretamente no artefato '" + getArtifactName(definitions) + "'.");
        }
    }

    private boolean findAndProcessUiElements(List<Object> flowElements, ProcessUIV2Plus ui, String indent) {
        boolean foundSomething = false;
        //System.out.println("[LOG-UI]" + indent + "-> Analisando " + flowElements.size() + " flow elements...");
        for (Object element : flowElements) {
            if (element instanceof FormTask) {
                processFormTask((FormTask) element, ui);
                foundSomething = true;
            } else if (element instanceof CallActivity) {
                processCallActivityForUi((CallActivity) element, ui);
                foundSomething = true;
            } else if (element instanceof SubProcess) {
                //System.out.println("[LOG-UI]" + indent + "-> Entrando no SubProcesso: '" + ((SubProcess) element).getName() + "' para procurar UI.");
                if (((SubProcess) element).getFlowElements() != null) {
                    foundSomething |= findAndProcessUiElements(((SubProcess) element).getFlowElements(), ui, indent + "  ");
                }
            }
        }
        return foundSomething;
    }

    private void processFormTask(FormTask formTask, ProcessUIV2Plus ui) {
        //System.out.println("[LOG-UI] -> Processando Coach Moderno (FormTask): '" + formTask.getName() + "' (ID: " + formTask.getId() + ")");
        if (formTask.getFormDefinition() == null || formTask.getFormDefinition().getCoachDefinition() == null) {
            //System.out.println("[LOG-UI]    - AVISO: FormTask sem definição de Coach válida.");
            return;
        }
        CoachDefinition coachDef = formTask.getFormDefinition().getCoachDefinition();
        Layout modernLayout = coachDef.getLayout();

        if (modernLayout != null && modernLayout.getLayoutItems() != null && !modernLayout.getLayoutItems().isEmpty()) {
            Map<String, List<CoachEventBinding>> eventsByControlId = new HashMap<>();

            // CORREÇÃO APLICADA: Agora o método getEventBindings() existe.
            if (coachDef.getEventBindings() != null) {
                //System.out.println("[LOG-UI]    - Encontrados " + coachDef.getEventBindings().size() + " eventos de nível de Coach.");
                for (CoachEventBinding event : coachDef.getEventBindings()) {
                    // CORREÇÃO APLICADA: Agora o método getControlId() existe.
                    eventsByControlId.computeIfAbsent(event.getControlId(), k -> new ArrayList<>()).add(event);
                }
            }

            ProcessUIV2Plus.UILayoutV2Plus uiLayout = new ProcessUIV2Plus.UILayoutV2Plus();
            uiLayout.setId("layout:" + formTask.getId());
            uiLayout.setName(formTask.getName());
            uiLayout.setType("Modern Coach");
            ui.getLayouts().add(uiLayout);

            List<String> rootComponentIds = new ArrayList<>();
            for (LayoutItem item : modernLayout.getLayoutItems()) {
                ProcessUIV2Plus.UIComponentV2Plus rootComponent = transformLayoutItem(item, ui, eventsByControlId, "   ");
                if (rootComponent != null) {
                    rootComponentIds.add(rootComponent.getId());
                }
            }
            if (!rootComponentIds.isEmpty()) {
                uiLayout.setConfiguration(new HashMap<>());
                uiLayout.getConfiguration().put("children", rootComponentIds);
            }
        }
    }
    private void processLegacyCoach(Item item, ProcessUIV2Plus ui) {
        TWComponent component = item.getTwComponent();
        if (component == null || component.getLayoutData() == null || component.getLayoutData().isEmpty()) return;
        try {
            CoachLayout layout = parseLayoutData(component.getLayoutData());
            if (layout != null && layout.getItems() != null && !layout.getItems().isEmpty()) {
                ProcessUIV2Plus.UILayoutV2Plus uiLayout = new ProcessUIV2Plus.UILayoutV2Plus();
                uiLayout.setId("layout:" + item.getProcessItemId());
                uiLayout.setName(item.getName());
                uiLayout.setType("Legacy Coach");
                ui.getLayouts().add(uiLayout);
                List<String> rootComponentIds = new ArrayList<>();
                for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem layoutItem : layout.getItems()) {
                    // CORREÇÃO: Chamando a versão sobrecarregada correta para Coaches legados
                    ProcessUIV2Plus.UIComponentV2Plus rootComponent = transformLayoutItem(layoutItem, ui, "   ");
                    if (rootComponent != null) {
                        rootComponentIds.add(rootComponent.getId());
                    }
                }
                if (!rootComponentIds.isEmpty()) {
                    uiLayout.setConfiguration(new HashMap<>());
                    uiLayout.getConfiguration().put("children", rootComponentIds);
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: Falha ao fazer parse do XML do Coach Legado '" + item.getName() + "'. Detalhes: " + e.getMessage());
        }
    }

    // MÉTODO SOBRECARREGADO (OVERLOADED) PARA COACHES LEGADOS
    private ProcessUIV2Plus.UIComponentV2Plus transformLayoutItem(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem legacyItem, ProcessUIV2Plus ui, String indent) {
        ProcessUIV2Plus.UIComponentV2Plus uiComponent = new ProcessUIV2Plus.UIComponentV2Plus();
        uiComponent.setId("comp:" + legacyItem.getLayoutItemId());
        uiComponent.setType(legacyItem.getXsiType() != null ? legacyItem.getXsiType().replace("coachview:", "") : "Unknown");
        uiComponent.setName(legacyItem.getLayoutItemId());

        if (legacyItem.getConfigData() != null) {
            legacyItem.getConfigData().forEach(config -> {
                String optionName = config.getOptionName();
                String value = config.getValue();
                if (optionName == null || value == null) return;

                if ("@label".equals(optionName)) {
                    uiComponent.setName(value);
                }
                if (optionName.startsWith("@on") && !value.trim().isEmpty()) {
                    uiComponent.getEvents().add("event:" + optionName.substring(1) + "::" + value);
                } else {
                    uiComponent.getProperties().put(optionName, value);
                }
            });
        }

        //System.out.println("[LOG-UI]" + indent + "- Componente (Legado) transformado: '" + uiComponent.getName() + "', Eventos: " + uiComponent.getEvents().size());

        List<Object> childrenToProcess = new ArrayList<>();
        if (legacyItem.getContentBoxContributions() != null) {
            legacyItem.getContentBoxContributions().forEach(c -> {
                if (c.getContributions() != null) childrenToProcess.addAll(c.getContributions());
            });
        }

        for (Object childItem : childrenToProcess) {
            if(childItem instanceof br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem){
                uiComponent.getChildren().add(transformLayoutItem((br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem)childItem, ui, indent + "  ").getId());
            }
        }

        ui.getComponents().add(uiComponent);
        return uiComponent;
    }

    // MÉTODO PARA COACHES MODERNOS
    private ProcessUIV2Plus.UIComponentV2Plus transformLayoutItem(LayoutItem modernItem, ProcessUIV2Plus ui, Map<String, List<CoachEventBinding>> eventsMap, String indent) {
        ProcessUIV2Plus.UIComponentV2Plus uiComponent = new ProcessUIV2Plus.UIComponentV2Plus();
        String layoutItemId = modernItem.getLayoutItemId();

        uiComponent.setId("comp:" + layoutItemId);
        uiComponent.setName(layoutItemId);

        if (modernItem.getConfigData() != null) {
            modernItem.getConfigData().stream()
                    .filter(cd -> "@label".equals(cd.getOptionName()) && cd.getValue() != null)
                    .findFirst()
                    .ifPresent(cd -> uiComponent.setName(cd.getValue()));
        }

        uiComponent.setType("View");
        uiComponent.getProperties().put("viewUUID", modernItem.getViewUUID());
        uiComponent.getProperties().put("binding", modernItem.getBinding());

        if (eventsMap != null && eventsMap.containsKey(layoutItemId)) {
            for(CoachEventBinding event : eventsMap.get(layoutItemId)) {
                // CORREÇÃO: Usando os nomes de método corretos 'getName' e 'getScript'
                if (event.getName() != null && event.getScript() != null && !event.getScript().trim().isEmpty()) {
                    uiComponent.getEvents().add("event:" + event.getName() + "::" + event.getScript());
                }
            }
        }

        //System.out.println("[LOG-UI]" + indent + "- Componente (Moderno) transformado: '" + uiComponent.getName() + "', Eventos: " + uiComponent.getEvents().size());

        List<Object> childrenToProcess = new ArrayList<>();
        if (modernItem.getContentBoxContribs() != null) {
            modernItem.getContentBoxContribs().forEach(c -> {
                if (c.getContributions() != null) childrenToProcess.addAll(c.getContributions());
            });
        }

        for (Object childItem : childrenToProcess) {
            if(childItem instanceof LayoutItem){
                uiComponent.getChildren().add(transformLayoutItem((LayoutItem)childItem, ui, eventsMap, indent + "  ").getId());
            }
        }

        ui.getComponents().add(uiComponent);
        return uiComponent;
    }
    private void processCallActivityForUi(CallActivity callActivity, ProcessUIV2Plus parentUi) {
        String calledElementId = callActivity.getCalledElement();
        if (calledElementId == null || calledElementId.trim().isEmpty()) return;
        Object calledArtifact = loader.getArtefatoDoCache(calledElementId);
        if (calledArtifact == null) return;

        ProcessUIV2Plus calledServiceUi = extractUI(calledArtifact);
        if (calledServiceUi != null) {
            parentUi.getComponents().addAll(calledServiceUi.getComponents());
            parentUi.getLayouts().addAll(calledServiceUi.getLayouts());
        }
    }

    private CoachLayout parseLayoutData(String layoutXml) throws Exception {
        String unescapedXml = layoutXml.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");
        Unmarshaller unmarshaller = this.coachLayoutContext.createUnmarshaller();
        return (CoachLayout) unmarshaller.unmarshal(new StringReader(unescapedXml));
    }

    private void addDefaultTheme(ProcessUIV2Plus ui) {
        // CORREÇÃO: Corrigido o erro de digitação de 'UIThemeV2plus' para 'UIThemeV2Plus'
        ProcessUIV2Plus.UIThemeV2Plus defaultTheme = new ProcessUIV2Plus.UIThemeV2Plus();
        defaultTheme.setId("theme:default");
        defaultTheme.setName("Default Theme");
        defaultTheme.getColors().put("primary", "#337ab7");
        defaultTheme.getColors().put("secondary", "#5cb85c");
        defaultTheme.getFonts().put("body", "Arial, sans-serif");
        ui.getThemes().add(defaultTheme);
    }

    private String getArtifactId(Object artifact) {
        if (artifact instanceof Teamworks) {
            Teamworks tw = (Teamworks) artifact;
            if (tw.getProcess() != null) return tw.getProcess().getId();
            if (tw.getBpd() != null) return tw.getBpd().getId();
        }
        if (artifact instanceof Definitions) {
            if (((Definitions) artifact).getProcess() != null) return ((Definitions) artifact).getProcess().getId();
            return ((Definitions) artifact).getId();
        }
        return "unknown-" + UUID.randomUUID().toString();
    }

    private String getArtifactName(Object artifact) {
        if (artifact instanceof Teamworks) {
            Teamworks tw = (Teamworks) artifact;
            if (tw.getProcess() != null) return tw.getProcess().getName();
            if (tw.getBpd() != null) return tw.getBpd().getName();
        }
        if (artifact instanceof Definitions) {
            if (((Definitions) artifact).getProcess() != null) return ((Definitions) artifact).getProcess().getName();
        }
        return "Unknown Artifact";
    }
}