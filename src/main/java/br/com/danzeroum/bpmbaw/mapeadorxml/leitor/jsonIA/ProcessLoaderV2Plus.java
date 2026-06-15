package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.CoachView;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Dependency;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.PackageObject;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass;
import br.com.danzeroum.bpmbaw.mapeadorxml.util.ClassFinder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessLoaderV2Plus {

    private final String rootDirectoryPath;
    private final PrintWriter logger;
    private final Map<String, Object> cacheDeArtefatos = new HashMap<>();
    private final Map<String, JAXBContext> jaxbContexts = new HashMap<>();
    private final br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package mainManifest;
    private final Map<String, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package> manifestCache = new HashMap<>();
    private JAXBContext universalJaxbContext;
    private boolean detailedLogging = false;

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

    public ProcessLoaderV2Plus(String twxDirectoryPath) throws Exception {
        this(twxDirectoryPath, new PrintWriter(System.out, true));
    }

    // Método do ProcessLoader original
    public Map<String, Object> loadProcessInMemory(String objectId) {
        logger.println("[LOG] Iniciando fase de carregamento para o ID raiz: " + objectId);
        carregarArtefatoRecursivamente(objectId, new HashSet<>());
        logger.println("[LOG] Carregamento concluído. " + cacheDeArtefatos.size() + " artefatos em memória." + cacheDeArtefatos.toString());
        return Collections.unmodifiableMap(cacheDeArtefatos);
    }

    // Método do ProcessLoader original
    public Map<String, Object> loadAllProcessesInMemory(List<String> objectIds) {
        logger.println("[LOG] Iniciando fase de carregamento para múltiplos IDs raiz...");
        Set<String> visitedIds = new HashSet<>();
        for (String objectId : objectIds) {
            carregarArtefatoRecursivamente(objectId, visitedIds);
        }
        logger.println("[LOG] Carregamento concluído. " + cacheDeArtefatos.size() + " artefatos em memória.");
        return Collections.unmodifiableMap(cacheDeArtefatos);
    }

    // Método do ProcessLoaderV2Plus
    public void carregarTodosOsArtefatosDoProjeto() {
        System.out.println("[LOG-LOADER] Preenchendo cache com todos os artefatos do projeto e toolkits...");

        if (mainManifest != null && mainManifest.getObjects() != null) {
            for (PackageObject obj : mainManifest.getObjects()) {
                loadArtifact(obj.getId());
            }
        }

        if (mainManifest != null && mainManifest.getDependencies() != null) {
            for (Dependency dep : mainManifest.getDependencies()) {
                if (dep.getSnapshot() != null) {
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package toolkitManifest = loadManifest(dep.getSnapshot().getId());
                    if (toolkitManifest != null && toolkitManifest.getObjects() != null) {
                        System.out.println("  -> Carregando " + toolkitManifest.getObjects().size() + " artefatos do toolkit: " + dep.getProject().getName());
                        for (PackageObject obj : toolkitManifest.getObjects()) {
                            loadArtifact(dep.getSnapshot().getId() + "/" + obj.getId());
                        }
                    }
                }
            }
        }
        System.out.println("[LOG-LOADER] Cache preenchido. Total de artefatos em memória: " + cacheDeArtefatos.size());
    }

    private void carregarArtefatoRecursivamente(String objectId, Set<String> visitedIds) {
        String cleanId = getCleanId(objectId);
        if (cleanId == null || !visitedIds.add(cleanId)) {
            return;
        }

        ArtifactLocation location = findArtifactLocation(objectId);
        if (location == null) {
            return;
        }

        String cacheKey = getCleanId(location.objectInfo.getId());

        if (cacheDeArtefatos.containsKey(cacheKey)) {
            return;
        }

        if ("participant".equalsIgnoreCase(location.objectInfo.getType())) {
            System.out.println("[LOG-LOADER-PARTICIPANT] Encontrado artefato de participante para carregar: " + location.objectInfo.getName() + " (ID: " + cacheKey + ")");
        }
        System.out.println("[LOADER] Processando e colocando no cache: " + location.objectInfo.getName() + " (Chave: " + cacheKey + ") -> " + location.objectInfo.getType());

        try {
            File targetFile = new File(new File(location.basePath, "objects"), location.filePath);
            String bpmnContent = extractBpmn2Data(targetFile);

            Set<String> subprocessosIds;

            if (bpmnContent != null && !bpmnContent.isEmpty()) {
                JAXBContext context = getJaxbContext();
                Unmarshaller unmarshaller = context.createUnmarshaller();
                Definitions definitions = (Definitions) unmarshaller.unmarshal(new StringReader(bpmnContent));
                cacheDeArtefatos.put(cacheKey, definitions);
                subprocessosIds = new HashSet<>();
                if (definitions.getProcess() != null) {
                    collectCallActivities(definitions.getProcess().getFlowElements(), subprocessosIds);
                }
            } else {
                Teamworks tw = loadXmlFile(targetFile, Teamworks.class);
                if (tw == null) return;
                cacheDeArtefatos.put(cacheKey, tw);
                subprocessosIds = encontrarSubprocessosEmServicoOuBpdLegado(tw);
            }

            for (String subId : subprocessosIds) {
                carregarArtefatoRecursivamente(subId, visitedIds);
            }
        } catch (Exception e) {
            System.err.println("[LOADER-ERRO] Falha ao carregar " + objectId + ": " + e.getMessage());
            e.printStackTrace(logger);
        }
    }

    // Método alternativo do ProcessLoaderV2Plus
    private Object loadArtifact(String artifactId) {
        try {
            String cleanId = getCleanId(artifactId);

            if (cacheDeArtefatos.containsKey(cleanId)) {
                return cacheDeArtefatos.get(cleanId);
            }

            ArtifactLocation location = findArtifactLocation(artifactId);
            if (location == null) {
                return null;
            }

            File targetFile = new File(location.basePath, location.filePath);

            System.out.println(
                    String.format("[LOG-CARREGAMENTO] Artefato Carregado: ID=%s, Nome='%s', Tipo=%s",
                            location.objectInfo.getId(),
                            location.objectInfo.getName(),
                            location.objectInfo.getType()
                    )
            );

            String xmlContent = new String(Files.readAllBytes(targetFile.toPath()), StandardCharsets.UTF_8);
            if (xmlContent.startsWith("\uFEFF")) {
                xmlContent = xmlContent.substring(1);
            }

            JAXBContext context = getUniversalJaxbContext();
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object result = unmarshaller.unmarshal(new StringReader(xmlContent));

            if (result instanceof Teamworks) {
                Teamworks tw = (Teamworks) result;
                if (tw.getBpd() != null && tw.getBpd().getBpmn2Data() != null && !tw.getBpd().getBpmn2Data().isEmpty()) {
                    try {
                        Definitions definitions = (Definitions) unmarshaller.unmarshal(new StringReader(tw.getBpd().getBpmn2Data()));
                        cacheDeArtefatos.put(cleanId, definitions);
                        return definitions;
                    } catch (Exception e) {
                        // Fallback para o Teamworks se o BPMN2 falhar
                    }
                }
                cacheDeArtefatos.put(cleanId, tw);
                return tw;
            }

            cacheDeArtefatos.put(cleanId, result);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    // Método do ProcessLoader original
    public void loadArtefatoSeNaoExistir(String artifactId) {
        if (cacheDeArtefatos.containsKey(artifactId)) {
            return;
        }
        loadProcessInMemory(artifactId);
    }

    // Método do ProcessLoader original
    public void loadDependentArtifactIfNotExists(String artifactId) {
        if (artifactId == null || artifactId.trim().isEmpty()) {
            return;
        }
        String cacheKey = getCleanId(artifactId);
        if (cacheKey != null && !cacheDeArtefatos.containsKey(cacheKey)) {
            System.out.println("[LOG] Dependência '" + artifactId + "' não encontrada no cache. Carregando dinamicamente...");
            carregarArtefatoRecursivamente(artifactId, new HashSet<>());
        }
    }

    private Set<String> encontrarSubprocessosEmServicoOuBpdLegado(Teamworks tw) {
        Set<String> ids = new HashSet<>();
        if (tw == null) return ids;

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
        } else if (tw.getBpd() != null && tw.getBpd().getBusinessProcessDiagram() != null) {
            tw.getBpd().getBusinessProcessDiagram().getPools().stream()
                    .filter(Objects::nonNull)
                    .flatMap(pool -> pool.getLanes() != null ? pool.getLanes().stream() : Stream.empty())
                    .filter(Objects::nonNull)
                    .flatMap(lane -> lane.getFlowObjects() != null ? lane.getFlowObjects().stream() : Stream.empty())
                    .filter(fo -> fo != null && fo.getComponent() != null && fo.getComponent().getImplementation() != null)
                    .map(FlowObject::getComponent)
                    .map(Component::getImplementation)
                    .forEach(impl -> {
                        if (impl.getAttachedActivityId() != null && !impl.getAttachedActivityId().isEmpty()) {
                            ids.add(impl.getAttachedActivityId());
                        }
                        if (impl.getAttachedProcessId() != null && !impl.getAttachedProcessId().isEmpty()) {
                            ids.add(impl.getAttachedProcessId());
                        }
                        if (impl.getEmbeddedProcessId() != null && !impl.getEmbeddedProcessId().isEmpty()) {
                            ids.add(impl.getEmbeddedProcessId());
                        }
                    });
        }
        return ids;
    }

    // Método unificado que funciona com ambas as abordagens
    private Set<String> findSubprocessIds(Object artifact) {
        Set<String> ids = new HashSet<>();
        if (artifact instanceof Definitions) {
            Process bpmnProcess = ((Definitions) artifact).getProcess();
            if (bpmnProcess != null) {
                collectCallActivities(bpmnProcess.getFlowElements(), ids);
            }
        } else if (artifact instanceof Teamworks) {
            ids.addAll(encontrarSubprocessosEmServicoOuBpdLegado((Teamworks) artifact));
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

    public ArtifactLocation findArtifactLocation(String objectId) {
        String normalizedId = normalizeId(objectId);
        if (normalizedId == null || normalizedId.isEmpty()) {
            return null;
        }

        if (normalizedId.startsWith("/")) {
            normalizedId = normalizedId.substring(1);
        }

        String searchObjectId = normalizedId;

        if (normalizedId.contains("/")) {
            String[] parts = normalizedId.split("/");
            if (parts.length < 2 || parts[0].isEmpty()) {
                searchObjectId = parts[parts.length - 1];
            } else {
                String dependencyPart = parts[0];
                String objectPart = parts[1];
                searchObjectId = objectPart;
                if (mainManifest.getDependencies() != null) {
                    for (Dependency dependency : mainManifest.getDependencies()) {
                        if (dependency.getId() != null && dependency.getId().endsWith(dependencyPart)) {
                            if (dependency.getSnapshot() == null) continue;
                            String snapshotId = dependency.getSnapshot().getId();
                            br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package toolkitManifest = loadManifest(snapshotId);
                            if (toolkitManifest != null) {
                                PackageObject objectInfo = findObjectInManifest(objectPart, toolkitManifest);
                                if (objectInfo != null) {
                                    String basePath = new File(new File(rootDirectoryPath, "toolkits"), snapshotId).getPath();
                                    String toolkitName = dependency.getProject() != null ? dependency.getProject().getName() : "Nome não encontrado";
                                    return new ArtifactLocation(objectInfo, objectInfo.getId() + ".xml", basePath, toolkitName);
                                }
                            }
                        }
                    }
                }
            }
        }

        PackageObject objectInfo = findObjectInManifest(searchObjectId, mainManifest);
        if (objectInfo != null) {
            return new ArtifactLocation(objectInfo, objectInfo.getId() + ".xml", rootDirectoryPath, null);
        }

        if (mainManifest.getDependencies() != null) {
            for (Dependency dependency : mainManifest.getDependencies()) {
                if (dependency.getSnapshot() == null) continue;
                String snapshotId = dependency.getSnapshot().getId();
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package toolkitManifest = loadManifest(snapshotId);
                if (toolkitManifest != null) {
                    objectInfo = findObjectInManifest(searchObjectId, toolkitManifest);
                    if (objectInfo != null) {
                        String basePath = new File(new File(rootDirectoryPath, "toolkits"), snapshotId).getPath();
                        String toolkitName = dependency.getProject() != null ? dependency.getProject().getName() : "Nome não encontrado";
                        return new ArtifactLocation(objectInfo, objectInfo.getId() + ".xml", basePath, toolkitName);
                    }
                }
            }
        }

        return null;
    }

    // Método do ProcessLoader original
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
            logger.println("[LOG] Criando JAXBContext universal pela primeira vez...");
            Class<?>[] classes = ClassFinder.getClasses("br.com.danzeroum.bpmbaw.mapeadorxml.modelo");
            universalJaxbContext = JAXBContext.newInstance(classes);
            logger.println("[LOG] JAXBContext universal criado com sucesso.");
        }
        return universalJaxbContext;
    }

    private <T> T loadXmlFile(File xmlFile, Class<T> clazz) throws Exception {
        if (!xmlFile.exists()) {
            logger.println("[ERRO-IO] Tentativa de ler um arquivo que não existe: " + xmlFile.getAbsolutePath());
            return null;
        }
        String xmlContent = new String(Files.readAllBytes(xmlFile.toPath()), StandardCharsets.UTF_8);
        return loadXmlString(xmlContent, clazz);
    }

    private <T> T loadXmlString(String xmlContent, Class<T> clazz) throws Exception {
        try {
            if (xmlContent.startsWith("\uFEFF")) xmlContent = xmlContent.substring(1);
            JAXBContext context = getUniversalJaxbContext();
            Unmarshaller unmarshaller = context.createUnmarshaller();
            T result = clazz.cast(unmarshaller.unmarshal(new StringReader(xmlContent)));
            return result;
        } catch (Exception e) {
            logger.println("[ERRO-JAXB] Falha ao desserializar XML para a classe: " + clazz.getSimpleName());
            logger.println(e.getMessage());
            logger.println("Início do XML com problema: " + xmlContent.substring(0, Math.min(xmlContent.length(), 300)));
            throw e;
        }
    }

    private br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package loadManifest(String snapshotId) {
        try {
            if (manifestCache.containsKey(snapshotId)) return manifestCache.get(snapshotId);
            File manifestFile = new File(new File(new File(rootDirectoryPath, "toolkits"), snapshotId), "META-INF/package.xml");
            if (!manifestFile.exists()) return null;
            br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package manifest = loadXmlFile(manifestFile, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package.class);
            if (manifest != null) manifestCache.put(snapshotId, manifest);
            return manifest;
        } catch (Exception e) {
            return null;
        }
    }

    private PackageObject findObjectInManifest(String objectId, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package manifest) {
        if (manifest == null || manifest.getObjects() == null) return null;
        return manifest.getObjects().stream()
                .filter(o -> normalizeId(o.getId()).equals(objectId))
                .findFirst()
                .orElse(null);
    }

    // Método do ProcessLoader original
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

    public String normalizeId(String id) {
        if (id == null) return null;
        return id.replaceAll("\\s+", "").trim();
    }

    public String getCleanId(String objectId) {
        if (objectId == null) return null;
        String normalized = objectId.replaceAll("\\s+", "").trim();
        return normalized.contains("/") ?
                normalized.substring(normalized.lastIndexOf('/') + 1) :
                normalized;
    }

    public Map<String, Object> getCacheDeArtefatos() {
        return Collections.unmodifiableMap(cacheDeArtefatos);
    }

    public Object getArtefatoDoCache(String objectId) {
        String cleanId = normalizeId(objectId).contains("/") ?
                normalizeId(objectId).substring(normalizeId(objectId).lastIndexOf('/') + 1) :
                normalizeId(objectId);
        return cacheDeArtefatos.get(cleanId);
    }

    // Método do ProcessLoader original
    public br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package getMainManifest() {
        return mainManifest;
    }

    private void log(String message) {
        if (detailedLogging && logger != null) {
            logger.println(message);
        } else if (detailedLogging) {
            System.out.println(message);
        }
    }

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
}