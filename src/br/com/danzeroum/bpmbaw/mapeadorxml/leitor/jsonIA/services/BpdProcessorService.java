package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoader;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportGeneratorV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Pool;

/**
 * Processes legacy BPD artifacts.
 */
public class BpdProcessorService {
    private final JsonReportGeneratorV2 generator;
    private final ProcessLoader loader;

    public BpdProcessorService(JsonReportGeneratorV2 generator, ProcessLoader loader) {
        this.generator = generator;
        this.loader = loader;
    }

    public void processBpd(Bpd bpd, JsonReportV2.Artifact artifact) {
        if (bpd.getBusinessProcessDiagram() == null) return;

        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();

        // Extract participants from pools/lanes
        if (diagram.getPools() != null) {
            for (Pool pool : diagram.getPools()) {
                if (pool.getLanes() != null) {
                    for (Lane lane : pool.getLanes()) {
                        if (lane.getAttachedParticipant() != null && !lane.getAttachedParticipant().trim().isEmpty()) {
                            String participantName = resolveParticipantName(lane.getAttachedParticipant());
                            if (participantName != null && !artifact.getParticipants().contains(participantName)) {
                                artifact.getParticipants().add(participantName);
                            }
                        }
                    }
                }
            }
        }
    }

    private String resolveParticipantName(String participantId) {
        try {
            ProcessLoader.ArtifactLocation loc = loader.findArtifactLocation(participantId);
            if (loc != null && loc.objectInfo != null && loc.objectInfo.getName() != null) {
                return loc.objectInfo.getName();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
