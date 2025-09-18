package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.services;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportGeneratorV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.JsonReportV2;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link;

import java.util.*;

/**
 * Processes legacy Teamworks service artifacts.
 */
public class TeamworksProcessorService {
    private final JsonReportGeneratorV2 generator;
    private final ProcessLoaderV2Plus loader;
    private final VariableEnricherService variableEnricher;

    public TeamworksProcessorService(JsonReportGeneratorV2 generator, ProcessLoaderV2Plus loader,
                                     VariableEnricherService variableEnricher) {
        this.generator = generator;
        this.loader = loader;
        this.variableEnricher = variableEnricher;
    }

    public void processLegacyService(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process,
                                     JsonReportV2.Artifact artifact) {

        if (process == null) return;
        // Process variables
        populateLegacyVariables(process, artifact);

        // Build graph structure
        if (process.getItems() != null && !process.getItems().isEmpty()) {
            buildLegacyGraphStructure(process, artifact);
            generateLegacyFlowSteps(process, artifact);
        } else {
            handleSingleStepService(process, artifact);
        }

        // Enrich variables
        variableEnricher.enrichAllVariablesInArtifact(artifact);
    }

    private void populateLegacyVariables(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process,
                                         JsonReportV2.Artifact artifact) {
        if (process.getProcessParameters() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.ProcessParameter pp : process.getProcessParameters()) {
                JsonReportV2.VariableInfo vi = new JsonReportV2.VariableInfo();
                vi.setName(pp.getName());
                vi.setTypeId(pp.getClassId());
                vi.setList(pp.isArrayOf());

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

                // Add to private variables (try both method names)
                try {
                    List<JsonReportV2.VariableInfo> privateVars =
                            (List<JsonReportV2.VariableInfo>) artifact.getVariables().getClass()
                                    .getMethod("getPrivate").invoke(artifact.getVariables());
                    privateVars.add(vi);
                } catch (Exception e) {
                    try {
                        List<JsonReportV2.VariableInfo> privateVars =
                                (List<JsonReportV2.VariableInfo>) artifact.getVariables().getClass()
                                        .getMethod("getPrivite").invoke(artifact.getVariables());
                        privateVars.add(vi);
                    } catch (Exception e2) {
                        // Fallback to output
                        artifact.getVariables().getOutput().add(vi);
                    }
                }
            }
        }
    }

    private void buildLegacyGraphStructure(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process,
                                           JsonReportV2.Artifact artifact) {
        Map<String, Item> itemMap = new HashMap<>();
        for (Item item : process.getItems()) {
            itemMap.put(item.getProcessItemId(), item);

            // Add node
            JsonReportV2.Node node = new JsonReportV2.Node();
            node.setId(item.getProcessItemId());
            node.setType(item.getTWComponentName());
            node.setName(item.getName() != null ? item.getName() : item.getProcessItemId());
            artifact.getGraph().getNodes().add(node);
        }

        // Add edges and conditions
        if (process.getLinks() != null) {
            Map<String, String> endStateToExpression = createEndStateExpressionMap(process.getItems());

            for (Link link : process.getLinks()) {
                JsonReportV2.Edge edge = new JsonReportV2.Edge();
                edge.setId(link.getProcessLinkId());
                edge.setSource(link.getFromProcessItemId());
                edge.setTarget(link.getToProcessItemId());
                edge.setLabel(link.getName());
                artifact.getGraph().getEdges().add(edge);

                // Add condition if present
                if (link.getEndStateId() != null && endStateToExpression.containsKey(link.getEndStateId())) {
                    JsonReportV2.EdgeCondition ec = new JsonReportV2.EdgeCondition();
                    ec.setEdgeId(link.getProcessLinkId());
                    ec.setExpression(normalizeExpression(endStateToExpression.get(link.getEndStateId())));
                    ec.setDefault("Default".equalsIgnoreCase(link.getName()));
                    ec.setLanguage("TWX-Expr");
                    artifact.getGraph().getConditions().add(ec);
                }
            }
        }

        // Set entry and exit points
        Set<String> hasIncoming = new HashSet<>();
        Set<String> hasOutgoing = new HashSet<>();

        if (process.getLinks() != null) {
            for (Link link : process.getLinks()) {
                hasIncoming.add(link.getToProcessItemId());
                hasOutgoing.add(link.getFromProcessItemId());
            }
        }

        for (String itemId : itemMap.keySet()) {
            if (!hasIncoming.contains(itemId)) {
                artifact.getGraph().getEntryPoints().add(itemId);
            }
            if (!hasOutgoing.contains(itemId)) {
                artifact.getGraph().getEndPoints().add(itemId);
            }
        }
    }

    private void generateLegacyFlowSteps(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process,
                                         JsonReportV2.Artifact artifact) {
        // Create ordered list of items
        List<Item> orderedItems = createOrderedItemList(process);

        int orderIndex = 0;
        for (Item item : orderedItems) {
            JsonReportV2.FlowStep step = new JsonReportV2.FlowStep();
            step.setStepId(item.getProcessItemId());
            step.setName(item.getName());
            step.setType(item.getTWComponentName());
            step.setOrderIndex(orderIndex++);

            // Set incoming/outgoing flows
            if (process.getLinks() != null) {
                for (Link link : process.getLinks()) {
                    if (item.getProcessItemId().equals(link.getToProcessItemId())) {
                        step.getIncomingFlows().add(link.getProcessLinkId());
                    }
                    if (item.getProcessItemId().equals(link.getFromProcessItemId())) {
                        step.getOutgoingFlows().add(link.getProcessLinkId());
                    }
                }
            }

            // Handle specific item types
            if (item.getTwComponent() != null) {
                step.setScript(item.getTwComponent().getScript());

                if ("SubProcess".equalsIgnoreCase(item.getTWComponentName())) {
                    step.setCalledArtifactId(item.getTwComponent().getAttachedProcessRef());
                    step.setParameterMapping(createLegacyParameterMapping(item.getTwComponent()));
                } else if ("Switch".equalsIgnoreCase(item.getTWComponentName())) {
                    step.getConditions().addAll(createSwitchConditions(item, process.getLinks()));
                } else if ("CoachFlow".equalsIgnoreCase(item.getTWComponentName()) ||
                        "CoachNG".equalsIgnoreCase(item.getTWComponentName())) {
                    step.setCoachId("coachId_" + item.getProcessItemId());
                }
            }

            artifact.getFlow().add(step);
        }
    }

    private void handleSingleStepService(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process,
                                         JsonReportV2.Artifact artifact) {
        JsonReportV2.FlowStep step = new JsonReportV2.FlowStep();
        step.setStepId("single_step");
        step.setName(process.getName());
        step.setOrderIndex(0);

        if (process.getClobField1() != null && !process.getClobField1().trim().isEmpty()) {
            step.setScript(process.getClobField1());
            step.setType("ScriptTask");
        } else {
            step.setType("ServiceTask");
        }

        artifact.getFlow().add(step);

        // Add corresponding graph elements
        JsonReportV2.Node node = new JsonReportV2.Node();
        node.setId(step.getStepId());
        node.setType(step.getType());
        node.setName(step.getName());
        artifact.getGraph().getNodes().add(node);
        artifact.getGraph().getEntryPoints().add(step.getStepId());
        artifact.getGraph().getEndPoints().add(step.getStepId());
    }

    // Helper methods for legacy processing
    private Map<String, String> createEndStateExpressionMap(List<Item> items) {
        Map<String, String> map = new HashMap<>();
        for (Item item : items) {
            if (item.getTwComponent() != null && item.getTwComponent().getSwitchConditions() != null) {
                for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.SwitchCondition sc :
                        item.getTwComponent().getSwitchConditions()) {
                    if (sc.getEndStateId() != null) {
                        map.put(sc.getEndStateId(), sc.getCondition());
                    }
                }
            }
        }
        return map;
    }

    private List<Item> createOrderedItemList(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process) {
        Map<String, Item> itemMap = new HashMap<>();
        Map<String, List<String>> outgoingMap = new HashMap<>();
        Set<String> hasIncoming = new HashSet<>();

        // Build maps
        for (Item item : process.getItems()) {
            itemMap.put(item.getProcessItemId(), item);
        }

        if (process.getLinks() != null) {
            for (Link link : process.getLinks()) {
                outgoingMap.computeIfAbsent(link.getFromProcessItemId(), k -> new ArrayList<>())
                        .add(link.getToProcessItemId());
                hasIncoming.add(link.getToProcessItemId());
            }
        }

        // Find start items
        List<String> startItems = new ArrayList<>();
        for (String itemId : itemMap.keySet()) {
            if (!hasIncoming.contains(itemId)) {
                startItems.add(itemId);
            }
        }

        // DFS traversal
        List<Item> orderedItems = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String startId : startItems) {
            legacyDfsTraversal(startId, itemMap, outgoingMap, visited, orderedItems);
        }

        // Add unvisited items
        for (Item item : itemMap.values()) {
            if (!visited.contains(item.getProcessItemId())) {
                orderedItems.add(item);
            }
        }

        return orderedItems;
    }

    private void legacyDfsTraversal(String itemId, Map<String, Item> itemMap,
                                    Map<String, List<String>> outgoingMap,
                                    Set<String> visited, List<Item> orderedItems) {
        if (!visited.add(itemId)) return;

        Item item = itemMap.get(itemId);
        if (item != null) {
            orderedItems.add(item);

            List<String> outgoing = outgoingMap.get(itemId);
            if (outgoing != null) {
                for (String nextId : outgoing) {
                    legacyDfsTraversal(nextId, itemMap, outgoingMap, visited, orderedItems);
                }
            }
        }
    }

    private JsonReportV2.ParameterMapping createLegacyParameterMapping(
            br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.TWComponent component) {
        JsonReportV2.ParameterMapping pm = new JsonReportV2.ParameterMapping();

        if (component.getParameterMapping() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.ParameterMapping m :
                    component.getParameterMapping()) {
                JsonReportV2.Mapping mapping = new JsonReportV2.Mapping();
                mapping.setSource(m.getValue());
                mapping.setTarget(m.getName());

                if (m.isInput()) {
                    pm.getInput().add(mapping);
                } else {
                    pm.getOutput().add(mapping);
                }
            }
        }

        return pm;
    }

    private List<JsonReportV2.Condition> createSwitchConditions(Item switchItem, List<Link> allLinks) {
        List<JsonReportV2.Condition> conditions = new ArrayList<>();

        if (switchItem.getTwComponent() == null) return conditions;

        Map<String, String> conditionMap = new HashMap<>();
        if (switchItem.getTwComponent().getSwitchConditions() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.SwitchCondition sc :
                    switchItem.getTwComponent().getSwitchConditions()) {
                if (sc.getEndStateId() != null) {
                    conditionMap.put(sc.getEndStateId(), sc.getCondition());
                }
            }
        }

        if (allLinks != null) {
            for (Link link : allLinks) {
                if (switchItem.getProcessItemId().equals(link.getFromProcessItemId())) {
                    JsonReportV2.Condition condition = new JsonReportV2.Condition();
                    condition.setTargetStepId(link.getToProcessItemId());
                    condition.setName(link.getName());
                    condition.setExpression(normalizeExpression(conditionMap.get(link.getEndStateId())));
                    condition.setExpressionLanguage("TWX-Expr");
                    condition.setDefault("Default".equalsIgnoreCase(link.getName()));
                    conditions.add(condition);
                }
            }
        }

        return conditions;
    }

    private String normalizeExpression(String expr) {
        return expr != null ? expr.replaceAll("\\s+", " ").trim() : "true";
    }
}
