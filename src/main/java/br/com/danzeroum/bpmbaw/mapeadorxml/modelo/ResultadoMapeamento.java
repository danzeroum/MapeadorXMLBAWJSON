package br.com.danzeroum.bpmbaw.mapeadorxml.modelo;

public class ResultadoMapeamento {
    private final String origem;
    private final String id;
    private final String tipo;
    private final String nomeSugerido;
    private final String arquivoDestino;

    public ResultadoMapeamento(String origem, String id, String tipo, String nomeSugerido, String arquivoDestino) {
        this.origem = origem;
        this.id = id;
        this.tipo = tipo;
        this.nomeSugerido = nomeSugerido;
        this.arquivoDestino = arquivoDestino;
    }

    public String getOrigem() {
        return origem;
    }

    public String getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNomeSugerido() {
        return nomeSugerido;
    }

    public String getArquivoDestino() {
        return arquivoDestino;
    }
}
