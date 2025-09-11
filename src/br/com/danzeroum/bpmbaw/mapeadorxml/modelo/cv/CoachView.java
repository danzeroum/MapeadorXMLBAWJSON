package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.cv;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coach.CoachLayout; // Reutilizando
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class CoachView {
    @XmlAttribute private String id;
    @XmlAttribute private String name;

    // --- Campos de Metadados e Configuração ---
    @XmlElement private long lastModified;
    @XmlElement private String lastModifiedBy;
    @XmlElement private String coachViewId;
    @XmlElement private boolean isTemplate;

    // O layout é um XML aninhado, mapeado como String para parsing posterior
    @XmlElement private String layout;

    @XmlElement private String loadJsFunction;
    @XmlElement private String unloadJsFunction;
    @XmlElement private String viewJsFunction;
    @XmlElement private String changeJsFunction;
    // ... outros campos simples como isMobileReady, guid, versionId, etc.

    @XmlElement(name = "amdDependency")
    private List<AmdDependency> amdDependencies;

    public List<AmdDependency> getAmdDependencies() {
        return amdDependencies;
    }
    // --- Listas de Configurações e Scripts ---
    @XmlElement(name = "bindingType")
    private List<BindingType> bindingTypes;

    @XmlElement(name = "configOption")
    private List<ConfigOption> configOptions;

    @XmlElement(name = "inlineScript")
    private List<InlineScript> inlineScripts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getCoachViewId() {
        return coachViewId;
    }

    public void setCoachViewId(String coachViewId) {
        this.coachViewId = coachViewId;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean template) {
        isTemplate = template;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getLoadJsFunction() {
        return loadJsFunction;
    }

    public void setLoadJsFunction(String loadJsFunction) {
        this.loadJsFunction = loadJsFunction;
    }

    public String getUnloadJsFunction() {
        return unloadJsFunction;
    }

    public void setUnloadJsFunction(String unloadJsFunction) {
        this.unloadJsFunction = unloadJsFunction;
    }

    public String getViewJsFunction() {
        return viewJsFunction;
    }

    public void setViewJsFunction(String viewJsFunction) {
        this.viewJsFunction = viewJsFunction;
    }

    public String getChangeJsFunction() {
        return changeJsFunction;
    }

    public void setChangeJsFunction(String changeJsFunction) {
        this.changeJsFunction = changeJsFunction;
    }

    public List<BindingType> getBindingTypes() {
        return bindingTypes;
    }

    public void setBindingTypes(List<BindingType> bindingTypes) {
        this.bindingTypes = bindingTypes;
    }

    public List<ConfigOption> getConfigOptions() {
        return configOptions;
    }

    public void setConfigOptions(List<ConfigOption> configOptions) {
        this.configOptions = configOptions;
    }

    public List<InlineScript> getInlineScripts() {
        return inlineScripts;
    }

    public void setInlineScripts(List<InlineScript> inlineScripts) {
        this.inlineScripts = inlineScripts;
    }
}