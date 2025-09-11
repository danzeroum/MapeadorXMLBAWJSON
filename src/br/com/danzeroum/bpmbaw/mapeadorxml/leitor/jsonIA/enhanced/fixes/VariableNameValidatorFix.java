package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.fixes;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.VariableNameValidator;

/**
 * Correção para o VariableNameValidator
 */
public class VariableNameValidatorFix {

    /**
     * SUBSTITUIR O MÉTODO of() POR ESTE:
     */
    public static VariableNameValidator create(String... names) {
        VariableNameValidator validator = new VariableNameValidator();
        // Implementar lógica de validação
        return validator;
    }

    /**
     * OU ADICIONAR MÉTODO of() À CLASSE VariableNameValidator:
     */
    public static VariableNameValidator of(String name1, String name2, String name3,
                                           String name4, String name5, String name6) {
        VariableNameValidator validator = new VariableNameValidator();
        // Implementar lógica de validação com os nomes fornecidos
        return validator;
    }
}
