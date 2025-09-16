package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.LogicItemV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessLogicV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Extrator de Lógica BPMN 2.0 (Versão Final com Extração Profunda e Coleta de Resultados)
 * Extrai scripts de todos os elementos, incluindo ScriptTasks, SubProcesses,
 * CallActivities e as implementações aninhadas de GlobalUserTasks.
 * @version 3.1 - Recursive Result Collection Fix
 */
public class BpmnToV2PlusLogicExtractor {

    public static ProcessLogicV2Plus extractLogic(Definitions definitions, ProcessLoaderV2Plus loader) {
        ProcessLogicV2Plus logic = new ProcessLogicV2Plus();
        if (definitions == null) {
            return logic;
        }

        Set<String> processedArtifactIds = new HashSet<>();

        if (definitions.getProcess() != null) {
            extractLogicFromFlowElements(definitions.getProcess().getFlowElements(), logic, loader, processedArtifactIds);
        }

        if (definitions.getGlobalUserTask() != null && definitions.getGlobalUserTask().getImplementation() != null) {
            extractLogicFromFlowElements(definitions.getGlobalUserTask().getImplementation().getFlowElements(), logic, loader, processedArtifactIds);
        }

        return logic;
    }

    private static void extractLogicFromFlowElements(List<Object> elements, ProcessLogicV2Plus logic, ProcessLoaderV2Plus loader, Set<String> processedArtifactIds) {
        if (elements == null) return;

        for (Object element : elements) {
            if (element instanceof ScriptTask) {
                handleScriptTask((ScriptTask) element, logic);
            }
            else if (element instanceof SubProcess) {
                handleSubProcess((SubProcess) element, logic, loader, processedArtifactIds);
            }
            else if (element instanceof CallActivity) {
                handleCallActivity((CallActivity) element, logic, loader, processedArtifactIds);
            }
        }
    }

    private static void handleScriptTask(ScriptTask scriptTask, ProcessLogicV2Plus logic) {
        if (scriptTask.getScript() != null && !scriptTask.getScript().trim().isEmpty()) {
            LogicItemV2Plus item = new LogicItemV2Plus();
            item.setId("lg:" + scriptTask.getId());
            item.setName(scriptTask.getName());
            item.setType(ProcessLogicV2Plus.ItemType.SCRIPT);
            item.setLanguage(ProcessLogicV2Plus.ScriptLanguage.JAVASCRIPT);
            item.setCode(scriptTask.getScript());
            item.setDescription("Lógica da tarefa de script: " + scriptTask.getName());
            logic.addItem(item);
        }
    }

    private static void handleSubProcess(SubProcess subProcess, ProcessLogicV2Plus logic, ProcessLoaderV2Plus loader, Set<String> processedArtifactIds) {
        extractLogicFromFlowElements(subProcess.getFlowElements(), logic, loader, processedArtifactIds);
    }

    private static void handleCallActivity(CallActivity callActivity, ProcessLogicV2Plus logic, ProcessLoaderV2Plus loader, Set<String> processedArtifactIds) {
        String calledElementId = callActivity.getCalledElement();
        if (calledElementId == null || !processedArtifactIds.add(calledElementId)) {
            return;
        }

        Object artifact = loader.getArtefatoDoCache(calledElementId);

        if (artifact instanceof Teamworks) {
            Teamworks twService = (Teamworks) artifact;
            if (twService.getProcess() != null) {
                extractLogicFromLegacyService(twService.getProcess(), logic);
            }
        } else if (artifact instanceof Definitions) {
            Definitions defs = (Definitions) artifact;

            // --- CORREÇÃO DEFINITIVA ---
            // 1. Chama a extração recursiva
            ProcessLogicV2Plus extractedLogic = extractLogic(defs, loader);
            // 2. Adiciona os itens de lógica encontrados ao resultado principal
            if (extractedLogic != null && extractedLogic.getItems() != null) {
                for (LogicItemV2Plus item : extractedLogic.getItems()) {
                    logic.addItem(item); // O método addItem já previne duplicatas
                }
            }
        }
    }

    private static void extractLogicFromLegacyService(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process service, ProcessLogicV2Plus logic) {
        if (service.getClobField1() != null && !service.getClobField1().trim().isEmpty()) {
            LogicItemV2Plus item = new LogicItemV2Plus();
            item.setId("lg:service_" + service.getId());
            item.setName(service.getName());
            item.setType(ProcessLogicV2Plus.ItemType.SCRIPT);
            item.setCode(service.getClobField1());
            item.setDescription("Lógica do serviço legado '" + service.getName() + "'");
            logic.addItem(item);
        } else if (service.getItems() != null) {
            for (Item legacyItem : service.getItems()) {
                if (legacyItem.getTwComponent() != null && legacyItem.getTwComponent().getScript() != null) {
                    LogicItemV2Plus item = new LogicItemV2Plus();
                    item.setId("lg:item_" + legacyItem.getProcessItemId());
                    item.setName(legacyItem.getName());
                    item.setType(ProcessLogicV2Plus.ItemType.SCRIPT);
                    item.setCode(legacyItem.getTwComponent().getScript());
                    item.setDescription("Lógica do item '" + legacyItem.getName() + "' no serviço '" + service.getName() + "'");
                    logic.addItem(item);
                }
            }
        }
    }
}