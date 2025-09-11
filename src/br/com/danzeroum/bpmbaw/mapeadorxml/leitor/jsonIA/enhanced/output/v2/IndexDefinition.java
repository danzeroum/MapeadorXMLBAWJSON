package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

/**
 * Index Definition for performance optimization
 */
public class IndexDefinition {
    private String name;
    private IndexType type;
    private String path;
    private String description;
    private boolean materialized;
    private IndexStatistics statistics;

    public IndexDefinition() {}

    public IndexDefinition(String name, IndexType type, String path, String description) {
        this.name = name;
        this.type = type;
        this.path = path;
        this.description = description;
        this.materialized = false;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public IndexType getType() { return type; }
    public void setType(IndexType type) { this.type = type; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isMaterialized() { return materialized; }
    public void setMaterialized(boolean materialized) { this.materialized = materialized; }

    public IndexStatistics getStatistics() { return statistics; }
    public void setStatistics(IndexStatistics statistics) { this.statistics = statistics; }

    public static class IndexStatistics {
        private int keyCount;
        private long memoryUsageBytes;
        private double averageLookupTimeMs;

        // Getters and setters
        public int getKeyCount() { return keyCount; }
        public void setKeyCount(int keyCount) { this.keyCount = keyCount; }

        public long getMemoryUsageBytes() { return memoryUsageBytes; }
        public void setMemoryUsageBytes(long memoryUsageBytes) { this.memoryUsageBytes = memoryUsageBytes; }

        public double getAverageLookupTimeMs() { return averageLookupTimeMs; }
        public void setAverageLookupTimeMs(double averageLookupTimeMs) { this.averageLookupTimeMs = averageLookupTimeMs; }
    }
}
