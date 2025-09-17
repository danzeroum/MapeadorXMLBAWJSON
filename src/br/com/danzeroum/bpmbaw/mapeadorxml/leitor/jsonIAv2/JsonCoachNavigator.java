package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;



import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.FormTask;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.GlobalUserTask;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.ContentBoxContrib;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.Layout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.CoachView;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.json.JsonReport;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.TWComponent;
import br.com.danzeroum.bpmbaw.mapeadorxml.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonCoachNavigator {

    private final JsonReportGenerator generator;
    private final ProcessLoader loader;
    private JAXBContext coachLayoutContext;
    private final Set<String> discoveredCoachViewIds = new HashSet<>();
    Set<String> nomesPadrao = new HashSet<>(Arrays.asList(
            "Image", "Text Area Minimum Size", "Input Decimal", "Button",
            "Text Area", "Text", "Select", "Section", "Date Time Picker",
            "View Responsive CSS", "Vertical Section", "Stack Container",
            "Combo Box", "Tabs", "Input String", "Input Integer",
            "Checkbox", "Table", "Date Picker", "Data Table",
            "Responsive Row", "Output Text", "Check Box", "Horizontal Line",
            "Responsive Column", "Decimal", "Integer", "Horizontal Section"
    ));

    public JsonCoachNavigator(JsonReportGenerator generator, ProcessLoader loader) {
        this.generator = generator;
        this.loader = loader;
        try {
            this.coachLayoutContext = JAXBContext.newInstance(CoachLayout.class);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar JAXBContext para CoachLayout", e);
        }
    }


    public void runAnalysis() {
        // *** CORREÇÃO APLICADA AQUI ***
        // Para evitar ConcurrentModificationException, criamos uma cópia da lista de chaves
        // para iterar, permitindo que o 'loader' adicione novos artefatos ao cache original.
        Set<String> artifactIdsToProcess = new HashSet<>(loader.getCacheDeArtefatos().keySet());

        // Etapa 1: Navega por todos os Coaches para preencher a seção "coaches" e descobrir os IDs das Views
        for (String artifactId : artifactIdsToProcess) {
            Object artifactObj = loader.getArtefatoDoCache(artifactId);
            if (artifactObj instanceof Teamworks) {
                Teamworks tw = (Teamworks) artifactObj;
                if (tw.getProcess() != null) {
                    // Lógica para Coaches em Serviços Legados
                    if (tw.getProcess().getItems() != null) {
                        tw.getProcess().getItems().forEach(item -> {
                            if ("CoachNG".equalsIgnoreCase(item.getTWComponentName())) {
                                processLegacyCoach(item, tw.getProcess());
                            }
                        });
                    }
                    // Lógica para Coaches Modernos (Client-Side)
                    if (tw.getProcess().getCoachflow() != null && tw.getProcess().getCoachflow().getDefinitions() != null) {
                        processModernCoach(tw.getProcess(), tw.getProcess().getCoachflow().getDefinitions());
                    }
                }
            }
        }

        // Etapa 2: Com a lista de IDs de Views, processa os detalhes de cada uma
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

                    JsonReport.JsonCoach jsonCoach = new JsonReport.JsonCoach();
                    jsonCoach.setCoachName(formTask.getName());
                    jsonCoach.setParentServiceId(process.getId());
                    jsonCoach.setParentServiceName(process.getName());
                    jsonCoach.setCoachId("coachId_"+formTask.getId());


                    Layout modernLayout = formTask.getFormDefinition().getCoachDefinition().getLayout();
                    for (LayoutItem item : modernLayout.getLayoutItems()) {
                        jsonCoach.getComponents().add(transformModernLayoutItem(item));
                    }
                    generator.getReport().getUiReport().getCoaches().add(jsonCoach);
                }
            }
        }
    }

    private JsonReport.JsonUiComponent transformModernLayoutItem(LayoutItem item) {
        JsonReport.JsonUiComponent component = new JsonReport.JsonUiComponent();
        component.setComponentId(item.getLayoutItemId());
        component.setType("ViewRef");
        component.setBinding(item.getBinding());

        if (item.getViewUUID() != null) {
            String cleanId = loader.getCleanId(item.getViewUUID());
            loader.loadArtefatoSeNaoExistir(cleanId);
            Object artifact = loader.getArtefatoDoCache(cleanId);
            if (artifact instanceof Teamworks) {
                CoachView cv = ((Teamworks) artifact).getCoachView();
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

        boolean filteredHasTw = false;
        if (item.getConfigData() != null) {
            // Garante que não herdaremos configurações anteriores
            component.getConfiguration().clear();

            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.ConfigData config : item.getConfigData()) {
                String option = config.getOptionName();
                String value = config.getValue();
                if (option != null && value != null) {
                    if ("@label".equals(option)) {
                        component.setLabel(value);
                    } else if (option.toLowerCase().startsWith("event.on")) {
                        component.getEvents().put(option, value);
                    } else if (value.contains("tw.")) {
                        component.getConfiguration().put(option, value);
                        filteredHasTw = true;
                    }
                }
            }

            // Se não há nenhuma configuração com "tw.", remove completamente a tag "configuration"
            if (!filteredHasTw) {
                component.setConfiguration(null);
            }
        } else {
            // Sem configData: também garante que não imprimiremos "configuration"
            component.setConfiguration(null);
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
        TWComponent component = item.getTwComponent();
        if (component == null || component.getLayoutData() == null || component.getLayoutData().isEmpty()) {
            return;
        }

        JsonReport.JsonCoach jsonCoach = new JsonReport.JsonCoach();
        jsonCoach.setCoachName(item.getName());
        jsonCoach.setParentServiceId(process.getId());
        jsonCoach.setParentServiceName(process.getName());
        jsonCoach.setCoachId("coachId_"+item.getProcessItemId());

        if (item.getProcessPrePosts() != null) {
            item.getProcessPrePosts().stream()
                    .filter(p -> p.getLocation() == 1 && p.getScript() != null && !p.getScript().isEmpty())
                    .forEach(p -> jsonCoach.getPreExecutionScripts().add(p.getScript()));
        }

        if (component.getCoachNGBoundaryEvents() != null) {
            component.getCoachNGBoundaryEvents().forEach(be -> {
                JsonReport.BoundaryEvent event = new JsonReport.BoundaryEvent();
                event.setViewPath(be.getViewPath());
                event.setEventLabel(be.getEventLabel());
                event.setFiresValidation(be.getFireValidation() == 1);
                jsonCoach.getBoundaryEvents().add(event);
            });
        }

        try {
            CoachLayout layout = parseLayoutData(component.getLayoutData());
            if (layout != null && layout.getItems() != null) {
                for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem layoutItem : layout.getItems()) {
                    jsonCoach.getComponents().add(transformLegacyLayoutItem(layoutItem));
                }
            }
        } catch (Exception e) {
            System.err.println("ERRO: Falha ao parsear layout do Coach legado: " + item.getName());
        }

        generator.getReport().getUiReport().getCoaches().add(jsonCoach);
    }

    private JsonReport.JsonUiComponent transformLegacyLayoutItem(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item) {
        JsonReport.JsonUiComponent component = new JsonReport.JsonUiComponent();
        component.setComponentId(item.getLayoutItemId());
        component.setType(item.getXsiType());
        component.setBinding(item.getBinding());

        if (item.getViewUUID() != null) {
            String cleanId = loader.getCleanId(item.getViewUUID());
            loader.loadArtefatoSeNaoExistir(cleanId);
            Object artifact = loader.getArtefatoDoCache(cleanId);
            if (artifact instanceof Teamworks) {
                CoachView cv = ((Teamworks) artifact).getCoachView();
                if( cv != null ) {
                    if( !nomesPadrao.contains(cv.getName())){
                        component.setCoachViewId(cleanId);
                        discoveredCoachViewIds.add(cleanId);
                    } else {
                        component.setType(cv.getName());
                    }
                }
            }
        }

        if(item.getConfigData() != null) {
            for(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData config : item.getConfigData()) {
                if (config.getOptionName() != null && config.getValue() != null) {
                    if ("@label".equals(config.getOptionName())) {
                        component.setLabel(config.getValue());
                    } else if (config.getOptionName().toLowerCase().startsWith("event.on")) {
                        component.getEvents().put(config.getOptionName(), config.getValue());
                    } else {
                        component.getConfiguration().put(config.getOptionName(), config.getValue());
                    }
                }
            }
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
                CoachView cv = ((Teamworks) artifact).getCoachView();
                if (cv != null) {
                    JsonReport.JsonCoachView jsonCv = new JsonReport.JsonCoachView();
                    jsonCv.setId(cv.getId());
                    jsonCv.setName(cv.getName());
                    System.out.println(cv.getName());
                    // Processa apenas scripts que atendem às condições
                    if (cv.getInlineScripts() != null) {
                        cv.getInlineScripts().forEach(s -> {
                            if ("Inline Javascript".equals(s.getName()) && s.getScriptBlock() != null && !s.getScriptBlock().isEmpty()) {
                                JsonReport.InlineScript jsonScript = new JsonReport.InlineScript();
                                jsonScript.setName(s.getName());
                                jsonScript.setScriptType(s.getScriptType());
                                jsonScript.setScriptBlock(s.getScriptBlock());
                                jsonCv.getInlineScripts().add(jsonScript);
                                generator.getReport().getUiReport().getCoachViews().add(jsonCv);
                            }
                        });
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