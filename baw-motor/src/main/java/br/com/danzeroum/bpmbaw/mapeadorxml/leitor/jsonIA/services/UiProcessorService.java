package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportGeneratorV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.CoachView;

/**
 * Processes CoachView UI components.
 */
public class UiProcessorService {
    private final JsonReportGeneratorV2 generator;

    public UiProcessorService(JsonReportGeneratorV2 generator) {
        this.generator = generator;
    }

    public void processCoachView(CoachView coachView) {
        JsonReportV2.CoachView jsonCoachView = new JsonReportV2.CoachView();
        jsonCoachView.setId(coachView.getId());
        jsonCoachView.setName(coachView.getName());

        if (coachView.getInlineScripts() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.InlineScript script : coachView.getInlineScripts()) {
                JsonReportV2.InlineScript jsonScript = new JsonReportV2.InlineScript();
                jsonScript.setName(script.getName());
                jsonScript.setScriptType(script.getScriptType());
                jsonScript.setScriptBlock(script.getScriptBlock());
                jsonCoachView.getInlineScripts().add(jsonScript);
            }
        }

        generator.getReport().getUiReport().getCoachViews().add(jsonCoachView);
    }
}
