// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/JsonEnvironmentNavigator.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportGenerator;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.envar.EnvironmentVariableSet;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.json.JsonReport;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class JsonEnvironmentNavigator {

    private final JsonReportGenerator generator;
    private final String rootDirectoryPath;

    public JsonEnvironmentNavigator(JsonReportGenerator generator, String rootDirectoryPath) {
        this.generator = generator;
        this.rootDirectoryPath = rootDirectoryPath;
    }

    public void runAnalysis() {
        try {
            File objectsDir = new File(rootDirectoryPath, "objects");
            if (!objectsDir.isDirectory()) {
                System.err.println("[ERRO-ENV] Diretório 'objects' não encontrado em: " + rootDirectoryPath);
                return;
            }

            File[] files = objectsDir.listFiles((dir, name) -> name.startsWith("62.") && name.endsWith(".xml"));

            if (files == null || files.length == 0) {
                System.out.println("[LOG-ENV] Nenhum arquivo de variáveis de ambiente (iniciado com '62.') foi encontrado.");
                return;
            }

            File envFile = files[0];
            System.out.println("[LOG-ENV] Processando arquivo de variáveis de ambiente: " + envFile.getName());
            processFile(envFile);

        } catch (Exception e) {
            System.err.println("[ERRO-ENV] Falha crítica ao processar variáveis de ambiente.");
            e.printStackTrace();
        }
    }

    private void processFile(File xmlFile) throws Exception {
        String xmlContent = new String(Files.readAllBytes(xmlFile.toPath()), StandardCharsets.UTF_8);
        if (xmlContent.startsWith("\uFEFF")) {
            xmlContent = xmlContent.substring(1);
        }

        JAXBContext context = JAXBContext.newInstance(Teamworks.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Teamworks tw = (Teamworks) unmarshaller.unmarshal(new StringReader(xmlContent));

        if (tw != null && tw.getEnvironmentVariableSet() != null) {
            transformToReport(tw.getEnvironmentVariableSet());
        } else {
            System.err.println("[ERRO-ENV] O arquivo " + xmlFile.getName() + " não contém a estrutura <environmentVariableSet> esperada.");
        }
    }

    private void transformToReport(EnvironmentVariableSet envSet) {
        JsonReport.JsonEnvironmentSet jsonSet = new JsonReport.JsonEnvironmentSet();
        jsonSet.setId(envSet.getId());
        jsonSet.setName(envSet.getName());

        if (envSet.getEnvVars() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.envar.EnvironmentVariable envVar : envSet.getEnvVars()) {
                JsonReport.JsonEnvironmentVariable jsonVar = new JsonReport.JsonEnvironmentVariable();
                jsonVar.setName("tw.env."+envVar.getName());
                jsonVar.setValue(envVar.getDefaultValue());
                //jsonVar.setDescription(envVar.getDescription());
                jsonSet.getVariables().add(jsonVar);
            }
        }
        // Adiciona o conjunto de variáveis ao relatório principal
        generator.getReport().getEnvironmentVariables().add(jsonSet);
    }
}