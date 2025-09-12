package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.envar.EnvironmentVariableSet;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.participant.Participant;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.participant.StandardMember;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Dependency;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.PackageObject;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.resources.ResourceBundleGroup;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import br.com.danzeroum.bpmbaw.mapeadorxml.util.ClassFinder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public class ProcessLoaderV2Plus {


    private final PrintWriter logger;

    private final String rootDirectoryPath;
    private final Map<String, Object> cacheDeArtefatos = new HashMap<>();
    private final br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package mainManifest;
    private final Map<String, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package> manifestCache = new HashMap<>();
    private JAXBContext universalJaxbContext;
    private boolean detailedLogging = false;
    private final Map<String, JAXBContext> jaxbContexts = new HashMap<>();


    public ProcessLoaderV2Plus(String twxDirectoryPath) throws Exception {
        this.logger = null;
        this.rootDirectoryPath = twxDirectoryPath;
        File mainManifestFile = new File(new File(twxDirectoryPath, "META-INF"), "package.xml");

        log("[DETAIL] Loading main manifest: " + mainManifestFile.getAbsolutePath());
        this.mainManifest = loadXmlFile(mainManifestFile, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package.class);

        if (this.mainManifest == null) {
            throw new IllegalArgumentException("Main package.xml not found in META-INF");
        }

        log("[INFO] Main manifest loaded successfully with " +
                (mainManifest.getObjects() != null ? mainManifest.getObjects().size() : 0) + " objects");

        manifestCache.put("main", this.mainManifest);
        initializeJaxbContext();
        //loadManifest();
    }

    public ProcessLoaderV2Plus(String twxDirectoryPath, PrintWriter logger) throws Exception {
        this.rootDirectoryPath = twxDirectoryPath;
        this.logger = logger;
        File mainManifestFile = new File(new File(twxDirectoryPath, "META-INF"), "package.xml");
        this.mainManifest = loadXmlFile(mainManifestFile, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package.class);
        if (this.mainManifest == null) {
            throw new IllegalArgumentException("Arquivo package.xml principal não encontrado em META-INF.");
        }
        manifestCache.put("main", this.mainManifest);

    }

    private void initializeJaxbContext() throws Exception {
        log("[DETAIL] Initializing universal JAXB context...");

        universalJaxbContext = JAXBContext.newInstance(
                // Root container
                Teamworks.class,
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package.class,

                // BPD Classes
                Bpd.class,
                BusinessProcessDiagram.class,
                Pool.class,
                Lane.class,
                FlowObject.class,
                Component.class,
                Implementation.class,
                Flow.class,
                DiagramFlow.class,
                StartPoint.class,
                Note.class,

                // Process Classes
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process.class,
                CoachFlow.class,
                Item.class,
                Link.class,
                ParameterMapping.class,
                ProcessParameter.class,
                ProcessPrePost.class,
                ProcessVariable.class,
                SwitchCondition.class,
                TWComponent.class,
                CoachNGBoundaryEvents.class,

                // BPMN Classes
                Definitions.class,
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process.class,
                LaneSet.class,
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Lane.class,
                FlowNode.class,
                StartEvent.class,
                EndEvent.class,
                ScriptTask.class,
                Task.class,
                FormTask.class,
                CallActivity.class,
                SubProcess.class,
                ExclusiveGateway.class,
                SequenceFlow.class,
                IoSpecification.class,
                DataInput.class,
                DataOutput.class,
                DataInputAssociation.class,
                DataOutputAssociation.class,
                Assignment.class,
                From.class,
                To.class,
                GlobalUserTask.class,
                UserTaskImplementation.class,
                ExtensionElements.class,

                // Coach Classes
                CoachLayout.class,
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem.class,
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData.class,
                Layout.class,
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem.class,
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.ConfigData.class,
                ContentBoxContrib.class,
                CoachDefinition.class,

                // CoachView Classes
                CoachView.class,
                ConfigOption.class,
                InlineScript.class,

                // Other Classes
                TwClass.class,
                Definition.class,
                Property.class,
                Participant.class,
                StandardMember.class,
                ResourceBundleGroup.class,
                EnvironmentVariableSet.class
        );

        log("[DETAIL] Universal JAXB context initialized successfully");
    }

    /**
     * CORREÇÃO PRINCIPAL: Carrega primeiro como Teamworks, depois extrai o conteúdo específico
     */
    public Object loadArtifact(String artifactId) {
        try {
            String cleanId = getCleanId(artifactId);

            if (cacheDeArtefatos.containsKey(cleanId)) {
                return cacheDeArtefatos.get(cleanId);
            }

            ArtifactLocation location = findArtifactLocation(artifactId);
            if (location == null) {
                log("[ERROR] Could not find location for artifact: " + artifactId);
                return null;
            }

            File targetFile = new File(location.basePath, location.filePath);
            log("[DETAIL] Loading artifact from: " + targetFile.getAbsolutePath());

            // SEMPRE carregar primeiro como Teamworks
            String xmlContent = new String(Files.readAllBytes(targetFile.toPath()), StandardCharsets.UTF_8);
            if (xmlContent.startsWith("\uFEFF")) {
                xmlContent = xmlContent.substring(1);
            }

            Unmarshaller unmarshaller = universalJaxbContext.createUnmarshaller();
            Object result = unmarshaller.unmarshal(new StringReader(xmlContent));

            // Se for Teamworks, extrair o conteúdo específico baseado no tipo
            if (result instanceof Teamworks) {
                Teamworks tw = (Teamworks) result;

                // Verificar se tem BPMN2 data primeiro
                if (location.objectInfo.getType().equalsIgnoreCase("bpd") && tw.getBpd() != null) {
                    String bpmn2Data = extractBpmn2DataFromBpd(tw.getBpd());
                    if (bpmn2Data != null && !bpmn2Data.isEmpty()) {
                        try {
                            Definitions definitions = (Definitions) unmarshaller.unmarshal(new StringReader(bpmn2Data));
                            cacheDeArtefatos.put(cleanId, definitions);
                            return definitions;
                        } catch (Exception e) {
                            log("[DETAIL] Could not parse BPMN2 data, using BPD: " + e.getMessage());
                        }
                    }
                }

                // Retornar o Teamworks completo para que os extractors possam acessar o conteúdo
                cacheDeArtefatos.put(cleanId, tw);
                return tw;
            }

            cacheDeArtefatos.put(cleanId, result);
            return result;

        } catch (Exception e) {
            log("[ERROR] Error loading artifact " + artifactId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Extrai BPMN2 data de um BPD se existir
     */
    private String extractBpmn2DataFromBpd(Bpd bpd) {
        try {
            if (bpd == null || bpd.getBusinessProcessDiagram() == null) {
                return null;
            }

            // Tenta acessar via reflexão se existir método getBpmn2Data
            java.lang.reflect.Method method = bpd.getBusinessProcessDiagram().getClass().getMethod("getBpmn2Data");
            if (method != null) {
                Object data = method.invoke(bpd.getBusinessProcessDiagram());
                return data != null ? data.toString() : null;
            }
        } catch (Exception e) {
            log("[DETAIL] No BPMN2 data available: " + e.getMessage());
        }
        return null;
    }



    public Map<String, Object> loadProcessInMemory(String objectId) {
        Set<String> visitedIds = new HashSet<>();
        carregarArtefatoRecursivamente(objectId, visitedIds);
        return Collections.unmodifiableMap(cacheDeArtefatos);
    }

    public Map<String, Object> loadAllProcessesInMemory(List<String> objectIds) {
        logger.println("[LOG] Iniciando fase de carregamento para múltiplos IDs raiz...");
        Set<String> visitedIds = new HashSet<>();
        for (String objectId : objectIds) {
            carregarArtefatoRecursivamente(objectId, visitedIds);
        }
        logger.println("[LOG] Carregamento concluído. " + cacheDeArtefatos.size() + " artefatos em memória.");
        return Collections.unmodifiableMap(cacheDeArtefatos);
    }


    public br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package getMainManifest() {
        return mainManifest;
    }


    private void carregarArtefatoRecursivamente(String objectId, Set<String> visitedIds) {
        String cleanId = getCleanId(objectId);
        if (cleanId == null || !visitedIds.add(cleanId)) {
            return;
        }

        Object artifact = loadArtifact(objectId);
        if (artifact == null) {
            return;
        }

        // Encontrar subprocessos e carregar recursivamente
        Set<String> subprocessIds = findSubprocessIds(artifact);
        for (String subId : subprocessIds) {
            carregarArtefatoRecursivamente(subId, visitedIds);
        }
    }

    private Set<String> findSubprocessIds(Object artifact) {
        Set<String> ids = new HashSet<>();

        if (artifact instanceof Teamworks) {
            Teamworks tw = (Teamworks) artifact;

            // Check Process
            if (tw.getProcess() != null) {
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process = tw.getProcess();

                // Check items for subprocess references
                if (process.getItems() != null) {
                    for (Item item : process.getItems()) {
                        if (item.getTwComponent() != null &&
                                item.getTwComponent().getAttachedProcessRef() != null) {
                            ids.add(item.getTwComponent().getAttachedProcessRef());
                        }
                    }
                }

                // Check coachflow
                if (process.getCoachflow() != null &&
                        process.getCoachflow().getDefinitions() != null) {
                    findSubprocessIdsInDefinitions(process.getCoachflow().getDefinitions(), ids);
                }
            }

            // Check BPD
            if (tw.getBpd() != null) {
                findSubprocessIdsInBpd(tw.getBpd(), ids);
            }
        } else if (artifact instanceof Definitions) {
            findSubprocessIdsInDefinitions((Definitions) artifact, ids);
        }

        return ids;
    }

    private void findSubprocessIdsInBpd(Bpd bpd, Set<String> ids) {
        if (bpd.getBusinessProcessDiagram() == null) return;

        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();
        if (diagram.getPools() != null) {
            for (Pool pool : diagram.getPools()) {
                if (pool.getLanes() != null) {
                    for (Lane lane : pool.getLanes()) {
                        if (lane.getFlowObjects() != null) {
                            for (FlowObject fo : lane.getFlowObjects()) {
                                if (fo.getComponent() != null &&
                                        fo.getComponent().getImplementation() != null) {
                                    Implementation impl = fo.getComponent().getImplementation();
                                    if (impl.getAttachedProcessId() != null) {
                                        ids.add(impl.getAttachedProcessId());
                                    }
                                    if (impl.getAttachedActivityId() != null) {
                                        ids.add(impl.getAttachedActivityId());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void findSubprocessIdsInDefinitions(Definitions definitions, Set<String> ids) {
        if (definitions.getProcess() != null) {
            collectCallActivities(definitions.getProcess().getFlowElements(), ids);
        }
        if (definitions.getGlobalUserTask() != null &&
                definitions.getGlobalUserTask().getImplementation() != null) {
            collectCallActivities(
                    definitions.getGlobalUserTask().getImplementation().getFlowElements(),
                    ids
            );
        }
    }

    private Set<String> encontrarSubprocessosEmServicoOuBpdLegado(Teamworks tw) {
        Set<String> ids = new HashSet<>();
        if (tw == null) return ids;

        // Cenário 1: Serviço (Humano com Coachflow ou Legado com <item>)
        if (tw.getProcess() != null) {
            if (tw.getProcess().getCoachflow() != null && tw.getProcess().getCoachflow().getDefinitions() != null) {
                GlobalUserTask gut = tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask();
                if (gut != null && gut.getImplementation() != null) {
                    collectCallActivities(gut.getImplementation().getFlowElements(), ids);
                }
            }
            if (tw.getProcess().getItems() != null) {
                tw.getProcess().getItems().stream()
                        .map(item -> item.getTwComponent())
                        .filter(Objects::nonNull)
                        .map(twc -> twc.getAttachedProcessRef())
                        .filter(ref -> ref != null && !ref.isEmpty())
                        .forEach(ids::add);
            }
        }
        // **NOVA LÓGICA**: Cenário 2: BPD Legado (sem <bpmn2Data>, mas com <bpd>)
        else if (tw.getBpd() != null && tw.getBpd().getBusinessProcessDiagram() != null) {
            tw.getBpd().getBusinessProcessDiagram().getPools().stream()
                    .filter(Objects::nonNull)
                    .flatMap(pool -> pool.getLanes() != null ? pool.getLanes().stream() : Stream.empty())
                    .filter(Objects::nonNull)
                    .flatMap(lane -> lane.getFlowObjects() != null ? lane.getFlowObjects().stream() : Stream.empty())
                    .filter(fo -> fo != null && fo.getComponent() != null && fo.getComponent().getImplementation() != null)
                    .map(FlowObject::getComponent)
                    .map(Component::getImplementation)
                    .forEach(impl -> { // <<< Itera sobre o objeto Implementation
                        if (impl.getAttachedActivityId() != null && !impl.getAttachedActivityId().isEmpty()) {
                            ids.add(impl.getAttachedActivityId());
                        }
                        if (impl.getAttachedProcessId() != null && !impl.getAttachedProcessId().isEmpty()) {
                            ids.add(impl.getAttachedProcessId()); // <<< Adiciona a nova verificação
                        }
                        if (impl.getEmbeddedProcessId() != null && !impl.getEmbeddedProcessId().isEmpty()) {
                            ids.add(impl.getEmbeddedProcessId());
                        }
                    });
        }
        return ids;
    }



    private void collectCallActivities(List<Object> elements, Set<String> ids) {
        if (elements == null) return;

        for (Object el : elements) {
            if (el instanceof CallActivity) {
                String calledElement = ((CallActivity) el).getCalledElement();
                if (calledElement != null && !calledElement.isEmpty()) {
                    ids.add(calledElement);
                }
            } else if (el instanceof SubProcess) {
                collectCallActivities(((SubProcess) el).getFlowElements(), ids);
            }
        }
    }

    // Utility methods
    public String getCleanId(String objectId) {
        if (objectId == null) return null;
        String normalized = objectId.replaceAll("\\s+", "").trim();
        return normalized.contains("/") ?
                normalized.substring(normalized.lastIndexOf('/') + 1) :
                normalized;
    }

    public Object getArtefatoDoCache(String objectId) {
        return cacheDeArtefatos.get(getCleanId(objectId));
    }

    public Map<String, Object> getCacheDeArtefatos() {
        return Collections.unmodifiableMap(cacheDeArtefatos);
    }

    // Rest of the class remains the same...
    // [Inclua os outros métodos auxiliares como findArtifactLocation, loadXmlFile, etc.]

    private void log(String message) {
        if (detailedLogging) {
            System.out.println(message);
        }
    }

    public void setDetailedLogging(boolean enabled) {
        this.detailedLogging = enabled;
    }

    // ArtifactLocation class
    public static class ArtifactLocation {
        public final PackageObject objectInfo;
        public final String filePath;
        public final String basePath;
        public final String toolkitName;

        public ArtifactLocation(PackageObject objectInfo, String filePath,
                                String basePath, String toolkitName) {
            this.objectInfo = objectInfo;
            this.filePath = filePath;
            this.basePath = basePath;
            this.toolkitName = toolkitName;
        }
    }

    private <T> T loadXmlFile(File xmlFile, Class<T> clazz) throws Exception {
        if (!xmlFile.exists()) {
            return null;
        }
        String xmlContent = new String(Files.readAllBytes(xmlFile.toPath()), StandardCharsets.UTF_8);
        if (xmlContent.startsWith("\uFEFF")) {
            xmlContent = xmlContent.substring(1);
        }
        Unmarshaller unmarshaller = getUniversalJaxbContext().createUnmarshaller();
        return clazz.cast(unmarshaller.unmarshal(new StringReader(xmlContent)));
    }

    private JAXBContext getJaxbContext() throws Exception {
        final String UNIVERSAL_CONTEXT_KEY = "UNIVERSAL_CONTEXT";
        if (!jaxbContexts.containsKey(UNIVERSAL_CONTEXT_KEY)) {
            Class<?>[] classes = ClassFinder.getClasses("br.com.danzeroum.bpmbaw.mapeadorxml.modelo");
            JAXBContext context = JAXBContext.newInstance(classes);
            jaxbContexts.put(UNIVERSAL_CONTEXT_KEY, context);
        }
        return jaxbContexts.get(UNIVERSAL_CONTEXT_KEY);
    }

    private JAXBContext getUniversalJaxbContext() throws Exception {
        if (universalJaxbContext == null) {
            System.out.println("[LOG-JAXB] Inicializando JAXBContext universal com lista explícita de classes...");
            universalJaxbContext = JAXBContext.newInstance(
                    // --- Raiz e Pacotes ---
                    Teamworks.class,
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package.class,

                    // --- Processo Legado (pacote 'process') ---
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process.class,
                    CoachFlow.class, Item.class, Link.class, ParameterMapping.class,
                    ProcessParameter.class, ProcessPrePost.class, ProcessVariable.class,
                    SwitchCondition.class, TWComponent.class, CoachNGBoundaryEvents.class,

                    // --- BPD Legado (pacote 'bpd') ---
                    Bpd.class, BusinessProcessDiagram.class, Pool.class, Lane.class,
                    FlowObject.class, Component.class, Implementation.class, Flow.class,
                    DiagramFlow.class, StartPoint.class, Note.class,

                    // --- BPMN Moderno (pacote 'bpmn' e 'bpmn.extension') ---
                    Definitions.class,
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process.class,
                    LaneSet.class, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Lane.class,
                    FlowNode.class, StartEvent.class, EndEvent.class, ScriptTask.class,
                    Task.class, FormTask.class, CallActivity.class, SubProcess.class,
                    ExclusiveGateway.class, SequenceFlow.class, IoSpecification.class,
                    DataInput.class, DataOutput.class, DataInputAssociation.class,
                    DataOutputAssociation.class, Assignment.class, From.class, To.class,
                    GlobalUserTask.class, UserTaskImplementation.class, ExtensionElements.class,
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.extension.BpdExtension.class,

                    // --- UI - Coaches (pacotes 'coach' e 'coachng') ---
                    CoachLayout.class,
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem.class,
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData.class,
                    Layout.class,
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem.class,
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.ConfigData.class,
                    ContentBoxContrib.class, CoachDefinition.class,

                    // --- UI - CoachView (pacote 'cv') ---
                    CoachView.class, ConfigOption.class, InlineScript.class,

                    // --- Dados e Suporte (outros pacotes) ---
                    TwClass.class, Definition.class, Property.class,
                    Participant.class, StandardMember.class,
                    ResourceBundleGroup.class,
                    EnvironmentVariableSet.class
            );
            System.out.println("[LOG-JAXB] JAXBContext universal criado com sucesso.");
        }
        return universalJaxbContext;
    }



    private <T> T loadXmlString(String xmlContent, Class<T> clazz) throws Exception {
        try {
            if (xmlContent.startsWith("\uFEFF")) xmlContent = xmlContent.substring(1);
            JAXBContext context = getUniversalJaxbContext();
            Unmarshaller unmarshaller = context.createUnmarshaller();
            T result = clazz.cast(unmarshaller.unmarshal(new StringReader(xmlContent)));
            //      logger.println("[LOG-JAXB] XML desserializado com sucesso para a classe: " + clazz.getSimpleName());
            return result;
        } catch (Exception e) {
            logger.println("[ERRO-JAXB] Falha ao desserializar XML para a classe: " + clazz.getSimpleName());
            logger.println(e.getMessage());
            // Para depuração, imprima o início do XML que falhou
            logger.println("Início do XML com problema: " + xmlContent.substring(0, Math.min(xmlContent.length(), 300)));
            throw e; // Lança a exceção para interromper o processo
        }
    }
    public ArtifactLocation findArtifactLocation(String objectId) {
        String normalizedId = normalizeId(objectId);
        if (normalizedId == null || normalizedId.isEmpty()) return null;
        if (normalizedId.startsWith("/")) normalizedId = normalizedId.substring(1);
        String searchObjectId = normalizedId;

        // Lógica para Toolkits
        if (normalizedId.contains("/")) {
            String[] parts = normalizedId.split("/");
            searchObjectId = parts[parts.length - 1];
            String dependencyPart = parts[0];
            if (mainManifest.getDependencies() != null) {
                for (Dependency dependency : mainManifest.getDependencies()) {
                    if (dependency.getId() != null && dependency.getId().endsWith(dependencyPart)) {
                        if (dependency.getSnapshot() == null) continue;
                        String snapshotId = dependency.getSnapshot().getId();
                        br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package toolkitManifest = loadManifest(snapshotId);
                        if (toolkitManifest != null) {
                            PackageObject objectInfo = findObjectInManifest(searchObjectId, toolkitManifest);
                            if (objectInfo != null) {
                                // --- INÍCIO DA CORREÇÃO PARA TOOLKITS ---
                                // Adiciona o subdiretório "objects" ao caminho do toolkit
                                String basePath = new File(new File(new File(rootDirectoryPath, "toolkits"), snapshotId), "objects").getPath();
                                // --- FIM DA CORREÇÃO PARA TOOLKITS ---
                                String toolkitName = dependency.getProject() != null ? dependency.getProject().getName() : "Nome não encontrado";
                                return new ArtifactLocation(objectInfo, objectInfo.getId() + ".xml", basePath, toolkitName);
                            }
                        }
                    }
                }
            }
        }

        // Lógica para o Projeto Principal (já corrigida anteriormente)
        PackageObject objectInfo = findObjectInManifest(searchObjectId, mainManifest);
        if (objectInfo != null) {
            String basePath = new File(rootDirectoryPath, "objects").getPath();
            return new ArtifactLocation(objectInfo, objectInfo.getId() + ".xml", basePath, null);
        }

        // Lógica de fallback para buscar em todos os toolkits
        if (mainManifest.getDependencies() != null) {
            for (Dependency dependency : mainManifest.getDependencies()) {
                if (dependency.getSnapshot() == null) continue;
                String snapshotId = dependency.getSnapshot().getId();
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package toolkitManifest = loadManifest(snapshotId);
                if (toolkitManifest != null) {
                    objectInfo = findObjectInManifest(searchObjectId, toolkitManifest);
                    if (objectInfo != null) {
                        // --- INÍCIO DA CORREÇÃO PARA TOOLKITS (FALLBACK) ---
                        // Adiciona o subdiretório "objects" ao caminho do toolkit
                        String basePath = new File(new File(new File(rootDirectoryPath, "toolkits"), snapshotId), "objects").getPath();
                        // --- FIM DA CORREÇÃO PARA TOOLKITS (FALLBACK) ---
                        String toolkitName = dependency.getProject() != null ? dependency.getProject().getName() : "Nome não encontrado";
                        return new ArtifactLocation(objectInfo, objectInfo.getId() + ".xml", basePath, toolkitName);
                    }
                }
            }
        }
        return null;
    }
    private br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package loadManifest(String snapshotId) {
        try {
            if (manifestCache.containsKey(snapshotId)) return manifestCache.get(snapshotId);
            File manifestFile = new File(new File(new File(rootDirectoryPath, "toolkits"), snapshotId), "META-INF/package.xml");
            if (!manifestFile.exists()) return null;
            br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package manifest = loadXmlFile(manifestFile, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package.class);
            if (manifest != null) manifestCache.put(snapshotId, manifest);
            return manifest;
        } catch (Exception e) { return null; }
    }
    private PackageObject findObjectInManifest(String objectId, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package manifest) {
        if (manifest == null || manifest.getObjects() == null) return null;
        return manifest.getObjects().stream().filter(o -> normalizeId(o.getId()).equals(objectId)).findFirst().orElse(null);
    }
    private String extractBpmn2Data(File ibmBpdFile) throws Exception {
        if (!ibmBpdFile.exists()) return null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc = factory.newDocumentBuilder().parse(ibmBpdFile);
        doc.getDocumentElement().normalize();
        NodeList bpdNodes = doc.getElementsByTagName("bpd");
        if (bpdNodes.getLength() == 0) bpdNodes = doc.getElementsByTagNameNS("*", "bpd");
        if (bpdNodes.getLength() > 0) {
            Node bpmn2DataNode = findChildNode(bpdNodes.item(0), "bpmn2Data");
            return (bpmn2DataNode != null) ? bpmn2DataNode.getTextContent().trim() : null;
        }
        return null;
    }
    private static Node findChildNode(Node parentNode, String nodeName) {
        NodeList childNodes = parentNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node currentNode = childNodes.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(currentNode.getLocalName())) {
                return currentNode;
            }
        }
        return null;
    }
    public void loadArtefatoSeNaoExistir(String artifactId) {
        if (cacheDeArtefatos.containsKey(artifactId)) {
            return;
        }
        loadProcessInMemory(artifactId);
    }
    public void loadDependentArtifactIfNotExists(String artifactId) {
        if (artifactId == null || artifactId.trim().isEmpty()) {
            return;
        }
        String cacheKey = getCleanId(artifactId);
        if (cacheKey != null && !cacheDeArtefatos.containsKey(cacheKey)) {
            carregarArtefatoRecursivamente(artifactId, new HashSet<>());
        }
    }
    public String normalizeId(String id) {
        if (id == null) return null;
        return id.replaceAll("\\s+", "").trim();
    }



}