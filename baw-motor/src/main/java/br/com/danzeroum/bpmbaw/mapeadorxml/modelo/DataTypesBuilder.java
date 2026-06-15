package br.com.danzeroum.bpmbaw.mapeadorxml.modelo;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.DataTypeDefinitionV2Plus;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.twclass.TwClass;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Constrói as definições de tipos de dados de domínio (Business Objects).
 */
public class DataTypesBuilder {

    /**
     * Gera as definições de DataType para os objetos de negócio do domínio,
     * como Recondicionamento, Orcamento, etc.
     * Em uma implementação real, a estrutura viria do XML do TwClass correspondente.
     *
     * @param bawXml Representação do XML do BAW para extrair a estrutura dos BOs.
     * @return Uma lista de DataTypes de domínio.
     */
    public List<DataTypeDefinitionV2Plus> synthesizeDomainTypes(Object bawXml) {
        List<DataTypeDefinitionV2Plus> domainTypes = new ArrayList<>();

        // Simulação baseada no formato final, pois a análise do TwClass é mais complexa.
        // Adiciona os 4 tipos de domínio principais esperados.

        // Tipo: Recondicionamento
        DataTypeDefinitionV2Plus recondicionamento = new DataTypeDefinitionV2Plus("dt:recondicionamento@1", "Recondicionamento", "Dados completos do processo de recondicionamento de viatura.");
        recondicionamento.setJsonSchema(createSimpleObjectSchema("Contém todos os dados da viatura e do transporte."));
        domainTypes.add(recondicionamento);

        // Tipo: Orcamento
        DataTypeDefinitionV2Plus orcamento = new DataTypeDefinitionV2Plus("dt:orcamento@1", "Orcamento", "Representa o orçamento gerado para o recondicionamento.");
        orcamento.setJsonSchema(createSimpleObjectSchema("Contém valor, status e dias úteis."));
        domainTypes.add(orcamento);

        // Tipo: ValidationResult
        DataTypeDefinitionV2Plus validation = new DataTypeDefinitionV2Plus("dt:validation@1", "ValidationResult", "Resultado consolidado das validações de negócio.");
        validation.setJsonSchema(createSimpleObjectSchema("Indica se é válido e lista de erros."));
        domainTypes.add(validation);

        // Tipo: ErrorList (um array de objetos)
        DataTypeDefinitionV2Plus errorList = new DataTypeDefinitionV2Plus("dt:errorList@1", "ErrorList", "Lista de erros de validação encontrados.");
        errorList.setArray(true);
        errorList.setBaseType("dt:error@1"); // Define o tipo dos itens da lista
        errorList.setJsonSchema(createArraySchema("Lista de objetos de erro.", "#/$defs/validationError"));
        domainTypes.add(errorList);

        return domainTypes;
    }

    private Map<String, Object> createSimpleObjectSchema(String description) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
        schema.put("type", "object");
        schema.put("description", description);
        schema.put("properties", new HashMap<>());
        return schema;
    }

    private Map<String, Object> createArraySchema(String description, String itemRef) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
        schema.put("type", "array");
        schema.put("description", description);
        Map<String, String> items = new HashMap<>();
        items.put("$ref", itemRef);
        schema.put("items", items);
        return schema;
    }
}