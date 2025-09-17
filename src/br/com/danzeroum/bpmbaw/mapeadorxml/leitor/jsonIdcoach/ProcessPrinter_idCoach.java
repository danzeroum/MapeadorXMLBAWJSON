package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIdcoach;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.Layout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.ContentBoxContrib;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.ConfigData;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.common.Expression;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.pkg.PackageObject;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Link;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.ProcessParameter;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.ProcessVariable;

import java.io.PrintWriter;
import java.util.*;

/**
 * Classe especializada em gerar relatório Markdown do processo (BPD/BPMN/Coaches),
 * com navegação confiável por âncoras (#) e índice.
 *
 * Garantias:
 * - IDs de âncora estáveis (branch-*, artifact-*).
 * - Linhas de link sem indentação de 4+ espaços (para não virar code block).
 * - Índice e cabeçalhos usam a mesma estratégia de IDs.
 */
public class ProcessPrinter_idCoach {

    private final PrintWriter writer;

    /** Lista usada para montar o Índice (artefatos já detectados). */
    private final List<PackageObject> indiceArtefatos = new ArrayList<>();

    public ProcessPrinter_idCoach(PrintWriter writer) {
        this.writer = writer;
    }

    /* ======================================================================
       Seções de título / índice
       ====================================================================== */

    /** Título principal do relatório. */
    public void printFlowReportTitle(String title) {
        writer.println("# 🗺️ " + md(title));
        writer.println("---");
        writer.println();
    }

    /** Cabeçalho de seção para o fluxo do processo. */
    public void printFlowSectionHeader(String processName) {
        writer.println("## 🌊 Fluxo do Processo: " + md(processName));
        writer.println();
    }

    /** Cabeçalho genérico de subseção. */
    public void printSubSectionHeader(String title) {
        writer.println("### " + md(title));
        writer.println();
    }

    /** Registra um artefato para posterior emissão no índice. */
    public void registrarArtefatoParaIndice(PackageObject artefato) {
        if (artefato == null) return;
        boolean exists = indiceArtefatos.stream().anyMatch(a -> a != null && a.getId().equals(artefato.getId()));
        if (!exists) indiceArtefatos.add(artefato);
    }

    /** Emite o índice (sumário) com links para os artefatos. */
    public void printIndex() {
        if (indiceArtefatos.isEmpty()) return;

        writer.println("## Índice");
        writer.println();
        writer.println("| Nome do Artefato | Tipo | Link |");
        writer.println("| :--- | :--- | :--- |");

        indiceArtefatos.stream()
                .sorted(Comparator.comparing(PackageObject::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .forEach(po -> {
                    String name = safe(po.getName());
                    String type = formatarTipo(po.getType());
                    String aid  = anchorId("artifact", po.getId());
                    writer.println("| " + md(name) + " | " + md(type) + " | [Ver Detalhes](#" + aid + ") |");
                });

        writer.println();
    }

    /* ======================================================================
       Artefatos / âncoras / cabeçalhos
       ====================================================================== */

    /**
     * Cabeçalho de um artefato (com âncora explícita baseada em ID).
     * Retorna true se impresso.
     */
    public boolean printHeaderDoArtefato(ProcessLoader_idCoach.ArtifactLocation location) {
        if (location == null || location.objectInfo == null) return false;

        String name = safe(location.objectInfo.getName());
        String id   = safe(location.objectInfo.getId());
        String aid  = anchorId("artifact", id);

        // Âncora explícita (independe de renderer)
        writer.println("<span id=\"" + aid + "\"></span>");
        writer.println("### " + md(name));

        if (notBlank(location.toolkitName)) {
            writer.println("_Toolkit_: " + md(location.toolkitName) + "  ");
        }
        if (notBlank(location.filePath)) {
            writer.println("_Arquivo_: " + md(location.filePath));
        }
        writer.println();
        return true;
    }

    /**
     * Referência para um artefato já impresso (subprocesso/serviço), com link.
     */
    public void printReferenceToPrintedArtifact(ProcessLoader_idCoach.ArtifactLocation location, String indent) {
        if (location == null || location.objectInfo == null) return;

        String icon  = getIconForType(location.objectInfo.getType());
        String name  = safe(location.objectInfo.getName());
        String aid   = anchorId("artifact", location.objectInfo.getId());
        String origemInfo = notBlank(location.toolkitName) ? " - *Toolkit: " + md(location.toolkitName) + "*" : "";

        writer.println((indent != null ? indent : "")
                + "- " + icon + " **" + md(name) + "** (*Chamada de Serviço" + origemInfo + "*) - [Ver Detalhes](#" + aid + ")");
    }

    /**
     * Referência para subprocesso (PackageObject), com link.
     */
    public void printReferenciaSubprocesso(PackageObject subprocesso, String indent) {
        if (subprocesso == null) return;
        String icon = getIconForType(subprocesso.getType());
        String aid  = anchorId("artifact", subprocesso.getId());
        writer.println((indent != null ? indent : "")
                + "- " + icon + " **" + md(subprocesso.getName()) + "** (*Chamada de Serviço*) - [Ver Detalhes](#" + aid + ")");
    }

    /* ======================================================================
       Navegação entre "branches" (decisões/caminhos)
       ====================================================================== */

    /**
     * Início de um branch (caminho) com âncora baseada em processLinkId (estável).
     * Evita indentação inicial de 4+ espaços antes de links/âncoras.
     */
    public void printBranchStart(String nodeName,
                                 String branchName,
                                 String processLinkId,
                                 int depth,
                                 boolean isFirst) {

        String resolvedId = notBlank(processLinkId)
                ? processLinkId
                : (safe(nodeName) + "-" + safe(branchName));

        String aid = anchorId("branch", resolvedId);

        // Âncora explícita numa linha sem indentação proibitiva
        writer.println("<span id=\"" + aid + "\"></span>");

        // Cabeçalho do trecho (nível fixo para estabilidade visual)
        writer.println("#### " + md(safe(nodeName)) + " — " + md(ptYN(safe(branchName))));
        writer.println();
    }

    /**
     * Links de navegação "Anterior | Próximo" entre branches.
     * Ignora indent para não criar code blocks acidentalmente.
     */
    public void printBranchNavigationLinks(String currentName,
                                           String prevName, String prevId,
                                           String nextName, String nextId,
                                           String indent) {

        StringBuilder sb = new StringBuilder();

        if (notBlank(prevId)) {
            sb.append("[◀ ").append(md(ptYN(orElse(prevName, "Anterior"))))
                    .append("](#").append(anchorId("branch", prevId)).append(")");
        }
        if (notBlank(nextId)) {
            if (sb.length() > 0) sb.append("  |  ");
            sb.append("[").append(md(ptYN(orElse(nextName, "Próximo"))))
                    .append(" ▶](#").append(anchorId("branch", nextId)).append(")");
        }

        if (sb.length() > 0) {
            writer.println(sb.toString()); // sem 4 espaços à esquerda
            writer.println();
        }
    }

    public void printMergePoint(String nodeName, int depth) {
        writer.println(getIndent(depth) + "- ↪️ *(...junta-se ao fluxo em **" + md(ptYN(safe(nodeName))) + "**)*");
    }

    /* ======================================================================
       Utilidades de fluxo / regras / dados (opcionais, mantidas da sua base)
       ====================================================================== */

    public void printFlowStep(Object node, int depth) {
        String type = getNodeType(node);
        String name = getNodeName(node);
        String icon = getIconForType(type);

        if ("(Sem Nome)".equals(name)) {
            writer.println(getIndent(depth) + "- " + icon + " " + type);
        } else {
            writer.println(getIndent(depth) + "- " + icon + " **" + md(name) + "** (*" + md(type) + "*)");
        }
    }

    public void printLegacyFlowCondition(Flow flow, String indent) {
        String flowName = flow != null ? flow.getName() : null;
        String conditionExpr = null;

        if (flow != null && flow.getConnection() != null && flow.getConnection().getCondition() != null) {
            conditionExpr = flow.getConnection().getCondition().getExpression();
        }

        boolean hasName = notBlank(flowName);
        boolean hasExpr = notBlank(conditionExpr);

        if (hasName || hasExpr) {
            String displayName = hasName ? ptYN(flowName) : "Fluxo Padrão";
            writer.println(orElse(indent, "") + "  - > 📜 **Regra de Negócio (Decisão): " + md(displayName) + "**");
            if (hasExpr) {
                writer.println(orElse(indent, "") + "    > ```javascript");
                writer.println(orElse(indent, "") + "    > " + conditionExpr.replace("\n", "\n" + orElse(indent, "") + "    > "));
                writer.println(orElse(indent, "") + "    > ```");
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
    public void printLegacyServiceCondition(Link link, String expression, String indent) {
        String flowName = link != null ? link.getName() : null;
        boolean hasName = notBlank(flowName);
        boolean hasExpr = notBlank(expression);

        if (hasName || hasExpr) {
            String displayName = hasName ? flowName : "Condição";
            writer.println(orElse(indent, "") + "  - > 📜 **Regra de Negócio (Decisão): " + md(displayName) + "**");
            if (hasExpr) {
                writer.println(orElse(indent, "") + "    > ```javascript");
                writer.println(orElse(indent, "") + "    > " + expression.replace("\n", "\n" + orElse(indent, "") + "    > "));
                writer.println(orElse(indent, "") + "    > ```");
            }
        }
    }

    /** Versão moderna para BPMN SequenceFlow. */
    public void printModernFlowCondition(SequenceFlow flow, String indent) {
        String flowName = flow != null ? flow.getName() : null;
        String conditionExpr = Optional.ofNullable(flow)
                .map(SequenceFlow::getConditionExpression)
                .map(Expression::getExpression)
                .map(String::trim)
                .orElse(null);

        boolean hasName = notBlank(flowName);
        boolean hasExpr = notBlank(conditionExpr) && !"true".equalsIgnoreCase(conditionExpr);

        if (hasName || hasExpr) {
            String displayName = hasName ? flowName : "Fluxo Padrão";
            writer.println(orElse(indent, "") + "  - > 📜 **Regra de Negócio (Decisão): " + md(displayName) + "**");
            if (hasExpr) {
                writer.println(orElse(indent, "") + "    > ```javascript");
                writer.println(orElse(indent, "") + "    > " + conditionExpr.replace("\n", "\n" + orElse(indent, "") + "    > "));
                writer.println(orElse(indent, "") + "    > ```");
            }
        }
    }

    public void printLegacyVariables(List<ProcessParameter> params, List<ProcessVariable> vars, String indent) {
        if (params != null && !params.isEmpty()) {
            writer.println(orElse(indent, "") + "#### 📥 Entradas e Saídas do Serviço");
            writer.println("| Nome | Direção | É uma lista? |");
            writer.println("| :--- | :--- | :--- |");
            for (ProcessParameter p : params) {
                String direction = p.getParameterType() == 1 ? "Entrada" : "Saída";
                writer.println("| `" + p.getName() + "` | " + direction + " | " + (p.isArrayOf() ? "Sim" : "Não") + " |");
            }
            writer.println();
        }
        if (vars != null && !vars.isEmpty()) {
            writer.println(orElse(indent, "") + "#### 📦 Variáveis Internas (Privadas)");
            writer.println("| Nome | É uma lista? |");
            writer.println("| :--- | :--- |");
            for (ProcessVariable v : vars) {
                writer.println("| `" + v.getName() + "` | " + (v.isArrayOf() ? "Sim" : "Não") + " |");
            }
            writer.println();
        }
    }

    public void printDataObjects(Process process, String indent) {
        if (process == null || process.getIoSpecification() == null) return;

        List<DataInput> inputs = process.getIoSpecification().getDataInputs();
        if (inputs != null && !inputs.isEmpty()) {
            writer.println(orElse(indent, "") + "#### 📥 Dados de Entrada");
            writer.println("| Nome da Variável | Tipo de Dado | É uma lista? |");
            writer.println("| :--- | :--- | :--- |");
            for (DataInput data : inputs) {
                writer.println("| `" + data.getName() + "` | `" + data.getItemSubjectRef() + "` | " + (data.getIsCollection() ? "Sim" : "Não") + " |");
            }
            writer.println();
        }

        List<DataOutput> outputs = process.getIoSpecification().getDataOutputs();
        if (outputs != null && !outputs.isEmpty()) {
            writer.println(orElse(indent, "") + "#### 📤 Dados de Saída");
            writer.println("| Nome da Variável | Tipo de Dado | É uma lista? |");
            writer.println("| :--- | :--- | :--- |");
            for (DataOutput data : outputs) {
                writer.println("| `" + data.getName() + "` | `" + data.getItemSubjectRef() + "` | " + (data.getIsCollection() ? "Sim" : "Não") + " |");
            }
            writer.println();
        }
    }

    public void printBPMNParameterMappings(CallActivity callActivity, String indent) {
        if (callActivity == null) return;

        if (callActivity.getDataInputAssociations() != null && !callActivity.getDataInputAssociations().isEmpty()) {
            writer.println(orElse(indent, "") + "  - > **Mapeamento de Entrada:**");
            for (DataInputAssociation mapping : callActivity.getDataInputAssociations()) {
                if (mapping.getAssignment() != null && mapping.getAssignment().getFrom() != null) {
                    writer.println(orElse(indent, "") + "    - De: `" + mapping.getAssignment().getFrom().getExpression() + "` Para: `" + mapping.getTargetRef() + "`");
                }
            }
        }

        if (callActivity.getDataOutputAssociations() != null && !callActivity.getDataOutputAssociations().isEmpty()) {
            writer.println(orElse(indent, "") + "  - > **Mapeamento de Saída:**");
            for (DataOutputAssociation mapping : callActivity.getDataOutputAssociations()) {
                if (mapping.getAssignment() != null && mapping.getAssignment().getTo() != null) {
                    writer.println(orElse(indent, "") + "    - De: `" + mapping.getSourceRef() + "` Para: `" + mapping.getAssignment().getTo().getContent() + "`");
                }
            }
        }
    }

    public void printBpdAssignments(List<AssignmentBpd> assignments, String indent) {
        if (assignments == null || assignments.isEmpty()) return;
        writer.println(orElse(indent, "") + "  - > **Mapeamento de Dados (Assignments):**");
        for (AssignmentBpd a : assignments) {
            writer.println(orElse(indent, "") + "    - De: `" + a.getFrom() + "` Para: `" + a.getTo() + "`");
        }
    }

    public void printBpdParameterMappings(Implementation implementation, String indent) {
        if (implementation == null) return;

        if (implementation.getInputMappings() != null && !implementation.getInputMappings().isEmpty()) {
            writer.println(orElse(indent, "") + "  - > **Mapeamento de Entrada:**");
            for (InputActivityParameterMapping m : implementation.getInputMappings()) {
                writer.println(orElse(indent, "") + "    - De: `" + m.getValue() + "` Para: `" + m.getName() + "`");
            }
        }

        if (implementation.getOutputMappings() != null && !implementation.getOutputMappings().isEmpty()) {
            writer.println(orElse(indent, "") + "  - > **Mapeamento de Saída:**");
            for (OutputActivityParameterMapping m : implementation.getOutputMappings()) {
                writer.println(orElse(indent, "") + "    - De: `" + m.getName() + "` Para: `" + m.getValue() + "`");
            }
        }
    }

    /* ======================================================================
       Helpers / utilidades internas
       ====================================================================== */

    /** Escapa caracteres que podem quebrar o Markdown. */
    private static String md(String s) {
        if (s == null) return "";
        // Escapamos só colchetes para não quebrar [links]; deixamos parênteses visíveis.
        return s.replace("[", "\\[").replace("]", "\\]");
    }

    // novo: normaliza rótulos de ramos para PT
    private static String ptYN(String s) {
        if (s == null) return "Sem rótulo";
        String t = s.trim();
        if (t.equalsIgnoreCase("yes")) return "Sim";
        if (t.equalsIgnoreCase("no"))  return "Não";
        if (t.equalsIgnoreCase("sem titulo") || t.equalsIgnoreCase("sem título") || t.isEmpty()) return "Sem rótulo";
        return t;
    }

    // novo: legenda de ícones para leitores de negócio
    public void printReportLegend() {
        writer.println("> **Legenda**: ▶️ Início  |  ⏹️ Fim  |  🔀 Decisão  |  📑 Chamada de Serviço  |  🔹 Subprocesso  |  ⚙️ Script  |  ↪️ Junção");
        writer.println();
    }

    /** Gera IDs do tipo kind-<slug>, ex.: artifact-abc123, branch-gateway-x. */
    private static String anchorId(String kind, String rawId) {
        String base = safe(rawId).toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\-]+", "-")
                .replaceAll("(^-+|-+$)", "");
        return kind + "-" + (base.isEmpty() ? "id" : base);
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String orElse(String s, String fallback) {
        return notBlank(s) ? s : fallback;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private String getIndent(int depth) {
        if (depth <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) sb.append("  ");
        return sb.toString();
    }

    private String formatarTipo(String tipo) {
        if (tipo == null) return "N/A";
        switch (tipo.toLowerCase(Locale.ROOT)) {
            case "bpd":     return "Processo de Negócio (BPD)";
            case "process": return "Serviço (Humano ou de Sistema)";
            default:        return tipo;
        }
    }

    private String getIconForType(String type) {
        if (type == null) return "❓";
        switch (type.toLowerCase(Locale.ROOT)) {
            case "bpd":
            case "process":          return "📑";
            case "subprocess":
            case "callactivity":
            case "chamada de serviço": return "➡️";
            case "formtask":
            case "coachng":
            case "tarefa de formulário": return "👩‍💻";
            case "script":
            case "scripttask":       return "⚙️";
            case "switch":
            case "exclusivegateway":
            case "gateway":          return "🔀";
            case "start":
            case "startevent":
            case "evento de início": return "▶️";
            case "exitpoint":
            case "endevent":
            case "evento de fim":    return "⏹️";
            default:                 return "🔹";
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
        if (node instanceof CallActivity)   return "Chamada de Serviço";
        if (node instanceof SubProcess)     return "Subprocesso Embutido";
        if (node instanceof ScriptTask)     return "Tarefa de Script";
        if (node instanceof FormTask)       return "Tarefa de Formulário";
        if (node instanceof Task)           return "Tarefa";
        if (node instanceof StartEvent)     return "Evento de Início";
        if (node instanceof EndEvent)       return "Evento de Fim";
        if (node instanceof ExclusiveGateway) return "Gateway";
        if (node instanceof Item)           return ((Item) node).getTWComponentName();
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
        return (notBlank(name)) ? name : "(Sem Nome)";
    }
}
