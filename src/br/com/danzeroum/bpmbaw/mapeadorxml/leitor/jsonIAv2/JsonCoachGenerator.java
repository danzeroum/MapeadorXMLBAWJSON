package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.json.JsonReport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class JsonCoachGenerator {

    private final JsonReport report;
    private final PrintWriter writer;

    public JsonCoachGenerator(PrintWriter writer, String projectName, String twxFileName) {
        this.writer = writer;
        this.report = new JsonReport(); // <-- MUDANÇA AQUI

        JsonReport.Metadata metadata = new JsonReport.Metadata();
        metadata.setProjectName(projectName);
        metadata.setTwxFile(twxFileName);
        metadata.setExportTimestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        this.report.setMetadata(metadata);
    }

    public JsonReport getReport() { // <-- MUDANÇA AQUI
        return this.report;
    }

    public void generate() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(this.report);
        writer.write(jsonOutput);
    }
}