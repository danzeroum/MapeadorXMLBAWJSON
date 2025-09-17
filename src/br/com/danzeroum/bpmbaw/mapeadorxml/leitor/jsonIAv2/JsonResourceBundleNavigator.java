// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/JsonResourceBundleNavigator.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.json.JsonReport;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.resources.ResourceBundleGroup;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.resources.ResourceBundleKey;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class JsonResourceBundleNavigator {

    private final JsonReportGenerator generator;
    private final String rootDirectoryPath;

    public JsonResourceBundleNavigator(JsonReportGenerator generator, String rootDirectoryPath) {
        this.generator = generator;
        this.rootDirectoryPath = rootDirectoryPath;
    }

    public void runAnalysis() {
        try {
            File objectsDir = new File(rootDirectoryPath, "objects");
            if (!objectsDir.isDirectory()) {
                System.err.println("[ERRO-RB] Diretório 'objects' não encontrado em: " + rootDirectoryPath);
                return;
            }

            // Busca por todos os arquivos que começam com "50."
            File[] files = objectsDir.listFiles((dir, name) -> name.startsWith("50.") && name.endsWith(".xml"));

            if (files == null || files.length == 0) {
                System.out.println("[LOG-RB] Nenhum arquivo de Resource Bundle (iniciado com '50.') foi encontrado.");
                return;
            }

            System.out.println("[LOG-RB] Encontrados " + files.length + " arquivos de Resource Bundle para processar.");

            for (File rbFile : files) {
                System.out.println("[LOG-RB] Processando arquivo: " + rbFile.getName());
                processFile(rbFile);
            }

        } catch (Exception e) {
            System.err.println("[ERRO-RB] Falha crítica ao processar Resource Bundles.");
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

        if (tw != null && tw.getResourceBundleGroup() != null) {
            transformToReport(tw.getResourceBundleGroup());
        } else {
            System.err.println("[ERRO-RB] O arquivo " + xmlFile.getName() + " não contém a tag <resourceBundleGroup> esperada.");
        }
    }

    private void transformToReport(ResourceBundleGroup group) {
        JsonReport.JsonResourceBundleGroup jsonGroup = new JsonReport.JsonResourceBundleGroup();
        jsonGroup.setId(group.getId());
        jsonGroup.setName(group.getName());

        if (group.getResourceBundles() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.resources.ResourceBundle bundle : group.getResourceBundles()) {
                if (bundle.getResourceBundleKeys() != null) {
                    for (ResourceBundleKey key : bundle.getResourceBundleKeys()) {
                        JsonReport.JsonResourceBundleKey jsonKey = new JsonReport.JsonResourceBundleKey();
                        jsonKey.setKey("tw.resource."+group.getName()+"."+key.getKey());
                        jsonKey.setValue(key.getValue());
                        jsonGroup.getKeys().add(jsonKey);
                    }
                }
            }
        }
        generator.getReport().getResourceBundles().add(jsonGroup);
    }
}