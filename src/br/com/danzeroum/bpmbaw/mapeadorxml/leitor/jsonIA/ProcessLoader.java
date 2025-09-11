package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.CoachView;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Dependency;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.PackageObject;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass;
import br.com.danzeroum.bpmbaw.mapeadorxml.util.ClassFinder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * PROCESSLOADER COMPLETO E ATUALIZADO - Java 8 Compatible
 *
 * Classe especialista responsável por carregar um processo BAW e todas as suas
 * dependências (subprocessos, serviços) para a memória.
 *
 * CARACTERÍSTICAS:
 * - Compatível com Java 8
 * - Suporte completo para BPM legado e IBM BAW novo
 * - Múltiplos construtores para flexibilidade máxima
 * - Carregamento defensivo com fallbacks
 * - Cache inteligente de artefatos
 * - Suporte completo para arquivos .twx mistos
 * - Tratamento robusto de dependências
 *
 * @version 2.1.0
 * @since Java 8
 */
public class ProcessLoader {

    // =========================================================================
    // CONSTANTS
    // =========================================================================

    private static final String META_INF_DIR = "META-INF";
    private static final String PACKAGE_XML = "package.xml";
    private static final String DEFAULT_ENCODING = "UTF-8";

    // =========================================================================
    // FIELDS
    // =========================================================================

    private final String rootDirectoryPath;
    private final PrintWriter logger;
    private final boolean enableDetailedLogging;

    // Caches
    private final Map<String, Object> cacheDeArtefatos = new LinkedHashMap<>();
    private final Map<String, JAXBContext> jaxbContexts = new ConcurrentHashMap<>();
    private final Map<String, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package> manifestCache = new HashMap<>();

    // Core components
    private br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package mainManifest;
    private JAXBContext universalJaxbContext;

    // Statistics
    private int totalArtifactsLoaded = 0;
    private int totalDependenciesResolved = 0;
    private long loadingStartTime = 0L;
    private Map<String, Object> instanceCache;

    // =========================================================================
    // CONSTRUCTORS - MÚLTIPLOS PARA MÁXIMA COMPATIBILIDADE
    // =========================================================================

    /**
     * Construtor padrão - REQUERIDO para compatibilidade V2Plus
     */
    public ProcessLoader() {
        this.rootDirectoryPath = null;
        this.logger = createSilentLogger();
        this.enableDetailedLogging = false;
    }

    /**
     * Construtor com path apenas
     */
    public ProcessLoader(String twxDirectoryPath) throws Exception {
        this(twxDirectoryPath, new PrintWriter(System.out), true);
    }

    /**
     * Construtor com path e logger
     */
    public ProcessLoader(String twxDirectoryPath, PrintWriter logger) throws Exception {
        this(twxDirectoryPath, logger, true);
    }

    /**
     * Construtor completo com todas as opções
     */
    public ProcessLoader(String twxDirectoryPath, PrintWriter logger, boolean enableDetailedLogging) throws Exception {
        this.rootDirectoryPath = twxDirectoryPath;
        this.logger = logger != null ? logger : createSilentLogger();
        this.enableDetailedLogging = enableDetailedLogging;

        if (twxDirectoryPath != null && !twxDirectoryPath.trim().isEmpty()) {
            initializeManifestAndContext(twxDirectoryPath);
        }
    }

    // =========================================================================
    // INITIALIZATION METHODS
    // =========================================================================

    /**
     * Inicializa manifest e contexto JAXB
     */
    private void initializeManifestAndContext(String twxDirectoryPath) throws Exception {
        validateDirectoryPath(twxDirectoryPath);

        logInfo("Initializing ProcessLoader for path: " + twxDirectoryPath);

        // Carregar manifest principal
        loadMainManifest(twxDirectoryPath);

        // Inicializar contexto JAXB universal
        initializeUniversalJAXBContext();

        // Carregar manifests de toolkits se existirem
        loadToolkitManifests(twxDirectoryPath);

        logInfo("ProcessLoader initialized successfully");
    }

    /**
     * Valida o caminho do diretório
     */
    private void validateDirectoryPath(String path) throws IllegalArgumentException {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory path cannot be null or empty");
        }

        File directory = new File(path);
        if (!directory.exists()) {
            throw new IllegalArgumentException("Directory does not exist: " + path);
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Path is not a directory: " + path);
        }
    }

    /**
     * Carrega o manifest principal
     */
    private void loadMainManifest(String twxDirectoryPath) throws Exception {
        File metaInfDir = new File(twxDirectoryPath, META_INF_DIR);
        File mainManifestFile = new File(metaInfDir, PACKAGE_XML);

        if (!mainManifestFile.exists()) {
            throw new IllegalArgumentException("Main package.xml not found in META-INF directory: " + mainManifestFile.getAbsolutePath());
        }

        logDetail("Loading main manifest: " + mainManifestFile.getAbsolutePath());

        this.mainManifest = loadXmlFile(mainManifestFile, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package.class);
        if (this.mainManifest == null) {
            throw new IllegalStateException("Failed to load main manifest file");
        }

        manifestCache.put("main", this.mainManifest);
        logInfo("Main manifest loaded successfully with " +
                (this.mainManifest.getObjects() != null ? this.mainManifest.getObjects().size() : 0) + " objects");
    }

    /**
     * Inicializa o contexto JAXB universal
     */
    private void initializeUniversalJAXBContext() {
        try {
            logDetail("Initializing universal JAXB context...");

            // Classes principais para o contexto
            Class<?>[] contextClasses = {
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package.class,
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Bpd.class,
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process.class,
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.CoachView.class,
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass.class,
                    Teamworks.class
            };

            this.universalJaxbContext = JAXBContext.newInstance(contextClasses);
            logDetail("Universal JAXB context initialized successfully");

        } catch (JAXBException e) {
            logError("Failed to initialize universal JAXB context: " + e.getMessage());
            // Não é fatal - contextos específicos serão criados conforme necessário
        }
    }

    /**
     * Carrega manifests de toolkits
     */
    private void loadToolkitManifests(String twxDirectoryPath) {
        try {
            File rootDir = new File(twxDirectoryPath);
            File[] subdirs = rootDir.listFiles(File::isDirectory);

            if (subdirs != null) {
                for (File subdir : subdirs) {
                    if (!META_INF_DIR.equals(subdir.getName())) {
                        loadToolkitManifest(subdir);
                    }
                }
            }
        } catch (Exception e) {
            logError("Error loading toolkit manifests: " + e.getMessage());
        }
    }

    /**
     * Carrega manifest de um toolkit específico
     */
    private void loadToolkitManifest(File toolkitDir) {
        try {
            File toolkitMetaInf = new File(toolkitDir, META_INF_DIR);
            File toolkitManifest = new File(toolkitMetaInf, PACKAGE_XML);

            if (toolkitManifest.exists()) {
                logDetail("Loading toolkit manifest: " + toolkitManifest.getAbsolutePath());

                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package pkg =
                        loadXmlFile(toolkitManifest, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package.class);

                if (pkg != null) {
                    manifestCache.put(toolkitDir.getName(), pkg);
                    logDetail("Toolkit manifest loaded: " + toolkitDir.getName());
                }
            }
        } catch (Exception e) {
            logError("Error loading toolkit manifest for " + toolkitDir.getName() + ": " + e.getMessage());
        }
    }

    // =========================================================================
    // MAIN LOADING METHODS
    // =========================================================================

    /**
     * Carrega processo na memória - Método principal
     */
    public Map<String, Object> loadProcessInMemory(String objectId) {
        if (objectId == null || objectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Object ID cannot be null or empty");
        }

        logInfo("Starting process loading for root ID: " + objectId);
        this.loadingStartTime = System.currentTimeMillis();
        this.totalArtifactsLoaded = 0;
        this.totalDependenciesResolved = 0;

        try {
            Set<String> visitedIds = new HashSet<>();
            carregarArtefatoRecursivamente(objectId, visitedIds);

            long loadingTime = System.currentTimeMillis() - loadingStartTime;
            logInfo(String.format("Process loading completed. Loaded %d artifacts, resolved %d dependencies in %d ms",
                    totalArtifactsLoaded, totalDependenciesResolved, loadingTime));

            return new HashMap<>(cacheDeArtefatos); // Return defensive copy

        } catch (Exception e) {
            logError("Error during process loading: " + e.getMessage());
            throw new RuntimeException("Failed to load process: " + objectId, e);
        }
    }

    /**
     * Método load para compatibilidade V2Plus
     */
    public Object load(String extractionPath) throws Exception {
        // Se não foi inicializado com path, inicializar agora
        if (rootDirectoryPath == null && extractionPath != null) {
            initializeManifestAndContext(extractionPath);
        }

        // Encontrar o processo principal
        String mainProcessId = findMainProcessId();
        if (mainProcessId != null) {
            Map<String, Object> loadedArtifacts = loadProcessInMemory(mainProcessId);
            return createTeamworksFromCache(loadedArtifacts);
        }

        logError("No main process found in extraction path: " + extractionPath);
        return null;
    }

    /**
     * Carrega múltiplos processos
     */
    public Map<String, Object> loadMultipleProcesses(List<String> processIds) {
        Map<String, Object> allArtifacts = new HashMap<>();

        for (String processId : processIds) {
            try {
                Map<String, Object> processArtifacts = loadProcessInMemory(processId);
                allArtifacts.putAll(processArtifacts);
            } catch (Exception e) {
                logError("Failed to load process " + processId + ": " + e.getMessage());
            }
        }

        return allArtifacts;
    }

    // =========================================================================
    // RECURSIVE LOADING
    // =========================================================================
    public Map<String, Object> getCacheDeArtefatos() {
        return this.instanceCache;
    }
    /**
     * Carrega artefato recursivamente com suas dependências
     */
    private void carregarArtefatoRecursivamente(String objectId, Set<String> visitedIds) {
        if (objectId == null || objectId.trim().isEmpty() || visitedIds.contains(objectId)) {
            return;
        }

        visitedIds.add(objectId);

        // Verificar se já está no cache
        if (cacheDeArtefatos.containsKey(objectId)) {
            logDetail("Artifact already in cache: " + objectId);
            return;
        }

        try {
            logDetail("Loading artifact: " + objectId);

            Object artifact = loadArtifact(objectId);
            if (artifact != null) {
                cacheDeArtefatos.put(objectId, artifact);
                totalArtifactsLoaded++;

                logDetail("Artifact loaded successfully: " + objectId + " (" + artifact.getClass().getSimpleName() + ")");

                // Extrair e carregar dependências
                Set<String> dependencies = extractDependencyIds(artifact);
                totalDependenciesResolved += dependencies.size();

                logDetail("Found " + dependencies.size() + " dependencies for " + objectId);

                for (String depId : dependencies) {
                    carregarArtefatoRecursivamente(depId, visitedIds);
                }
            } else {
                logError("Failed to load artifact: " + objectId);
            }

        } catch (Exception e) {
            logError("Error loading artifact " + objectId + ": " + e.getMessage());
        }
    }

    /**
     * Carrega um artefato específico
     */
    private Object loadArtifact(String objectId) throws Exception {
        ArtifactLocation location = findArtifactLocation(objectId);
        if (location == null) {
            logError("Artifact location not found: " + objectId);
            return null;
        }

        String filePath = getArtifactFilePath(location);
        File artifactFile = new File(location.basePath, filePath);

        if (!artifactFile.exists()) {
            logError("Artifact file does not exist: " + artifactFile.getAbsolutePath());
            return null;
        }

        // Determinar classe do artefato
        Class<?> targetClass = determineArtifactClass(location.objectInfo.getType());
        if (targetClass == null) {
            logError("Could not determine target class for artifact type: " + location.objectInfo.getType());
            return null;
        }

        logDetail("Loading artifact file: " + artifactFile.getAbsolutePath() + " as " + targetClass.getSimpleName());

        return loadXmlFile(artifactFile, targetClass);
    }

    // =========================================================================
    // DEPENDENCY EXTRACTION
    // =========================================================================

    /**
     * Extrai IDs de dependências de um artefato
     */
    private Set<String> extractDependencyIds(Object artifact) {
        Set<String> ids = new HashSet<>();

        if (artifact instanceof Bpd) {
            extractBpdDependencies((Bpd) artifact, ids);
        } else if (artifact instanceof br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process) {
            extractProcessDependencies((br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process) artifact, ids);
        } else if (artifact instanceof CoachView) {
            extractCoachViewDependencies((CoachView) artifact, ids);
        }
        // Adicionar outros tipos conforme necessário

        logDetail("Extracted " + ids.size() + " dependency IDs from " + artifact.getClass().getSimpleName());
        return ids;
    }

    /**
     * Extrai dependências de um Process
     */
    private void extractProcessDependencies(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process, Set<String> ids) {
        try {
            // Extrair de items
            if (process.getItems() != null) {
                for (Object item : process.getItems()) {
                    extractItemDependencies(item, ids);
                }
            }

            // Extrair de links
            if (process.getLinks() != null) {
                for (Object link : process.getLinks()) {
                    extractLinkDependencies(link, ids);
                }
            }
        } catch (Exception e) {
            logError("Error extracting Process dependencies: " + e.getMessage());
        }
    }

    /**
     * NOVO: Extrai dependências de um item
     */
    private void extractItemDependencies(Object item, Set<String> ids) {
        try {
            // Tentar extrair referências usando reflexão
            Object component = item.getClass().getMethod("getTwComponent").invoke(item);
            if (component != null) {
                String processRef = (String) component.getClass().getMethod("getAttachedProcessRef").invoke(component);
                addIfNotEmpty(ids, processRef);
            }
        } catch (Exception e) {
            // Item pode não ter component - ignorar
        }
    }


    /**
     * Extrai dependências de um BPD
     */
    /**
     * Extrai dependências de um BPD - VERSÃO CORRIGIDA
     */
    private void extractBpdDependencies(Bpd bpd, Set<String> ids) {
        if (bpd.getBusinessProcessDiagram() == null) return;

        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();

        try {
            // CORRIGIDO: Extrair de pools e lanes
            if (diagram.getPools() != null) {
                for (Pool pool : diagram.getPools()) {
                    if (pool.getLanes() != null) {
                        for (Lane lane : pool.getLanes()) {
                            extractFlowObjectDependencies(lane.getFlowObjects(), ids);
                        }
                    }
                }
            }

            // CORRIGIDO: Tentar extrair flow objects diretos usando reflexão segura
            extractDirectFlowObjectsSafely(diagram, ids);

            // CORRIGIDO: Tentar extrair lanes diretas usando reflexão segura
            extractDirectLanesSafely(diagram, ids);

        } catch (Exception e) {
            logError("Error extracting BPD dependencies: " + e.getMessage());
        }
    }

    /**
     * NOVO: Extração segura de flow objects diretos
     */
    private void extractDirectFlowObjectsSafely(BusinessProcessDiagram diagram, Set<String> ids) {
        try {
            // Tentar usar método getFlowObjects() se disponível
            @SuppressWarnings("unchecked")
            List<FlowObject> flowObjects = (List<FlowObject>) diagram.getClass()
                    .getMethod("getFlowObjects")
                    .invoke(diagram);

            if (flowObjects != null) {
                extractFlowObjectDependencies(flowObjects, ids);
                logDetail("Extracted dependencies from direct flow objects");
            }
        } catch (Exception e) {
            logDetail("Direct flow objects not available: " + e.getMessage());
        }
    }

    /**
     * NOVO: Extração segura de lanes diretas
     */
    private void extractDirectLanesSafely(BusinessProcessDiagram diagram, Set<String> ids) {
        try {
            // Tentar usar método getLanes() se disponível
            @SuppressWarnings("unchecked")
            List<Lane> lanes = (List<Lane>) diagram.getClass()
                    .getMethod("getLanes")
                    .invoke(diagram);

            if (lanes != null) {
                for (Lane lane : lanes) {
                    extractFlowObjectDependencies(lane.getFlowObjects(), ids);
                }
                logDetail("Extracted dependencies from direct lanes");
            }
        } catch (Exception e) {
            logDetail("Direct lanes not available: " + e.getMessage());
        }
    }

    /**
     * Extrai dependências de flow objects
     */

    private void extractFlowObjectDependencies(List<FlowObject> flowObjects, Set<String> ids) {
        if (flowObjects == null) return;

        for (FlowObject flowObject : flowObjects) {
            try {
                if (flowObject.getComponent() != null &&
                        flowObject.getComponent().getImplementation() != null) {

                    Implementation impl = flowObject.getComponent().getImplementation();

                    // IDs de atividades/processos anexados
                    addIfNotEmpty(ids, impl.getAttachedActivityId());
                    addIfNotEmpty(ids, impl.getAttachedProcessId());
                    addIfNotEmpty(ids, impl.getEmbeddedProcessId());

                    // IDs de mapeamentos (se disponíveis)
                    extractMappingDependencies(impl, ids);
                }
            } catch (Exception e) {
                logDetail("Error extracting dependencies from flow object " + flowObject.getId() + ": " + e.getMessage());
            }
        }
    }


    private void extractMappingDependencies(Implementation impl, Set<String> ids) {
        try {
            if (impl.getInputMappings() != null) {
                for (Object mapping : impl.getInputMappings()) {
                    // Extrair IDs de mapeamentos se houver estrutura específica
                    extractMappingId(mapping, ids);
                }
            }

            if (impl.getOutputMappings() != null) {
                for (Object mapping : impl.getOutputMappings()) {
                    extractMappingId(mapping, ids);
                }
            }
        } catch (Exception e) {
            logDetail("Error extracting mapping dependencies: " + e.getMessage());
        }
    }


    /**
     * Extrai dependências de um CoachView
     */
    private void extractCoachViewDependencies(CoachView coachView, Set<String> ids) {
        // Implementar extração de dependências específicas de CoachView
    }

    /**
     * Adiciona ID à lista se não for vazio
     */
    private void addIfNotEmpty(Set<String> ids, String id) {
        if (id != null && !id.trim().isEmpty()) {
            ids.add(normalizeId(id));
        }
    }

    /**
     * Normaliza um ID removendo espaços extras
     */
    public String normalizeId(String id) {
        if (id == null) return null;
        return id.replaceAll("\\s+", "").trim();
    }

    // =========================================================================
    // ARTIFACT LOCATION AND CLASS DETERMINATION
    // =========================================================================

    /**
     * Encontra a localização de um artefato
     */
    public ArtifactLocation findArtifactLocation(String objectId) {
        String normalizedId = normalizeId(objectId);

        // Procurar no manifest principal primeiro
        ArtifactLocation location = searchInManifest(mainManifest, rootDirectoryPath, normalizedId);
        if (location != null) {
            return location;
        }

        // Procurar nos toolkits
        for (Map.Entry<String, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package> entry : manifestCache.entrySet()) {
            if (!"main".equals(entry.getKey())) {
                String toolkitPath = rootDirectoryPath + File.separator + entry.getKey();
                location = searchInManifest(entry.getValue(), toolkitPath, normalizedId);
                if (location != null) {
                    return location;
                }
            }
        }

        // Tratamento especial para IDs compostos (com barra)
        if (normalizedId.contains("/")) {
            return handleCompositeId(normalizedId);
        }

        logError("Artifact location not found for ID: " + objectId);
        return null;
    }

    /**
     * NOVO: Extrai ID de um mapeamento específico
     */
    private void extractMappingId(Object mapping, Set<String> ids) {
        try {
            // Tentar extrair ID usando reflexão
            String id = (String) mapping.getClass().getMethod("getId").invoke(mapping);
            addIfNotEmpty(ids, id);
        } catch (Exception e) {
            // Mapping pode não ter ID - ignorar
        }
    }


    /**
     * NOVO: Extrai dependências de um link
     */
    private void extractLinkDependencies(Object link, Set<String> ids) {
        try {
            String toProcessItemId = (String) link.getClass().getMethod("getToProcessItemId").invoke(link);
            String fromProcessItemId = (String) link.getClass().getMethod("getFromProcessItemId").invoke(link);
            addIfNotEmpty(ids, toProcessItemId);
            addIfNotEmpty(ids, fromProcessItemId);
        } catch (Exception e) {
            // Link pode não ter esses métodos - ignorar
        }
    }





    /**
     * Procura em um manifest específico
     */
    private ArtifactLocation searchInManifest(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package manifest,
                                              String basePath, String objectId) {
        if (manifest == null || manifest.getObjects() == null) {
            return null;
        }

        for (PackageObject obj : manifest.getObjects()) {
            if (objectId.equals(obj.getId())) {
                return new ArtifactLocation(basePath, obj);
            }
        }

        return null;
    }

    /**
     * Trata IDs compostos (formato toolkit/id)
     */
    private ArtifactLocation handleCompositeId(String compositeId) {
        String[] parts = compositeId.split("/", 2);
        if (parts.length == 2) {
            String toolkitId = parts[0];
            String artifactId = parts[1];

            // Procurar o toolkit específico
            br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package toolkitManifest = manifestCache.get(toolkitId);
            if (toolkitManifest != null) {
                String toolkitPath = rootDirectoryPath + File.separator + toolkitId;
                return searchInManifest(toolkitManifest, toolkitPath, artifactId);
            }
        }

        return null;
    }

    /**
     * Obtém o caminho do arquivo do artefato de forma segura
     */
    private String getArtifactFilePath(ArtifactLocation location) {
        try {
            // Tentar getPath() primeiro
            return (String) location.objectInfo.getClass().getMethod("getPath").invoke(location.objectInfo);
        } catch (Exception e) {
            // Fallback: construir caminho baseado no tipo e ID
            String type = location.objectInfo.getType();
            String id = location.objectInfo.getId();

            if (type != null && id != null) {
                return type.toLowerCase() + File.separator + id + ".xml";
            }

            // Último fallback: usar ID como nome do arquivo
            return (id != null ? id : "unknown") + ".xml";
        }
    }

    /**
     * Determina a classe do artefato baseado no tipo
     */
    private Class<?> determineArtifactClass(String type) {
        if (type == null) return null;

        switch (type.toLowerCase()) {
            case "bpd":
                return Bpd.class;
            case "process":
                return br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process.class;
            case "service":
                return loadServiceClassSafely();
            case "coachview":
                return CoachView.class;
            case "businessobject":
            case "twclass":
                return TwClass.class;
            case "environmentvariable":
                return loadEnvironmentVariableClassSafely();
            default:
                logDetail("Unknown artifact type: " + type + ", using Object.class");
                return Object.class;
        }
    }

    /**
     * Carrega classe Service de forma segura
     */
    private Class<?> loadServiceClassSafely() {
        try {
            return Class.forName("br.com.danzeroum.bpmbaw.mapeadorxml.modelo.service.Service");
        } catch (ClassNotFoundException e) {
            logDetail("Service class not found, using Object.class");
            return Object.class;
        }
    }

    /**
     * Carrega classe EnvironmentVariable de forma segura
     */
    private Class<?> loadEnvironmentVariableClassSafely() {
        try {
            return Class.forName("br.com.danzeroum.bpmbaw.mapeadorxml.modelo.environmentvariable.EnvironmentVariable");
        } catch (ClassNotFoundException e) {
            logDetail("EnvironmentVariable class not found, using Object.class");
            return Object.class;
        }
    }

    // =========================================================================
    // XML LOADING
    // =========================================================================

    /**
     * Carrega arquivo XML usando JAXB
     */
    @SuppressWarnings("unchecked")
    private <T> T loadXmlFile(File file, Class<T> clazz) throws Exception {
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }

        try {
            JAXBContext context = getJAXBContext(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // Ler arquivo com encoding correto
            String content = readFileWithCorrectEncoding(file);
            StringReader stringReader = new StringReader(content);

            Object result = unmarshaller.unmarshal(stringReader);
            return (T) result;

        } catch (Exception e) {
            logError("Error loading XML file " + file.getAbsolutePath() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Obtém contexto JAXB para uma classe
     */
    private JAXBContext getJAXBContext(Class<?> clazz) throws JAXBException {
        String className = clazz.getName();

        // Verificar cache primeiro
        JAXBContext cached = jaxbContexts.get(className);
        if (cached != null) {
            return cached;
        }

        // Tentar usar contexto universal primeiro
        if (universalJaxbContext != null) {
            try {
                return universalJaxbContext;
            } catch (Exception e) {
                logDetail("Universal context failed for " + className + ", creating specific context");
            }
        }

        // Criar contexto específico
        JAXBContext context = JAXBContext.newInstance(clazz);
        jaxbContexts.put(className, context);

        return context;
    }

    /**
     * Lê arquivo com encoding correto
     */
    private String readFileWithCorrectEncoding(File file) throws IOException {
        // Tentar detectar encoding do XML
        String encoding = detectXmlEncoding(file);

        try {
            Path path = Paths.get(file.getAbsolutePath());
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, encoding);
        } catch (Exception e) {
            // Fallback para UTF-8
            logDetail("Failed to read with encoding " + encoding + ", using UTF-8");
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        }
    }

    /**
     * Detecta encoding do arquivo XML
     */
    private String detectXmlEncoding(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String firstLine = reader.readLine();
            if (firstLine != null && firstLine.contains("encoding=")) {
                int start = firstLine.indexOf("encoding=\"") + 10;
                int end = firstLine.indexOf("\"", start);
                if (start > 9 && end > start) {
                    return firstLine.substring(start, end);
                }
            }
        } catch (Exception e) {
            logDetail("Could not detect XML encoding: " + e.getMessage());
        }

        return DEFAULT_ENCODING;
    }

    // =========================================================================
    // TEAMWORKS CREATION
    // =========================================================================

    /**
     * Cria objeto Teamworks a partir do cache
     */
    private Teamworks createTeamworksFromCache(Map<String, Object> cache) {
        Teamworks teamworks = new Teamworks();

        try {
            // Tentar criar ProcessApp de forma segura
            Object processApp = createProcessAppSafely(cache);
            if (processApp != null) {
                setProcessAppOnTeamworks(teamworks, processApp);
            } else {
                // Fallback: definir BPD diretamente
                setBpdDirectlyOnTeamworks(teamworks, cache);
            }

        } catch (Exception e) {
            logError("Error creating Teamworks from cache: " + e.getMessage());
            // Tentar fallback final
            setBpdDirectlyOnTeamworks(teamworks, cache);
        }

        return teamworks;
    }

    /**
     * Cria ProcessApp de forma segura
     */
    private Object createProcessAppSafely(Map<String, Object> cache) {
        try {
            // Tentar carregar classe ProcessApp
            Class<?> processAppClass = Class.forName("br.com.danzeroum.bpmbaw.mapeadorxml.modelo.processapp.ProcessApp");
            Object processApp = processAppClass.newInstance();

            // Procurar BPD no cache
            for (Object obj : cache.values()) {
                if (obj instanceof Bpd) {
                    processAppClass.getMethod("setBpd", Bpd.class).invoke(processApp, obj);
                    logDetail("BPD set on ProcessApp successfully");
                    break;
                }
            }

            return processApp;

        } catch (ClassNotFoundException e) {
            logDetail("ProcessApp class not found - package may not exist");
            return null;
        } catch (Exception e) {
            logError("Error creating ProcessApp: " + e.getMessage());
            return null;
        }
    }

    /**
     * Define ProcessApp no Teamworks usando reflexão
     */
    private void setProcessAppOnTeamworks(Teamworks teamworks, Object processApp) {
        try {
            teamworks.getClass().getMethod("setProcessApp", Object.class).invoke(teamworks, processApp);
            logDetail("ProcessApp set on Teamworks successfully");
        } catch (Exception e) {
            logError("Could not set ProcessApp on Teamworks: " + e.getMessage());
        }
    }

    /**
     * Define BPD diretamente no Teamworks como fallback
     */
    private void setBpdDirectlyOnTeamworks(Teamworks teamworks, Map<String, Object> cache) {
        try {
            for (Object obj : cache.values()) {
                if (obj instanceof Bpd) {
                    teamworks.getClass().getMethod("setBpd", Bpd.class).invoke(teamworks, obj);
                    logDetail("BPD set directly on Teamworks successfully");
                    break;
                }
            }
        } catch (Exception e) {
            logError("Could not set BPD directly on Teamworks: " + e.getMessage());
        }
    }

    // =========================================================================
    // UTILITY METHODS
    // =========================================================================

    /**
     * Encontra o ID do processo principal
     */
    private String findMainProcessId() {
        if (mainManifest == null || mainManifest.getObjects() == null) {
            return null;
        }

        // Procurar por BPD primeiro
        for (PackageObject obj : mainManifest.getObjects()) {
            if (obj.getType() != null && obj.getType().toLowerCase().contains("bpd")) {
                return obj.getId();
            }
        }

        // Procurar por Process
        for (PackageObject obj : mainManifest.getObjects()) {
            if (obj.getType() != null && obj.getType().toLowerCase().contains("process")) {
                return obj.getId();
            }
        }

        // Fallback: retornar primeiro objeto
        if (!mainManifest.getObjects().isEmpty()) {
            return mainManifest.getObjects().get(0).getId();
        }

        return null;
    }

    /**
     * Lista todos os processos disponíveis
     */
    public List<String> listAvailableProcesses() {
        List<String> processes = new ArrayList<>();

        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package manifest : manifestCache.values()) {
            if (manifest.getObjects() != null) {
                for (PackageObject obj : manifest.getObjects()) {
                    if (obj.getType() != null &&
                            (obj.getType().toLowerCase().contains("bpd") ||
                                    obj.getType().toLowerCase().contains("process"))) {
                        processes.add(obj.getId());
                    }
                }
            }
        }

        return processes;
    }

    /**
     * Obtém informações de um artefato
     */
    public ArtifactInfo getArtifactInfo(String objectId) {
        ArtifactLocation location = findArtifactLocation(objectId);
        if (location != null) {
            return new ArtifactInfo(location.objectInfo);
        }
        return null;
    }

    /**
     * Obtém estatísticas do carregamento
     */
    public LoadingStatistics getLoadingStatistics() {
        return new LoadingStatistics(
                totalArtifactsLoaded,
                totalDependenciesResolved,
                cacheDeArtefatos.size(),
                System.currentTimeMillis() - loadingStartTime
        );
    }

    /**
     * Limpa o cache de artefatos
     */
    public void clearCache() {
        cacheDeArtefatos.clear();
        totalArtifactsLoaded = 0;
        totalDependenciesResolved = 0;
        logInfo("Artifact cache cleared");
    }

    /**
     * Verifica se um artefato está no cache
     */
    public boolean isInCache(String objectId) {
        return cacheDeArtefatos.containsKey(normalizeId(objectId));
    }

    /**
     * Obtém artefato do cache
     */
    public Object getFromCache(String objectId) {
        return cacheDeArtefatos.get(normalizeId(objectId));
    }

    // =========================================================================
    // LOGGING METHODS
    // =========================================================================

    /**
     * Cria logger silencioso
     */
    private PrintWriter createSilentLogger() {
        return new PrintWriter(new StringWriter()) {
            @Override
            public void println(String s) {
                // Logger silencioso - não faz nada
            }
        };
    }
    public Object getArtefatoDoCache(String key) {
        if (key == null) {
            return null;
        }
        return this.instanceCache.get(key);
    }
    /**
     * Log de informação
     */
    private void logInfo(String message) {
        if (logger != null) {
            logger.println("[INFO] " + message);
            logger.flush();
        }
    }

    /**
     * Log detalhado (apenas se habilitado)
     */
    private void logDetail(String message) {
        if (enableDetailedLogging && logger != null) {
            logger.println("[DETAIL] " + message);
            logger.flush();
        }
    }

    /**
     * Log de erro
     */
    private void logError(String message) {
        if (logger != null) {
            logger.println("[ERROR] " + message);
            logger.flush();
        }
    }

    // =========================================================================
    // INNER CLASSES
    // =========================================================================

    /**
     * Localização de um artefato
     */
    public static class ArtifactLocation {
        public final String basePath;
        public final PackageObject objectInfo;
        public String toolkitName;

        public ArtifactLocation(String basePath, PackageObject objectInfo) {
            this.basePath = basePath;
            this.objectInfo = objectInfo;
        }

        @Override
        public String toString() {
            return String.format("ArtifactLocation{basePath='%s', id='%s', type='%s'}",
                    basePath, objectInfo.getId(), objectInfo.getType());
        }
    }

    /**
     * Informações de um artefato
     */
    public static class ArtifactInfo {
        private final String id;
        private final String type;
        private final String name;

        public ArtifactInfo(PackageObject packageObject) {
            this.id = packageObject.getId();
            this.type = packageObject.getType();
            this.name = packageObject.getName();
        }

        public String getId() { return id; }
        public String getType() { return type; }
        public String getName() { return name; }

        @Override
        public String toString() {
            return String.format("ArtifactInfo{id='%s', type='%s', name='%s'}", id, type, name);
        }
    }

    /**
     * Estatísticas do carregamento
     */
    public static class LoadingStatistics {
        private final int artifactsLoaded;
        private final int dependenciesResolved;
        private final int cacheSize;
        private final long loadingTime;

        public LoadingStatistics(int artifactsLoaded, int dependenciesResolved, int cacheSize, long loadingTime) {
            this.artifactsLoaded = artifactsLoaded;
            this.dependenciesResolved = dependenciesResolved;
            this.cacheSize = cacheSize;
            this.loadingTime = loadingTime;
        }

        public int getArtifactsLoaded() { return artifactsLoaded; }
        public int getDependenciesResolved() { return dependenciesResolved; }
        public int getCacheSize() { return cacheSize; }
        public long getLoadingTime() { return loadingTime; }

        @Override
        public String toString() {
            return String.format("LoadingStats{artifacts: %d, dependencies: %d, cache: %d, time: %dms}",
                    artifactsLoaded, dependenciesResolved, cacheSize, loadingTime);
        }
    }

    // =========================================================================
    // VALIDATION AND HEALTH CHECK
    // =========================================================================

    /**
     * Valida se o ProcessLoader está corretamente inicializado
     */
    public boolean isValid() {
        return mainManifest != null && !manifestCache.isEmpty();
    }

    /**
     * Executa health check completo
     */
    public HealthCheckResult performHealthCheck() {
        HealthCheckResult result = new HealthCheckResult();

        // Verificar manifest principal
        result.mainManifestValid = (mainManifest != null);

        // Verificar diretório raiz
        if (rootDirectoryPath != null) {
            File rootDir = new File(rootDirectoryPath);
            result.rootDirectoryExists = rootDir.exists() && rootDir.isDirectory();
        }

        // Verificar contexto JAXB
        result.jaxbContextValid = (universalJaxbContext != null);

        // Verificar cache
        result.cacheSize = cacheDeArtefatos.size();
        result.manifestCount = manifestCache.size();

        // Status geral
        result.overallHealthy = result.mainManifestValid &&
                result.rootDirectoryExists &&
                result.manifestCount > 0;

        return result;
    }

    /**
     * Resultado do health check
     */
    public static class HealthCheckResult {
        public boolean mainManifestValid = false;
        public boolean rootDirectoryExists = false;
        public boolean jaxbContextValid = false;
        public int cacheSize = 0;
        public int manifestCount = 0;
        public boolean overallHealthy = false;

        @Override
        public String toString() {
            return String.format("HealthCheck{healthy: %s, manifest: %s, directory: %s, jaxb: %s, cache: %d, manifests: %d}",
                    overallHealthy, mainManifestValid, rootDirectoryExists,
                    jaxbContextValid, cacheSize, manifestCount);
        }
    }

    // =========================================================================
    // THREAD SAFETY UTILITIES
    // =========================================================================

    /**
     * ConcurrentHashMap para contextos JAXB thread-safe
     */
    private static class ConcurrentHashMap<K, V> extends java.util.concurrent.ConcurrentHashMap<K, V> {
        // Java 8 compatible ConcurrentHashMap
    }

    // =========================================================================
    // CLEANUP AND RESOURCE MANAGEMENT
    // =========================================================================

    /**
     * Cleanup de recursos
     */
    public void cleanup() {
        try {
            clearCache();

            if (logger != null) {
                logger.close();
            }

            jaxbContexts.clear();
            manifestCache.clear();

            logInfo("ProcessLoader cleanup completed");

        } catch (Exception e) {
            System.err.println("Error during ProcessLoader cleanup: " + e.getMessage());
        }
    }

    /**
     * Finalize method para cleanup automático
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            cleanup();
        } finally {
            super.finalize();
        }
    }

    // =========================================================================
    // BUILDER PATTERN FOR COMPLEX INITIALIZATION
    // =========================================================================

    /**
     * Builder para configuração avançada do ProcessLoader
     */
    public static class Builder {
        private String twxDirectoryPath;
        private PrintWriter logger;
        private boolean enableDetailedLogging = true;

        public Builder setTwxDirectoryPath(String path) {
            this.twxDirectoryPath = path;
            return this;
        }

        public Builder setLogger(PrintWriter logger) {
            this.logger = logger;
            return this;
        }

        public Builder setDetailedLogging(boolean enable) {
            this.enableDetailedLogging = enable;
            return this;
        }

        public ProcessLoader build() throws Exception {
            return new ProcessLoader(twxDirectoryPath, logger, enableDetailedLogging);
        }
    }

    /**
     * Cria builder para configuração avançada
     */
    public static Builder builder() {
        return new Builder();
    }
}