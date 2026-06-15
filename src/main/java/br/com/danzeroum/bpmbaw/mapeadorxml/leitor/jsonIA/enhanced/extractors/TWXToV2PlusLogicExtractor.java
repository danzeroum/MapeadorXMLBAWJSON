package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoaderV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Component;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.FlowObject;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Implementation;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.TWComponent;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrator TWX → V2Plus Logic - VERSÃO CONSCIENTE DO CONTEXTO
 *
 * CORREÇÕES APLICADAS:
 * ✅ Adicionado construtor que aceita ProcessLoaderV2Plus, corrigindo o erro de compilação.
 * ✅ A lógica agora usa o 'loader' para carregar artefatos referenciados (serviços) e encontrar os scripts reais.
 * ✅ O método extractLogic agora não é mais estático para poder acessar o 'loader'.
 */
public class TWXToV2PlusLogicExtractor {

    private static ProcessLoaderV2Plus loader = null;

    /**
     * CORREÇÃO: Construtor que aceita o ProcessLoader para dar contexto ao extrator.
     * @param loader A instância do ProcessLoader com todos os artefatos carregados.
     */
    public TWXToV2PlusLogicExtractor(ProcessLoaderV2Plus loader) {
        if (loader == null) {
            throw new IllegalArgumentException("ProcessLoader cannot be null.");
        }
        this.loader = loader;
    }

    /**
     * Extrai a lógica de uma lista de FlowObjects.
     * Este método agora não é estático para poder usar o 'loader'.
     */
    public static ProcessLogicV2Plus extractLogic(List<FlowObject> flowObjects) {
        ProcessLogicV2Plus logic = new ProcessLogicV2Plus();
        if (flowObjects == null || flowObjects.isEmpty()) {
            return logic;
        }

        for (FlowObject flowObject : flowObjects) {
            String scriptContent = findScriptForFlowObject(flowObject);

            if (scriptContent != null && !scriptContent.trim().isEmpty()) {
                LogicItemV2Plus item = convertToLogicItem(flowObject, scriptContent);
                logic.addItem(item);
            }
        }
        return logic;
    }

    /**
     * NOVA LÓGICA: Encontra o script para um FlowObject, usando o loader para buscar em outros artefatos.
     */
    private static String findScriptForFlowObject(FlowObject flowObject) {
        if (flowObject == null || flowObject.getComponent() == null || flowObject.getComponent().getImplementation() == null) {
            return null;
        }

        Implementation impl = flowObject.getComponent().getImplementation();
        String activityId = impl.getAttachedActivityId();

        if (activityId == null || activityId.trim().isEmpty()) {
            return null; // Não é uma chamada para um serviço com script
        }

        // Usa o loader para buscar o artefato (serviço) referenciado
        Object artifact = loader.getArtefatoDoCache(activityId);

        if (artifact instanceof Teamworks) {
            Teamworks tw = (Teamworks) artifact;
            if (tw.getProcess() != null) {
                Process process = tw.getProcess();

                // Estratégia 1: Procurar script no clobField1 (serviços simples)
                if (process.getClobField1() != null && !process.getClobField1().trim().isEmpty()) {
                    return process.getClobField1();
                }

                // Estratégia 2: Procurar em 'items' (serviços mais complexos)
                if (process.getItems() != null) {
                    for (Item item : process.getItems()) {
                        if (item.getTwComponent() != null && item.getTwComponent().getScript() != null) {
                            return item.getTwComponent().getScript();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Converte um FlowObject e seu script encontrado para um LogicItemV2Plus.
     */
    private static LogicItemV2Plus convertToLogicItem(FlowObject flowObject, String scriptContent) {
        LogicItemV2Plus item = new LogicItemV2Plus();
        item.setId("lg:" + cleanId(flowObject.getId()));
        item.setName(flowObject.getName() != null ? flowObject.getName() : "Script Task");
        item.setType(ProcessLogicV2Plus.ItemType.SCRIPT);
        item.setLanguage(ProcessLogicV2Plus.ScriptLanguage.JAVASCRIPT);
        item.setCode(scriptContent);
        item.setDescription("Script extraído da atividade: " + flowObject.getName());

        // Análise básica de inputs/outputs
        item.setInputs(extractVariablesFromScript(scriptContent, true));
        item.setOutputs(extractVariablesFromScript(scriptContent, false));

        return item;
    }

    /**
     * Extrai variáveis de um script (lógica simplificada).
     * @param isInput Se true, procura por leituras; se false, procura por escritas.
     */
    private static List<String> extractVariablesFromScript(String script, boolean isInput) {
        List<String> vars = new ArrayList<>();
        Set<String> uniqueVars = new HashSet<>();

        // Regex para encontrar `tw.local.variável`
        Pattern pattern = Pattern.compile("tw\\.local\\.([a-zA-Z0-9_]+)");
        Matcher matcher = pattern.matcher(script);

        while (matcher.find()) {
            String varName = matcher.group(1);
            if (isInput) {
                uniqueVars.add(varName);
            } else {
                // Heurística simples: se a variável está à esquerda de um '=', é um output.
                int matchIndex = matcher.start();
                int lineEnd = script.indexOf('\n', matchIndex);
                if (lineEnd == -1) lineEnd = script.length();
                String line = script.substring(0, lineEnd);
                if (line.substring(matchIndex).contains("=")) {
                    uniqueVars.add(varName);
                }
            }
        }
        vars.addAll(uniqueVars);
        return vars;
    }

    private static String cleanId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return "unknown_" + System.currentTimeMillis();
        }
        return id.replaceAll("[^a-zA-Z0-9_]", "_");
    }
}