package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIdcoach;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.ConfigData;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.ContentBoxContrib;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.Layout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.CoachNGBoundaryEvents;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.ProcessPrePost;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CoachReportPrinter_idCoach {

    private final PrintWriter writer;
    private final ProcessLoader_idCoach loader;
    private final Set<String> nomesPadrao;

    // ===== KPIs/Agregações para resumo e matriz =====
    private final Map<String, Integer> kpiScripts = new LinkedHashMap<>(); // ON_LOAD/ON_CLICK/Expression/Script(...)
    private final Map<String, Integer> bindingFreq = new LinkedHashMap<>();
    private final Set<String> telas = new LinkedHashSet<>();
    private final Map<String, Set<String>> telaToCustomCvNames = new LinkedHashMap<>();
    private final Map<String, Integer> cvCustomUsage = new LinkedHashMap<>();
    private final Set<String> customCvIdsSeen = new LinkedHashSet<>();
    private final Deque<String> telaStack = new ArrayDeque<>();

    private static final int COLLAPSE_THRESHOLD = 180;

    // Construtor novo (recomendado)
    public CoachReportPrinter_idCoach(PrintWriter writer, ProcessLoader_idCoach loader, Set<String> nomesPadrao) {
        this.writer = writer;
        this.loader = loader;
        this.nomesPadrao = (nomesPadrao != null) ? new HashSet<>(nomesPadrao) : Collections.emptySet();
    }
    // Construtor antigo (compat)
    public CoachReportPrinter_idCoach(PrintWriter writer) { this(writer, null, Collections.emptySet()); }

    /* ====================== SEÇÕES E TÍTULOS ====================== */

    public void printEmbeddedCoachSectionHeader(String serviceName, String coachName, String indent) {
        indent = "";
        // Contexto de tela para matrizes/resumo
        String telaKey = (serviceName != null ? serviceName : "(Sem serviço)") + " → " + (coachName != null ? coachName : "(Sem nome)");
        telas.add(telaKey);
        telaStack.push(telaKey);

        writer.println("\n---\n");
        writer.println(indent + "## 🖥️ Análise da Interface: " + (coachName != null ? coachName : "(Sem nome)"));
        writer.println(indent + "**Serviço Pai:** " + (serviceName != null ? serviceName : "(Desconhecido)"));
    }
    private void closeEmbeddedCoachSectionHeader() {
        if (!telaStack.isEmpty()) telaStack.pop();
    }
    private String currentTela() {
        return telaStack.isEmpty() ? "(Tela desconhecida)" : telaStack.peek();
    }

    /* ====================== LAYOUT MODERNO (coachng) ====================== */

    public void imprimirLayoutCoach(Layout layout, String serviceName, String coachName, String indent, Set<String> coachViewIds) {
        indent = "";
        if (layout == null || layout.getLayoutItems() == null || layout.getLayoutItems().isEmpty()) return;
        printEmbeddedCoachSectionHeader(serviceName, coachName, indent);
        writer.println("\n" + indent + "### Estrutura dos Componentes");
        for (LayoutItem item : layout.getLayoutItems()) {
            imprimirLayoutItem(item, indent, coachViewIds);
        }
        closeEmbeddedCoachSectionHeader();
    }

    private void imprimirLayoutItem(LayoutItem item, String indent, Set<String> coachViewIds) {
        indent = "";
        if (item == null) return;
        String itemName = getCoachItemName(item);
        writer.println("\n" + indent + "- **Componente:** `" + itemName + "`");

        if (notBlank(item.getViewUUID())) {
            ResolvedView rv = resolveView(item.getViewUUID());
            if (rv.isResolved && isPadrao(rv.name)) {
                writer.println(indent + "  - **Tipo de View (Padrão):** `" + rv.name + "`");
            } else {
                writer.println(indent + "  - **Tipo de View (ID):** `" + rv.id + "`");
                if (rv.isResolved && notBlank(rv.name)) {
                    writer.println(indent + "    - **Nome da View:** `" + rv.name + "`");
                }
                // só adiciona se NÃO for padrão
                if (!isPadrao(rv.name)) {
                    coachViewIds.add(rv.id);
                    // KPIs
                    customCvIdsSeen.add(rv.id);
                    if (notBlank(rv.name)) {
                        cvCustomUsage.merge(rv.name, 1, Integer::sum);
                        telaToCustomCvNames.computeIfAbsent(currentTela(), k -> new LinkedHashSet<>()).add(rv.name);
                    }
                }
            }
        } else {
            writer.println(indent + "  - **Tipo de View (ID):** `N/A - Componente Customizado (ex: CustomHTML)`");
        }

        imprimirDetalhesComuns(item.getBinding(), item.getConfigData(), indent);

        if (isCustomHtmlModern(item.getConfigData())) {
            imprimirCustomHtmlContentModern(item.getConfigData(), indent + "  ");
        }

        if (item.getContentBoxContribs() != null) {
            for (ContentBoxContrib contrib : item.getContentBoxContribs()) {
                if (contrib.getContributions() != null && !contrib.getContributions().isEmpty()) {
                    writer.println(indent + "  - **Itens Aninhados em `" + contrib.getContentBoxId() + "`:**");
                    for (LayoutItem subItem : contrib.getContributions()) {
                        imprimirLayoutItem(subItem, indent + "      ", coachViewIds);
                    }
                }
            }
        }
    }

    /* ====================== LAYOUT LEGADO (coach) ====================== */

    public void imprimirLayoutCoach(CoachLayout layout, String serviceName, String coachName, String indent, Set<String> coachViewIds) {
        indent = "";
        if (layout == null || layout.getItems() == null || layout.getItems().isEmpty()) return;
        printEmbeddedCoachSectionHeader(serviceName, coachName, indent);
        writer.println("\n" + indent + "### Estrutura dos Componentes");
        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item : layout.getItems()) {
            imprimirLayoutItem(item, indent, coachViewIds);
        }
        closeEmbeddedCoachSectionHeader();
    }

    private void imprimirLayoutItem(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item,
                                    String indent, Set<String> coachViewIds) {
        indent = "";
        if (item == null) return;
        String itemName = getCoachItemName(item);
        writer.println("\n" + indent + "- **Componente:** `" + itemName + "`");

        if (notBlank(item.getViewUUID())) {
            ResolvedView rv = resolveView(item.getViewUUID());
            if (rv.isResolved && isPadrao(rv.name)) {
                writer.println(indent + "  - **Tipo de View (Padrão):** `" + rv.name + "`");
            } else {
                writer.println(indent + "  - **Tipo de View (ID):** `" + rv.id + "`");
                if (rv.isResolved && notBlank(rv.name)) {
                    writer.println(indent + "    - **Nome da View:** `" + rv.name + "`");
                }
                if (!isPadrao(rv.name)) {
                    coachViewIds.add(rv.id);
                    // KPIs
                    customCvIdsSeen.add(rv.id);
                    if (notBlank(rv.name)) {
                        cvCustomUsage.merge(rv.name, 1, Integer::sum);
                        telaToCustomCvNames.computeIfAbsent(currentTela(), k -> new LinkedHashSet<>()).add(rv.name);
                    }
                }
            }
        } else {
            writer.println(indent + "  - **Tipo de View (ID):** `N/A - Componente Customizado (ex: CustomHTML)`");
        }

        imprimirDetalhesComuns(item.getBinding(), item.getConfigData(), indent);

        if (isCustomHtmlLegacy(item.getConfigData())) {
            imprimirCustomHtmlContentLegacy(item.getConfigData(), indent + "  ");
        }

        if (item.getContentBoxContributions() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem.ContentBoxContribution contrib : item.getContentBoxContributions()) {
                if (contrib.getContributions() != null && !contrib.getContributions().isEmpty()) {
                    writer.println(indent + "  - **Itens Aninhados:**");
                    for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem subItem : contrib.getContributions()) {
                        imprimirLayoutItem(subItem, indent + "      ", coachViewIds);
                    }
                }
            }
        }
    }

    /* ====================== DETALHES COMUNS (binding/config) ====================== */

    private <T> void imprimirDetalhesComuns(String binding, List<T> configs, String indent) {
        indent = "";
        if (notBlank(binding)) {
            writer.println(indent + "  - **Dado Associado (Binding):** `" + binding + "`");
            // KPI
            bindingFreq.merge(binding, 1, Integer::sum);
        }
        if (configs != null && !configs.isEmpty()) {
            writer.println(indent + "  - **Configurações e Regras:**");
            for (T cfg : configs) {
                if (cfg instanceof ConfigData) {
                    ConfigData c = (ConfigData) cfg;
                    imprimirConfigDataDetalhado(c.getOptionName(), c.getValue(), indent + "    ");
                } else if (cfg instanceof br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData) {
                    br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData c =
                            (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData) cfg;
                    imprimirConfigDataDetalhado(c.getOptionName(), c.getValue(), indent + "    ");
                }
            }
        }
    }

    private void imprimirConfigDataDetalhado(String optionName, String value, String indent) {
        indent = "";
        if (!notBlank(value) || optionName == null) return;

        // Ignorar label e HTML brutos (impressos em seções próprias)
        if ("@label".equals(optionName) || "@customHTML.textContent".equals(optionName)) return;

        String formattedName = formatarOptionName(optionName);
        String lower = optionName.toLowerCase(Locale.ROOT);

        // 1) Eventos → JS direto (com colapso automático)
        if (lower.startsWith("event.on")) {
            kpiScripts.merge("ON_" + eventSuffix(optionName), 1, Integer::sum);
            printScriptProperty(formattedName, value.trim(), "javascript", indent);
            return;
        }

        // 2) Casos conhecidos com JSON (Visibility/Flow/Width/Align/etc.)
        if (looksJson(value)) {
            if (tryPrintResponsive(formattedName, value, indent)) return; // imprimiu tabela
            // se não for responsivo, tentar pretty e colapsar
            String pretty = prettyJson(value);
            printScriptProperty(formattedName, pretty, "json", indent);
            return;
        }

        // 3) Fallback: simples
        writer.println(indent + "- **" + formattedName + ":** `" + value + "`");
    }

    private void imprimirCustomHtmlContentLegacy(List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData> configs, String indent) {
        indent = "";
        configs.stream()
                .filter(c -> "@customHTML.textContent".equals(c.getOptionName()) && notBlank(c.getValue()))
                .findFirst()
                .ifPresent(c -> {
                    writer.println("- **Conteúdo HTML/CSS:**");
                    printCodeBlock("html", c.getValue().trim().replace("&quot;", "\"").replace("&#xD;", ""), "  ");
                });
    }

    private void imprimirCustomHtmlContentModern(List<ConfigData> configs, String indent) {
        indent = "";
        configs.stream()
                .filter(c -> "@customHTML.textContent".equals(c.getOptionName()) && notBlank(c.getValue()))
                .findFirst()
                .ifPresent(c -> {
                    writer.println( "- **Conteúdo HTML/CSS:**");
                    printCodeBlock("html", c.getValue().trim().replace("&quot;", "\"").replace("&#xD;", ""), "  ");
                });
    }

    /* ====================== PRÉ-EXECUÇÃO (ProcessPrePosts) ====================== */

    public void imprimirScriptsPreExecucao(Item item, String indent) {
        indent = "";
        if (item == null) return;
        List<ProcessPrePost> list = item.getProcessPrePosts();
        if (list == null || list.isEmpty()) return;

        Set<String> dedup = new HashSet<>();
        for (ProcessPrePost pp : list) {
            if (pp == null) continue;

            String onLoad  = tryGetString(pp, "getOnLoad", "getOnload", "getOnLoadScript", "getOnLoadCode");
            String onClick = tryGetString(pp, "getOnClick", "getOnclick", "getOnClickScript", "getOnClickCode");
            String expr    = tryGetString(pp, "getExpression", "getExpressionScript", "getExpr");

            if (notBlank(onLoad))  { kpiScripts.merge("ON_LOAD", 1, Integer::sum);  printOnce(dedup, "Event ON_LOAD",  onLoad,  "javascript", indent); }
            if (notBlank(onClick)) { kpiScripts.merge("ON_CLICK", 1, Integer::sum); printOnce(dedup, "Event ON_CLICK", onClick, "javascript", indent); }
            if (notBlank(expr))    { kpiScripts.merge("Expression", 1, Integer::sum); printOnce(dedup, "Expression", expr, "javascript", indent); }

            for (Method m : pp.getClass().getMethods()) {
                if (m.getParameterCount() != 0) continue;
                if (!String.class.equals(m.getReturnType())) continue;
                String name = m.getName();
                if ("getClass".equals(name)) continue;
                try {
                    Object val = m.invoke(pp);
                    if (val == null) continue;
                    String s = String.valueOf(val);
                    if (!notBlank(s)) continue;
                    String lower = name.toLowerCase(Locale.ROOT);
                    String label = null;
                    if (lower.contains("onclick"))      { label = "Event ON_CLICK"; kpiScripts.merge("ON_CLICK", 1, Integer::sum); }
                    else if (lower.contains("onload"))  { label = "Event ON_LOAD";  kpiScripts.merge("ON_LOAD", 1, Integer::sum); }
                    else if (lower.contains("expr"))    { label = "Expression";     kpiScripts.merge("Expression", 1, Integer::sum); }
                    else if (lower.contains("script"))  { label = "Script (" + name + ")"; kpiScripts.merge("Script", 1, Integer::sum); }
                    if (label != null) printOnce(dedup, label, s, "javascript", indent);
                } catch (Exception ignore) { }
            }
        }
    }

    private void printOnce(Set<String> dedup, String label, String code, String lang, String indent) {
        indent = "";
        String key = label + "::" + code.hashCode();
        if (dedup.add(key)) printScriptProperty(label, code, lang, indent);
    }

    private String tryGetString(Object target, String... candidates) {
        for (String name : candidates) {
            try {
                Method m = target.getClass().getMethod(name);
                Object val = m.invoke(target);
                if (val != null) return String.valueOf(val);
            } catch (Exception ignore) { }
        }
        return null;
    }

    /* ====================== BOUNDARY EVENTS ====================== */

    public void imprimirEventosDeBoundary(Item item, String indent) {
        indent = "";
        if (item == null || item.getTwComponent() == null) return;
        List<CoachNGBoundaryEvents> events = item.getTwComponent().getCoachNGBoundaryEvents();
        if (events == null || events.isEmpty()) return;

        writer.println("\n" + indent + "### ⚡ Eventos e Navegação (Boundary Events)");
        writer.println(indent + "| Elemento da Interface (View Path) | Evento Disparado | Valida a Tela? |");
        writer.println(indent + "| :--- | :--- | :--- |");
        for (CoachNGBoundaryEvents e : events) {
            writer.println(indent + "| `" + e.getViewPath() + "` | `" + e.getEventLabel() + "` | " +
                    (e.getFireValidation() == 1 ? "Sim" : "Não") + " |");
        }
    }

    /* ====================== COACH VIEW DETALHES + RESUMO ====================== */

    public void imprimirDetalhesDeTodasAsCoachViews(ProcessLoader_idCoach loader, Set<String> coachViewIds) {
        // ---- Resumo executivo + matriz (Item 4) ----
        printExecutiveSummaryAndMatrix();

        if (coachViewIds == null || coachViewIds.isEmpty()) return;

        writer.println("\n\n---\n");
        writer.println("## 📚 Análise Detalhada das Coach Views Utilizadas");

        for (String cvId : coachViewIds) {
            Object artifact = loader.getArtefatoDoCache(cvId);
            if (artifact instanceof Teamworks) {
                CoachView cv = ((Teamworks) artifact).getCoachView();
                if (cv != null && !isPadrao(cv.getName())) {
                    writer.println("\n### 🎨 Coach View: " + cv.getName());
                    writer.println("**ID:** `" + cv.getId() + "`");
                    printCoachViewBindings(cv, "");
                    printCoachViewConfigOptions(cv, "");
                    printCoachViewAmdDependencies(cv, "");
                    printCoachViewInlineScripts(cv, "");
                }
            }
        }
    }

    private void printExecutiveSummaryAndMatrix() {
        writer.println("\n---\n");
        writer.println("# 📌 Resumo Executivo");
        writer.println();
        writer.println("- **Quantidade de telas analisadas:** " + telas.size());
        writer.println("- **Coach Views customizadas distintas:** " + customCvIdsSeen.size());

        // Top 5 CVs custom por uso
        if (!cvCustomUsage.isEmpty()) {
            writer.println("- **Top 5 Coach Views custom:**");
            cvCustomUsage.entrySet().stream()
                    .sorted((a,b) -> Integer.compare(b.getValue(), a.getValue()))
                    .limit(5)
                    .forEach(e -> writer.println("  - `" + e.getKey() + "` — " + e.getValue() + " uso(s)"));
        }

        // Scripts
        if (!kpiScripts.isEmpty()) {
            writer.println("- **Scripts detectados:**");
            kpiScripts.forEach((k,v) -> writer.println("  - " + k + ": " + v));
        }

        // Bindings mais frequentes (Top 5)
        if (!bindingFreq.isEmpty()) {
            writer.println("- **Bindings mais frequentes:**");
            bindingFreq.entrySet().stream()
                    .sorted((a,b) -> Integer.compare(b.getValue(), a.getValue()))
                    .limit(5)
                    .forEach(e -> writer.println("  - `" + e.getKey() + "` — " + e.getValue() + " ocorrência(s)"));
        }

        // Matriz Tela ↔ CV Custom
        writer.println("\n## 🧭 Matriz Tela ↔ Coach View Custom");
        writer.println("| Tela/Serviço | Coach Views Custom |");
        writer.println("| :--- | :--- |");
        for (String tela : telas) {
            Set<String> cvs = telaToCustomCvNames.getOrDefault(tela, Collections.emptySet());
            String list = cvs.isEmpty() ? "—" : cvs.stream().map(n -> "`" + n + "`").collect(Collectors.joining(", "));
            writer.println("| " + tela + " | " + list + " |");
        }
    }

    public void printCoachViewBindings(CoachView cv, String indent) {
        indent = "";
        if (cv.getBindingTypes() != null && !cv.getBindingTypes().isEmpty()) {
            writer.println(indent + "\n#### 🔗 Binding de Dados Principal");
            writer.println(indent + "| Nome | Tipo de Dado | É uma Lista? |");
            writer.println(indent + "| :--- | :--- | :--- |");
            for (BindingType binding : cv.getBindingTypes()) {
                writer.println(indent + "| `" + binding.getName() + "` | `" + binding.getClassId() + "` | " +
                        (binding.isList() ? "Sim" : "Não") + " |");
            }
        }
    }

    public void printCoachViewConfigOptions(CoachView cv, String indent) {
        indent = "";
        if (cv.getConfigOptions() != null && !cv.getConfigOptions().isEmpty()) {
            writer.println(indent + "\n#### ⚙️ Opções de Configuração");
            writer.println(indent + "| Nome | Rótulo (Label) | Tipo |");
            writer.println(indent + "| :--- | :--- | :--- |");
            for (ConfigOption option : cv.getConfigOptions()) {
                writer.println(indent + "| `" + option.getName() + "` | " + option.getLabel() + " | `" +
                        option.getPropertyType() + "` |");
            }
        }
    }

    public void printCoachViewAmdDependencies(CoachView cv, String indent) {
        indent = "";
        if (cv.getAmdDependencies() != null && !cv.getAmdDependencies().isEmpty()) {
            writer.println(indent + "\n#### 📦 Dependências de Módulos (AMD)");
            writer.println(indent + "| Módulo | Alias |");
            writer.println(indent + "| :--- | :--- |");
            for (AmdDependency dep : cv.getAmdDependencies()) {
                writer.println(indent + "| `" + dep.getModuleId() + "` | `" + dep.getFunctionArgument() + "` |");
            }
        }
    }

    public void printCoachViewInlineScripts(CoachView cv, String indent) {
        indent = "";
        if (cv.getInlineScripts() != null && !cv.getInlineScripts().isEmpty()) {
            writer.println(indent + "\n#### 📜 Lógica Interna (Behavior e Eventos)");
            for (InlineScript script : cv.getInlineScripts()) {
                writer.println(indent + "- **Tipo de Script: " + script.getScriptType().toUpperCase() +
                        " (Nome: " + script.getName() + ")**");
                printCodeBlock("javascript", script.getScriptBlock(), indent + "  ");
            }
        }
    }

    /* ====================== HELPERS ====================== */

    private boolean isPadrao(String name) {
        return name != null && nomesPadrao.contains(name);
    }

    private ResolvedView resolveView(String uuid) {
        String id = uuid;
        String name = null;
        boolean resolved = false;

        if (loader != null && notBlank(uuid)) {
            String clean = safeCleanId(uuid);
            try { loader.loadArtefatoSeNaoExistir(clean); } catch (Exception ignore) { }
            Object artifact = loader.getArtefatoDoCache(clean);
            if (artifact instanceof Teamworks) {
                CoachView cv = ((Teamworks) artifact).getCoachView();
                if (cv != null) {
                    id = clean;
                    name = cv.getName();
                    resolved = true;
                }
            }
        }
        return new ResolvedView(id, name, resolved);
    }

    private String safeCleanId(String id) {
        try {
            Method m = loader.getClass().getMethod("getCleanId", String.class);
            Object v = m.invoke(loader, id);
            if (v != null) return String.valueOf(v);
        } catch (Exception ignore) { }
        return id;
    }

    private static class ResolvedView {
        final String id; final String name; final boolean isResolved;
        ResolvedView(String id, String name, boolean resolved) { this.id = id; this.name = name; this.isResolved = resolved; }
    }

    // ===== Item (1): formatarOptionName melhorado (eventos e siglas) =====
    private String formatarOptionName(String optionName) {
        if (optionName == null) return "";
        String on = optionName;

        // Eventos: event.onX → Event ON_X
        String lower = on.toLowerCase(Locale.ROOT);
        if (lower.startsWith("event.on")) {
            return "Event ON_" + eventSuffix(on).toUpperCase(Locale.ROOT);
        }

        // Remove '@' e normaliza underscores para espaços
        if (on.startsWith("@")) on = on.substring(1);
        on = on.replace('_', ' ');

        // Quebrar somente antes de "Aa" (evita explodir siglas)
        String[] tokens = on.split("(?=(?<!^)[A-Z][a-z])|\\s+");
        return Arrays.stream(tokens)
                .filter(t -> !t.isEmpty())
                .map(t -> t.substring(0,1).toUpperCase() + t.substring(1))
                .collect(Collectors.joining(" "));
    }
    private String eventSuffix(String optionName) {
        // "event.onClick" -> "click"; "event.onLoad" -> "load"
        String s = optionName.substring(optionName.toLowerCase(Locale.ROOT).indexOf("on")+2);
        s = s.replaceAll("[^A-Za-z]", "");
        return s;
    }

    private boolean isCustomHtmlModern(List<ConfigData> configs) {
        return configs != null && configs.stream().anyMatch(c -> "@customHTML.textContent".equals(c.getOptionName()));
    }
    private boolean isCustomHtmlLegacy(List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData> configs) {
        return configs != null && configs.stream().anyMatch(c -> "@customHTML.textContent".equals(c.getOptionName()));
    }

    // ===== Item (3): blocos longos colapsáveis =====
    private void printCodeBlock(String lang, String code, String indent) {
        indent = "";
        if (!notBlank(code)) return;
        String ind = indent == null ? "" : indent;
        String normalized = code.replace("\r\n", "\n").replace("```", "````");

        boolean collapse = normalized.length() > COLLAPSE_THRESHOLD;
        if (collapse) {
            writer.println(ind + "<details><summary>Ver conteúdo</summary>");
            writer.println(ind);
        }
        writer.println(ind + "````" + (lang != null ? lang : ""));
        writer.print(ind);
        writer.println(normalized);
        writer.println(ind + "````");
        if (collapse) {
            writer.println(ind + "</details>");
        }
        writer.println(ind);
    }

    private void printScriptProperty(String label, String code, String lang, String indent) {
        indent = "";
        String ind = indent == null ? "" : indent;
        writer.println(ind + "- `" + label + "`:");
        printCodeBlock(lang, code, ind + "  ");
    }

    private String prettyJson(String input) {
        if (!notBlank(input)) return input;
        try {
            com.fasterxml.jackson.databind.ObjectMapper m = new com.fasterxml.jackson.databind.ObjectMapper();
            Object tree = m.readTree(input);
            return m.writerWithDefaultPrettyPrinter().writeValueAsString(tree);
        } catch (Exception e) {
            return input;
        }
    }

    private static boolean notBlank(String s) { return s != null && !s.trim().isEmpty(); }

    private String getCoachItemName(LayoutItem item) {
        if (item == null) return "N/A";
        if (item.getConfigData() != null) {
            Optional<String> label = item.getConfigData().stream()
                    .filter(c -> "@label".equals(c.getOptionName()) && notBlank(c.getValue()))
                    .map(ConfigData::getValue)
                    .findFirst();
            if (label.isPresent()) return label.get();
        }
        return notBlank(item.getLayoutItemId()) ? item.getLayoutItemId() : "Sem ID";
    }

    private String getCoachItemName(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item) {
        if (item == null) return "N/A";
        if (item.getConfigData() != null) {
            Optional<String> label = item.getConfigData().stream()
                    .filter(Objects::nonNull)
                    .filter(c -> "@label".equals(c.getOptionName()) && notBlank(c.getValue()))
                    .map(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData::getValue)
                    .findFirst();
            if (label.isPresent()) return label.get();
        }
        return notBlank(item.getLayoutItemId()) ? item.getLayoutItemId() : "Sem ID";
    }

    // ===== Item (2): Responsive JSON → tabela =====
    private boolean looksJson(String s) {
        if (!notBlank(s)) return false;
        String t = s.trim();
        return (t.startsWith("{") || t.startsWith("[")) && t.contains(":");
    }

    /**
     * Tenta imprimir uma tabela "Dispositivo × Valor" para JSON do tipo responsivo.
     * Suporta formats:
     *  { isResponsiveData: true, values: [ { deviceConfigId, value }, ... ] }
     *  { values: [ { device, value }, ... ] }
     *  [ { deviceConfigId/device, value }, ... ]
     */
    private boolean tryPrintResponsive(String formattedName, String rawJson, String indent) {
        indent = "";
        try {
            com.fasterxml.jackson.databind.ObjectMapper m = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = m.readTree(rawJson);

            List<String[]> rows = new ArrayList<>();
            if (root.isObject()) {
                // Caso tenha "values"
                com.fasterxml.jackson.databind.JsonNode values = root.get("values");
                if (values != null && values.isArray()) {
                    for (com.fasterxml.jackson.databind.JsonNode n : values) {
                        String dev = textOf(n, "deviceConfigId", "device", "id", "name");
                        String val = textOf(n, "value", "val");
                        if (dev != null || val != null) rows.add(new String[]{dev, val});
                    }
                } else if (root.get("isResponsiveData") != null) {
                    // Se marcou isResponsiveData mas sem values, imprime o objeto "prettificado"
                    printScriptProperty(formattedName, m.writerWithDefaultPrettyPrinter().writeValueAsString(root), "json", indent);
                    return true;
                }
            } else if (root.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode n : root) {
                    String dev = textOf(n, "deviceConfigId", "device", "id", "name");
                    String val = textOf(n, "value", "val");
                    if (dev != null || val != null) rows.add(new String[]{dev, val});
                }
            }

            if (!rows.isEmpty()) {
                writer.println(indent + "- **" + formattedName + ":**");
                writer.println(indent + "  | Dispositivo | Valor |");
                writer.println(indent + "  | :--- | :--- |");
                for (String[] r : rows) {
                    writer.println(indent + "  | " + safeMd(r[0]) + " | " + safeMd(r[1]) + " |");
                }
                return true;
            }
        } catch (Exception ignore) { /* não é responsivo, segue fluxo padrão */ }
        return false;
    }

    private String textOf(com.fasterxml.jackson.databind.JsonNode node, String... keys) {
        for (String k : keys) {
            com.fasterxml.jackson.databind.JsonNode v = node.get(k);
            if (v != null && !v.isNull()) return v.asText();
        }
        return null;
    }
    private String safeMd(String s) { return s == null ? "—" : ("`" + s + "`"); }
}
