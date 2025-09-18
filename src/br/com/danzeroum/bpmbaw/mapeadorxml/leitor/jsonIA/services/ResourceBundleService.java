package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.resources.ResourceBundleGroup;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.resources.ResourceBundleKey;
import com.google.gson.Gson;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Serviço para encontrar e resolver recursos de localização (tw.resource.*)
 * utilizados em um processo.
 */
public class ResourceBundleService {

    private final String rootDirectoryPath;
    // Regex ajustado para permitir múltiplos pontos no nome da chave
    private final Pattern rbPattern = Pattern.compile("(tw\\.resource\\.[a-zA-Z0-9_.]+)");

    public ResourceBundleService(String rootDirectoryPath) {
        this.rootDirectoryPath = rootDirectoryPath;
    }

    /**
     * Orquestra a busca e resolução dos recursos de localização.
     * @param report O relatório JSON já populado com a estrutura do processo.
     */
    public List<JsonReportV2.ResourceBundleUsage> findAndResolveUsedVariables(JsonReportV2 report) {
        Set<String> usedKeys = findAllUsedResourceBundles(report);
        System.out.println("[RB-SERVICE] Chaves de localização utilizadas encontradas: " + usedKeys.size());

        Map<String, String> resolvedValues = resolveKeyValues(usedKeys);
        System.out.println("[RB-SERVICE] Valores resolvidos: " + resolvedValues.size());

        List<JsonReportV2.ResourceBundleUsage> result = new ArrayList<>();
        for (String keyName : usedKeys) {
            JsonReportV2.ResourceBundleUsage usage = new JsonReportV2.ResourceBundleUsage();
            usage.setKey(keyName);
            usage.setValue(resolvedValues.getOrDefault(keyName, "VALOR_NAO_ENCONTRADO"));
            result.add(usage);
        }

        result.sort(Comparator.comparing(JsonReportV2.ResourceBundleUsage::getKey));
        return result;
    }

    /**
     * Varre todo o objeto de relatório em busca de ocorrências de "tw.resource.*".
     */
    private Set<String> findAllUsedResourceBundles(JsonReportV2 report) {
        Set<String> found = new HashSet<>();
        String reportAsString = new Gson().toJson(report);

        Matcher matcher = rbPattern.matcher(reportAsString);
        while (matcher.find()) {
            found.add(matcher.group(1));
        }
        return found;
    }

    /**
     * Lê os arquivos XML de Resource Bundles para encontrar os valores.
     * Baseado na lógica do antigo JsonResourceBundleNavigator.
     */
    private Map<String, String> resolveKeyValues(Set<String> usedKeys) {
        Map<String, String> resolved = new HashMap<>();
        File objectsDir = new File(rootDirectoryPath, "objects");
        if (!objectsDir.isDirectory()) {
            return resolved;
        }

        // Recursos de localização geralmente estão em arquivos 65.*.xml
        File[] files = objectsDir.listFiles((dir, name) -> name.startsWith("50.") && name.endsWith(".xml"));
        if (files == null) return resolved;

        try {
            JAXBContext context = JAXBContext.newInstance(Teamworks.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            for (File rbFile : files) {
                String xmlContent = new String(Files.readAllBytes(rbFile.toPath()), StandardCharsets.UTF_8);
                if (xmlContent.startsWith("\uFEFF")) {
                    xmlContent = xmlContent.substring(1);
                }
                Teamworks tw = (Teamworks) unmarshaller.unmarshal(new StringReader(xmlContent));

                if (tw != null && tw.getResourceBundleGroup() != null) {
                    ResourceBundleGroup group = tw.getResourceBundleGroup();
                    if (group.getResourceBundles() != null) {
                        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.resources.ResourceBundle bundle : group.getResourceBundles()) {
                            if (bundle.getResourceBundleKeys() != null) {
                                for (ResourceBundleKey key : bundle.getResourceBundleKeys()) {
                                    String fullKey = "tw.resource." + group.getName() + "." + key.getKey();
                                    if (usedKeys.contains(fullKey)) {
                                        resolved.put(fullKey, key.getValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[RB-SERVICE] ERRO ao ler arquivos de recursos de localização: " + e.getMessage());
        }
        return resolved;
    }
}