// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/jsonIA/enhanced/output/v2plus/ParameterMappingV2Plus.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class ParameterMappingV2Plus {

    @JsonProperty("inputs")
    private List<Mapping> inputs = new ArrayList<>();

    @JsonProperty("outputs")
    private List<Mapping> outputs = new ArrayList<>();

    public List<Mapping> getInputs() {
        return inputs;
    }

    public void setInputs(List<Mapping> inputs) {
        this.inputs = inputs;
    }

    public List<Mapping> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Mapping> outputs) {
        this.outputs = outputs;
    }

    // Classe aninhada para representar um mapeamento individual
    public static class Mapping {
        @JsonProperty("source")
        private String source;

        @JsonProperty("target")
        private String target;

        public Mapping(String source, String target) {
            this.source = source;
            this.target = target;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }
    }
}