package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.json.JsonReport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class JsonReportGenerator {

    private final JsonReport report;
    private final PrintWriter writer;
    private final Map<String, String> typeNameToCanonicalIdMap = new HashMap<>();

    public JsonReportGenerator(PrintWriter writer, String projectName, String twxFileName) {
        this.writer = writer;
        this.report = new JsonReport();
        JsonReport.Metadata metadata = new JsonReport.Metadata();
        metadata.setProjectName(projectName);
        metadata.setTwxFile(twxFileName);
        metadata.setExportTimestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        this.report.setMetadata(metadata);
    }

    public JsonReport.JsonArtifact createOrGetArtifact(String id, String name, String type, String filePath) {
        return report.getArtifacts().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElseGet(() -> {
                    JsonReport.JsonArtifact newArtifact = new JsonReport.JsonArtifact();
                    newArtifact.setId(id);
                    newArtifact.setName(name);
                    newArtifact.setType(type);
                    newArtifact.setFilePath(filePath);
                    newArtifact.setVariables(new JsonReport.Variables());
                    report.getArtifacts().add(newArtifact);
                    return newArtifact;
                });
    }

    public String addBusinessObjectDefinition(JsonReport.BusinessObject bo) {
        if (bo == null || bo.getTypeName() == null || bo.getTypeName().trim().isEmpty()) {
            return bo != null ? bo.getTypeId() : null;
        }

        String typeName = bo.getTypeName();

        if (typeNameToCanonicalIdMap.containsKey(typeName)) {
            String existingId = typeNameToCanonicalIdMap.get(typeName);
            System.out.println("[LOG-GENERATOR] Definição para '" + typeName + "' já existe. Reutilizando ID canônico: " + existingId);
            return existingId;
        }

        String canonicalId = "canonical-" + typeName.toLowerCase().replaceAll("[^a-z0-9]", "-");
        System.out.println("[LOG-GENERATOR] Primeira vez vendo '" + typeName + "'. Criando novo ID canônico: " + canonicalId);

        bo.setTypeId(canonicalId);
        typeNameToCanonicalIdMap.put(typeName, canonicalId);
        report.getBusinessObjects().getDefinitions().add(bo);

        return canonicalId;
    }

    public JsonReport getReport() {
        return this.report;
    }

    public void generate() {
        report.getBusinessObjects().getDefinitions().sort(Comparator.comparing(JsonReport.BusinessObject::getTypeName));
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        String jsonOutput = gson.toJson(this.report);
        writer.write(jsonOutput);
    }
}