package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class JsonReportGeneratorV2 {

    private final JsonReportV2 report;
    private final PrintWriter writer;
    private final Map<String, String> typeNameToCanonicalIdMap = new HashMap<String, String>();

    public JsonReportGeneratorV2(PrintWriter writer, String projectName, String twxFileName) {
        this.writer = writer;
        this.report = new JsonReportV2();
        JsonReportV2.Metadata md = new JsonReportV2.Metadata();
        md.setProjectName(projectName);
        md.setTwxFile(twxFileName);
        md.setExportTimestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        this.report.setMetadata(md);
    }

    public JsonReportV2 getReport() {
        return report;
    }

    public JsonReportV2.Artifact createOrGetArtifact(String id, String name, String type, String filePath) {
        for (JsonReportV2.Artifact a : report.getArtifacts()) {
            if (id != null && id.equals(a.getId())) {
                return a;
            }
        }
        JsonReportV2.Artifact newArtifact = new JsonReportV2.Artifact();
        newArtifact.setId(id);
        newArtifact.setName(name);
        newArtifact.setType(type);
        newArtifact.setFilePath(filePath);
        report.getArtifacts().add(newArtifact);
        return newArtifact;
    }

    public String addBusinessObjectDefinition(JsonReportV2.Definition def) {
        if (def == null || def.getTypeName() == null || def.getTypeName().trim().isEmpty()) {
            return def != null ? def.getTypeId() : null;
        }
        String typeName = def.getTypeName();
        if (typeNameToCanonicalIdMap.containsKey(typeName)) {
            return typeNameToCanonicalIdMap.get(typeName);
        }
        String canonicalId = "canonical-" + typeName.toLowerCase().replaceAll("[^a-z0-9]", "-");
        def.setTypeId(canonicalId);
        typeNameToCanonicalIdMap.put(typeName, canonicalId);
        report.getBusinessObjects().getDefinitions().add(def);
        return canonicalId;
    }

    public void generate() {
        report.getBusinessObjects().getDefinitions().sort(new Comparator<JsonReportV2.Definition>() {
            @Override
            public int compare(JsonReportV2.Definition o1, JsonReportV2.Definition o2) {
                String a = o1.getTypeName() == null ? "" : o1.getTypeName();
                String b = o2.getTypeName() == null ? "" : o2.getTypeName();
                return a.compareToIgnoreCase(b);
            }
        });
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        String jsonOutput = gson.toJson(this.report);
        writer.write(jsonOutput);
        writer.flush();
    }
}
