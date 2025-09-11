// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/modelo/ia/SemanticFlowNode.java

package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.ia;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Representa um nó de fluxo (tarefa, gateway, evento) enriquecido com dados semânticos
 * para melhor interpretação por IAs e desenvolvedores.
 */
@JsonInclude(Include.NON_NULL)
public class SemanticFlowNode {

    private String id;
    private String type; // Tipo do nó (ex: UserTask, ExclusiveGateway)
    private String name; // Nome original (pode ser uma chave de localização)
    private String resolvedName; // Nome legível para humanos
    private String documentation; // Descrição de negócio do elemento

    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}