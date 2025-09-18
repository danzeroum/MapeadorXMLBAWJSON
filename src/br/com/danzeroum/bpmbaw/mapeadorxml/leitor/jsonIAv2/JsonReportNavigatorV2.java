package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services.BpmnProcessorService;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services.TeamworksProcessorService;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services.VariableEnricherService;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Bpd;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.BusinessProcessDiagram;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Pool;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.CoachView;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.Property;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass;

import java.util.*;

/**
 * Versão linear/sequencial a partir do root, com DFS iterativa na rootView e geração de executionPaths.
 * Compatível com Java 8.
 */
public class JsonReportNavigatorV2 {

    private final JsonReportGeneratorV2 generator;
    private final ProcessLoaderV2Plus loader;

    private final BpmnProcessorService bpmnProcessor;
    private final TeamworksProcessorService teamworksProcessor;
    private final ExecutionPathGeneratorService pathGenerator;
    private final VariableEnricherService variableEnricher;
    private static final Set<String> PRIMITIVE_TYPES = new HashSet<String>(Arrays.asList(
            "String", "Integer", "Boolean", "Decimal", "Date", "Time", "DateTime", "ANY"
    ));

    public JsonReportNavigatorV2(JsonReportGeneratorV2 generator, ProcessLoaderV2Plus loader) {
        this.generator = generator;
        this.loader = loader;

        // Inicializa todos os serviços necessários
        this.variableEnricher = new VariableEnricherService(generator, loader);
        this.bpmnProcessor = new BpmnProcessorService(generator, loader, variableEnricher);
        this.teamworksProcessor = new TeamworksProcessorService(generator, loader, variableEnricher);
        this.pathGenerator = new ExecutionPathGeneratorService();
    }

    // ============================
    // Entrada principal
    // ============================
    public void populateReport(String rootObjectId) {
        System.out.println("[NAVIGATOR V2] Iniciando orquestração da análise...");

        // 1. Processa a estrutura de todos os artefatos carregados
        processAllArtifacts();

        // 2. Após a estrutura estar montada, gera os caminhos de execução
        generateExecutionPaths();

        System.out.println("[NAVIGATOR V2] Análise orquestrada concluída com sucesso.");
    }

    // ============================
    // Ordem de artefatos a partir do root (BFS)
    // ============================
    private LinkedHashSet<String> buildArtifactOrderFromRoot(String rootClean) {
        LinkedHashSet<String> order = new LinkedHashSet<String>();
        Deque<String> q = new ArrayDeque<String>();
        Set<String> seen = new HashSet<String>();
        q.add(rootClean);
        seen.add(rootClean);

        while (!q.isEmpty()) {
            String cur = q.removeFirst();
            order.add(cur);

            List<JsonReportV2.FlowStep> shallow = generateOrderedStepsForArtifactShallow(cur);
            for (JsonReportV2.FlowStep fs : shallow) {
                String called = fs.getCalledArtifactId();
                if (called == null || called.trim().isEmpty()) continue;
                String clean = loader.getCleanId(called);
                if (clean != null && loader.getArtefatoDoCache(clean) != null && seen.add(clean)) {
                    q.addLast(clean);
                }
            }
        }

        // cobrir órfãos do cache (se houver)
        for (String id : loader.getCacheDeArtefatos().keySet()) order.add(id);
        return order;
    }

    private void processAllArtifacts() {
        // Cria uma cópia da lista de IDs para iterar com segurança
        System.out.println("[NAVIGATOR V2] FASE 1: Processando a estrutura de " + loader.getCacheDeArtefatos().size() + " artefatos...");

        List<String> artifactIds = new ArrayList<>(loader.getCacheDeArtefatos().keySet());

        for (String artifactId : artifactIds) {
            Object artifactData = loader.getArtefatoDoCache(artifactId);
            ProcessLoaderV2Plus.ArtifactLocation location = loader.findArtifactLocation(artifactId);

            if (location == null || location.objectInfo == null) {
                System.err.println("AVISO: Metadados não encontrados para o artefato: " + artifactId);
                continue;
            }

            // Cria ou obtém o objeto do artefato no relatório JSON.
            JsonReportV2.Artifact artifactJson = generator.createOrGetArtifact(
                    location.objectInfo.getId(),
                    location.objectInfo.getName(),
                    location.objectInfo.getType(),
                    location.filePath
            );

            // Delega o processamento para o serviço correto.
            if (artifactData instanceof Definitions) {
                bpmnProcessor.processDefinitions((Definitions) artifactData, artifactJson);
            } else if (artifactData instanceof Teamworks) {
                teamworksProcessor.processLegacyService(((Teamworks) artifactData).getProcess(), artifactJson);
            }
        }
    }

    private void generateExecutionPaths() {
        System.out.println("[NAVIGATOR V2] Gerando caminhos de execução para todos os artefatos...");
        List<JsonReportV2.Artifact> artifacts = generator.getReport().getArtifacts();

        for (JsonReportV2.Artifact artifact : artifacts) {
            try {
                // Chama o serviço para gerar os caminhos
                List<JsonReportV2.ExecutionPath> paths = pathGenerator.generateExecutionPaths(artifact);

                if (!paths.isEmpty()) {
                    // Adiciona os caminhos gerados à lista principal do relatório
                    generator.getReport().getExecutionPaths().addAll(paths);
                    System.out.printf("  -> Gerados %d caminhos de execução para o artefato: %s%n", paths.size(), artifact.getName());
                }
            } catch (Exception e) {
                System.err.printf("ERRO: Falha ao gerar caminhos de execução para o artefato '%s': %s%n", artifact.getName(), e.getMessage());
            }
        }
    }



    /** Passos ORDENADOS sem expandir subprocessos (para descoberta de chamadas). */
    private List<JsonReportV2.FlowStep> generateOrderedStepsForArtifactShallow(String artifactId) {
        Object obj = loader.getArtefatoDoCache(artifactId);
        if (obj instanceof Definitions) {
            Definitions defs = (Definitions) obj;
            if (defs.getProcess() != null) return generateOrderedStepsForProcess(defs.getProcess());
        } else if (obj instanceof Teamworks) {
            Teamworks tw = (Teamworks) obj;
            if (tw.getProcess() != null) return generateOrderedStepsForLegacyProcess(tw.getProcess());
        }
        return Collections.emptyList();
    }

    // ============================
    // RootView sequencial (DFS iterativa)
    // ============================
    private void buildRootViewIterative(JsonReportV2.Artifact rootArtifact, String rootCleanId, int depthLimit) {
        class Frame {
            final String artifactId;
            final List<JsonReportV2.FlowStep> steps;
            int index;
            final int depth;
            final String parentStepId;
            Frame(String a, List<JsonReportV2.FlowStep> s, int d, String p) {
                artifactId = a; steps = s; depth = d; parentStepId = p; index = 0;
            }
        }

        Deque<Frame> stack = new ArrayDeque<Frame>();
        Set<String> visited = new HashSet<String>();

        List<JsonReportV2.FlowStep> rootSteps = generateOrderedStepsForArtifactShallow(rootCleanId);
        if (rootSteps.isEmpty()) return;

        rootArtifact.getRootView().clear();
        stack.push(new Frame(rootCleanId, rootSteps, 0, null));
        visited.add(rootCleanId);

        int order = 0;
        while (!stack.isEmpty()) {
            Frame f = stack.peek();
            if (f.index >= f.steps.size()) { stack.pop(); continue; }

            JsonReportV2.FlowStep s = f.steps.get(f.index++);
            JsonReportV2.FlowStep c = cloneForRootView(s);
            c.setOrderIndex(order++);
            c.setSubflowDepth(f.depth);
            c.setParentStepId(f.parentStepId);
            rootArtifact.getRootView().add(c);

            if (s.getCalledArtifactId() != null && !s.getCalledArtifactId().trim().isEmpty() && f.depth < depthLimit) {
                String child = loader.getCleanId(s.getCalledArtifactId());
                if (child != null && loader.getArtefatoDoCache(child) != null && !visited.contains(child)) {
                    List<JsonReportV2.FlowStep> childSteps = generateOrderedStepsForArtifactShallow(child);
                    visited.add(child);
                    stack.push(new Frame(child, childSteps, f.depth + 1, s.getStepId()));
                }
            }
        }
    }

    // ===== executionPaths (happy + alternativas) =====
    private void buildExecutionPathsForArtifact(JsonReportV2.Artifact art) {
        if (art == null || art.getGraph() == null || art.getGraph().getEntryPoints() == null || art.getGraph().getEntryPoints().isEmpty()) {
            return;
        }

        List<List<String>> paths = new ArrayList<List<String>>();
        List<List<String>> conds = new ArrayList<List<String>>();

        // happy-path a partir dos 2 primeiros starts (se existirem)
        int maxStarts = Math.min(2, art.getGraph().getEntryPoints().size());
        for (int i = 0; i < maxStarts; i++) {
            String start = art.getGraph().getEntryPoints().get(i);
            List<String> p = new ArrayList<String>();
            List<String> cs = new ArrayList<String>();
            followPathGreedy(art, start, p, cs, 30);
            if (!p.isEmpty()) { paths.add(capRepeatsPerNode(p, 2)); conds.add(cs); }
        }

        // alternativa: primeira não-default no primeiro gateway do primeiro start
        if (!art.getGraph().getEntryPoints().isEmpty()) {
            List<String> pAlt = new ArrayList<String>();
            List<String> cAlt = new ArrayList<String>();
            followPathFirstNonDefault(art, art.getGraph().getEntryPoints().get(0), pAlt, cAlt, 30);
            if (!pAlt.isEmpty()) { paths.add(capRepeatsPerNode(pAlt, 2)); conds.add(cAlt); }
        }

        // evitar duplicatas
        Set<String> seen = new HashSet<String>();
        for (int i = 0; i < paths.size(); i++) {
            String sig = paths.get(i).toString();
            if (!seen.add(sig)) continue;

            JsonReportV2.ExecutionPath xp = new JsonReportV2.ExecutionPath();
            xp.setArtifactId(art.getId());
            xp.setPathId(i == 0 ? "happy-path" : (i == 1 ? "alt-1" : "alt-2"));
            xp.setSteps(paths.get(i));
            xp.setConditions(conds.get(i));

            // stepLabels (opcional, via reflection — não exige alterar JsonReportV2)
            try {
                List<String> labels = new ArrayList<String>();
                for (String nodeId : paths.get(i)) {
                    String name = findNodeName(art, nodeId);
                    String type = findNodeType(art, nodeId);
                    String lane = findNodeLane(art, nodeId);
                    String label = (name != null ? name : nodeId)
                            + (type != null ? " [" + type + "]" : "")
                            + (lane != null ? " — " + lane : "");
                    labels.add(label);
                }
                java.lang.reflect.Method m = xp.getClass().getMethod("setStepLabels", List.class);
                m.invoke(xp, labels);
            } catch (Throwable ignore) { /* ok se o modelo não tiver stepLabels */ }

            generator.getReport().getExecutionPaths().add(xp);
        }
    }

    // Nome amigável do nó (procura em graph.nodes e, se preciso, em flow)
    private String findNodeName(JsonReportV2.Artifact art, String nodeId) {
        if (art == null || art.getGraph() == null || art.getGraph().getNodes() == null) return nodeId;
        for (JsonReportV2.Node n : art.getGraph().getNodes()) {
            if (nodeId.equals(n.getId())) {
                if (n.getName() != null && !n.getName().trim().isEmpty()) return n.getName();
                if (art.getFlow() != null) {
                    for (JsonReportV2.FlowStep s : art.getFlow()) {
                        if (nodeId.equals(s.getStepId()) && s.getName() != null && !s.getName().trim().isEmpty()) {
                            return s.getName();
                        }
                    }
                }
                return nodeId;
            }
        }
        return nodeId;
    }

    private String findNodeType(JsonReportV2.Artifact art, String nodeId) {
        if (art == null || art.getGraph() == null || art.getGraph().getNodes() == null) return null;
        for (JsonReportV2.Node n : art.getGraph().getNodes()) {
            if (nodeId.equals(n.getId())) return n.getType();
        }
        return null;
    }

    private String findNodeLane(JsonReportV2.Artifact art, String nodeId) {
        if (art == null || art.getGraph() == null || art.getGraph().getNodes() == null) return null;
        for (JsonReportV2.Node n : art.getGraph().getNodes()) {
            if (nodeId.equals(n.getId())) return n.getLane();
        }
        return null;
    }

    // Cap simples para evitar loops muito longos no path
    private List<String> capRepeatsPerNode(List<String> in, int maxRepeated) {
        Map<String,Integer> seen = new HashMap<String,Integer>();
        List<String> out = new ArrayList<String>();
        for (String id : in) {
            Integer c = seen.get(id);
            c = (c == null ? 1 : c + 1);
            seen.put(id, c);
            out.add(id);
            if (c >= maxRepeated) {
                // bateu limite para este nó — corta aqui
                break;
            }
        }
        return out;
    }



    private void followPathGreedy(JsonReportV2.Artifact art, String startNodeId, List<String> outSteps, List<String> outConds, int max) {
        String cur = startNodeId; int steps = 0;
        while (cur != null && steps < max) {
            steps++;
            outSteps.add(cur);

            // tentar defaultFlow do gateway; senão primeira aresta
            String next = null;
            String chosenEdgeId = null;

            String defEdge = findDefaultFlowForGateway(art, cur);
            if (defEdge != null) {
                next = findEdgeTargetById(art, defEdge);
                chosenEdgeId = defEdge;
            }

            if (next == null) {
                String firstOutEdge = findFirstOutgoingEdgeId(art, cur);
                if (firstOutEdge != null) {
                    next = findEdgeTargetById(art, firstOutEdge);
                    chosenEdgeId = firstOutEdge;
                }
            }

            if (chosenEdgeId != null) {
                String cond = findConditionLabel(art, chosenEdgeId);
                if (cond != null) outConds.add(cond);
            }

            if (next == null || isEndNode(art, next)) {
                if (next != null) outSteps.add(next);
                break;
            }
            cur = next;
        }
    }

    private void followPathFirstNonDefault(JsonReportV2.Artifact art, String startNodeId, List<String> outSteps, List<String> outConds, int max) {
        String cur = startNodeId; int steps = 0; boolean diverted = false;
        while (cur != null && steps < max) {
            steps++;
            outSteps.add(cur);

            String next = null;
            String chosenEdge = null;
            List<JsonReportV2.Edge> outs = findOutgoingEdges(art, cur);

            if (outs != null && !outs.isEmpty()) {
                if (!diverted) {
                    String def = findDefaultFlowForGateway(art, cur);
                    for (JsonReportV2.Edge e : outs) {
                        if (def == null || !def.equals(e.getId())) {
                            chosenEdge = e.getId(); next = e.getTarget(); diverted = true; break;
                        }
                    }
                }
                if (next == null) { chosenEdge = outs.get(0).getId(); next = outs.get(0).getTarget(); }
            }

            if (chosenEdge != null) {
                String cond = findConditionLabel(art, chosenEdge);
                if (cond != null) outConds.add(cond);
            }

            if (next == null || isEndNode(art, next)) {
                if (next != null) outSteps.add(next);
                break;
            }
            cur = next;
        }
    }

    // --- helpers de grafo ---
    private String findEdgeTargetById(JsonReportV2.Artifact art, String edgeId) {
        if (edgeId == null || art.getGraph() == null || art.getGraph().getEdges() == null) return null;
        for (JsonReportV2.Edge e : art.getGraph().getEdges()) if (edgeId.equals(e.getId())) return e.getTarget();
        return null;
    }
    private String findFirstOutgoingEdgeId(JsonReportV2.Artifact art, String nodeId) {
        List<JsonReportV2.Edge> outs = findOutgoingEdges(art, nodeId);
        return (outs == null || outs.isEmpty()) ? null : outs.get(0).getId();
    }
    private List<JsonReportV2.Edge> findOutgoingEdges(JsonReportV2.Artifact art, String nodeId) {
        List<JsonReportV2.Edge> r = new ArrayList<JsonReportV2.Edge>();
        if (art.getGraph() == null || art.getGraph().getEdges() == null) return r;
        for (JsonReportV2.Edge e : art.getGraph().getEdges()) if (nodeId.equals(e.getSource())) r.add(e);
        return r;
    }
    private String findDefaultFlowForGateway(JsonReportV2.Artifact art, String nodeId) {
        if (art.getGraph() == null || art.getGraph().getGateways() == null) return null;
        for (JsonReportV2.Gateway g : art.getGraph().getGateways()) if (nodeId.equals(g.getId())) return g.getDefaultFlow();
        return null;
    }
    private boolean isEndNode(JsonReportV2.Artifact art, String nodeId) {
        if (art.getGraph() == null || art.getGraph().getEndPoints() == null) return false;
        for (String e : art.getGraph().getEndPoints()) if (nodeId.equals(e)) return true;
        return false;
    }
    private String findConditionLabel(JsonReportV2.Artifact art, String edgeId) {
        if (edgeId == null || art.getGraph() == null || art.getGraph().getConditions() == null) return null;
        for (JsonReportV2.EdgeCondition c : art.getGraph().getConditions()) if (edgeId.equals(c.getEdgeId())) {
            String expr = c.getExpression();
            if (expr == null) expr = "";
            expr = expr.replaceAll("\\s+", " ").trim();
            return "edge:" + edgeId + " (" + expr + ")";
        }
        return null;
    }

    private void visitBpmnDefinitions(Definitions defs, JsonReportV2.Artifact artifact) {
        Process process = defs.getProcess();
        if (process == null) return;

        populateBpmnVariables(process, artifact);
        addParticipantsFromLanes(process, artifact);

        Map<String, FlowNode> nodeMap = createNodeMap(process.getFlowElements());
        Map<String, List<SequenceFlow>> flowMap = createSourceIdToFlowMap(process.getSequenceFlows());
        Map<String, String> nodeToLane = mapNodeToLaneNames(process);

        for (FlowNode n : nodeMap.values()) {
            JsonReportV2.Node node = new JsonReportV2.Node();
            node.setId(n.getId());
            node.setType(getNodeType(n));
            node.setName(n.getName() != null ? n.getName() : n.getId());
            node.setLane(nodeToLane.get(n.getId()));
            artifact.getGraph().getNodes().add(node);
        }

        if (process.getSequenceFlows() != null) {
            for (SequenceFlow sf : process.getSequenceFlows()) {
                JsonReportV2.Edge edge = new JsonReportV2.Edge();
                edge.setId(sf.getId());
                edge.setSource(sf.getSourceRef());
                edge.setTarget(sf.getTargetRef());
                edge.setLabel(sf.getName());
                artifact.getGraph().getEdges().add(edge);

                if (sf.getConditionExpression() != null && sf.getConditionExpression().getExpression() != null) {
                    JsonReportV2.EdgeCondition ec = new JsonReportV2.EdgeCondition();
                    ec.setEdgeId(sf.getId());
                    ec.setExpression(normalizeExpr(sf.getConditionExpression().getExpression()));
                    ec.setDefault(false);
                    ec.setLanguage("TWX-Expr");
                    artifact.getGraph().getConditions().add(ec);
                }
            }
        }

        for (FlowNode n : nodeMap.values()) {
            if (n instanceof ExclusiveGateway) {
                ExclusiveGateway gw = (ExclusiveGateway) n;
                JsonReportV2.Gateway g = new JsonReportV2.Gateway();
                g.setId(gw.getId());
                g.setType("ExclusiveGateway");
                g.setDefaultFlow(gw.getDefaultFlow());
                artifact.getGraph().getGateways().add(g);
            }
        }

        List<String> startIds = new ArrayList<String>();
        List<String> endIds = new ArrayList<String>();
        for (FlowNode n : nodeMap.values()) {
            if (n instanceof StartEvent) startIds.add(n.getId());
            if (n instanceof EndEvent) endIds.add(n.getId());
        }
        artifact.getGraph().getEntryPoints().addAll(startIds);
        artifact.getGraph().getEndPoints().addAll(endIds);

        List<JsonReportV2.FlowStep> orderedSteps = generateOrderedStepsForProcess(process);
        artifact.getFlow().addAll(orderedSteps);

        // <<< NOVO: completa variables com o que aparecer nos mappings dos passos (CallActivity etc.)
        augmentVariablesFromFlowMappings(artifact, process);
    }

    /** Complementa artifact.variables (input/output) com base nos mappings dos passos do flow (BPMN). */
    private void augmentVariablesFromFlowMappings(JsonReportV2.Artifact artifact,
                                                  Process process) {
        if (artifact == null || artifact.getFlow() == null) return;

        // Índice de tipos do processo pai (IoSpecification)
        Map<String, String> typeByVarName = new HashMap<String, String>();
        if (process != null && process.getIoSpecification() != null) {
            IoSpecification pio = process.getIoSpecification();
            if (pio.getDataInputs() != null) {
                for (DataInput di : pio.getDataInputs()) {
                    if (di.getName() != null) typeByVarName.put(di.getName(), di.getItemSubjectRef());
                }
            }
            if (pio.getDataOutputs() != null) {
                for (DataOutput d : pio.getDataOutputs()) {
                    if (d.getName() != null) typeByVarName.put(d.getName(), d.getItemSubjectRef());
                }
            }
        }

        for (JsonReportV2.FlowStep s : artifact.getFlow()) {
            JsonReportV2.ParameterMapping pm = s.getParameterMapping();
            if (pm == null) continue;

            // INPUT: fonte (expressão do pai) => trate como variável "input"
            for (JsonReportV2.Mapping m : pm.getInput()) {
                if (m.getSource() == null) continue;
                for (String cand : parseCandidateVariableNames(m.getSource())) {
                    String typeGuess = typeByVarName.containsKey(cand) ? typeByVarName.get(cand) : "String";
                    ensureVariablePresent(artifact, cand, "input", typeGuess, false);
                }
            }

            // OUTPUT: destino (nome var do pai) => trate como "output"
            for (JsonReportV2.Mapping m : pm.getOutput()) {
                String targetVar = m.getTarget();
                if (targetVar == null || targetVar.trim().isEmpty()) continue;
                String root = rootIdentifier(targetVar);
                String typeGuess = typeByVarName.containsKey(root) ? typeByVarName.get(root) : "String";
                ensureVariablePresent(artifact, root, "output", typeGuess, false);
            }
        }
    }



    private void populateBpmnVariables(Process process, JsonReportV2.Artifact artifact) {
        IoSpecification io = process.getIoSpecification();
        if (io == null) return;

        if (io.getDataInputs() != null) {
            for (DataInput di : io.getDataInputs()) {
                JsonReportV2.VariableInfo v = new JsonReportV2.VariableInfo();
                v.setName(di.getName());
                v.setTypeId(di.getItemSubjectRef());
                v.setList(di.getIsCollection() != null && di.getIsCollection().booleanValue());
                enrichVariable(v, new HashSet<String>());
                artifact.getVariables().getInput().add(v);
            }
        }
        if (io.getDataOutputs() != null) {
            for (DataOutput d : io.getDataOutputs()) {
                JsonReportV2.VariableInfo v = new JsonReportV2.VariableInfo();
                v.setName(d.getName());
                v.setTypeId(d.getItemSubjectRef());
                v.setList(d.getIsCollection() != null && d.getIsCollection().booleanValue());
                enrichVariable(v, new HashSet<String>());
                artifact.getVariables().getOutput().add(v);
            }
        }
    }

    private void addParticipantsFromLanes(Process process, JsonReportV2.Artifact artifact) {
        if (process.getLaneSet() == null || process.getLaneSet().getLanes() == null) return;
        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Lane lane : process.getLaneSet().getLanes()) {
            if (lane.getPartitionElementRef() != null && !lane.getPartitionElementRef().trim().isEmpty()) {
                String participantName = resolveParticipantName(lane.getPartitionElementRef());
                if (participantName == null || participantName.trim().isEmpty()) {
                    participantName = lane.getPartitionElementRef(); // fallback
                }
                if (!artifact.getParticipants().contains(participantName)) {
                    artifact.getParticipants().add(participantName);
                }
            }
        }
    }

    private String resolveParticipantName(String participantId) {
        try {
            ProcessLoaderV2Plus.ArtifactLocation loc = loader.findArtifactLocation(participantId);
            if (loc != null && loc.objectInfo != null && loc.objectInfo.getName() != null) {
                return loc.objectInfo.getName();
            }
        } catch (Exception ignored) {}
        return null;
    }


    // JsonReportNavigatorV2
    private List<JsonReportV2.Condition> populateBpmnConditions(
            ExclusiveGateway gateway,
            Map<String, List<SequenceFlow>> flowMap) {

        List<JsonReportV2.Condition> conditions = new ArrayList<JsonReportV2.Condition>();
        List<SequenceFlow> outs = flowMap.get(gateway.getId());
        if (outs == null) return conditions;

        for (SequenceFlow sf : outs) {
            JsonReportV2.Condition c = new JsonReportV2.Condition();
            c.setTargetStepId(sf.getTargetRef());
            c.setName(sf.getName());
            if (sf.getConditionExpression() != null &&
                    sf.getConditionExpression().getExpression() != null) {
                c.setExpression(normalizeExpr(sf.getConditionExpression().getExpression()));
                c.setExpressionLanguage("TWX-Expr");
            } else {
                c.setExpression("true");
                c.setExpressionLanguage("TWX-Expr");
            }
            c.setDefault(sf.getId() != null && sf.getId().equals(gateway.getDefaultFlow()));
            conditions.add(c);
        }
        return conditions;
    }

    private void visitLegacyService(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process, JsonReportV2.Artifact artifact) {
        if (process == null) return;

        populateLegacyVariablesV2(process, artifact);

        List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item> items = process.getItems();
        List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link> links = process.getLinks();

        if (items == null || items.isEmpty()) {
            handleSingleStepLegacyService(process, artifact);
            return;
        }

        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item it : items) {
            JsonReportV2.Node node = new JsonReportV2.Node();
            node.setId(it.getProcessItemId());
            node.setType(it.getTWComponentName());
            node.setName(it.getName() != null ? it.getName() : it.getProcessItemId());
            node.setLane(null);
            artifact.getGraph().getNodes().add(node);
        }

        Map<String, String> endStateToExpression = new HashMap<String, String>();
        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item it : items) {
            if (it.getTwComponent() != null && it.getTwComponent().getSwitchConditions() != null) {
                for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.SwitchCondition sc : it.getTwComponent().getSwitchConditions()) {
                    if (sc.getEndStateId() != null) endStateToExpression.put(sc.getEndStateId(), sc.getCondition());
                }
            }
        }

        if (links != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link lk : links) {
                JsonReportV2.Edge edge = new JsonReportV2.Edge();
                edge.setId(lk.getProcessLinkId());
                edge.setSource(lk.getFromProcessItemId());
                edge.setTarget(lk.getToProcessItemId());
                edge.setLabel(lk.getName());
                artifact.getGraph().getEdges().add(edge);

                if (lk.getEndStateId() != null && endStateToExpression.containsKey(lk.getEndStateId())) {
                    JsonReportV2.EdgeCondition ec = new JsonReportV2.EdgeCondition();
                    ec.setEdgeId(lk.getProcessLinkId());
                    ec.setExpression(normalizeExpr(endStateToExpression.get(lk.getEndStateId())));
                    ec.setDefault("Default".equalsIgnoreCase(lk.getName()));
                    ec.setLanguage("TWX-Expr");
                    artifact.getGraph().getConditions().add(ec);
                }
            }
        }

        Set<String> allIds = new HashSet<String>();
        Set<String> withIncoming = new HashSet<String>();
        Set<String> withOutgoing = new HashSet<String>();
        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item it : items) {
            if (it.getProcessItemId() != null) allIds.add(it.getProcessItemId());
        }
        if (links != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link lk : links) {
                if (lk.getToProcessItemId() != null) withIncoming.add(lk.getToProcessItemId());
                if (lk.getFromProcessItemId() != null) withOutgoing.add(lk.getFromProcessItemId());
            }
        }
        List<String> entryPoints = new ArrayList<String>();
        List<String> endPoints = new ArrayList<String>();
        for (String id : allIds) {
            if (!withIncoming.contains(id)) entryPoints.add(id);
            if (!withOutgoing.contains(id)) endPoints.add(id);
        }
        artifact.getGraph().getEntryPoints().addAll(entryPoints);
        artifact.getGraph().getEndPoints().addAll(endPoints);

        List<JsonReportV2.FlowStep> orderedSteps = generateOrderedStepsForLegacyProcess(process);
        artifact.getFlow().addAll(orderedSteps);

        // <<< NOVO: completa variables com o que aparecer nos mappings dos passos legados (SubProcess etc.)
        augmentVariablesFromLegacyFlowMappings(artifact);
    }

    /** Complementa artifact.variables (input/output) com base nos mappings dos passos do flow (LEGADO). */
    private void augmentVariablesFromLegacyFlowMappings(JsonReportV2.Artifact artifact) {
        if (artifact == null || artifact.getFlow() == null) return;

        final String DEFAULT_TYPE = "String";

        for (JsonReportV2.FlowStep s : artifact.getFlow()) {
            JsonReportV2.ParameterMapping pm = s.getParameterMapping();
            if (pm == null) continue;

            for (JsonReportV2.Mapping m : pm.getInput()) {
                if (m.getSource() == null) continue;
                for (String cand : parseCandidateVariableNames(m.getSource())) {
                    ensureVariablePresent(artifact, cand, "input", DEFAULT_TYPE, false);
                }
            }
            for (JsonReportV2.Mapping m : pm.getOutput()) {
                String targetVar = m.getTarget();
                if (targetVar == null || targetVar.trim().isEmpty()) continue;
                String root = rootIdentifier(targetVar);
                ensureVariablePresent(artifact, root, "output", DEFAULT_TYPE, false);
            }
        }
    }


    // BPD legado (participantes)
    private void visitBpdLegado(Bpd bpd, JsonReportV2.Artifact artifact) {
        if (bpd == null || bpd.getBusinessProcessDiagram() == null) return;
        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();

        if (diagram.getPools() != null) {
            for (Pool pool : diagram.getPools()) {
                if (pool.getLanes() != null) {
                    for (Lane lane : pool.getLanes()) {
                        if (lane.getAttachedParticipant() != null && !lane.getAttachedParticipant().trim().isEmpty()) {
                            String participantName = resolveParticipantName(lane.getAttachedParticipant());
                            if (participantName != null && !artifact.getParticipants().contains(participantName)) {
                                artifact.getParticipants().add(participantName);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("[NAV-V2][INFO] BPD Legado '" + artifact.getName() + "': participantes extraídos.");
    }

    // TwClass -> businessObjects.definitions
    private void visitTwClass(TwClass twClass) {
        if (twClass == null) return;

        JsonReportV2.VariableInfo varInfo = new JsonReportV2.VariableInfo();
        varInfo.setTypeId(twClass.getId());
        varInfo.setName(twClass.getName());
        varInfo.setList(false);

        enrichVariable(varInfo, new HashSet<String>());
    }

    // CoachView -> UI report
    private void visitCoachView(CoachView cv) {
        if (cv == null) return;

        JsonReportV2.CoachView jsonCv = new JsonReportV2.CoachView();
        jsonCv.setId(cv.getId());
        jsonCv.setName(cv.getName());

        if (cv.getInlineScripts() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.InlineScript s : cv.getInlineScripts()) {
                JsonReportV2.InlineScript jsonScript = new JsonReportV2.InlineScript();
                jsonScript.setName(s.getName());
                jsonScript.setScriptType(s.getScriptType());
                jsonScript.setScriptBlock(s.getScriptBlock());
                jsonCv.getInlineScripts().add(jsonScript);
            }
        }
        generator.getReport().getUiReport().getCoachViews().add(jsonCv);
    }

    // ============================
    // Helpers de variáveis / tipos
    // ============================
    private void populateLegacyVariablesV2(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process,
                                           JsonReportV2.Artifact artifact) {
        if (process.getProcessParameters() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.ProcessParameter pp : process.getProcessParameters()) {
                JsonReportV2.VariableInfo vi = new JsonReportV2.VariableInfo();
                vi.setName(pp.getName());
                vi.setTypeId(pp.getClassId());
                vi.setList(pp.isArrayOf());
                enrichVariable(vi, new HashSet<String>());
                if (pp.getParameterType() == 1) {
                    artifact.getVariables().getInput().add(vi);
                } else {
                    artifact.getVariables().getOutput().add(vi);
                }
            }
        }
        if (process.getProcessVariables() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.ProcessVariable pv : process.getProcessVariables()) {
                JsonReportV2.VariableInfo vi = new JsonReportV2.VariableInfo();
                vi.setName(pv.getName());
                vi.setTypeId(pv.getClassId());
                vi.setList(pv.isArrayOf());
                enrichVariable(vi, new HashSet<String>());
                artifact.getVariables().getPrivite().add(vi); // usar "private"
            }
        }
    }

    // Mantém a assinatura original (compatível com seu código atual)
    private JsonReportV2.ParameterMapping populateLegacyParameterMappingV2(
            br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.TWComponent component) {

        JsonReportV2.ParameterMapping pm = new JsonReportV2.ParameterMapping();
        if (component != null && component.getParameterMapping() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.ParameterMapping m : component.getParameterMapping()) {
                JsonReportV2.Mapping map = new JsonReportV2.Mapping();
                map.setSource(m.getValue());
                map.setTarget(m.getName());
                if (m.isInput()) {
                    pm.getInput().add(map);   // fonte = variável do processo pai
                } else {
                    pm.getOutput().add(map);  // destino = variável do processo pai
                }
            }
        }
        return pm;
    }


    // ===== projeta variáveis do mapping legado para artifact.variables =====
    private void augmentVariablesFromLegacyComponentMapping(
            br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.TWComponent component,
            JsonReportV2.Artifact artifact,
            JsonReportV2.ParameterMapping pm) {

        if (artifact == null || pm == null) return;
        final String DEFAULT_TYPE = "String";

        // INPUTs: fonte (expressão do processo pai) -> variável "input"
        for (JsonReportV2.Mapping m : pm.getInput()) {
            if (m.getSource() == null) continue;
            String root = rootIdentifier(m.getSource());
            if (root != null && !root.isEmpty()) {
                ensureVariablePresent(artifact, root, "input", DEFAULT_TYPE, false);
            }
        }

        // OUTPUTs: destino (nome da var do pai) -> variável "output"
        for (JsonReportV2.Mapping m : pm.getOutput()) {
            if (m.getTarget() == null) continue;
            String root = rootIdentifier(m.getTarget());
            if (root != null && !root.isEmpty()) {
                ensureVariablePresent(artifact, root, "output", DEFAULT_TYPE, false);
            }
        }
    }



    // JsonReportNavigatorV2
    private List<JsonReportV2.Condition> populateLegacySwitchConditionsV2(
            br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item switchItem,
            List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link> allLinks) {

        List<JsonReportV2.Condition> conditions = new ArrayList<JsonReportV2.Condition>();
        if (switchItem == null || switchItem.getTwComponent() == null) return conditions;

        Map<String, String> condMap = new HashMap<String, String>();
        if (switchItem.getTwComponent().getSwitchConditions() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.SwitchCondition sc
                    : switchItem.getTwComponent().getSwitchConditions()) {
                if (sc.getEndStateId() != null) condMap.put(sc.getEndStateId(), sc.getCondition());
            }
        }

        if (allLinks != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link lk : allLinks) {
                if (switchItem.getProcessItemId().equals(lk.getFromProcessItemId())) {
                    JsonReportV2.Condition c = new JsonReportV2.Condition();
                    c.setTargetStepId(lk.getToProcessItemId());
                    c.setName(lk.getName());
                    c.setExpression(normalizeExpr(condMap.get(lk.getEndStateId())));
                    c.setExpressionLanguage("TWX-Expr");
                    c.setDefault("Default".equalsIgnoreCase(lk.getName()));
                    conditions.add(c);
                }
            }
        }
        return conditions;
    }

    private List<JsonReportV2.FlowStep> generateOrderedStepsForProcess(Process process) {
        List<JsonReportV2.FlowStep> result = new ArrayList<JsonReportV2.FlowStep>();
        if (process == null) return result;

        Map<String, FlowNode> nodeMap = createNodeMap(process.getFlowElements());
        Map<String, List<SequenceFlow>> flowMap = createSourceIdToFlowMap(process.getSequenceFlows());
        Map<String, String> nodeToLane = mapNodeToLaneNames(process);

        List<String> starts = new ArrayList<String>();
        for (FlowNode n : nodeMap.values()) if (n instanceof StartEvent) starts.add(n.getId());

        Set<String> visited = new HashSet<String>();
        List<FlowNode> ordered = new ArrayList<FlowNode>();
        for (String startId : starts) {
            Deque<String> st = new ArrayDeque<String>();
            st.push(startId);
            while (!st.isEmpty()) {
                String cur = st.pop();
                if (!visited.add(cur)) continue;
                FlowNode node = nodeMap.get(cur);
                if (node != null) {
                    ordered.add(node);
                    List<SequenceFlow> outs = flowMap.get(cur);
                    if (outs != null) {
                        for (int i = outs.size() - 1; i >= 0; i--) {
                            SequenceFlow f = outs.get(i);
                            if (f.getTargetRef() != null && !visited.contains(f.getTargetRef())) st.push(f.getTargetRef());
                        }
                    }
                }
            }
        }
        for (FlowNode n : nodeMap.values()) if (!visited.contains(n.getId())) ordered.add(n);

        int idx = 0;
        for (FlowNode node : ordered) {
            JsonReportV2.FlowStep step = new JsonReportV2.FlowStep();
            step.setStepId(node.getId());
            step.setName(node.getName());
            step.setType(getNodeType(node));
            step.setOrderIndex(idx++);
            step.setLane(nodeToLane.get(node.getId()));

            if (node.getIncoming() != null) step.getIncomingFlows().addAll(node.getIncoming());
            if (node.getOutgoing() != null) step.getOutgoingFlows().addAll(node.getOutgoing());

            if (node instanceof ScriptTask) {
                step.setScript(((ScriptTask) node).getScript());
            } else if (node instanceof CallActivity) {
                CallActivity ca = (CallActivity) node;
                step.setCalledArtifactId(ca.getCalledElement()); // principal
                step.setParameterMapping(populateBpmnParameterMapping(ca));
                setCalledArtifactIfMissing(step, ca);             // garantia
            } else if (node instanceof ExclusiveGateway) {
                step.getConditions().addAll(populateBpmnConditions((ExclusiveGateway) node, flowMap));
            }
            result.add(step);
        }
        return result;
    }


    // Mantém a assinatura original (compatível com seu código atual)
    private JsonReportV2.ParameterMapping populateBpmnParameterMapping(CallActivity callActivity) {
        JsonReportV2.ParameterMapping pm = new JsonReportV2.ParameterMapping();
        if (callActivity.getDataInputAssociations() != null) {
            for (DataInputAssociation dia : callActivity.getDataInputAssociations()) {
                if (dia.getAssignment() != null && dia.getAssignment().getFrom() != null) {
                    JsonReportV2.Mapping m = new JsonReportV2.Mapping();
                    m.setSource(dia.getAssignment().getFrom().getExpression());
                    m.setTarget(dia.getTargetRef()); // ID do DataInput do CallActivity
                    pm.getInput().add(m);
                }
            }
        }
        if (callActivity.getDataOutputAssociations() != null) {
            for (DataOutputAssociation doa : callActivity.getDataOutputAssociations()) {
                if (doa.getAssignment() != null && doa.getAssignment().getTo() != null) {
                    JsonReportV2.Mapping m = new JsonReportV2.Mapping();
                    m.setSource(doa.getSourceRef()); // ID do DataOutput do CallActivity
                    m.setTarget(doa.getAssignment().getTo().getContent()); // variável do processo pai
                    pm.getOutput().add(m);
                }
            }
        }
        return pm;
    }

    /** Complementa o artifact.variables a partir do mapeamento do CallActivity.
     *  Chame logo após setar step.setParameterMapping(pm) (vide instrução acima).
     */
    private void augmentVariablesFromBpmnCallActivity(CallActivity callActivity,
                                                      Process process,
                                                      JsonReportV2.Artifact artifact,
                                                      JsonReportV2.ParameterMapping pm) {
        if (pm == null) return;

        // Índices por ID de DataInput/DataOutput do próprio CallActivity (úteis p/ metadados; aqui focamos nas variáveis do processo pai)
     /*   Map<String, DataInputAssociation> diById  = new HashMap<String, DataInputAssociation>();
        Map<String, DataOutputAssociation> doById = new HashMap<String, DataOutputAssociation>();
        try {
            if (callActivity != null) {
                if (callActivity.getDataInputAssociations() != null) {
                    for (DataInputAssociation di : callActivity.getDataInputAssociations()) {
                        if (di.getTargetRef() != null) diById.put(di.getTargetRef(), di);
                    }
                }
                if (callActivity.getDataOutputAssociations() != null) {
                    for (DataOutputAssociation d : callActivity.getDataOutputAssociations()) {
                        if (d.getSourceRef() != null) doById.put(d.getSourceRef(), d);
                    }
                }
            }
        } catch (Throwable ignore) {}  */

        // Heurística p/ inferir tipo pelo IO do processo "pai"
        Map<String, String> typeByVarName = new HashMap<String, String>();
        if (process != null && process.getIoSpecification() != null) {
            IoSpecification pio = process.getIoSpecification();
            if (pio.getDataInputs() != null) {
                for (DataInput di : pio.getDataInputs()) {
                    if (di.getName() != null) typeByVarName.put(di.getName(), di.getItemSubjectRef());
                }
            }
            if (pio.getDataOutputs() != null) {
                for (DataOutput d : pio.getDataOutputs()) {
                    if (d.getName() != null) typeByVarName.put(d.getName(), d.getItemSubjectRef());
                }
            }
        }

        // Entradas do CallActivity: fonte = expressão do processo pai -> tratamos como variável "input" do artifact atual
        for (JsonReportV2.Mapping m : pm.getInput()) {
            if (m.getSource() == null) continue;
            for (String cand : parseCandidateVariableNames(m.getSource())) {
                String typeGuess = typeByVarName.containsKey(cand) ? typeByVarName.get(cand) : "String";
                ensureVariablePresent(artifact, cand, "input", typeGuess, false);
            }
        }

        // Saídas do CallActivity: destino = variável do processo pai (string literal no .to). -> tratamos como "output"
        for (JsonReportV2.Mapping m : pm.getOutput()) {
            String targetVar = m.getTarget();
            if (targetVar == null || targetVar.trim().isEmpty()) continue;
            String root = rootIdentifier(targetVar);
            String typeGuess = typeByVarName.containsKey(root) ? typeByVarName.get(root) : "String";
            ensureVariablePresent(artifact, root, "output", typeGuess, false);
        }
    }

    private List<JsonReportV2.FlowStep> generateOrderedStepsForLegacyProcess(
            br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process) {

        List<JsonReportV2.FlowStep> result = new ArrayList<JsonReportV2.FlowStep>();
        if (process == null) return result;

        List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item> items = process.getItems();
        List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link> links = process.getLinks();

        Map<String, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item> itemById = new HashMap<String, br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item>();
        if (items != null) for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item it : items)
            if (it.getProcessItemId() != null) itemById.put(it.getProcessItemId(), it);

        Map<String, List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link>> outBySource = new HashMap<String, List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link>>();
        Map<String, Integer> incomingCount = new HashMap<String, Integer>();

        if (links != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link lk : links) {
                String src = lk.getFromProcessItemId();
                String tgt = lk.getToProcessItemId();
                if (src != null) {
                    List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link> list = outBySource.get(src);
                    if (list == null) { list = new ArrayList<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link>(); outBySource.put(src, list); }
                    list.add(lk);
                }
                if (tgt != null) {
                    Integer c = incomingCount.get(tgt);
                    incomingCount.put(tgt, (c == null ? 1 : c + 1));
                }
            }
        }

        List<String> starts = new ArrayList<String>();
        for (String id : itemById.keySet()) {
            Integer c = incomingCount.get(id);
            if (c == null || c == 0) starts.add(id);
        }

        Set<String> visited = new HashSet<String>();
        List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item> ordered = new ArrayList<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item>();
        for (String start : starts) {
            Deque<String> stack = new ArrayDeque<String>();
            stack.push(start);
            while (!stack.isEmpty()) {
                String cur = stack.pop();
                if (!visited.add(cur)) continue;
                br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item current = itemById.get(cur);
                if (current != null) {
                    ordered.add(current);
                    List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link> outs = outBySource.get(cur);
                    if (outs != null) {
                        for (int i = outs.size() - 1; i >= 0; i--) {
                            br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link f = outs.get(i);
                            if (f.getToProcessItemId() != null && !visited.contains(f.getToProcessItemId())) stack.push(f.getToProcessItemId());
                        }
                    }
                }
            }
        }
        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item it : itemById.values())
            if (!visited.contains(it.getProcessItemId())) ordered.add(it);

        int idx = 0;
        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item it : ordered) {
            JsonReportV2.FlowStep step = new JsonReportV2.FlowStep();
            step.setStepId(it.getProcessItemId());
            step.setName(it.getName());
            step.setType(it.getTWComponentName());
            step.setOrderIndex(idx++);

            if (links != null) {
                for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link lk : links) {
                    if (it.getProcessItemId().equals(lk.getToProcessItemId())) step.getIncomingFlows().add(lk.getProcessLinkId());
                    if (it.getProcessItemId().equals(lk.getFromProcessItemId())) step.getOutgoingFlows().add(lk.getProcessLinkId());
                }
            }

            if (it.getTwComponent() != null) {
                step.setScript(it.getTwComponent().getScript());
                if ("SubProcess".equalsIgnoreCase(it.getTWComponentName())) {
                    step.setCalledArtifactId(it.getTwComponent().getAttachedProcessRef());
                    JsonReportV2.ParameterMapping pm = populateLegacyParameterMappingV2(it.getTwComponent());
                    step.setParameterMapping(pm);

                } else if ("Switch".equalsIgnoreCase(it.getTWComponentName())) {
                    step.getConditions().addAll(populateLegacySwitchConditionsV2(it, links));
                } else if ("CoachFlow".equalsIgnoreCase(it.getTWComponentName()) || "CoachNG".equalsIgnoreCase(it.getTWComponentName())) {
                    step.setCoachId("coachId_" + it.getProcessItemId());
                }
            }
            result.add(step);
        }
        return result;
    }


    // ============================
    // Utilitários de BPMN/Legacy
    // ============================
    private JsonReportV2.FlowStep cloneForRootView(JsonReportV2.FlowStep src) {
        JsonReportV2.FlowStep d = new JsonReportV2.FlowStep();
        d.setStepId(src.getStepId());
        d.setName(src.getName());
        d.setType(src.getType());
        d.getIncomingFlows().addAll(src.getIncomingFlows());
        d.getOutgoingFlows().addAll(src.getOutgoingFlows());
        // orderIndex será definido na rootView
        d.setCalledArtifactId(src.getCalledArtifactId());
        d.setScript(src.getScript());
        d.setLane(src.getLane());
        d.setCoachId(src.getCoachId());
        if (src.getParameterMapping() != null) {
            JsonReportV2.ParameterMapping pm = new JsonReportV2.ParameterMapping();
            pm.getInput().addAll(src.getParameterMapping().getInput());
            pm.getOutput().addAll(src.getParameterMapping().getOutput());
            d.setParameterMapping(pm);
        }
        if (src.getConditions() != null) {
            d.getConditions().addAll(src.getConditions());
        }
        return d;
    }

    private void handleSingleStepLegacyService(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process, JsonReportV2.Artifact artifact) {
        System.out.println("[NAV-V2] -> Serviço legado sem itens de fluxo. Mapeando como passo único.");
        JsonReportV2.FlowStep step = new JsonReportV2.FlowStep();
        step.setStepId("step_unico");
        step.setName(process.getName());
        step.setOrderIndex(0);

        if (process.getClobField1() != null && !process.getClobField1().trim().isEmpty()) {
            step.setScript(process.getClobField1());
            step.setType("ScriptTask");
        } else if (process.getXmlData() != null && process.getXmlData().contains("<externalServiceRef>")) {
            step.setName(process.getName() + " - Chamada de Serviço");
            step.setType("ServiceTask");
        } else {
            step.setType("UncategorizedTask");
        }

        artifact.getFlow().add(step);
        JsonReportV2.Node node = new JsonReportV2.Node();
        node.setId(step.getStepId());
        node.setType(step.getType());
        node.setName(step.getName());
        node.setLane(null);
        artifact.getGraph().getNodes().add(node);
        artifact.getGraph().getEntryPoints().add(step.getStepId());
        artifact.getGraph().getEndPoints().add(step.getStepId());
    }

    private void enrichVariable(JsonReportV2.VariableInfo varInfo, Set<String> visitedTypes) {
        if (varInfo == null || varInfo.getTypeId() == null || varInfo.getTypeId().trim().isEmpty()) return;

        String originalTypeId = varInfo.getTypeId();
        String cleanTypeId = loader.getCleanId(originalTypeId);
        if (cleanTypeId == null || !visitedTypes.add(cleanTypeId)) return;

        loader.loadDependentArtifactIfNotExists(originalTypeId);
        Object artifact = loader.getArtefatoDoCache(cleanTypeId);

        if (artifact instanceof Teamworks) {
            Teamworks tw = (Teamworks) artifact;
            if (tw.getTwClass() != null) {
                TwClass twClass = tw.getTwClass();

                // primitivo com nome
                if (twClass.getName() != null && PRIMITIVE_TYPES.contains(twClass.getName())) {
                    varInfo.setTypeId(twClass.getName());
                    return;
                }

                JsonReportV2.Definition def = new JsonReportV2.Definition();
                def.setTypeId(originalTypeId);
                def.setTypeName(twClass.getName());

                if (twClass.getDefinition() != null && twClass.getDefinition().getProperties() != null) {
                    for (Property p : twClass.getDefinition().getProperties()) {
                        JsonReportV2.PropertyStructure ps = new JsonReportV2.PropertyStructure();
                        ps.setName(p.getName());
                        ps.setList(p.isArrayProperty());

                        JsonReportV2.VariableInfo tmp = new JsonReportV2.VariableInfo();
                        tmp.setTypeId(p.getClassRef());
                        enrichVariable(tmp, new HashSet<String>(visitedTypes));
                        ps.setTypeRef(tmp.getTypeId());

                        def.getStructure().add(ps);
                    }
                }

                String canonicalId = generator.addBusinessObjectDefinition(def);
                varInfo.setTypeId(canonicalId);
            }
        }
    }

    private Map<String, FlowNode> createNodeMap(List<Object> elements) {
        Map<String, FlowNode> map = new LinkedHashMap<String, FlowNode>();
        if (elements == null) return map;
        for (Object el : elements) {
            if (el instanceof FlowNode) {
                FlowNode fn = (FlowNode) el;
                if (fn.getId() != null && !map.containsKey(fn.getId())) {
                    map.put(fn.getId(), fn);
                }
            }
        }
        return map;
    }

    private Map<String, List<SequenceFlow>> createSourceIdToFlowMap(List<SequenceFlow> flows) {
        Map<String, List<SequenceFlow>> map = new HashMap<String, List<SequenceFlow>>();
        if (flows == null) return map;
        for (SequenceFlow f : flows) {
            if (f.getSourceRef() == null) continue;
            List<SequenceFlow> list = map.get(f.getSourceRef());
            if (list == null) { list = new ArrayList<SequenceFlow>(); map.put(f.getSourceRef(), list); }
            list.add(f);
        }
        return map;
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
        return "Elemento";
    }

    private Definitions findDefinitionsByArtifactId(String artifactId) {
        String clean = loader.getCleanId(artifactId);
        Object obj = loader.getArtefatoDoCache(clean);
        if (obj instanceof Definitions) return (Definitions) obj;
        return null;
    }

    private br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process findLegacyProcessByArtifactId(String artifactId) {
        String clean = loader.getCleanId(artifactId);
        Object obj = loader.getArtefatoDoCache(clean);
        if (obj instanceof Teamworks) {
            Teamworks tw = (Teamworks) obj;
            if (tw.getProcess() != null) return tw.getProcess();
        }
        return null;
    }

    private String normalizeExpr(String raw) {
        if (raw == null) return "true";
        return raw.replaceAll("\\s+", " ").trim();
    }
    // JsonReportNavigatorV2
    // ===== lanes -> nodeId => participantName =====
    private Map<String, String> mapNodeToLaneNames(Process process) {
        Map<String, String> byNode = new HashMap<String, String>();
        if (process == null || process.getLaneSet() == null || process.getLaneSet().getLanes() == null) {
            return byNode;
        }

        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Lane lane : process.getLaneSet().getLanes()) {
            // Resolve nome do participante (preferir o partitionElementRef)
            String participantName = null;
            if (lane.getPartitionElementRef() != null && !lane.getPartitionElementRef().trim().isEmpty()) {
                participantName = resolveParticipantName(lane.getPartitionElementRef());
            }
            if (participantName == null || participantName.trim().isEmpty()) {
                // fallback para o nome da própria lane (se existir)
                try {
                    java.lang.reflect.Method m = lane.getClass().getMethod("getName");
                    Object v = m.invoke(lane);
                    if (v != null) participantName = String.valueOf(v);
                } catch (Throwable ignore) { /* ok */ }
            }

            // Associa os nós desta lane ao participante
            boolean mappedAny = false;
            // Tentativas comuns de API para listar refs de nós
            String[] candMethods = new String[] {
                    "getFlowNodeRefs", "getFlowNodeRefIds", "getFlowNodes", "getNodeRefs"
            };
            for (String mn : candMethods) {
                try {
                    java.lang.reflect.Method mRefs = lane.getClass().getMethod(mn);
                    Object refs = mRefs.invoke(lane);
                    if (refs instanceof List) {
                        for (Object ref : (List) refs) {
                            if (ref == null) continue;
                            String nodeId;
                            if (ref instanceof String) {
                                nodeId = (String) ref;
                            } else {
                                // pode ser objeto com getId()
                                try {
                                    java.lang.reflect.Method mid = ref.getClass().getMethod("getId");
                                    Object idv = mid.invoke(ref);
                                    nodeId = (idv != null ? String.valueOf(idv) : null);
                                } catch (Throwable t) {
                                    nodeId = String.valueOf(ref);
                                }
                            }
                            if (nodeId != null) {
                                byNode.put(nodeId, participantName);
                                mappedAny = true;
                            }
                        }
                    }
                } catch (Throwable ignore) { /* tenta próximo nome de método */ }
                if (mappedAny) break;
            }
        }
        return byNode;
    }


    /** Extrai possíveis nomes de variáveis de uma expressão simples (heurístico). */
    private List<String> parseCandidateVariableNames(String expr) {
        List<String> out = new ArrayList<String>();
        if (expr == null) return out;

        // remove literais de string
        String s = expr.replaceAll("\"[^\"]*\"", " ").replaceAll("'[^']*'", " ");
        // pega identificadores "raiz": foo, bar2, pedido, tw, etc.
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\\b([A-Za-z_][A-Za-z0-9_]*)\\b")
                .matcher(s);
        Set<String> seen = new HashSet<String>();
        Set<String> stop = new HashSet<String>(Arrays.asList(
                "true","false","null","and","or","not","if","then","else","return","var","let","function"
        ));
        while (m.find()) {
            String id = m.group(1);
            if (!stop.contains(id) && seen.add(id)) out.add(id);
        }
        return out;
    }

    private String rootIdentifier(String nameOrPath) {
        if (nameOrPath == null) return null;
        String s = nameOrPath.trim();
        int dot = s.indexOf('.');
        if (dot > 0) s = s.substring(0, dot);
        int br = s.indexOf('[');
        if (br > 0) s = s.substring(0, br);
        // remover ${...} ou tw(...) comuns em expressões
        if (s.startsWith("${") && s.endsWith("}")) s = s.substring(2, s.length() - 1);
        return s;
    }

    // ===== ensureVariablePresent =====
// ===== ensureVariablePresent =====
    private void ensureVariablePresent(JsonReportV2.Artifact artifact,
                                       String varName,
                                       String bucket,         // "input" | "output" | "private"
                                       String typeIdGuess,    // ex.: "String"
                                       boolean isList) {
        if (artifact == null || varName == null || varName.trim().isEmpty()) return;

        // checa nas listas existentes
        if (hasVariable(artifact.getVariables().getInput(), varName)
                || hasVariable(artifact.getVariables().getOutput(), varName)
                || hasVariable(safeGetPrivateList(artifact), varName)) {
            return;
        }

        JsonReportV2.VariableInfo v = new JsonReportV2.VariableInfo();
        v.setName(varName);
        v.setTypeId(typeIdGuess == null ? "String" : typeIdGuess);
        v.setList(isList);

        try { enrichVariable(v, new HashSet<String>()); } catch (Throwable ignore) {}

        if ("input".equalsIgnoreCase(bucket)) {
            artifact.getVariables().getInput().add(v);
        } else if ("output".equalsIgnoreCase(bucket)) {
            artifact.getVariables().getOutput().add(v);
        } else {
            // adiciona na lista "private" que existir (getPrivate ou getPrivite)
            List<JsonReportV2.VariableInfo> priv = safeGetPrivateList(artifact);
            if (priv != null) {
                priv.add(v);
            } else {
                // fallback mais conservador
                artifact.getVariables().getOutput().add(v);
            }
        }
    }



    // tenta pegar a lista "private" por getPrivate() OU getPrivite()
    @SuppressWarnings("unchecked")
    private List<JsonReportV2.VariableInfo> safeGetPrivateList(JsonReportV2.Artifact artifact) {
        try {
            java.lang.reflect.Method m = artifact.getVariables().getClass().getMethod("getPrivate");
            Object v = m.invoke(artifact.getVariables());
            if (v instanceof List) return (List<JsonReportV2.VariableInfo>) v;
        } catch (Throwable ignore) { /* tenta getPrivite */ }
        try {
            java.lang.reflect.Method m = artifact.getVariables().getClass().getMethod("getPrivite");
            Object v = m.invoke(artifact.getVariables());
            if (v instanceof List) return (List<JsonReportV2.VariableInfo>) v;
        } catch (Throwable ignore) { }
        return null;
    }

    private boolean hasVariable(List<JsonReportV2.VariableInfo> list, String name) {
        if (list == null || name == null) return false;
        for (JsonReportV2.VariableInfo vi : list) {
            if (vi != null && name.equals(vi.getName())) return true;
        }
        return false;
    }



    private void setCalledArtifactIfMissing(JsonReportV2.FlowStep step, CallActivity ca) {
        if (step.getCalledArtifactId() != null && !step.getCalledArtifactId().trim().isEmpty()) return;
        String called = null;
        try { called = ca.getCalledElement(); } catch (Throwable ignore) {}
        if (called != null && !called.trim().isEmpty()) {
            step.setCalledArtifactId(called);
        }
    }


}
