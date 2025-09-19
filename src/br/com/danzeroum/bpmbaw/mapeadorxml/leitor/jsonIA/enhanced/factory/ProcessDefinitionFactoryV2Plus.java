package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.factory;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import java.util.*;

/**
 * ProcessDefinitionFactoryV2Plus - Factory para criar definições de processo V2Plus
 *
 * Versão corrigida usando apenas classes existentes no projeto
 *
 * @version 3.1.0 - Corrigida com classes reais
 * @author Enhanced BAW Analysis System
 */
public class ProcessDefinitionFactoryV2Plus {

    // =========================================================================
    // FACTORY METHODS - PROCESSOS SIMPLES
    // =========================================================================

    /**
     * Creates a simple process CONFORMING TO MODEL
     */
    public static ProcessDefinitionV2Plus createSimpleProcess() {
        ProcessDefinitionV2Plus definition = new ProcessDefinitionV2Plus();

        // ===== VARIABLES =====
        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();

        // Criar variáveis individuais primeiro, depois adicionar
        ProcessVariableV2Plus inputVar = new ProcessVariableV2Plus();
        inputVar.setName("recondicionamento");
        inputVar.setTypeRef("dt:recondicionamento@1");
        inputVar.setCardinality("one");
        inputVar.setNullable(false);
        inputVar.setDescription("Dados do processo de recondicionamento");
        inputVar.setType("canonical-recondicionamento");
        inputVar.setList(false);

        variables.addInputVariable(inputVar);

        // Output variable
        ProcessVariableV2Plus outputVar = new ProcessVariableV2Plus();
        outputVar.setName("orcamentoFinal");
        outputVar.setTypeRef("dt:orcamento@1");
        outputVar.setCardinality("one");
        outputVar.setNullable(false);
        outputVar.setDescription("Orçamento processado e validado");
        outputVar.setType("canonical-orcamento");
        outputVar.setList(false);

        variables.addOutputVariable(outputVar);

        // Private variable
        ProcessVariableV2Plus privateVar = new ProcessVariableV2Plus();
        privateVar.setName("orcamentoAux");
        privateVar.setTypeRef("dt:orcamento@1");
        privateVar.setCardinality("one");
        privateVar.setNullable(true);
        privateVar.setDescription("Orçamento auxiliar para cálculos intermediários");
        privateVar.setType("canonical-orcamento");
        privateVar.setList(false);

        variables.addPrivateVariable(privateVar);

        definition.setVariables(variables);

        // ===== GRAPH =====
        ProcessGraphV2Plus graph = createSimpleGraph();
        definition.setGraph(graph);

        // ===== CONDITIONS =====
        List<ProcessConditionV2Plus> conditions = createSimpleConditions();
        definition.setConditions(conditions);

        // ===== MAPPINGS =====
        ProcessMappingsV2Plus mappings = createSimpleMappings();
        definition.setMappings(mappings);

        // ===== LOGIC =====
        ProcessLogicV2Plus logic = createSimpleLogic();
        definition.setLogic(logic);

        // ===== METADATA =====
        definition.setStats(generateStats(definition));

        return definition;
    }

    /**
     * Creates a complex process with full features
     */
    public static ProcessDefinitionV2Plus createComplexProcess() {
        ProcessDefinitionV2Plus definition = new ProcessDefinitionV2Plus();

        // ===== VARIABLES - Múltiplas de cada tipo =====
        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();

        // Input variables
        ProcessVariableV2Plus input1 = createVariable(
                "recondicionamento", "dt:recondicionamento@1", "one", false,
                "Dados completos do processo de recondicionamento"
        );
        variables.addInputVariable(input1);

        ProcessVariableV2Plus input2 = createVariable(
                "viatura", "dt:viatura@1", "one", false,
                "Informações da viatura a ser recondicionada"
        );
        variables.addInputVariable(input2);

        ProcessVariableV2Plus input3 = createVariable(
                "instalacao", "dt:instalacao@1", "one", true,
                "Dados da instalação responsável"
        );
        variables.addInputVariable(input3);

        // Output variables
        ProcessVariableV2Plus output1 = createVariable(
                "orcamentoFinal", "dt:orcamento@1", "one", false,
                "Orçamento final aprovado"
        );
        variables.addOutputVariable(output1);

        ProcessVariableV2Plus output2 = createVariable(
                "validationResult", "dt:validation@1", "one", false,
                "Resultado das validações aplicadas"
        );
        variables.addOutputVariable(output2);

        ProcessVariableV2Plus output3 = createVariable(
                "historico", "dt:historicodatas@1", "many", true,
                "Histórico de datas do processo"
        );
        variables.addOutputVariable(output3);

        // Private variables
        ProcessVariableV2Plus private1 = createVariable(
                "orcamentoAux", "dt:orcamento@1", "one", true,
                "Orçamento temporário para cálculos"
        );
        variables.addPrivateVariable(private1);

        ProcessVariableV2Plus private2 = createVariable(
                "validationErrors", "dt:errorlist@1", "many", true,
                "Lista de erros encontrados durante validação"
        );
        variables.addPrivateVariable(private2);

        definition.setVariables(variables);

        // ===== GRAPH - Processo complexo =====
        ProcessGraphV2Plus graph = createComplexGraph();
        definition.setGraph(graph);

        // ===== CONDITIONS - Múltiplas condições =====
        List<ProcessConditionV2Plus> conditions = createComplexConditions();
        definition.setConditions(conditions);

        // ===== MAPPINGS - Mapeamentos completos =====
        ProcessMappingsV2Plus mappings = createComplexMappings();
        definition.setMappings(mappings);

        // ===== LOGIC - Lógica completa =====
        ProcessLogicV2Plus logic = createComplexLogic();
        definition.setLogic(logic);

        // ===== METADATA =====
        definition.setStats(generateStats(definition));

        return definition;
    }

    /**
     * Creates an empty process definition
     */
    public static ProcessDefinitionV2Plus createEmptyProcess() {
        ProcessDefinitionV2Plus definition = new ProcessDefinitionV2Plus();

        // Inicializar com estruturas vazias
        definition.setVariables(new ProcessVariablesV2Plus());
        definition.setGraph(new ProcessGraphV2Plus());
        definition.setConditions(new ArrayList<ProcessConditionV2Plus>());
        definition.setMappings(new ProcessMappingsV2Plus());
        definition.setLogic(new ProcessLogicV2Plus());

        return definition;
    }

    // =========================================================================
    // HELPER METHODS - CRIAÇÃO DE COMPONENTES
    // =========================================================================

    /**
     * Helper para criar uma variável completa
     */
    private static ProcessVariableV2Plus createVariable(String name, String typeRef,
                                                        String cardinality, boolean nullable,
                                                        String description) {
        ProcessVariableV2Plus var = new ProcessVariableV2Plus();
        var.setName(name);
        var.setTypeRef(typeRef);
        var.setCardinality(cardinality);
        var.setNullable(nullable);
        var.setDescription(description);
        var.setList("many".equals(cardinality));

        // Inferir type a partir do typeRef
        if (typeRef != null && typeRef.startsWith("dt:")) {
            String type = typeRef.substring(3);
            int atIndex = type.indexOf('@');
            if (atIndex > 0) {
                type = type.substring(0, atIndex);
            }
            var.setType("canonical-" + type);
        }

        return var;
    }

    /**
     * Cria um grafo simples
     */
    private static ProcessGraphV2Plus createSimpleGraph() {
        ProcessGraphV2Plus graph = new ProcessGraphV2Plus();

        // Nodes
        List<ProcessNodeV2Plus> nodes = new ArrayList<ProcessNodeV2Plus>();

        // Start node
        ProcessNodeV2Plus startNode = new ProcessNodeV2Plus();
        startNode.setId("n_start");
        startNode.setType(ProcessNodeV2Plus.NodeType.START_EVENT);
        startNode.setName("Início do Processo");
        startNode.setLane("operations");
        nodes.add(startNode);

        // Task node
        ProcessNodeV2Plus taskNode = new ProcessNodeV2Plus();
        taskNode.setId("n_validate");
        taskNode.setType(ProcessNodeV2Plus.NodeType.SCRIPT);
        taskNode.setName("Validar Formulário");
        taskNode.setLane("operations");
        nodes.add(taskNode);

        // Gateway node
        ProcessNodeV2Plus gatewayNode = new ProcessNodeV2Plus();
        gatewayNode.setId("n_gateway");
        gatewayNode.setType(ProcessNodeV2Plus.NodeType.GATEWAY);
        gatewayNode.setName("Formulário Válido?");
        gatewayNode.setLane("operations");
        nodes.add(gatewayNode);

        // End node
        ProcessNodeV2Plus endNode = new ProcessNodeV2Plus();
        endNode.setId("n_end");
        endNode.setType(ProcessNodeV2Plus.NodeType.END_EVENT);
        endNode.setName("Fim do Processo");
        endNode.setLane("operations");
        nodes.add(endNode);

        graph.setNodes(nodes);

        // Edges
        List<ProcessEdgeV2Plus> edges = new ArrayList<ProcessEdgeV2Plus>();

        ProcessEdgeV2Plus edge1 = new ProcessEdgeV2Plus();
        edge1.setId("e_start_to_validate");
        edge1.setSource("n_start");
        edge1.setTarget("n_validate");
        edge1.setLabel("");
        edges.add(edge1);

        ProcessEdgeV2Plus edge2 = new ProcessEdgeV2Plus();
        edge2.setId("e_validate_to_gateway");
        edge2.setSource("n_validate");
        edge2.setTarget("n_gateway");
        edge2.setLabel("");
        edges.add(edge2);

        ProcessEdgeV2Plus edge3 = new ProcessEdgeV2Plus();
        edge3.setId("e_gateway_to_end");
        edge3.setSource("n_gateway");
        edge3.setTarget("n_end");
        edge3.setLabel("Sim");
        edge3.setConditionRef("cond_valid");
        edges.add(edge3);

        graph.setEdges(edges);

        // Lanes
        List<ProcessLaneV2Plus> lanes = new ArrayList<ProcessLaneV2Plus>();
        ProcessLaneV2Plus lane = new ProcessLaneV2Plus();
        lane.setId("lane_operations");
        lane.setName("Operations");
        lanes.add(lane);
        graph.setLanes(lanes);

        // Entry and End points
        graph.setEntryPoints(Arrays.asList("n_start"));
        graph.setEndPoints(Arrays.asList("n_end"));

        return graph;
    }

    /**
     * Cria um grafo complexo
     */
    private static ProcessGraphV2Plus createComplexGraph() {
        ProcessGraphV2Plus graph = createSimpleGraph();

        // Adicionar mais nodes e edges para complexidade
        List<ProcessNodeV2Plus> nodes = new ArrayList<ProcessNodeV2Plus>(graph.getNodes());

        // Adicionar subprocess
        ProcessNodeV2Plus subprocessNode = new ProcessNodeV2Plus();
        subprocessNode.setId("n_subprocess");
        subprocessNode.setType(ProcessNodeV2Plus.NodeType.SUBPROCESS);
        subprocessNode.setName("Processar Orçamento");
        subprocessNode.setLane("finance");
        nodes.add(subprocessNode);

        // Adicionar user task
        ProcessNodeV2Plus userTaskNode = new ProcessNodeV2Plus();
        userTaskNode.setId("n_approval");
        userTaskNode.setType(ProcessNodeV2Plus.NodeType.USER_TASK);
        userTaskNode.setName("Aprovar Orçamento");
        userTaskNode.setLane("management");
        nodes.add(userTaskNode);

        graph.setNodes(nodes);

        // Adicionar lane finance
        List<ProcessLaneV2Plus> lanes = new ArrayList<ProcessLaneV2Plus>(graph.getLanes());

        ProcessLaneV2Plus financeLane = new ProcessLaneV2Plus();
        financeLane.setId("lane_finance");
        financeLane.setName("Finance");
        lanes.add(financeLane);

        ProcessLaneV2Plus mgmtLane = new ProcessLaneV2Plus();
        mgmtLane.setId("lane_management");
        mgmtLane.setName("Management");
        lanes.add(mgmtLane);

        graph.setLanes(lanes);

        return graph;
    }

    /**
     * Cria condições simples
     */
    private static List<ProcessConditionV2Plus> createSimpleConditions() {
        List<ProcessConditionV2Plus> conditions = new ArrayList<ProcessConditionV2Plus>();

        ProcessConditionV2Plus condition = new ProcessConditionV2Plus();
        condition.setId("cond_valid");
        condition.setName("Formulário Válido");
        condition.setExpression("validationResult.isValid == true");
        condition.setLanguage("cel");
        conditions.add(condition);

        return conditions;
    }

    /**
     * Cria condições complexas
     */
    private static List<ProcessConditionV2Plus> createComplexConditions() {
        List<ProcessConditionV2Plus> conditions = new ArrayList<ProcessConditionV2Plus>();

        // Condição válido
        conditions.add(createCondition("cond_valid", "Válido",
                "validationResult.isValid == true"));

        // Condição inválido
        conditions.add(createCondition("cond_invalid", "Inválido",
                "validationResult.isValid == false"));

        // Condição orçamento alto
        conditions.add(createCondition("cond_orcamento_alto", "Orçamento Alto",
                "orcamentoFinal.valor > 10000"));

        // Condição urgente
        conditions.add(createCondition("cond_urgente", "Urgente",
                "recondicionamento.prioridade == 'URGENTE'"));

        // Condição aprovado
        conditions.add(createCondition("cond_aprovado", "Aprovado",
                "orcamentoFinal.status == 'APROVADO'"));

        return conditions;
    }

    /**
     * Helper para criar condição
     */
    private static ProcessConditionV2Plus createCondition(String id, String name, String expression) {
        ProcessConditionV2Plus condition = new ProcessConditionV2Plus();
        condition.setId(id);
        condition.setName(name);
        condition.setExpression(expression);
        condition.setLanguage("cel");
        return condition;
    }

    /**
     * Cria mapeamentos simples
     */
    private static ProcessMappingsV2Plus createSimpleMappings() {
        ProcessMappingsV2Plus mappings = new ProcessMappingsV2Plus();
        mappings.setExprLang("cel");

        // Inicializar listas se necessário
        if (mappings.getInputs() == null) {
            mappings.setInputs(new ArrayList<InputMappingV2Plus>());
        }
        if (mappings.getOutputs() == null) {
            mappings.setOutputs(new ArrayList<OutputMappingV2Plus>());
        }

        // Input mapping
        InputMappingV2Plus inputMapping = new InputMappingV2Plus();
        inputMapping.setSourceField("request.recondicionamento");
        inputMapping.setTargetField("recondicionamento");
        inputMapping.setDescription("Map request to process variable");
        mappings.getInputs().add(inputMapping);

        // Output mapping
        OutputMappingV2Plus outputMapping = new OutputMappingV2Plus();
        outputMapping.setSourceField("orcamentoFinal");
        outputMapping.setTargetField("response.orcamento");
        outputMapping.setAlias("orcamento");
        outputMapping.setDescription("Map result to response");
        mappings.getOutputs().add(outputMapping);

        return mappings;
    }

    /**
     * Cria mapeamentos complexos
     */
    private static ProcessMappingsV2Plus createComplexMappings() {
        ProcessMappingsV2Plus mappings = new ProcessMappingsV2Plus();
        mappings.setExprLang("cel");

        // Inicializar listas
        mappings.setInputs(new ArrayList<InputMappingV2Plus>());
        mappings.setOutputs(new ArrayList<OutputMappingV2Plus>());
        mappings.setTransformations(new ArrayList<TransformationRuleV2Plus>());

        // Múltiplos input mappings
        mappings.getInputs().add(createInputMapping(
                "request.recondicionamento", "recondicionamento",
                "Dados de recondicionamento do request"
        ));
        mappings.getInputs().add(createInputMapping(
                "request.viatura", "viatura",
                "Dados da viatura do request"
        ));
        mappings.getInputs().add(createInputMapping(
                "request.instalacao", "instalacao",
                "Dados da instalação do request"
        ));

        // Múltiplos output mappings
        mappings.getOutputs().add(createOutputMapping(
                "orcamentoFinal", "response.orcamento", "orcamento",
                "Orçamento final para response"
        ));
        mappings.getOutputs().add(createOutputMapping(
                "validationResult", "response.validation", "validation",
                "Resultado da validação para response"
        ));

        // Transformation rules
        TransformationRuleV2Plus transform = new TransformationRuleV2Plus();
        transform.setId("tr_normalize_dates");
        transform.setName("Normalize Dates");
        transform.setExpression("normalizeToISO8601(date)");
        mappings.getTransformations().add(transform);

        return mappings;
    }

    /**
     * Helper para criar input mapping
     */
    private static InputMappingV2Plus createInputMapping(String source, String target, String desc) {
        InputMappingV2Plus mapping = new InputMappingV2Plus();
        mapping.setSourceField(source);
        mapping.setTargetField(target);
        mapping.setDescription(desc);
        return mapping;
    }

    /**
     * Helper para criar output mapping
     */
    private static OutputMappingV2Plus createOutputMapping(String source, String target,
                                                           String alias, String desc) {
        OutputMappingV2Plus mapping = new OutputMappingV2Plus();
        mapping.setSourceField(source);
        mapping.setTargetField(target);
        mapping.setAlias(alias);
        mapping.setDescription(desc);
        return mapping;
    }

    /**
     * Cria lógica simples
     */
    private static ProcessLogicV2Plus createSimpleLogic() {
        ProcessLogicV2Plus logic = new ProcessLogicV2Plus();

        // A classe ProcessLogicV2Plus provavelmente tem listas de scripts e validações
        // Vou usar estruturas genéricas já que não temos as classes exatas

        // Se ProcessLogicV2Plus tiver um mapa ou lista genérica para scripts
        Map<String, Object> scriptData = new HashMap<String, Object>();
        scriptData.put("id", "script_validate");
        scriptData.put("language", "javascript");
        scriptData.put("code", "// Validation logic\nreturn isValid(input);");
        scriptData.put("inputs", Arrays.asList("recondicionamento"));
        scriptData.put("outputs", Arrays.asList("validationResult"));

        // Se ProcessLogicV2Plus tiver métodos para adicionar dados
        if (logic.getMetadata() == null) {
            logic.setMetadata(new HashMap<String, Object>());
        }
        logic.getMetadata().put("script_validate", scriptData);

        // Para validações
        Map<String, Object> validationData = new HashMap<String, Object>();
        validationData.put("id", "val_required");
        validationData.put("when", "step == 'Validar Formulário'");
        validationData.put("rule", "recondicionamento != null");
        validationData.put("severity", "error");
        validationData.put("messageKey", "campo.obrigatorio");
        validationData.put("description", "Recondicionamento é obrigatório");

        logic.getMetadata().put("validation_required", validationData);

        return logic;
    }

    /**
     * Cria lógica complexa
     */
    private static ProcessLogicV2Plus createComplexLogic() {
        ProcessLogicV2Plus logic = new ProcessLogicV2Plus();

        // Inicializar metadata se necessário
        if (logic.getMetadata() == null) {
            logic.setMetadata(new HashMap<String, Object>());
        }

        // Scripts múltiplos
        List<Map<String, Object>> scripts = new ArrayList<Map<String, Object>>();

        Map<String, Object> validateScript = new HashMap<String, Object>();
        validateScript.put("id", "script_validate");
        validateScript.put("language", "javascript");
        validateScript.put("code", "// Validar formulário\nreturn validateForm(recondicionamento);");
        validateScript.put("inputs", Arrays.asList("recondicionamento"));
        validateScript.put("outputs", Arrays.asList("validationResult"));
        scripts.add(validateScript);

        Map<String, Object> calculateScript = new HashMap<String, Object>();
        calculateScript.put("id", "script_calculate");
        calculateScript.put("language", "javascript");
        calculateScript.put("code", "// Calcular orçamento\nreturn calculateBudget(viatura, instalacao);");
        calculateScript.put("inputs", Arrays.asList("viatura", "instalacao"));
        calculateScript.put("outputs", Arrays.asList("orcamentoAux"));
        scripts.add(calculateScript);

        logic.getMetadata().put("scripts", scripts);

        // Validações múltiplas
        List<Map<String, Object>> validations = new ArrayList<Map<String, Object>>();

        Map<String, Object> matriculaValidation = new HashMap<String, Object>();
        matriculaValidation.put("id", "val_matricula");
        matriculaValidation.put("description", "Validar Matrícula");
        matriculaValidation.put("rule", "!matches(recondicionamento.viatura.matricula, '^[A-Z0-9]{6,12}$')");
        matriculaValidation.put("severity", "error");
        matriculaValidation.put("messageKey", "matricula.formato.invalido");
        matriculaValidation.put("when", "step == 'Validar Formulário'");
        validations.add(matriculaValidation);

        Map<String, Object> orcamentoValidation = new HashMap<String, Object>();
        orcamentoValidation.put("id", "val_orcamento");
        orcamentoValidation.put("description", "Validar Orçamento");
        orcamentoValidation.put("rule", "orcamentoAux.valor <= 0");
        orcamentoValidation.put("severity", "error");
        orcamentoValidation.put("messageKey", "orcamento.valor.positivo");
        orcamentoValidation.put("when", "step == 'Processar Orçamento'");
        validations.add(orcamentoValidation);

        Map<String, Object> transporteValidation = new HashMap<String, Object>();
        transporteValidation.put("id", "val_transporte");
        transporteValidation.put("description", "Validar Responsável Transporte");
        transporteValidation.put("rule", "isEmpty(recondicionamento.transporteEntrega.responsavelTransporte.value)");
        transporteValidation.put("severity", "error");
        transporteValidation.put("messageKey", "campo.obrigatorio");
        transporteValidation.put("when", "step == 'Validar Formulário'");
        validations.add(transporteValidation);

        logic.getMetadata().put("validations", validations);

        return logic;
    }

    /**
     * Gera estatísticas do processo
     */
    private static String generateStats(ProcessDefinitionV2Plus definition) {
        Map<String, Integer> stats = new HashMap<String, Integer>();

        // Contar variáveis
        if (definition.getVariables() != null) {
            ProcessVariablesV2Plus vars = definition.getVariables();
            int inputCount = vars.getInputs() != null ? vars.getInputs().size() : 0;
            int outputCount = vars.getOutputs() != null ? vars.getOutputs().size() : 0;
            int privateCount = vars.getPrivates() != null ? vars.getPrivates().size() : 0;

            stats.put("inputVars", inputCount);
            stats.put("outputVars", outputCount);
            stats.put("privateVars", privateCount);
            stats.put("totalVars", inputCount + outputCount + privateCount);
        }

        // Contar elementos do grafo
        if (definition.getGraph() != null) {
            ProcessGraphV2Plus graph = definition.getGraph();

            if (graph.getNodes() != null) {
                stats.put("nodes", graph.getNodes().size());

                // Contar por tipo de nó
                int startEvents = 0;
                int endEvents = 0;
                int tasks = 0;
                int gateways = 0;

                for (ProcessNodeV2Plus node : graph.getNodes()) {
                    if (node.getType() == ProcessNodeV2Plus.NodeType.START_EVENT) startEvents++;
                    else if (node.getType() == ProcessNodeV2Plus.NodeType.END_EVENT) endEvents++;
                    else if (node.getType() == ProcessNodeV2Plus.NodeType.GATEWAY) gateways++;
                    else tasks++;
                }

                stats.put("startEvents", startEvents);
                stats.put("endEvents", endEvents);
                stats.put("gateways", gateways);
                stats.put("tasks", tasks);
            }

            if (graph.getEdges() != null) {
                stats.put("edges", graph.getEdges().size());
            }

            if (graph.getLanes() != null) {
                stats.put("lanes", graph.getLanes().size());
            }
        }

        // Contar condições
        if (definition.getConditions() != null) {
            stats.put("conditions", definition.getConditions().size());
        }

        // Contar elementos de lógica (se estiverem em metadata)
        if (definition.getLogic() != null && definition.getLogic().getMetadata() != null) {
            Map<String, Object> logicMeta = definition.getLogic().getMetadata();

            if (logicMeta.get("scripts") instanceof List) {
                stats.put("scripts", ((List<?>) logicMeta.get("scripts")).size());
            }

            if (logicMeta.get("validations") instanceof List) {
                stats.put("validations", ((List<?>) logicMeta.get("validations")).size());
            }
        }

        // Formatar como string legível
        StringBuilder sb = new StringBuilder();
        sb.append("ProcessStats{");

        boolean first = true;
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }

        sb.append("}");

        return sb.toString();
    }

    /**
     * Método auxiliar para criar um ProcessDefinitionV2Plus a partir de um TWX legado
     */
    public static ProcessDefinitionV2Plus createFromLegacyTWX(Object twxArtifact) {
        ProcessDefinitionV2Plus definition = createEmptyProcess();

        // Adicionar lógica de conversão aqui quando necessário
        // Por enquanto retorna estrutura vazia

        return definition;
    }

    /**
     * Valida se uma ProcessDefinitionV2Plus está completa e válida
     */
    public static boolean validateProcessDefinition(ProcessDefinitionV2Plus definition) {
        if (definition == null) return false;

        // Validar que tem pelo menos estruturas básicas
        if (definition.getVariables() == null) return false;
        if (definition.getGraph() == null) return false;

        // Validar que o grafo tem pelo menos start e end
        ProcessGraphV2Plus graph = definition.getGraph();
        if (graph.getNodes() == null || graph.getNodes().isEmpty()) return false;

        boolean hasStart = false;
        boolean hasEnd = false;

        for (ProcessNodeV2Plus node : graph.getNodes()) {
            if (node.getType() == ProcessNodeV2Plus.NodeType.START_EVENT) hasStart = true;
            if (node.getType() == ProcessNodeV2Plus.NodeType.END_EVENT) hasEnd = true;
        }

        if (!hasStart || !hasEnd) return false;

        // Validar que tem pelo menos uma edge se tiver mais de um nó
        if (graph.getNodes().size() > 1) {
            if (graph.getEdges() == null || graph.getEdges().isEmpty()) {
                return false;
            }
        }

        return true;
    }
}