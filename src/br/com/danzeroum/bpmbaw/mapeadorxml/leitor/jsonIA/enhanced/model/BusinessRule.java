package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.model;
public class BusinessRule {
    private String id;
    private String name;
    private String description;
    private String condition;
    private String source;
    private int complexity;

    public BusinessRule() {}

    public BusinessRule(String name, String condition, String source) {
        this.name = name;
        this.condition = condition;
        this.source = source;
        this.complexity = calculateComplexity(condition);
    }

    private int calculateComplexity(String condition) {
        if (condition == null) return 0;
        return condition.split("&&|\\|\\||if|else").length;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public int getComplexity() { return complexity; }
    public void setComplexity(int complexity) { this.complexity = complexity; }
}