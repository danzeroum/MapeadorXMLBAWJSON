package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIAv2;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.FormTask;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.GlobalUserTask;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Classe especialista em formatar e imprimir um relatório detalhado
 * de Coaches e suas Coach Views aninhadas, incluindo scripts e lógica de negócio. (Versão Final Corrigida)
 */
public class CoachReportPrinter {

    private final PrintWriter writer;

    public CoachReportPrinter(PrintWriter writer) {
        this.writer = writer;
    }

    public void printEmbeddedCoachSectionHeader(String serviceName, String coachName, String indent) {
        writer.println("\n---\n");
        writer.println(indent + "## 🖥️ Análise da Interface: " + coachName);
        writer.println(indent + "**Serviço Pai:** " + serviceName);
    }

    public void imprimirLayoutCoach(Layout layout, String serviceName, String coachName, String indent, Set<String> coachViewIds) {
        if (layout == null || layout.getLayoutItems() == null || layout.getLayoutItems().isEmpty()) return;
        printEmbeddedCoachSectionHeader(serviceName, coachName, indent);
        writer.println("\n" + indent + "### Estrutura dos Componentes");
        for (LayoutItem item : layout.getLayoutItems()) {
            imprimirLayoutItem(item, indent, coachViewIds);
        }
    }

    public void imprimirLayoutCoach(CoachLayout layout, String serviceName, String coachName, String indent, Set<String> coachViewIds) {
        if (layout == null || layout.getItems() == null || layout.getItems().isEmpty()) return;
        printEmbeddedCoachSectionHeader(serviceName, coachName, indent);
        writer.println("\n" + indent + "### Estrutura dos Componentes");
        for (br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item : layout.getItems()) {
            imprimirLayoutItem(item, indent, coachViewIds);
        }
    }

    private void imprimirLayoutItem(LayoutItem item, String indent, Set<String> coachViewIds) {
        if (item == null) return;
        String itemName = getCoachItemName(item);
        writer.println("\n" + indent + "- **Componente:** `" + itemName + "`");

        if (item.getViewUUID() != null) {
            writer.println(indent + "  - **Tipo de View (ID):** `" + item.getViewUUID() + "`");
            coachViewIds.add(item.getViewUUID());
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

    private void imprimirLayoutItem(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item, String indent, Set<String> coachViewIds) {
        if (item == null) return;
        String itemName = getCoachItemName(item);
        writer.println("\n" + indent + "- **Componente:** `" + itemName + "`");

        if (item.getViewUUID() != null) {
            writer.println(indent + "  - **Tipo de View (ID):** `" + item.getViewUUID() + "`");
            coachViewIds.add(item.getViewUUID());
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

    private <T> void imprimirDetalhesComuns(String binding, List<T> configs, String indent) {
        if (binding != null && !binding.isEmpty()) {
            writer.println(indent + "  - **Dado Associado (Binding):** `" + binding + "`");
        }
        if (configs != null && !configs.isEmpty()) {
            writer.println(indent + "  - **Configurações e Regras:**");
            configs.forEach(config -> {
                if (config instanceof ConfigData) {
                    imprimirConfigDataDetalhado(((ConfigData) config).getOptionName(), ((ConfigData) config).getValue(), indent + "    ");
                } else if (config instanceof br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData) {
                    imprimirConfigDataDetalhado(((br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData) config).getOptionName(), ((br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData) config).getValue(), indent + "    ");
                }
            });
        }
    }

    private void imprimirConfigDataDetalhado(String optionName, String value, String indent) {
        if (value == null || value.trim().isEmpty() || optionName == null || optionName.equals("@label") || optionName.equals("@customHTML.textContent")) return;

        String formattedName = formatarOptionName(optionName);

        if (optionName.toLowerCase().startsWith("event.on")) {
            writer.println(indent + "- **" + formattedName + ":**");
            writer.println(indent + "  ```javascript");
            writer.println(indent + "  " + value.trim().replace("\n", "\n" + indent + "  "));
            writer.println(indent + "  ```");
        } else if (optionName.equals("selectionService")) {
            writer.println(indent + "- **" + formattedName + ":** Chama o serviço AJAX com ID `" + value + "` para buscar dados.");
        } else {
            writer.println(indent + "- **" + formattedName + ":** `" + value + "`");
        }
    }

    private void imprimirCustomHtmlContentLegacy(List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData> configs, String indent) {
        configs.stream()
                .filter(config -> "@customHTML.textContent".equals(config.getOptionName()) && config.getValue() != null && !config.getValue().trim().isEmpty())
                .findFirst()
                .ifPresent(config -> {
                    writer.println(indent + "- **Conteúdo HTML/CSS:**");
                    writer.println(indent + "  ```html");
                    writer.println(config.getValue().trim().replace("&quot;", "\"").replace("&#xD;", ""));
                    writer.println(indent + "  ```");
                });
    }

    private void imprimirCustomHtmlContentModern(List<ConfigData> configs, String indent) {
        configs.stream()
                .filter(config -> "@customHTML.textContent".equals(config.getOptionName()) && config.getValue() != null && !config.getValue().trim().isEmpty())
                .findFirst()
                .ifPresent(config -> {
                    writer.println(indent + "- **Conteúdo HTML/CSS:**");
                    writer.println(indent + "  ```html");
                    writer.println(config.getValue().trim().replace("&quot;", "\"").replace("&#xD;", ""));
                    writer.println(indent + "  ```");
                });
    }

    public void imprimirScriptsPreExecucao(Item item, String indent) {
        if (item.getProcessPrePosts() == null) return;
        boolean hasScript = item.getProcessPrePosts().stream().anyMatch(p -> p.getLocation() == 1 && p.getScript() != null && !p.getScript().trim().isEmpty());
        if (!hasScript) return;

        writer.println("\n" + indent + "### 📜 Lógica de Inicialização (Scripts de Pré-Execução)");
        for (ProcessPrePost prePost : item.getProcessPrePosts()) {
            if (prePost.getLocation() == 1 && prePost.getScript() != null && !prePost.getScript().trim().isEmpty()) {
                writer.println(indent + "```javascript");
                writer.println(prePost.getScript().trim());
                writer.println(indent + "```");
            }
        }
    }

    public void imprimirEventosDeBoundary(Item item, String indent) {
        if (item.getTwComponent() == null || item.getTwComponent().getCoachNGBoundaryEvents() == null || item.getTwComponent().getCoachNGBoundaryEvents().isEmpty()) {
            return;
        }
        writer.println("\n" + indent + "### ⚡ Eventos e Navegação (Boundary Events)");
        writer.println(indent + "| Elemento da Interface (View Path) | Evento Disparado | Valida a Tela? |");
        writer.println(indent + "| :--- | :--- | :--- |");
        for (CoachNGBoundaryEvents event : item.getTwComponent().getCoachNGBoundaryEvents()) {
            writer.println(indent + "| `" + event.getViewPath() + "` | `" + event.getEventLabel() + "` | " + (event.getFireValidation() == 1 ? "Sim" : "Não") + " |");
        }
    }

    public void imprimirDetalhesDeTodasAsCoachViews(ProcessLoader loader, Set<String> coachViewIds) {
        writer.println("\n\n---\n");
        writer.println("## 📚 Análise Detalhada das Coach Views Utilizadas");

        for (String cvId : coachViewIds) {
            Object artifact = loader.getArtefatoDoCache(cvId);
            if (artifact instanceof Teamworks) {
                CoachView cv = ((Teamworks) artifact).getCoachView();
                if (cv != null) {
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

    private String formatarOptionName(String optionName) {
        if (optionName.startsWith("@")) optionName = optionName.substring(1);
        String formatted = optionName.replaceAll("([A-Z])", " $1");
        return formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
    }

    private boolean isCustomHtmlModern(List<ConfigData> configs) {
        if (configs == null) return false;
        return configs.stream().anyMatch(c -> "@customHTML.textContent".equals(c.getOptionName()));
    }

    private boolean isCustomHtmlLegacy(List<br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData> configs) {
        if (configs == null) return false;
        return configs.stream().anyMatch(c -> "@customHTML.textContent".equals(c.getOptionName()));
    }

    public void printCoachViewBindings(CoachView cv, String indent) { if (cv.getBindingTypes() != null && !cv.getBindingTypes().isEmpty()) { writer.println(indent + "\n#### 🔗 Binding de Dados Principal"); writer.println(indent + "| Nome | Tipo de Dado | É uma Lista? |"); writer.println(indent + "| :--- | :--- | :--- |"); for (BindingType binding : cv.getBindingTypes()) { writer.println(indent + "| `" + binding.getName() + "` | `" + binding.getClassId() + "` | " + (binding.isList() ? "Sim" : "Não") + " |"); } } }
    public void printCoachViewConfigOptions(CoachView cv, String indent) { if (cv.getConfigOptions() != null && !cv.getConfigOptions().isEmpty()) { writer.println(indent + "\n#### ⚙️ Opções de Configuração"); writer.println(indent + "| Nome | Rótulo (Label) | Tipo |"); writer.println(indent + "| :--- | :--- | :--- |"); for (ConfigOption option : cv.getConfigOptions()) { writer.println(indent + "| `" + option.getName() + "` | " + option.getLabel() + " | `" + option.getPropertyType() + "` |"); } } }
    public void printCoachViewInlineScripts(CoachView cv, String indent) { if (cv.getInlineScripts() != null && !cv.getInlineScripts().isEmpty()) { writer.println(indent + "\n#### 📜 Lógica Interna (Behavior e Eventos)"); for (InlineScript script : cv.getInlineScripts()) { writer.println(indent + "- **Tipo de Script: " + script.getScriptType().toUpperCase() + " (Nome: " + script.getName() + ")**"); writer.println(indent + "  ```javascript"); writer.println(script.getScriptBlock()); writer.println(indent + "  ```"); } } }
    public void printCoachViewAmdDependencies(CoachView cv, String indent) { if (cv.getAmdDependencies() != null && !cv.getAmdDependencies().isEmpty()) { writer.println(indent + "\n#### 📦 Dependências de Módulos (AMD)"); writer.println(indent + "| Módulo | Alias |"); writer.println(indent + "| :--- | :--- |"); for (AmdDependency dep : cv.getAmdDependencies()) { writer.println(indent + "| `" + dep.getModuleId() + "` | `" + dep.getFunctionArgument() + "` |"); } } }
    private String getCoachItemName(LayoutItem item) { if (item == null) return "N/A"; return Optional.ofNullable(item.getConfigData()).flatMap(configs -> configs.stream().filter(c -> "@label".equals(c.getOptionName()) && c.getValue() != null && !c.getValue().isEmpty()).map(ConfigData::getValue).findFirst()).orElse(item.getLayoutItemId() != null ? item.getLayoutItemId() : "Sem ID"); }
    private String getCoachItemName(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.LayoutItem item) { if (item == null) return "N/A"; return Optional.ofNullable(item.getConfigData()).flatMap(configs -> configs.stream().filter(c -> c != null && "@label".equals(c.getOptionName()) && c.getValue() != null && !c.getValue().isEmpty()).map(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.ConfigData::getValue).findFirst()).orElse(item.getLayoutItemId() != null ? item.getLayoutItemId() : "Sem ID"); }
}