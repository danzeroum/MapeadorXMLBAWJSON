package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

/**
 * Index Type Enum
 */
public enum IndexType {
    HASHMAP("HashMap - O(1) lookup"),
    GROUPING("Grouping - Group by key"),
    MULTIMAP("Multimap - Multiple values per key"),
    BTREE("B-Tree - Sorted access"),
    FULLTEXT("Full-text search");

    private final String description;

    IndexType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name() + " (" + description + ")";
    }
}
