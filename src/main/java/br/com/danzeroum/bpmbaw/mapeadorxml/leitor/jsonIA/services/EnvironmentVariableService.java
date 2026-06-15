package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.envar.EnvironmentVariable;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.envar.EnvironmentVariableSet;
import com.google.gson.Gson;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Serviço para encontrar e resolver variáveis de ambiente (tw.env.*)
 * utilizadas em um processo.
 */
public class EnvironmentVariableService {

    private final String rootDirectoryPath;
    private final Pattern envVarPattern = Pattern.compile("(tw\\.env\\.[a-zA-Z0-9_]+)");

    public EnvironmentVariableService(String rootDirectoryPath) {
        this.rootDirectoryPath = rootDirectoryPath;
    }

    /**
     * Orquestra a busca e resolução de variáveis de ambiente.
     * @param report O relatório JSON já populado com a estrutura do processo.
     */
    public List<JsonReportV2.EnvironmentVariableUsage> findAndResolveUsedVariables(JsonReportV2 report) {
        // 1. Encontra todas as variáveis de ambiente usadas no relatório
        Set<String> usedVariables = findAllUsedEnvironmentVariables(report);
        System.out.println("[ENV-SERVICE] Variáveis de ambiente utilizadas encontradas: " + usedVariables.size());

        // 2. Resolve os valores dessas variáveis a partir dos arquivos XML
        Map<String, String> resolvedValues = resolveVariableValues(usedVariables);
        System.out.println("[ENV-SERVICE] Valores resolvidos: " + resolvedValues.size());

        // 3. Formata o resultado para o modelo do JSON
        List<JsonReportV2.EnvironmentVariableUsage> result = new ArrayList<>();
        for (String varName : usedVariables) {
            JsonReportV2.EnvironmentVariableUsage usage = new JsonReportV2.EnvironmentVariableUsage();
            usage.setName(varName);
            usage.setValue(resolvedValues.getOrDefault(varName, "VALOR_NAO_ENCONTRADO"));
            result.add(usage);
        }

        result.sort(Comparator.comparing(JsonReportV2.EnvironmentVariableUsage::getName));
        return result;
    }

    /**
     * Varre todo o objeto de relatório em busca de ocorrências de "tw.env.*".
     */
    private Set<String> findAllUsedEnvironmentVariables(JsonReportV2 report) {
        Set<String> found = new HashSet<>();
        // Converte o relatório inteiro para uma string JSON para fazer uma busca global.
        String reportAsString = new Gson().toJson(report);

        Matcher matcher = envVarPattern.matcher(reportAsString);
        while (matcher.find()) {
            found.add(matcher.group(1));
        }
        return found;
    }

    /**
     * Lê os arquivos XML de variáveis de ambiente para encontrar os valores.
     * Reutiliza a lógica do antigo JsonEnvironmentNavigator.
     */
    private Map<String, String> resolveVariableValues(Set<String> usedVariables) {
        Map<String, String> resolved = new HashMap<>();
        File objectsDir = new File(rootDirectoryPath, "objects");
        if (!objectsDir.isDirectory()) {
            return resolved;
        }

        // Variáveis de ambiente geralmente estão em arquivos 62.*.xml
        File[] files = objectsDir.listFiles((dir, name) -> name.startsWith("62.") && name.endsWith(".xml"));
        if (files == null) return resolved;

        try {
            JAXBContext context = JAXBContext.newInstance(Teamworks.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            for (File envFile : files) {
                String xmlContent = new String(Files.readAllBytes(envFile.toPath()), StandardCharsets.UTF_8);
                if (xmlContent.startsWith("\uFEFF")) {
                    xmlContent = xmlContent.substring(1);
                }
                Teamworks tw = (Teamworks) unmarshaller.unmarshal(new StringReader(xmlContent));

                if (tw != null && tw.getEnvironmentVariableSet() != null) {
                    EnvironmentVariableSet envSet = tw.getEnvironmentVariableSet();
                    if (envSet.getEnvVars() != null) {
                        for (EnvironmentVariable envVar : envSet.getEnvVars()) {
                            String fullName = "tw.env." + envVar.getName();
                            if (usedVariables.contains(fullName)) {
                                resolved.put(fullName, envVar.getDefaultValue());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ENV-SERVICE] ERRO ao ler arquivos de variáveis de ambiente: " + e.getMessage());
        }
        return resolved;
    }
}