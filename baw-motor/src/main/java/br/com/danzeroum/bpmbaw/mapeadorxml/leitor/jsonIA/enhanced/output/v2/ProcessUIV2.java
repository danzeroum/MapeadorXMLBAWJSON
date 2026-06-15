package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Process UI V2 - Enhanced user interface definitions
 */
public class ProcessUIV2 {

    @JsonProperty("$id")
    private String id;

    @JsonProperty("schemaVersion")
    private String schemaVersion = "2.0.0";

    private List<UIComponent> components;
    private List<UILayout> layouts;
    private UIMetadata metadata;

    public ProcessUIV2() {
        this.components = new ArrayList<>();
        this.layouts = new ArrayList<>();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(String schemaVersion) { this.schemaVersion = schemaVersion; }

    public List<UIComponent> getComponents() { return components; }
    public void setComponents(List<UIComponent> components) {
        this.components = components != null ? components : new ArrayList<>();
    }

    public List<UILayout> getLayouts() { return layouts; }
    public void setLayouts(List<UILayout> layouts) {
        this.layouts = layouts != null ? layouts : new ArrayList<>();
    }

    public UIMetadata getMetadata() { return metadata; }
    public void setMetadata(UIMetadata metadata) { this.metadata = metadata; }

    public static class UIComponent {
        private String id;
        private String type;
        private String label;
        private Map<String, Object> properties;

        public UIComponent() {
            this.properties = new HashMap<>();
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) {
            this.properties = properties != null ? properties : new HashMap<>();
        }
    }

    public static class UILayout {
        private String id;
        private String name;
        private List<String> componentIds;

        public UILayout() {
            this.componentIds = new ArrayList<>();
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public List<String> getComponentIds() { return componentIds; }
        public void setComponentIds(List<String> componentIds) {
            this.componentIds = componentIds != null ? componentIds : new ArrayList<>();
        }
    }

    public static class UIMetadata {
        private String version;
        private String framework;
        private Map<String, String> customAttributes;

        public UIMetadata() {
            this.customAttributes = new HashMap<>();
        }

        // Getters and setters
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public String getFramework() { return framework; }
        public void setFramework(String framework) { this.framework = framework; }

        public Map<String, String> getCustomAttributes() { return customAttributes; }
        public void setCustomAttributes(Map<String, String> customAttributes) {
            this.customAttributes = customAttributes != null ? customAttributes : new HashMap<>();
        }
    }
}