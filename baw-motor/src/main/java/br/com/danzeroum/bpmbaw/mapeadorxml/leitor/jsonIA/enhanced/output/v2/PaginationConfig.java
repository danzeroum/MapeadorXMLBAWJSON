package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

/**
 * Pagination Configuration for large collections
 */
public class PaginationConfig {
    private int defaultPageSize;
    private int maxPageSize;
    private boolean enableCursor;
    private String sortField;
    private SortOrder sortOrder;

    public PaginationConfig() {
        this.defaultPageSize = 50;
        this.maxPageSize = 1000;
        this.enableCursor = true;
        this.sortOrder = SortOrder.ASC;
    }

    // Factory method for default config
    public static PaginationConfig createDefault() {
        return new PaginationConfig();
    }

    // Getters and setters
    public int getDefaultPageSize() { return defaultPageSize; }
    public void setDefaultPageSize(int defaultPageSize) {
        if (defaultPageSize <= 0) {
            throw new IllegalArgumentException("Page size must be positive");
        }
        this.defaultPageSize = defaultPageSize;
    }

    public int getMaxPageSize() { return maxPageSize; }
    public void setMaxPageSize(int maxPageSize) {
        if (maxPageSize <= 0) {
            throw new IllegalArgumentException("Max page size must be positive");
        }
        this.maxPageSize = maxPageSize;
    }

    public boolean isEnableCursor() { return enableCursor; }
    public void setEnableCursor(boolean enableCursor) { this.enableCursor = enableCursor; }

    public String getSortField() { return sortField; }
    public void setSortField(String sortField) { this.sortField = sortField; }

    public SortOrder getSortOrder() { return sortOrder; }
    public void setSortOrder(SortOrder sortOrder) { this.sortOrder = sortOrder; }

    public enum SortOrder {
        ASC, DESC
    }
}