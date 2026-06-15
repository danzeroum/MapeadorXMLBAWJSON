package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2.ProcessLoader;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
// Imports para os dois tipos de Coach
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.Layout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.ContentBoxContrib;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.ConfigData;
// Fim dos imports
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.Expression;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.PackageObject;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.ProcessParameter;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.ProcessVariable;

import java.io.PrintWriter;
import java.util.*;

/**
 * (VERSÃO FINAL)
 * Classe especialista focada em gerar um relatório de análise de processo formatado
 * em Markdown, otimizado para Analistas de Negócio e de Processos.
 */
public class ProcessPrinter {

    private final PrintWriter writer;
    private final Set<String> artefatosJaImpressos;
    private final List<PackageObject> todosOsArtefatos = new ArrayList<>();

    public ProcessPrinter(PrintWriter writer) {
        this.writer = writer;
        this.artefatosJaImpressos = new HashSet<>();
    }

    public void registrarArtefatoParaIndice(PackageObject artefato) {
        if (artefato != null && todosOsArtefatos.stream().noneMatch(a -> a.getId().equals(artefato.getId()))) {
            todosOsArtefatos.add(artefato);
        }
    }

    public void printIndex() {
        writer.println("# 🗺️ Índice de Artefatos do Processo");
        writer.println("---");
        writer.println("Este documento detalha o fluxo e as regras de negócio dos seguintes componentes. Clique em 'Ver Detalhes' para navegar até a seção correspondente.");
        writer.println();
        writer.println("| Nome do Artefato | Tipo | Link para Detalhes |");
        writer.println("| :--- | :--- | :--- |");
        todosOsArtefatos.sort(Comparator.comparing(PackageObject::getName));
        for (PackageObject artefato : todosOsArtefatos) {
            String nome = artefato.getName();
            String tipo = formatarTipo(artefato.getType());
            String ancora = gerarAncora(nome);
            writer.println("| " + nome + " | " + tipo + " | [Ver Detalhes](#" + ancora + ") |");
        }
        writer.println("\n<br>");
    }

    /**
     * **MÉTODO MODIFICADO**
     * Imprime o cabeçalho de um artefato, agora incluindo a informação de sua origem (projeto ou toolkit).
     * @param location O objeto ArtifactLocation que contém os detalhes do artefato.
     * @return true se o cabeçalho foi impresso, false se o artefato já foi impresso antes.
     */
    public boolean printHeaderDoArtefato(ProcessLoader.ArtifactLocation location) {
        if (location == null || location.objectInfo == null || !artefatosJaImpressos.add(location.objectInfo.getId())) {
            return false;
        }
        writer.println("\n---");
        String icon = getIconForType(location.objectInfo.getType());
        writer.println("\n## " + icon + " " + location.objectInfo.getName());
        writer.println("**Tipo:** " + formatarTipo(location.objectInfo.getType()));

        // --- NOVA LÓGICA ---
        if (location.toolkitName != null && !location.toolkitName.isEmpty()) {
            writer.println("**Origem:** Toolkit (`" + location.toolkitName + "`)");
        } else {
            writer.println("**Origem:** Projeto Principal");
        }
        // --- FIM DA NOVA LÓGICA ---

        return true;
    }

    public void printSectionHeader(String title, String indent) {
        writer.println("\n" + indent + "#### " + title);
    }
    public void printFlowReportTitle(String title) {
        writer.println("# 🗺️ " + title);
        writer.println("---");
    }

    public void printFlowSectionHeader(String processName) {
        writer.println("\n## 🌊 Fluxo do Processo: " + processName);
    }

    public void printSubSectionHeader(String title) {
        writer.println("\n### " + title);
    }

    public void printAlternativeFlowStart(String triggerEventName) {
        writer.println("  - ⚡ **Início por Evento:** " + triggerEventName);
    }

    public void printGatewayDecision(String gatewayName, int depth) {
        writer.println(getIndent(depth) + "- 🔀 **Decisão:** " + gatewayName);
    }

    public void printBranchStart(String condition, int depth) {
        String conditionText = (condition != null && !condition.isEmpty()) ? condition : "Caminho Padrão";
        writer.println(getIndent(depth) + "- ↳ **Caminho:** " + conditionText);
    }
    /**

    public void printBranchStart(String gatewayName, String condition, int depth, boolean isFirstBranch) {
        String conditionText = (condition != null && !condition.isEmpty()) ? condition : "Caminho Padrão";

        // Se for o primeiro caminho, a impressão é mais simples.
        if (isFirstBranch) {
            writer.println(getIndent(depth) + "- ↳ **Caminho:** " + conditionText);
        } else {
            // Para os demais, adiciona o contexto "Retorno Decisão".
            writer.println(getIndent(depth) + "- ↳ **Retorno Decisão:** " + gatewayName + " - **Caminho:** " + conditionText);
        }
    }
*/

    // Em: src/br/com/danzeroum/bpmbaw/mapeadorxml/leitor/ProcessPrinter.java

    /**
     * MÉTODO MODIFICADO
     * Cria a âncora usando o ID único do ramo para garantir que os links funcionem.
     */
    public void printBranchStart(String gatewayName, String condition, String branchId, int depth, boolean isFirstBranch) {
        String conditionText = (condition != null && !condition.isEmpty()) ? condition : "Caminho Padrão";
        // ALTERAÇÃO: A âncora agora é baseada no ID do gateway + ID do ramo, garantindo unicidade.
        String ancoraId = gerarAncora(gatewayName + "-" + branchId);
        String header;

        if (isFirstBranch) {
            header = "- <a id=\"" + ancoraId + "\"></a>↳ **Caminho:** " + conditionText;
        } else {
            header = "- <a id=\"" + ancoraId + "\"></a>↳ **Retorno Decisão:** " + gatewayName + " - **Caminho:** " + conditionText;
        }
        writer.println(getIndent(depth) + header);
    }

    /**
     * MÉTODO MODIFICADO
     * Gera os links de navegação usando os IDs únicos dos ramos.
     */
    public void printBranchNavigationLinks(String gatewayName, String prevBranchName, String prevBranchId, String nextBranchName, String nextBranchId, String indent) {
        String prevBranchText = (prevBranchName != null && !prevBranchName.isEmpty()) ? prevBranchName : "Caminho Padrão";
        String nextBranchText = (nextBranchName != null && !nextBranchName.isEmpty()) ? nextBranchName : "Caminho Padrão";

        List<String> links = new ArrayList<>();
        if (prevBranchId != null) {
            // ALTERAÇÃO: A âncora do link também é gerada com o ID.
            String ancora = gerarAncora(gatewayName + "-" + prevBranchId);
            links.add("[⏮️ Caminho Anterior: " + prevBranchText + "](#" + ancora + ")");
        }
        if (nextBranchId != null) {
            // ALTERAÇÃO: A âncora do link também é gerada com o ID.
            String ancora = gerarAncora(gatewayName + "-" + nextBranchId);
            links.add("[Caminho Seguinte: " + nextBranchText + " ⏭️](#" + ancora + ")");
        }

        if (!links.isEmpty()) {
            writer.println(indent + "  > " + String.join(" | ", links));
        }
    }

    public void printMergePoint(String nodeName, int depth) {
        writer.println(getIndent(depth) + "- ↪️ *(...junta-se ao fluxo em **" + nodeName + "**)*");
    }

    public void printFlowStep(Object node, int depth) {
        String type = getNodeType(node);
        String name = getNodeName(node);
        String icon = getIconForType(type);

        if ("(Sem Nome)".equals(name)) {
            writer.println(getIndent(depth) + "- " + icon + " " + type);
        } else {
            writer.println(getIndent(depth) + "- " + icon + " **" + name + "** (*" + type + "*)");
        }
    }

    public void printLegacyFlowCondition(Flow flow, String indent) {
        String flowName = flow.getName();
        String conditionExpr = null;

        if (flow.getConnection() != null && flow.getConnection().getCondition() != null) {
            conditionExpr = flow.getConnection().getCondition().getExpression();
        }

        boolean hasName = flowName != null && !flowName.trim().isEmpty();
        boolean hasExpr = conditionExpr != null && !conditionExpr.trim().isEmpty();

        if (hasName || hasExpr) {
            String displayName = hasName ? flowName : "Fluxo Padrão";
            writer.println(indent + "  - > 📜 **Regra de Negócio (Decisão): " + displayName + "**");
            if(hasExpr){
                writer.println(indent + "    > ```javascript\n" + indent + "    > " + conditionExpr.replace("\n", "\n" + indent + "    > ") + "\n" + indent + "    > ```");
            }
        }
    }

    public void printLegacyServiceCondition(Link link, String expression, String indent) {
        String flowName = link.getName();
        boolean hasName = flowName != null && !flowName.trim().isEmpty();
        boolean hasExpr = expression != null && !expression.trim().isEmpty();

        if (hasName || hasExpr) {
            String displayName = hasName ? flowName : "Condição";
            writer.println(indent + "  - > 📜 **Regra de Negócio (Decisão): " + displayName + "**");
            if (hasExpr) {
                writer.println(indent + "    > ```javascript\n" + indent + "    > " + expression.replace("\n", "\n" + indent + "    > ") + "\n" + indent + "    > ```");
            }
        }
    }

    // Adicionar em br/com/danzeroum/bpmbaw/mapeadorxml/leitor/ProcessPrinter.java

    /**
     * Imprime a condição de um fluxo de sequência (SequenceFlow) de um processo BPMN.
     * É o equivalente moderno dos métodos printLegacyFlowCondition e printLegacyServiceCondition.
     *
     * @param flow O objeto SequenceFlow que representa a seta de conexão.
     * @param indent A indentação para formatação do relatório.
     */
    public void printModernFlowCondition(SequenceFlow flow, String indent) {
        // Extrai o nome do fluxo (o "label" da seta no diagrama)
        String flowName = flow.getName();

        // Extrai a expressão de condição, com segurança para evitar NullPointerException
        String conditionExpr = Optional.ofNullable(flow.getConditionExpression())
                .map(Expression::getExpression) // Usa o getter correto
                .map(String::trim)
                .orElse(null);


        // Verifica se há um nome ou uma expressão relevante para exibir
        // Ignora expressões "true" que são comuns em fluxos padrão
        boolean hasName = flowName != null && !flowName.trim().isEmpty();
        boolean hasExpr = conditionExpr != null && !conditionExpr.isEmpty() && !"true".equalsIgnoreCase(conditionExpr);

        if (hasName || hasExpr) {
            String displayName = hasName ? flowName : "Fluxo Padrão";
            System.out.println(" displayName: "+displayName+" - conditionExpr: "+conditionExpr);
            writer.println(indent + "  - > 📜 **Regra de Negócio (Decisão): " + displayName + "**");
            if (hasExpr) {
                writer.println(indent + "    > ```javascript\n" + indent + "    > " + conditionExpr.replace("\n", "\n" + indent + "    > ") + "\n" + indent + "    > ```");
            }
        }
    }

    /**
     * **MÉTODO MODIFICADO**
     * Imprime uma referência a um subprocesso, indicando se ele é de um toolkit.
     * @param location O objeto ArtifactLocation do subprocesso referenciado.
     * @param indent String de indentação.
     */
    public void printReferenceToPrintedArtifact(ProcessLoader.ArtifactLocation location, String indent) {
        if (location == null || location.objectInfo == null) return;
        String icon = getIconForType(location.objectInfo.getType());
        String ancora = gerarAncora(location.objectInfo.getName());

        // --- NOVA LÓGICA ---
        String origemInfo = "";
        if (location.toolkitName != null && !location.toolkitName.isEmpty()) {
            origemInfo = " - *Toolkit: " + location.toolkitName + "*";
        }
        // --- FIM DA NOVA LÓGICA ---

        writer.println(indent + "- " + icon + " **" + location.objectInfo.getName() + "** (*Chamada de Serviço" + origemInfo + "*) - [Ver Detalhes Já Impressos](#" + ancora + ")");
    }

    public void printEtapaDoFluxo(Object node, String indent) {
        String icon = getIconForType(getNodeType(node));
        String name = getNodeName(node);
        String type = getNodeType(node);
        writer.println(indent + "- " + icon + " **" + name + "** (*" + type + "*)");
    }

    public void printReferenciaSubprocesso(PackageObject subprocesso, String indent) {
        if (subprocesso == null) return;
        String icon = getIconForType(subprocesso.getType());
        String ancora = gerarAncora(subprocesso.getName());
        writer.println(indent + "  - " + icon + " **" + subprocesso.getName() + "** (*Chamada de Serviço*) - [Ver Detalhes](#" + ancora + ")");
    }

    public void printDataObjects(Process process, String indent) {
        if (process.getIoSpecification() == null) return;
        List<DataInput> inputs = process.getIoSpecification().getDataInputs();
        if (inputs != null && !inputs.isEmpty()) {
            writer.println(indent + "\n**📥 Dados de Entrada:**");
            writer.println("| Nome da Variável | Tipo de Dado | É uma lista? |");
            writer.println("| :--- | :--- | :--- |");
            for (DataInput data : inputs) {
                writer.println("| `" + data.getName() + "` | `" + data.getItemSubjectRef() + "` | " + (data.getIsCollection() ? "Sim" : "Não") + " |");
            }
        }
        List<DataOutput> outputs = process.getIoSpecification().getDataOutputs();
        if (outputs != null && !outputs.isEmpty()) {
            writer.println(indent + "\n**📤 Dados de Saída:**");
            writer.println("| Nome da Variável | Tipo de Dado | É uma lista? |");
            writer.println("| :--- | :--- | :--- |");
            for (DataOutput data : outputs) {
                writer.println("| `" + data.getName() + "` | `" + data.getItemSubjectRef() + "` | " + (data.getIsCollection() ? "Sim" : "Não") + " |");
            }
        }
    }

    public void printLegacyVariables(List<ProcessParameter> params, List<ProcessVariable> vars, String indent) {
        if (params != null && !params.isEmpty()) {
            printSectionHeader("📥 Entradas e Saídas do Serviço", indent);
            writer.println("| Nome | Direção | Tipo | É uma lista? |");
            writer.println("| :--- | :--- | :--- | :--- |");
            for(ProcessParameter p : params){
                String direction = p.getParameterType() == 1 ? "Entrada" : "Saída";
                writer.println("| `" + p.getName() + "` | " + direction + " | `" + p.getClassId() + "` | " + (p.isArrayOf() ? "Sim" : "Não") + " |");
            }
        }
        if (vars != null && !vars.isEmpty()) {
            printSectionHeader("📦 Variáveis Internas (Privadas)", indent);
            writer.println("| Nome | Tipo | É uma lista? |");
            writer.println("| :--- | :--- | :--- |");
            for(ProcessVariable v : vars){
                writer.println("| `" + v.getName() + "` | `" + v.getClassId() + "` | " + (v.isArrayOf() ? "Sim" : "Não") + " |");
            }
        }
    }

    public void printFlowCondition(SequenceFlow flow, String indent) {
        String flowName = flow.getName();
        String conditionExpr = Optional.ofNullable(flow.getConditionExpression())
                .map(Expression::getExpression).map(String::trim).orElse(null);

        boolean hasName = flowName != null && !flowName.trim().isEmpty();
        boolean hasExpr = conditionExpr != null && !conditionExpr.isEmpty() && !"true".equalsIgnoreCase(conditionExpr);

        if (hasName || hasExpr) {
            String displayName = hasName ? flowName : "Fluxo Padrão";
            writer.println(indent + "  - > 📜 **Regra de Negócio (Decisão): " + displayName + "**");
            if(hasExpr){
                writer.println(indent + "    > ```javascript\n" + indent + "    > " + conditionExpr.replace("\n", "\n" + indent + "    > ") + "\n" + indent + "    > ```");
            }
        }
    }

    public void printScript(String script, String indent){
        if (script != null && !script.trim().isEmpty()) {
            writer.println(indent + "  - > **Regra de Negócio (Script):**");
            writer.println(indent + "    > ```javascript");
            for(String line : script.trim().split("\n")){
                writer.println(indent + "    > " + line.trim());
            }
            writer.println(indent + "    > ```");
        }
    }

    // --- Ponto de entrada para Coach MODERNO (de um FormTask)
    public void imprimirLayoutCoach(Layout layout, String indent) {
        if (layout == null || layout.getLayoutItems() == null || layout.getLayoutItems().isEmpty()) return;
        writer.println("\n" + indent + "##### 🖥️ Análise da Interface de Usuário (Coach)");
        for (LayoutItem item : layout.getLayoutItems()) {
            imprimirLayoutItem(item, indent);
        }
    }

    // --- Ponto de entrada para Coach LEGADO (de um serviço)
    public void imprimirLayoutCoach(CoachLayout layout, String indent) {
        if (layout == null || layout.getItems() == null || layout.getItems().isEmpty()) return;
        writer.println("\n" + indent + "##### 🖥️ Análise da Interface de Usuário (Coach)");
        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item : layout.getItems()) {
            imprimirLayoutItem(item, indent);
        }
    }

    // --- Método auxiliar para Coach MODERNO (coachng)
    private void imprimirLayoutItem(LayoutItem item, String indent) {
        if (item == null) return;
        String itemName = getCoachItemName(item);
        writer.println("\n" + indent + "- **Componente:** `" + itemName + "`");

        if (item.getBinding() != null && !item.getBinding().isEmpty()) {
            writer.println(indent + "  - **Dado Associado:** `" + item.getBinding() + "`");
        }

        if (item.getConfigData() != null) {
            for (ConfigData config : item.getConfigData()) {
                String optionName = config.getOptionName();
                String value = config.getValue();
                if (value == null || value.trim().isEmpty() || optionName == null) continue;

                if (optionName.startsWith("@label") && !"SHOW".equals(value) && !"HIDE".equals(value)) {
                    writer.println(indent + "  - **Rótulo (Label):** " + value);
                } else if (optionName.toLowerCase().startsWith("event.on")) {
                    writer.println(indent + "  - **Ação do Usuário (" + optionName.substring(6).toUpperCase() + "):**");
                    writer.println(indent + "    ```javascript\n" + indent + "    " + value.trim().replace("\n", "\n" + indent + "    ") + "\n" + indent + "    ```");
                }
            }
        }

        if (item.getContentBoxContribs() != null) {
            for (ContentBoxContrib contrib : item.getContentBoxContribs()) {
                if (contrib.getContributions() != null) {
                    writer.println(indent + "  - **Itens Aninhados em `" + contrib.getContentBoxId() + "`:**");
                    for (LayoutItem subItem : contrib.getContributions()) {
                        imprimirLayoutItem(subItem, indent + "    ");
                    }
                }
            }
        }
    }

    // --- MÉTODO CORRIGIDO: Auxiliar para Coach LEGADO (coach)
    private void imprimirLayoutItem(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item, String indent) {
        if (item == null) return;
        String itemName = getCoachItemName(item);

        writer.println("\n" + indent + "- **Componente:** `" + itemName + "`");

        if (item.getBinding() != null && !item.getBinding().isEmpty()) {
            writer.println(indent + "  - **Dado Associado:** `" + item.getBinding() + "`");
        }

        if (item.getConfigData() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData config : item.getConfigData()) {
                String optionName = config.getOptionName();
                String value = config.getValue();
                // --- Verificação de segurança adicionada ---
                if (value == null || value.trim().isEmpty() || optionName == null) continue;

                if (optionName.startsWith("@label") && !"SHOW".equals(value) && !"HIDE".equals(value)) {
                    writer.println(indent + "  - **Rótulo (Label):** " + value);
                } else if (optionName.toLowerCase().startsWith("event.on")) {
                    writer.println(indent + "  - **Ação do Usuário (" + optionName.substring(6).toUpperCase() + "):**");
                    writer.println(indent + "    ```javascript\n" + indent + "    " + value.trim().replace("\n", "\n" + indent + "    ") + "\n" + indent + "    ```");
                }
            }
        }

        if (item.getContentBoxContributions() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem.ContentBoxContribution contrib : item.getContentBoxContributions()) {
                if (contrib.getContributions() != null) {
                    writer.println(indent + "  - **Itens Aninhados:**");
                    for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem subItem : contrib.getContributions()) {
                        imprimirLayoutItem(subItem, indent + "    ");
                    }
                }
            }
        }
    }

    private String getCoachItemName(LayoutItem item){
        if (item == null) return "N/A";
        if(item.getConfigData() != null){
            Optional<String> label = item.getConfigData().stream()
                    .filter(c -> c.getOptionName() != null && "@label".equals(c.getOptionName()) && c.getValue() != null && !c.getValue().isEmpty())
                    .map(ConfigData::getValue)
                    .findFirst();
            if (label.isPresent()) return label.get();
        }
        return item.getLayoutItemId() != null ? item.getLayoutItemId() : "Sem ID";
    }

    private String getCoachItemName(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item) {
        if (item == null) return "N/A";
        if (item.getConfigData() != null) {
            Optional<String> label = item.getConfigData().stream()
                    .filter(c -> c.getOptionName() != null && c.getOptionName().equals("@label") && c.getValue() != null && !c.getValue().isEmpty())
                    .map(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData::getValue)
                    .findFirst();
            if (label.isPresent()) {
                return label.get();
            }
        }
        return item.getLayoutItemId() != null ? item.getLayoutItemId() : "Sem ID";
    }

    public void printMensagem(String mensagem, String indent) {
        writer.println(indent + mensagem);
    }

    public void printSubprocessStart(String name, String type, String indent) {
        String icon = getIconForType(type);
        String ancoraId = gerarAncora(name);
        writer.println(indent + "> <a id=\"" + ancoraId + "\"></a>➡️ **Iniciando " + formatarTipo(type) + ":** `" + name + "`");
    }

    public void printSubprocessEnd(String name, String indent) {
        writer.println(indent + "> ⬅️ **Retornando de:** `" + name + "`");
    }

    public void printCircularReference(String name, String indent) {
        writer.println(indent + "- ↪️ *(Referência circular para '" + name + "', expansão pulada)*");
    }

    private String getIndent(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < depth; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    private String gerarAncora(String nome) {
        return nome.toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "");
    }

    private String formatarTipo(String tipo) {
        if (tipo == null) return "N/A";
        switch (tipo.toLowerCase()) {
            case "bpd": return "Processo de Negócio (BPD)";
            case "process": return "Serviço (Humano ou de Sistema)";
            default: return tipo;
        }
    }

    private String getIconForType(String type) {
        if (type == null) return "❓";
        switch (type.toLowerCase()) {
            case "bpd": case "process": return "📑";
            case "subprocess": case "callactivity": case "chamada de serviço": return "➡️";
            case "formtask": case "coachng": case "tarefa de formulário": return "👩‍💻";
            case "script": case "scripttask": return "⚙️";
            case "switch": case "exclusivegateway": case "gateway": return "🔀";
            case "start": case "startevent": case "evento de início": return "▶️";
            case "exitpoint": case "endevent": case "evento de fim": return "⏹️";
            default: return "🔹";
        }
    }

    private String getNodeType(Object node) {
        if (node instanceof FlowObject) {
            FlowObject fo = (FlowObject) node;
            String componentType = fo.getComponentType();
            if ("Event".equals(componentType) && fo.getComponent() != null) {
                String eventType = fo.getComponent().getEventType();
                if ("1".equals(eventType)) return "Evento de Início";
                if ("2".equals(eventType)) return "Evento de Fim";
                return "Evento";
            }
            if ("Gateway".equals(componentType)) return "Gateway";
            if ("Activity".equals(componentType)) return "Chamada de Serviço";
            return componentType != null ? componentType : "Elemento";
        }
        if (node instanceof CallActivity) return "Chamada de Serviço";
        if (node instanceof SubProcess) return "Subprocesso Embutido";
        if (node instanceof ScriptTask) return "Tarefa de Script";
        if (node instanceof FormTask) return "Tarefa de Formulário";
        if (node instanceof Task) return "Tarefa";
        if (node instanceof StartEvent) return "Evento de Início";
        if (node instanceof EndEvent) return "Evento de Fim";
        if (node instanceof ExclusiveGateway) return "Gateway";
        if (node instanceof Item) return ((Item) node).getTWComponentName();
        return "Elemento";
    }

    private String getNodeName(Object node) {
        String name = null;
        if (node instanceof Item) {
            name = ((Item) node).getName();
        } else if (node instanceof FlowNode) {
            name = ((FlowNode) node).getName();
        } else if (node instanceof FlowObject) {
            name = ((FlowObject) node).getName();
        }
        return (name != null && !name.isEmpty()) ? name : "(Sem Nome)";
    }

    public void printBPMNParameterMappings(CallActivity callActivity, String indent) {
        if (callActivity == null) return;

        if (callActivity.getDataInputAssociations() != null && !callActivity.getDataInputAssociations().isEmpty()) {
            writer.println(indent + "  - > **Mapeamento de Entrada:**");
            for (DataInputAssociation mapping : callActivity.getDataInputAssociations()) {
                if (mapping.getAssignment() != null && mapping.getAssignment().getFrom() != null) {
                    writer.println(indent + "    - De: `" + mapping.getAssignment().getFrom().getExpression() + "` Para: `" + mapping.getTargetRef() + "`");
                }
            }
        }

        if (callActivity.getDataOutputAssociations() != null && !callActivity.getDataOutputAssociations().isEmpty()) {
            writer.println(indent + "  - > **Mapeamento de Saída:**");
            for (DataOutputAssociation mapping : callActivity.getDataOutputAssociations()) {
                if (mapping.getAssignment() != null && mapping.getAssignment().getTo() != null) {
                    writer.println(indent + "    - De: `" + mapping.getSourceRef() + "` Para: `" + mapping.getAssignment().getTo().getContent() + "`");
                }
            }
        }
    }
    public void printBpdAssignments(List<AssignmentBpd> assignments, String indent) {
        if (assignments != null && !assignments.isEmpty()) {
            writer.println(indent + "  - > **Mapeamento de Dados (Assignments):**");
            for (AssignmentBpd assignment : assignments) {
                writer.println(indent + "    - De: `" + assignment.getFrom() + "` Para: `" + assignment.getTo() + "`");
            }
        }
    }

    public void printBpdParameterMappings(Implementation implementation, String indent) {
        if (implementation == null) return;

        if (implementation.getInputMappings() != null && !implementation.getInputMappings().isEmpty()) {
            writer.println(indent + "  - > **Mapeamento de Entrada:**");
            for (InputActivityParameterMapping mapping : implementation.getInputMappings()) {
                writer.println(indent + "    - De: `" + mapping.getValue() + "` Para: `" + mapping.getName() + "`");
            }
        }

        if (implementation.getOutputMappings() != null && !implementation.getOutputMappings().isEmpty()) {
            writer.println(indent + "  - > **Mapeamento de Saída:**");
            for (OutputActivityParameterMapping mapping : implementation.getOutputMappings()) {
                writer.println(indent + "    - De: `" + mapping.getName() + "` Para: `" + mapping.getValue() + "`");
            }
        }
    }


}