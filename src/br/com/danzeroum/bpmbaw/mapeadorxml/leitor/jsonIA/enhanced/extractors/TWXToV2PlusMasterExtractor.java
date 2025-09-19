package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.FlowObject;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;

import java.util.ArrayList;
import java.util.List;

/**
 * TWXToV2PlusMasterExtractor - VERSÃO FINAL CORRIGIDA E ORQUESTRADA
 *
 * CORREÇÕES APLICADAS:
 * ✅ Orquestração correta: Passa o objeto 'Bpd' para o extrator de variáveis e o 'BusinessProcessDiagram' para os demais.
 * ✅ Resolve todos os erros de compilação de "incompatible types".
 * ✅ Garante que cada extrator receba a parte correta do XML para análise.
 * ✅ Mantém a compatibilidade com Java 8.
 *
 * @version 2.9.0-final-orchestration
 */
public class TWXToV2PlusMasterExtractor {

    // =========================================================================
    // MÉTODO PRINCIPAL DE EXTRAÇÃO ORQUESTRADO
    // =========================================================================

    public static ProcessDefinitionV2Plus extractComplete(Bpd bpd, ProcessLoaderV2Plus loader) {
        if (bpd == null) {
            System.err.println("⚠️ Bpd object is null, creating minimal definition");
            return createMinimalDefinition();
        }

        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();
        if (diagram == null) {
            System.err.println("⚠️ BPD does not contain a BusinessProcessDiagram. Creating minimal definition.");
            return createMinimalDefinition();
        }

        System.out.println("🚀 Starting V2+ Orchestrated Extraction...");

        ProcessDefinitionV2Plus definition = ProcessDefinitionV2Plus.create(diagram.getId());

        try {
            // 1. Extrair variáveis (passando o BPD completo)
            System.out.println("📝 Extracting variables...");
            TWXToV2PlusVariablesExtractor varExtractor = new TWXToV2PlusVariablesExtractor(loader);
            ProcessVariablesV2Plus variables = varExtractor.extractVariables(bpd);
            definition.setVariables(variables);
            System.out.println("[LOG-MASTER] Variáveis extraídas. Inputs: " + variables.getInput().size() + ", Outputs: " + variables.getOutput().size() + ", Privadas: " + variables.getPrivateVars().size());

            // 2. Extrair grafo (passando apenas o diagrama)
            System.out.println("🔗 Extracting graph...");
            ProcessGraphV2Plus graph = GraphExtractorV2Plus.extractGraph(diagram);
            definition.setGraph(graph);
            System.out.println("[LOG-MASTER] Grafo extraído. Nós: " + graph.getNodes().size() + ", Arestas: " + graph.getEdges().size());



            // 3. Extrair condições (passando apenas o diagrama)
            System.out.println("🔀 Extracting conditions...");
            TWXToV2PlusConditionsExtractor conditionsExtractor = new TWXToV2PlusConditionsExtractor();
            definition.setConditions(conditionsExtractor.extractConditions(diagram));

            // 4. Extrair lógica (passando apenas o diagrama)
            System.out.println("⚙️ Extracting logic...");
            TWXToV2PlusLogicExtractor logicExtractor = new TWXToV2PlusLogicExtractor(loader);
            List<FlowObject> allFlowObjects = GraphExtractorV2Plus.extractAllFlowObjects(diagram);
            ProcessLogicV2Plus logic = logicExtractor.extractLogic(allFlowObjects);
            definition.setLogic(logic);

            // 5. Extrair mapeamentos (passando apenas o diagrama)
            System.out.println("📄 Extracting mappings...");
            definition.setMappings(TWXToV2PlusMappingsExtractor.extractMappings(diagram));

            System.out.println("🎉 V2+ extraction completed successfully!");
            return definition;

        } catch (Exception e) {
            System.err.println("❌ Error during V2+ extraction: " + e.getMessage());
            e.printStackTrace();
            return definition; // Retorna o que foi extraído até o momento do erro
        }
    }

    // =========================================================================
    // MÉTODOS DE CRIAÇÃO E FALLBACK (Mantidos para robustez)
    // =========================================================================

    public static ProcessDefinitionV2Plus createMinimalDefinition() {
        ProcessDefinitionV2Plus definition = ProcessDefinitionV2Plus.create("minimal-process");
        definition.setName("Minimal Process");
        definition.setDescription("Minimal process created due to an extraction error or null input.");

        definition.setVariables(new ProcessVariablesV2Plus());
        definition.setGraph(createMinimalGraph());
        definition.setLogic(createMinimalLogic());
        definition.setConditions(new ArrayList<>());
        definition.setMappings(new ProcessMappingsV2Plus());

        return definition;
    }

    public static ProcessGraphV2Plus createMinimalGraph() {
        ProcessGraphV2Plus graph = ProcessGraphV2Plus.create("minimal-graph");

        ProcessNodeV2Plus startNode = new ProcessNodeV2Plus("start", ProcessNodeV2Plus.NodeType.START_EVENT, "Start", "default_lane");
        ProcessNodeV2Plus endNode = new ProcessNodeV2Plus("end", ProcessNodeV2Plus.NodeType.END_EVENT, "End", "default_lane");
        graph.addNode(startNode);
        graph.addNode(endNode);

        ProcessEdgeV2Plus edge = new ProcessEdgeV2Plus("start-to-end", "start", "end", "Default flow");
        graph.addEdge(edge);

        ProcessLaneV2Plus lane = new ProcessLaneV2Plus("default_lane", "Default Lane", ProcessLaneV2Plus.LaneType.GENERIC);
        graph.addLane(lane);

        graph.autoDetectEntryPoints();
        graph.autoDetectExitPoints();

        return graph;
    }

    public static ProcessLogicV2Plus createMinimalLogic() {
        ProcessLogicV2Plus logic = new ProcessLogicV2Plus();
        LogicItemV2Plus defaultItem = LogicItemV2Plus.createScript("lg:default_script", "Default Script", "// Default minimal script");
        logic.addItem(defaultItem);
        return logic;
    }
}