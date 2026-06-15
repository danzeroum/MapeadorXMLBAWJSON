package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model.AIReadinessScore;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model.BusinessContext;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Instant;
import java.util.*;

// ===============================================
// 2. PROCESSLANE CLASS (FALTANTE)
// ===============================================

public class ProcessLane {
    private String id;
    private String name;
    private String partitionElementRef;
    private List<String> flowNodeRefs;

    public ProcessLane() {
        this.flowNodeRefs = new ArrayList<>();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPartitionElementRef() { return partitionElementRef; }
    public void setPartitionElementRef(String partitionElementRef) { this.partitionElementRef = partitionElementRef; }

    public List<String> getFlowNodeRefs() { return flowNodeRefs; }
    public void setFlowNodeRefs(List<String> flowNodeRefs) { this.flowNodeRefs = flowNodeRefs; }
}