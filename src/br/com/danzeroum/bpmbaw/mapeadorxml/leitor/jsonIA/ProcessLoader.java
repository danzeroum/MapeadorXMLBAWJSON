/**
 * ProcessLoader COMPLETO E ATUALIZADO - Java 8 Compatible
 *
 * VERSÃO FINAL COM TODAS AS CORREÇÕES:
 * ✅ Compatibilidade V1/V2Plus completa
 * ✅ Detecção automática de arquivos BPD
 * ✅ Busca recursiva inteligente
 * ✅ Cache sincronizado (instanceCache + cacheDeArtefatos)
 * ✅ Log detalhado de debug
 * ✅ Mapeamento correto de tipos para diretórios
 * ✅ Tratamento robusto de erros
 * ✅ Múltiplos construtores para flexibilidade
 *
 * @version 2.1.3-complete-final
 * @since Java 8
 */
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.PackageObject;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessLoader {

    // =========================================================================
    // CONSTANTS
    // =========================================================================
    private static final String META_INF_DIR = "META-INF";
    private static final String PACKAGE_XML = "package.xml";
    private static final String DEFAULT_ENCODING = "UTF-8";

    // =========================================================================
    // FIELDS - SINCRONIZAÇÃO V1/V2Plus
    // =========================================================================
    private final String rootDirectoryPath;
    private final PrintWriter logger;
    private final boolean enableDetailedLogging;

    // Caches principais - V1 e V2Plus compatível
    private final Map<String, Object> cacheDeArtefatos = new LinkedHashMap<>();
    private Map<String, Object> instanceCache; // V2Plus compatibility
    private final Map<String, JAXBContext> jaxbContexts = new ConcurrentHashMap<>();
    private final Map<String, Package> manifestCache = new HashMap<>();

    // Core components
    private Package mainManifest;
    private JAXBContext universalJaxbContext;

    // Statistics
    private int totalArtifactsLoaded = 0;
    private int totalDependenciesResolved = 0;
    private long loadingStartTime = 0L;

    // =========================================================================
    // CONSTRUCTORS - COMPATIBILIDADE TOTAL
    // =========================================================================

    /**
     * Construtor padrão - V2Plus compatibility
     */
    public ProcessLoader() {
        this.rootDirectoryPath = null;
        this.logger = createSilentLogger();
        this.enableDetailedLogging = false;
        this.instanceCache = new LinkedHashMap<>(); // CORREÇÃO: Inicialização
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
     * Construtor completo
     */
    public ProcessLoader(String twxDirectoryPath, PrintWriter logger, boolean enableDetailedLogging) throws Exception {
        this.rootDirectoryPath = twxDirectoryPath;
        this.logger = logger != null ? logger : createSilentLogger();
        this.enableDetailedLogging = enableDetailedLogging;
        this.instanceCache = new LinkedHashMap<>(); // CORREÇÃO: Inicialização

        if (twxDirectoryPath != null) {
            initializeManifestAndContext(twxDirectoryPath);
        }
    }

    // =========================================================================
    // MÉTODOS PRINCIPAIS - CORRIGIDOS PARA V2Plus
    // =========================================================================

    /**
     * CORREÇÃO PRINCIPAL: Carrega processo na memória com sincronização total
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

            // CORREÇÃO: Sincronizar os dois caches
            syncCaches();

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
     * CORREÇÃO PRINCIPAL: Método getArtefatoDoCache com tratamento de null
     */
    public Object getArtefatoDoCache(String key) {
        if (key == null) {
            return null;
        }

        // CORREÇÃO: Verificar instanceCache primeiro, depois cacheDeArtefatos
        if (instanceCache != null && instanceCache.containsKey(key)) {
            return instanceCache.get(key);
        }

        if (cacheDeArtefatos.containsKey(key)) {
            return cacheDeArtefatos.get(key);
        }

        // Tentar normalizar o ID e procurar novamente
        String normalizedKey = normalizeId(key);
        if (normalizedKey != null && !normalizedKey.equals(key)) {
            if (instanceCache != null && instanceCache.containsKey(normalizedKey)) {
                return instanceCache.get(normalizedKey);
            }
            if (cacheDeArtefatos.containsKey(normalizedKey)) {
                return cacheDeArtefatos.get(normalizedKey);
            }
        }

        return null;
    }

    /**
     * NOVO: Sincronização entre os dois caches
     */
    private void syncCaches() {
        if (instanceCache == null) {
            instanceCache = new LinkedHashMap<>();
        }

        // Sincronizar cacheDeArtefatos -> instanceCache
        for (Map.Entry<String, Object> entry : cacheDeArtefatos.entrySet()) {
            instanceCache.put(entry.getKey(), entry.getValue());
        }

        logDetail("Caches synchronized: " + instanceCache.size() + " artifacts");
    }

    /**
     * Método de compatibilidade V2Plus
     */
    public Map<String, Object> getCacheDeArtefatos() {
        syncCaches();
        return instanceCache != null ? instanceCache : new HashMap<>();
    }

    /**
     * Método getFromCache para compatibilidade
     */
    public Object getFromCache(String objectId) {
        return getArtefatoDoCache(objectId);
    }

    // =========================================================================
    // RECURSIVE LOADING - IGUAL À V1
    // =========================================================================

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
     * Carrega um artefato específico com detecção melhorada
     */
    private Object loadArtifact(String objectId) throws Exception {
        ArtifactLocation location = findArtifactLocation(objectId);
        if (location == null) {
            logError("Artifact location not found: " + objectId);

            // Fazer debug listing se não encontrar
            if (enableDetailedLogging) {
                listAvailableXmlFiles(rootDirectoryPath);
            }

            return null;
        }

        String filePath = getArtifactFilePath(location);
        File artifactFile = new File(location.basePath, filePath);

        if (!artifactFile.exists()) {
            logError("Artifact file does not exist: " + artifactFile.getAbsolutePath());

            // Fazer debug listing se arquivo não existir
            if (enableDetailedLogging) {
                listAvailableXmlFiles(rootDirectoryPath);
            }

            return null;
        }

        // Determinar classe do artefato
        Class<?> targetClass = determineArtifactClass(location.objectInfo.getType());
        if (targetClass == null) {
            logError("Unknown artifact class for type: " + location.objectInfo.getType());
            return null;
        }

        logDetail("Loading artifact from: " + artifactFile.getAbsolutePath());
        logDetail("Target class: " + targetClass.getSimpleName());

        // Carregar e unmarshall
        return unmarshallArtifact(artifactFile, targetClass);
    }

    /**
     * Obtém path do arquivo do artefato com detecção melhorada
     */
    private String getArtifactFilePath(ArtifactLocation location) {
        String type = location.objectInfo.getType();
        String id = location.objectInfo.getId();

        logDetail("Looking for artifact:");
        logDetail("   ID: " + id);
        logDetail("   Type: " + type);
        logDetail("   Name: " + location.objectInfo.getName());

        // Lista de caminhos possíveis baseados no tipo
        List<String> possiblePaths = new ArrayList<>();

        if (type != null) {
            String normalizedType = type.toLowerCase();

            // Mapeamento de tipos para diretórios
            if (normalizedType.contains("bpd") || normalizedType.equals("businessprocessdiagram")) {
                possiblePaths.add("bpd/" + id + ".xml");
                possiblePaths.add("process/" + id + ".xml");
                possiblePaths.add("processes/" + id + ".xml");
            } else if (normalizedType.contains("process") || normalizedType.contains("app")) {
                possiblePaths.add("process/" + id + ".xml");
                possiblePaths.add("processes/" + id + ".xml");
                possiblePaths.add("bpd/" + id + ".xml");
            } else if (normalizedType.contains("service")) {
                possiblePaths.add("service/" + id + ".xml");
                possiblePaths.add("services/" + id + ".xml");
            } else if (normalizedType.contains("coach")) {
                possiblePaths.add("coach/" + id + ".xml");
                possiblePaths.add("coaches/" + id + ".xml");
                possiblePaths.add("coachview/" + id + ".xml");
            } else if (normalizedType.contains("class") || normalizedType.contains("twclass")) {
                possiblePaths.add("twclass/" + id + ".xml");
                possiblePaths.add("class/" + id + ".xml");
            } else {
                // Tipo desconhecido - adicionar diretórios comuns
                possiblePaths.add("objects/" + id + ".xml");
                possiblePaths.add("bpd/" + id + ".xml");
                possiblePaths.add("process/" + id + ".xml");
            }
        }

        // Adicionar caminhos padrão como fallback
        possiblePaths.add("objects/" + id + ".xml");
        possiblePaths.add(id + ".xml"); // Arquivo diretamente na raiz

        // Tentar encontrar o arquivo em cada caminho possível
        for (String path : possiblePaths) {
            File candidateFile = new File(location.basePath, path);
            logDetail("Checking: " + candidateFile.getAbsolutePath());

            if (candidateFile.exists()) {
                logDetail("✅ Found file: " + path);
                return path;
            } else {
                logDetail("❌ Not found: " + path);
            }
        }

        // Se não encontrou, fazer busca recursiva
        logDetail("File not found in standard paths, performing recursive search...");
        String recursivePath = findFileRecursively(location.basePath, id + ".xml");
        if (recursivePath != null) {
            logDetail("✅ Found via recursive search: " + recursivePath);
            return recursivePath;
        }

        logError("❌ File not found anywhere: " + id + ".xml");

        // Retornar o primeiro caminho como último recurso
        return possiblePaths.get(0);
    }

    /**
     * Busca recursiva por arquivo
     */
    private String findFileRecursively(String basePath, String fileName) {
        try {
            File baseDir = new File(basePath);
            return findFileInDirectory(baseDir, fileName, basePath);
        } catch (Exception e) {
            logDetail("Error during recursive search: " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca arquivo em diretório recursivamente
     */
    private String findFileInDirectory(File directory, String fileName, String basePath) {
        if (!directory.isDirectory()) {
            return null;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return null;
        }

        // Primeiro procurar arquivos diretos
        for (File file : files) {
            if (file.isFile() && file.getName().equals(fileName)) {
                try {
                    String relativePath = new File(basePath).toURI().relativize(file.toURI()).getPath();
                    return relativePath;
                } catch (Exception e) {
                    return file.getAbsolutePath().substring(basePath.length() + 1);
                }
            }
        }

        // Depois procurar em subdiretórios (máximo 3 níveis)
        for (File file : files) {
            if (file.isDirectory() && !file.getName().equals("META-INF")) {
                String result = findFileInDirectory(file, fileName, basePath);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    /**
     * Lista todos os arquivos XML disponíveis para debug
     */
    private void listAvailableXmlFiles(String basePath) {
        logDetail("Listing all XML files in: " + basePath);
        try {
            File baseDir = new File(basePath);
            listXmlFilesRecursively(baseDir, basePath, 0);
        } catch (Exception e) {
            logDetail("Error listing files: " + e.getMessage());
        }
    }

    /**
     * Lista arquivos XML recursivamente
     */
    private void listXmlFilesRecursively(File directory, String basePath, int depth) {
        if (!directory.isDirectory() || depth > 3) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".xml") &&
                    !file.getName().equals("package.xml")) {

                try {
                    String relativePath = new File(basePath).toURI().relativize(file.toURI()).getPath();
                    logDetail("Found XML: " + relativePath + " (size: " + file.length() + " bytes)");
                } catch (Exception e) {
                    logDetail("Found XML: " + file.getName());
                }
            } else if (file.isDirectory() && !file.getName().equals("META-INF")) {
                listXmlFilesRecursively(file, basePath, depth + 1);
            }
        }
    }

    /**
     * Determina classe do artefato com melhor detecção
     */
    private Class<?> determineArtifactClass(String type) {
        if (type == null) {
            logDetail("Type is null, defaulting to Teamworks");
            return br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks.class;
        }

        String normalizedType = type.toLowerCase();
        logDetail("Determining class for type: " + type + " (normalized: " + normalizedType + ")");

        if (normalizedType.contains("bpd") || normalizedType.contains("businessprocess")) {
            logDetail("Mapped to BusinessProcessDiagram");
            return br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram.class;
        } else if (normalizedType.contains("process") || normalizedType.contains("app")) {
            logDetail("Mapped to Teamworks (ProcessApp)");
            return br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks.class;
        } else if (normalizedType.contains("service")) {
            logDetail("Mapped to Teamworks (Service)");
            return br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks.class;
        } else if (normalizedType.contains("coach")) {
            logDetail("Mapped to CoachView");
            return br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.CoachView.class;
        } else if (normalizedType.contains("class") || normalizedType.contains("twclass")) {
            logDetail("Mapped to TwClass");
            return br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass.class;
        } else {
            logDetail("Unknown type, defaulting to Teamworks");
            return br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks.class;
        }
    }

    /**
     * Extrai IDs de dependências de um artefato
     */
    private Set<String> extractDependencyIds(Object artifact) {
        Set<String> dependencies = new HashSet<>();

        if (artifact instanceof Teamworks) {
            Teamworks tw = (Teamworks) artifact;
            // Extrair dependências do Teamworks usando reflexão defensiva
            try {
                // Tentar getBpds()
                java.lang.reflect.Method getBpdsMethod = tw.getClass().getMethod("getBpds");
                Object bpds = getBpdsMethod.invoke(tw);
                if (bpds instanceof List) {
                    for (Object bpd : (List<?>) bpds) {
                        String id = extractIdFromObject(bpd);
                        if (id != null) dependencies.add(id);
                    }
                }
            } catch (Exception e) {
                // Ignorar silenciosamente
            }

            // Tentar getServices()
            try {
                java.lang.reflect.Method getServicesMethod = tw.getClass().getMethod("getServices");
                Object services = getServicesMethod.invoke(tw);
                if (services instanceof List) {
                    for (Object service : (List<?>) services) {
                        String id = extractIdFromObject(service);
                        if (id != null) dependencies.add(id);
                    }
                }
            } catch (Exception e) {
                // Ignorar silenciosamente
            }
        }

        logDetail("Extracted " + dependencies.size() + " dependency IDs from " +
                (artifact != null ? artifact.getClass().getSimpleName() : "null"));
        return dependencies;
    }

    /**
     * Extrai ID de um objeto usando reflexão
     */
    private String extractIdFromObject(Object obj) {
        if (obj == null) return null;

        try {
            java.lang.reflect.Method getIdMethod = obj.getClass().getMethod("getId");
            Object result = getIdMethod.invoke(obj);
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Unmarshall artefato usando JAXB
     */
    private Object unmarshallArtifact(File file, Class<?> targetClass) throws Exception {
        JAXBContext context = getJAXBContextForClass(targetClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            return unmarshaller.unmarshal(isr);
        }
    }

    /**
     * Obtém JAXB context para uma classe
     */
    private JAXBContext getJAXBContextForClass(Class<?> clazz) throws Exception {
        String className = clazz.getName();

        if (!jaxbContexts.containsKey(className)) {
            JAXBContext context = JAXBContext.newInstance(clazz);
            jaxbContexts.put(className, context);
        }

        return jaxbContexts.get(className);
    }

    // =========================================================================
    // UTILITY METHODS
    // =========================================================================

    /**
     * Normaliza ID removendo prefixos desnecessários
     */
    public String normalizeId(String id) {
        if (id == null) return null;

        // Remover prefixos comuns
        if (id.startsWith("urn:")) {
            return id.substring(4);
        }
        if (id.startsWith("bpd:")) {
            return id.substring(4);
        }

        return id;
    }

    /**
     * Encontra localização de um artefato
     */
    public ArtifactLocation findArtifactLocation(String objectId) {
        String normalizedId = normalizeId(objectId);

        // Primeiro tentar no manifest principal
        if (mainManifest != null && mainManifest.getObjects() != null) {
            for (PackageObject obj : mainManifest.getObjects()) {
                if (normalizedId.equals(normalizeId(obj.getId()))) {
                    return new ArtifactLocation(rootDirectoryPath, obj);
                }
            }
        }

        // Depois tentar nos toolkits
        for (Package toolkit : manifestCache.values()) {
            if (toolkit.getObjects() != null) {
                for (PackageObject obj : toolkit.getObjects()) {
                    if (normalizedId.equals(normalizeId(obj.getId()))) {
                        return new ArtifactLocation(rootDirectoryPath, obj);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Procura por processos disponíveis
     */
    public List<String> findAvailableProcesses() {
        List<String> processes = new ArrayList<>();

        if (mainManifest != null && mainManifest.getObjects() != null) {
            for (PackageObject obj : mainManifest.getObjects()) {
                if (obj.getType() != null &&
                        (obj.getType().toLowerCase().contains("bpd") ||
                                obj.getType().toLowerCase().contains("process"))) {
                    processes.add(obj.getId());
                }
            }
        }

        return processes;
    }

    /**
     * Busca o ID do processo principal
     */
    private String findMainProcessId() {
        List<String> processes = findAvailableProcesses();
        return processes.isEmpty() ? null : processes.get(0);
    }

    /**
     * Cria objeto Teamworks a partir do cache
     */
    private Object createTeamworksFromCache(Map<String, Object> artifacts) {
        // Procurar por objeto Teamworks principal
        for (Object artifact : artifacts.values()) {
            if (artifact instanceof Teamworks) {
                return artifact;
            }
        }

        // Se não encontrou Teamworks, retornar o primeiro artefato
        return artifacts.isEmpty() ? null : artifacts.values().iterator().next();
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
    // INITIALIZATION METHODS - MELHORADOS
    // =========================================================================

    /**
     * Inicializa manifest e contexto JAXB
     */
    private void initializeManifestAndContext(String extractionPath) throws Exception {
        logDetail("Initializing ProcessLoader for path: " + extractionPath);

        // Carregar manifest principal
        File mainManifestFile = new File(extractionPath, META_INF_DIR + File.separator + PACKAGE_XML);
        if (!mainManifestFile.exists()) {
            throw new FileNotFoundException("Main manifest not found: " + mainManifestFile.getAbsolutePath());
        }

        logDetail("Loading main manifest: " + mainManifestFile.getAbsolutePath());
        this.mainManifest = loadManifest(mainManifestFile);
        logInfo("Main manifest loaded successfully with " +
                (mainManifest.getObjects() != null ? mainManifest.getObjects().size() : 0) + " objects");

        // Inicializar contexto JAXB universal
        logDetail("Initializing universal JAXB context...");
        this.universalJaxbContext = createUniversalJAXBContext();
        logDetail("Universal JAXB context initialized successfully");

        // Carregar manifests de toolkits se existirem
        loadToolkitManifests(extractionPath);

        logInfo("ProcessLoader initialized successfully");
    }

    /**
     * Carrega manifest XML
     */
    private Package loadManifest(File manifestFile) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Package.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        try (FileInputStream fis = new FileInputStream(manifestFile);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            return (Package) unmarshaller.unmarshal(isr);
        }
    }

    /**
     * Cria contexto JAXB universal para todos os tipos
     */
    private JAXBContext createUniversalJAXBContext() throws Exception {
        return JAXBContext.newInstance(
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks.class,
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram.class,
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package.class
        );
    }

    /**
     * Carrega manifests de toolkits
     */
    private void loadToolkitManifests(String basePath) {
        File toolkitsDir = new File(basePath, "toolkits");
        if (!toolkitsDir.exists() || !toolkitsDir.isDirectory()) {
            return;
        }

        File[] toolkitDirs = toolkitsDir.listFiles(File::isDirectory);
        if (toolkitDirs == null) return;

        for (File toolkitDir : toolkitDirs) {
            loadToolkitManifest(toolkitDir);
        }
    }

    /**
     * Carrega manifest de um toolkit específico
     */
    private void loadToolkitManifest(File toolkitDir) {
        try {
            File manifestFile = new File(toolkitDir, META_INF_DIR + File.separator + PACKAGE_XML);
            if (manifestFile.exists()) {
                Package pkg = loadManifest(manifestFile);
                manifestCache.put(toolkitDir.getName(), pkg);
                logDetail("Toolkit manifest loaded: " + toolkitDir.getName());
            }
        } catch (Exception e) {
            logError("Error loading toolkit manifest for " + toolkitDir.getName() + ": " + e.getMessage());
        }
    }

    // =========================================================================
    // ADDITIONAL UTILITY METHODS
    // =========================================================================

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
        if (instanceCache != null) {
            instanceCache.clear();
        }
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

            @Override
            public void flush() {
                // Logger silencioso - não faz nada
            }

            @Override
            public void close() {
                // Logger silencioso - não faz nada
            }
        };
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
            return String.format("LoadingStatistics{artifacts=%d, dependencies=%d, cache=%d, time=%dms}",
                    artifactsLoaded, dependenciesResolved, cacheSize, loadingTime);
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

    // =========================================================================
    // CLEANUP AND RESOURCE MANAGEMENT
    // =========================================================================

    /**
     * Cleanup de recursos
     */
    public void cleanup() {
        try {
            clearCache();

            if (logger != null ) {
                logger.close();
            }

            jaxbContexts.clear();
            manifestCache.clear();

            logInfo("ProcessLoader cleanup completed");

        } catch (Exception e) {
            System.err.println("Error during ProcessLoader cleanup: " + e.getMessage());
        }
    }
// Em br/com/danzeroum/bpmbaw/mapeadorxml/leitor/jsonIA/ProcessLoader.java
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
    // VALIDATION AND DIAGNOSTIC METHODS
    // =========================================================================

    /**
     * Valida a integridade do manifest carregado
     */
    public boolean validateManifest() {
        if (mainManifest == null) {
            logError("Main manifest is null");
            return false;
        }

        if (mainManifest.getObjects() == null || mainManifest.getObjects().isEmpty()) {
            logError("Main manifest has no objects");
            return false;
        }

        logInfo("Manifest validation passed: " + mainManifest.getObjects().size() + " objects found");
        return true;
    }

    /**
     * Imprime diagnóstico completo do estado do ProcessLoader
     */
    public void printDiagnostics() {
        logInfo("=== ProcessLoader Diagnostics ===");
        logInfo("Root Directory: " + rootDirectoryPath);
        logInfo("Detailed Logging: " + enableDetailedLogging);
        logInfo("Main Manifest: " + (mainManifest != null ? "Loaded" : "Not Loaded"));

        if (mainManifest != null && mainManifest.getObjects() != null) {
            logInfo("Manifest Objects: " + mainManifest.getObjects().size());
        }

        logInfo("Toolkit Manifests: " + manifestCache.size());
        logInfo("JAXB Contexts: " + jaxbContexts.size());
        logInfo("Cached Artifacts: " + cacheDeArtefatos.size());
        logInfo("Instance Cache: " + (instanceCache != null ? instanceCache.size() : 0));
        logInfo("Loading Statistics: " + getLoadingStatistics());
        logInfo("=== End Diagnostics ===");
    }

    /**
     * Lista todos os objetos disponíveis no manifest
     */
    public void listAllAvailableObjects() {
        logInfo("=== Available Objects in Manifest ===");

        if (mainManifest != null && mainManifest.getObjects() != null) {
            logInfo("Main Manifest Objects:");
            for (PackageObject obj : mainManifest.getObjects()) {
                logInfo(String.format("  - ID: %s, Type: %s, Name: %s",
                        obj.getId(), obj.getType(), obj.getName()));
            }
        }

        for (Map.Entry<String, Package> entry : manifestCache.entrySet()) {
            if (entry.getValue().getObjects() != null) {
                logInfo("Toolkit " + entry.getKey() + " Objects:");
                for (PackageObject obj : entry.getValue().getObjects()) {
                    logInfo(String.format("  - ID: %s, Type: %s, Name: %s",
                            obj.getId(), obj.getType(), obj.getName()));
                }
            }
        }

        logInfo("=== End Available Objects ===");
    }

    /**
     * Procura objeto por nome (útil para debugging)
     */
    public List<PackageObject> findObjectsByName(String name) {
        List<PackageObject> results = new ArrayList<>();

        if (name == null || name.trim().isEmpty()) {
            return results;
        }

        String searchName = name.toLowerCase();

        // Procurar no manifest principal
        if (mainManifest != null && mainManifest.getObjects() != null) {
            for (PackageObject obj : mainManifest.getObjects()) {
                if (obj.getName() != null && obj.getName().toLowerCase().contains(searchName)) {
                    results.add(obj);
                }
            }
        }

        // Procurar nos toolkits
        for (Package toolkit : manifestCache.values()) {
            if (toolkit.getObjects() != null) {
                for (PackageObject obj : toolkit.getObjects()) {
                    if (obj.getName() != null && obj.getName().toLowerCase().contains(searchName)) {
                        results.add(obj);
                    }
                }
            }
        }

        return results;
    }

    /**
     * Procura objeto por tipo (útil para debugging)
     */
    public List<PackageObject> findObjectsByType(String type) {
        List<PackageObject> results = new ArrayList<>();

        if (type == null || type.trim().isEmpty()) {
            return results;
        }

        String searchType = type.toLowerCase();

        // Procurar no manifest principal
        if (mainManifest != null && mainManifest.getObjects() != null) {
            for (PackageObject obj : mainManifest.getObjects()) {
                if (obj.getType() != null && obj.getType().toLowerCase().contains(searchType)) {
                    results.add(obj);
                }
            }
        }

        // Procurar nos toolkits
        for (Package toolkit : manifestCache.values()) {
            if (toolkit.getObjects() != null) {
                for (PackageObject obj : toolkit.getObjects()) {
                    if (obj.getType() != null && obj.getType().toLowerCase().contains(searchType)) {
                        results.add(obj);
                    }
                }
            }
        }

        return results;
    }

    /**
     * Método para testar conectividade e funcionalidade básica
     */
    public boolean performSelfTest() {
        logInfo("=== ProcessLoader Self Test ===");

        try {
            // Teste 1: Validar manifest
            if (!validateManifest()) {
                logError("Self test failed: Manifest validation");
                return false;
            }

            // Teste 2: Verificar se consegue encontrar pelo menos um processo
            List<String> processes = findAvailableProcesses();
            if (processes.isEmpty()) {
                logError("Self test failed: No processes found");
                return false;
            }
            logInfo("Self test: Found " + processes.size() + " processes");

            // Teste 3: Tentar carregar o primeiro processo (se existe)
            if (!processes.isEmpty()) {
                String firstProcess = processes.get(0);
                try {
                    ArtifactLocation location = findArtifactLocation(firstProcess);
                    if (location == null) {
                        logError("Self test failed: Could not locate first process");
                        return false;
                    }
                    logInfo("Self test: Successfully located first process");
                } catch (Exception e) {
                    logError("Self test failed: Error locating first process: " + e.getMessage());
                    return false;
                }
            }

            logInfo("=== Self Test Passed ===");
            return true;

        } catch (Exception e) {
            logError("Self test failed with exception: " + e.getMessage());
            return false;
        }
    }
}