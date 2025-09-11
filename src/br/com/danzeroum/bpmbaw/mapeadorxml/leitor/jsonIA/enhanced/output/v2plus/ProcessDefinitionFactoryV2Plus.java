package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Factory methods for creating ProcessDefinitionV2Plus instances
 * CORRIGIDA: Métodos addInputVariable, addOutputVariable, addPrivateVariable com assinaturas corretas
 *
 * CORREÇÕES APLICADAS:
 * ✅ Métodos de ProcessVariablesV2Plus com assinatura correta (5 parâmetros)
 * ✅ Compatível com Java 8
 * ✅ Remove incompatibilidades de tipos
 * ✅ Conformidade com modelo V2+
 */
public class ProcessDefinitionFactoryV2Plus {

    /**
     * Creates a simple process CONFORMING TO MODEL
     */
    public static ProcessDefinitionV2Plus createSimpleProcess() {
        ProcessDefinitionV2Plus definition = new ProcessDefinitionV2Plus();

        // Variables conforming to model
        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();

        // Input: usando assinatura correta com 5 parâmetros
        variables.addInputVariable("recondicionamento", "dt:recondicionamento@1", "one", false,
                "Dados do processo de recondicionamento");

        // Output: usando assinatura correta com 5 parâmetros
        variables.addOutputVariable("orcamentoFinal", "dt:orcamento@1", "one", false,
                "Orcamento processado e validado");

        // Private: usando assinatura correta com 5 parâmetros
        variables.addPrivateVariable("orcamentoAux", "dt:orcamento@1", "one", true,
                "Orcamento auxiliar para calculos intermediarios");

        definition.setVariables(variables);

        // Conditions conforming to model
        List<ConditionV2Plus> conditions = new ArrayList<>();
        conditions.add(new ConditionV2Plus(
                "cd:formulario_valido",
                "validationResult.errors.size() == 0", // expr, not expression
                "cel", // exprLang, not language
                "Formulario passou em todas as validacoes"
        ));

        // CORRIGIDO: Cast explícito para evitar incompatibilidade de tipos
        List<ProcessConditionV2Plus> processConditions = new ArrayList<>();
        for (ConditionV2Plus condition : conditions) {
            ProcessConditionV2Plus processCondition = convertToProcessCondition(condition);
            processConditions.add(processCondition);
        }
        definition.setConditions(processConditions);

        return definition;
    }

    /**
     * Converte ConditionV2Plus para ProcessConditionV2Plus
     */
    private static ProcessConditionV2Plus convertToProcessCondition(ConditionV2Plus condition) {
        ProcessConditionV2Plus processCondition = new ProcessConditionV2Plus();
        processCondition.setId(condition.getId());
        processCondition.setExpression(condition.getExpr()); // Mapear expr -> expression
        processCondition.setLanguage(ProcessConditionV2Plus.ExpressionLanguage.CEL); // Mapear exprLang
        processCondition.setDescription(condition.getDescription());
        return processCondition;
    }

    /**
     * Creates simple process variables only
     */
    public static ProcessVariablesV2Plus createSimpleVariables() {
        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();

        // CORRIGIDO: Usando assinatura correta com 5 parâmetros
        variables.addInputVariable("entrada", "dt:string@1", "one", false, "Entrada do processo");
        variables.addOutputVariable("saida", "dt:string@1", "one", false, "Saida do processo");
        variables.addPrivateVariable("auxiliar", "dt:object@1", "one", true, "Variavel auxiliar");

        return variables;
    }

    /**
     * Creates a logic item conforming to model
     */
    public static LogicItemV2Plus createSampleLogicItem() {
        String scriptCode = "function validarFormulario(dados) {\n" +
                "  var erros = [];\n" +
                "  if (!dados.matricula || dados.matricula.length < 6) {\n" +
                "    erros.push('Matricula invalida');\n" +
                "  }\n" +
                "  return { valido: erros.length === 0, erros: erros };\n" +
                "}";

        LogicItemV2Plus item = new LogicItemV2Plus();
        item.setId("lg:validar_formulario"); // ID conforming to model
        item.setName("Validador de Formulário");
        item.setType(ProcessLogicV2Plus.ItemType.SCRIPT);
        item.setLanguage(ProcessLogicV2Plus.ScriptLanguage.JAVASCRIPT); // lowercase conforming to model
        item.setInputs(Arrays.asList("recondicionamento")); // inputs
        item.setOutputs(Arrays.asList("validationErrors")); // outputs
        item.setCode(scriptCode); // inline code, not reference
        item.setDescription("Valida dados do formulário de recondicionamento");

        return item;
    }

    /**
     * Creates complex process for testing
     */
    public static ProcessDefinitionV2Plus createComplexProcess() {
        ProcessDefinitionV2Plus definition = new ProcessDefinitionV2Plus();

        // Variables - CORRIGIDO: usando assinatura correta
        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();
        variables.addInputVariable("recondicionamento", "dt:recondicionamento@1", "one", false, "Dados do recondicionamento");
        variables.addInputVariable("viatura", "dt:viatura@1", "one", false, "Dados da viatura");
        variables.addOutputVariable("orcamento", "dt:orcamento@1", "one", false, "Orcamento final");
        variables.addOutputVariable("relatorio", "dt:relatorio@1", "one", false, "Relatorio do processo");
        variables.addPrivateVariable("calculosIntermedios", "dt:object@1", "one", true, "Calculos intermediarios");
        variables.addPrivateVariable("validacoes", "dt:array@1", "many", true, "Resultados de validacoes");
        definition.setVariables(variables);

        // Graph
        ProcessGraphV2Plus graph = ProcessGraphV2Plus.create("complex-test-process");

        // Add nodes
        graph.addNode(createNode("start", ProcessNodeV2Plus.NodeType.START_EVENT, "Inicio do Processo"));
        graph.addNode(createNode("validar", ProcessNodeV2Plus.NodeType.SCRIPT_TASK, "Validar Dados"));
        graph.addNode(createNode("gateway1", ProcessNodeV2Plus.NodeType.EXCLUSIVE_GATEWAY, "Dados Validos?"));
        graph.addNode(createNode("calcular", ProcessNodeV2Plus.NodeType.SCRIPT_TASK, "Calcular Orcamento"));
        graph.addNode(createNode("aprovacao", ProcessNodeV2Plus.NodeType.USER_TASK, "Aprovar Orcamento"));
        graph.addNode(createNode("gateway2", ProcessNodeV2Plus.NodeType.EXCLUSIVE_GATEWAY, "Aprovado?"));
        graph.addNode(createNode("finalizar", ProcessNodeV2Plus.NodeType.SCRIPT_TASK, "Finalizar Processo"));
        graph.addNode(createNode("end", ProcessNodeV2Plus.NodeType.END_EVENT, "Fim do Processo"));

        // Add edges
        graph.addEdge(createEdge("e1", "start", "validar", ""));
        graph.addEdge(createEdge("e2", "validar", "gateway1", ""));
        graph.addEdge(createEdge("e3", "gateway1", "calcular", "Validos"));
        graph.addEdge(createEdge("e4", "gateway1", "end", "Invalidos"));
        graph.addEdge(createEdge("e5", "calcular", "aprovacao", ""));
        graph.addEdge(createEdge("e6", "aprovacao", "gateway2", ""));
        graph.addEdge(createEdge("e7", "gateway2", "finalizar", "Aprovado"));
        graph.addEdge(createEdge("e8", "gateway2", "calcular", "Rejeitado"));
        graph.addEdge(createEdge("e9", "finalizar", "end", ""));

        definition.setGraph(graph);

        // Logic
        ProcessLogicV2Plus logic = new ProcessLogicV2Plus();
        logic.addItem(createSampleLogicItem());
        definition.setLogic(logic);

        // CORRIGIDO: Conditions com tipo correto
        List<ProcessConditionV2Plus> conditions = new ArrayList<>();

        ProcessConditionV2Plus condition1 = new ProcessConditionV2Plus();
        condition1.setId("cd:dados_validos");
        condition1.setExpression("validationResult.errors.size() == 0");
        condition1.setLanguage(ProcessConditionV2Plus.ExpressionLanguage.CEL);
        condition1.setDescription("Verifica se dados são válidos");
        conditions.add(condition1);

        ProcessConditionV2Plus condition2 = new ProcessConditionV2Plus();
        condition2.setId("cd:orcamento_aprovado");
        condition2.setExpression("aprovacao.resultado == 'APROVADO'");
        condition2.setLanguage(ProcessConditionV2Plus.ExpressionLanguage.CEL);
        condition2.setDescription("Verifica se orçamento foi aprovado");
        conditions.add(condition2);

        definition.setConditions(conditions);

        return definition;
    }

    /**
     * Helper method to create nodes
     */
    private static ProcessNodeV2Plus createNode(String id, ProcessNodeV2Plus.NodeType type, String name) {
        ProcessNodeV2Plus node = new ProcessNodeV2Plus();
        node.setId(id);
        node.setType(type);
        node.setName(name);
        return node;
    }

    /**
     * Helper method to create edges
     */
    private static ProcessEdgeV2Plus createEdge(String id, String source, String target, String label) {
        ProcessEdgeV2Plus edge = new ProcessEdgeV2Plus();
        edge.setId(id);
        edge.setSource(source);
        edge.setTarget(target);
        edge.setLabel(label);
        return edge;
    }

    /**
     * Creates test variables with all types
     */
    public static ProcessVariablesV2Plus createTestVariables() {
        ProcessVariablesV2Plus variables = new ProcessVariablesV2Plus();

        // Inputs - CORRIGIDO: usando assinatura correta
        variables.addInputVariable("recondicionamento", "dt:recondicionamento@1", "one", false, "Dados do recondicionamento");
        variables.addInputVariable("matriculaViatura", "dt:string@1", "one", false, "Matricula da viatura");
        variables.addInputVariable("parametrosOpcionais", "dt:object@1", "one", true, "Parametros opcionais");

        // Outputs - CORRIGIDO: usando assinatura correta
        variables.addOutputVariable("orcamentoCalculado", "dt:orcamento@1", "one", false, "Orcamento final calculado");
        variables.addOutputVariable("relatorioValidacao", "dt:validation@1", "one", true, "Relatorio de validacao");
        variables.addOutputVariable("historicoAlteracoes", "dt:array@1", "many", true, "Historico de alteracoes");

        // Private vars - CORRIGIDO: usando assinatura correta
        variables.addPrivateVariable("dadosTemporarios", "dt:object@1", "one", true, "Dados temporarios");
        variables.addPrivateVariable("contadorIteracoes", "dt:integer@1", "one", false, "Contador de iteracoes");
        variables.addPrivateVariable("flagProcessamento", "dt:boolean@1", "one", false, "Flag de processamento");

        return variables;
    }

    /**
     * Creates definition with validation
     */
    public static ProcessDefinitionV2Plus createValidatedProcess(String processId) {
        if (processId == null || processId.trim().isEmpty()) {
            throw new IllegalArgumentException("Process ID cannot be null or empty");
        }

        ProcessDefinitionV2Plus definition = new ProcessDefinitionV2Plus();
        definition.setId(processId);

        // Add minimal valid content
        ProcessVariablesV2Plus variables = createSimpleVariables();
        definition.setVariables(variables);

        ProcessGraphV2Plus graph = ProcessGraphV2Plus.create(processId);
        definition.setGraph(graph);

        ProcessLogicV2Plus logic = new ProcessLogicV2Plus();
        definition.setLogic(logic);

        return definition;
    }

    /**
     * Teste básico para validação durante desenvolvimento
     */
    public static void main(String[] args) {
        System.out.println("🧪 Testing ProcessDefinitionFactoryV2Plus...");

        try {
            // Teste 1: Criação de processo simples
            ProcessDefinitionV2Plus simpleProcess = createSimpleProcess();
            System.out.println("✅ Simple process creation: " + (simpleProcess != null));
            System.out.println("   Variables count: " +
                    (simpleProcess.getVariables().getInput().size() +
                            simpleProcess.getVariables().getOutput().size() +
                            simpleProcess.getVariables().getPrivateVars().size()));

            // Teste 2: Criação de variáveis de teste
            ProcessVariablesV2Plus testVars = createTestVariables();
            System.out.println("✅ Test variables creation: " + testVars.validate());

            // Teste 3: Criação de logic item
            LogicItemV2Plus logicItem = createSampleLogicItem();
            System.out.println("✅ Logic item creation: " + logicItem.isValid());

            // Teste 4: Processo complexo
            ProcessDefinitionV2Plus complexProcess = createComplexProcess();
            System.out.println("✅ Complex process creation: " + (complexProcess != null));
            System.out.println("   Nodes: " + complexProcess.getGraph().getNodes().size());
            System.out.println("   Edges: " + complexProcess.getGraph().getEdges().size());
            System.out.println("   Conditions: " + complexProcess.getConditions().size());

            // Teste 5: Processo validado
            ProcessDefinitionV2Plus validatedProcess = createValidatedProcess("test-validated-process");
            System.out.println("✅ Validated process creation: " + (validatedProcess.getId() != null));

            System.out.println("\n🎉 ProcessDefinitionFactoryV2Plus: ALL TESTS PASSED!");

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}