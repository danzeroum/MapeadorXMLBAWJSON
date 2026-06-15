// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/JsonParticipantNavigator.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;



import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.json.JsonReport;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.participant.Participant;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.participant.StandardMember;

public class JsonParticipantNavigator {

    private final JsonReportGenerator generator;
    private final ProcessLoader loader;

    public JsonParticipantNavigator(JsonReportGenerator generator, ProcessLoader loader) {
        this.generator = generator;
        this.loader = loader;
    }

    /**
     * Varre todos os artefatos em cache, encontra aqueles que são
     * definições de Participantes (equipes) e os adiciona à lista centralizada do relatório.
     */
    public void runAnalysis() {
        System.out.println("[LOG-PARTICIPANT-NAV] ==> Iniciando runAnalysis. Verificando " + loader.getCacheDeArtefatos().size() + " artefatos em cache.");
        for (String artifactId : loader.getCacheDeArtefatos().keySet()) {
            Object artifactObj = loader.getArtefatoDoCache(artifactId);

            if (artifactObj instanceof Teamworks && ((Teamworks) artifactObj).getParticipant() != null) {
                Participant participant = ((Teamworks) artifactObj).getParticipant();
                System.out.println("[LOG-PARTICIPANT] Encontrada definição da equipe: " + participant.getName());
                transformToReport(participant);
            }
        }
    }

    private void transformToReport(Participant participant) {
        JsonReport.JsonParticipantGroup jsonGroup = new JsonReport.JsonParticipantGroup();
        jsonGroup.setId(participant.getId());
        jsonGroup.setName(participant.getName());

        if (participant.getStandardMembers() != null) {
            for (StandardMember member : participant.getStandardMembers()) {
                JsonReport.JsonParticipantMember jsonMember = new JsonReport.JsonParticipantMember();
                jsonMember.setName(member.getName());
                jsonMember.setType(member.getType());
                jsonGroup.getMembers().add(jsonMember);
            }
        }
        generator.getReport().getParticipantGroups().add(jsonGroup);
    }
}