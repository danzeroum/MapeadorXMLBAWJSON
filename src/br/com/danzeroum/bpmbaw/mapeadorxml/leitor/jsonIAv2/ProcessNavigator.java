package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessPrinter;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.*;

import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.bind.JAXBContext;
/**
 * (VERSÃO CORRIGIDA)
 * Representa o "Navegador Inteligente" do processo.
 * Opera sobre os dados já carregados em memória pelo ProcessLoader para gerar o relatório.
 */
public class ProcessNavigator {

    private final ProcessPrinter printer;
    private final ProcessLoader loader;
    private JAXBContext coachLayoutContext;

    public ProcessNavigator(ProcessPrinter printer, ProcessLoader loader) {
        this.printer = printer;
        this.loader = loader;
        try {

            this.coachLayoutContext = JAXBContext.newInstance(CoachLayout.class);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar o JAXBContext para CoachLayout", e);
        }
    }

    /**
     * Orquestra a geração do relatório completo, primeiro construindo o índice
     * e depois detalhando cada artefato carregado na memória.
     */
    public void generateReport() {
        // Usa o novo método para obter o cache e registrar os artefatos para o índice.
        Map<String, Object> artefatosEmMemoria = loader.getCacheDeArtefatos();

        artefatosEmMemoria.keySet().forEach(artefactId -> {
            ProcessLoader.ArtifactLocation location = loader.findArtifactLocation(artefactId);
            if (location != null) {
                printer.registrarArtefatoParaIndice(location.objectInfo);
            }
        });

        printer.printIndex();

        // Itera sobre o mapa de artefatos em memória para imprimir os detalhes de cada um.
        artefatosEmMemoria.forEach((id, artefatoObj) -> {
            ProcessLoader.ArtifactLocation location = loader.findArtifactLocation(id);

            if (location != null) {
                if (printer.printHeaderDoArtefato(location)) {
                    if (artefatoObj instanceof Definitions) {
                        visitProcessDefinition(((Definitions) artefatoObj).getProcess(), "", new HashSet<>());
                    } else if (artefatoObj instanceof Teamworks) {
                        Teamworks tw = (Teamworks) artefatoObj;
                        if (tw.getProcess() != null) {
                            if (tw.getProcess().getCoachflow() != null && tw.getProcess().getCoachflow().getDefinitions() != null) {
                                visitCoachflowProcess(tw.getProcess().getCoachflow(), tw.getProcess().getName(), "", new HashSet<>());
                            } else {
                                visitServicoLegado(tw.getProcess(), "", new HashSet<>());
                            }
                        }else if (tw.getBpd() != null) {
                            visitLegacyBpd(tw.getBpd(), "");
                        }
                    }
                }
            }
        });
    }

    /**
     * NOVO MÉTODO PÚBLICO
     * Gera um relatório focado exclusivamente no fluxo do processo, priorizando o caminho principal
     * e tratando os fluxos de UCA e Ad-Hoc como alternativos.
     * @param rootObjectId O ID do BPD ou Serviço raiz.
     */
    public void generateFlowReport(String rootObjectId) {
        printer.printFlowReportTitle("Análise de Fluxo de Processo");

        Object rootArtifact = loader.getArtefatoDoCache(rootObjectId);
        if (rootArtifact == null) {
            printer.printMensagem("Artefato raiz com ID '" + rootObjectId + "' não foi encontrado no cache.", "");
            return;
        }

        // Delega a análise do fluxo para o método especializado.
        printDiscoveredFlows(rootArtifact);
    }

    /*
    private void printDiscoveredFlows(Object artefatoObj) {

        //System.out.println("printDiscoveredFlows - artefatoObj: "+artefatoObj.toString());
        if (artefatoObj instanceof Definitions) {
            Process bpmnProcess = ((Definitions) artefatoObj).getProcess();
            printer.printFlowSectionHeader(bpmnProcess.getName());
            navigateAndPrintBPMNFlows(bpmnProcess);
        } else if (artefatoObj instanceof Teamworks) {
            Teamworks tw = (Teamworks) artefatoObj;
            // Lógica para extrair e analisar o fluxo de diferentes tipos de serviços
            if (tw.getProcess() != null && tw.getProcess().getCoachflow() != null) {
                // ... Lógica para Coachflow ...
            } else if (tw.getBpd() != null) {
                // ... Lógica para BPD Legado ...
            }
        }
    }*/

    /**
     * Analisa um artefato (BPD ou Serviço) e orquestra a impressão de seus fluxos,
     * direcionando para o método de navegação apropriado com base no tipo do artefato.
     */
    private void printDiscoveredFlows(Object artefatoObj) {
        if (artefatoObj == null) {
            return;
        }

        // --- Cenário 1: O artefato é um BPD Moderno (continha <bpmn2Data>) ---
        if (artefatoObj instanceof Definitions) {
            Process bpmnProcess = ((Definitions) artefatoObj).getProcess();
            if (bpmnProcess != null) {
                printer.printFlowSectionHeader(bpmnProcess.getName());
                navigateAndPrintBPMNFlows(bpmnProcess);
            }
        }
        // --- Cenário 2: O artefato é um Serviço ou BPD Legado ---
        else if (artefatoObj instanceof Teamworks) {
            Teamworks tw = (Teamworks) artefatoObj;

            // Caso 2a: É um Serviço Humano do Lado do Cliente (com <coachflow>)
            if (tw.getProcess() != null && tw.getProcess().getCoachflow() != null) {
                printer.printFlowSectionHeader(tw.getProcess().getName());

                GlobalUserTask gut = tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask();
                if (gut != null && gut.getImplementation() != null) {
                    // Cria um processo BPMN "virtual" a partir do conteúdo do coachflow
                    Process coachflowProcess = new Process();
                    coachflowProcess.setName(tw.getProcess().getName());
                    coachflowProcess.setFlowElements(gut.getImplementation().getFlowElements());
                    coachflowProcess.setSequenceFlows(gut.getImplementation().getSequenceFlows());

                    // Reutiliza a mesma lógica de navegação de um BPD moderno
                    navigateAndPrintBPMNFlows(coachflowProcess);
                }
            }
            // Caso 2b: É um BPD Legado (sem <bpmn2Data>)
            else if (tw.getBpd() != null) {
                //System.out.println("printDiscoveredFlows - tw.getBpd():"+tw.getBpd().getName() + " - ");
                printer.printFlowSectionHeader(tw.getBpd().getName());
                // Chama o método especializado para navegar em BPDs legados
                navigateAndPrintLegacyBpdFlow(tw.getBpd());
            }
            // Caso 2c: É um Serviço Legado simples (baseado em <item> e <link>)
            else if (tw.getProcess() != null) {
                //System.out.println("printDiscoveredFlows - tw.getProcess().getName():"+tw.getProcess().getName() + " - ");
                printer.printFlowSectionHeader(tw.getProcess().getName());
                if (tw.getProcess().getStartingProcessItemId() != null) {
                    printer.printSubSectionHeader("Fluxo de Execução (Legado)");
                    traverseServicePath(tw.getProcess(), tw.getProcess().getStartingProcessItemId(), "1", new HashSet<>(),new HashSet<>());
                }
            }
        }
    }

    /**
     * NOVO MÉTODO
     * Ponto de entrada para visitar e navegar por um BPD Legado (que não usa BPMN 2.0).
     *
     * @param bpd O objeto Bpd desserializado, contendo a estrutura do diagrama.
     */
    private void navigateAndPrintLegacyBpdFlow(Bpd bpd) {
        if (bpd == null || bpd.getBusinessProcessDiagram() == null) return;
        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();

        // Extrai todos os FlowObjects (atividades, eventos, gateways) de todas as lanes
        List<FlowObject> allFlowObjects = diagram.getPools().stream()
                .filter(Objects::nonNull)
                .flatMap(pool -> pool.getLanes() != null ? pool.getLanes().stream() : Stream.empty())
                .filter(Objects::nonNull)
                .flatMap(lane -> lane.getFlowObjects() != null ? lane.getFlowObjects().stream() : Stream.empty())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Cria mapas para navegação eficiente: um para os nós e outro para as conexões
        Map<String, FlowObject> nodeMap = allFlowObjects.stream()
                .collect(Collectors.toMap(FlowObject::getId, fo -> fo));

        Map<String, List<Flow>> sourceIdToFlowMap = buildLegacyBpdLinkMap(diagram.getFlows(), allFlowObjects);
        // --- LOG 1: Imprimir todos os FlowObjects encontrados ---
       //System.out.println("\n--- [LOG1] Conteúdo de allFlowObjects (" + allFlowObjects.size() + " itens) ---");
      //  allFlowObjects.forEach(fo -> {
           //System.out.println("  - ID: " + fo.getId() + ", Nome: " + fo.getName() + ", Tipo: " + fo.getComponentType() );
   //     });
       //System.out.println("-------------------------------------------------");

// --- LOG 2: Imprimir o mapa de nós (nodeMap) ---
       //System.out.println("\n--- [LOG2] Conteúdo de nodeMap (" + nodeMap.size() + " itens) ---");
  //      nodeMap.forEach((key, value) -> {
           //System.out.println("  - Chave: " + key + " -> Valor (Nome do Nó): " + value.getName());
   //     });
       //System.out.println("---------------------------------------------");

// --- LOG 3: Imprimir o mapa de conexões (sourceIdToFlowMap) ---
       //System.out.println("\n--- [LOG3] Conteúdo de sourceIdToFlowMap (" + sourceIdToFlowMap.size() + " fontes) ---");
    //    sourceIdToFlowMap.forEach((sourceId, flowList) -> {
           //System.out.println("  - ID de Origem: " + sourceId);
      //      flowList.forEach(flow -> {
               //System.out.println("    -> Leva para o ID de Destino: " + flow.getTargetObjectId() + " (Via Flow ID: " + flow.getId() + ")");
      //      });
 //       });
       //System.out.println("-------------------------------------------------------");

        // Encontra o nó inicial do fluxo (um evento do tipo "Start")
        Optional<FlowObject> startNodeOpt = allFlowObjects.stream()
                .filter(fo -> "Event".equals(fo.getComponentType()) && fo.getComponent() != null && "1".equals(fo.getComponent().getEventType()))
                .findFirst();

        if (startNodeOpt.isPresent()) {
            printer.printSubSectionHeader("Fluxo de Execução");
            traverseLegacyBpdPath(startNodeOpt.get(), 1, new HashSet<>(), nodeMap, sourceIdToFlowMap);
        }
    }

    /**
     * NOVO MÉTODO
     * Navega recursivamente pelo fluxo de um BPD Legado, seguindo as conexões (<flow>).
     */
    private void traverseLegacyBpdPath(FlowObject currentNode, int depth, Set<String> visitedInPath, Map<String, FlowObject> nodeMap, Map<String, List<Flow>> sourceIdToFlowMap) {
       //System.out.println("traverseLegacyBpdPath - currentNode: "+ currentNode.getName() + " - "+currentNode.getComponent().toString());

        if (currentNode == null || !visitedInPath.add(currentNode.getId())) {
            if (currentNode != null) printer.printMergePoint(currentNode.getName(), depth);
            return;
        }
        printer.printFlowStep(currentNode, depth);

        printer.printBpdAssignments(currentNode.getAssignments(), getIndent(depth));
        if (currentNode.getComponent() != null) {
            printer.printBpdParameterMappings(currentNode.getComponent().getImplementation(), getIndent(depth));
        }

        // Lógica para encontrar e referenciar chamadas a subprocessos
        if (currentNode.getComponent() != null && currentNode.getComponent().getImplementation() != null) {
            Implementation impl = currentNode.getComponent().getImplementation();
            String subprocessRef = impl.getEmbeddedProcessId();
            if (subprocessRef == null || subprocessRef.isEmpty()) {
                subprocessRef = impl.getAttachedProcessId();
            }
            if (subprocessRef == null || subprocessRef.isEmpty()) {
                subprocessRef = impl.getAttachedActivityId();
            }
            //System.out.println("subprocessRef: "+subprocessRef);
            if (subprocessRef != null && !subprocessRef.isEmpty()) {
                ProcessLoader.ArtifactLocation location = loader.findArtifactLocation(subprocessRef);
                if (location != null) {
                    printer.printReferenciaSubprocesso(location.objectInfo, ""); // Indentação controlada pelo printer
                } else {
                    printer.printMensagem(getIndent(depth + 1) + "- ⚠️ **Subprocesso não encontrado:** " + subprocessRef, "");
                }
            }
        }

        List<Flow> outgoingFlows = sourceIdToFlowMap.getOrDefault(currentNode.getId(), Collections.emptyList());
        if (outgoingFlows.isEmpty()) {
            // Verifica se não é um evento de fim antes de imprimir a mensagem
            if (!("Event".equals(currentNode.getComponentType()) && "2".equals(currentNode.getComponent().getEventType()))) {
                printer.printFlowStep("*(Fim do Fluxo)*", depth + 1);
            }
        } else {
            for (Flow flow : outgoingFlows) {
                if ("Gateway".equals(currentNode.getComponentType())) {
                    printer.printLegacyFlowCondition(flow, getIndent(depth));
                }

                FlowObject nextNode = nodeMap.get(flow.getTargetObjectId());
                if (nextNode != null) {
                    traverseLegacyBpdPath(nextNode, depth + 1, new HashSet<>(visitedInPath), nodeMap, sourceIdToFlowMap);
                }
            }
        }
    }

    /**
     * NOVO MÉTODO AUXILIAR
     * Constrói o mapa de links para um BPD Legado. Este método é crucial para conectar
     * as atividades, lendo as referências das portas de entrada/saída de cada uma.
     */
    private Map<String, List<Flow>> buildLegacyBpdLinkMap(List<Flow> flows, List<FlowObject> allFlowObjects) {
        if (flows == null || allFlowObjects == null) return new HashMap<>();

        // Mapeia cada Flow (seta) para seu objeto de origem e destino
        for (Flow flow : flows) {
            for (FlowObject fo : allFlowObjects) {
                // Encontra a origem da seta
                if (fo.getOutputPorts() != null) {
                    fo.getOutputPorts().stream()
                            .filter(p -> p.getFlow() != null && flow.getId().equals(p.getFlow().getRef()))
                            .findFirst().ifPresent(p -> flow.setSourceObjectId(fo.getId()));
                }
                // Encontra o destino da seta
                if (fo.getInputPorts() != null) {
                    fo.getInputPorts().stream()
                            .filter(p -> p.getFlow() != null && flow.getId().equals(p.getFlow().getRef()))
                            .findFirst().ifPresent(p -> flow.setTargetObjectId(fo.getId()));
                }
            }
        }

        // Agrupa as setas pelo ID de seu objeto de origem
        return flows.stream()
                .filter(f -> f.getSourceObjectId() != null)
                .collect(Collectors.groupingBy(Flow::getSourceObjectId));
    }

    /**
     * Identifica e navega pelos diferentes tipos de fluxo de um processo BPMN.
     */
    private void navigateAndPrintBPMNFlows(Process processo) {
        Map<String, FlowNode> nodeMap = createNodeMap(processo.getFlowElements());
        Map<String, List<SequenceFlow>> flowMap = createSourceIdToFlowMap(processo.getSequenceFlows());
        Set<String> visitedNodes = new HashSet<>();


        // 1. Fluxo Principal
        printer.printSubSectionHeader("Fluxo Principal");
        nodeMap.values().stream()
                .filter(StartEvent.class::isInstance).map(StartEvent.class::cast)
                .filter(se -> se.getUcaMessageEventDefinition() == null) // Não é UCA
                .forEach(startEvent -> traverseFlowPath(startEvent, 1, visitedNodes, nodeMap, flowMap));

        // 2. Fluxos Alternativos (UCAs)
        List<StartEvent> ucaEvents = nodeMap.values().stream()
                .filter(StartEvent.class::isInstance).map(StartEvent.class::cast)
                .filter(se -> se.getUcaMessageEventDefinition() != null)
                .collect(Collectors.toList());

        if (!ucaEvents.isEmpty()) {
            printer.printSubSectionHeader("Fluxos Alternativos (Iniciados por Eventos UCA)");
            for (StartEvent uca : ucaEvents) {
                printer.printAlternativeFlowStart(uca.getName());
                traverseFlowPath(uca, 1, visitedNodes, nodeMap, flowMap);
            }
        }

        // 3. Atividades Ad-Hoc (atividades sem fluxos de entrada que não foram visitadas)
        List<FlowNode> adHocNodes = nodeMap.values().stream()
                .filter(node -> !visitedNodes.contains(node.getId()))
                .filter(node -> node.getIncoming() == null || node.getIncoming().isEmpty())
                .collect(Collectors.toList());

        if (!adHocNodes.isEmpty()) {
            printer.printSubSectionHeader("Atividades Ad-Hoc (Execução Manual)");
            adHocNodes.forEach(node -> printer.printFlowStep(node, 1));
        }
    }


    /**
     * O novo método de travessia recursiva para o relatório de fluxo.
     */
    private void traverseFlowPath(FlowNode currentNode, int depth, Set<String> visitedNodes,
                                  Map<String, FlowNode> nodeMap, Map<String, List<SequenceFlow>> flowMap) {

        if (currentNode == null) return;

        // Se o nó já foi visitado (ex: um fluxo alternativo que converge), para aqui.
        if (!visitedNodes.add(currentNode.getId())) {
            printer.printMergePoint(currentNode.getName(), depth);
            return;
        }

        printer.printFlowStep(currentNode, depth);

        // Lógica especial para Gateways
        if (currentNode instanceof ExclusiveGateway) {
            printer.printGatewayDecision(currentNode.getName(), depth + 1);
        }

        List<SequenceFlow> outgoingFlows = flowMap.getOrDefault(currentNode.getId(), Collections.emptyList());

        for (SequenceFlow flow : outgoingFlows) {
            if (currentNode instanceof ExclusiveGateway) {
                printer.printBranchStart(flow.getName(), depth + 2);
            }

            FlowNode nextNode = nodeMap.get(flow.getTargetRef());
            traverseFlowPath(nextNode, (currentNode instanceof ExclusiveGateway ? depth + 2 : depth + 1), visitedNodes, nodeMap, flowMap);
        }
    }

    // --- MÉTODOS "VISIT" E DE NAVEGAÇÃO ---
    // (O restante da classe permanece o mesmo da versão anterior, pois a lógica de navegação está correta)

    private void visitServicoLegado(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process, String indent, Set<String> visitedGlobal) {
        printer.printLegacyVariables(process.getProcessParameters(), process.getProcessVariables(), indent);
        if (process.getStartingProcessItemId() != null) {
            printer.printSectionHeader("Fluxo de Execução (Legado)", indent);
            traverseServicePath(process, process.getStartingProcessItemId(), indent, new HashSet<>(), visitedGlobal);
        }
    }

    private void visitCoachflowProcess(CoachFlow coachflow, String serviceName, String indent, Set<String> visitedGlobal) {
        GlobalUserTask globalUserTask = coachflow.getDefinitions().getGlobalUserTask();
        if (globalUserTask != null && globalUserTask.getImplementation() != null) {
            Process bpmnProcess = new Process();
            bpmnProcess.setName(serviceName);
            UserTaskImplementation implementation = globalUserTask.getImplementation();
            bpmnProcess.setFlowElements(implementation.getFlowElements());
            bpmnProcess.setSequenceFlows(implementation.getSequenceFlows());
            this.visitProcessDefinition(bpmnProcess, indent, visitedGlobal);
        }
    }

    private void visitProcessDefinition(Process processo, String indent, Set<String> visitedGlobal) {
        if (processo == null) return;
        printer.printDataObjects(processo, indent);

        Map<String, FlowNode> contextNodeMap = createNodeMap(processo.getFlowElements());
        Map<String, List<SequenceFlow>> contextSourceIdToFlowMap = createSourceIdToFlowMap(processo.getSequenceFlows());

        printer.printSectionHeader("Fluxo de Execução Principal", indent);
        List<StartEvent> mainStartEvents = contextNodeMap.values().stream()
                .filter(StartEvent.class::isInstance).map(StartEvent.class::cast)
                .filter(se -> se.getUcaMessageEventDefinition() == null)
                .collect(Collectors.toList());

        Set<String> nosVisitados = new HashSet<>();
        for (StartEvent startEvent : mainStartEvents) {
            traverseBpmnPath(startEvent, indent, nosVisitados, contextNodeMap, contextSourceIdToFlowMap, visitedGlobal);
        }

        List<StartEvent> ucaStartEvents = contextNodeMap.values().stream()
                .filter(StartEvent.class::isInstance).map(StartEvent.class::cast)
                .filter(se -> se.getUcaMessageEventDefinition() != null && !nosVisitados.contains(se.getId()))
                .collect(Collectors.toList());

        if (!ucaStartEvents.isEmpty()) {
            printer.printSectionHeader("Fluxos Alternativos (Iniciados por Eventos/UCA)", indent);
            for (StartEvent startEvent : ucaStartEvents) {
                traverseBpmnPath(startEvent, indent, nosVisitados, contextNodeMap, contextSourceIdToFlowMap, visitedGlobal);
            }
        }
    }
    private void visitLegacyBpd(Bpd bpd, String indent) {
        if (bpd == null || bpd.getBusinessProcessDiagram() == null) return;
        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();

        // Extrai todos os FlowObjects de todas as lanes
        List<FlowObject> allFlowObjects = diagram.getPools().stream()
                .filter(Objects::nonNull)
                .flatMap(pool -> pool.getLanes() != null ? pool.getLanes().stream() : Stream.empty())
                .filter(Objects::nonNull)
                .flatMap(lane -> lane.getFlowObjects() != null ? lane.getFlowObjects().stream() : Stream.empty())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, FlowObject> nodeMap = allFlowObjects.stream()
                .collect(Collectors.toMap(FlowObject::getId, fo -> fo));

        Map<String, List<Flow>> sourceIdToFlowMap = buildLegacyBpdLinkMap(diagram.getFlows(), allFlowObjects);

        // Encontra o nó inicial
        Optional<FlowObject> startNodeOpt = allFlowObjects.stream()
                .filter(fo -> "Event".equals(fo.getComponentType()) && fo.getComponent() != null && "1".equals(fo.getComponent().getEventType()))
                .findFirst();

        if (startNodeOpt.isPresent()) {
            printer.printSectionHeader("Fluxo de Execução", indent);
            traverseLegacyBpdPath(startNodeOpt.get(), indent, new HashSet<>(), nodeMap, sourceIdToFlowMap);
        }
    }

/*
    private void traverseServicePath(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process, String currentItemId                      , String indent, Set<String> pathVisited, Set<String> globalVisited) {
        if (currentItemId == null || !pathVisited.add(loader.normalizeId(currentItemId))) {
            return;
        }
        Map<String, Item> itemMap = process.getItems().stream().collect(Collectors.toMap(item -> loader.normalizeId(item.getProcessItemId()), i -> i));
        Item currentItem = itemMap.get(loader.normalizeId(currentItemId));
        if (currentItem == null) return;

        printer.printEtapaDoFluxo(currentItem, indent);

        br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.TWComponent component = currentItem.getTwComponent();
        if (component != null) {
            printer.printScript(component.getScript(), indent + "  ");
            String subprocessRef = component.getAttachedProcessRef();
            if (subprocessRef != null && !subprocessRef.isEmpty()) {
                ProcessLoader.ArtifactLocation location = loader.findArtifactLocation(subprocessRef);
                if (location != null) {
                    printer.printReferenciaSubprocesso(location.objectInfo, indent);
                } else {
                    printer.printMensagem(indent + "  - ⚠️ **Subprocesso não encontrado:** " + subprocessRef, "");
                }
            }
        }
        List<Link> outgoingLinks = Collections.emptyList();
        if (process.getLinks() != null) {
            outgoingLinks = process.getLinks().stream()
                    .filter(link -> loader.normalizeId(currentItemId).equals(loader.normalizeId(link.getFromProcessItemId())))
                    .collect(Collectors.toList());
        }

        boolean isGateway = "Switch".equalsIgnoreCase(currentItem.getTWComponentName());

        Map<String, String> conditionMap = new HashMap<>();

        if (isGateway && component != null && component.getSwitchConditions() != null) {
            for (SwitchCondition condition : component.getSwitchConditions()) {
                // Mapeia o ID do estado final (a "saída" da condição) para o script da expressão
                if (condition.getEndStateId() != null && condition.getCondition() != null) {
                    conditionMap.put(condition.getEndStateId(), condition.getCondition());
                }
            }
        }

        if (outgoingLinks.isEmpty()) {
            if (!"ExitPoint".equalsIgnoreCase(currentItem.getTWComponentName())) {
                printer.printMensagem(indent + "- ⏹️ *(Fim do Fluxo)*", "");
            }
        } else {
            for (Link link : outgoingLinks) {
                if (isGateway) {
                    String expression =  conditionMap.get(link.getEndStateId());
                    printer.printLegacyServiceCondition(link, expression, indent);
                }
                traverseServicePath(process, link.getToProcessItemId(), indent, new HashSet<>(pathVisited), globalVisited);
            }
        }
    }*/

    private void traverseServicePath(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process, String currentItemId, String indent, Set<String> pathVisited, Set<String> globalVisited) {
        if (currentItemId == null || !pathVisited.add(loader.normalizeId(currentItemId))) {
            return; // Evita loops
        }

        Map<String, Item> itemMap = process.getItems().stream().collect(Collectors.toMap(item -> loader.normalizeId(item.getProcessItemId()), i -> i));
        Item currentItem = itemMap.get(loader.normalizeId(currentItemId));
        if (currentItem == null) return;

        printer.printEtapaDoFluxo(currentItem, indent);

        TWComponent component = currentItem.getTwComponent();
        if (component != null) {
            printer.printScript(component.getScript(), indent + "  ");

            // Lógica para expandir o subprocesso
            String subprocessRef = component.getAttachedProcessRef();
            if (subprocessRef != null && !subprocessRef.isEmpty()) {
                // Usa o 'loader' para encontrar a localização do artefato
                ProcessLoader.ArtifactLocation location = loader.findArtifactLocation(subprocessRef);
                if (location != null) {
                    // Usa o 'printer' para imprimir a referência de forma padronizada
                    printer.printReferenciaSubprocesso(location.objectInfo, indent);
                } else {
                    printer.printMensagem(indent + "  - ⚠️ **Subprocesso não encontrado:** " + subprocessRef, "");
                }
            }
        }

        // Encontra o próximo link no fluxo
        Link nextLink = process.getLinks().stream()
                .filter(link -> loader.normalizeId(currentItemId).equals(loader.normalizeId(link.getFromProcessItemId())))
                .findFirst().orElse(null);

        if (nextLink == null) {
            // Se não houver próximo link e não for um ponto de fim, imprime "Fim do Fluxo"
            if (!"ExitPoint".equalsIgnoreCase(currentItem.getTWComponentName())) {
                printer.printMensagem(indent + "- ⏹️ *(Fim do Fluxo)*", "");
            }
        } else {
            // Continua a travessia para o próximo item
            traverseServicePath(process, nextLink.getToProcessItemId(), indent, new HashSet<>(pathVisited), globalVisited);
        }
    }


    private void traverseLegacyBpdPath(FlowObject currentNode, String indent, Set<String> visitedInPath, Map<String, FlowObject> nodeMap, Map<String, List<Flow>> sourceIdToFlowMap) {
        if (currentNode == null || !visitedInPath.add(currentNode.getId())) {
            if (currentNode != null) printer.printMensagem(indent + "- ↪️ *(Loop detectado para '" + currentNode.getName() + "')*", "");
            return;
        }
        printer.printEtapaDoFluxo(currentNode, indent);

        // Lógica para chamar subprocessos
        if (currentNode.getComponent() != null && currentNode.getComponent().getImplementation() != null) {
            String subprocessRef = currentNode.getComponent().getImplementation().getAttachedActivityId();
            if (subprocessRef != null && !subprocessRef.isEmpty()) {
                ProcessLoader.ArtifactLocation location = loader.findArtifactLocation(subprocessRef);
                if (location != null) {
                    printer.printReferenciaSubprocesso(location.objectInfo, indent);
                } else {
                    printer.printMensagem(indent + "  - ⚠️ **Subprocesso não encontrado:** " + subprocessRef, "");
                }
            }
        }

        List<Flow> outgoingFlows = sourceIdToFlowMap.getOrDefault(currentNode.getId(), Collections.emptyList());
        if (outgoingFlows.isEmpty()) {
            if (!"Event".equals(currentNode.getComponentType()) || !"2".equals(currentNode.getComponent().getEventType())) {
                printer.printMensagem(indent + "- ⏹️ *(Fim do Fluxo)*", "");
            }
        } else {
            for (Flow flow : outgoingFlows) {
                FlowObject nextNode = nodeMap.get(flow.getTargetObjectId());
                if (nextNode != null) {
                    traverseLegacyBpdPath(nextNode, indent, new HashSet<>(visitedInPath), nodeMap, sourceIdToFlowMap);
                }
            }
        }
    }
    private void traverseBpmnPath(FlowNode currentNode, String indent, Set<String> visitedInPath,
                                  Map<String, FlowNode> contextNodeMap, Map<String, List<SequenceFlow>> contextSourceIdToFlowMap,
                                  Set<String> globalVisited) {
        System.out.println( "traverseBpmnPath: "+currentNode.getName());
        if (currentNode == null || !visitedInPath.add(loader.normalizeId(currentNode.getId()))) {
            if (currentNode != null) printer.printMensagem(indent + "- ↪️ *(Loop detectado para '" + currentNode.getName() + "')*", "");
            return;
        }
        printer.printEtapaDoFluxo(currentNode, indent);
        if (currentNode instanceof SubProcess) {
            handleSubProcess((SubProcess) currentNode, indent, globalVisited);
        } else if (currentNode instanceof CallActivity) {
            handleCallActivity((CallActivity) currentNode, indent, globalVisited);
        } else if (currentNode instanceof ScriptTask) {
            printer.printScript(((ScriptTask) currentNode).getScript(), indent);
        } else if (currentNode instanceof FormTask) {
            FormTask formTask = (FormTask) currentNode;
            if (formTask.getFormDefinition() != null && formTask.getFormDefinition().getCoachDefinition() != null) {
                printer.imprimirLayoutCoach(formTask.getFormDefinition().getCoachDefinition().getLayout(), indent + "  ");
            }
        }
        if (currentNode instanceof EndEvent) return;
        List<SequenceFlow> outgoingFlows = contextSourceIdToFlowMap.getOrDefault(loader.normalizeId(currentNode.getId()), Collections.emptyList());
        for (SequenceFlow flow : outgoingFlows) {
            if (currentNode instanceof ExclusiveGateway) {
                printer.printFlowCondition(flow, indent);
            }

            FlowNode nextNode = contextNodeMap.get(loader.normalizeId(flow.getTargetRef()));
            if (nextNode != null) {
                traverseBpmnPath(nextNode, indent, new HashSet<>(visitedInPath), contextNodeMap, contextSourceIdToFlowMap, globalVisited);
            }
        }
    }

    private void handleSubProcess(SubProcess sub, String indent, Set<String> globalVisited) {
        printer.printMensagem(indent + "  - **Iniciando Subprocesso Embutido:** " + sub.getName(), "");
        Map<String, FlowNode> subNodeMap = createNodeMap(sub.getFlowElements());
        Map<String, List<SequenceFlow>> subSourceIdToFlowMap = createSourceIdToFlowMap(sub.getSequenceFlows());
        List<StartEvent> subStartEvents = subNodeMap.values().stream().filter(StartEvent.class::isInstance).map(StartEvent.class::cast).collect(Collectors.toList());
        if (subStartEvents.isEmpty()) {
            printer.printMensagem(indent + "    - *[AVISO] Nenhum evento de início encontrado.*", "");
        } else {
            for (StartEvent subStart : subStartEvents) {
                traverseBpmnPath(subStart, indent + "    ", new HashSet<>(), subNodeMap, subSourceIdToFlowMap, globalVisited);
            }
        }
        printer.printMensagem(indent + "  - **Fim do Subprocesso Embutido:** " + sub.getName(), "");
    }

    private void handleCallActivity(CallActivity call, String indent, Set<String> globalVisited) {
        String calledElementId = call.getCalledElement();
        if (calledElementId != null && !calledElementId.isEmpty()) {
            ProcessLoader.ArtifactLocation location = loader.findArtifactLocation(calledElementId);
            if(location != null) {
                printer.printReferenciaSubprocesso(location.objectInfo, indent);
            } else {
                printer.printMensagem(indent + "  - ⚠️ **Subprocesso não encontrado:** " + calledElementId, "");
            }
        }
    }

    private Map<String, FlowNode> createNodeMap(List<Object> elements) {
        return Optional.ofNullable(elements).orElse(Collections.emptyList()).stream()
                .filter(FlowNode.class::isInstance).map(FlowNode.class::cast)
                .collect(Collectors.toMap(node -> loader.normalizeId(node.getId()), node -> node, (a, b) -> a));
    }

    private Map<String, List<SequenceFlow>> createSourceIdToFlowMap(List<SequenceFlow> flows) {
        return Optional.ofNullable(flows).orElse(Collections.emptyList()).stream()
                .filter(flow -> flow.getSourceRef() != null)
                .collect(Collectors.groupingBy(flow -> loader.normalizeId(flow.getSourceRef())));
    }

    private String getIndent(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < depth; i++) {
            sb.append("  "); // 2 espaços por nível de profundidade
        }
        return sb.toString();
    }


    // ============================================================================
    // ============= NOVOS MÉTODOS PARA O RELATÓRIO SEQUENCIAL ====================
    // ============================================================================


    /**
     * PONTO DE ENTRADA PÚBLICO para gerar o relatório de fluxo sequencial.
     * Este relatório expande todos os subprocessos e serviços em linha.
     * @param rootObjectId O ID do processo ou serviço raiz para iniciar a análise.
     */
    public void generateSequentialReport(String rootObjectId) {
        printer.printFlowReportTitle("Análise de Fluxo Sequencial Detalhado");
        Object rootArtifact = loader.getArtefatoDoCache(rootObjectId);

        if (rootArtifact == null) {
            printer.printMensagem("Artefato raiz com ID '" + rootObjectId + "' não foi encontrado no cache.", "");
            return;
        }

        // Imprime o cabeçalho do processo raiz
        if (rootArtifact instanceof Definitions) {
            printer.printFlowSectionHeader(((Definitions) rootArtifact).getProcess().getName());
        } else if (rootArtifact instanceof Teamworks) {
            Teamworks tw = (Teamworks) rootArtifact;
            if (tw.getProcess() != null) {
                printer.printFlowSectionHeader(tw.getProcess().getName());
            } else if (tw.getBpd() != null) {
                printer.printFlowSectionHeader(tw.getBpd().getName());
            }
        }


        // Adiciona o cabeçalho do processo raiz
        ProcessLoader.ArtifactLocation location = loader.findArtifactLocation(rootObjectId);
        if (location != null) {
            printer.printFlowSectionHeader(location.objectInfo.getName());
        }
        Set<String> artefatosJaImpressos = new HashSet<>();

        // Adiciona o processo raiz à lista de impressos para garantir que a âncora funcione.
        if (rootArtifact != null) {
            artefatosJaImpressos.add(location.objectInfo.getId());
            printer.registrarArtefatoParaIndice(location.objectInfo); // Garante que o raiz esteja no índice
        }
        // Inicia o processo de visitação sequencial
        visitArtifactSequentially(rootArtifact, "", new HashSet<>(), artefatosJaImpressos);
    }

    /**
     * Dispatcher que determina o tipo de artefato e inicia a travessia de seu fluxo.
     * Este método é chamado tanto para o processo raiz quanto para os subprocessos expandidos.
     */
    private void visitArtifactSequentially(Object artefatoObj, String indent, Set<String> visitedGlobal, Set<String> printedArtifacts) {
        if (artefatoObj instanceof Definitions) {
            Process bpmnProcess = ((Definitions) artefatoObj).getProcess();
            if (bpmnProcess != null) {
                traverseBPMNPathSequentially(bpmnProcess, indent, visitedGlobal, printedArtifacts);
            }
        } else if (artefatoObj instanceof Teamworks) {
            Teamworks tw = (Teamworks) artefatoObj;
            if (tw.getProcess() != null) {
                if (tw.getProcess().getCoachflow() != null && tw.getProcess().getCoachflow().getDefinitions() != null) {
                    GlobalUserTask gut = tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask();
                    if (gut != null && gut.getImplementation() != null) {
                        Process coachflowProcess = new Process();
                        coachflowProcess.setName(tw.getProcess().getName());
                        coachflowProcess.setFlowElements(gut.getImplementation().getFlowElements());
                        coachflowProcess.setSequenceFlows(gut.getImplementation().getSequenceFlows());
                        traverseBPMNPathSequentially(coachflowProcess, indent, visitedGlobal, printedArtifacts);
                    }
                } else if (tw.getProcess().getStartingProcessItemId() != null) {
                    System.out.println("traverseServicePathSequentially: "+ tw.getProcess().getName());
                    traverseServicePathSequentially(tw.getProcess(), indent, visitedGlobal, printedArtifacts);
                }
                System.out.println("Erro"+tw.getProcess().getName());
            } else if (tw.getBpd() != null) {
                traverseLegacyBpdPathSequentially(tw.getBpd(), indent, visitedGlobal, printedArtifacts);
            }
        }
    }

    private void traverseServicePathSequentially(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process, String indent, Set<String> visitedGlobal, Set<String> printedArtifacts) {
        String startNodeId = process.getStartingProcessItemId();
        if (startNodeId != null) {
            traverseServiceRecursiveSequentially(process, startNodeId, indent, new HashSet<>(), visitedGlobal, printedArtifacts);
        }
    }


    /**
     * Ponto de entrada para a travessia de um processo BPMN. Encontra os eventos de início.
     */
    private void traverseBPMNPathSequentially(Process processo, String indent, Set<String> visitedGlobal, Set<String> printedArtifacts) {
        Map<String, FlowNode> nodeMap = createNodeMap(processo.getFlowElements());
        Map<String, List<SequenceFlow>> flowMap = createSourceIdToFlowMap(processo.getSequenceFlows());

        nodeMap.values().stream()
                .filter(StartEvent.class::isInstance)
                .forEach(startEvent -> traverseBpmnRecursiveSequentially(startEvent, indent, new HashSet<>(), nodeMap, flowMap, visitedGlobal, printedArtifacts));
    }



    /**
     * MÉTODO CORRIGIDO
     * Navega recursivamente por um fluxo BPMN, tratando gateways e outros nós corretamente.
     */
    private void traverseBpmnRecursiveSequentially(FlowNode currentNode, String indent, Set<String> visitedInPath, Map<String, FlowNode> nodeMap, Map<String, List<SequenceFlow>> flowMap, Set<String> visitedGlobal, Set<String> printedArtifacts) {
        if (currentNode == null || !visitedInPath.add(currentNode.getId())) {
            if (currentNode != null) printer.printCircularReference(currentNode.getName(), indent);
            return;
        }

        // Se o nó atual for um gateway, ele é tratado de forma especial para agrupar seus ramos.
        if (currentNode instanceof ExclusiveGateway) {
            printer.printGatewayDecision(currentNode.getName(), indent.length() / 2 + 1);

            List<SequenceFlow> outgoingFlows = flowMap.getOrDefault(currentNode.getId(), Collections.emptyList());

            // --- ALTERAÇÃO: Loop indexado para saber o vizinho anterior e próximo ---
            for (int i = 0; i < outgoingFlows.size(); i++) {
                SequenceFlow currentFlow = outgoingFlows.get(i);
                // Pega o caminho anterior (se não for o primeiro)
                SequenceFlow prevFlow = (i > 0) ? outgoingFlows.get(i - 1) : null;
                // Pega o próximo caminho (se não for o último)
                SequenceFlow nextFlow = (i < outgoingFlows.size() - 1) ? outgoingFlows.get(i + 1) : null;

                printer.printBranchStart(currentNode.getName(), currentFlow.getName(), currentFlow.getId(), indent.length() / 2 + 2, (i == 0));

                // ALTERAÇÃO: Passa os IDs dos fluxos vizinhos para gerar os links.
                printer.printBranchNavigationLinks(currentNode.getName(),
                        prevFlow != null ? prevFlow.getName() : null, prevFlow != null ? prevFlow.getId() : null,
                        nextFlow != null ? nextFlow.getName() : null, nextFlow != null ? nextFlow.getId() : null,
                        indent + "    ");

                printer.printModernFlowCondition(currentFlow, indent + "    ");

                FlowNode nextNode = nodeMap.get(currentFlow.getTargetRef());
                traverseBpmnRecursiveSequentially(nextNode, indent + "      ", new HashSet<>(visitedInPath), nodeMap, flowMap, visitedGlobal, printedArtifacts);
            }
            return;
        }

        // Para todos os outros tipos de nós, a impressão e o processamento continuam normalmente.
        printer.printEtapaDoFluxo(currentNode, indent);

        if (currentNode instanceof CallActivity) {
            printer.printBPMNParameterMappings((CallActivity) currentNode, indent);
            String calledElementId = ((CallActivity) currentNode).getCalledElement();
            if (calledElementId != null && !calledElementId.isEmpty()) {
                expandSubprocessSequentially(calledElementId, indent + "  ", visitedGlobal, printedArtifacts);
            }
        } else if (currentNode instanceof SubProcess) {
            handleSubProcessSequentially((SubProcess) currentNode, indent + "  ", visitedGlobal, printedArtifacts);
        } else if (currentNode instanceof ScriptTask) {
            printer.printScript(((ScriptTask) currentNode).getScript(), indent + "  ");
        }

        // Se for um evento de fim, a navegação para este caminho termina.
        if (currentNode instanceof EndEvent) return;

        // CORREÇÃO: Este loop agora executa para todos os nós que NÃO SÃO gateways,
        // garantindo que o fluxo continue após StartEvents, Tasks, etc.
        List<SequenceFlow> outgoingFlows = flowMap.getOrDefault(currentNode.getId(), Collections.emptyList());
        for (SequenceFlow flow : outgoingFlows) {
            FlowNode nextNode = nodeMap.get(flow.getTargetRef());
            traverseBpmnRecursiveSequentially(nextNode, indent, new HashSet<>(visitedInPath), nodeMap, flowMap, visitedGlobal, printedArtifacts);
        }
    }


    // Em: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/ProcessNavigator.java

    /**
     * MÉTODO COMPLETAMENTE ATUALIZADO
     * Navega recursivamente por um fluxo de Serviço Legado com a nova lógica de gateways (Switch).
     */
    private void traverseServiceRecursiveSequentially(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process, String currentItemId, String indent, Set<String> pathVisited, Set<String> globalVisited, Set<String> printedArtifacts) {
        if (currentItemId == null || !pathVisited.add(loader.normalizeId(currentItemId))) {
            return;
        }
        Map<String, Item> itemMap = process.getItems().stream().collect(Collectors.toMap(item -> loader.normalizeId(item.getProcessItemId()), i -> i));
        Item currentItem = itemMap.get(loader.normalizeId(currentItemId));
        if (currentItem == null) return;

        boolean isGateway = "Switch".equalsIgnoreCase(currentItem.getTWComponentName());

        // --- LÓGICA REESTRUTURADA PARA TRATAR GATEWAYS DE FORMA ESPECIAL ---

        // 1. Se o item atual for um Gateway (Switch), entra neste bloco.
        if (isGateway) {
            printer.printGatewayDecision(currentItem.getName(), indent.length() / 2 + 1);

            TWComponent component = currentItem.getTwComponent();
            Map<String, String> conditionMap = new HashMap<>();
            if (component != null && component.getSwitchConditions() != null) {
                for (SwitchCondition condition : component.getSwitchConditions()) {
                    if (condition.getEndStateId() != null && condition.getCondition() != null) {
                        conditionMap.put(condition.getEndStateId(), condition.getCondition());
                    }
                }
            }

            List<Link> outgoingLinks = process.getLinks().stream()
                    .filter(link -> loader.normalizeId(currentItem.getProcessItemId()).equals(loader.normalizeId(link.getFromProcessItemId())))
                    .collect(Collectors.toList());

            boolean isFirstBranch = true; // Flag para controlar o primeiro caminho
            for (int i = 0; i < outgoingLinks.size(); i++) {
                Link currentLink = outgoingLinks.get(i);
                Link prevLink = (i > 0) ? outgoingLinks.get(i - 1) : null;
                Link nextLink = (i < outgoingLinks.size() - 1) ? outgoingLinks.get(i + 1) : null;

                // ALTERAÇÃO: Passa o ID do link (currentLink.getProcessLinkId()) para o printer.
                printer.printBranchStart(currentItem.getName(), currentLink.getName(), currentLink.getProcessLinkId(), indent.length() / 2 + 2, (i == 0));

                // ALTERAÇÃO: Passa os IDs dos links vizinhos para gerar os links.
                printer.printBranchNavigationLinks(currentItem.getName(),
                        prevLink != null ? prevLink.getName() : null, prevLink != null ? prevLink.getProcessLinkId() : null,
                        nextLink != null ? nextLink.getName() : null, nextLink != null ? nextLink.getProcessLinkId() : null,
                        indent + "    ");

                String expression = conditionMap.get(currentLink.getEndStateId());
                printer.printLegacyServiceCondition(currentLink, expression, indent + "    ");

                traverseServiceRecursiveSequentially(process, currentLink.getToProcessItemId(), indent + "      ", new HashSet<>(pathVisited), globalVisited, printedArtifacts);
            }
            // Para a execução aqui, pois todos os caminhos do gateway já foram explorados.
            return;
        }

        // 2. Para todos os outros tipos de nós, a lógica continua como antes.
        printer.printEtapaDoFluxo(currentItem, indent);

        TWComponent component = currentItem.getTwComponent();
        if (component != null) {
            printer.printScript(component.getScript(), indent + "  ");

            if ("CoachNG".equalsIgnoreCase(currentItem.getTWComponentName()) && component.getLayoutData() != null && !component.getLayoutData().isEmpty()) {
                try {
                    CoachLayout layout = parseLayoutData(component.getLayoutData());
                    printer.imprimirLayoutCoach(layout, indent + "  ");
                } catch (Exception e) {
                    printer.printMensagem(indent + "  - ‼️ **ERRO:** Falha ao ler a estrutura da interface (Coach).", "");
                }
            }
            String subprocessRef = component.getAttachedProcessRef();
            if (subprocessRef != null && !subprocessRef.isEmpty()) {
                expandSubprocessSequentially(subprocessRef, indent + "  ", globalVisited, printedArtifacts);
            }
        }

        // Navegação padrão para nós sequenciais
        List<Link> outgoingLinks = process.getLinks().stream()
                .filter(link -> loader.normalizeId(currentItemId).equals(loader.normalizeId(link.getFromProcessItemId())))
                .collect(Collectors.toList());

        if (outgoingLinks.isEmpty()) {
            if (!"ExitPoint".equalsIgnoreCase(currentItem.getTWComponentName())) {
                printer.printMensagem(indent + "- ⏹️ *(Fim do Fluxo)*", "");
            }
        } else {
            // Para nós não-gateways, geralmente haverá apenas um link.
            for (Link link : outgoingLinks) {
                traverseServiceRecursiveSequentially(process, link.getToProcessItemId(), indent, new HashSet<>(pathVisited), globalVisited, printedArtifacts);
            }
        }
    }

    /**
     * NOVO MÉTODO AUXILIAR
     * Desserializa a string XML contida na tag <layoutData> para um objeto CoachLayout.
     */
    private CoachLayout parseLayoutData(String layoutXml) throws Exception {
        if (layoutXml == null || layoutXml.trim().isEmpty()) {
            return null;
        }
        // Limpa o XML de caracteres de escape comuns
        String unescapedXml = layoutXml.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");

        Unmarshaller unmarshaller = this.coachLayoutContext.createUnmarshaller();
        return (CoachLayout) unmarshaller.unmarshal(new StringReader(unescapedXml));
    }
    /**
     * Ponto de entrada para a travessia de um BPD Legado. Encontra o nó inicial.
     */
    private void traverseLegacyBpdPathSequentially(Bpd bpd, String indent, Set<String> visitedGlobal, Set<String> printedArtifacts) {
        if (bpd == null || bpd.getBusinessProcessDiagram() == null) return;
        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();
        List<FlowObject> allFlowObjects = diagram.getPools().stream()
                .flatMap(pool -> pool.getLanes() != null ? pool.getLanes().stream() : Stream.empty())
                .flatMap(lane -> lane.getFlowObjects() != null ? lane.getFlowObjects().stream() : Stream.empty())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<String, FlowObject> nodeMap = allFlowObjects.stream().collect(Collectors.toMap(FlowObject::getId, fo -> fo));
        Map<String, List<Flow>> sourceIdToFlowMap = buildLegacyBpdLinkMap(diagram.getFlows(), allFlowObjects);

        Optional<FlowObject> startNodeOpt = allFlowObjects.stream()
                .filter(fo -> "Event".equals(fo.getComponentType()) && fo.getComponent() != null && "1".equals(fo.getComponent().getEventType()))
                .findFirst();

        if (startNodeOpt.isPresent()) {
            traverseLegacyBpdRecursiveSequentially(startNodeOpt.get(), indent, new HashSet<>(), nodeMap, sourceIdToFlowMap, visitedGlobal, printedArtifacts);
        }
    }

    /**
     * Navega recursivamente por um fluxo de BPD Legado.
     */
    private void traverseLegacyBpdRecursiveSequentially(FlowObject currentNode, String indent, Set<String> visitedInPath, Map<String, FlowObject> nodeMap, Map<String, List<Flow>> sourceIdToFlowMap, Set<String> visitedGlobal, Set<String> printedArtifacts) {
        if (currentNode == null || !visitedInPath.add(currentNode.getId())) {
            if (currentNode != null) printer.printCircularReference(currentNode.getName(), indent);
            return;
        }
        printer.printEtapaDoFluxo(currentNode, indent);

        printer.printBpdAssignments(currentNode.getAssignments(), indent);
        if (currentNode.getComponent() != null) {
            printer.printBpdParameterMappings(currentNode.getComponent().getImplementation(), indent);
        }
        if (currentNode.getComponent() != null && currentNode.getComponent().getImplementation() != null) {
            String subprocessRef = currentNode.getComponent().getImplementation().getAttachedActivityId();
            if (subprocessRef != null && !subprocessRef.isEmpty()) {
                expandSubprocessSequentially(subprocessRef, indent + "  ", visitedGlobal, printedArtifacts);
            }
        }

        List<Flow> outgoingFlows = sourceIdToFlowMap.getOrDefault(currentNode.getId(), Collections.emptyList());
        for (Flow flow : outgoingFlows) {
            FlowObject nextNode = nodeMap.get(flow.getTargetObjectId());
            if (nextNode != null) {
                traverseLegacyBpdRecursiveSequentially(nextNode, indent, new HashSet<>(visitedInPath), nodeMap, sourceIdToFlowMap, visitedGlobal, printedArtifacts);
            }
        }
    }


    private void expandSubprocessSequentially(String objectId, String indent, Set<String> visitedGlobal, Set<String> printedArtifacts) {
        ProcessLoader.ArtifactLocation location = loader.findArtifactLocation(objectId);
        if (location == null) {
            printer.printMensagem(indent + "- ⚠️ **Subprocesso não encontrado:** " + objectId, "");
            return;
        }

        // *** LÓGICA CENTRAL DA MUDANÇA ***
        if (printedArtifacts.contains(location.objectInfo.getId())) {
            printer.printReferenceToPrintedArtifact(location, indent);
            return; // Já foi impresso, então apenas cria o link e para.
        }

        // Se for a primeira vez, adiciona ao set para não imprimir de novo.
        printedArtifacts.add(location.objectInfo.getId());
        printer.registrarArtefatoParaIndice(location.objectInfo); // Garante que esteja no índice

        if (!visitedGlobal.add(location.objectInfo.getId())) {
            printer.printCircularReference(location.objectInfo.getName(), indent);
            return;
        }

        System.out.println(indent + "[NAVIGATOR-DEBUG] >>> Entrando no subprocesso: " + location.objectInfo.getName());
        printer.printSubprocessStart(location.objectInfo.getName(), location.objectInfo.getType(), indent);

        Object artifactToExpand = loader.getArtefatoDoCache(location.objectInfo.getId());
        if (artifactToExpand == null) {
            System.err.println(indent + "[NAVIGATOR-ERRO] Falha crítica! Artefato '" + location.objectInfo.getName() + "' não encontrado no cache do loader.");
            printer.printMensagem(indent + "  - ‼️ **ERRO:** Não foi possível carregar o conteúdo deste subprocesso.", "");
        } else {
            visitArtifactSequentially(artifactToExpand, indent + "  ", visitedGlobal, printedArtifacts);
        }

        printer.printSubprocessEnd(location.objectInfo.getName(), indent);
        System.out.println(indent + "[NAVIGATOR-DEBUG] <<< Saindo do subprocesso: " + location.objectInfo.getName());
        visitedGlobal.remove(location.objectInfo.getId());
    }

    /**
     * NOVO MÉTODO
     * Lida com a expansão de um SubProcesso embutido durante a geração do relatório sequencial.
     */
    private void handleSubProcessSequentially(SubProcess sub, String indent, Set<String> visitedGlobal, Set<String> printedArtifacts) {
        printer.printSubprocessStart(sub.getName(), "Subprocesso Embutido", indent);

        // Cria um contexto de navegação local apenas com os elementos do subprocesso
        Map<String, FlowNode> subNodeMap = createNodeMap(sub.getFlowElements());
        Map<String, List<SequenceFlow>> subFlowMap = createSourceIdToFlowMap(sub.getSequenceFlows());

        // Encontra o(s) evento(s) de início DENTRO do subprocesso
        List<StartEvent> subStartEvents = subNodeMap.values().stream()
                .filter(StartEvent.class::isInstance)
                .map(StartEvent.class::cast)
                .collect(Collectors.toList());

        if (subStartEvents.isEmpty()) {
            printer.printMensagem(indent + "  - *[AVISO] Nenhum evento de início encontrado dentro do subprocesso.*", "");
        } else {
            // Inicia a navegação recursiva para cada ponto de partida interno
            for (StartEvent subStart : subStartEvents) {
                traverseBpmnRecursiveSequentially(subStart, indent + "  ", new HashSet<>(), subNodeMap, subFlowMap, visitedGlobal, printedArtifacts);
            }
        }

        printer.printSubprocessEnd(sub.getName(), indent);
    }
}