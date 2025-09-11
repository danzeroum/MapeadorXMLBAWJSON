// BusinessContextExtractor.java
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.services;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ProcessLoader;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model.BusinessContext;
import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model.BusinessRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BusinessContextExtractor {

    public BusinessContext extractContext(String processId, ProcessLoader loader) {
        BusinessContext context = new BusinessContext();

        try {
            // Inferir domínio baseado no ID/nome do processo
            context.setDomain(inferDomain(processId));

            // Extrair termos básicos
            Set<String> domainTerms = extractDomainTerms(processId);
            context.setDomainTerms(domainTerms);

            // Extrair entidades principais
            Set<String> mainEntities = extractMainEntities(processId, loader);
            context.setMainEntities(mainEntities);

            // Extrair regras de negócio básicas
            List<BusinessRule> businessRules = extractBasicBusinessRules(processId);
            context.setBusinessRules(businessRules);

        } catch (Exception e) {
            System.err.println("Warning: Error extracting business context: " + e.getMessage());
            // Retornar contexto básico mesmo se houver erro
        }

        return context;
    }

    private String inferDomain(String processId) {
        String processIdLower = processId.toLowerCase();

        if (processIdLower.contains("recondicionamento") || processIdLower.contains("reconditioning")) {
            return "Vehicle Management";
        }
        if (processIdLower.contains("financial") || processIdLower.contains("finance")) {
            return "Financial Services";
        }
        if (processIdLower.contains("order") || processIdLower.contains("pedido")) {
            return "Order Management";
        }
        if (processIdLower.contains("customer") || processIdLower.contains("cliente")) {
            return "Customer Service";
        }

        return "General Business Process";
    }

    private Set<String> extractDomainTerms(String processId) {
        Set<String> terms = new HashSet<>();

        // Termos básicos sempre presentes
        terms.add("process");
        terms.add("workflow");
        terms.add("business");

        // Termos específicos baseados no processo
        String processIdLower = processId.toLowerCase();
        if (processIdLower.contains("recondicionamento")) {
            terms.add("reconditioning");
            terms.add("vehicle");
            terms.add("repair");
            terms.add("automotive");
        }

        return terms;
    }

    private Set<String> extractMainEntities(String processId, ProcessLoader loader) {
        Set<String> entities = new HashSet<>();

        // Entidades básicas inferidas do processo
        if (processId.toLowerCase().contains("recondicionamento")) {
            entities.add("recondicionamento");
            entities.add("viatura");
            entities.add("orcamento");
            entities.add("cliente");
        }

        // TODO: Futuramente, extrair de variáveis do processo via loader

        return entities;
    }

    private List<BusinessRule> extractBasicBusinessRules(String processId) {
        List<BusinessRule> rules = new ArrayList<>();

        // Regra básica inferida
        BusinessRule basicRule = new BusinessRule(
                "Process Execution Rule",
                "Process must follow defined workflow",
                processId
        );
        rules.add(basicRule);

        return rules;
    }
}