package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonReportV2 {

    private Metadata metadata = new Metadata();
    private List<Artifact> artifacts = new ArrayList<>();
    private BusinessObjects businessObjects = new BusinessObjects();
    private List<ExecutionPath> executionPaths = new ArrayList<>();
    private UiReport uiReport = new UiReport();
    private List<EnvironmentVariableUsage> environmentVariablesUsed = new ArrayList<>();
    private List<ResourceBundleUsage> resourceBundlesUsed = new ArrayList<>();

    // Getters e Setters
    public Metadata getMetadata() { return metadata; }
    public void setMetadata(Metadata metadata) { this.metadata = metadata; }
    public List<Artifact> getArtifacts() { return artifacts; }
    public void setArtifacts(List<Artifact> artifacts) { this.artifacts = artifacts; }
    public BusinessObjects getBusinessObjects() { return businessObjects; }
    public void setBusinessObjects(BusinessObjects businessObjects) { this.businessObjects = businessObjects; }
    public List<ExecutionPath> getExecutionPaths() { return executionPaths; }
    public void setExecutionPaths(List<ExecutionPath> executionPaths) { this.executionPaths = executionPaths; }
    public UiReport getUiReport() { return uiReport; }
    public void setUiReport(UiReport uiReport) { this.uiReport = uiReport; }
    public List<EnvironmentVariableUsage> getEnvironmentVariablesUsed() { return environmentVariablesUsed;}
    public void setEnvironmentVariablesUsed(List<EnvironmentVariableUsage> environmentVariablesUsed) { this.environmentVariablesUsed = environmentVariablesUsed; }
    public List<ResourceBundleUsage> getResourceBundlesUsed() { return resourceBundlesUsed; }
    public void setResourceBundlesUsed(List<ResourceBundleUsage> resourceBundlesUsed) { this.resourceBundlesUsed = resourceBundlesUsed; }


    public static class Metadata {
        private String projectName;
        private String twxFile;
        private String exportTimestamp;
        private String expressionLanguage;

        // Getters e Setters
        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }
        public String getTwxFile() { return twxFile; }
        public void setTwxFile(String twxFile) { this.twxFile = twxFile; }
        public String getExportTimestamp() { return exportTimestamp; }
        public void setExportTimestamp(String exportTimestamp) { this.exportTimestamp = exportTimestamp; }
        public String getExpressionLanguage() { return expressionLanguage; }
        public void setExpressionLanguage(String expressionLanguage) { this.expressionLanguage = expressionLanguage; }
    }

    public static class Artifact {
        private String id;
        private String name;
        private String type;
        private String filePath;
        private List<String> participants = new ArrayList<>();
        private Variables variables = new Variables();
        private List<FlowStep> flow = new ArrayList<>();
        // --- CORREÇÃO APLICADA AQUI ---
        private List<FlowStep> rootView = new ArrayList<>(); // Alterado de List<RootView> para List<FlowStep>
        private String parentStepId;
        private Graph graph = new Graph();

        // Getters e Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        public List<String> getParticipants() { return participants; }
        public void setParticipants(List<String> participants) { this.participants = participants; }
        public Variables getVariables() { return variables; }
        public void setVariables(Variables variables) { this.variables = variables; }
        public List<FlowStep> getFlow() { return flow; }
        public void setFlow(List<FlowStep> flow) { this.flow = flow; }

        // --- CORREÇÃO APLICADA AQUI ---
        public List<FlowStep> getRootView() { return rootView; }
        public void setRootView(List<FlowStep> rootView) { this.rootView = rootView; }

        public Graph getGraph() { return graph; }
        public void setGraph(Graph graph) { this.graph = graph; }
        public String getParentStepId() { return parentStepId; }
        public void setParentStepId(String parentStepId) { this.parentStepId = parentStepId; }
    }

    public static class Variables {
        private List<VariableInfo> input = new ArrayList<>();
        private List<VariableInfo> output = new ArrayList<>();
        @SerializedName("private")
        private List<VariableInfo> privite = new ArrayList<>();

        // Getters e Setters
        public List<VariableInfo> getInput() { return input; }
        public void setInput(List<VariableInfo> input) { this.input = input; }
        public List<VariableInfo> getOutput() { return output; }
        public void setOutput(List<VariableInfo> output) { this.output = output; }
        public List<VariableInfo> getPrivite() { return privite; }
        public void setPrivite(List<VariableInfo> privite) { this.privite = privite; }
    }

    public static class VariableInfo {
        private String name;
        private String typeId;
        private boolean list;

        // Getters e Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTypeId() { return typeId; }
        public void setTypeId(String typeId) { this.typeId = typeId; }
        public boolean isList() { return list; }
        public void setList(boolean list) { this.list = list; }
    }

    public static class FlowStep {
        private String stepId;
        private String name;
        private String type;
        private List<String> incomingFlows = new ArrayList<>();
        private List<String> outgoingFlows = new ArrayList<>();
        private Integer orderIndex;
        private String calledArtifactId;
        private ParameterMapping parameterMapping = new ParameterMapping();
        private String script;
        private List<Condition> conditions = new ArrayList<>();
        private String lane;
        private String coachId;
        private String parentStepId;
        private Integer subflowDepth;

        // Getters e Setters
        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public List<String> getIncomingFlows() { return incomingFlows; }
        public void setIncomingFlows(List<String> incomingFlows) { this.incomingFlows = incomingFlows; }
        public List<String> getOutgoingFlows() { return outgoingFlows; }
        public void setOutgoingFlows(List<String> outgoingFlows) { this.outgoingFlows = outgoingFlows; }
        public Integer getOrderIndex() { return orderIndex; }
        public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
        public String getCalledArtifactId() { return calledArtifactId; }
        public void setCalledArtifactId(String calledArtifactId) { this.calledArtifactId = calledArtifactId; }
        public ParameterMapping getParameterMapping() { return parameterMapping; }
        public void setParameterMapping(ParameterMapping parameterMapping) { this.parameterMapping = parameterMapping; }
        public String getScript() { return script; }
        public void setScript(String script) { this.script = script; }
        public List<Condition> getConditions() { return conditions; }
        public void setConditions(List<Condition> conditions) { this.conditions = conditions; }
        public String getLane() { return lane; }
        public void setLane(String lane) { this.lane = lane; }
        public String getCoachId() { return coachId; }
        public void setCoachId(String coachId) { this.coachId = coachId; }
        public String getParentStepId() { return parentStepId; }
        public void setParentStepId(String parentStepId) { this.parentStepId = parentStepId; }
        public Integer getSubflowDepth() { return subflowDepth; }
        public void setSubflowDepth(Integer subflowDepth) { this.subflowDepth = subflowDepth; }
    }

    public static class ParameterMapping {
        private List<Mapping> input = new ArrayList<>();
        private List<Mapping> output = new ArrayList<>();

        // Getters e Setters
        public List<Mapping> getInput() { return input; }
        public void setInput(List<Mapping> input) { this.input = input; }
        public List<Mapping> getOutput() { return output; }
        public void setOutput(List<Mapping> output) { this.output = output; }
    }

    public static class Mapping {
        private String source;
        private String target;

        // Getters e Setters
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
    }

    public static class Condition {
        private String targetStepId;
        private String name;
        private String expression;
        private boolean isDefault;
        private String expressionLanguage;

        // Getters e Setters
        public String getTargetStepId() { return targetStepId; }
        public void setTargetStepId(String targetStepId) { this.targetStepId = targetStepId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }
        public boolean isDefault() { return isDefault; }
        public void setDefault(boolean aDefault) { isDefault = aDefault; }
        public String getExpressionLanguage() { return expressionLanguage; }
        public void setExpressionLanguage(String expressionLanguage) { this.expressionLanguage = expressionLanguage; }
    }

    public static class Graph {
        private List<Node> nodes = new ArrayList<>();
        private List<Edge> edges = new ArrayList<>();
        private List<Gateway> gateways = new ArrayList<>();
        private List<EdgeCondition> conditions = new ArrayList<>();
        private List<String> entryPoints = new ArrayList<>();
        private List<String> endPoints = new ArrayList<>();

        // Getters e Setters
        public List<Node> getNodes() { return nodes; }
        public void setNodes(List<Node> nodes) { this.nodes = nodes; }
        public List<Edge> getEdges() { return edges; }
        public void setEdges(List<Edge> edges) { this.edges = edges; }
        public List<Gateway> getGateways() { return gateways; }
        public void setGateways(List<Gateway> gateways) { this.gateways = gateways; }
        public List<EdgeCondition> getConditions() { return conditions; }
        public void setConditions(List<EdgeCondition> conditions) { this.conditions = conditions; }
        public List<String> getEntryPoints() { return entryPoints; }
        public void setEntryPoints(List<String> entryPoints) { this.entryPoints = entryPoints; }
        public List<String> getEndPoints() { return endPoints; }
        public void setEndPoints(List<String> endPoints) { this.endPoints = endPoints; }
    }

    public static class Node {
        private String id;
        private String type;
        private String lane;
        private String name;

        // Getters e Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getLane() { return lane; }
        public void setLane(String lane) { this.lane = lane; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class Edge {
        private String id;
        private String source;
        private String target;
        private String label;

        // Getters e Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
    }

    public static class Gateway {
        private String id;
        private String type;
        private String defaultFlow;

        // Getters e Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDefaultFlow() { return defaultFlow; }
        public void setDefaultFlow(String defaultFlow) { this.defaultFlow = defaultFlow; }
    }

    public static class EdgeCondition {
        private String edgeId;
        private String expression;
        private boolean isDefault;
        private String language;

        // Getters e Setters
        public String getEdgeId() { return edgeId; }
        public void setEdgeId(String edgeId) { this.edgeId = edgeId; }
        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }
        public boolean isDefault() { return isDefault; }
        public void setDefault(boolean aDefault) { isDefault = aDefault; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
    }

    public static class BusinessObjects {
        private List<Definition> definitions = new ArrayList<>();

        // Getters e Setters
        public List<Definition> getDefinitions() { return definitions; }
        public void setDefinitions(List<Definition> definitions) { this.definitions = definitions; }
    }

    public static class Definition {
        private String typeId;
        private String typeName;
        private List<PropertyStructure> structure = new ArrayList<>();

        // Getters e Setters
        public String getTypeId() { return typeId; }
        public void setTypeId(String typeId) { this.typeId = typeId; }
        public String getTypeName() { return typeName; }
        public void setTypeName(String typeName) { this.typeName = typeName; }
        public List<PropertyStructure> getStructure() { return structure; }
        public void setStructure(List<PropertyStructure> structure) { this.structure = structure; }
    }

    public static class PropertyStructure {
        private String name;
        private String typeRef;
        private boolean list;

        // Getters e Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTypeRef() { return typeRef; }
        public void setTypeRef(String typeRef) { this.typeRef = typeRef; }
        public boolean isList() { return list; }
        public void setList(boolean list) { this.list = list; }
    }

    public static class ExecutionPath {
        private String artifactId;
        private String pathId;
        private List<String> steps = new ArrayList<>();
        private List<String> conditions = new ArrayList<>();
        private List<String> stepLabels = new ArrayList<>();

        // Getters e Setters
        public String getArtifactId() { return artifactId; }
        public void setArtifactId(String artifactId) { this.artifactId = artifactId; }
        public String getPathId() { return pathId; }
        public void setPathId(String pathId) { this.pathId = pathId; }
        public List<String> getSteps() { return steps; }
        public void setSteps(List<String> steps) { this.steps = steps; }
        public List<String> getConditions() { return conditions; }
        public void setConditions(List<String> conditions) { this.conditions = conditions; }
        public List<String> getStepLabels() { return stepLabels; }
        public void setStepLabels(List<String> stepLabels) { this.stepLabels = stepLabels; }
    }

    public static class UiReport {
        private List<Coach> coaches = new ArrayList<>();
        private List<CoachView> coachViews = new ArrayList<>();

        // Getters e Setters
        public List<Coach> getCoaches() { return coaches; }
        public void setCoaches(List<Coach> coaches) { this.coaches = coaches; }
        public List<CoachView> getCoachViews() { return coachViews; }
        public void setCoachViews(List<CoachView> coachViews) { this.coachViews = coachViews; }
    }

    public static class Coach {
        private String coachId;
        private String coachName;
        private String parentServiceId;
        private String parentServiceName;
        private List<String> preExecutionScripts = new ArrayList<>();
        private List<BoundaryEvent> boundaryEvents = new ArrayList<>();
        private List<UiComponent> components = new ArrayList<>();

        // Getters e Setters
        public String getCoachId() { return coachId; }
        public void setCoachId(String coachId) { this.coachId = coachId; }
        public String getCoachName() { return coachName; }
        public void setCoachName(String coachName) { this.coachName = coachName; }
        public String getParentServiceId() { return parentServiceId; }
        public void setParentServiceId(String parentServiceId) { this.parentServiceId = parentServiceId; }
        public String getParentServiceName() { return parentServiceName; }
        public void setParentServiceName(String parentServiceName) { this.parentServiceName = parentServiceName; }
        public List<String> getPreExecutionScripts() { return preExecutionScripts; }
        public void setPreExecutionScripts(List<String> preExecutionScripts) { this.preExecutionScripts = preExecutionScripts; }
        public List<BoundaryEvent> getBoundaryEvents() { return boundaryEvents; }
        public void setBoundaryEvents(List<BoundaryEvent> boundaryEvents) { this.boundaryEvents = boundaryEvents; }
        public List<UiComponent> getComponents() { return components; }
        public void setComponents(List<UiComponent> components) { this.components = components; }
    }

    public static class UiComponent {
        private String componentId;
        private String type;
        private String coachViewId;
        private String label;
        private String binding;
        private Map<String, String> events = new HashMap<>();
        private Map<String, String> configuration = new HashMap<>();
        private List<UiComponent> children = new ArrayList<>();

        // Getters e Setters
        public String getComponentId() { return componentId; }
        public void setComponentId(String componentId) { this.componentId = componentId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCoachViewId() { return coachViewId; }
        public void setCoachViewId(String coachViewId) { this.coachViewId = coachViewId; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getBinding() { return binding; }
        public void setBinding(String binding) { this.binding = binding; }
        public Map<String, String> getEvents() { return events; }
        public void setEvents(Map<String, String> events) { this.events = events; }
        public Map<String, String> getConfiguration() { return configuration; }
        public void setConfiguration(Map<String, String> configuration) { this.configuration = configuration; }
        public List<UiComponent> getChildren() { return children; }
        public void setChildren(List<UiComponent> children) { this.children = children; }
    }

    public static class RootView {
        private String coachId;
        private String coachName;
        private List<View> views = new ArrayList<>();

        // Getters e Setters
        public String getCoachId() { return coachId; }
        public void setCoachId(String coachId) { this.coachId = coachId; }
        public String getCoachName() { return coachName; }
        public void setCoachName(String coachName) { this.coachName = coachName; }
        public List<View> getViews() { return views; }
        public void setViews(List<View> views) { this.views = views; }
    }

    public static class View {
        private String viewId;
        private String viewName;
        private List<InlineScript> inlineScripts = new ArrayList<>();

        // Getters e Setters
        public String getViewId() { return viewId; }
        public void setViewId(String viewId) { this.viewId = viewId; }
        public String getViewName() { return viewName; }
        public void setViewName(String viewName) { this.viewName = viewName; }
        public List<InlineScript> getInlineScripts() { return inlineScripts; }
        public void setInlineScripts(List<InlineScript> inlineScripts) { this.inlineScripts = inlineScripts; }
    }

    public static class BoundaryEvent {
        private String viewPath;
        private String eventLabel;
        private boolean firesValidation;

        // Getters e Setters
        public String getViewPath() { return viewPath; }
        public void setViewPath(String viewPath) { this.viewPath = viewPath; }
        public String getEventLabel() { return eventLabel; }
        public void setEventLabel(String eventLabel) { this.eventLabel = eventLabel; }
        public boolean isFiresValidation() { return firesValidation; }
        public void setFiresValidation(boolean firesValidation) { this.firesValidation = firesValidation; }
    }

    public static class CoachView {
        private String id;
        private String name;
        private List<InlineScript> inlineScripts = new ArrayList<>();

        // Getters e Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<InlineScript> getInlineScripts() { return inlineScripts; }
        public void setInlineScripts(List<InlineScript> inlineScripts) { this.inlineScripts = inlineScripts; }
    }

    public static class InlineScript {
        private String name;
        private String scriptType;
        private String scriptBlock;

        // Getters e Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getScriptType() { return scriptType; }
        public void setScriptType(String scriptType) { this.scriptType = scriptType; }
        public String getScriptBlock() { return scriptBlock; }
        public void setScriptBlock(String scriptBlock) { this.scriptBlock = scriptBlock; }
    }

    public static class EnvironmentVariableUsage {
        private String name; // Ex: "tw.env.GSCCOM_CURRENT_ENVIRONMENT"
        private String value; // Ex: "DEV"

        // Getters e Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
    public static class ResourceBundleUsage {
        private String key; // Ex: "tw.resource.MyGroup.myKey"
        private String value; // Ex: "Meu Valor"

        // Getters e Setters
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
}