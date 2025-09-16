package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

/**
 * Process UI Configuration V2+ - Interface de usuário separada da lógica
 *
 * @version 2.1.0
 * @since V2+ IA-Friendly Migration
 */
public class ProcessUIV2Plus {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("components")
    private List<UIComponentV2Plus> components;

    @JsonProperty("themes")
    private List<UIThemeV2Plus> themes;

    @JsonProperty("layouts")
    private List<UILayoutV2Plus> layouts;

    @JsonProperty("metadata")
    private UIMetadata metadata;

    public ProcessUIV2Plus() {
        this.components = new ArrayList<UIComponentV2Plus>();
        this.themes = new ArrayList<UIThemeV2Plus>();
        this.layouts = new ArrayList<UILayoutV2Plus>();
        this.metadata = new UIMetadata();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<UIComponentV2Plus> getComponents() { return components; }
    public void setComponents(List<UIComponentV2Plus> components) {
        this.components = components != null ? components : new ArrayList<UIComponentV2Plus>();
    }

    public List<UIThemeV2Plus> getThemes() { return themes; }
    public void setThemes(List<UIThemeV2Plus> themes) {
        this.themes = themes != null ? themes : new ArrayList<UIThemeV2Plus>();
    }

    public List<UILayoutV2Plus> getLayouts() { return layouts; }
    public void setLayouts(List<UILayoutV2Plus> layouts) {
        this.layouts = layouts != null ? layouts : new ArrayList<UILayoutV2Plus>();
    }

    public UIMetadata getMetadata() { return metadata; }
    public void setMetadata(UIMetadata metadata) {
        this.metadata = metadata != null ? metadata : new UIMetadata();
    }

    /**
     * Componente de UI V2Plus
     */
    public static class UIComponentV2Plus {
        private String id;
        private String type;
        private String name;
        private String description;
        private Map<String, Object> properties;
        private List<String> events;
        @JsonProperty("children") // Garante que será serializado no JSON
        private List<String> children; // Lista de IDs dos componentes filhos

        public UIComponentV2Plus() {
            this.properties = new HashMap<String, Object>();
            this.events = new ArrayList<String>();
            this.children = new ArrayList<String>(); // Inicializa a lista
        }


        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) {
            this.properties = properties != null ? properties : new HashMap<String, Object>();
        }

        public List<String> getEvents() { return events; }
        public void setEvents(List<String> events) {
            this.events = events != null ? events : new ArrayList<String>();
        }

        public List<String> getChildren() { return children; }
        public void setChildren(List<String> children) {
            this.children = children != null ? children : new ArrayList<String>();
        }

    }

    /**
     * Tema de UI V2Plus
     */
    public static class UIThemeV2Plus {
        private String id;
        private String name;
        private Map<String, String> colors;
        private Map<String, String> fonts;

        public UIThemeV2Plus() {
            this.colors = new HashMap<String, String>();
            this.fonts = new HashMap<String, String>();
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Map<String, String> getColors() { return colors; }
        public void setColors(Map<String, String> colors) {
            this.colors = colors != null ? colors : new HashMap<String, String>();
        }

        public Map<String, String> getFonts() { return fonts; }
        public void setFonts(Map<String, String> fonts) {
            this.fonts = fonts != null ? fonts : new HashMap<String, String>();
        }
    }

    /**
     * Layout de UI V2Plus
     */
    public static class UILayoutV2Plus {
        private String id;
        private String name;
        private String type;
        private Map<String, Object> configuration;

        public UILayoutV2Plus() {
            this.configuration = new HashMap<String, Object>();
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Map<String, Object> getConfiguration() { return configuration; }
        public void setConfiguration(Map<String, Object> configuration) {
            this.configuration = configuration != null ? configuration : new HashMap<String, Object>();
        }
    }

    /**
     * Metadados de UI V2Plus
     */
    public static class UIMetadata {
        public String version = "2.1.0";
        public String framework;
        public String lastModified;
        public Map<String, Object> customProperties = new HashMap<String, Object>();
    }

    @Override
    public String toString() {
        return String.format("ProcessUIV2Plus{id='%s', components=%d}", id, components.size());
    }


}