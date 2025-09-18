package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2.BoundaryEvent;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2.Coach;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2.CoachView;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2.InlineScript;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2.UiComponent;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.FormTask;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.GlobalUserTask;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.ContentBoxContrib;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.Layout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Navegador de UI refatorado para popular a seção 'uiReport' do JSON,
 * resolvendo o conflito com a lista 'rootView' da classe Artifact.
 */
public class JsonCoachNavigatorV2 {

    private final JsonReportGeneratorV2 generator;
    private final ProcessLoaderV2Plus loader;
    private final JAXBContext coachLayoutContext;
    private final Set<String> discoveredCoachViewIds = new HashSet<>();
    private final Set<String> nomesPadrao = new HashSet<>(Arrays.asList(
            "Image", "Text Area Minimum Size", "Input Decimal", "Button",
            "Text Area", "Text", "Select", "Section", "Date Time Picker",
            "View Responsive CSS", "Vertical Section", "Stack Container",
            "Combo Box", "Tabs", "Input String", "Input Integer",
            "Checkbox", "Table", "Date Picker", "Data Table",
            "Responsive Row", "Output Text", "Check Box", "Horizontal Line",
            "Responsive Column", "Decimal", "Integer", "Horizontal Section"
    ));

    public JsonCoachNavigatorV2(JsonReportGeneratorV2 generator, ProcessLoaderV2Plus loader) throws JAXBException {
        this.generator = generator;
        this.loader = loader;
        this.coachLayoutContext = JAXBContext.newInstance(CoachLayout.class);
    }

    public void runAnalysis() {
        // *** CORREÇÃO APLICADA AQUI ***
        // Para evitar ConcurrentModificationException, criamos uma cópia da coleção de artefatos
        // para iterar, permitindo que o 'loader' adicione novos artefatos ao cache original.
        Collection<Object> artifactsToProcess = new ArrayList<>(loader.getCacheDeArtefatos().values());

        // Itera sobre a cópia dos artefatos carregados para encontrar processos e serviços
        for (Object artifactObj : artifactsToProcess) {
            if (artifactObj instanceof Teamworks) {
                Teamworks tw = (Teamworks) artifactObj;
                if (tw.getProcess() != null) {
                    // Lógica para Coaches em Serviços Legados (baseado em 'item')
                    if (tw.getProcess().getItems() != null) {
                        tw.getProcess().getItems().forEach(item -> {
                            if ("CoachNG".equalsIgnoreCase(item.getTWComponentName())) {
                                processLegacyCoach(item, tw.getProcess());
                            }
                        });
                    }
                    // Lógica para Coaches Modernos (Client-Side, baseado em 'coachflow')
                    if (tw.getProcess().getCoachflow() != null && tw.getProcess().getCoachflow().getDefinitions() != null) {
                        processModernCoach(tw.getProcess(), tw.getProcess().getCoachflow().getDefinitions());
                    }
                }
            }
        }
        // Após descobrir todas as views, processa os detalhes delas
        processDiscoveredCoachViews();
    }

    private void processModernCoach(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process, Definitions definitions) {
        GlobalUserTask userTask = definitions.getGlobalUserTask();
        if (userTask == null || userTask.getImplementation() == null || userTask.getImplementation().getFlowElements() == null) {
            return;
        }

        for (Object flowElement : userTask.getImplementation().getFlowElements()) {
            if (flowElement instanceof FormTask) {
                FormTask formTask = (FormTask) flowElement;
                if (formTask.getFormDefinition() != null &&
                        formTask.getFormDefinition().getCoachDefinition() != null &&
                        formTask.getFormDefinition().getCoachDefinition().getLayout() != null &&
                        formTask.getFormDefinition().getCoachDefinition().getLayout().getLayoutItems() != null) {

                    Coach jsonCoach = new Coach();
                    jsonCoach.setCoachName(formTask.getName());
                    jsonCoach.setParentServiceId(process.getId());
                    jsonCoach.setParentServiceName(process.getName());
                    jsonCoach.setCoachId("coachId_" + formTask.getId());

                    Layout modernLayout = formTask.getFormDefinition().getCoachDefinition().getLayout();
                    for (LayoutItem item : modernLayout.getLayoutItems()) {
                        jsonCoach.getComponents().add(transformModernLayoutItem(item));
                    }
                    // Adiciona o Coach na seção correta do relatório
                    generator.getReport().getUiReport().getCoaches().add(jsonCoach);
                }
            }
        }
    }

    private UiComponent transformModernLayoutItem(LayoutItem item) {
        UiComponent component = new UiComponent();
        component.setComponentId(item.getLayoutItemId());
        component.setType("ViewRef");
        component.setBinding(item.getBinding());

        if (item.getViewUUID() != null) {
            String cleanId = loader.getCleanId(item.getViewUUID());
            loader.loadDependentArtifactIfNotExists(cleanId);
            Object artifact = loader.getArtefatoDoCache(cleanId);
            if (artifact instanceof Teamworks) {
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.CoachView cv = ((Teamworks) artifact).getCoachView();
                if (cv != null) {
                    if (!nomesPadrao.contains(cv.getName())) {
                        component.setCoachViewId(cleanId);
                        discoveredCoachViewIds.add(cleanId);
                    } else {
                        component.setType(cv.getName());
                    }
                }
            }
        }

        if (item.getConfigData() != null) {
            item.getConfigData().forEach(config -> {
                String option = config.getOptionName();
                String value = config.getValue();
                if (option != null && value != null) {
                    if ("@label".equals(option)) {
                        component.setLabel(value);
                    } else if (option.toLowerCase().startsWith("event.on")) {
                        component.getEvents().put(option, value);
                    } else {
                        component.getConfiguration().put(option, value);
                    }
                }
            });
        }

        if (item.getContentBoxContribs() != null) {
            for (ContentBoxContrib contrib : item.getContentBoxContribs()) {
                if (contrib.getContributions() != null) {
                    for (LayoutItem subItem : contrib.getContributions()) {
                        component.getChildren().add(transformModernLayoutItem(subItem));
                    }
                }
            }
        }
        return component;
    }

    private void processLegacyCoach(Item item, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process) {
        if (item.getTwComponent() == null || item.getTwComponent().getLayoutData() == null || item.getTwComponent().getLayoutData().isEmpty()) {
            return;
        }

        Coach jsonCoach = new Coach();
        jsonCoach.setCoachName(item.getName());
        jsonCoach.setParentServiceId(process.getId());
        jsonCoach.setParentServiceName(process.getName());
        jsonCoach.setCoachId("coachId_" + item.getProcessItemId());

        if (item.getProcessPrePosts() != null) {
            item.getProcessPrePosts().stream()
                    .filter(p -> p.getLocation() == 1 && p.getScript() != null && !p.getScript().isEmpty())
                    .forEach(p -> jsonCoach.getPreExecutionScripts().add(p.getScript()));
        }

        if (item.getTwComponent().getCoachNGBoundaryEvents() != null) {
            item.getTwComponent().getCoachNGBoundaryEvents().forEach(be -> {
                BoundaryEvent event = new BoundaryEvent();
                event.setViewPath(be.getViewPath());
                event.setEventLabel(be.getEventLabel());
                event.setFiresValidation(be.getFireValidation() == 1);
                jsonCoach.getBoundaryEvents().add(event);
            });
        }

        try {
            CoachLayout layout = parseLayoutData(item.getTwComponent().getLayoutData());
            if (layout != null && layout.getItems() != null) {
                for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem layoutItem : layout.getItems()) {
                    jsonCoach.getComponents().add(transformLegacyLayoutItem(layoutItem));
                }
            }
        } catch (Exception e) {
            System.err.println("ERRO: Falha ao parsear layout do Coach legado: " + item.getName());
        }
        // Adiciona o Coach na seção correta do relatório
        generator.getReport().getUiReport().getCoaches().add(jsonCoach);
    }

    private UiComponent transformLegacyLayoutItem(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item) {
        UiComponent component = new UiComponent();
        component.setComponentId(item.getLayoutItemId());
        component.setType(item.getXsiType());
        component.setBinding(item.getBinding());

        if (item.getViewUUID() != null) {
            String cleanId = loader.getCleanId(item.getViewUUID());
            loader.loadDependentArtifactIfNotExists(cleanId);
            Object artifact = loader.getArtefatoDoCache(cleanId);
            if (artifact instanceof Teamworks) {
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.CoachView cv = ((Teamworks) artifact).getCoachView();
                if (cv != null) {
                    if (!nomesPadrao.contains(cv.getName())) {
                        component.setCoachViewId(cleanId);
                        discoveredCoachViewIds.add(cleanId);
                    } else {
                        component.setType(cv.getName());
                    }
                }
            }
        }

        if (item.getConfigData() != null) {
            item.getConfigData().forEach(config -> {
                if (config.getOptionName() != null && config.getValue() != null) {
                    if ("@label".equals(config.getOptionName())) {
                        component.setLabel(config.getValue());
                    } else if (config.getOptionName().toLowerCase().startsWith("event.on")) {
                        component.getEvents().put(config.getOptionName(), config.getValue());
                    } else {
                        component.getConfiguration().put(config.getOptionName(), config.getValue());
                    }
                }
            });
        }

        if (item.getContentBoxContributions() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem.ContentBoxContribution contrib : item.getContentBoxContributions()) {
                if (contrib.getContributions() != null) {
                    for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem subItem : contrib.getContributions()) {
                        component.getChildren().add(transformLegacyLayoutItem(subItem));
                    }
                }
            }
        }
        return component;
    }

    private void processDiscoveredCoachViews() {
        for (String cvId : discoveredCoachViewIds) {
            Object artifact = loader.getArtefatoDoCache(cvId);
            if (artifact instanceof Teamworks) {
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.CoachView cv = ((Teamworks) artifact).getCoachView();
                if (cv != null) {
                    CoachView jsonCv = new CoachView();
                    jsonCv.setId(cv.getId());
                    jsonCv.setName(cv.getName());

                    if (cv.getInlineScripts() != null) {
                        cv.getInlineScripts().forEach(s -> {
                            if ("Inline Javascript".equals(s.getName()) && s.getScriptBlock() != null && !s.getScriptBlock().isEmpty()) {
                                InlineScript jsonScript = new InlineScript();
                                jsonScript.setName(s.getName());
                                jsonScript.setScriptType(s.getScriptType());
                                jsonScript.setScriptBlock(s.getScriptBlock());
                                jsonCv.getInlineScripts().add(jsonScript);
                            }
                        });
                    }
                    if (!jsonCv.getInlineScripts().isEmpty()) {
                        generator.getReport().getUiReport().getCoachViews().add(jsonCv);
                    }
                }
            }
        }
    }

    private CoachLayout parseLayoutData(String layoutXml) throws Exception {
        String unescapedXml = layoutXml.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");
        Unmarshaller unmarshaller = this.coachLayoutContext.createUnmarshaller();
        return (CoachLayout) unmarshaller.unmarshal(new StringReader(unescapedXml));
    }
}