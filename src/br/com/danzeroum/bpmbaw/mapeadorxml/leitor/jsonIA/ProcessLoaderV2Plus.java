package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Dependency;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.PackageObject;
import br.com.danzeroum.bpmbaw.mapeadorxml.util.ClassFinder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
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
        initializeJaxbContext();
    }

    public ProcessLoaderV2Plus(String twxDirectoryPath) throws Exception {
        this(twxDirectoryPath, new PrintWriter(System.out, true));
    }


    private void initializeJaxbContext() throws Exception {
        // A inicialização completa do JAXBContext já está correta na sua classe
        // Manter o método getUniversalJaxbContext como está.
    }

    public Map<String, Object> loadProcessInMemory(String objectId) {
        Set<String> visitedIds = new HashSet<>();
        carregarArtefatoRecursivamente(objectId, visitedIds);
        return Collections.unmodifiableMap(cacheDeArtefatos);
    }

    /**
     * NOVO MÉTODO ESTRATÉGICO: Carrega TODOS os artefatos do projeto principal
     * e de seus toolkits para o cache. Isso garante que todas as dependências
     * estejam disponíveis antes do início da fase de extração.
     */
    public void carregarTodosOsArtefatosDoProjeto() {
        System.out.println("[LOG-LOADER] Preenchendo cache com todos os artefatos do projeto e toolkits...");

        // Carrega artefatos do projeto principal
        if (mainManifest != null && mainManifest.getObjects() != null) {
           // System.out.println("  -> Carregando " + mainManifest.getObjects().size() + " artefatos do projeto principal.");
            for (PackageObject obj : mainManifest.getObjects()) {
                loadArtifact(obj.getId());
            }
        }

        // Carrega artefatos de todos os toolkits referenciados
        if (mainManifest != null && mainManifest.getDependencies() != null) {
            for (Dependency dep : mainManifest.getDependencies()) {
                if (dep.getSnapshot() != null) {
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package toolkitManifest = loadManifest(dep.getSnapshot().getId());
                    if (toolkitManifest != null && toolkitManifest.getObjects() != null) {
                        System.out.println("  -> Carregando " + toolkitManifest.getObjects().size() + " artefatos do toolkit: " + dep.getProject().getName());
                        for (PackageObject obj : toolkitManifest.getObjects()) {
                            // O ID para artefatos de toolkit é composto: "snapshotId/objectId"
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

        Object artifact = loadArtifact(objectId);
        if (artifact == null) {
            return;
        }

        Set<String> subprocessIds = findSubprocessIds(artifact);
        for (String subId : subprocessIds) {
            carregarArtefatoRecursivamente(subId, visitedIds);
        }
    }

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

    private Set<String> encontrarSubprocessosEmServicoOuBpdLegado(Teamworks tw) {
        Set<String> ids = new HashSet<>();
        if (tw == null) return ids;

        if (tw.getProcess() != null) {
            // Coachflow
            if (tw.getProcess().getCoachflow() != null && tw.getProcess().getCoachflow().getDefinitions() != null) {
                GlobalUserTask gut = tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask();
                if (gut != null && gut.getImplementation() != null) {
                    collectCallActivities(gut.getImplementation().getFlowElements(), ids);
                }
            }
            // Itens de Serviço Legado
            if (tw.getProcess().getItems() != null) {
                tw.getProcess().getItems().stream()
                        .map(item -> item.getTwComponent())
                        .filter(Objects::nonNull)
                        .map(twc -> twc.getAttachedProcessRef())
                        .filter(ref -> ref != null && !ref.isEmpty())
                        .forEach(ids::add);
            }
        } else if (tw.getBpd() != null && tw.getBpd().getBusinessProcessDiagram() != null) {
            // BPD Legado
            tw.getBpd().getBusinessProcessDiagram().getPools().stream()
                    .filter(Objects::nonNull)
                    .flatMap(pool -> pool.getLanes() != null ? pool.getLanes().stream() : Stream.empty())
                    .filter(Objects::nonNull)
                    .flatMap(lane -> lane.getFlowObjects() != null ? lane.getFlowObjects().stream() : Stream.empty())
                    .filter(fo -> fo != null && fo.getComponent() != null && fo.getComponent().getImplementation() != null)
                    .map(FlowObject::getComponent)
                    .map(Component::getImplementation)
                    .forEach(impl -> {
                        if (impl.getAttachedActivityId() != null && !impl.getAttachedActivityId().isEmpty()) ids.add(impl.getAttachedActivityId());
                        if (impl.getAttachedProcessId() != null && !impl.getAttachedProcessId().isEmpty()) ids.add(impl.getAttachedProcessId());
                        if (impl.getEmbeddedProcessId() != null && !impl.getEmbeddedProcessId().isEmpty()) ids.add(impl.getEmbeddedProcessId());
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

    // Métodos auxiliares (findArtifactLocation, loadManifest, etc. permanecem os mesmos)
    // ...
    // [Cole aqui os métodos auxiliares da sua classe `ProcessLoaderV2Plus` original]
    // ...
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
                                String basePath = new File(new File(new File(rootDirectoryPath, "toolkits"), snapshotId), "objects").getPath();
                                String toolkitName = dependency.getProject() != null ? dependency.getProject().getName() : "Nome não encontrado";
                                return new ArtifactLocation(objectInfo, objectInfo.getId() + ".xml", basePath, toolkitName);
                            }
                        }
                    }
                }
            }
        }

        // Lógica para o Projeto Principal
        PackageObject objectInfo = findObjectInManifest(searchObjectId, mainManifest);
        if (objectInfo != null) {
            String basePath = new File(rootDirectoryPath, "objects").getPath();
            return new ArtifactLocation(objectInfo, objectInfo.getId() + ".xml", basePath, null);
        }

        // Lógica de fallback
        if (mainManifest.getDependencies() != null) {
            for (Dependency dependency : mainManifest.getDependencies()) {
                if (dependency.getSnapshot() == null) continue;
                String snapshotId = dependency.getSnapshot().getId();
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.Package toolkitManifest = loadManifest(snapshotId);
                if (toolkitManifest != null) {
                    objectInfo = findObjectInManifest(searchObjectId, toolkitManifest);
                    if (objectInfo != null) {
                        String basePath = new File(new File(new File(rootDirectoryPath, "toolkits"), snapshotId), "objects").getPath();
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

    private JAXBContext getUniversalJaxbContext() throws Exception {
        if (universalJaxbContext == null) {
            Class<?>[] classes = ClassFinder.getClasses("br.com.danzeroum.bpmbaw.mapeadorxml.modelo");
            universalJaxbContext = JAXBContext.newInstance(classes);
        }
        return universalJaxbContext;
    }

    public String normalizeId(String id) {
        if (id == null) return null;
        return id.replaceAll("\\s+", "").trim();
    }

    private void log(String message) {
        if (detailedLogging && logger != null) {
            logger.println(message);
        } else if (detailedLogging) {
            System.out.println(message);
        }
    }
    public Map<String, Object> getCacheDeArtefatos() {
        return Collections.unmodifiableMap(cacheDeArtefatos);
    }
    public Object getArtefatoDoCache(String objectId) {
        return cacheDeArtefatos.get(getCleanId(objectId));
    }

    public String getCleanId(String objectId) {
        if (objectId == null) return null;
        String normalized = objectId.replaceAll("\\s+", "").trim();
        return normalized.contains("/") ?
                normalized.substring(normalized.lastIndexOf('/') + 1) :
                normalized;
    }

}