package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.fixes;

/**
 * Extensões para Lane - adicionar estes métodos à classe Lane original
 */
public class LaneMethodsFix {

    /**
     * ADICIONAR ESTES MÉTODOS À CLASSE Lane:
     */

    // Método setDescription() que está faltando
    public void setDescription(String description) {
        // Adicionar campo description à classe Lane se não existir
        this.description = description;
    }

    // Campo description se não existir
    private String description;

    public String getDescription() {
        return description;
    }
}
