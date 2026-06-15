package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model;

import java.util.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model.*;

public class BusinessContext {
    private String domain;
    private Set<String> domainTerms = new HashSet<>();
    private Set<String> mainEntities = new HashSet<>();
    private List<BusinessRule> businessRules = new ArrayList<>();
    private List<IntegrationPoint> integrationPoints = new ArrayList<>();
    private Set<String> userRoles = new HashSet<>();
    private Map<String, String> glossary = new HashMap<>();

    public BusinessContext() {}

    // Getters e Setters
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public Set<String> getDomainTerms() { return domainTerms; }
    public void setDomainTerms(Set<String> domainTerms) { this.domainTerms = domainTerms; }

    public Set<String> getMainEntities() { return mainEntities; }
    public void setMainEntities(Set<String> mainEntities) { this.mainEntities = mainEntities; }

    public List<BusinessRule> getBusinessRules() { return businessRules; }
    public void setBusinessRules(List<BusinessRule> businessRules) { this.businessRules = businessRules; }

    public List<IntegrationPoint> getIntegrationPoints() { return integrationPoints; }
    public void setIntegrationPoints(List<IntegrationPoint> integrationPoints) { this.integrationPoints = integrationPoints; }

    public Set<String> getUserRoles() { return userRoles; }
    public void setUserRoles(Set<String> userRoles) { this.userRoles = userRoles; }

    public Map<String, String> getGlossary() { return glossary; }
    public void setGlossary(Map<String, String> glossary) { this.glossary = glossary; }
}