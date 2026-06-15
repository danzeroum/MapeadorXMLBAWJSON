package br.com.danzeroum.bpmbaw.mapeadorxml.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe utilitária para formatação de datas.
 */
public class FormatadorDeDataUtil {

    /**
     * Retorna a data e hora atuais em uma String formatada.
     * @return A data atual no formato "yyyyMMdd_HHmmss".
     */
    public static String getTimestampAtualFormatado() {
        // 1. Obtém a data e hora atuais.
        LocalDateTime agora = LocalDateTime.now();

        // 2. Define o padrão de formatação desejado.
        //    yyyy = ano com 4 dígitos
        //    MM   = mês com 2 dígitos
        //    dd   = dia do mês com 2 dígitos
        //    HH   = hora no formato 24h (00-23)
        //    mm   = minuto com 2 dígitos
        //    ss   = segundo com 2 dígitos
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

        // 3. Formata a data atual usando o padrão e retorna a String.
        return agora.format(formatador);
    }


}