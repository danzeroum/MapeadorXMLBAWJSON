
package br.com.danzeroum.bpmbaw.mapeadorxml.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CopiadorRecursivoXml {

    public static void copiarTodosXmlsConectados(String origem, String destino, String arquivoRaiz) {
        Set<String> visitados = new HashSet<>();
        Queue<String> fila = new LinkedList<>();
        fila.add(arquivoRaiz);

        new File(destino).mkdirs(); // cria destino se não existir

        while (!fila.isEmpty()) {
            String atual = fila.poll();
            if (visitados.contains(atual)) continue;
            visitados.add(atual);

            String nomeArquivoOrigem = atual.endsWith(".xml") ? atual : atual + ".xml";
            Path caminhoOrigem       = Paths.get(origem, nomeArquivoOrigem);
            String nomeArquivoDestino = nomeArquivoOrigem;
            if (nomeArquivoOrigem.contains("toolkit")) {
                Path caminho = Paths.get(nomeArquivoOrigem);
                nomeArquivoDestino = caminho.getFileName().toString();
            }
            Path caminhoDestino      = Paths.get(destino, nomeArquivoDestino);

            if (!Files.exists(caminhoDestino)) {
                try {
                    Files.copy(caminhoOrigem, caminhoDestino);
                    System.out.println("📄 Copiado: " + nomeArquivoOrigem);
                } catch (IOException e) {
                    System.err.println("⚠️ Erro ao copiar: " + nomeArquivoDestino + " → " + e.getMessage());
                    continue;
                }
            } else {
                System.out.println("🔁 Já existente, pulando: " + nomeArquivoDestino);
            }

            Set<String> novosIds = ExtratorIdsXml.extrairIds(caminhoDestino);
            for (String id : novosIds) {
                String novoNome = id.replace("/", "") + ".xml";
                if (!visitados.contains(novoNome)) {
                    fila.add(novoNome);
                }
            }
        }
    }
}
