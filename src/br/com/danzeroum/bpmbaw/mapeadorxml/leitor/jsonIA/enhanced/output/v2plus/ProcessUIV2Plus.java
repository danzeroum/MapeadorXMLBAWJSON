package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.*;

/**
 * Process UI V2+ - Interface de Usuário IA-Friendly
 *
 * @version 2.1.0
 * @since V2+ IA-Friendly Migration
 */
@JsonPropertyOrder({"hints", "i18n", "themes", "layouts", "metadata"})
public class ProcessUIV2Plus {

    @JsonProperty("hints")
    private UIHints hints;

    @JsonProperty("i18n")
    private I18nConfiguration i18n;

    @JsonProperty("themes")
    private List<UITheme> themes;

    @JsonProperty("layouts")
    private List<UILayout> layouts;

    @JsonProperty("metadata")
    private UIMetadata metadata;

    public ProcessUIV2Plus() {
        this.hints = new UIHints();
        this.i18n = new I18nConfiguration();
        this.themes = new ArrayList<UITheme>();
        this.layouts = new ArrayList<UILayout>();
        this.metadata = new UIMetadata();
    }

    // Getters and Setters
    public UIHints getHints() { return hints; }
    public void setHints(UIHints hints) { this.hints = hints != null ? hints : new UIHints(); }

    public I18nConfiguration getI18n() { return i18n; }
    public void setI18n(I18nConfiguration i18n) { this.i18n = i18n != null ? i18n : new I18nConfiguration(); }

    public List<UITheme> getThemes() { return themes; }
    public void setThemes(List<UITheme> themes) {
        this.themes = themes != null ? themes : new ArrayList<UITheme>();
    }

    public List<UILayout> getLayouts() { return layouts; }
    public void setLayouts(List<UILayout> layouts) {
        this.layouts = layouts != null ? layouts : new ArrayList<UILayout>();
    }

    public UIMetadata getMetadata() { return metadata; }
    public void setMetadata(UIMetadata metadata) {
        this.metadata = metadata != null ? metadata : new UIMetadata();
    }

    public static class UIHints {
        public Map<String, String> fieldHints = new HashMap<String, String>();
        public Map<String, String> stepHints = new HashMap<String, String>();
        public Map<String, String> validationHints = new HashMap<String, String>();
    }

    public static class I18nConfiguration {
        public String defaultLocale = "pt_BR";
        public List<String> supportedLocales = new ArrayList<String>();
        public Map<String, Map<String, String>> bundles = new HashMap<String, Map<String, String>>();
    }

    public static class UITheme {
        public String name;
        public String description;
        public Map<String, String> colors = new HashMap<String, String>();
        public Map<String, String> fonts = new HashMap<String, String>();
    }

    public static class UILayout {
        public String name;
        public String description;
        public String type;
        public Map<String, Object> configuration = new HashMap<String, Object>();
    }

    public static class UIMetadata {
        public String version = "2.1.0";
        public String framework;
        public Map<String, Object> customProperties = new HashMap<String, Object>();
    }

    @Override
    public String toString() {
        return String.format("ProcessUIV2Plus{themes=%d, layouts=%d, locale='%s'}",
                themes.size(), layouts.size(), i18n.defaultLocale);
    }
}