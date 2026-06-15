package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output;

import java.util.List;

/**
 * Security configuration
 */
class SecurityConfig {
    private boolean scriptSandbox;
    private List<String> allowedAPIs;
    private List<String> prohibitedAPIs;
    private List<String> piiFields;
    private String dataClassification;
    private String auditLevel;

    // Getters and setters...
    public boolean isScriptSandbox() {
        return scriptSandbox;
    }

    public void setScriptSandbox(boolean scriptSandbox) {
        this.scriptSandbox = scriptSandbox;
    }

    public List<String> getAllowedAPIs() {
        return allowedAPIs;
    }

    public void setAllowedAPIs(List<String> allowedAPIs) {
        this.allowedAPIs = allowedAPIs;
    }

    public List<String> getProhibitedAPIs() {
        return prohibitedAPIs;
    }

    public void setProhibitedAPIs(List<String> prohibitedAPIs) {
        this.prohibitedAPIs = prohibitedAPIs;
    }

    public List<String> getPiiFields() {
        return piiFields;
    }

    public void setPiiFields(List<String> piiFields) {
        this.piiFields = piiFields;
    }

    public String getDataClassification() {
        return dataClassification;
    }

    public void setDataClassification(String dataClassification) {
        this.dataClassification = dataClassification;
    }

    public String getAuditLevel() {
        return auditLevel;
    }

    public void setAuditLevel(String auditLevel) {
        this.auditLevel = auditLevel;
    }
}
