package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

import java.util.HashMap;
import java.util.Map;

public class I18nConfigV2 {
    private String version;
    private String defaultLocale;
    private Map<String, Object> bundles;

    public I18nConfigV2() {
        this.bundles = new HashMap<>();
    }

    // Getters and setters
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getDefaultLocale() { return defaultLocale; }
    public void setDefaultLocale(String defaultLocale) { this.defaultLocale = defaultLocale; }

    public Map<String, Object> getBundles() { return bundles; }
    public void setBundles(Map<String, Object> bundles) { this.bundles = bundles; }
}