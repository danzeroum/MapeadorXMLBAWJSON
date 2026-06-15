package br.com.danzeroum.bpmbaw.mapeadorxml.util;

/**
 * Esta classe centraliza todas as opções de parametrização para o mapeamento do fluxo.
 * A partir daqui, é possível controlar o nível de detalhe do relatório gerado.
 */
public class OpcoesMapeamento {

    // --- Opções de Seção ---
    private boolean imprimirBlocoParametros = true;
    private boolean imprimirBlocoVariaveis = true;
    private boolean imprimirBlocoFluxoDeExecucao = true;

    // --- Opções de Detalhe do Fluxo ---
    private boolean mapearSubprocessos = true;
    private boolean imprimirScripts = true;
    private boolean imprimirCondicoesDeFluxo = true;
    private boolean imprimirDetalhesDoCoach = true;
    private boolean imprimirFluxosSecundarios = true; // Controla a análise de UCAs
    private boolean imprimirFluxosDeEventosAnexados = true; // Controla a análise de eventos de borda
    private boolean mapearCoachViewsSeparadamente = false;
    // --- Getters e Setters ---

    public boolean isImprimirBlocoParametros() {
        return imprimirBlocoParametros;
    }

    public void setImprimirBlocoParametros(boolean imprimirBlocoParametros) {
        this.imprimirBlocoParametros = imprimirBlocoParametros;
    }

    public boolean isImprimirBlocoVariaveis() {
        return imprimirBlocoVariaveis;
    }

    public void setImprimirBlocoVariaveis(boolean imprimirBlocoVariaveis) {
        this.imprimirBlocoVariaveis = imprimirBlocoVariaveis;
    }

    public boolean isMapearSubprocessos() {
        return mapearSubprocessos;
    }

    public void setMapearSubprocessos(boolean mapearSubprocessos) {
        this.mapearSubprocessos = mapearSubprocessos;
    }

    public boolean isImprimirBlocoFluxoDeExecucao() {
        return imprimirBlocoFluxoDeExecucao;
    }

    public void setImprimirBlocoFluxoDeExecucao(boolean imprimirBlocoFluxoDeExecucao) {
        this.imprimirBlocoFluxoDeExecucao = imprimirBlocoFluxoDeExecucao;
    }

    public boolean isImprimirScripts() {
        return imprimirScripts;
    }

    public void setImprimirScripts(boolean imprimirScripts) {
        this.imprimirScripts = imprimirScripts;
    }

    public boolean isImprimirCondicoesDeFluxo() {
        return imprimirCondicoesDeFluxo;
    }

    public void setImprimirCondicoesDeFluxo(boolean imprimirCondicoesDeFluxo) {
        this.imprimirCondicoesDeFluxo = imprimirCondicoesDeFluxo;
    }

    public boolean isImprimirDetalhesDoCoach() {
        return imprimirDetalhesDoCoach;
    }

    public void setImprimirDetalhesDoCoach(boolean imprimirDetalhesDoCoach) {
        this.imprimirDetalhesDoCoach = imprimirDetalhesDoCoach;
    }

    public boolean isImprimirFluxosSecundarios() {
        return imprimirFluxosSecundarios;
    }

    public void setImprimirFluxosSecundarios(boolean imprimirFluxosSecundarios) {
        this.imprimirFluxosSecundarios = imprimirFluxosSecundarios;
    }

    public boolean isImprimirFluxosDeEventosAnexados() {
        return imprimirFluxosDeEventosAnexados;
    }

    public void setImprimirFluxosDeEventosAnexados(boolean imprimirFluxosDeEventosAnexados) {
        this.imprimirFluxosDeEventosAnexados = imprimirFluxosDeEventosAnexados;
    }

    public boolean isMapearCoachViewsSeparadamente() {
        return mapearCoachViewsSeparadamente;
    }

    public void setMapearCoachViewsSeparadamente(boolean mapearCoachViewsSeparadamente) {
        this.mapearCoachViewsSeparadamente = mapearCoachViewsSeparadamente;
    }
}