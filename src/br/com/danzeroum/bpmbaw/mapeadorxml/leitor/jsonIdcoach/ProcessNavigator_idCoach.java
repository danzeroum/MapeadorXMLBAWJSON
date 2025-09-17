package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIdcoach;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Navegador do processo que usa a API da ProcessPrinter_idCoach (âncoras e navegação).
 */
public class ProcessNavigator_idCoach {

    private final ProcessPrinter_idCoach printer;
    private final ProcessLoader_idCoach loader;
    private JAXBContext coachLayoutContext;

    public ProcessNavigator_idCoach(ProcessPrinter_idCoach printer, ProcessLoader_idCoach loader) {
        this.printer = printer;
        this.loader = loader;
        try {
            this.coachLayoutContext = JAXBContext.newInstance(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout.class);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar JAXBContext para CoachLayout", e);
        }
    }

    /* ========================= RELATÓRIO COMPLETO (ÍNDICE + DETALHES) ========================= */

    public void generateReport() {
        Map<String, Object> artefatos = loader.getCacheDeArtefatos();

        // Preenche índice
        artefatos.keySet().forEach(artefactId -> {
            ProcessLoader_idCoach.ArtifactLocation loc = loader.findArtifactLocation(artefactId);
            if (loc != null) printer.registrarArtefatoParaIndice(loc.objectInfo);
        });
        printer.printIndex();

        // Detalhes
        artefatos.forEach((id, obj) -> {
            ProcessLoader_idCoach.ArtifactLocation loc = loader.findArtifactLocation(id);
            if (loc == null) return;
            if (!printer.printHeaderDoArtefato(loc)) return;

            if (obj instanceof Definitions) {
                visitProcessDefinition(((Definitions) obj).getProcess(), "", new HashSet<>());
            } else if (obj instanceof Teamworks) {
                Teamworks tw = (Teamworks) obj;

                if (tw.getProcess() != null) {
                    // Coachflow → trata como BPMN virtual
                    if (tw.getProcess().getCoachflow() != null
                            && tw.getProcess().getCoachflow().getDefinitions() != null
                            && tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask() != null
                            && tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask().getImplementation() != null) {

                        Process coachflow = new Process();
                        coachflow.setName(tw.getProcess().getName());
                        coachflow.setFlowElements(tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask().getImplementation().getFlowElements());
                        coachflow.setSequenceFlows(tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask().getImplementation().getSequenceFlows());
                        visitProcessDefinition(coachflow, "", new HashSet<>());
                    } else {
                        // Serviço legado
                        visitServicoLegado(tw.getProcess(), "", new HashSet<>());
                    }
                } else if (tw.getBpd() != null) {
                    visitLegacyBpd(tw.getBpd(), "");
                }
            }
        });
    }

    /* ========================= RELATÓRIO DE FLUXO (visual) ========================= */

    public void generateFlowReport(String rootObjectId) {
        printer.printFlowReportTitle("Análise de Fluxo de Processo");
        printer.printReportLegend();

        Object root = loader.getArtefatoDoCache(rootObjectId);
        if (root == null) {
            printer.printSubSectionHeader("Aviso: artefato raiz '" + rootObjectId + "' não encontrado no cache.");
            return;
        }
        printDiscoveredFlows(root);
    }

    private void printDiscoveredFlows(Object artefatoObj) {
        if (artefatoObj == null) return;

        // BPMN moderno
        if (artefatoObj instanceof Definitions) {
            Process bpmn = ((Definitions) artefatoObj).getProcess();
            if (bpmn != null) {
                printer.printFlowSectionHeader(bpmn.getName());
                navigateAndPrintBPMNFlows(bpmn);
            }
            return;
        }

        // Teamworks (serviço/coaches/BPD legado)
        if (artefatoObj instanceof Teamworks) {
            Teamworks tw = (Teamworks) artefatoObj;

            // Coachflow → converte para BPMN virtual e reaproveita a navegação BPMN
            if (tw.getProcess() != null && tw.getProcess().getCoachflow() != null
                    && tw.getProcess().getCoachflow().getDefinitions() != null
                    && tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask() != null
                    && tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask().getImplementation() != null) {

                printer.printFlowSectionHeader(tw.getProcess().getName());
                Process coachflow = new Process();
                coachflow.setName(tw.getProcess().getName());
                coachflow.setFlowElements(tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask().getImplementation().getFlowElements());
                coachflow.setSequenceFlows(tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask().getImplementation().getSequenceFlows());
                navigateAndPrintBPMNFlows(coachflow);
                return;
            }

            // BPD legado
            if (tw.getBpd() != null) {
                printer.printFlowSectionHeader(tw.getBpd().getName());
                navigateAndPrintLegacyBpdFlow(tw.getBpd());
                return;
            }

            // Serviço legado (process items/links)
            if (tw.getProcess() != null) {
                printer.printFlowSectionHeader(tw.getProcess().getName());
                if (tw.getProcess().getStartingProcessItemId() != null) {
                    printer.printSubSectionHeader("Fluxo de Execução (Legado)");
                    traverseServicePath(tw.getProcess(), tw.getProcess().getStartingProcessItemId(), "1", new HashSet<>(), new HashSet<>());
                }
            }
        }
    }

    /* ========================= BPMN (visual) ========================= */

    private void navigateAndPrintBPMNFlows(Process processo) {
        Map<String, FlowNode> nodeMap = createNodeMap(processo.getFlowElements());
        Map<String, List<SequenceFlow>> flowMap = createSourceIdToFlowMap(processo.getSequenceFlows());
        Set<String> visited = new HashSet<>();

        // 1) Principal
        printer.printSubSectionHeader("Fluxo Principal");
        nodeMap.values().stream()
                .filter(StartEvent.class::isInstance).map(StartEvent.class::cast)
                .filter(se -> se.getUcaMessageEventDefinition() == null)
                .forEach(start -> traverseFlowPath(start, 1, visited, nodeMap, flowMap));

        // 2) Alternativos (UCA) - sem duplicar por nome
        List<StartEvent> ucas = nodeMap.values().stream()
                .filter(StartEvent.class::isInstance).map(StartEvent.class::cast)
                .filter(se -> se.getUcaMessageEventDefinition() != null)
                .collect(Collectors.toList());

        if (!ucas.isEmpty()) {
            printer.printSubSectionHeader("Fluxos Alternativos (Iniciados por UCA)");
            Set<String> seen = new HashSet<>();
            for (StartEvent uca : ucas) {
                String title = uca.getName() == null ? "(Sem nome)" : uca.getName().trim();
                if (!seen.add(title.toLowerCase(Locale.ROOT))) continue; // pula duplicado por nome
                printer.printFlowStep(uca, 1);
                traverseFlowPath(uca, 1, new HashSet<>(visited), nodeMap, flowMap);
            }
        }


        // 3) Ad-hoc (sem incoming e não visitados)
        List<FlowNode> adHoc = nodeMap.values().stream()
                .filter(n -> !visited.contains(n.getId()))
                .filter(n -> n.getIncoming() == null || n.getIncoming().isEmpty())
                .collect(Collectors.toList());

        if (!adHoc.isEmpty()) {
            printer.printSubSectionHeader("Atividades Ad-Hoc (Execução Manual)");
            adHoc.forEach(n -> printer.printFlowStep(n, 1));
        }
    }

    private void traverseFlowPath(FlowNode current, int depth, Set<String> visited,
                                  Map<String, FlowNode> nodeMap, Map<String, List<SequenceFlow>> flowMap) {

        if (current == null) return;

        if (!visited.add(current.getId())) {
            printer.printMergePoint(current.getName(), depth);
            return;
        }

        printer.printFlowStep(current, depth);
        List<SequenceFlow> outgoing = flowMap.getOrDefault(current.getId(), Collections.emptyList());

        if (current instanceof ExclusiveGateway) {
            // Imprime ramos com navegação
            for (int i = 0; i < outgoing.size(); i++) {
                SequenceFlow cur = outgoing.get(i);
                SequenceFlow prev = (i > 0) ? outgoing.get(i - 1) : null;
                SequenceFlow next = (i < outgoing.size() - 1) ? outgoing.get(i + 1) : null;

                printer.printBranchStart(current.getName(), cur.getName(), cur.getId(), depth + 2, i == 0);
                printer.printBranchNavigationLinks(
                        current.getName(),
                        prev != null ? prev.getName() : null, prev != null ? prev.getId() : null,
                        next != null ? next.getName() : null, next != null ? next.getId() : null,
                        getIndent(depth + 2)
                );
                printer.printModernFlowCondition(cur, getIndent(depth + 2));

                FlowNode nextNode = nodeMap.get(cur.getTargetRef());
                traverseFlowPath(nextNode, depth + 2, new HashSet<>(visited), nodeMap, flowMap);
            }
            return;
        }

        // Nó comum
        for (SequenceFlow f : outgoing) {
            FlowNode next = nodeMap.get(f.getTargetRef());
            traverseFlowPath(next, depth + 1, new HashSet<>(visited), nodeMap, flowMap);
        }
    }

    /* ========================= BPD legado (visual) ========================= */

    private void navigateAndPrintLegacyBpdFlow(Bpd bpd) {
        if (bpd == null || bpd.getBusinessProcessDiagram() == null) return;
        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();

        List<FlowObject> all = diagram.getPools().stream()
                .filter(Objects::nonNull)
                .flatMap(pool -> pool.getLanes() != null ? pool.getLanes().stream() : Stream.empty())
                .filter(Objects::nonNull)
                .flatMap(lane -> lane.getFlowObjects() != null ? lane.getFlowObjects().stream() : Stream.empty())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, FlowObject> nodeMap = all.stream().collect(Collectors.toMap(FlowObject::getId, fo -> fo));
        Map<String, List<Flow>> sourceIdToFlow = buildLegacyBpdLinkMap(diagram.getFlows(), all);

        Optional<FlowObject> startOpt = all.stream()
                .filter(fo -> "Event".equals(fo.getComponentType()) && fo.getComponent() != null && "1".equals(fo.getComponent().getEventType()))
                .findFirst();

        startOpt.ifPresent(start -> {
            printer.printSubSectionHeader("Fluxo de Execução");
            traverseLegacyBpdPath(start, 1, new HashSet<>(), nodeMap, sourceIdToFlow);
        });
    }

    private void traverseLegacyBpdPath(FlowObject current, int depth, Set<String> visited,
                                       Map<String, FlowObject> nodeMap, Map<String, List<Flow>> sourceIdToFlow) {
        if (current == null || !visited.add(current.getId())) {
            if (current != null) printer.printMergePoint(current.getName(), depth);
            return;
        }

        printer.printFlowStep(current, depth);
        printer.printBpdAssignments(current.getAssignments(), getIndent(depth));
        if (current.getComponent() != null) {
            printer.printBpdParameterMappings(current.getComponent().getImplementation(), getIndent(depth));
        }

        // referência a subprocessos
        if (current.getComponent() != null && current.getComponent().getImplementation() != null) {
            Implementation impl = current.getComponent().getImplementation();
            String subprocessRef = impl.getEmbeddedProcessId();
            if (!notBlank(subprocessRef)) subprocessRef = impl.getAttachedProcessId();
            if (!notBlank(subprocessRef)) subprocessRef = impl.getAttachedActivityId();

            if (notBlank(subprocessRef)) {
                ProcessLoader_idCoach.ArtifactLocation loc = loader.findArtifactLocation(subprocessRef);
                if (loc != null) {
                    printer.printReferenciaSubprocesso(loc.objectInfo, "");
                }
            }
        }

        List<Flow> outgoing = sourceIdToFlow.getOrDefault(current.getId(), Collections.emptyList());
        if (outgoing.isEmpty()) return;

        for (Flow flow : outgoing) {
            if ("Gateway".equals(current.getComponentType())) {
                printer.printLegacyFlowCondition(flow, getIndent(depth));
            }
            FlowObject next = nodeMap.get(flow.getTargetObjectId());
            traverseLegacyBpdPath(next, depth + 1, new HashSet<>(visited), nodeMap, sourceIdToFlow);
        }
    }

    private Map<String, List<Flow>> buildLegacyBpdLinkMap(List<Flow> flows, List<FlowObject> allFlowObjects) {
        if (flows == null || allFlowObjects == null) return new HashMap<>();

        for (Flow flow : flows) {
            for (FlowObject fo : allFlowObjects) {
                if (fo.getOutputPorts() != null) {
                    fo.getOutputPorts().stream()
                            .filter(p -> p.getFlow() != null && flow.getId().equals(p.getFlow().getRef()))
                            .findFirst().ifPresent(p -> flow.setSourceObjectId(fo.getId()));
                }
                if (fo.getInputPorts() != null) {
                    fo.getInputPorts().stream()
                            .filter(p -> p.getFlow() != null && flow.getId().equals(p.getFlow().getRef()))
                            .findFirst().ifPresent(p -> flow.setTargetObjectId(fo.getId()));
                }
            }
        }

        return flows.stream()
                .filter(f -> f.getSourceObjectId() != null)
                .collect(Collectors.groupingBy(Flow::getSourceObjectId));
    }

    /* ========================= VISIT (detalhes por tipo) ========================= */

    private void visitServicoLegado(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process,
                                    String indent, Set<String> visitedGlobal) {
        printer.printLegacyVariables(process.getProcessParameters(), process.getProcessVariables(), indent);
        if (process.getStartingProcessItemId() != null) {
            printer.printSubSectionHeader("Fluxo de Execução (Legado)");
            traverseServicePath(process, process.getStartingProcessItemId(), indent, new HashSet<>(), visitedGlobal);
        }
    }

    private void visitProcessDefinition(Process processo, String indent, Set<String> visitedGlobal) {
        if (processo == null) return;
        printer.printDataObjects(processo, indent);

        Map<String, FlowNode> nodeMap = createNodeMap(processo.getFlowElements());
        Map<String, List<SequenceFlow>> flowMap = createSourceIdToFlowMap(processo.getSequenceFlows());

        printer.printSubSectionHeader("Fluxo de Execução Principal");

        Set<String> visited = new HashSet<>();
        List<StartEvent> main = nodeMap.values().stream()
                .filter(StartEvent.class::isInstance).map(StartEvent.class::cast)
                .filter(se -> se.getUcaMessageEventDefinition() == null)
                .collect(Collectors.toList());

        for (StartEvent se : main) {
            // delega para o visual (profundidade baseada em indent)
            traverseFlowPath(se, indentToDepth(indent) + 1, visited, nodeMap, flowMap);
        }

        List<StartEvent> ucas = nodeMap.values().stream()
                .filter(StartEvent.class::isInstance).map(StartEvent.class::cast)
                .filter(se -> se.getUcaMessageEventDefinition() != null && !visited.contains(se.getId()))
                .collect(Collectors.toList());

        if (!ucas.isEmpty()) {
            printer.printSubSectionHeader("Fluxos Alternativos (Iniciados por UCA)");
            for (StartEvent se : ucas) {
                traverseFlowPath(se, indentToDepth(indent) + 1, visited, nodeMap, flowMap);
            }
        }
    }

    private void visitLegacyBpd(Bpd bpd, String indent) {
        if (bpd == null || bpd.getBusinessProcessDiagram() == null) return;

        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();
        List<FlowObject> all = diagram.getPools().stream()
                .filter(Objects::nonNull)
                .flatMap(pool -> pool.getLanes() != null ? pool.getLanes().stream() : Stream.empty())
                .filter(Objects::nonNull)
                .flatMap(lane -> lane.getFlowObjects() != null ? lane.getFlowObjects().stream() : Stream.empty())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, FlowObject> nodeMap = all.stream().collect(Collectors.toMap(FlowObject::getId, fo -> fo));
        Map<String, List<Flow>> sourceIdToFlow = buildLegacyBpdLinkMap(diagram.getFlows(), all);

        Optional<FlowObject> start = all.stream()
                .filter(fo -> "Event".equals(fo.getComponentType()) && fo.getComponent() != null && "1".equals(fo.getComponent().getEventType()))
                .findFirst();

        if (start.isPresent()) {
            printer.printSubSectionHeader("Fluxo de Execução");
            traverseLegacyBpdPath(start.get(), indentToDepth(indent) + 1, new HashSet<>(), nodeMap, sourceIdToFlow);
        }
    }

    /* ========================= Serviço legado (detalhe) ========================= */

    private void traverseServicePath(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process,
                                     String currentItemId, String indent,
                                     Set<String> pathVisited, Set<String> globalVisited) {
        if (currentItemId == null || !pathVisited.add(loader.normalizeId(currentItemId))) return;

        Map<String, Item> itemMap = process.getItems().stream()
                .collect(Collectors.toMap(i -> loader.normalizeId(i.getProcessItemId()), i -> i));

        Item current = itemMap.get(loader.normalizeId(currentItemId));
        if (current == null) return;

        int depth = indentToDepth(indent);
        printer.printFlowStep(current, depth);

        TWComponent comp = current.getTwComponent();
        if (comp != null) {
            printer.printScript(comp.getScript(), " ");
            String subprocessRef = comp.getAttachedProcessRef();
            if (notBlank(subprocessRef)) {
                ProcessLoader_idCoach.ArtifactLocation loc = loader.findArtifactLocation(subprocessRef);
                if (loc != null) {
                    printer.printReferenciaSubprocesso(loc.objectInfo, "");
                }
            }
        }

        List<Link> outgoing = process.getLinks().stream()
                .filter(link -> loader.normalizeId(currentItemId).equals(loader.normalizeId(link.getFromProcessItemId())))
                .collect(Collectors.toList());

        if (outgoing.isEmpty()) return;

        // Gateway (Switch) → imprime ramos com regras
        boolean isGateway = "Switch".equalsIgnoreCase(current.getTWComponentName());
        if (isGateway) {
            Map<String, String> conditionMap = new HashMap<>();
            if (comp != null && comp.getSwitchConditions() != null) {
                for (SwitchCondition c : comp.getSwitchConditions()) {
                    if (notBlank(c.getEndStateId()) && notBlank(c.getCondition())) {
                        conditionMap.put(c.getEndStateId(), c.getCondition());
                    }
                }
            }

            for (int i = 0; i < outgoing.size(); i++) {
                Link cur = outgoing.get(i);
                Link prev = (i > 0) ? outgoing.get(i - 1) : null;
                Link next = (i < outgoing.size() - 1) ? outgoing.get(i + 1) : null;

                printer.printBranchStart(current.getName(), cur.getName(), cur.getProcessLinkId(), depth + 2, i == 0);
                printer.printBranchNavigationLinks(
                        current.getName(),
                        prev != null ? prev.getName() : null, prev != null ? prev.getProcessLinkId() : null,
                        next != null ? next.getName() : null, next != null ? next.getProcessLinkId() : null,
                        getIndent(depth + 2)
                );

                String expr = conditionMap.get(cur.getEndStateId());
                printer.printLegacyServiceCondition(cur, expr, getIndent(depth + 2));

                traverseServicePath(process, cur.getToProcessItemId(), indent + "    ", new HashSet<>(pathVisited), globalVisited);
            }
            return;
        }

        // Nó sequencial comum (geralmente 1 link)
        for (Link l : outgoing) {
            traverseServicePath(process, l.getToProcessItemId(), indent, new HashSet<>(pathVisited), globalVisited);
        }
    }

    /* ========================= RELATÓRIO SEQUENCIAL (expansão inline) ========================= */

    public void generateSequentialReport(String rootObjectId) {
        printer.printFlowReportTitle("Análise de Fluxo Sequencial Detalhado");
        printer.printReportLegend();

        Object root = loader.getArtefatoDoCache(rootObjectId);
        if (root == null) {
            printer.printSubSectionHeader("Aviso: artefato raiz '" + rootObjectId + "' não encontrado no cache.");
            return;
        }

        ProcessLoader_idCoach.ArtifactLocation loc = loader.findArtifactLocation(rootObjectId);
        if (loc != null) printer.printFlowSectionHeader(loc.objectInfo.getName());

        Set<String> printed = new HashSet<>();
        Set<String> visitedGlobal = new HashSet<>();

        if (loc != null) {
            printed.add(loc.objectInfo.getId());
            printer.registrarArtefatoParaIndice(loc.objectInfo);
        }

        visitArtifactSequentially(root, "", visitedGlobal, printed);
    }

    private void visitArtifactSequentially(Object artefato, String indent, Set<String> visitedGlobal, Set<String> printed) {
        if (artefato instanceof Definitions) {
            Process bpmn = ((Definitions) artefato).getProcess();
            if (bpmn != null) traverseBPMNPathSequentially(bpmn, indent, visitedGlobal, printed);
            return;
        }
        if (artefato instanceof Teamworks) {
            Teamworks tw = (Teamworks) artefato;

            if (tw.getProcess() != null) {
                if (tw.getProcess().getCoachflow() != null && tw.getProcess().getCoachflow().getDefinitions() != null
                        && tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask() != null
                        && tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask().getImplementation() != null) {

                    Process coachflow = new Process();
                    coachflow.setName(tw.getProcess().getName());
                    coachflow.setFlowElements(tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask().getImplementation().getFlowElements());
                    coachflow.setSequenceFlows(tw.getProcess().getCoachflow().getDefinitions().getGlobalUserTask().getImplementation().getSequenceFlows());
                    traverseBPMNPathSequentially(coachflow, indent, visitedGlobal, printed);

                } else if (tw.getProcess().getStartingProcessItemId() != null) {
                    traverseServicePathSequentially(tw.getProcess(), indent, visitedGlobal, printed);
                }
            } else if (tw.getBpd() != null) {
                traverseLegacyBpdPathSequentially(tw.getBpd(), indent, visitedGlobal, printed);
            }
        }
    }

    private void traverseServicePathSequentially(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process,
                                                 String indent, Set<String> visitedGlobal, Set<String> printed) {
        String startId = process.getStartingProcessItemId();
        if (startId != null) {
            traverseServiceRecursiveSequentially(process, startId, indent, new HashSet<>(), visitedGlobal, printed);
        }
    }

    private void traverseBPMNPathSequentially(Process processo, String indent, Set<String> visitedGlobal, Set<String> printed) {
        Map<String, FlowNode> nodeMap = createNodeMap(processo.getFlowElements());
        Map<String, List<SequenceFlow>> flowMap = createSourceIdToFlowMap(processo.getSequenceFlows());

        nodeMap.values().stream()
                .filter(StartEvent.class::isInstance)
                .forEach(start -> traverseBpmnRecursiveSequentially(start, indent, new HashSet<>(), nodeMap, flowMap, visitedGlobal, printed));
    }

    private void traverseBpmnRecursiveSequentially(FlowNode current, String indent, Set<String> visitedInPath,
                                                   Map<String, FlowNode> nodeMap, Map<String, List<SequenceFlow>> flowMap,
                                                   Set<String> visitedGlobal, Set<String> printed) {
        if (current == null || !visitedInPath.add(current.getId())) {
            if (current != null) printer.printMergePoint(current.getName(), indentToDepth(indent));
            return;
        }

        int depth = indentToDepth(indent);

        if (current instanceof ExclusiveGateway) {
            List<SequenceFlow> outgoing = flowMap.getOrDefault(current.getId(), Collections.emptyList());
            for (int i = 0; i < outgoing.size(); i++) {
                SequenceFlow cur = outgoing.get(i);
                SequenceFlow prev = (i > 0) ? outgoing.get(i - 1) : null;
                SequenceFlow next = (i < outgoing.size() - 1) ? outgoing.get(i + 1) : null;

                printer.printBranchStart(current.getName(), cur.getName(), cur.getId(), depth + 2, i == 0);
                printer.printBranchNavigationLinks(
                        current.getName(),
                        prev != null ? prev.getName() : null, prev != null ? prev.getId() : null,
                        next != null ? next.getName() : null, next != null ? next.getId() : null,
                        indent + "    "
                );
                printer.printModernFlowCondition(cur, indent + "    ");

                FlowNode nextNode = nodeMap.get(cur.getTargetRef());
                traverseBpmnRecursiveSequentially(nextNode, indent + "      ", new HashSet<>(visitedInPath), nodeMap, flowMap, visitedGlobal, printed);
            }
            return;
        }

        printer.printFlowStep(current, depth);

        if (current instanceof CallActivity) {
            printer.printBPMNParameterMappings((CallActivity) current, indent);
            String calledId = ((CallActivity) current).getCalledElement();
            if (notBlank(calledId)) expandSubprocessSequentially(calledId, indent + "  ", visitedGlobal, printed);
        } else if (current instanceof SubProcess) {
            handleSubProcessSequentially((SubProcess) current, indent + "  ", visitedGlobal, printed);
        }

        if (current instanceof EndEvent) return;

        for (SequenceFlow f : flowMap.getOrDefault(current.getId(), Collections.emptyList())) {
            FlowNode next = nodeMap.get(f.getTargetRef());
            traverseBpmnRecursiveSequentially(next, indent, new HashSet<>(visitedInPath), nodeMap, flowMap, visitedGlobal, printed);
        }
    }

    private void traverseServiceRecursiveSequentially(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process,
                                                      String currentItemId, String indent, Set<String> pathVisited,
                                                      Set<String> visitedGlobal, Set<String> printed) {
        if (currentItemId == null || !pathVisited.add(loader.normalizeId(currentItemId))) return;

        Map<String, Item> itemMap = process.getItems().stream()
                .collect(Collectors.toMap(i -> loader.normalizeId(i.getProcessItemId()), i -> i));

        Item current = itemMap.get(loader.normalizeId(currentItemId));
        if (current == null) return;

        int depth = indentToDepth(indent);
        boolean isGateway = "Switch".equalsIgnoreCase(current.getTWComponentName());

        if (isGateway) {
            TWComponent comp = current.getTwComponent();
            Map<String, String> conditionMap = new HashMap<>();
            if (comp != null && comp.getSwitchConditions() != null) {
                for (SwitchCondition c : comp.getSwitchConditions()) {
                    if (notBlank(c.getEndStateId()) && notBlank(c.getCondition())) {
                        conditionMap.put(c.getEndStateId(), c.getCondition());
                    }
                }
            }

            List<Link> outgoing = process.getLinks().stream()
                    .filter(l -> loader.normalizeId(current.getProcessItemId()).equals(loader.normalizeId(l.getFromProcessItemId())))
                    .collect(Collectors.toList());

            for (int i = 0; i < outgoing.size(); i++) {
                Link cur = outgoing.get(i);
                Link prev = (i > 0) ? outgoing.get(i - 1) : null;
                Link next = (i < outgoing.size() - 1) ? outgoing.get(i + 1) : null;

                printer.printBranchStart(current.getName(), cur.getName(), cur.getProcessLinkId(), depth + 2, i == 0);
                printer.printBranchNavigationLinks(
                        current.getName(),
                        prev != null ? prev.getName() : null, prev != null ? prev.getProcessLinkId() : null,
                        next != null ? next.getName() : null, next != null ? next.getProcessLinkId() : null,
                        indent + "    "
                );

                String expr = conditionMap.get(cur.getEndStateId());
                printer.printLegacyServiceCondition(cur, expr, indent + "    ");

                traverseServiceRecursiveSequentially(process, cur.getToProcessItemId(), indent + "      ", new HashSet<>(pathVisited), visitedGlobal, printed);
            }
            return;
        }

        // Nó comum
        printer.printFlowStep(current, depth);

        TWComponent comp = current.getTwComponent();
        if (comp != null) {
            String subprocessRef = comp.getAttachedProcessRef();
            if (notBlank(subprocessRef)) {
                expandSubprocessSequentially(subprocessRef, indent + "  ", visitedGlobal, printed);
            }
        }

        List<Link> outgoing = process.getLinks().stream()
                .filter(l -> loader.normalizeId(currentItemId).equals(loader.normalizeId(l.getFromProcessItemId())))
                .collect(Collectors.toList());

        if (outgoing.isEmpty()) return;
        for (Link l : outgoing) {
            traverseServiceRecursiveSequentially(process, l.getToProcessItemId(), indent, new HashSet<>(pathVisited), visitedGlobal, printed);
        }
    }

    private br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout parseLayoutData(String layoutXml) throws Exception {
        if (layoutXml == null || layoutXml.trim().isEmpty()) return null;
        String unescaped = layoutXml.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");
        Unmarshaller u = this.coachLayoutContext.createUnmarshaller();
        return (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout) u.unmarshal(new StringReader(unescaped));
    }

    private void traverseLegacyBpdPathSequentially(Bpd bpd, String indent, Set<String> visitedGlobal, Set<String> printed) {
        if (bpd == null || bpd.getBusinessProcessDiagram() == null) return;
        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();

        List<FlowObject> all = diagram.getPools().stream()
                .flatMap(pool -> pool.getLanes() != null ? pool.getLanes().stream() : Stream.empty())
                .flatMap(lane -> lane.getFlowObjects() != null ? lane.getFlowObjects().stream() : Stream.empty())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, FlowObject> nodeMap = all.stream().collect(Collectors.toMap(FlowObject::getId, fo -> fo));
        Map<String, List<Flow>> sourceIdToFlow = buildLegacyBpdLinkMap(diagram.getFlows(), all);

        Optional<FlowObject> start = all.stream()
                .filter(fo -> "Event".equals(fo.getComponentType()) && fo.getComponent() != null && "1".equals(fo.getComponent().getEventType()))
                .findFirst();

        start.ifPresent(s -> traverseLegacyBpdRecursiveSequentially(s, indent, new HashSet<>(), nodeMap, sourceIdToFlow, visitedGlobal, printed));
    }

    private void traverseLegacyBpdRecursiveSequentially(FlowObject current, String indent, Set<String> visitedInPath,
                                                        Map<String, FlowObject> nodeMap, Map<String, List<Flow>> sourceIdToFlow,
                                                        Set<String> visitedGlobal, Set<String> printed) {
        if (current == null || !visitedInPath.add(current.getId())) {
            if (current != null) printer.printMergePoint(current.getName(), indentToDepth(indent));
            return;
        }

        printer.printFlowStep(current, indentToDepth(indent));
        printer.printBpdAssignments(current.getAssignments(), indent);
        if (current.getComponent() != null) {
            printer.printBpdParameterMappings(current.getComponent().getImplementation(), indent);
        }

        if (current.getComponent() != null && current.getComponent().getImplementation() != null) {
            String subprocessRef = current.getComponent().getImplementation().getAttachedActivityId();
            if (notBlank(subprocessRef)) {
                expandSubprocessSequentially(subprocessRef, indent + "  ", visitedGlobal, printed);
            }
        }

        for (Flow f : sourceIdToFlow.getOrDefault(current.getId(), Collections.emptyList())) {
            FlowObject next = nodeMap.get(f.getTargetObjectId());
            if (next != null) {
                traverseLegacyBpdRecursiveSequentially(next, indent, new HashSet<>(visitedInPath), nodeMap, sourceIdToFlow, visitedGlobal, printed);
            }
        }
    }

    private void expandSubprocessSequentially(String objectId, String indent, Set<String> visitedGlobal, Set<String> printed) {
        ProcessLoader_idCoach.ArtifactLocation loc = loader.findArtifactLocation(objectId);
        if (loc == null) return;

        if (printed.contains(loc.objectInfo.getId())) {
            printer.printReferenceToPrintedArtifact(loc, indent);
            return;
        }

        printed.add(loc.objectInfo.getId());
        printer.registrarArtefatoParaIndice(loc.objectInfo);

        if (!visitedGlobal.add(loc.objectInfo.getId())) {
            printer.printMergePoint(loc.objectInfo.getName(), indentToDepth(indent));
            return;
        }

        Object artifact = loader.getArtefatoDoCache(loc.objectInfo.getId());
        if (artifact != null) {
            visitArtifactSequentially(artifact, indent + "  ", visitedGlobal, printed);
        }

        visitedGlobal.remove(loc.objectInfo.getId());
    }

    private void handleSubProcessSequentially(SubProcess sub, String indent, Set<String> visitedGlobal, Set<String> printed) {
        Map<String, FlowNode> subNodeMap = createNodeMap(sub.getFlowElements());
        Map<String, List<SequenceFlow>> subFlowMap = createSourceIdToFlowMap(sub.getSequenceFlows());

        List<StartEvent> starts = subNodeMap.values().stream()
                .filter(StartEvent.class::isInstance).map(StartEvent.class::cast)
                .collect(Collectors.toList());

        for (StartEvent se : starts) {
            traverseBpmnRecursiveSequentially(se, indent + "  ", new HashSet<>(), subNodeMap, subFlowMap, visitedGlobal, printed);
        }
    }

    /* ========================= Helpers ========================= */

    private Map<String, FlowNode> createNodeMap(List<Object> elements) {
        return Optional.ofNullable(elements).orElse(Collections.emptyList()).stream()
                .filter(FlowNode.class::isInstance).map(FlowNode.class::cast)
                .collect(Collectors.toMap(n -> loader.normalizeId(n.getId()), n -> n, (a, b) -> a));
    }

    private Map<String, List<SequenceFlow>> createSourceIdToFlowMap(List<SequenceFlow> flows) {
        return Optional.ofNullable(flows).orElse(Collections.emptyList()).stream()
                .filter(f -> f.getSourceRef() != null)
                .collect(Collectors.groupingBy(f -> loader.normalizeId(f.getSourceRef())));
    }

    private String getIndent(int depth) {
        if (depth <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) sb.append("  ");
        return sb.toString();
    }

    private int indentToDepth(String indent) {
        return indent == null ? 0 : Math.max(0, indent.length() / 2);
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
