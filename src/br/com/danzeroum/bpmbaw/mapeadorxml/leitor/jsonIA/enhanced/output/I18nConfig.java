package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output;

import java.util.Map;

/**
 * I18n configuration
 */
class I18nConfig {
    private String defaultLocale;
    private Map<String, Map<String, String>> bundles;

    // Getters and setters...
    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public Map<String, Map<String, String>> getBundles() {
        return bundles;
    }

    public void setBundles(Map<String, Map<String, String>> bundles) {
        this.bundles = bundles;
    }
}
