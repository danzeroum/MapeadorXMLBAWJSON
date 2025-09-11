package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.fixes;

/**
 * Classes que podem estar faltando no modelo
 */
public class MissingClassesFix {

    /**
     * Se ItemType não existir, adicionar esta enum:
     */
    public enum ItemType {
        SCRIPT, VALIDATION, TRANSFORMATION, MAPPING
    }

    /**
     * Se ExecutionPolicy não existir, adicionar esta classe:
     */
    public static class ExecutionPolicy {
        private String mode = "SEQUENTIAL";
        private int timeout = 30000;
        private boolean async = false;

        // Getters and setters
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }

        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }

        public boolean isAsync() { return async; }
        public void setAsync(boolean async) { this.async = async; }
    }

    /**
     * Se LogicItemMetadata não existir, adicionar esta classe:
     */
    public static class LogicItemMetadata {
        private String author;
        private String version = "1.0";
        private long created = System.currentTimeMillis();

        // Getters and setters
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public long getCreated() { return created; }
        public void setCreated(long created) { this.created = created; }
    }
}
