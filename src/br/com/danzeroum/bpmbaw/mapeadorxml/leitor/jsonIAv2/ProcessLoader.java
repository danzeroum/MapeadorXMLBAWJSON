package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.FlowObject;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Component;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Pool;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

/**
 * (VERSÃO CORRIGIDA)
 * Classe especialista responsável por carregar um processo BAW e todas as suas
 * dependências (subprocessos, serviços) para a memória, incluindo suporte
 * completo para a descoberta de subprocessos em BPDs legados.
 */
public class ProcessLoader {

    private final String rootDirectoryPath;
    private final PrintWriter logger;

    private final Map<String, Object> cacheDeArtefatos = new HashMap<>();
    private final Map<String, JAXBContext> jaxbContexts = new HashMap<>();
    private final br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package mainManifest;
    private final Map<String, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package> manifestCache = new HashMap<>();
    private JAXBContext universalJaxbContext; // Contexto único e robusto


    public ProcessLoader(String twxDirectoryPath, PrintWriter logger) throws Exception {
        this.rootDirectoryPath = twxDirectoryPath;
        this.logger = logger;
        File mainManifestFile = new File(new File(twxDirectoryPath, "META-INF"), "package.xml");
        this.mainManifest = loadXmlFile(mainManifestFile, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package.class);
        if (this.mainManifest == null) {
            throw new IllegalArgumentException("Arquivo package.xml principal não encontrado em META-INF.");
        }
        manifestCache.put("main", this.mainManifest);

    }

    public Map<String, Object> loadProcessInMemory(String objectId) {
        logger.println("[LOG] Iniciando fase de carregamento para o ID raiz: " + objectId);
        carregarArtefatoRecursivamente(objectId, new HashSet<>());
        logger.println("[LOG] Carregamento concluído. " + cacheDeArtefatos.size() + " artefatos em memória."+ cacheDeArtefatos.toString());
        return Collections.unmodifiableMap(cacheDeArtefatos);
    }

    public Map<String, Object> getCacheDeArtefatos() {
        return Collections.unmodifiableMap(cacheDeArtefatos);
    }

    public Object getArtefatoDoCache(String objectId) {
        // A chave de busca sempre será o ID limpo.
        String cleanId = normalizeId(objectId).contains("/") ? normalizeId(objectId).substring(normalizeId(objectId).lastIndexOf('/') + 1) : normalizeId(objectId);
        return cacheDeArtefatos.get(cleanId);
    }


    public String getCleanId(String objectId) {
        if (objectId == null) return null;
        String normalized = objectId.replaceAll("\\s+", "").trim();
        return normalized.contains("/") ? normalized.substring(normalized.lastIndexOf('/') + 1) : normalized;
    }

    /**
     * NOVO MÉTODO PÚBLICO
     * Carrega uma lista específica de artefatos e suas dependências na memória.
     * @param objectIds A lista de IDs de artefatos a serem carregados.
     * @return Um mapa somente leitura do cache de artefatos preenchido.
     */
    public Map<String, Object> loadAllProcessesInMemory(List<String> objectIds) {
        logger.println("[LOG] Iniciando fase de carregamento para múltiplos IDs raiz...");
        Set<String> visitedIds = new HashSet<>();
        for (String objectId : objectIds) {
            carregarArtefatoRecursivamente(objectId, visitedIds);
        }
        logger.println("[LOG] Carregamento concluído. " + cacheDeArtefatos.size() + " artefatos em memória.");
        return Collections.unmodifiableMap(cacheDeArtefatos);
    }

    // <<< NOVO MÉTODO ADICIONADO AQUI >>>
    /**
     * Retorna o manifesto principal (package.xml) do projeto.
     * @return O objeto Package desserializado.
     */
    public br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package getMainManifest() {
        return mainManifest;
    }

    private void carregarArtefatoRecursivamente(String objectId, Set<String> visitedIds) {
        String cleanId = getCleanId(objectId);
        if (cleanId == null || !visitedIds.add(cleanId)) {
            return;
        }

        ArtifactLocation location = findArtifactLocation(objectId);
        if (location == null) {
           // System.err.println("[LOADER-AVISO] Artefato dependente não encontrado: " + objectId);
            return;
        }

        String cacheKey = getCleanId(location.objectInfo.getId());

        if (cacheDeArtefatos.containsKey(cacheKey)) {
            return;
        }
        if ("participant".equalsIgnoreCase(location.objectInfo.getType())) {
            System.out.println("[LOG-LOADER-PARTICIPANT] Encontrado artefato de participante para carregar: " + location.objectInfo.getName() + " (ID: " + cacheKey + ")");
        }
        System.out.println("[LOADER] Processando e colocando no cache: " + location.objectInfo.getName() + " (Chave: " + cacheKey + ") -> "+location.objectInfo.getType());

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
    /**
     * **MÉTODO CORRIGIDO**
     * Analisa um objeto Teamworks para encontrar todos os IDs de subprocessos que ele chama.
     * Agora inclui a lógica para inspecionar BPDs legados (sem <bpmn2Data>).
     */
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

    public String normalizeId(String id) {
        if (id == null) return null;
        return id.replaceAll("\\s+", "").trim();
    }

    /**
     * **MÉTODO CORRIGIDO**
     * Localiza um artefato no projeto principal ou em um dos toolkits,
     * tratando corretamente IDs compostos que contêm uma barra ("/").
     *
     * @param objectId O ID do artefato, que pode ser simples (ex: "1....") ou
     * composto (ex: "94cf6f.../1.cb13...")
     * @return Um objeto ArtifactLocation contendo as informações e o caminho
     * base para o artefato, ou null se não for encontrado.
     */
    public ArtifactLocation findArtifactLocation(String objectId) {
        String normalizedId = normalizeId(objectId);
        if (normalizedId == null || normalizedId.isEmpty()) {
            return null;
        }
        // Remove a barra inicial ANTES de qualquer outra lógica
        if (normalizedId.startsWith("/")) {
            normalizedId = normalizedId.substring(1);
        }


        String searchObjectId = normalizedId;

        if (normalizedId.contains("/")) {
            String[] parts = normalizedId.split("/");
            if (parts.length < 2 || parts[0].isEmpty()) {
                searchObjectId = parts[parts.length - 1];

               // System.out.println("1 - searchObjectId: "+searchObjectId);
            } else {
                String dependencyPart = parts[0];
                String objectPart = parts[1];
                searchObjectId = objectPart;
             //   System.out.println("2 - searchObjectId: "+searchObjectId);
                if (mainManifest.getDependencies() != null) {
                    for (Dependency dependency : mainManifest.getDependencies()) {
                        if (dependency.getId() != null && dependency.getId().endsWith(dependencyPart)) {
                            if (dependency.getSnapshot() == null) continue;
                            String snapshotId = dependency.getSnapshot().getId();
                          //  System.out.println("3 - snapshotId: "+snapshotId);
                            br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package toolkitManifest = loadManifest(snapshotId);
                            if (toolkitManifest != null) {
                          //      System.out.println("4 - toolkitManifest: "+toolkitManifest.toString());
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

    /*
    public ArtifactLocation findArtifactLocation(String objectId) {
        String cleanId = normalizeId(objectId).contains("/") ? normalizeId(objectId).substring(normalizeId(objectId).lastIndexOf('/') + 1) : objectId;
        PackageObject objectInfo = findObjectInManifest(cleanId, mainManifest);
        if (objectInfo != null) return new ArtifactLocation(objectInfo, objectInfo.getId() + ".xml", rootDirectoryPath);
        if (mainManifest.getDependencies() != null) {
            for (Dependency dependency : mainManifest.getDependencies()) {
                if (dependency.getSnapshot() == null) continue;
                String snapshotId = dependency.getSnapshot().getId();
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package toolkitManifest = loadManifest(snapshotId);
                if (toolkitManifest != null) {
                    objectInfo = findObjectInManifest(cleanId, toolkitManifest);
                    if (objectInfo != null) return new ArtifactLocation(objectInfo, objectInfo.getId() + ".xml", new File(new File(rootDirectoryPath, "toolkits"), snapshotId).getPath());
                }
            }
        }
        return null;
    }
*/
    private JAXBContext getJaxbContext() throws Exception {
        final String UNIVERSAL_CONTEXT_KEY = "UNIVERSAL_CONTEXT";
        if (!jaxbContexts.containsKey(UNIVERSAL_CONTEXT_KEY)) {
            Class<?>[] classes = ClassFinder.getClasses("br.com.danzeroum.bpmbaw.mapeadorxml.modelo");
            JAXBContext context = JAXBContext.newInstance(classes);
            jaxbContexts.put(UNIVERSAL_CONTEXT_KEY, context);
        }
        return jaxbContexts.get(UNIVERSAL_CONTEXT_KEY);
    }
    /**
     * MÉTODO ATUALIZADO
     * Cria e gerencia um contexto JAXB universal para evitar erros de parsing.
     */
    private JAXBContext getUniversalJaxbContext() throws Exception {
        if (universalJaxbContext == null) {
            logger.println("[LOG] Criando JAXBContext universal pela primeira vez...");
            // Lista explícita de todas as classes de modelo que o JAXB precisa conhecer
            universalJaxbContext = JAXBContext.newInstance(
                    Teamworks.class, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package.class,
                    Definitions.class, Process.class, TwClass.class, CoachView.class, // Classes Raiz
                    // Adicione outras classes de modelo aqui se necessário
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process.class
            );
            logger.println("[LOG] JAXBContext universal criado com sucesso.");
        }
        return universalJaxbContext;
    }


/*    private <T> T loadXmlFile(File xmlFile, Class<T> clazz) throws Exception {
        String cacheKey = xmlFile.getAbsolutePath();
        if (cacheDeArtefatos.containsKey(cacheKey)) return clazz.cast(cacheDeArtefatos.get(cacheKey));
        String xmlContent = new String(Files.readAllBytes(xmlFile.toPath()), StandardCharsets.UTF_8);
        if (xmlContent.startsWith("\uFEFF")) xmlContent = xmlContent.substring(1);
        JAXBContext context = getJaxbContext();
        Unmarshaller unmarshaller = context.createUnmarshaller();
        T result = clazz.cast(unmarshaller.unmarshal(new StringReader(xmlContent)));
        // **IMPORTANTE**: Armazena o objeto desserializado, não o conteúdo do arquivo.
        // cacheDeArtefatos.put(cacheKey, result); // Isso será feito no método de carregamento principal.
        return result;
    }*/

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

    public static class ArtifactLocation {
        public final PackageObject objectInfo;
        public final String filePath;
        final String basePath;
        public final String toolkitName;

        ArtifactLocation(PackageObject objectInfo, String filePath, String basePath, String toolkitName) {
            this.objectInfo = objectInfo; this.filePath = filePath; this.basePath = basePath; this.toolkitName = toolkitName;
        }
    }
    /**
     * Carrega um artefato específico para o cache se ele ainda não estiver presente.
     * Este método é projetado para ser chamado externamente, por exemplo, pelo CoachNavigator
     * quando descobre uma nova Coach View.
     * @param artifactId O ID do artefato (ex: Coach View ID) a ser carregado.
     */
    public void loadArtefatoSeNaoExistir(String artifactId) {
        if (cacheDeArtefatos.containsKey(artifactId)) {
            return; // Já está no cache, não faz nada.
        }
       // System.out.println("[LOG] Descoberta dinâmica de Coach View. Carregando: " + artifactId);
        loadProcessInMemory(artifactId); // Usa a lógica de carregamento existente.
    }

    /**
     * NOVO MÉTODO (RENOMEADO E SEGURO)
     * Carrega um artefato dependente (como um TwClass ou CoachView) no cache se ele ainda não estiver lá.
     * Este método é seguro para ser chamado por outros navegadores.
     * @param artifactId O ID do artefato a ser carregado.
     */
    public void loadDependentArtifactIfNotExists(String artifactId) {
        if (artifactId == null || artifactId.trim().isEmpty()) {
            return;
        }
        String cacheKey = getCleanId(artifactId);
        if (cacheKey != null && !cacheDeArtefatos.containsKey(cacheKey)) {
            System.out.println("[LOG] Dependência '" + artifactId + "' não encontrada no cache. Carregando dinamicamente...");
            // Usa o método de carregamento principal com um novo Set de "visitados" para este carregamento pontual.
            carregarArtefatoRecursivamente(artifactId, new HashSet<>());
        }
    }


}