package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.fixes;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane;

import java.util.ArrayList;
import java.util.List; /**
 * Se a classe Pool não existir no modelo bpmn, criar uma versão compatível
 */
public class PoolFix {

    /**
     * Esta classe deve ser adicionada ao pacote modelo.bpmn se não existir
     */
    private String id;
    private String name;
    private List<Lane> lanes;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Lane> getLanes() {
        return lanes != null ? lanes : new ArrayList<>();
    }
    public void setLanes(List<Lane> lanes) {
        this.lanes = lanes;
    }
}
