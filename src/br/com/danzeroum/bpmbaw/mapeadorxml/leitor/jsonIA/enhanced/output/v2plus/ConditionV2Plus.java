package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Condition V2+ - CORRIGIDO para conformidade com modelo
 *
 * CORREÇÕES:
 * ✅ expr vs expression
 * ✅ exprLang vs language
 */
@JsonPropertyOrder({"id", "expr", "exprLang", "description"})
public class ConditionV2Plus {

    @JsonProperty("id")
    private String id;

    @JsonProperty("expr") // ✅ Corrigido: era "expression"
    private String expr;

    @JsonProperty("exprLang") // ✅ Corrigido: era "language"
    private String exprLang;

    @JsonProperty("description")
    private String description;

    // Construtores
    public ConditionV2Plus() {}

    public ConditionV2Plus(String id, String expr, String exprLang, String description) {
        this.id = id;
        this.expr = expr;
        this.exprLang = exprLang;
        this.description = description;
    }

    // Getters/Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getExpr() { return expr; }
    public void setExpr(String expr) { this.expr = expr; }

    public String getExprLang() { return exprLang; }
    public void setExprLang(String exprLang) { this.exprLang = exprLang; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}