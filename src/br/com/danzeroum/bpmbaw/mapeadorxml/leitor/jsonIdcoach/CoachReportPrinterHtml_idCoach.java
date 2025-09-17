package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIdcoach;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.CoachView;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.ProcessPrePost;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.TWComponent;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Relatório HTML para Coaches / Coach Views (versão getters-based).
 * - Tema light/dim/dark (contraste no tema claro)
 * - TOC lateral + Sumário (telas e CVs)
 * - Blocos <details> colapsados (scripts, tabelas)
 * - Copiar código, colapsar/expandir tudo, auto-open por âncora
 * - Flatten de Layout (LEGACY e NG) via getters do seu modelo
 * - Filtro de views padrão + linkagem para CVs custom
 */
public class CoachReportPrinterHtml_idCoach {

    private final PrintWriter out;
    private final ProcessLoader_idCoach loader;
    private final Set<String> nomesPadrao;

    // Estado do doc
    private boolean docAberto = false;

    // KPIs
    private int telasDetectadas = 0;
    private int coachViewsCustomReferenciadas = 0;
    private final Set<String> nomesPadraoUsados = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    // Sumário interno
    private static class Link { String title; String anchor; Link(String t, String a){title=t;anchor=a;} }
    private final List<Link> sumScreens = new ArrayList<>();
    private final List<Link> sumCVs     = new ArrayList<>();

    public CoachReportPrinterHtml_idCoach(PrintWriter out,
                                          ProcessLoader_idCoach loader,
                                          Set<String> nomesPadrao) {
        this.out = out;
        this.loader = loader;
        this.nomesPadrao = (nomesPadrao != null) ? new HashSet<>(nomesPadrao) : Collections.emptySet();
    }

    /* ============================ Documento ============================ */

    public void beginHtmlDocument(String titulo, String artifactRootId) {
        if (docAberto) return;
        docAberto = true;

        String agora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        out.println("<!doctype html><html lang='pt-br'><head><meta charset='utf-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
        out.println("<title>" + esc(titulo) + "</title>");
        out.println("<style>" + css() + "</style>");
        out.println("</head><body>");
        out.println("<div class='app'>");

        // Sidebar
        out.println("<aside class='sidebar'>");
        out.println("  <div class='brand'><div class='dot'></div><div>");
        out.println("    <div class='brand-title'>Relatório de Interfaces (Coaches)</div>");
        out.println("    <div class='muted'>Leve • Offline • Navegável</div>");
        out.println("  </div></div>");

        out.println("  <div class='card' style='margin-bottom:10px;'>");
        out.println("    <div class='row wrap' style='gap:6px 8px; margin-bottom:8px;'>");
        out.println("      <button id='btnCollapseAll' class='btn ghost'>Colapsar tudo</button>");
        out.println("      <button id='btnExpandAll' class='btn ghost'>Expandir tudo</button>");
        out.println("      <button id='btnDark' class='btn ghost'>Tema</button>");
        out.println("    </div>");
        out.println("    <div class='kv muted'><span>Artefato raiz:</span><span>" + esc(orElse(artifactRootId, "-")) + "</span></div>");
        out.println("    <div class='kv muted'><span>Gerado em:</span><span>" + agora + "</span></div>");
        out.println("  </div>");

        out.println("  <div class='card' style='margin-bottom:10px;'>");
        out.println("    <input id='search' class='search' type='search' placeholder='Filtrar (TOC)…'>");
        out.println("    <div class='toc' id='toc'></div>");
        out.println("  </div>");

        out.println("  <div class='card status' id='status'>");
        out.println("    <div class='kv'><span>Telas detectadas:</span><span id='stScreens'>"+telasDetectadas+"</span></div>");
        out.println("    <div class='kv'><span>CVs custom referenciadas:</span><span id='stCvCustom'>"+coachViewsCustomReferenciadas+"</span></div>");
        out.println("    <div class='kv'><span>Views padrão (sistema):</span><span id='stCvPadrao'>"+nomesPadraoUsados.size()+"</span></div>");
        out.println("    <div class='kv'><span>Seções (H2/H3):</span><span id='stToc'>0</span></div>");
        out.println("  </div>");

        out.println("</aside>");

        // Conteúdo
        out.println("<main class='content'>");
        out.println("<section class='card'><h1>🧭 Sumário &amp; Navegação</h1>");
        out.println("<div class='kv'><span>Artefato raiz</span><span><code>" + esc(orElse(artifactRootId, "-")) + "</code></span></div>");
        out.println("<div class='kv'><span>Observação</span><span class='muted'>Views padrão são tratadas como componentes de sistema e não aparecem na área de detalhes de Coach Views.</span></div>");
        out.println("</section>");

        // Placeholder do Sumário
        out.println("<section class='card' id='sumarioCard'><h2>📚 Sumário</h2>");
        out.println("<div id='sumScreens'></div><div id='sumCVs' style='margin-top:12px;'></div>");
        out.println("</section>");
    }

    public void finishHtmlDocument() {
        if (!docAberto) return;

        // KPIs (pré-preenchidos e também atualizados via JS)
        out.println("<script>");
        out.println("document.getElementById('stScreens').textContent='" + telasDetectadas + "';");
        out.println("document.getElementById('stCvCustom').textContent='" + coachViewsCustomReferenciadas + "';");
        out.println("document.getElementById('stCvPadrao').textContent='" + nomesPadraoUsados.size() + "';");
        out.println("</script>");

        // Sumário client-side
        out.println("<script>");
        out.println("const __screens = " + toJsArray(sumScreens) + ";");
        out.println("const __cvs = " + toJsArray(sumCVs) + ";");
        out.println("(()=>{");
        out.println("  const sc = document.getElementById('sumScreens');");
        out.println("  const cv = document.getElementById('sumCVs');");
        out.println("  if(sc){ if(__screens.length){ const ul=document.createElement('ul'); ul.className='sumlist'; __screens.forEach(o=>{const li=document.createElement('li'); li.innerHTML=`<a href=\"#${o.anchor}\">${o.title}</a>`; ul.appendChild(li);}); sc.innerHTML='<h3>👩‍💻 Telas</h3>'; sc.appendChild(ul);} else { sc.innerHTML='<div class=\"muted\">Nenhuma tela encontrada.</div>'; }}");
        out.println("  if(cv){ if(__cvs.length){ const ul=document.createElement('ul'); ul.className='sumlist'; __cvs.forEach(o=>{const li=document.createElement('li'); li.innerHTML=`<a href=\"#${o.anchor}\">${o.title}</a>`; ul.appendChild(li);}); cv.innerHTML='<h3>🧩 Coach Views</h3>'; cv.appendChild(ul);} else { cv.innerHTML='<div class=\"muted\">Nenhuma Coach View custom listada.</div>'; }}");
        out.println("})();");
        out.println("</script>");

        out.println("</main></div>"); // fecha .app

        out.println("<script>");
        out.println(js()); // TOC, tema, copiar, expand/collapse, auto-open
        out.println("</script>");

        out.println("</body></html>");
        docAberto = false;
    }

    /* ============================ Seções de impressão ============================ */

    /** Scripts de Pré/Pós execução (colapsados) */
    public void imprimirScriptsPreExecucao(Item item, String indent) {
        if (item == null || item.getProcessPrePosts() == null || item.getProcessPrePosts().isEmpty()) return;

        out.println("<section class='card'>");
        out.println("<h3>⚙️ Scripts de Pré/Pós Execução — <span class='muted'>" + esc(orElse(item.getName(), "(Sem nome)")) + "</span></h3>");
        for (ProcessPrePost ppp : item.getProcessPrePosts()) {
            String tipo = obterCampoTexto(ppp, "getType", "getProcessPrePostType", "getPhase");
            String label = notBlank(tipo) ? tipo : "Bloco";
            String script = obterCampoTexto(ppp, "getScript", "getJavascript", "getExpression", "getValue", "getContent");
            if (notBlank(script)) {
                printCollapsibleBlock("📜 " + label, "javascript", script);
            }
        }
        out.println("</section>");
    }

    /** Eventos de boundary (colapsados) */
    public void imprimirEventosDeBoundary(Item item, String indent) {
        if (item == null || item.getTwComponent() == null) return;
        TWComponent comp = item.getTwComponent();
        try {
            Method m = comp.getClass().getMethod("getBoundaryEvents");
            Object events = m.invoke(comp);
            if (!(events instanceof Collection)) return;

            Collection<?> lista = (Collection<?>) events;
            if (lista.isEmpty()) return;

            out.println("<section class='card'>");
            out.println("<h3>⛑️ Eventos de Boundary — <span class='muted'>" + esc(orElse(item.getName(), "(Sem nome)")) + "</span></h3>");
            StringBuilder table = new StringBuilder();
            table.append("<div class='table-wrap'><table class='tbl tbl-io'>");
            table.append("<thead><tr><th>Tipo</th><th>Script/Expressão</th></tr></thead><tbody>");
            for (Object ev : lista) {
                String tipo = obterCampoTexto(ev, "getType", "getEventType", "getName");
                String script = obterCampoTexto(ev, "getScript", "getJavascript", "getExpression", "getValue", "getContent");
                table.append("<tr><td>").append(esc(orElse(tipo, "-"))).append("</td><td><code>")
                        .append(esc(orElse(script, "-"))).append("</code></td></tr>");
            }
            table.append("</tbody></table></div>");
            printCollapsibleHtml("📎 Eventos de boundary (" + lista.size() + ")", table.toString());
            out.println("</section>");
        } catch (NoSuchMethodException ignore) {
            // sem boundary events
        } catch (Exception e) {
            System.err.println("[WARN] Falha ao extrair boundary events: " + e.getMessage());
        }
    }

    /** Layout legado (colapsado) */
    public void imprimirLayoutCoach(CoachLayout legacyLayout,
                                    String processName,
                                    String activityName,
                                    String indent,
                                    Set<String> discoveredCoachViewIds) {
        if (legacyLayout == null || legacyLayout.getItems() == null || legacyLayout.getItems().isEmpty()) return;
        telasDetectadas++;

        String anchor = "screen-" + slug(orElse(activityName, "tela"));
        sumScreens.add(new Link(orElse(activityName, "(Sem nome)"), anchor));

        out.println("<section class='card'>");
        out.println("<h2 id='" + anchor + "'>👩‍💻 Tela (Legado) — " + esc(orElse(activityName, "(Sem nome)")) + "</h2>");
        out.println("<div class='muted'>Processo: <span class='chip'><code>" + esc(orElse(processName, "-")) + "</code></span></div>");

        List<Row> rows = new ArrayList<>();
        for (LayoutItem li : legacyLayout.getItems()) {
            flattenLegacy(li, rows, discoveredCoachViewIds);
        }
        String table = buildTabelaComponentes(rows);
        printCollapsibleHtml("📦 Componentes da tela (" + rows.size() + ")", table);
        out.println("</section>");
    }

    /** Layout moderno (colapsado) */
    public void imprimirLayoutCoach(Layout modernLayout,
                                    String processName,
                                    String activityName,
                                    String indent,
                                    Set<String> discoveredCoachViewIds) {
        if (modernLayout == null || modernLayout.getLayoutItems() == null || modernLayout.getLayoutItems().isEmpty()) return;
        telasDetectadas++;

        String anchor = "screen-" + slug(orElse(activityName, "tela"));
        sumScreens.add(new Link(orElse(activityName, "(Sem nome)"), anchor));

        out.println("<section class='card'>");
        out.println("<h2 id='" + anchor + "'>👩‍💻 Tela — " + esc(orElse(activityName, "(Sem nome)")) + "</h2>");
        out.println("<div class='muted'>Processo: <span class='chip'><code>" + esc(orElse(processName, "-")) + "</code></span></div>");

        List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem> items = modernLayout.getLayoutItems();
        List<Row> rows = new ArrayList<>();
        if (items != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem it : items) {
                flattenModern(it, rows, discoveredCoachViewIds);
            }
        }
        String table = buildTabelaComponentes(rows);
        printCollapsibleHtml("📦 Componentes da tela (" + rows.size() + ")", table);
        out.println("</section>");
    }

    /** Detalhes de todas as CoachViews (com blocos colapsados) */
    public void imprimirDetalhesDeTodasAsCoachViews(ProcessLoader_idCoach loader, Set<String> coachViewIds) {
        if (coachViewIds == null || coachViewIds.isEmpty()) return;

        List<String> ids = coachViewIds.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        if (ids.isEmpty()) return;

        // ---------- Tabela-resumo ----------
        out.println("<section class='card'>");
        out.println("<h2>🧩 Coach Views (Custom)</h2>");
        out.println("<div class='table-wrap'><table class='tbl tbl-index'>");
        out.println("<colgroup><col style='width:38%'><col style='width:32%'><col style='width:30%'></colgroup>");
        out.println("<thead><tr><th>Nome</th><th>ID</th><th>Detalhes</th></tr></thead><tbody>");

        for (String id : ids) {
            Object art = loader.getArtefatoDoCache(id);

            // Pode vir como Teamworks ou diretamente como CoachView
            CoachView cv = null;
            if (art instanceof Teamworks) {
                cv = ((Teamworks) art).getCoachView();
            } else if (art instanceof CoachView) {
                cv = (CoachView) art;
            }

            String nome = (cv != null && notBlank(cv.getName())) ? cv.getName() : "(Desconhecida)";
            String anchor = coachViewAnchor(id);
            if (cv != null) {
                sumCVs.add(new Link(esc(nome), anchor));
            }

            out.println("<tr>"
                    + "<td><a href='#" + anchor + "'>" + esc(nome) + "</a></td>"
                    + "<td><code>" + esc(id) + "</code></td>"
                    + "<td>—</td>"
                    + "</tr>");
        }
        out.println("</tbody></table></div>");
        out.println("</section>");

        // ---------- Seções detalhadas por CV ----------
        for (String id : ids) {
            Object art = loader.getArtefatoDoCache(id);

            CoachView cv = null;
            if (art instanceof Teamworks) {
                cv = ((Teamworks) art).getCoachView();
            } else if (art instanceof CoachView) {
                cv = (CoachView) art;
            }
            if (cv == null) continue;

            String nome = notBlank(cv.getName()) ? cv.getName() : "(Sem nome)";
            String anchor = coachViewAnchor(id);

            out.println("<section class='card'>");
            out.println("<h3 id='" + anchor + "'>🧩 " + esc(nome) + " <span class='muted'>(ID: " + esc(id) + ")</span></h3>");

            // ---------- Metadados ----------
            String toolkit = "-";
            String filePath = "-";
            try {
                ProcessLoader_idCoach.ArtifactLocation loc = loader.findArtifactLocation(id);
                if (loc != null) {
                    if (notBlank(loc.toolkitName)) toolkit = loc.toolkitName;
                    if (notBlank(loc.filePath))    filePath = loc.filePath;
                }
            } catch (Exception ignore) { }

            String lastModStr = "-";
            try {
                long lm = cv.getLastModified();
                if (lm > 0L) {
                    lastModStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(lm));
                }
            } catch (Exception ignore) { }

            StringBuilder meta = new StringBuilder();
            meta.append("<div class='table-wrap'><table class='tbl tbl-map'>");
            meta.append("<thead><tr><th>Campo</th><th>Valor</th></tr></thead><tbody>");
            meta.append("<tr><td>Nome</td><td>").append(esc(nome)).append("</td></tr>");
            meta.append("<tr><td>CoachView ID</td><td><code>").append(esc(orElse(cv.getCoachViewId(), "-"))).append("</code></td></tr>");
            meta.append("<tr><td>Toolkit</td><td><code>").append(esc(toolkit)).append("</code></td></tr>");
            meta.append("<tr><td>Arquivo</td><td><code>").append(esc(filePath)).append("</code></td></tr>");
            meta.append("<tr><td>Última modificação</td><td>").append(esc(lastModStr)).append("</td></tr>");
            if (notBlank(cv.getLastModifiedBy())) {
                meta.append("<tr><td>Modificado por</td><td>").append(esc(cv.getLastModifiedBy())).append("</td></tr>");
            }
            meta.append("<tr><td>É Template?</td><td>").append(cv.isTemplate() ? "Sim" : "Não").append("</td></tr>");
            meta.append("</tbody></table></div>");
            printCollapsibleHtml("ℹ️ Metadados", meta.toString());

            // ---------- Funções JS (API do modelo) ----------
            String viewFn   = orElse(cv.getViewJsFunction(), null);
            String loadFn   = orElse(cv.getLoadJsFunction(), null);
            String unloadFn = orElse(cv.getUnloadJsFunction(), null);
            String changeFn = orElse(cv.getChangeJsFunction(), null);

            if (notBlank(viewFn))   printCollapsibleBlock("🧠 viewJsFunction",   "javascript", viewFn);
            if (notBlank(loadFn))   printCollapsibleBlock("⚡ loadJsFunction",   "javascript", loadFn);
            if (notBlank(unloadFn)) printCollapsibleBlock("🧹 unloadJsFunction", "javascript", unloadFn);
            if (notBlank(changeFn)) printCollapsibleBlock("🔁 changeJsFunction", "javascript", changeFn);

            // ---------- Inline Scripts ----------
            List<?> inlineScripts = cv.getInlineScripts();
            if (inlineScripts != null && !inlineScripts.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                int idx = 1;
                for (Object inl : inlineScripts) {
                    String s = obterCampoTexto(inl, "getScript", "getValue", "getContent", "getText");
                    if (notBlank(s)) {
                        sb.append("<details class='collapsible'>")
                                .append("<summary>Inline Script #").append(idx++).append("</summary>")
                                .append("<pre><button class='btn small copy-btn'>Copiar</button><code class='javascript'>")
                                .append(esc(s.trim()))
                                .append("</code></pre></details>");
                    }
                }
                if (sb.length() > 0) {
                    printCollapsibleHtml("🧩 Inline Scripts (" + (idx - 1) + ")", sb.toString());
                }
            }

            // ---------- Layout (HTML) ----------
            if (notBlank(cv.getLayout())) {
                printCollapsibleBlock("🧱 Layout (HTML)", "html", cv.getLayout());
            } else {
                // fallback leve: tenta getters alternativos se existirem no seu modelo real
                String html = obterCampoTexto(cv, "getTemplate", "getHtml", "getMarkup", "getViewDefinition");
                if (notBlank(html)) {
                    printCollapsibleBlock("🧱 Layout (HTML)", "html", html);
                }
            }

            // ---------- AMD Dependencies ----------
            List<?> deps = null;
            try { deps = cv.getAmdDependencies(); } catch (Exception ignore) {}
            if (deps != null && !deps.isEmpty()) {
                StringBuilder t = new StringBuilder();
                t.append("<div class='table-wrap'><table class='tbl tbl-index'>");
                t.append("<thead><tr><th>Nome/Módulo</th><th>Versão</th><th>Path</th></tr></thead><tbody>");
                for (Object d : deps) {
                    String nome2    = orElse(obterCampoTexto(d, "getName", "getModule", "getId"), "-");
                    String versao  = orElse(obterCampoTexto(d, "getVersion", "getVer"), "-");
                    String path    = orElse(obterCampoTexto(d, "getPath", "getUrl"), "-");
                    t.append("<tr>")
                            .append("<td>").append(esc(nome2)).append("</td>")
                            .append("<td>").append(esc(versao)).append("</td>")
                            .append("<td><code>").append(esc(path)).append("</code></td>")
                            .append("</tr>");
                }
                t.append("</tbody></table></div>");
                printCollapsibleHtml("📦 AMD Dependencies (" + deps.size() + ")", t.toString());
            }

            // ---------- Binding Types / Config Options (tabelas genéricas) ----------
            List<?> bindingTypes = null, configOptions = null;
            try { bindingTypes = cv.getBindingTypes(); } catch (Exception ignore) {}
            try { configOptions = cv.getConfigOptions(); } catch (Exception ignore) {}

            if (bindingTypes != null && !bindingTypes.isEmpty()) {
                StringBuilder t = new StringBuilder();
                t.append("<div class='table-wrap'><table class='tbl tbl-index'>");
                t.append("<thead><tr><th>Nome</th><th>Tipo</th><th>Default</th></tr></thead><tbody>");
                for (Object bt : bindingTypes) {
                    String nome1 = orElse(obterCampoTexto(bt, "getName", "getId", "getKey"), "-");
                    String tipo = orElse(obterCampoTexto(bt, "getType", "getDataType"), "-");
                    String def  = orElse(obterCampoTexto(bt, "getDefaultValue", "getDefault", "getValue"), "-");
                    t.append("<tr><td>").append(esc(nome1)).append("</td><td>")
                            .append(esc(tipo)).append("</td><td><code>")
                            .append(esc(def)).append("</code></td></tr>");
                }
                t.append("</tbody></table></div>");
                printCollapsibleHtml("🔗 Binding Types (" + bindingTypes.size() + ")", t.toString());
            }

            if (configOptions != null && !configOptions.isEmpty()) {
                StringBuilder t = new StringBuilder();
                t.append("<div class='table-wrap'><table class='tbl tbl-index'>");
                t.append("<thead><tr><th>Chave</th><th>Tipo</th><th>Default</th></tr></thead><tbody>");
                for (Object co : configOptions) {
                    String key  = orElse(obterCampoTexto(co, "getKey", "getName", "getId"), "-");
                    String tipo = orElse(obterCampoTexto(co, "getType", "getDataType"), "-");
                    String def  = orElse(obterCampoTexto(co, "getDefaultValue", "getDefault", "getValue"), "-");
                    t.append("<tr><td>").append(esc(key)).append("</td><td>")
                            .append(esc(tipo)).append("</td><td><code>")
                            .append(esc(def)).append("</code></td></tr>");
                }
                t.append("</tbody></table></div>");
                printCollapsibleHtml("⚙️ Config Options (" + configOptions.size() + ")", t.toString());
            }

            out.println("</section>");
        }
    }


    /* ============================ Flatten & Tabela ============================ */

    private static class Row { String tipo; String binding; String cvInfo; String id; }

    private void flattenLegacy(LayoutItem it,
                               List<Row> rows,
                               Set<String> discoveredCoachViewIds) {
        if (it == null) return;

        Row r = new Row();
        r.id = it.getLayoutItemId();
        r.tipo = orElse(it.getXsiType(), "Component");
        r.binding = it.getBinding();

        String viewUUID = it.getViewUUID();
        if (notBlank(viewUUID)) {
            String cleanId = getCleanId(viewUUID);
            try { loader.loadArtefatoSeNaoExistir(cleanId); } catch (Exception ignore) {}
            Object artifact = loader.getArtefatoDoCache(cleanId);

            String cvName = null;
            if (artifact instanceof Teamworks) {
                CoachView cv = ((Teamworks) artifact).getCoachView();
                if (cv != null) cvName = cv.getName();
            }

            if (notBlank(cvName)) {
                if (nomesPadrao.contains(cvName)) {
                    r.cvInfo = esc(cvName) + " <span class='muted'>(padrão)</span>";
                    nomesPadraoUsados.add(cvName);
                } else {
                    r.cvInfo = "<a href='#" + coachViewAnchor(cleanId) + "'>" + esc(cvName) + "</a>";
                    discoveredCoachViewIds.add(cleanId);
                    coachViewsCustomReferenciadas++;
                }
            } else {
                r.cvInfo = "<code>" + esc(cleanId) + "</code>";
                discoveredCoachViewIds.add(cleanId);
                coachViewsCustomReferenciadas++;
            }
        }
        rows.add(r);

        // Descer na árvore (LEGACY): contentBoxContributions -> contributions
        List<LayoutItem.ContentBoxContribution> boxes = it.getContentBoxContributions();
        if (boxes != null) {
            for (LayoutItem.ContentBoxContribution box : boxes) {
                List<LayoutItem> children = box.getContributions();
                if (children != null) {
                    for (LayoutItem child : children) {
                        flattenLegacy(child, rows, discoveredCoachViewIds);
                    }
                }
            }
        }
    }

    private void flattenModern(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem it,
                               List<Row> rows,
                               Set<String> discoveredCoachViewIds) {
        if (it == null) return;

        Row r = new Row();
        r.id = it.getLayoutItemId();
        r.tipo = "ViewRef";
        r.binding = it.getBinding();

        String viewUUID = it.getViewUUID();
        if (notBlank(viewUUID)) {
            String cleanId = getCleanId(viewUUID);
            try { loader.loadArtefatoSeNaoExistir(cleanId); } catch (Exception ignore) {}
            Object artifact = loader.getArtefatoDoCache(cleanId);

            String cvName = null;
            if (artifact instanceof Teamworks) {
                CoachView cv = ((Teamworks) artifact).getCoachView();
                if (cv != null) cvName = cv.getName();
            }

            if (notBlank(cvName)) {
                if (nomesPadrao.contains(cvName)) {
                    r.cvInfo = esc(cvName) + " <span class='muted'>(padrão)</span>";
                    nomesPadraoUsados.add(cvName);
                } else {
                    r.cvInfo = "<a href='#" + coachViewAnchor(cleanId) + "'>" + esc(cvName) + "</a>";
                    discoveredCoachViewIds.add(cleanId);
                    coachViewsCustomReferenciadas++;
                }
            } else {
                r.cvInfo = "<code>" + esc(cleanId) + "</code>";
                discoveredCoachViewIds.add(cleanId);
                coachViewsCustomReferenciadas++;
            }
        }
        rows.add(r);

        // Descer na árvore (NG): contentBoxContribs -> contributions
        List<ContentBoxContrib> boxes =
                it.getContentBoxContribs();
        if (boxes != null) {
            for (ContentBoxContrib box : boxes) {
                List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem> children = box.getContributions();
                if (children != null) {
                    for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem child : children) {
                        flattenModern(child, rows, discoveredCoachViewIds);
                    }
                }
            }
        }
    }

    private String buildTabelaComponentes(List<Row> rows) {
        StringBuilder sb = new StringBuilder();
        if (rows == null || rows.isEmpty()) {
            sb.append("<div class='muted'>Nenhum componente encontrado.</div>");
            return sb.toString();
        }
        sb.append("<div class='table-wrap'><table class='tbl tbl-index'>");
        sb.append("<colgroup><col style='width:28%'><col style='width:32%'><col style='width:25%'><col style='width:15%'></colgroup>");
        sb.append("<thead><tr><th>Componente</th><th>Binding</th><th>CoachView</th><th>ID</th></tr></thead><tbody>");
        for (Row r : rows) {
            sb.append("<tr>")
                    .append("<td>").append(esc(orElse(r.tipo, "-"))).append("</td>")
                    .append("<td><code>").append(esc(orElse(r.binding, "-"))).append("</code></td>")
                    .append("<td>").append(notBlank(r.cvInfo) ? r.cvInfo : "-").append("</td>")
                    .append("<td><code>").append(esc(orElse(r.id, "-"))).append("</code></td>")
                    .append("</tr>");
        }
        sb.append("</tbody></table></div>");
        return sb.toString();
    }

    /* ============================ Utilidades ============================ */

    private String coachViewAnchor(String id) {
        return "cv-" + id.replaceAll("[^a-zA-Z0-9]+", "-").toLowerCase(Locale.ROOT);
    }

    private String getCleanId(String raw) {
        try { return String.valueOf(loader.getCleanId(raw)); }
        catch (Exception e) { return raw; }
    }

    private static boolean notBlank(String s) { return s != null && !s.trim().isEmpty(); }
    private static String orElse(String s, String fallback) { return notBlank(s) ? s : fallback; }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'", "&#39;");
    }

    private String slug(String s) {
        String t = (s == null ? "" : s.toLowerCase(Locale.ROOT));
        t = t.replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+|-+$)", "");
        if (t.isEmpty()) t = "tela";
        return t;
    }

    // Bloco de código colapsado
    private void printCollapsibleBlock(String title, String lang, String code) {
        if (!notBlank(code)) return;
        String normalized = code.replace("\r\n", "\n").trim();
        out.println("<details class='collapsible'>");
        out.println("  <summary>" + esc(title) + " <span class='muted'>(" + countLines(normalized) + " linhas)</span></summary>");
        out.println("  <pre><button class='btn small copy-btn'>Copiar</button><code class='" + esc(lang) + "'>" + esc(normalized) + "</code></pre>");
        out.println("</details>");
    }

    // Bloco HTML arbitrário colapsado (tabelas, metadados etc.)
    private void printCollapsibleHtml(String title, String innerHtml) {
        out.println("<details class='collapsible'>");
        out.println("  <summary>" + esc(title) + "</summary>");
        out.println(innerHtml);
        out.println("</details>");
    }

    private int countLines(String s){ int c=1; for(int i=0;i<s.length();i++) if(s.charAt(i)=='\n') c++; return c; }

    // Busca “best-effort” de campos texto via reflexão em getters (apenas para conteúdo)
    private String obterCampoTexto(Object obj, String... getters) {
        if (obj == null) return null;
        for (String g : getters) {
            try {
                Method m = obj.getClass().getMethod(g);
                if (m.getParameterCount() != 0) continue;
                Object v = m.invoke(obj);
                if (v == null) continue;
                String s = String.valueOf(v);
                if (notBlank(s)) return s;
            } catch (Exception ignore) { }
        }
        return null;
    }

    // Heurísticas de JS/HTML/CSS + scan raso (getters) como fallback
    private boolean looksLikeJs(String s) {
        if (!notBlank(s)) return false;
        String t = s.trim();
        int len = Math.min(t.length(), 1200);
        t = t.substring(0, len);
        return t.contains("tw.") || t.contains("function") || t.contains("=>")
                || t.contains("var ") || t.contains("let ") || t.contains("const ")
                || t.contains(".addEventListener(") || t.contains("return ");
    }
    private boolean looksLikeHtml(String s) {
        if (!notBlank(s)) return false;
        String t = s.toLowerCase(Locale.ROOT);
        return t.contains("<div") || t.contains("<span") || t.contains("<table")
                || t.contains("<script") || t.contains("<template")
                || t.contains("<form") || t.contains("<section");
    }
    private boolean looksLikeCss(String s) {
        if (!notBlank(s)) return false;
        String t = s;
        return (t.contains("{") && t.contains("}") && t.contains(":")) || t.contains("@media");
    }

    private String deepFindLikelyJs(Object root, int maxDepth) {
        List<String> bag = deepCollectGetterStrings(root, maxDepth);
        for (String s : bag) if (looksLikeJs(s)) return s;
        return null;
    }
    private String deepFindLikelyHtml(Object root, int maxDepth) {
        List<String> bag = deepCollectGetterStrings(root, maxDepth);
        for (String s : bag) if (looksLikeHtml(s)) return s;
        return null;
    }
    private String deepFindLikelyCss(Object root, int maxDepth) {
        List<String> bag = deepCollectGetterStrings(root, maxDepth);
        for (String s : bag) if (looksLikeCss(s)) return s;
        return null;
    }

    private List<String> deepCollectGetterStrings(Object obj, int maxDepth) {
        List<String> out = new ArrayList<>();
        deepCollectGetterStrings0(obj, 0, maxDepth, new HashSet<>(), out);
        return out;
    }

    @SuppressWarnings("unchecked")
    private void deepCollectGetterStrings0(Object obj, int depth, int maxDepth, Set<Object> visited, List<String> outList) {
        if (obj == null || depth > maxDepth) return;
        if (!visited.add(obj)) return;

        if (obj instanceof String) {
            String s = (String) obj;
            if (notBlank(s)) outList.add(s);
            return;
        }
        if (obj instanceof Collection) {
            for (Object o : ((Collection<?>) obj)) deepCollectGetterStrings0(o, depth + 1, maxDepth, visited, outList);
            return;
        }
        if (obj.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(obj);
            for (int i = 0; i < len; i++) deepCollectGetterStrings0(java.lang.reflect.Array.get(obj, i), depth + 1, maxDepth, visited, outList);
            return;
        }
        if (obj instanceof Map) {
            for (Object v : ((Map<?, ?>) obj).values()) deepCollectGetterStrings0(v, depth + 1, maxDepth, visited, outList);
            return;
        }
        Package p = obj.getClass().getPackage();
        if (p != null) {
            String pn = p.getName();
            if (pn.startsWith("java.time") || pn.startsWith("java.math") || pn.startsWith("java.lang")) return;
        }
        for (Method m : obj.getClass().getMethods()) {
            String name = m.getName();
            if (!name.startsWith("get") || "getClass".equals(name)) continue;
            if (m.getParameterCount() != 0) continue;
            try { deepCollectGetterStrings0(m.invoke(obj), depth + 1, maxDepth, visited, outList); }
            catch (Throwable ignore) { }
        }
    }

    /* ============================ CSS/JS ============================ */

    private String css() {
        return ""
                // Paleta DIM (padrão)
                + ":root{--bg:#0f1525;--fg:#e9edf6;--muted:#9fb0c8;--accent:#6aa2ff;"
                + "      --card:#151e31;--border:#263555;--code-bg:#1a2642;--code-fg:#e9edf6;"
                + "      --inline-code-bg:#111c33;--inline-code-fg:inherit;--table-bg:#111a2f;--table-alt:#0f1833;"
                + "      --summary-bg:#16233f;--summary-fg:#e9edf6;--chip-bg:#0f1a33;--chip-fg:#dbe7ff}"
                // Paleta LIGHT (contraste alto para sumários/labels)
                + "body.light{--bg:#f7f9fc;--fg:#111827;--muted:#6b7280;--accent:#2563eb;"
                + "            --card:#ffffff;--border:#e5e7eb;--code-bg:#f3f4f6;--code-fg:#111827;"
                + "            --inline-code-bg:#eef2f7;--inline-code-fg:#111827;--table-bg:#ffffff;--table-alt:#f7f9ff;"
                + "            --summary-bg:#eef2ff;--summary-fg:#111827;--chip-bg:#e8eefc;--chip-fg:#1f2937}"
                // Paleta DARK
                + "body.dark{--bg:#0b1020;--fg:#e7ecff;--muted:#a9b3d1;--accent:#7aa2ff;"
                + "          --card:#10162b;--border:#1c2547;--code-bg:#0f1a33;--code-fg:#e7ecff;"
                + "          --inline-code-bg:#0f1730;--inline-code-fg:#e7ecff;--table-bg:#0d1530;--table-alt:#0f1833;"
                + "          --summary-bg:#0f1730;--summary-fg:#e7ecff;--chip-bg:#0e1530;--chip-fg:#dae6ff}"

                + "html,body{height:100%}body{margin:0;font-family:ui-sans-serif,system-ui,-apple-system,Segoe UI,Roboto,Ubuntu,Cantarell,Noto Sans,Arial;background:var(--bg);color:var(--fg)}"
                + ".app{display:grid;grid-template-columns:320px 1fr;min-height:100vh}"
                + ".sidebar{border-right:1px solid var(--border);background:color-mix(in oklab, var(--bg) 94%, #000 6%);padding:16px;position:sticky;top:0;height:100vh;overflow:auto}"
                + ".content{padding:24px 32px;max-width:1200px;margin:0 auto}"
                + ".brand{display:flex;gap:8px;align-items:center;margin-bottom:12px}.brand .dot{width:10px;height:10px;background:var(--accent);border-radius:999px;box-shadow:0 0 12px var(--accent)}.brand-title{font-weight:800}"
                + ".muted{color:var(--muted);font-size:12px}"
                + ".card{background:var(--card);border:1px solid var(--border);border-radius:12px;padding:16px;margin:0 0 16px 0;box-shadow:0 10px 30px rgba(0,0,0,.08)}"
                + ".row{display:flex;gap:8px;align-items:center}.row.wrap{flex-wrap:wrap}"
                + ".btn{background:color-mix(in oklab, var(--card) 88%, var(--bg) 12%);border:1px solid var(--border);color:var(--fg);padding:8px 10px;border-radius:10px;cursor:pointer;font-weight:600;transition:.2s}"
                + ".btn:hover{transform:translateY(-1px);background:color-mix(in oklab, var(--card) 70%, var(--bg) 30%)}.btn.small{padding:4px 8px;font-size:12px}.btn.ghost{background:transparent;border-color:color-mix(in oklab, var(--border) 70%, var(--accent) 30%)}"
                + ".search{width:100%;padding:10px 12px;border-radius:10px;background:color-mix(in oklab, var(--bg) 90%, var(--card) 10%);border:1px solid var(--border);color:var(--fg)}"
                + ".status{display:grid;gap:8px}.kv{display:flex;justify-content:space-between;font-size:13px;color:var(--muted)}"
                + ".toc .item{display:block;padding:6px 8px;border-radius:8px;color:var(--fg);border:1px solid transparent}.toc .item:hover{background:color-mix(in oklab, var(--card) 80%, var(--bg) 20%);border-color:var(--border)}.toc .lvl2{margin-left:0;font-weight:600}.toc .lvl3{margin-left:14px;color:var(--muted)}"

                + "h1,h2,h3,h4{color:var(--fg);margin:0 0 10px 0}h1{font-size:26px}h2{font-size:22px}h3{font-size:18px}"

                + "code{background:var(--inline-code-bg);color:var(--inline-code-fg);border:1px solid var(--border);border-radius:6px;padding:0 4px}"
                + "pre{background:var(--code-bg);color:var(--code-fg);border:1px solid var(--border);border-radius:12px;padding:12px;overflow:auto;position:relative}"
                + "pre code{background:transparent;border:none;padding:0;color:inherit}.copy-btn{position:absolute;top:8px;right:8px}"

                + ".chip{display:inline-flex;align-items:center;gap:6px;padding:2px 8px;border-radius:999px;background:var(--chip-bg);color:var(--chip-fg);border:1px solid var(--border)}"

                + ".table-wrap{overflow-x:auto}table{table-layout:auto;border-collapse:collapse;background:var(--table-bg);border:1px solid var(--border);border-radius:10px;overflow:hidden;margin:8px 0;width:100%}"
                + "th,td{border-bottom:1px solid var(--border);padding:8px 10px;text-align:left;vertical-align:top;word-break:break-word}"
                + "tr:nth-child(even) td{background:var(--table-alt)}"
                + ".tbl-index td:nth-child(2), .tbl-index th:nth-child(2){max-width:36ch}"
                + ".tbl-index td:nth-child(4), .tbl-index th:nth-child(4){white-space:nowrap;max-width:26ch;overflow:hidden;text-overflow:ellipsis}"

                + "details.collapsible{border:1px solid var(--border);background:color-mix(in oklab, var(--card) 90%, var(--bg) 10%);border-radius:10px;margin:10px 0;padding:0}"
                + "details.collapsible>summary{list-style:none;cursor:pointer;padding:10px 12px;font-weight:700;display:flex;align-items:center;gap:8px;background:var(--summary-bg);color:var(--summary-fg);border-bottom:1px solid var(--border)}"
                + "details.collapsible>summary::before{content:'▸';display:inline-block;transform:translateY(-1px)}"
                + "details.collapsible[open]>summary::before{content:'▾'}"
                + ".sumlist{margin:6px 0 0 16px;padding:0} .sumlist li{margin:2px 0}";
    }

    private String js() {
        return ""
                // TOC e copiar
                + "function buildToc(){const cont=document.querySelector('main');const toc=document.getElementById('toc');if(!toc)return;toc.innerHTML='';"
                + "const hs=[...cont.querySelectorAll('h2, h3')];hs.forEach((h,i)=>{if(!h.id){h.id='h-'+i;}const a=document.createElement('a');a.href='#'+h.id;a.textContent=h.textContent;"
                + "a.className='item '+(h.tagName==='H2'?'lvl2':'lvl3');toc.appendChild(a);});const st=document.getElementById('stToc');if(st)st.textContent=hs.length;}"
                + "function filterToc(){const q=(document.getElementById('search')||{}).value||'';document.querySelectorAll('#toc .item').forEach(a=>{a.classList.toggle('hidden',!( !q || a.textContent.toLowerCase().includes(q.toLowerCase())));});}"
                + "function setAllDetails(open){document.querySelectorAll('details').forEach(d=>d.open=open);} "
                + "function enableCopy(){document.querySelectorAll('pre .copy-btn').forEach(btn=>{btn.addEventListener('click',()=>{const code=btn.parentElement.querySelector('code').textContent;navigator.clipboard.writeText(code).then(()=>{btn.textContent='Copiado!';setTimeout(()=>btn.textContent='Copiar',1200);});});});}"
                + "document.getElementById('btnCollapseAll')?.addEventListener('click',()=>setAllDetails(false));"
                + "document.getElementById('btnExpandAll')?.addEventListener('click',()=>setAllDetails(true));"
                + "document.getElementById('search')?.addEventListener('input',filterToc);"
                // Tema cíclico e persistente
                + "(function(){const THEMES=['light','dim','dark'];function apply(t){document.body.classList.remove('light','dim','dark');if(t!=='dim')document.body.classList.add(t);localStorage.setItem('theme',t);}let cur=localStorage.getItem('theme')||'dim';apply(cur);const btn=document.getElementById('btnDark');btn&&btn.addEventListener('click',()=>{cur=THEMES[(THEMES.indexOf(cur)+1)%THEMES.length];apply(cur);});})();"
                // Auto-open do primeiro details ao navegar por âncora
                + "(function autoOpenOnAnchor(){function openFor(hash){if(!hash)return;const tgt=document.querySelector(hash);if(!tgt)return;const details=tgt.closest('section')?.querySelector('details.collapsible');if(details)details.open=true;}window.addEventListener('hashchange',()=>openFor(location.hash));openFor(location.hash);})();"
                + "enableCopy();buildToc();";
    }

    private String toJsArray(List<Link> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i=0;i<list.size();i++){
            Link l = list.get(i);
            if (i>0) sb.append(',');
            sb.append("{\"title\":\"").append(esc(l.title)).append("\",\"anchor\":\"").append(esc(l.anchor)).append("\"}");
        }
        sb.append("]");
        return sb.toString();
    }
}
