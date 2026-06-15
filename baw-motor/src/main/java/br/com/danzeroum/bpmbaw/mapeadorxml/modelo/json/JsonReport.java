package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.json;

import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modelo de dados principal para o relatório JSON. Contém metadados, artefatos,
 * e a nova seção centralizada para definições de objetos de negócio.
 */
public class JsonReport {

    private Metadata metadata;
    private BusinessObjectDefinitions businessObjects = new BusinessObjectDefinitions();
    private List<JsonArtifact> artifacts = new ArrayList<>();
    private UiReport uiReport = new UiReport();
    private List<JsonEnvironmentSet> environmentVariables = new ArrayList<>(); // <-- ADICIONE ESTA LINHA
    private List<JsonResourceBundleGroup> resourceBundles = new ArrayList<>(); // <-- ADICIONE ESTA LINHA
    private List<JsonParticipantGroup> participantGroups = new ArrayList<>(); // <-- ADICIONE ESTA LINHA

    // --- Getters e Setters ---
    public Metadata getMetadata() { return metadata; }
    public void setMetadata(Metadata metadata) { this.metadata = metadata; }

    public BusinessObjectDefinitions getBusinessObjects() { return businessObjects; }
    public void setBusinessObjects(BusinessObjectDefinitions businessObjects) { this.businessObjects = businessObjects; }

    public List<JsonArtifact> getArtifacts() { return artifacts; }
    public void setArtifacts(List<JsonArtifact> artifacts) { this.artifacts = artifacts; }

    public UiReport getUiReport() { return uiReport; }
    public void setUiReport(UiReport uiReport) { this.uiReport = uiReport; }
    public List<JsonEnvironmentSet> getEnvironmentVariables() { return environmentVariables; }
    public void setEnvironmentVariables(List<JsonEnvironmentSet> environmentVariables) { this.environmentVariables = environmentVariables; }
    public List<JsonResourceBundleGroup> getResourceBundles() { return resourceBundles; }
    public void setResourceBundles(List<JsonResourceBundleGroup> resourceBundles) { this.resourceBundles = resourceBundles; }
    public List<JsonParticipantGroup> getParticipantGroups() { return participantGroups; }
    public void setParticipantGroups(List<JsonParticipantGroup> participantGroups) { this.participantGroups = participantGroups; }

    // --- Classes aninhadas para a estrutura do JSON ---

    @XmlType(name = "JsonBusinessObjectDefinitions")
    public static class BusinessObjectDefinitions {
        private List<BusinessObject> definitions = new ArrayList<>();

        public List<BusinessObject> getDefinitions() { return definitions; }
        public void setDefinitions(List<BusinessObject> definitions) { this.definitions = definitions; }
    }

    @XmlType(name = "JsonBusinessObject")
    public static class BusinessObject {
        private String typeId;
        private String typeName;
        private List<PropertyStructure> structure = new ArrayList<>();

        public String getTypeId() { return typeId; }
        public void setTypeId(String typeId) { this.typeId = typeId; }
        public String getTypeName() { return typeName; }
        public void setTypeName(String typeName) { this.typeName = typeName; }
        public List<PropertyStructure> getStructure() { return structure; }
        public void setStructure(List<PropertyStructure> structure) { this.structure = structure; }
    }

    @XmlType(name = "JsonPropertyStruc-ture")
    public static class PropertyStructure {
        private String name;
        private String typeRef;
        private boolean isList;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTypeRef() { return typeRef; }
        public void setTypeRef(String typeRef) { this.typeRef = typeRef; }
        public boolean isList() { return isList; }
        public void setList(boolean list) { this.isList = list; }
    }

    @XmlType(name = "JsonVariableInfo")
    public static class VariableInfo {
        private String name;
        private String typeId;
        private boolean isList;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTypeId() { return typeId; }
        public void setTypeId(String typeId) { this.typeId = typeId; }
        public boolean isList() { return isList; }
        public void setList(boolean list) { this.isList = list; }
    }

    // --- Mantenha todas as outras classes internas (Metadata, JsonArtifact, etc.) aqui ---
    // (O restante do ficheiro permanece igual à versão anterior)

    @XmlType(name = "JsonMetadata")
    public static class Metadata {
        private String projectName;
        private String twxFile;
        private String exportTimestamp;
        private List<Toolkit> toolkits = new ArrayList<>();
        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }
        public String getTwxFile() { return twxFile; }
        public void setTwxFile(String twxFile) { this.twxFile = twxFile; }
        public String getExportTimestamp() { return exportTimestamp; }
        public void setExportTimestamp(String exportTimestamp) { this.exportTimestamp = exportTimestamp; }
        public List<Toolkit> getToolkits() { return toolkits; }
        public void setToolkits(List<Toolkit> toolkits) { this.toolkits = toolkits; }
    }

    @XmlType(name = "JsonToolkit")
    public static class Toolkit {
        private String id;
        private String name;
        private String snapshotId;
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSnapshotId() { return snapshotId; }
        public void setSnapshotId(String snapshotId) { this.snapshotId = snapshotId; }
    }

    @XmlType(name = "JsonArtifact")
    public static class JsonArtifact {
        private String id;
        private String name;
        private String type;
        private String description;
        private String filePath;
        private Variables variables;
        private List<String> participants = new ArrayList<>(); // <-- ADICIONE ESTA LINHA

        public List<String> getParticipants() { return participants; }
        public void setParticipants(List<String> participants) { this.participants = participants; }
        private List<JsonFlowStep> flow = new ArrayList<>();
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        public Variables getVariables() { return variables; }
        public void setVariables(Variables variables) { this.variables = variables; }
        public List<JsonFlowStep> getFlow() { return flow; }
        public void setFlow(List<JsonFlowStep> flow) { this.flow = flow; }
    }

    @XmlType(name = "JsonVariables")
    public static class Variables {
        private List<VariableInfo> input = new ArrayList<>();
        private List<VariableInfo> output = new ArrayList<>();
        private List<VariableInfo> Private = new ArrayList<>();
        public List<VariableInfo> getInput() { return input; }
        public void setInput(List<VariableInfo> input) { this.input = input; }
        public List<VariableInfo> getOutput() { return output; }
        public void setOutput(List<VariableInfo> output) { this.output = output; }
        public List<VariableInfo> getPrivate() { return Private; }
        public void setPrivate(List<VariableInfo> aPrivate) { Private = aPrivate; }
    }

    @XmlType(name = "JsonFlowStep")
    public static class JsonFlowStep {
        private String stepId;
        private String name;
        private String type;
        private String script;
        private List<String> incomingFlows = new ArrayList<>();
        private List<String> outgoingFlows = new ArrayList<>();
        private List<Condition> conditions = new ArrayList<>();
        private String calledArtifactId;
        private ParameterMapping parameterMapping;
        private String coachId; // Nova propriedade

        // Adicione o getter e setter correspondente
        public String getCoachId() { return coachId; }
        public void setCoachId(String coachId) { this.coachId = coachId; }
        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getScript() { return script; }
        public void setScript(String script) { this.script = script; }
        public List<String> getIncomingFlows() { return incomingFlows; }
        public void setIncomingFlows(List<String> incomingFlows) { this.incomingFlows = incomingFlows; }
        public List<String> getOutgoingFlows() { return outgoingFlows; }
        public void setOutgoingFlows(List<String> outgoingFlows) { this.outgoingFlows = outgoingFlows; }
        public List<Condition> getConditions() { return conditions; }
        public void setConditions(List<Condition> conditions) { this.conditions = conditions; }
        public String getCalledArtifactId() { return calledArtifactId; }
        public void setCalledArtifactId(String calledArtifactId) { this.calledArtifactId = calledArtifactId; }
        public ParameterMapping getParameterMapping() { return parameterMapping; }
        public void setParameterMapping(ParameterMapping parameterMapping) { this.parameterMapping = parameterMapping; }
    }

    @XmlType(name = "JsonCondition")
    public static class Condition {
        private String targetStepId;
        private String name;
        private String expression;
        private boolean isDefault;
        public String getTargetStepId() { return targetStepId; }
        public void setTargetStepId(String targetStepId) { this.targetStepId = targetStepId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }
        public boolean isDefault() { return isDefault; }
        public void setDefault(boolean aDefault) { isDefault = aDefault; }
    }

    @XmlType(name = "JsonParameterMapping")
    public static class ParameterMapping {
        private List<Mapping> input = new ArrayList<>();
        private List<Mapping> output = new ArrayList<>();
        public List<Mapping> getInput() { return input; }
        public void setInput(List<Mapping> input) { this.input = input; }
        public List<Mapping> getOutput() { return output; }
        public void setOutput(List<Mapping> output) { this.output = output; }
    }

    @XmlType(name = "JsonMapping")
    public static class Mapping {
        private String source;
        private String target;
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
    }

    @XmlType(name = "JsonUiReport")
    public static class UiReport {
        private List<JsonCoach> coaches = new ArrayList<>();
        private List<JsonCoachView> coachViews = new ArrayList<>();
        public List<JsonCoach> getCoaches() { return coaches; }
        public void setCoaches(List<JsonCoach> coaches) { this.coaches = coaches; }
        public List<JsonCoachView> getCoachViews() { return coachViews; }
        public void setCoachViews(List<JsonCoachView> coachViews) { this.coachViews = coachViews; }
    }

    @XmlType(name = "JsonCoach")
    public static class JsonCoach {
        private String coachId; // ID único para este coach, pode ser o ID do serviço + nome do coach.


        public String getCoachId() { return coachId; }
        public void setCoachId(String coachId) { this.coachId = coachId; }
        private String coachName;
        private String parentServiceId;
        private String parentServiceName;
        private List<String> preExecutionScripts = new ArrayList<>();
        private List<BoundaryEvent> boundaryEvents = new ArrayList<>();
        private List<JsonUiComponent> components = new ArrayList<>();
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
        public List<JsonUiComponent> getComponents() { return components; }
        public void setComponents(List<JsonUiComponent> components) { this.components = components; }
    }

    @XmlType(name = "JsonUiComponent")
    public static class JsonUiComponent {
        private String componentId;
        private String type;
        private String coachViewId;
        private String label;
        private String binding;
        private Map<String, String> events = new HashMap<>();
        private Map<String, String> configuration = new HashMap<>();
        private List<JsonUiComponent> children = new ArrayList<>();
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
        public List<JsonUiComponent> getChildren() { return children; }
        public void setChildren(List<JsonUiComponent> children) { this.children = children; }
    }

    @XmlType(name = "JsonBoundaryEvent")
    public static class BoundaryEvent {
        private String viewPath;
        private String eventLabel;
        private boolean firesValidation;
        public String getViewPath() { return viewPath; }
        public void setViewPath(String viewPath) { this.viewPath = viewPath; }
        public String getEventLabel() { return eventLabel; }
        public void setEventLabel(String eventLabel) { this.eventLabel = eventLabel; }
        public boolean isFiresValidation() { return firesValidation; }
        public void setFiresValidation(boolean firesValidation) { this.firesValidation = firesValidation; }
    }

    @XmlType(name = "JsonCoachView")
    public static class JsonCoachView {
        private String id;
        private String name;
        private List<InlineScript> inlineScripts = new ArrayList<>();
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<InlineScript> getInlineScripts() { return inlineScripts; }
        public void setInlineScripts(List<InlineScript> inlineScripts) { this.inlineScripts = inlineScripts; }
    }

    @XmlType(name = "JsonInlineScript")
    public static class InlineScript {
        private String name;
        private String scriptType;
        private String scriptBlock;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getScriptType() { return scriptType; }
        public void setScriptType(String scriptType) { this.scriptType = scriptType; }
        public String getScriptBlock() { return scriptBlock; }
        public void setScriptBlock(String scriptBlock) { this.scriptBlock = scriptBlock; }
    }

    @XmlType(name = "JsonEnvironmentSet")
    public static class JsonEnvironmentSet {
        private String id;
        private String name;
        private List<JsonEnvironmentVariable> variables = new ArrayList<>();

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<JsonEnvironmentVariable> getVariables() { return variables; }
        public void setVariables(List<JsonEnvironmentVariable> variables) { this.variables = variables; }
    }

    @XmlType(name = "JsonEnvironmentVariable")
    public static class JsonEnvironmentVariable {
        private String name;
        private String value;
        private String description;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    @XmlType(name = "JsonResourceBundleGroup")
    public static class JsonResourceBundleGroup {
        private String id;
        private String name;
        private List<JsonResourceBundleKey> keys = new ArrayList<>();

        // Getters e Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<JsonResourceBundleKey> getKeys() { return keys; }
        public void setKeys(List<JsonResourceBundleKey> keys) { this.keys = keys; }
    }

    @XmlType(name = "JsonResourceBundleKey")
    public static class JsonResourceBundleKey {
        private String key;
        private String value;

        // Getters e Setters
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    @XmlType(name = "JsonParticipantGroup")
    public static class JsonParticipantGroup {
        private String id;
        private String name;
        private List<JsonParticipantMember> members = new ArrayList<>();

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<JsonParticipantMember> getMembers() { return members; }
        public void setMembers(List<JsonParticipantMember> members) { this.members = members; }
    }

    @XmlType(name = "JsonParticipantMember")
    public static class JsonParticipantMember {
        private String type;
        private String name;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}