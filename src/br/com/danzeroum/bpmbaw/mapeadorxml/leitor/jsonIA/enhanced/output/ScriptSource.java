package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output;

/**
 * Script source
 */
class ScriptSource {
    private String repository;
    private String file;
    private String commitHash;
    private String inlineCode;

    // Getters and setters...
    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public String getInlineCode() {
        return inlineCode;
    }

    public void setInlineCode(String inlineCode) {
        this.inlineCode = inlineCode;
    }
}
