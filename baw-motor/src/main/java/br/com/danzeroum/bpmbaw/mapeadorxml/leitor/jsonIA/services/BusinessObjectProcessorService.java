package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services;


import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass;

/**
 * Processes TwClass business objects.
 */
public class BusinessObjectProcessorService {
    private final VariableEnricherService variableEnricher;

    public BusinessObjectProcessorService(VariableEnricherService variableEnricher) {
        this.variableEnricher = variableEnricher;
    }

    public void processTwClass(TwClass twClass) {
        JsonReportV2.VariableInfo varInfo = new JsonReportV2.VariableInfo();
        varInfo.setTypeId(twClass.getId());
        varInfo.setName(twClass.getName());
        varInfo.setList(false);

        variableEnricher.enrichVariableInfo(varInfo);
    }
}
