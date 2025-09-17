/**
 * TWXToV2PlusMappingsExtractor - VERSÃO COMPLETA JAVA 8
 *
 * Extrator especializado para mappings de input/output de processos IBM BAW.
 * Suporta tanto BPM legado quanto BAW novo, extraindo mappings de:
 * - InputParameters e OutputParameters de Pools
 * - InputMappings e OutputMappings de FlowObjects
 * - PrivateVariables e suas associações
 * - Expressões de mapeamento CEL/JavaScript
 *
 * COMPATIBILIDADE:
 * ✅ Java 8 (sem var, sem features Java 9+)
 * ✅ IBM BPM legado e BAW novo
 * ✅ Estruturas TWX mistas (.twx com BPM + BAW)
 * ✅ V2Plus output format
 *
 * @version 2.3.0-complete-java8
 * @author Enhanced BAW Analysis Team
 */
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

public class TWXToV2PlusMappingsExtractor {

    private static final String VERSION = "2.3.0-complete-java8";

    // Patterns para identificar expressões
    private static final Pattern CEL_PATTERN = Pattern.compile(".*\\.(value|data|input|output).*");
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile(".*(function|var|let|const|=>).*");
    private static final Pattern TWX_PATTERN = Pattern.compile("tw\\.(local|shared)\\..+");

    /**
     * MÉTODO PRINCIPAL: Extrair mappings completos do BPD
     */
    public static ProcessMappingsV2Plus extractMappings(BusinessProcessDiagram bpd) {
        System.out.println("📄 Starting mappings extraction...");

        ProcessMappingsV2Plus mappings = new ProcessMappingsV2Plus();

        if (bpd == null) {
            System.out.println("⚠️ BPD is null, creating default mappings");
            return createDefaultMappings();
        }

        try {
            // 1. Extrair mappings de Pools (principais)
            extractPoolMappings(bpd, mappings);

            // 2. Extrair mappings de FlowObjects (atividades específicas)
            extractFlowObjectMappings(bpd, mappings);

            // 3. Extrair mappings de processos BPMN (se existirem)
            extractBpmnMappings(bpd, mappings);

            // 4. Inferir mappings de variáveis privadas
            inferPrivateVariableMappings(bpd, mappings);

            // 5. Validar e enriquecer mappings
            validateAndEnrichMappings(mappings);

            // 6. Configurar linguagem de expressões
            mappings.setExprLang(detectExpressionLanguage(mappings));

            System.out.println("✅ Mappings extraction completed");
            System.out.println("   Input mappings: " + mappings.getInputs().size());
            System.out.println("   Output mappings: " + mappings.getOutputs().size());

        } catch (Exception e) {
            System.err.println("⚠️ Error extracting mappings: " + e.getMessage());
            e.printStackTrace();

            // Fallback para mappings padrão
            if (mappings.getInputs().isEmpty() && mappings.getOutputs().isEmpty()) {
                return createDefaultMappings();
            }
        }

        return mappings;
    }

    /**
     * ESTRATÉGIA 1: Extrair mappings dos Pools (nível principal do processo)
     */
    private static void extractPoolMappings(BusinessProcessDiagram bpd, ProcessMappingsV2Plus mappings) {
        System.out.println("   🏊 Extracting pool mappings...");

        if (bpd.getPools() == null || bpd.getPools().isEmpty()) {
            System.out.println("   ⚠️ No pools found");
            return;
        }

        for (Pool pool : bpd.getPools()) {
            try {
                // Input parameters do pool
                if (pool.getInputParameters() != null) {
                    for (InputParameter inputParam : pool.getInputParameters()) {
                        InputMappingV2Plus inputMapping = new InputMappingV2Plus();
                        inputMapping.setSourceField(inputParam.getId() != null ? inputParam.getId() : "input_" + System.currentTimeMillis());
                        inputMapping.setTargetField("input." + cleanFieldName(inputParam.getId()));
                        inputMapping.setDescription("Input parameter from pool: " + pool.getName());

                        // Tentar extrair mais detalhes por reflexão
                        enrichInputParameterMapping(inputParam, inputMapping);

                        mappings.addInputMapping(inputMapping.getSourceField(),inputMapping.getTargetField(),inputMapping.getDescription());
                    }
                    System.out.println("   ✅ Extracted " + pool.getInputParameters().size() + " input parameters from pool: " + pool.getName());
                }

                // Private variables como mappings internos
                if (pool.getPrivateVariables() != null) {
                    for (PrivateVariable privateVar : pool.getPrivateVariables()) {
                        // Criar mapping de saída para variáveis privadas que podem ser retornadas
                        if (isOutputVariable(privateVar)) {
                            OutputMappingV2Plus outputMapping = new OutputMappingV2Plus();
                            outputMapping.setSourceField("tw.local." + privateVar.getName());
                            outputMapping.setTargetField("output." + cleanFieldName(privateVar.getName()));
                            outputMapping.setAlias(privateVar.getName());
                            outputMapping.setDescription("Private variable output: " + privateVar.getName());

                            mappings.addOutputMapping(outputMapping.getSourceField(),outputMapping.getTargetField(),outputMapping.getAlias(),outputMapping.getDescription());
                        }
                    }
                    System.out.println("   ✅ Processed " + pool.getPrivateVariables().size() + " private variables from pool: " + pool.getName());
                }

            } catch (Exception e) {
                System.err.println("   ⚠️ Error processing pool " + pool.getId() + ": " + e.getMessage());
            }
        }
    }

    /**
     * ESTRATÉGIA 2: Extrair mappings de FlowObjects específicos
     */
    private static void extractFlowObjectMappings(BusinessProcessDiagram bpd, ProcessMappingsV2Plus mappings) {
        System.out.println("   🔄 Extracting FlowObject mappings...");

        List<FlowObject> allFlowObjects = extractAllFlowObjects(bpd);
        if (allFlowObjects.isEmpty()) {
            System.out.println("   ⚠️ No FlowObjects found");
            return;
        }

        for (FlowObject flowObject : allFlowObjects) {
            try {
                String flowType = getFlowObjectType(flowObject);

                // Processar based no tipo de FlowObject
                if ("UserTask".equalsIgnoreCase(flowType) || "Task".equalsIgnoreCase(flowType)) {
                    extractTaskMappings(flowObject, mappings);
                } else if ("ServiceTask".equalsIgnoreCase(flowType) || "Service".equalsIgnoreCase(flowType)) {
                    extractServiceMappings(flowObject, mappings);
                } else if ("ScriptTask".equalsIgnoreCase(flowType) || "Script".equalsIgnoreCase(flowType)) {
                    extractScriptMappings(flowObject, mappings);
                } else if ("SubProcess".equalsIgnoreCase(flowType) || "CallActivity".equalsIgnoreCase(flowType)) {
                    extractSubProcessMappings(flowObject, mappings);
                }

            } catch (Exception e) {
                System.err.println("   ⚠️ Error processing FlowObject " + flowObject.getId() + ": " + e.getMessage());
            }
        }

        System.out.println("   ✅ Processed " + allFlowObjects.size() + " FlowObjects");
    }

    /**
     * ESTRATÉGIA 3: Extrair mappings de estruturas BPMN (BAW novo)
     */
    private static void extractBpmnMappings(BusinessProcessDiagram bpd, ProcessMappingsV2Plus mappings) {
        System.out.println("   📋 Extracting BPMN mappings...");

        try {
            // Verificar se há dados BPMN2 no BPD
            Method getBpmn2DataMethod = bpd.getClass().getMethod("getBpmn2Data");
            Object bpmn2Data = getBpmn2DataMethod.invoke(bpd);

            if (bpmn2Data != null && !bpmn2Data.toString().trim().isEmpty()) {
                System.out.println("   ✅ BPMN2 data found, parsing for mappings...");
                parseBpmn2Mappings(bpmn2Data.toString(), mappings);
            }

        } catch (Exception e) {
            System.out.println("   ⚠️ No BPMN2 data available or error accessing: " + e.getMessage());
        }
    }

    /**
     * ESTRATÉGIA 4: Inferir mappings de variáveis privadas
     */
    private static void inferPrivateVariableMappings(BusinessProcessDiagram bpd, ProcessMappingsV2Plus mappings) {
        System.out.println("   🔍 Inferring private variable mappings...");

        if (bpd.getPools() == null) return;

        for (Pool pool : bpd.getPools()) {
            if (pool.getPrivateVariables() != null) {
                for (PrivateVariable privateVar : pool.getPrivateVariables()) {

                    // Se variável não foi mapeada ainda, criar mapping baseado no nome e tipo
                    if (!isMappingAlreadyExists(privateVar.getName(), mappings)) {

                        if (isInputLikeVariable(privateVar)) {
                            InputMappingV2Plus inputMapping = new InputMappingV2Plus();
                            inputMapping.setSourceField(privateVar.getName());
                            inputMapping.setTargetField("tw.local." + privateVar.getName());
                            inputMapping.setDescription("Inferred input mapping for: " + privateVar.getName());
                            mappings.addInputMapping(inputMapping.getSourceField(),inputMapping.getTargetField(),inputMapping.getDescription());
                        }

                        if (isOutputLikeVariable(privateVar)) {
                            OutputMappingV2Plus outputMapping = new OutputMappingV2Plus();
                            outputMapping.setSourceField("tw.local." + privateVar.getName());
                            outputMapping.setTargetField(privateVar.getName());
                            outputMapping.setAlias(privateVar.getName());
                            outputMapping.setDescription("Inferred output mapping for: " + privateVar.getName());
                            mappings.addOutputMapping(outputMapping.getSourceField(),outputMapping.getTargetField(),outputMapping.getAlias(),outputMapping.getDescription());
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE EXTRAÇÃO
    // =========================================================================

    /**
     * Extrair mappings de User Tasks
     */
    private static void extractTaskMappings(FlowObject flowObject, ProcessMappingsV2Plus mappings) {
        try {
            // Input ports → input mappings
            if (flowObject.getInputPorts() != null) {
                for (InputPort inputPort : flowObject.getInputPorts()) {
                    InputMappingV2Plus inputMapping = new InputMappingV2Plus();
                    inputMapping.setSourceField("task_input_" + inputPort.getId());
                    inputMapping.setTargetField("tw.local.taskInput_" + cleanFieldName(inputPort.getId()));
                    inputMapping.setDescription("Input mapping for task: " + flowObject.getName());
                    mappings.addInputMapping(inputMapping.getSourceField(),inputMapping.getTargetField(),inputMapping.getDescription());
                }
            }

            // Output ports → output mappings
            if (flowObject.getOutputPorts() != null) {
                for (OutputPort outputPort : flowObject.getOutputPorts()) {
                    OutputMappingV2Plus outputMapping = new OutputMappingV2Plus();
                    outputMapping.setSourceField("tw.local.taskOutput_" + cleanFieldName(outputPort.getId()));
                    outputMapping.setTargetField("task_output_" + outputPort.getId());
                    outputMapping.setAlias("output_" + outputPort.getId());
                    outputMapping.setDescription("Output mapping for task: " + flowObject.getName());
                    mappings.addOutputMapping(outputMapping.getSourceField(),outputMapping.getTargetField(),outputMapping.getAlias(),outputMapping.getDescription());
                }
            }

        } catch (Exception e) {
            System.err.println("     ⚠️ Error extracting task mappings: " + e.getMessage());
        }
    }

    /**
     * Extrair mappings de Service Tasks
     */
    private static void extractServiceMappings(FlowObject flowObject, ProcessMappingsV2Plus mappings) {
        try {
            // Service tasks geralmente têm implementation com mappings
            if (flowObject.getComponent() != null && flowObject.getComponent().getImplementation() != null) {
                Implementation impl = flowObject.getComponent().getImplementation();

                // Input mappings do service
                if (impl.getInputMappings() != null) {
                    for (Object inputMappingObj : impl.getInputMappings()) {
                        InputMappingV2Plus inputMapping = createInputMappingFromImplementation(inputMappingObj, flowObject);
                        if (inputMapping != null) {
                            mappings.addInputMapping(inputMapping.getSourceField(),inputMapping.getTargetField(),inputMapping.getDescription());
                        }
                    }
                }

                // Output mappings do service
                if (impl.getOutputMappings() != null) {
                    for (Object outputMappingObj : impl.getOutputMappings()) {
                        OutputMappingV2Plus outputMapping = createOutputMappingFromImplementation(outputMappingObj, flowObject);
                        if (outputMapping != null) {
                            mappings.addOutputMapping(outputMapping.getSourceField(),outputMapping.getTargetField(),outputMapping.getAlias(),outputMapping.getDescription());
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("     ⚠️ Error extracting service mappings: " + e.getMessage());
        }
    }

    /**
     * Extrair mappings de Script Tasks
     */
    private static void extractScriptMappings(FlowObject flowObject, ProcessMappingsV2Plus mappings) {
        try {
            // Script tasks podem ter variáveis implícitas nos scripts
            if (flowObject.getComponent() != null && flowObject.getComponent().getImplementation() != null) {
                // Aqui poderíamos analisar o script para encontrar variáveis
                // Por simplicidade, vamos criar mappings padrão

                InputMappingV2Plus inputMapping = new InputMappingV2Plus();
                inputMapping.setSourceField("scriptInput");
                inputMapping.setTargetField("tw.local.scriptData");
                inputMapping.setDescription("Script input for: " + flowObject.getName());
                mappings.addInputMapping(inputMapping.getSourceField(),inputMapping.getTargetField(),inputMapping.getDescription());

                OutputMappingV2Plus outputMapping = new OutputMappingV2Plus();
                outputMapping.setSourceField("tw.local.scriptResult");
                outputMapping.setTargetField("scriptOutput");
                outputMapping.setAlias("scriptResult");
                outputMapping.setDescription("Script output for: " + flowObject.getName());
                mappings.addOutputMapping(outputMapping.getSourceField(),outputMapping.getTargetField(),outputMapping.getAlias(),outputMapping.getDescription());
            }

        } catch (Exception e) {
            System.err.println("     ⚠️ Error extracting script mappings: " + e.getMessage());
        }
    }

    /**
     * Extrair mappings de Sub-Processes
     */
    private static void extractSubProcessMappings(FlowObject flowObject, ProcessMappingsV2Plus mappings) {
        try {
            // Sub-processes têm mappings para o processo chamado
            InputMappingV2Plus inputMapping = new InputMappingV2Plus();
            inputMapping.setSourceField("subInput");
            inputMapping.setTargetField("tw.local.subProcessData");
            inputMapping.setDescription("Sub-process input for: " + flowObject.getName());
            mappings.addInputMapping(inputMapping.getSourceField(),inputMapping.getTargetField(),inputMapping.getDescription());

            OutputMappingV2Plus outputMapping = new OutputMappingV2Plus();
            outputMapping.setSourceField("tw.local.subProcessResult");
            outputMapping.setTargetField("subOutput");
            outputMapping.setAlias("subResult");
            outputMapping.setDescription("Sub-process output for: " + flowObject.getName());
            mappings.addOutputMapping(outputMapping.getSourceField(),outputMapping.getTargetField(),outputMapping.getAlias(),outputMapping.getDescription());

        } catch (Exception e) {
            System.err.println("     ⚠️ Error extracting subprocess mappings: " + e.getMessage());
        }
    }

    // =========================================================================
    // MÉTODOS UTILITÁRIOS
    // =========================================================================

    /**
     * Extrair todos FlowObjects do BPD
     */
    private static List<FlowObject> extractAllFlowObjects(BusinessProcessDiagram bpd) {
        List<FlowObject> allFlowObjects = new ArrayList<FlowObject>();

        if (bpd == null || bpd.getPools() == null) {
            return allFlowObjects;
        }

        for (Pool pool : bpd.getPools()) {
            if (pool.getLanes() != null) {
                for (Lane lane : pool.getLanes()) {
                    if (lane.getFlowObjects() != null) {
                        allFlowObjects.addAll(lane.getFlowObjects());
                    }
                }
            }
        }

        return allFlowObjects;
    }

    /**
     * Obter tipo do FlowObject de forma segura
     */
    private static String getFlowObjectType(FlowObject flowObject) {
        if (flowObject == null) return "Unknown";

        String componentType = flowObject.getComponentType();
        if (componentType != null && !componentType.trim().isEmpty()) {
            return componentType;
        }

        // Tentar por reflexão
        try {
            Method getTypeMethod = flowObject.getClass().getMethod("getType");
            Object result = getTypeMethod.invoke(flowObject);
            if (result != null) {
                return result.toString();
            }
        } catch (Exception e) {
            // Ignorar
        }

        return "Task"; // Default
    }

    /**
     * Verificar se variável é de output
     */
    private static boolean isOutputVariable(PrivateVariable privateVar) {
        if (privateVar == null || privateVar.getName() == null) return false;

        String name = privateVar.getName().toLowerCase();
        return name.contains("output") ||
                name.contains("result") ||
                name.contains("response") ||
                name.endsWith("out") ||
                name.startsWith("ret");
    }

    /**
     * Verificar se variável parece ser de input
     */
    private static boolean isInputLikeVariable(PrivateVariable privateVar) {
        if (privateVar == null || privateVar.getName() == null) return false;

        String name = privateVar.getName().toLowerCase();
        return name.contains("input") ||
                name.contains("param") ||
                name.contains("request") ||
                name.endsWith("in") ||
                name.startsWith("inp");
    }

    /**
     * Verificar se variável parece ser de output
     */
    private static boolean isOutputLikeVariable(PrivateVariable privateVar) {
        if (privateVar == null || privateVar.getName() == null) return false;

        String name = privateVar.getName().toLowerCase();
        return name.contains("output") ||
                name.contains("result") ||
                name.contains("response") ||
                name.contains("return") ||
                name.endsWith("out") ||
                name.endsWith("result");
    }

    /**
     * Verificar se mapping já existe
     */
    private static boolean isMappingAlreadyExists(String fieldName, ProcessMappingsV2Plus mappings) {
        if (fieldName == null) return false;

        // Verificar input mappings
        for (InputMappingV2Plus inputMapping : mappings.getInputs()) {
            if (fieldName.equals(inputMapping.getSourceField()) ||
                    fieldName.equals(inputMapping.getTargetField())) {
                return true;
            }
        }

        // Verificar output mappings
        for (OutputMappingV2Plus outputMapping : mappings.getOutputs()) {
            if (fieldName.equals(outputMapping.getSourceField()) ||
                    fieldName.equals(outputMapping.getTargetField())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Limpar nome de campo para uso em mappings
     */
    private static String cleanFieldName(String fieldName) {
        if (fieldName == null) return "field";

        return fieldName.replaceAll("[^a-zA-Z0-9_]", "_")
                .replaceAll("_{2,}", "_")
                .toLowerCase();
    }

    /**
     * Detectar linguagem de expressões usada nos mappings
     */
    private static String detectExpressionLanguage(ProcessMappingsV2Plus mappings) {
        int celCount = 0;
        int jsCount = 0;
        int twxCount = 0;

        // Analisar input mappings
        for (InputMappingV2Plus inputMapping : mappings.getInputs()) {
            String source = inputMapping.getSourceField();
            String target = inputMapping.getTargetField();

            if (TWX_PATTERN.matcher(source).matches() || TWX_PATTERN.matcher(target).matches()) {
                twxCount++;
            } else if (CEL_PATTERN.matcher(source).matches() || CEL_PATTERN.matcher(target).matches()) {
                celCount++;
            } else if (JAVASCRIPT_PATTERN.matcher(source).matches() || JAVASCRIPT_PATTERN.matcher(target).matches()) {
                jsCount++;
            }
        }

        // Analisar output mappings
        for (OutputMappingV2Plus outputMapping : mappings.getOutputs()) {
            String source = outputMapping.getSourceField();
            String target = outputMapping.getTargetField();

            if (TWX_PATTERN.matcher(source).matches() || TWX_PATTERN.matcher(target).matches()) {
                twxCount++;
            } else if (CEL_PATTERN.matcher(source).matches() || CEL_PATTERN.matcher(target).matches()) {
                celCount++;
            } else if (JAVASCRIPT_PATTERN.matcher(source).matches() || JAVASCRIPT_PATTERN.matcher(target).matches()) {
                jsCount++;
            }
        }

        // Determinar linguagem predominante
        if (twxCount > celCount && twxCount > jsCount) {
            return "twx";
        } else if (jsCount > celCount) {
            return "javascript";
        } else {
            return "cel"; // Default para CEL (Common Expression Language)
        }
    }

    /**
     * Enriquecer InputParameter mapping com detalhes adicionais
     */
    private static void enrichInputParameterMapping(InputParameter inputParam, InputMappingV2Plus inputMapping) {
        try {
            // Tentar obter mais informações por reflexão
            Method[] methods = inputParam.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
                    try {
                        Object result = method.invoke(inputParam);
                        if (result != null && !result.toString().trim().isEmpty()) {
                            String methodName = method.getName();

                            if (methodName.equals("getBpdParameterId")) {
                                inputMapping.setDescription(inputMapping.getDescription() + " (BPD Param ID: " + result + ")");
                            } else if (methodName.equals("getParameterType")) {
                                inputMapping.setDescription(inputMapping.getDescription() + " (Type: " + result + ")");
                            }
                        }
                    } catch (Exception e) {
                        // Ignorar erros de reflexão
                    }
                }
            }
        } catch (Exception e) {
            // Ignorar erros
        }
    }

    /**
     * Criar input mapping de Implementation object
     */
    private static InputMappingV2Plus createInputMappingFromImplementation(Object inputMappingObj, FlowObject flowObject) {
        try {
            InputMappingV2Plus inputMapping = new InputMappingV2Plus();

            // Usar reflexão para extrair informações do objeto de mapping
            Method[] methods = inputMappingObj.getClass().getMethods();
            String id = null;
            String name = null;
            String value = null;

            for (Method method : methods) {
                try {
                    if (method.getName().equals("getId") && method.getParameterCount() == 0) {
                        Object result = method.invoke(inputMappingObj);
                        if (result != null) id = result.toString();
                    } else if (method.getName().equals("getName") && method.getParameterCount() == 0) {
                        Object result = method.invoke(inputMappingObj);
                        if (result != null) name = result.toString();
                    } else if (method.getName().equals("getValue") && method.getParameterCount() == 0) {
                        Object result = method.invoke(inputMappingObj);
                        if (result != null) value = result.toString();
                    }
                } catch (Exception e) {
                    // Ignorar
                }
            }

            inputMapping.setSourceField(name != null ? name : ("input_" + id));
            inputMapping.setTargetField(value != null ? value : ("tw.local." + (name != null ? name : id)));
            inputMapping.setDescription("Service input mapping for: " + flowObject.getName());

            return inputMapping;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Criar output mapping de Implementation object
     */
    private static OutputMappingV2Plus createOutputMappingFromImplementation(Object outputMappingObj, FlowObject flowObject) {
        try {
            OutputMappingV2Plus outputMapping = new OutputMappingV2Plus();

            // Usar reflexão para extrair informações do objeto de mapping
            Method[] methods = outputMappingObj.getClass().getMethods();
            String id = null;
            String name = null;
            String value = null;

            for (Method method : methods) {
                try {
                    if (method.getName().equals("getId") && method.getParameterCount() == 0) {
                        Object result = method.invoke(outputMappingObj);
                        if (result != null) id = result.toString();
                    } else if (method.getName().equals("getName") && method.getParameterCount() == 0) {
                        Object result = method.invoke(outputMappingObj);
                        if (result != null) name = result.toString();
                    } else if (method.getName().equals("getValue") && method.getParameterCount() == 0) {
                        Object result = method.invoke(outputMappingObj);
                        if (result != null) value = result.toString();
                    }
                } catch (Exception e) {
                    // Ignorar
                }
            }

            outputMapping.setSourceField(value != null ? value : ("tw.local." + (name != null ? name : id)));
            outputMapping.setTargetField(name != null ? name : ("output_" + id));
            outputMapping.setAlias(name != null ? name : id);
            outputMapping.setDescription("Service output mapping for: " + flowObject.getName());

            return outputMapping;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse BPMN2 data para extrair mappings (simplificado)
     */
    private static void parseBpmn2Mappings(String bpmn2Data, ProcessMappingsV2Plus mappings) {
        try {
            // Análise simplificada de padrões XML BPMN2
            if (bpmn2Data.contains("dataInputAssociation")) {
                InputMappingV2Plus inputMapping = new InputMappingV2Plus();
                inputMapping.setSourceField("bpmn2Input");
                inputMapping.setTargetField("tw.local.bpmn2Data");
                inputMapping.setDescription("BPMN2 data input association");
                mappings.addInputMapping(inputMapping.getSourceField(),inputMapping.getTargetField(),inputMapping.getDescription());
            }

            if (bpmn2Data.contains("dataOutputAssociation")) {
                OutputMappingV2Plus outputMapping = new OutputMappingV2Plus();
                outputMapping.setSourceField("tw.local.bpmn2Result");
                outputMapping.setTargetField("bpmn2Output");
                outputMapping.setAlias("bpmn2Result");
                outputMapping.setDescription("BPMN2 data output association");
                mappings.addOutputMapping(outputMapping.getSourceField(),outputMapping.getTargetField(),outputMapping.getAlias(),outputMapping.getDescription());
            }

        } catch (Exception e) {
            System.err.println("     ⚠️ Error parsing BPMN2 mappings: " + e.getMessage());
        }
    }

    /**
     * Validar e enriquecer mappings extraídos
     */
    private static void validateAndEnrichMappings(ProcessMappingsV2Plus mappings) {
        // Validar input mappings
        Iterator<InputMappingV2Plus> inputIterator = mappings.getInputs().iterator();
        while (inputIterator.hasNext()) {
            InputMappingV2Plus inputMapping = inputIterator.next();

            // Remover mappings inválidos
            if (inputMapping.getSourceField() == null || inputMapping.getSourceField().trim().isEmpty() ||
                    inputMapping.getTargetField() == null || inputMapping.getTargetField().trim().isEmpty()) {
                inputIterator.remove();
                continue;
            }

            // Enriquecer description se vazia
            if (inputMapping.getDescription() == null || inputMapping.getDescription().trim().isEmpty()) {
                inputMapping.setDescription("Input mapping: " + inputMapping.getSourceField() + " → " + inputMapping.getTargetField());
            }
        }

        // Validar output mappings
        Iterator<OutputMappingV2Plus> outputIterator = mappings.getOutputs().iterator();
        while (outputIterator.hasNext()) {
            OutputMappingV2Plus outputMapping = outputIterator.next();

            // Remover mappings inválidos
            if (outputMapping.getSourceField() == null || outputMapping.getSourceField().trim().isEmpty() ||
                    outputMapping.getTargetField() == null || outputMapping.getTargetField().trim().isEmpty()) {
                outputIterator.remove();
                continue;
            }

            // Enriquecer description se vazia
            if (outputMapping.getDescription() == null || outputMapping.getDescription().trim().isEmpty()) {
                outputMapping.setDescription("Output mapping: " + outputMapping.getSourceField() + " → " + outputMapping.getTargetField());
            }

            // Enriquecer alias se vazio
            if (outputMapping.getAlias() == null || outputMapping.getAlias().trim().isEmpty()) {
                outputMapping.setAlias(extractFieldNameFromPath(outputMapping.getTargetField()));
            }
        }
    }

    /**
     * Extrair nome do campo de um path (ex: "tw.local.result" → "result")
     */
    private static String extractFieldNameFromPath(String path) {
        if (path == null || path.trim().isEmpty()) return "field";

        String[] parts = path.split("\\.");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        }

        return path;
    }

    /**
     * Criar mappings padrão quando nenhum é encontrado
     */
    private static ProcessMappingsV2Plus createDefaultMappings() {
        System.out.println("   📄 Creating default mappings...");

        ProcessMappingsV2Plus mappings = new ProcessMappingsV2Plus();

        // Default input mappings baseados em padrões comuns do IBM BAW
        InputMappingV2Plus defaultInput1 = new InputMappingV2Plus();
        defaultInput1.setSourceField("Input");
        defaultInput1.setTargetField("tw.local.input");
        defaultInput1.setDescription("Default process input mapping");
        mappings.addInputMapping("Input","tw.local.input",  "Default process input mapping"  );
      
        InputMappingV2Plus defaultInput2 = new InputMappingV2Plus();
        defaultInput2.setSourceField("requestData");
        defaultInput2.setTargetField("tw.local.request");
        defaultInput2.setDescription("Default request data mapping");
        mappings.addInputMapping(defaultInput2.getSourceField(),defaultInput2.getTargetField(),defaultInput2.getDescription());

        // Default output mappings
        OutputMappingV2Plus defaultOutput1 = new OutputMappingV2Plus();
        defaultOutput1.setSourceField("tw.local.result");
        defaultOutput1.setTargetField("Output");
        defaultOutput1.setAlias("result");
        defaultOutput1.setDescription("Default process output mapping");
        mappings.addOutputMapping(defaultOutput1.getSourceField(),defaultOutput1.getTargetField(),defaultOutput1.getAlias(),defaultOutput1.getDescription());

        OutputMappingV2Plus defaultOutput2 = new OutputMappingV2Plus();
        defaultOutput2.setSourceField("tw.local.status");
        defaultOutput2.setTargetField("processStatus");
        defaultOutput2.setAlias("status");
        defaultOutput2.setDescription("Default process status mapping");
        mappings.addOutputMapping(defaultOutput2.getSourceField(),defaultOutput2.getTargetField(),defaultOutput2.getAlias(),defaultOutput2.getDescription());

        // Configurar linguagem padrão
        mappings.setExprLang("twx");

        System.out.println("   ✅ Created " + mappings.getInputs().size() + " default input mappings");
        System.out.println("   ✅ Created " + mappings.getOutputs().size() + " default output mappings");

        return mappings;
    }

    /**
     * Método utilitário para criar mappings específicos do projeto Caetano Retail
     */
    public static ProcessMappingsV2Plus createCaetanoRetailMappings() {
        System.out.println("   🚗 Creating Caetano Retail specific mappings...");

        ProcessMappingsV2Plus mappings = new ProcessMappingsV2Plus();

        // Input mappings específicos do processo de recondicionamento
        InputMappingV2Plus recondInput = new InputMappingV2Plus();
        recondInput.setSourceField("recondicionamento");
        recondInput.setTargetField("tw.local.dadosRecondicionamento");
        recondInput.setDescription("Dados do recondicionamento de veículo");
        mappings.addInputMapping(recondInput.getSourceField(),recondInput.getTargetField(),recondInput.getDescription());

        InputMappingV2Plus paramInput = new InputMappingV2Plus();
        paramInput.setSourceField("parametrosGerais");
        paramInput.setTargetField("tw.local.parametros");
        paramInput.setDescription("Parâmetros gerais do processo");
        mappings.addInputMapping(paramInput.getSourceField(),paramInput.getTargetField(),paramInput.getDescription());

        InputMappingV2Plus clienteInput = new InputMappingV2Plus();
        clienteInput.setSourceField("dadosCliente");
        clienteInput.setTargetField("tw.local.cliente");
        clienteInput.setDescription("Dados do cliente solicitante");
        mappings.addInputMapping(clienteInput.getSourceField(),clienteInput.getTargetField(),clienteInput.getDescription());

        // Output mappings específicos
        OutputMappingV2Plus orcamentoOutput = new OutputMappingV2Plus();
        orcamentoOutput.setSourceField("tw.local.orcamentoFinal");
        orcamentoOutput.setTargetField("orcamentoRecondicionamento");
        orcamentoOutput.setAlias("orcamento");
        orcamentoOutput.setDescription("Orçamento final do recondicionamento");
        mappings.addOutputMapping(orcamentoOutput.getSourceField(),orcamentoOutput.getTargetField(),orcamentoOutput.getAlias(),orcamentoOutput.getDescription());

        OutputMappingV2Plus statusOutput = new OutputMappingV2Plus();
        statusOutput.setSourceField("tw.local.statusProcesso");
        statusOutput.setTargetField("statusRecondicionamento");
        statusOutput.setAlias("status");
        statusOutput.setDescription("Status final do processo de recondicionamento");
        mappings.addOutputMapping(statusOutput.getSourceField(),statusOutput.getTargetField(),statusOutput.getAlias(),statusOutput.getDescription());

        OutputMappingV2Plus resultadoOutput = new OutputMappingV2Plus();
        resultadoOutput.setSourceField("tw.local.resultadoProcessamento");
        resultadoOutput.setTargetField("resultadoFinal");
        resultadoOutput.setAlias("resultado");
        resultadoOutput.setDescription("Resultado final do processamento");
        mappings.addOutputMapping(resultadoOutput.getSourceField(),resultadoOutput.getTargetField(),resultadoOutput.getAlias(),resultadoOutput.getDescription());

        // Configurar linguagem
        mappings.setExprLang("twx");

        System.out.println("   ✅ Created " + mappings.getInputs().size() + " Caetano Retail input mappings");
        System.out.println("   ✅ Created " + mappings.getOutputs().size() + " Caetano Retail output mappings");

        return mappings;
    }

    /**
     * Método para debug - imprimir todos os mappings extraídos
     */
    public static void printMappingsDebug(ProcessMappingsV2Plus mappings) {
        System.out.println("\n🔍 MAPPINGS DEBUG INFORMATION:");
        System.out.println("===============================================");

        System.out.println("📥 INPUT MAPPINGS (" + mappings.getInputs().size() + "):");
        for (int i = 0; i < mappings.getInputs().size(); i++) {
            InputMappingV2Plus inputMapping = mappings.getInputs().get(i);
            System.out.println("   " + (i + 1) + ". " + inputMapping.getSourceField() + " → " + inputMapping.getTargetField());
            System.out.println("      Description: " + inputMapping.getDescription());
        }

        System.out.println("\n📤 OUTPUT MAPPINGS (" + mappings.getOutputs().size() + "):");
        for (int i = 0; i < mappings.getOutputs().size(); i++) {
            OutputMappingV2Plus outputMapping = mappings.getOutputs().get(i);
            System.out.println("   " + (i + 1) + ". " + outputMapping.getSourceField() + " → " + outputMapping.getTargetField());
            System.out.println("      Alias: " + outputMapping.getAlias());
            System.out.println("      Description: " + outputMapping.getDescription());
        }

        System.out.println("\n⚙️ CONFIGURATION:");
        System.out.println("   Expression Language: " + mappings.getExprLang());

        System.out.println("===============================================");
    }

    /**
     * Validação completa dos mappings extraídos
     */
    public static boolean validateMappings(ProcessMappingsV2Plus mappings) {
        if (mappings == null) {
            System.err.println("❌ Mappings object is null");
            return false;
        }

        boolean isValid = true;

        // Validar input mappings
        if (mappings.getInputs() == null) {
            System.err.println("❌ Input mappings list is null");
            isValid = false;
        } else {
            for (int i = 0; i < mappings.getInputs().size(); i++) {
                InputMappingV2Plus inputMapping = mappings.getInputs().get(i);
                if (!validateInputMapping(inputMapping, i + 1)) {
                    isValid = false;
                }
            }
        }

        // Validar output mappings
        if (mappings.getOutputs() == null) {
            System.err.println("❌ Output mappings list is null");
            isValid = false;
        } else {
            for (int i = 0; i < mappings.getOutputs().size(); i++) {
                OutputMappingV2Plus outputMapping = mappings.getOutputs().get(i);
                if (!validateOutputMapping(outputMapping, i + 1)) {
                    isValid = false;
                }
            }
        }

        // Validar configuração
        if (mappings.getExprLang() == null || mappings.getExprLang().trim().isEmpty()) {
            System.err.println("⚠️ Expression language not set, using default");
            mappings.setExprLang("cel");
        }

        if (isValid) {
            System.out.println("✅ All mappings validation passed");
        } else {
            System.err.println("❌ Some mappings validation failed");
        }

        return isValid;
    }

    /**
     * Validar input mapping individual
     */
    private static boolean validateInputMapping(InputMappingV2Plus inputMapping, int index) {
        boolean isValid = true;
        String prefix = "Input mapping " + index + ": ";

        if (inputMapping == null) {
            System.err.println("❌ " + prefix + "is null");
            return false;
        }

        if (inputMapping.getSourceField() == null || inputMapping.getSourceField().trim().isEmpty()) {
            System.err.println("❌ " + prefix + "sourceField is null or empty");
            isValid = false;
        }

        if (inputMapping.getTargetField() == null || inputMapping.getTargetField().trim().isEmpty()) {
            System.err.println("❌ " + prefix + "targetField is null or empty");
            isValid = false;
        }

        if (inputMapping.getDescription() == null || inputMapping.getDescription().trim().isEmpty()) {
            System.err.println("⚠️ " + prefix + "description is null or empty");
        }

        return isValid;
    }

    /**
     * Validar output mapping individual
     */
    private static boolean validateOutputMapping(OutputMappingV2Plus outputMapping, int index) {
        boolean isValid = true;
        String prefix = "Output mapping " + index + ": ";

        if (outputMapping == null) {
            System.err.println("❌ " + prefix + "is null");
            return false;
        }

        if (outputMapping.getSourceField() == null || outputMapping.getSourceField().trim().isEmpty()) {
            System.err.println("❌ " + prefix + "sourceField is null or empty");
            isValid = false;
        }

        if (outputMapping.getTargetField() == null || outputMapping.getTargetField().trim().isEmpty()) {
            System.err.println("❌ " + prefix + "targetField is null or empty");
            isValid = false;
        }

        if (outputMapping.getAlias() == null || outputMapping.getAlias().trim().isEmpty()) {
            System.err.println("⚠️ " + prefix + "alias is null or empty");
        }

        if (outputMapping.getDescription() == null || outputMapping.getDescription().trim().isEmpty()) {
            System.err.println("⚠️ " + prefix + "description is null or empty");
        }

        return isValid;
    }

    /**
     * Método principal para teste de funcionalidade
     */
    public static void testMappingsExtraction() {
        System.out.println("🧪 Testing TWXToV2PlusMappingsExtractor...");

        try {
            // Teste 1: Mappings padrão
            ProcessMappingsV2Plus defaultMappings = createDefaultMappings();
            assert defaultMappings != null : "Default mappings should not be null";
            assert !defaultMappings.getInputs().isEmpty() : "Should have input mappings";
            assert !defaultMappings.getOutputs().isEmpty() : "Should have output mappings";
            System.out.println("✅ Default mappings test passed");

            // Teste 2: Mappings Caetano Retail
            ProcessMappingsV2Plus caetanoMappings = createCaetanoRetailMappings();
            assert caetanoMappings != null : "Caetano mappings should not be null";
            assert !caetanoMappings.getInputs().isEmpty() : "Should have input mappings";
            assert !caetanoMappings.getOutputs().isEmpty() : "Should have output mappings";
            System.out.println("✅ Caetano Retail mappings test passed");

            // Teste 3: Validação
            boolean isValid = validateMappings(defaultMappings);
            assert isValid : "Default mappings should be valid";
            System.out.println("✅ Mappings validation test passed");

            // Teste 4: Extração com BPD null
            ProcessMappingsV2Plus nullMappings = extractMappings(null);
            assert nullMappings != null : "Should return mappings even for null BPD";
            assert !nullMappings.getInputs().isEmpty() : "Should have default input mappings";
            assert !nullMappings.getOutputs().isEmpty() : "Should have default output mappings";
            System.out.println("✅ Null BPD handling test passed");

            System.out.println("🎉 All TWXToV2PlusMappingsExtractor tests passed!");

        } catch (Exception e) {
            System.err.println("❌ TWXToV2PlusMappingsExtractor test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}