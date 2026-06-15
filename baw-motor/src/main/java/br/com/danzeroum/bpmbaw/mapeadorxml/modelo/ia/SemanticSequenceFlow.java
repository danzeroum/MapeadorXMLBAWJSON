// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/modelo/ia/SemanticSequenceFlow.java

package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.ia;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Representa um fluxo de sequência (conexão) entre dois nós, 
 * enriquecido com os nomes de origem e destino para clareza.
 */
@JsonInclude(Include.NON_NULL)
public class SemanticSequenceFlow {

    private String id;
    private String name;
    private String resolvedName;
    private String documentation;
    private String sourceRef;
    private String targetRef;
    private String sourceName; // Nome do nó de origem
    private String targetName; // Nome do nó de destino

    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResolvedName() {
        return resolvedName;
    }

    public void setResolvedName(String resolvedName) {
        this.resolvedName = resolvedName;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getSourceRef() {
        return sourceRef;
    }

    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    public String getTargetRef() {
        return targetRef;
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}