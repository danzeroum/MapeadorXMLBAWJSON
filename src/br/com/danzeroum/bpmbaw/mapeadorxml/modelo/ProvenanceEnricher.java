package br.com.danzeroum.bpmbaw.mapeadorxml.modelo;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.EnhancedStructuredProcessReportV2;
import java.time.Instant;
import java.util.Arrays;

/**
 * Preenche os metadados e a proveniência do relatório final.
 */
public class ProvenanceEnricher {

    /**
     * Preenche os campos de metadados como description, tags e a seção de provenance
     * no objeto de relatório principal.
     *
     * @param report O objeto de relatório a ser enriquecido.
     * @param originalArtifactId O ID do artefato no sistema de origem (BAW).
     */
    public void enrichMetadata(EnhancedStructuredProcessReportV2 report, String originalArtifactId) {
        if (report == null) {
            return;
        }

        // 1. Enriquecer o bloco 'metadata' principal
        EnhancedStructuredProcessReportV2.ReportMetadata metadata = report.getMetadata();
        if (metadata == null) {
            metadata = new EnhancedStructuredProcessReportV2.ReportMetadata();
            report.setMetadata(metadata);
        }

        // Supondo que a descrição possa ser extraída de algum lugar, ou usamos um padrão.
        // metadata.setDescription("Descrição detalhada do processo " + report.getProcessDefinition().getName());

        // Adiciona as tags padrão do formato final
        // metadata.getTags().addAll(Arrays.asList("migrated-from-v1", "ai-ready"));

        // 2. Preencher a seção 'provenance' (se a classe existir e for necessária)
        // A estrutura de metadata no seu JSON já contém os campos necessários.
        // Se houver uma classe específica para 'provenance', o código seria:
        // Provenance p = metadata.getProvenance();
        // p.setSourceVersion("1.0");
        // p.setMigrationTimestamp(Instant.now().toString());
        // p.setMigrationTool("V1toV2+Migrator");
        // p.setOriginalArtifactId(originalArtifactId);
    }
}