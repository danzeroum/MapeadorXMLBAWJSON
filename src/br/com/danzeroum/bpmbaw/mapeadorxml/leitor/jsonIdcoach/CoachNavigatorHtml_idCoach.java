package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIdcoach;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Process;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.LayoutItem;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv.CoachView;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.TWComponent;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
        import java.util.stream.Collectors;

/**
 * Navigator para Coaches/Coach Views voltado ao printer HTML (CoachReportPrinterHtml_idCoach).
 * - NÃO gera Markdown; toda a saída é via printer HTML.
 * - Descobre CoachViews (filtra "padrão") e passa para impressão detalhada no final.
 * - Lê layouts legados (Item.layoutData/TWComponent) e modernos (BPMN FormTask).
 * - Evita ConcurrentModification fazendo snapshot do cache antes de iterar.
 */
public class CoachNavigatorHtml_idCoach {

    private final CoachReportPrinterHtml_idCoach printer;
    private final ProcessLoader_idCoach loader;
    private final Set<String> nomesPadrao;

    private JAXBContext coachLayoutContext;

    /** Coach Views (não-padrão) descobertas */
    private final Set<String> discoveredCoachViewIds = new LinkedHashSet<>();

    // contadores diagnósticos
    private int cntItemsCoachNg = 0;
    private int cntLayoutsLegadosOk = 0;
    private int cntFormTasks = 0;

    public CoachNavigatorHtml_idCoach(CoachReportPrinterHtml_idCoach printer,
                                  ProcessLoader_idCoach loader,
                                  Set<String> nomesPadrao) {
        this.printer = printer;
        this.loader = loader;
        this.nomesPadrao = (nomesPadrao != null) ? new HashSet<>(nomesPadrao) : Collections.emptySet();
        try {
            this.coachLayoutContext = JAXBContext.newInstance(CoachLayout.class);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar JAXBContext para CoachLayout", e);
        }
    }

    /* ============================ ORQUESTRAÇÃO ============================ */

    public void runAnalysis(String artifactId) {
        long t0 = System.currentTimeMillis();
        resetCounters();

        // 1) Descoberta inicial a partir da raiz + varredura global para cobrir fallback de impressão
        visitArtifactToDiscoverCoachViews(artifactId);
        globalDiscoveryOverCache();

        // 2) Impressão a partir da raiz; se vazio, fallback cache completo
        boolean imprimiu = imprimirRelatorioRaiz(artifactId);
        if (imprimiu && (cntItemsCoachNg == 0 && cntLayoutsLegadosOk == 0 && cntFormTasks == 0)) {
            System.out.println("[LOG] Raiz imprimida mas sem resultados úteis. Fallback: varrendo o cache inteiro…");
            imprimirTodosTeamworksDoCache();
        } else if (!imprimiu) {
            System.out.println("[LOG] Raiz não suportada. Fallback: varrendo o cache inteiro…");
            imprimirTodosTeamworksDoCache();
        }

        // 3) Carrega em lote as CoachViews não-padrão e imprime detalhes
        List<String> toLoad = discoveredCoachViewIds.stream()
                .filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty())
                .distinct().collect(Collectors.toList());

        System.out.println("[LOG] Coach Views descobertas (não-padrão): " + toLoad.size());
        for (String id : toLoad) {
            try { loader.loadArtefatoSeNaoExistir(id); }
            catch (Exception e) { System.err.println("[WARN] Falha ao carregar Coach View " + id + ": " + e.getMessage()); }
        }

        printer.imprimirDetalhesDeTodasAsCoachViews(loader, discoveredCoachViewIds);

        long t1 = System.currentTimeMillis();
        System.out.println("[RESUMO] Itens CoachNG: " + cntItemsCoachNg
                + " | Layouts legados parseados: " + cntLayoutsLegadosOk
                + " | FormTasks BPMN: " + cntFormTasks
                + " | CoachViews (não-padrão): " + discoveredCoachViewIds.size()
                + " | Tempo: " + (t1 - t0) + "ms | Gerado em: " +
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    private void resetCounters() {
        cntItemsCoachNg = 0;
        cntLayoutsLegadosOk = 0;
        cntFormTasks = 0;
        discoveredCoachViewIds.clear();
    }

    private boolean imprimirRelatorioRaiz(String artifactId) {
        Object root = loader.getArtefatoDoCache(artifactId);
        if (root == null) return false;

        if (root instanceof Teamworks) {
            analyzeAndPrintTeamworks((Teamworks) root);
            return true;
        }
        if (root instanceof Definitions) {
            imprimirDetalhesBpmn(processNameSafe((Definitions) root), (Definitions) root);
            return true;
        }
        return false;
    }

    private void imprimirTodosTeamworksDoCache() {
        List<Object> snap = new ArrayList<>(loader.getCacheDeArtefatos().values());
        snap.stream()
                .filter(Teamworks.class::isInstance)
                .map(Teamworks.class::cast)
                .sorted(Comparator.comparing(t -> t.getProcess() != null ? safe(t.getProcess().getName()) : ""))
                .forEach(this::analyzeAndPrintTeamworks);
    }

    /* ============================ IMPRESSÃO ============================ */

    private void analyzeAndPrintTeamworks(Teamworks tw) {
        if (tw == null || tw.getProcess() == null) return;

        String serviceName = safe(tw.getProcess().getName());

        // Legado: Items -> CoachNG
        if (tw.getProcess().getItems() != null) {
            for (Item item : tw.getProcess().getItems()) {
                if (!"CoachNG".equalsIgnoreCase(item.getTWComponentName())) continue;
                cntItemsCoachNg++;

                // Pre/Pós + Boundary
                printer.imprimirScriptsPreExecucao(item, "");
                printer.imprimirEventosDeBoundary(item, "");

                // Layout legado do item
                String layoutXml = extractLayoutXmlFromItem(item);
                if (notBlank(layoutXml)) {
                    try {
                        CoachLayout layout = parseLegacyCoachLayout(layoutXml);
                        cntLayoutsLegadosOk++;
                        printer.imprimirLayoutCoach(layout, serviceName, safe(item.getName()), "", discoveredCoachViewIds);
                    } catch (Exception e) {
                        System.err.println("[ERRO] Parse CoachLayout do item '" + safe(item.getName()) + "': " + e.getMessage());
                        System.err.println("[ERRO] Prévia do layoutXml (primeiros 200 chars): "
                                + layoutXml.substring(0, Math.min(200, layoutXml.length())));
                    }
                } else {
                    System.out.println("[LOG] Item '" + safe(item.getName()) + "': layoutXml vazio/nulo (verifique Item.layoutData/TWComponent).");
                }
            }
        }

        // Moderno: coachflow → Definitions
        if (tw.getProcess().getCoachflow() != null &&
                tw.getProcess().getCoachflow().getDefinitions() != null) {
            imprimirDetalhesBpmn(serviceName, tw.getProcess().getCoachflow().getDefinitions());
        }
    }

    private void imprimirDetalhesBpmn(String processName, Definitions definitions) {
        if (definitions == null) return;

        Process proc = definitions.getProcess();
        if (proc != null && proc.getFlowElements() != null) {
            traverseBpmnElementsForPrint(proc.getFlowElements(), processName);
        }

        GlobalUserTask gut = definitions.getGlobalUserTask();
        if (gut != null && gut.getImplementation() != null && gut.getImplementation().getFlowElements() != null) {
            traverseBpmnElementsForPrint(gut.getImplementation().getFlowElements(), processName);
        }
    }

    private void traverseBpmnElementsForPrint(List<Object> elements, String processName) {
        if (elements == null) return;
        for (Object el : elements) {
            if (el instanceof FormTask) {
                FormTask form = (FormTask) el;
                if (form.getFormDefinition() != null &&
                        form.getFormDefinition().getCoachDefinition() != null &&
                        form.getFormDefinition().getCoachDefinition().getLayout() != null) {
                    cntFormTasks++;
                    printer.imprimirLayoutCoach(
                            form.getFormDefinition().getCoachDefinition().getLayout(),
                            processName,
                            safe(form.getName()),
                            "",
                            discoveredCoachViewIds
                    );
                }
            } else if (el instanceof SubProcess) {
                traverseBpmnElementsForPrint(((SubProcess) el).getFlowElements(), processName);
            }
        }
    }

    /* ============================ DESCOBERTA (coleta de CVs custom) ============================ */

    public void visitArtifactToDiscoverCoachViews(String artifactId) {
        Object root = loader.getArtefatoDoCache(artifactId);
        if (root == null) return;

        if (root instanceof Teamworks) {
            Teamworks tw = (Teamworks) root;

            // Legado
            if (tw.getProcess() != null && tw.getProcess().getItems() != null) {
                for (Item item : tw.getProcess().getItems()) {
                    if (!"CoachNG".equalsIgnoreCase(item.getTWComponentName())) continue;
                    String xml = extractLayoutXmlFromItem(item);
                    collectCoachViewIdsFromLegacyLayoutXml(xml);
                }
            }

            // Moderno
            if (tw.getProcess() != null &&
                    tw.getProcess().getCoachflow() != null &&
                    tw.getProcess().getCoachflow().getDefinitions() != null) {
                collectCoachViewsFromDefinitions(tw.getProcess().getCoachflow().getDefinitions());
            }
            return;
        }

        if (root instanceof Definitions) {
            collectCoachViewsFromDefinitions((Definitions) root);
        }
    }

    /** Varredura global para garantir que as CVs coletadas cubram o fallback de impressão. */
    private void globalDiscoveryOverCache() {
        List<Object> snap = new ArrayList<>(loader.getCacheDeArtefatos().values());
        for (Object o : snap) {
            if (!(o instanceof Teamworks)) continue;
            Teamworks tw = (Teamworks) o;

            if (tw.getProcess() != null && tw.getProcess().getItems() != null) {
                for (Item item : tw.getProcess().getItems()) {
                    if (!"CoachNG".equalsIgnoreCase(item.getTWComponentName())) continue;
                    String xml = extractLayoutXmlFromItem(item);
                    collectCoachViewIdsFromLegacyLayoutXml(xml);
                }
            }

            if (tw.getProcess() != null &&
                    tw.getProcess().getCoachflow() != null &&
                    tw.getProcess().getCoachflow().getDefinitions() != null) {
                collectCoachViewsFromDefinitions(tw.getProcess().getCoachflow().getDefinitions());
            }
        }
    }

    private void collectCoachViewsFromDefinitions(Definitions defs) {
        Process proc = defs.getProcess();
        if (proc != null && proc.getFlowElements() != null) {
            traverseBpmnElementsForDiscovery(proc.getFlowElements());
        }
        GlobalUserTask gut = defs.getGlobalUserTask();
        if (gut != null && gut.getImplementation() != null && gut.getImplementation().getFlowElements() != null) {
            traverseBpmnElementsForDiscovery(gut.getImplementation().getFlowElements());
        }
    }

    private void traverseBpmnElementsForDiscovery(List<Object> elements) {
        if (elements == null) return;
        for (Object el : elements) {
            if (el instanceof FormTask) {
                FormTask form = (FormTask) el;
                if (form.getFormDefinition() != null &&
                        form.getFormDefinition().getCoachDefinition() != null &&
                        form.getFormDefinition().getCoachDefinition().getLayout() != null &&
                        form.getFormDefinition().getCoachDefinition().getLayout().getLayoutItems() != null) {

                    for (LayoutItem it : form.getFormDefinition().getCoachDefinition().getLayout().getLayoutItems()) {
                        collectCoachViewIdsFromModernLayoutItem(it);
                    }
                }
            } else if (el instanceof SubProcess) {
                traverseBpmnElementsForDiscovery(((SubProcess) el).getFlowElements());
            }
        }
    }

    /* ============================ COLETORES (com filtro padrão) ============================ */

    private void collectCoachViewIdsFromLegacyLayoutXml(String layoutXml) {
        if (!notBlank(layoutXml)) return;
        try {
            CoachLayout layout = parseLegacyCoachLayout(layoutXml);
            if (layout.getItems() == null) return;
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem li : layout.getItems()) {
                collectCoachViewIdsFromLegacyLayoutItem(li);
            }
        } catch (Exception e) {
            System.err.println("[WARN] Não foi possível parsear CoachLayout legado p/ coleta: " + e.getMessage());
        }
    }

    private void collectCoachViewIdsFromLegacyLayoutItem(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item) {
        if (item == null) return;
        if (notBlank(item.getViewUUID())) {
            String id = clean(item.getViewUUID());
            if (!isPadraoView(id)) discoveredCoachViewIds.add(id);
        }
        if (item.getContentBoxContributions() != null) {
            for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem.ContentBoxContribution c : item.getContentBoxContributions()) {
                if (c.getContributions() != null) {
                    for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem nested : c.getContributions()) {
                        collectCoachViewIdsFromLegacyLayoutItem(nested);
                    }
                }
            }
        }
    }

    private void collectCoachViewIdsFromModernLayoutItem(LayoutItem item) {
        if (item == null) return;
        if (notBlank(item.getViewUUID())) {
            String id = clean(item.getViewUUID());
            if (!isPadraoView(id)) discoveredCoachViewIds.add(id);
        }
        if (item.getContentBoxContribs() != null) {
            item.getContentBoxContribs().forEach(box -> {
                if (box.getContributions() != null) {
                    box.getContributions().forEach(this::collectCoachViewIdsFromModernLayoutItem);
                }
            });
        }
    }

    /* ============================ SUPORTE ============================ */

    private CoachLayout parseLegacyCoachLayout(String layoutXml) throws Exception {
        String xml = unescapeXmlDeep(layoutXml);
        Unmarshaller u = this.coachLayoutContext.createUnmarshaller();
        return (CoachLayout) u.unmarshal(new StringReader(xml));
    }

    /** Desescapa (&amp;lt; → &lt; → <) até 2 vezes */
    private String unescapeXmlDeep(String s) {
        if (s == null) return null;
        String prev, cur = s;
        for (int i = 0; i < 2; i++) {
            prev = cur;
            cur = cur.replace("&amp;lt;", "&lt;").replace("&amp;gt;", "&gt;").replace("&amp;quot;", "&quot;");
            cur = cur.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");
            if (cur.equals(prev)) break;
        }
        return cur;
    }

    /**
     * Extrai XML do layout legado a partir de Item.layoutData (ou fallback no TWComponent).
     * Suporta getters/fields String/byte[] e marshal JAXB com extração de <coachLayout>…</coachLayout>.
     */
    private String extractLayoutXmlFromItem(Item item) {
        // 1) Via item.getLayoutData()
        try {
            Object layoutDataObj = item.getLayoutData();
            String xml = extractXmlFromLayoutDataObject(layoutDataObj);
            if (notBlank(xml)) return xml;
        } catch (Exception ignore) { }

        // 2) Fallback via TWComponent
        try {
            TWComponent comp = item.getTwComponent();
            if (comp != null) {
                for (Method m : comp.getClass().getMethods()) {
                    String n = m.getName();
                    if (m.getParameterCount() == 0 && (n.equals("getLayoutData") || n.equals("getLayout")
                            || n.equals("getLayoutXml") || n.equals("getLayoutXML"))) {
                        Object obj = m.invoke(comp);
                        String xml = extractXmlFromLayoutDataObject(obj);
                        if (notBlank(xml)) return xml;
                    }
                }
            }
        } catch (Exception ignore) { }

        return null;
    }

    private String extractXmlFromLayoutDataObject(Object obj) {
        if (obj == null) return null;

        if (obj instanceof String) return (String) obj;
        if (obj instanceof byte[]) return new String((byte[]) obj, StandardCharsets.UTF_8);

        String byGetter = getStringOrBytesViaGetters(obj, "getData", "getValue", "getXml", "getLayout", "getLayoutXml", "getLayoutXML");
        if (notBlank(byGetter)) return byGetter;

        String byField = getStringOrBytesViaFields(obj, "data", "value", "xml", "layout", "layoutXml", "layoutXML");
        if (notBlank(byField)) return byField;

        String marshalled = marshalObjectToXml(obj);
        if (!notBlank(marshalled)) return null;

        String encodedInner = extractEncodedInnerXml(marshalled);
        String unescaped = unescapeXmlDeep(encodedInner != null ? encodedInner : marshalled);

        String inner = extractTagBlock(unescaped, "coachLayout");
        if (notBlank(inner)) return inner;

        inner = extractTagBlock(unescaped, "layout");
        return inner;
    }

    private String getStringOrBytesViaGetters(Object obj, String... names) {
        for (String g : names) {
            try {
                Method m = obj.getClass().getMethod(g);
                if (m.getParameterCount() != 0) continue;
                Class<?> rt = m.getReturnType();
                Object v = m.invoke(obj);
                if (v == null) continue;

                if (rt == String.class) {
                    String s = v.toString();
                    if (notBlank(s) && !looksLikeJavaToString(s)) return s;
                }
                if (rt == byte[].class) {
                    byte[] b = (byte[]) v;
                    if (b.length > 0) return new String(b, StandardCharsets.UTF_8);
                }
            } catch (Exception ignore) { }
        }
        return null;
    }

    private String getStringOrBytesViaFields(Object obj, String... names) {
        Class<?> c = obj.getClass();
        while (c != null && c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                try {
                    String fn = f.getName();
                    boolean candidate = false;
                    for (String name : names) if (fn.equalsIgnoreCase(name)) { candidate = true; break; }
                    if (!candidate) continue;

                    f.setAccessible(true);
                    Object v = f.get(obj);
                    if (v == null) continue;

                    if (v instanceof String) {
                        String s = (String) v;
                        if (notBlank(s) && !looksLikeJavaToString(s)) return s;
                    }
                    if (v instanceof byte[]) {
                        byte[] b = (byte[]) v;
                        if (b.length > 0) return new String(b, StandardCharsets.UTF_8);
                    }
                } catch (Exception ignore) { }
            }
            c = c.getSuperclass();
        }
        return null;
    }

    private String marshalObjectToXml(Object obj) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(obj.getClass());
            Marshaller mar = ctx.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            mar.marshal(obj, sw);
            return sw.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private String extractEncodedInnerXml(String s) {
        if (s == null) return null;
        int a = s.indexOf("&lt;");
        int b = s.lastIndexOf("&gt;");
        if (a >= 0 && b > a) return s.substring(a, b + 4);
        return null;
    }

    private String extractTagBlock(String xml, String tagName) {
        if (xml == null) return null;
        String open = "<" + tagName;
        int a = xml.indexOf(open);
        if (a < 0) return null;
        String close = "</" + tagName + ">";
        int b = xml.indexOf(close, a);
        if (b < 0) return null;
        b += close.length();
        return xml.substring(a, b);
    }

    private boolean looksLikeJavaToString(String s) {
        return s != null && s.matches("^[\\w.$]+@\\p{XDigit}+(-\\p{XDigit}+)?$");
    }

    private static String processNameSafe(Definitions d) {
        if (d == null) return "(Sem nome)";
        Process p = d.getProcess();
        return (p != null && p.getName() != null) ? p.getName() : "(Sem nome)";
    }

    private static boolean notBlank(String s) { return s != null && !s.trim().isEmpty(); }
    private static String safe(String s) { return (s == null || s.trim().isEmpty()) ? "(Sem nome)" : s.trim(); }

    /* ===== Filtro de padrão ===== */

    private String clean(String id) {
        try {
            Method m = loader.getClass().getMethod("getCleanId", String.class);
            Object v = m.invoke(loader, id);
            return (v != null) ? String.valueOf(v) : id;
        } catch (Exception ignore) {
            return id;
        }
    }

    private boolean isPadraoView(String cleanId) {
        try {
            loader.loadArtefatoSeNaoExistir(cleanId);
        } catch (Exception ignore) { }
        Object artifact = loader.getArtefatoDoCache(cleanId);
        if (artifact instanceof Teamworks) {
            CoachView cv = ((Teamworks) artifact).getCoachView();
            return (cv != null && cv.getName() != null && nomesPadrao.contains(cv.getName()));
        }
        return false;
    }
}
