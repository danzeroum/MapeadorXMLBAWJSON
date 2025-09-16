package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessDefinitionV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;

/**
 * Orquestrador central para a extração de processos modernos (BPMN 2.0).
 * @version 2.2 - Final Deep Extraction Orchestrator
 */
public class BpmnProcessExtractor {

    public static ProcessDefinitionV2Plus extractProcessDefinition(Definitions definitions, ProcessLoaderV2Plus loader) {
        if (definitions == null || definitions.getProcess() == null) {
            System.err.println("⚠️ Definitions ou Process aninhado nulos. Não é possível extrair dados do BPMN.");
            return new ProcessDefinitionV2Plus();
        }

        Process process = definitions.getProcess();
        ProcessDefinitionV2Plus processDefinition = ProcessDefinitionV2Plus.create(process.getId());
        processDefinition.setName(process.getName());

        System.out.println("BPMN Extractor: Iniciando extração final para o processo '" + process.getName() + "'");

        // 1. Extrai Variáveis (Busca Profunda)
        System.out.println("  -> Extraindo variáveis (final)...");
        processDefinition.setVariables(BpmnToV2PlusVariablesExtractor.extractVariables(definitions));
        System.out.println("     ... " + processDefinition.getVariables().getTotalVariableCount() + " variáveis encontradas.");

        // 2. Extrai o Grafo de Execução
        System.out.println("  -> Extraindo grafo de execução...");
        processDefinition.setGraph(BpmnToV2PlusGraphExtractor.extractGraph(process));
        System.out.println("     ... " + processDefinition.getGraph().getNodes().size() + " nós e " + processDefinition.getGraph().getEdges().size() + " conexões encontradas.");

        // 3. Extrai a Lógica de Negócio (AGORA PASSA 'DEFINITIONS' COMPLETO)
        System.out.println("  -> Extraindo lógica de negócio (final)...");
        processDefinition.setLogic(BpmnToV2PlusLogicExtractor.extractLogic(definitions, loader));
        System.out.println("     ... " + processDefinition.getLogic().getItems().size() + " itens de lógica encontrados.");

        // 4. Extrai as Condições dos Gateways
        System.out.println("  -> Extraindo condições dos gateways...");
        processDefinition.setConditions(BpmnToV2PlusConditionsExtractor.extractConditions(process));
        System.out.println("     ... " + processDefinition.getConditions().size() + " condições encontradas.");

        System.out.println("BPMN Extractor: Extração final para o processo '" + process.getName() + "' finalizada com sucesso.");

        return processDefinition;
    }
}