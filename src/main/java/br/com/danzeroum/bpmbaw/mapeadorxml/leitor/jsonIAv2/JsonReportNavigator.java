// Local: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/JsonReportNavigator.java

package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Pool;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.json.JsonReport;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.Property;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass;

import java.util.*;
import java.util.stream.Collectors;

public class JsonReportNavigator {

    private final JsonReportGenerator generator;
    private final ProcessLoader loader;

    private static final Set<String> PRIMITIVE_TYPES = new HashSet<>(Arrays.asList(
            "String", "Integer", "Boolean", "Decimal", "Date", "Time", "DateTime", "ANY"
    ));

    public JsonReportNavigator(JsonReportGenerator generator, ProcessLoader loader) {
        this.generator = generator;
        this.loader = loader;
    }

    /**
     * Orquestra a população da seção de fluxo do relatório JSON.
     * A iteração sobre os artefatos prioriza o artefato raiz informado e,
     * em seguida, processa os demais artefatos carregados no cache.
     *
     * @param rootObjectId O ID do artefato raiz que deve ser processado primeiro,
     *                     servindo como referência do início da impressão.
     */
    public void populateReport(String rootObjectId) {
        System.out.println("[LOG-NAVIGATOR] ==> Iniciando populateReport...");

        Map<String, Object> cache = loader.getCacheDeArtefatos();
        System.out.println("[LOG-NAVIGATOR] Total de artefatos a processar: " + cache.size());

        if (cache.isEmpty()) {
            System.out.println("[AVISO-NAVIGATOR] A lista de artefatos para processar está vazia.");
            return;
        }

        // Monta uma lista ordenada: primeiro o artefato raiz (se presente), depois os demais
        String rootCleanId = loader.getCleanId(rootObjectId);
        List<String> orderedArtifactIds = new ArrayList<String>();
        if (rootCleanId != null && cache.containsKey(rootCleanId)) {
            orderedArtifactIds.add(rootCleanId);
            System.out.println("[LOG-NAVIGATOR] Artefato raiz priorizado: " + rootCleanId);
        } else {
            System.out.println("[AVISO-NAVIGATOR] Artefato raiz não encontrado no cache: " + rootObjectId + " (clean: " + rootCleanId + "). Prosseguindo com os demais artefatos.");
        }
        for (String id : cache.keySet()) {
            if (rootCleanId == null || !rootCleanId.equals(id)) {
                orderedArtifactIds.add(id);
            }
        }

        for (String artifactId : orderedArtifactIds) {
            Object artefatoObj = loader.getArtefatoDoCache(artifactId);
            ProcessLoader.ArtifactLocation location = loader.findArtifactLocation(artifactId);

            if (location != null && location.objectInfo != null) {
                System.out.println("\n[LOG-NAVIGATOR] Processando artefato: " + location.objectInfo.getName() + " (ID: " + artifactId + ")");

                JsonReport.JsonArtifact artifact = generator.createOrGetArtifact(
                        location.objectInfo.getId(),
                        location.objectInfo.getName(),
                        location.objectInfo.getType(),
                        location.filePath
                );

                if (artefatoObj instanceof Definitions) {
                    System.out.println("[LOG-NAVIGATOR] -> Tipo: Definitions (Processo BPMN).");
                    visitProcessDefinition(((Definitions) artefatoObj).getProcess(), artifact);
                } else if (artefatoObj instanceof Teamworks) {
                    Teamworks tw = (Teamworks) artefatoObj;
                    if (tw.getProcess() != null) {
                        System.out.println("[LOG-NAVIGATOR] -> Tipo: Teamworks com Process (Serviço Legado/Coachflow).");
                        visitServicoLegado(tw.getProcess(), artifact);
                    } else if (tw.getBpd() != null) {
                        System.out.println("[LOG-NAVIGATOR] -> Tipo: Teamworks com Bpd (BPD Legado).");
                        visitBpdLegado(tw.getBpd(), artifact);
                    } else if (tw.getTwClass() != null || tw.getCoachView() != null || tw.getParticipant() != null) {
                        System.out.println("[LOG-NAVIGATOR] -> Tipo: " + location.objectInfo.getType() + ". Ignorando para a seção de fluxo (correto).");
                    } else {
                        System.out.println("[LOG-NAVIGATOR] -> Tipo: Teamworks, mas sem um <process> ou <bpd> mapeável. Ignorando.");
                    }
                } else {
                    System.out.println("[AVISO-NAVIGATOR] -> Tipo de objeto desconhecido no cache: " + (artefatoObj != null ? artefatoObj.getClass().getName() : "null"));
                }
            } else {
                System.err.println("[ERRO-NAVIGATOR] -> CRÍTICO: Não foi possível encontrar a localização (metadata) para o artefato com ID: " + artifactId);
            }
        }
        System.out.println("\n[LOG-NAVIGATOR] ==> Conclusão de populateReport.");
    }

    /**
     * Processa um artefato do tipo Processo BPMN (moderno).
     */
    private void visitProcessDefinition(Process process, JsonReport.JsonArtifact artifact) {
        if (process == null) {
            System.out.println("[AVISO-NAVIGATOR] O objeto 'Process' dentro de Definitions é nulo para o artefato: " + artifact.getName());
            return;
        }

        populateBPMNVariables(process, artifact);

        // Adiciona participantes das Lanes
        if (process.getLaneSet() != null && process.getLaneSet().getLanes() != null) {
            for (Lane lane : process.getLaneSet().getLanes()) {
                if (lane.getPartitionElementRef() != null && !lane.getPartitionElementRef().isEmpty()) {
                    String participantName = findParticipantNameById(lane.getPartitionElementRef());
                    if (participantName != null && !artifact.getParticipants().contains(participantName)) {
                        artifact.getParticipants().add(participantName);
                    }
                }
            }
        }

        Map<String, FlowNode> nodeMap = createNodeMap(process.getFlowElements());
        Map<String, List<SequenceFlow>> flowMap = createSourceIdToFlowMap(process.getSequenceFlows());

        if (nodeMap.isEmpty()) return;

        // Nova lógica: ordenar os nós a partir do(s) StartEvent(s) do processo
        Set<String> visited = new HashSet<String>();
        List<FlowNode> orderedNodes = new ArrayList<FlowNode>();

        // 1) Encontrar todos os StartEvents do processo
        List<String> startIds = new ArrayList<String>();
        for (FlowNode n : nodeMap.values()) {
            if (n instanceof StartEvent) {
                startIds.add(n.getId());
            }
        }

        // 2) Fazer uma travessia (DFS) a partir de cada StartEvent seguindo os SequenceFlows
        for (String startId : startIds) {
            Deque<String> stack = new ArrayDeque<String>();
            stack.push(startId);
            while (!stack.isEmpty()) {
                String currentId = stack.pop();
                if (!visited.add(currentId)) {
                    continue;
                }
                FlowNode currentNode = nodeMap.get(currentId);
                if (currentNode != null) {
                    orderedNodes.add(currentNode);
                    List<SequenceFlow> outs = flowMap.get(currentId);
                    if (outs != null) {
                        // Empilha os destinos na ordem inversa para manter a ordem natural na saída
                        for (int i = outs.size() - 1; i >= 0; i--) {
                            SequenceFlow f = outs.get(i);
                            if (f.getTargetRef() != null && !visited.contains(f.getTargetRef())) {
                                stack.push(f.getTargetRef());
                            }
                        }
                    }
                }
            }
        }

        // 3) Adicionar quaisquer nós não alcançáveis a partir de StartEvent (evita perdas em grafos não conexos)
        for (FlowNode n : nodeMap.values()) {
            if (!visited.contains(n.getId())) {
                orderedNodes.add(n);
            }
        }

        // 4) Gerar os passos do fluxo respeitando a ordem calculada
        for (FlowNode node : orderedNodes) {
            JsonReport.JsonFlowStep step = new JsonReport.JsonFlowStep();
            step.setStepId(node.getId());
            step.setName(node.getName());
            step.setType(getNodeType(node));

            if (node.getIncoming() != null) step.setIncomingFlows(node.getIncoming());
            if (node.getOutgoing() != null) step.setOutgoingFlows(node.getOutgoing());

            if (node instanceof ScriptTask) {
                step.setScript(((ScriptTask) node).getScript());
            } else if (node instanceof CallActivity) {
                step.setCalledArtifactId(((CallActivity) node).getCalledElement());
                step.setParameterMapping(populateBPMNParameterMapping((CallActivity) node));
            } else if (node instanceof ExclusiveGateway) {
                step.setConditions(populateBPMNConditions((ExclusiveGateway) node, flowMap));
            }
            artifact.getFlow().add(step);
        }
    }

    /**
     * Processa um artefato do tipo Serviço Legado (Heritage).
     */
    private void visitServicoLegado(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process, JsonReport.JsonArtifact artifact) {
        if (process == null || process.getItems() == null || process.getItems().isEmpty()) {
            return;
        }

        populateLegacyVariables(process, artifact);

        for (Item item : process.getItems()) {
            JsonReport.JsonFlowStep step = new JsonReport.JsonFlowStep();
            step.setStepId(item.getProcessItemId());
            step.setName(item.getName());
            step.setType(item.getTWComponentName());

            TWComponent component = item.getTwComponent();
            if (component != null) {
                step.setScript(component.getScript());
                if ("SubProcess".equalsIgnoreCase(item.getTWComponentName())) {
                    step.setCalledArtifactId(component.getAttachedProcessRef());
                    step.setParameterMapping(populateLegacyParameterMapping(component));
                }else if ("Switch".equalsIgnoreCase(item.getTWComponentName())) {
                    step.setConditions(populateLegacySwitchConditions(item, process.getLinks()));
                }else if ("CoachFlow".equalsIgnoreCase(item.getTWComponentName())) {
                    if (process.getCoachflow() != null && process.getCoachflow().getDefinitions() != null) {
                        GlobalUserTask userTask = process.getCoachflow().getDefinitions().getGlobalUserTask();
                        if (userTask != null && userTask.getImplementation() != null && userTask.getImplementation().getFlowElements() != null) {
                            // Encontra o primeiro FormTask dentro do CoachFlow
                            Optional<FormTask> formTaskOpt = userTask.getImplementation().getFlowElements().stream()
                                    .filter(FormTask.class::isInstance)
                                    .map(FormTask.class::cast)
                                    .findFirst();

                            // Se encontrou, usa o ID dele como coachId
                            if (formTaskOpt.isPresent()) {
                                step.setCoachId("coachId_"+formTaskOpt.get().getId());
                                System.out.println("[LOG][JsonReportNavigator] Associando CoachFlow '" + step.getName() + "' ao coachId interno: " + step.getCoachId());
                            }
                        }
                    }
                }else if ("CoachNG".equalsIgnoreCase(item.getTWComponentName())) {
                    step.setCoachId("coachId_"+item.getProcessItemId());
                }
            }
            if (process.getLinks() != null) {
                step.getIncomingFlows().addAll(process.getLinks().stream().filter(l -> item.getProcessItemId().equals(l.getToProcessItemId())).map(Link::getProcessLinkId).collect(Collectors.toList()));
                step.getOutgoingFlows().addAll(process.getLinks().stream().filter(l -> item.getProcessItemId().equals(l.getFromProcessItemId())).map(Link::getProcessLinkId).collect(Collectors.toList()));
            }
            artifact.getFlow().add(step);
        }
    }

    /**
     * Processa um artefato do tipo BPD Legado para extrair referências de participantes.
     * Observação:
     * - Atualmente este método não imprime o fluxo do BPD legado; ele apenas coleta e associa participantes ao artefato.
     * - Se for necessário "imprimir" o fluxo em ordem iniciando do nó inicial (equivalente ao Start Event),
     *   será preciso estender este método para:
     *     (1) mapear os FlowObjects e as conexões (links/conectores) do BPD legado,
     *     (2) identificar os nós iniciais e,
     *     (3) percorrer o grafo (DFS/BFS) gerando JsonFlowStep na ordem visitada.
     */
    private void visitBpdLegado(Bpd bpd, JsonReport.JsonArtifact artifact) {
        if (bpd == null || bpd.getBusinessProcessDiagram() == null) return;

        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();

        if (diagram.getPools() != null) {
            for (Pool pool : diagram.getPools()) {
                if (pool.getLanes() != null) {
                    for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane lane : pool.getLanes()) {
                        String participantRef = lane.getAttachedParticipant();
                        if (participantRef != null && !participantRef.isEmpty()) {
                            String participantName = findParticipantNameById(participantRef);
                            if (participantName != null && !artifact.getParticipants().contains(participantName)) {
                                artifact.getParticipants().add(participantName);
                                System.out.println("[LOG-PARTICIPANT] Associando '" + participantName + "' ao artefato legado '" + artifact.getName() + "'");
                            }
                        }
                    }
                }
            }
        }
    }

    private void populateBPMNVariables(Process process, JsonReport.JsonArtifact artifact) {
        if (process.getIoSpecification() == null) return;
        IoSpecification ioSpec = process.getIoSpecification();
        if (ioSpec.getDataInputs() != null) {
            ioSpec.getDataInputs().forEach(input -> {
                JsonReport.VariableInfo varInfo = new JsonReport.VariableInfo();
                varInfo.setName(input.getName());
                varInfo.setTypeId(input.getItemSubjectRef());
                varInfo.setList(input.getIsCollection() != null && input.getIsCollection());
                enrichVariableInfo(varInfo, new HashSet<>());
                artifact.getVariables().getInput().add(varInfo);
            });
        }
        if (ioSpec.getDataOutputs() != null) {
            ioSpec.getDataOutputs().forEach(output -> {
                JsonReport.VariableInfo varInfo = new JsonReport.VariableInfo();
                varInfo.setName(output.getName());
                varInfo.setTypeId(output.getItemSubjectRef());
                varInfo.setList(output.getIsCollection() != null && output.getIsCollection());
                enrichVariableInfo(varInfo, new HashSet<>());
                artifact.getVariables().getOutput().add(varInfo);
            });
        }
    }

    private void populateLegacyVariables(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process, JsonReport.JsonArtifact artifact) {
        if (process.getProcessParameters() != null) {
            for (ProcessParameter param : process.getProcessParameters()) {
                JsonReport.VariableInfo varInfo = new JsonReport.VariableInfo();
                varInfo.setName(param.getName());
                varInfo.setTypeId(param.getClassId());
                varInfo.setList(param.isArrayOf());
                enrichVariableInfo(varInfo, new HashSet<>());
                if (param.getParameterType() == 1) {
                    artifact.getVariables().getInput().add(varInfo);
                } else {
                    artifact.getVariables().getOutput().add(varInfo);
                }
            }
        }
        if (process.getProcessVariables() != null) {
            for (ProcessVariable variable : process.getProcessVariables()) {
                JsonReport.VariableInfo varInfo = new JsonReport.VariableInfo();
                varInfo.setName(variable.getName());
                varInfo.setTypeId(variable.getClassId());
                varInfo.setList(variable.isArrayOf());
                enrichVariableInfo(varInfo, new HashSet<>());
                artifact.getVariables().getPrivate().add(varInfo);
            }
        }
    }

    private void enrichVariableInfo(JsonReport.VariableInfo varInfo, Set<String> visitedTypes) {

        if (varInfo == null || varInfo.getTypeId() == null || varInfo.getTypeId().trim().isEmpty()) {
            return;
        }

        String originalTypeId = varInfo.getTypeId();
        String cleanTypeId = loader.getCleanId(originalTypeId);

        if (cleanTypeId == null || !visitedTypes.add(cleanTypeId)) {
            return;
        }

        loader.loadDependentArtifactIfNotExists(originalTypeId);
        Object artifact = loader.getArtefatoDoCache(cleanTypeId);

        if (artifact instanceof Teamworks && ((Teamworks) artifact).getTwClass() != null) {
            TwClass twClass = ((Teamworks) artifact).getTwClass();

            if (PRIMITIVE_TYPES.contains(twClass.getName())) {
                System.out.println("[LOG-NAVIGATOR] Encontrado tipo primitivo: '" + twClass.getName() + "'.");
                varInfo.setTypeId(twClass.getName());
                return;
            }

            JsonReport.BusinessObject boDefinition = new JsonReport.BusinessObject();
            boDefinition.setTypeId(originalTypeId);
            boDefinition.setTypeName(twClass.getName());

            if (twClass.getDefinition() != null && twClass.getDefinition().getProperties() != null) {
                for (Property prop : twClass.getDefinition().getProperties()) {
                    JsonReport.PropertyStructure propStruct = new JsonReport.PropertyStructure();
                    propStruct.setName(prop.getName());
                    propStruct.setList(prop.isArrayProperty());

                    // --- INÍCIO DA CORREÇÃO ---
                    // Passo 1: Preparar um objeto temporário para a chamada recursiva.
                    JsonReport.VariableInfo tempVarInfo = new JsonReport.VariableInfo();
                    tempVarInfo.setTypeId(prop.getClassRef());

                    // Passo 2: Chamar o método void. Ele vai modificar o `tempVarInfo` internamente.
                    enrichVariableInfo(tempVarInfo, new HashSet<>(visitedTypes));

                    // Passo 3: Usar o valor modificado do objeto temporário. Agora é garantido que é uma String.
                    propStruct.setTypeRef(tempVarInfo.getTypeId());
                    // --- FIM DA CORREÇÃO ---

                    boDefinition.getStructure().add(propStruct);
                }
            }

            String canonicalId = generator.addBusinessObjectDefinition(boDefinition);
            varInfo.setTypeId(canonicalId);

        }


    }

    private String findParticipantNameById(String participantId) {
        if (participantId == null) return null;
        String cleanId = loader.getCleanId(participantId);

        return generator.getReport().getParticipantGroups().stream()
                .filter(group -> cleanId.equals(loader.getCleanId(group.getId())))
                .map(JsonReport.JsonParticipantGroup::getName)
                .findFirst()
                .orElse(participantId);
    }


    private JsonReport.ParameterMapping populateBPMNParameterMapping(CallActivity callActivity) {
        JsonReport.ParameterMapping parameterMapping = new JsonReport.ParameterMapping();
        if (callActivity.getDataInputAssociations() != null) {
            for (DataInputAssociation dia : callActivity.getDataInputAssociations()) {
                if (dia.getAssignment() != null && dia.getAssignment().getFrom() != null) {
                    JsonReport.Mapping mapping = new JsonReport.Mapping();
                    mapping.setSource(dia.getAssignment().getFrom().getExpression());
                    mapping.setTarget(dia.getTargetRef());
                    parameterMapping.getInput().add(mapping);
                }
            }
        }
        if (callActivity.getDataOutputAssociations() != null) {
            for (DataOutputAssociation doa : callActivity.getDataOutputAssociations()) {
                if (doa.getAssignment() != null && doa.getAssignment().getTo() != null) {
                    JsonReport.Mapping mapping = new JsonReport.Mapping();
                    mapping.setSource(doa.getSourceRef());
                    mapping.setTarget(doa.getAssignment().getTo().getContent());
                    parameterMapping.getOutput().add(mapping);
                }
            }
        }
        return parameterMapping;
    }
    private JsonReport.ParameterMapping populateLegacyParameterMapping(TWComponent component) {
        JsonReport.ParameterMapping parameterMapping = new JsonReport.ParameterMapping();
        if (component.getParameterMapping() != null) {
            for (ParameterMapping pm : component.getParameterMapping()) {
                JsonReport.Mapping mapping = new JsonReport.Mapping();
                mapping.setSource(pm.getValue());
                mapping.setTarget(pm.getName());
                if (pm.isInput()) {
                    parameterMapping.getInput().add(mapping);
                } else {
                    parameterMapping.getOutput().add(mapping);
                }
            }
        }
        return parameterMapping;
    }

    private List<JsonReport.Condition> populateBPMNConditions(ExclusiveGateway gateway, Map<String, List<SequenceFlow>> flowMap) {
        List<JsonReport.Condition> conditions = new ArrayList<>();
        List<SequenceFlow> outgoingFlows = flowMap.getOrDefault(gateway.getId(), Collections.emptyList());
        for (SequenceFlow flow : outgoingFlows) {
            JsonReport.Condition condition = new JsonReport.Condition();
            condition.setTargetStepId(flow.getTargetRef());
            condition.setName(flow.getName());
            if (flow.getConditionExpression() != null) {
                condition.setExpression(flow.getConditionExpression().getExpression());
            }
            condition.setDefault(flow.getId().equals(gateway.getDefaultFlow()));
            conditions.add(condition);
        }
        return conditions;
    }
    private List<JsonReport.Condition> populateLegacySwitchConditions(Item switchItem, List<Link> allLinks) {
        List<JsonReport.Condition> conditions = new ArrayList<>();
        if (switchItem.getTwComponent() == null || switchItem.getTwComponent().getSwitchConditions() == null) {
            return conditions;
        }

        Map<String, String> conditionMap = new HashMap<>();
        if (switchItem.getTwComponent().getSwitchConditions() != null) {
            for(SwitchCondition cond : switchItem.getTwComponent().getSwitchConditions()) {
                if (cond.getEndStateId() != null && cond.getCondition() != null) {
                    conditionMap.put(cond.getEndStateId(), cond.getCondition());
                }
            }
        }

        if (allLinks != null) {
            allLinks.stream()
                    .filter(link -> switchItem.getProcessItemId().equals(link.getFromProcessItemId()))
                    .forEach(link -> {
                        JsonReport.Condition condition = new JsonReport.Condition();
                        condition.setTargetStepId(link.getToProcessItemId());
                        condition.setName(link.getName());
                        condition.setExpression(conditionMap.get(link.getEndStateId()));
                        condition.setDefault("Default".equalsIgnoreCase(link.getName()));
                        conditions.add(condition);
                    });
        }
        return conditions;
    }
    private Map<String, FlowNode> createNodeMap(List<Object> elements) {
        if (elements == null) return new HashMap<>();
        return elements.stream()
                .filter(FlowNode.class::isInstance)
                .map(FlowNode.class::cast)
                .collect(Collectors.toMap(FlowNode::getId, node -> node, (a, b) -> a));
    }
    private Map<String, List<SequenceFlow>> createSourceIdToFlowMap(List<SequenceFlow> flows) {
        if (flows == null) return new HashMap<>();
        return flows.stream()
                .filter(f -> f.getSourceRef() != null)
                .collect(Collectors.groupingBy(SequenceFlow::getSourceRef));
    }
    private String getNodeType(Object node) {
        if (node instanceof CallActivity) return "CallActivity";
        if (node instanceof SubProcess) return "SubProcess";
        if (node instanceof ScriptTask) return "ScriptTask";
        if (node instanceof FormTask) return "FormTask";
        if (node instanceof Task) return "Task";
        if (node instanceof StartEvent) return "StartEvent";
        if (node instanceof EndEvent) return "EndEvent";
        if (node instanceof ExclusiveGateway) return "ExclusiveGateway";
        if (node instanceof Item) return ((Item) node).getTWComponentName();
        return "Elemento";
    }
}