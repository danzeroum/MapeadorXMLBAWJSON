// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/JsonCoachNavigator.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIdcoach;

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
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.TWComponent;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

/**
 * (VERSÃO REATORADA)
 * Navegador especialista que analisa a estrutura de Coaches, tanto em processos legados
 * quanto em processos BPMN modernos.
 * Responsabilidades:
 * 1. Popular a seção centralizada 'uiReport' no JSON final.
 * 2. Descobrir todas as Coach Views utilizadas e carregar suas definições.
 * 3. Gerar e retornar um ID único para cada Coach, para ser referenciado pelo fluxo do processo.
 */
public class JsonCoachNavigator_idCoach {

    private final JsonReportGenerator_idCoach generator;
    private final ProcessLoader_idCoach loader;
    private JAXBContext coachLayoutContext;
    private final Set<String> discoveredCoachViewIds = new HashSet<>();
    private final Set<String> processedCoachIds = new HashSet<>();



    public JsonCoachNavigator_idCoach(JsonReportGenerator_idCoach generator, ProcessLoader_idCoach loader) {
        this.generator = generator;
        this.loader = loader;
        try {
            this.coachLayoutContext = JAXBContext.newInstance(CoachLayout.class);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar JAXBContext para CoachLayout", e);
        }
    }

    /**
     * Orquestra a análise de todas as interfaces de usuário (Coaches).
     * Primeiro, ele varre todos os artefatos de processo para encontrar e processar os Coaches.
     * Depois, ele processa os detalhes de todas as Coach Views que foram descobertas.
     */
    public void runAnalysis() {
        // Para evitar ConcurrentModificationException, iteramos sobre uma cópia das chaves do cache.
        Set<String> artifactIdsToProcess = new HashSet<>(loader.getCacheDeArtefatos().keySet());

        for (String artifactId : artifactIdsToProcess) {
            Object artifactObj = loader.getArtefatoDoCache(artifactId);
            if (artifactObj instanceof Teamworks) {
                Teamworks tw = (Teamworks) artifactObj;
                if (tw.getProcess() != null) {
                    // Processa Coaches em Serviços Legados (Heritage) baseados em <item>
                    if (tw.getProcess().getItems() != null) {
                        tw.getProcess().getItems().forEach(item -> {
                            if ("CoachNG".equalsIgnoreCase(item.getTWComponentName())) {
                                processLegacyCoach(item, tw.getProcess());
                            }
                        });
                    }
                    // Processa Coaches em Serviços Modernos (Client-Side) baseados em <coachflow>
                    if (tw.getProcess().getCoachflow() != null && tw.getProcess().getCoachflow().getDefinitions() != null) {
                        processModernCoach(tw.getProcess(), tw.getProcess().getCoachflow().getDefinitions());
                    }
                }
            }
        }

        // Após descobrir todas as views, processa os detalhes de cada uma.
        processDiscoveredCoachViews();
    }

    /**
     * Processa um Coach moderno encontrado dentro de um <coachflow> (Serviço Humano do Lado do Cliente).
     * Retorna o ID único gerado para este Coach.
     *
     * @param process O serviço pai.
     * @param definitions As definições BPMN contidas no coachflow.
     * @return O ID único do Coach processado.
     */
    public String processModernCoach(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process, Definitions definitions) {
        GlobalUserTask userTask = definitions.getGlobalUserTask();
        if (userTask == null || userTask.getImplementation() == null || userTask.getImplementation().getFlowElements() == null) {
            return null;
        }

        for (Object flowElement : userTask.getImplementation().getFlowElements()) {
            if (flowElement instanceof FormTask) {
                FormTask formTask = (FormTask) flowElement;
                if (formTask.getFormDefinition() != null &&
                        formTask.getFormDefinition().getCoachDefinition() != null &&
                        formTask.getFormDefinition().getCoachDefinition().getLayout() != null &&
                        formTask.getFormDefinition().getCoachDefinition().getLayout().getLayoutItems() != null) {

                    // Gera um ID único para o Coach
                    String coachId = process.getId() + "-Coach";  //formTask.getName().replaceAll("\\s+", "_");

                    JsonReport.JsonCoach jsonCoach = new JsonReport.JsonCoach();
                    jsonCoach.setCoachId(coachId); // Define o ID
                    jsonCoach.setCoachName(formTask.getName());
                    jsonCoach.setParentServiceId(process.getId());
                    jsonCoach.setParentServiceName(process.getName());

                    Layout modernLayout = formTask.getFormDefinition().getCoachDefinition().getLayout();
                    for (LayoutItem item : modernLayout.getLayoutItems()) {
                        jsonCoach.getComponents().add(transformModernLayoutItem(item));
                    }

                    // Adiciona o Coach processado à lista central do relatório
                    generator.getReport().getUiReport().getCoaches().add(jsonCoach);
                    return coachId; // Retorna o ID para referência
                }
            }
        }
        return null;
    }

    /**
     * Processa um Coach legado encontrado em um serviço Heritage.
     * Retorna o ID único gerado para este Coach.
     *
     * @param item O item do tipo CoachNG.
     * @param process O serviço pai.
     * @return O ID único do Coach processado.
     */
    public String processLegacyCoach(Item item, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process) {
        TWComponent component = item.getTwComponent();
        if (component == null || component.getLayoutData() == null || component.getLayoutData().isEmpty()) {
            return null;
        }

        // Gera um ID único para o Coach
        //String coachId = process.getId() + "-" + item.getName().replaceAll("\\s+", "_");
        String coachId = process.getId() + "-Coach";// + item.getName().replaceAll("\\s+", "_");

        if (processedCoachIds.contains(coachId)) {
            return coachId;
        }

        JsonReport.JsonCoach jsonCoach = new JsonReport.JsonCoach();
        jsonCoach.setCoachId(coachId); // Define o ID
        jsonCoach.setCoachName(item.getName());
        jsonCoach.setParentServiceId(process.getId());
        jsonCoach.setParentServiceName(process.getName());

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

        // Adiciona o Coach processado à lista central do relatório
        generator.getReport().getUiReport().getCoaches().add(jsonCoach);
        return coachId; // Retorna o ID para referência
    }

    // MÉTODOS transform... E processDiscoveredCoachViews permanecem privados e com a mesma lógica.

    private JsonReport.JsonUiComponent transformModernLayoutItem(LayoutItem item) {
        JsonReport.JsonUiComponent component = new JsonReport.JsonUiComponent();
        component.setComponentId(item.getLayoutItemId());
        component.setType("ViewRef"); // Coaches modernos sempre referenciam Views
        component.setBinding(item.getBinding());

        if (item.getViewUUID() != null) {
            String cleanId = loader.getCleanId(item.getViewUUID());
            component.setCoachViewId(cleanId);
            loader.loadDependentArtifactIfNotExists(cleanId);
            discoveredCoachViewIds.add(cleanId);
        }

        if (item.getConfigData() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.ConfigData config : item.getConfigData()) {
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

    private JsonReport.JsonUiComponent transformLegacyLayoutItem(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item) {
        JsonReport.JsonUiComponent component = new JsonReport.JsonUiComponent();
        component.setComponentId(item.getLayoutItemId());
        component.setType(item.getXsiType());
        component.setBinding(item.getBinding());

        if (item.getViewUUID() != null) {
            String cleanId = loader.getCleanId(item.getViewUUID());
            component.setCoachViewId(cleanId);
            loader.loadDependentArtifactIfNotExists(cleanId);
            discoveredCoachViewIds.add(cleanId);
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

                    if (cv.getInlineScripts() != null) {
                        cv.getInlineScripts().forEach(s -> {
                            JsonReport.InlineScript jsonScript = new JsonReport.InlineScript();
                            jsonScript.setName(s.getName());
                            jsonScript.setScriptType(s.getScriptType());
                            jsonScript.setScriptBlock(s.getScriptBlock());
                            jsonCv.getInlineScripts().add(jsonScript);
                        });
                    }
                    generator.getReport().getUiReport().getCoachViews().add(jsonCv);
                }
            }
        }
    }

    private CoachLayout parseLayoutData(String layoutXml) throws Exception {
        if (layoutXml == null || layoutXml.trim().isEmpty()) {
            return null;
        }
        String unescapedXml = layoutXml.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");
        Unmarshaller unmarshaller = this.coachLayoutContext.createUnmarshaller();
        return (CoachLayout) unmarshaller.unmarshal(new StringReader(unescapedXml));
    }
}