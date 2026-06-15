package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.transformers;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transforma um relatório V2, rico em dados extraídos, para a estrutura semântica V2Plus.
 * Atua como a ponte entre as duas arquiteturas, garantindo que nenhuma informação seja perdida.
 * @version 2.1 - Lossless Transformation
 */
public class V2toV2PlusTransformer {

    public EnhancedStructuredProcessReportV2 transform(JsonReportV2 reportV2, String processId, String projectName) {
        EnhancedStructuredProcessReportV2 reportV2Plus = EnhancedStructuredProcessReportV2.create(processId, projectName);
        ProcessDefinitionV2Plus processDefinition = reportV2Plus.getProcessDefinition();

        // Transforma os recursos externos primeiro, garantindo que não sejam perdidos.
        if (reportV2.getEnvironmentVariablesUsed() != null) {
            reportV2.getEnvironmentVariablesUsed().forEach(envVar ->
                    reportV2Plus.getEnvironmentVariablesUsed().add(new ExternalResourceV2Plus(envVar.getName(), envVar.getValue()))
            );
        }
        if (reportV2.getResourceBundlesUsed() != null) {
            reportV2.getResourceBundlesUsed().forEach(rb ->
                    reportV2Plus.getResourceBundlesUsed().add(new ExternalResourceV2Plus(rb.getKey(), rb.getValue()))
            );
        }

        // Processa o artefato principal
        if (reportV2.getArtifacts() != null && !reportV2.getArtifacts().isEmpty()) {
            JsonReportV2.Artifact artifactV2 = reportV2.getArtifacts().get(0);

            processDefinition.setName(artifactV2.getName());

            // 1. Transformar Variáveis (lógica existente é suficiente)
            processDefinition.setVariables(transformVariables(artifactV2.getVariables()));

            // 2. Transformar Grafo (LÓGICA APRIMORADA)
            processDefinition.setGraph(transformGraph(artifactV2));

            // 3. Transformar Lógica e Condições (LÓGICA APRIMORADA)
            transformLogicAndConditions(artifactV2, processDefinition);
        }

        // 4. Transformar UI (LÓGICA APRIMORADA)
        reportV2Plus.setUi(transformUi(reportV2.getUiReport()));

        // 5. Inferir DataTypes a partir das variáveis já transformadas
        reportV2Plus.setDataTypes(inferDataTypes(processDefinition.getVariables()));

        return reportV2Plus;
    }

    private ProcessVariablesV2Plus transformVariables(JsonReportV2.Variables variablesV2) {
        ProcessVariablesV2Plus variablesV2Plus = new ProcessVariablesV2Plus();
        if (variablesV2 == null) return variablesV2Plus;

        if (variablesV2.getInput() != null) {
            variablesV2.getInput().forEach(v -> variablesV2Plus.addInputVariable(transformVariable(v)));
        }
        if (variablesV2.getOutput() != null) {
            variablesV2.getOutput().forEach(v -> variablesV2Plus.addOutputVariable(transformVariable(v)));
        }
        if (variablesV2.getPrivite() != null) {
            variablesV2.getPrivite().forEach(v -> variablesV2Plus.addPrivateVariable(transformVariable(v)));
        }
        return variablesV2Plus;
    }

    private ProcessVariableV2Plus transformVariable(JsonReportV2.VariableInfo varInfoV2) {
        ProcessVariableV2Plus varV2Plus = new ProcessVariableV2Plus(
                varInfoV2.getName(),
                varInfoV2.getTypeId(),
                "Variável importada da V2",
                varInfoV2.isList()
        );
        varV2Plus.normalizeTypeRef();
        return varV2Plus;
    }

    /**
     * APRIMORADO: Transforma o grafo V2 para V2Plus, enriquecendo os nós com
     * informações detalhadas da lista de 'flow' do artefato V2.
     */
    private ProcessGraphV2Plus transformGraph(JsonReportV2.Artifact artifactV2) {
        ProcessGraphV2Plus graphV2Plus = new ProcessGraphV2Plus();
        if (artifactV2.getGraph() == null) return graphV2Plus;

        // Mapeia o 'flow' para fácil acesso pelo ID do step
        Map<String, JsonReportV2.FlowStep> flowMap = artifactV2.getFlow().stream()
                .collect(Collectors.toMap(JsonReportV2.FlowStep::getStepId, step -> step));

        if (artifactV2.getGraph().getNodes() != null) {
            for (JsonReportV2.Node nodeV2 : artifactV2.getGraph().getNodes()) {
                JsonReportV2.FlowStep correspondingFlowStep = flowMap.get(nodeV2.getId());
                // Passa o FlowStep para enriquecer o nó
                graphV2Plus.addNode(transformNode(nodeV2, correspondingFlowStep));
            }
        }

        if (artifactV2.getGraph().getEdges() != null) {
            artifactV2.getGraph().getEdges().forEach(e -> graphV2Plus.addEdge(transformEdge(e)));
        }

        graphV2Plus.autoDetectEntryPoints();
        graphV2Plus.autoDetectExitPoints();
        return graphV2Plus;
    }

    /**
     * APRIMORADO: Transforma um nó V2 para V2Plus, incluindo os dados ricos do FlowStep.
     */
    private ProcessNodeV2Plus transformNode(JsonReportV2.Node nodeV2, JsonReportV2.FlowStep flowStep) {
        ProcessNodeV2Plus nodeV2Plus = new ProcessNodeV2Plus(
                nodeV2.getId(),
                ProcessNodeV2Plus.NodeType.fromString(nodeV2.getType()),
                nodeV2.getName(),
                nodeV2.getLane()
        );

        if (flowStep != null) {
            // Transferência dos dados vitais que estavam faltando
            nodeV2Plus.setCalledArtifactId(flowStep.getCalledArtifactId());
            nodeV2Plus.setCoachId(flowStep.getCoachId());
            nodeV2Plus.setParameterMapping(transformParameterMapping(flowStep.getParameterMapping()));
        }

        return nodeV2Plus;
    }

    private ParameterMappingV2Plus transformParameterMapping(JsonReportV2.ParameterMapping mappingV2) {
        ParameterMappingV2Plus mappingV2Plus = new ParameterMappingV2Plus();
        if (mappingV2 == null) return mappingV2Plus;

        if (mappingV2.getInput() != null) {
            mappingV2.getInput().forEach(m ->
                    mappingV2Plus.getInputs().add(new ParameterMappingV2Plus.Mapping(m.getSource(), m.getTarget()))
            );
        }
        if (mappingV2.getOutput() != null) {
            mappingV2.getOutput().forEach(m ->
                    mappingV2Plus.getOutputs().add(new ParameterMappingV2Plus.Mapping(m.getSource(), m.getTarget()))
            );
        }
        return mappingV2Plus;
    }

    private ProcessEdgeV2Plus transformEdge(JsonReportV2.Edge edgeV2) {
        return new ProcessEdgeV2Plus(
                edgeV2.getId(),
                edgeV2.getSource(),
                edgeV2.getTarget(),
                edgeV2.getLabel()
        );
    }

    /**
     * APRIMORADO: Centraliza a extração de Lógica e Condições do artefato V2.
     */
    private void transformLogicAndConditions(JsonReportV2.Artifact artifactV2, ProcessDefinitionV2Plus processDefinition) {
        if (artifactV2.getFlow() == null) return;

        for (JsonReportV2.FlowStep step : artifactV2.getFlow()) {
            // Extrai scripts para a seção de Lógica
            if (step.getScript() != null && !step.getScript().isEmpty()) {
                LogicItemV2Plus logicItem = LogicItemV2Plus.createScript("lg:" + step.getStepId(), step.getName() + " Logic", step.getScript());
                processDefinition.getLogic().addItem(logicItem);
            }

            // Extrai condições dos gateways a partir do 'flow'
            if ("ExclusiveGateway".equalsIgnoreCase(step.getType()) && step.getConditions() != null) {
                for (JsonReportV2.Condition condV2 : step.getConditions()) {
                    ProcessConditionV2Plus condV2Plus = new ProcessConditionV2Plus();
                    condV2Plus.setId("cond:" + step.getStepId() + "_" + condV2.getTargetStepId());
                    condV2Plus.setName(condV2.getName());
                    condV2Plus.setExpression(condV2.getExpression());
                    condV2Plus.setLanguage(ProcessConditionV2Plus.ExpressionLanguage.CEL); // Assumindo conversão para CEL
                    condV2Plus.setDescription("Condição de roteamento do gateway '" + step.getName() + "'");
                    processDefinition.getConditions().add(condV2Plus);
                }
            }
        }
    }

    private ProcessUIV2Plus transformUi(JsonReportV2.UiReport uiReportV2) {
        ProcessUIV2Plus uiV2Plus = new ProcessUIV2Plus();
        if (uiReportV2 == null) return uiV2Plus;

        if (uiReportV2.getCoaches() != null) {
            for (JsonReportV2.Coach coachV2 : uiReportV2.getCoaches()) {
                ProcessUIV2Plus.UILayoutV2Plus layout = new ProcessUIV2Plus.UILayoutV2Plus();
                layout.setId(coachV2.getCoachId());
                layout.setName(coachV2.getCoachName());
                layout.setType("Coach");
                uiV2Plus.getLayouts().add(layout);

                if (coachV2.getComponents() != null) {
                    coachV2.getComponents().forEach(c -> uiV2Plus.getComponents().add(transformComponent(c)));
                }
            }
        }
        return uiV2Plus;
    }

    /**
     * APRIMORADO: Transforma um componente de UI, incluindo o data binding.
     */
    private ProcessUIV2Plus.UIComponentV2Plus transformComponent(JsonReportV2.UiComponent compV2) {
        ProcessUIV2Plus.UIComponentV2Plus compV2Plus = new ProcessUIV2Plus.UIComponentV2Plus();
        compV2Plus.setId(compV2.getComponentId());
        compV2Plus.setName(compV2.getLabel());
        compV2Plus.setType(compV2.getType());
        compV2Plus.setBinding(compV2.getBinding()); // Mapeia o data binding
        return compV2Plus;
    }

    private List<DataTypeDefinitionV2Plus> inferDataTypes(ProcessVariablesV2Plus variables) {
        List<DataTypeDefinitionV2Plus> dataTypes = new ArrayList<>();
        if (variables == null) return dataTypes;

        Set<String> processedTypes = new HashSet<>();
        List<ProcessVariableV2Plus> allVars = new ArrayList<>();
        allVars.addAll(variables.getInputs());
        allVars.addAll(variables.getOutputs());
        allVars.addAll(variables.getPrivateVariables());

        for (ProcessVariableV2Plus var : allVars) {
            if (var.getTypeRef() != null && !processedTypes.contains(var.getTypeRef())) {
                DataTypeDefinitionV2Plus dt = new DataTypeDefinitionV2Plus();
                dt.setId(var.getTypeRef());
                String cleanName = var.getTypeRef().replaceAll("dt:|@\\d+", "");
                dt.setName(cleanName.substring(0, 1).toUpperCase() + cleanName.substring(1));
                dt.setDescription("Tipo de dado para a variável " + var.getName());
                dataTypes.add(dt);
                processedTypes.add(var.getTypeRef());
            }
        }
        return dataTypes;
    }
}