package br.com.danzeroum.bpmbaw.mapeadorxml.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipAndCleanDirectory {


    public void unzipAndClean(String directoryPath) {
        if (directoryPath == null || directoryPath.length() == 0) {
            System.out.println("Erro: Por favor, forneça o caminho do diretório como argumento.");
            System.out.println("Uso: java UnzipAndCleanDirectory <caminho_da_pasta>");
            return;
        }

        File directory = new File(directoryPath);

        if (!directory.isDirectory()) {
            System.out.println("Erro: O caminho fornecido não é um diretório válido.");
            return;
        }

        System.out.println("Iniciando processo no diretório: " + directory.getAbsolutePath());
        new UnzipAndCleanDirectory().processDirectory(directory);
        System.out.println("Processo finalizado com sucesso.");
    }

    /**
     * Processa o diretório, encontrando, extraindo e removendo arquivos .zip.
     *
     * @param directory O diretório a ser processado.
     */
    public void processDirectory(File directory) {
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));

        if (files == null || files.length == 0) {
            System.out.println("Nenhum arquivo .zip encontrado no diretório.");
            return;
        }

        for (File zipFile : files) {
            System.out.println("Processando arquivo: " + zipFile.getName());
            try {
                // Determina o nome do diretório de destino a partir do nome do arquivo zip
                String fileName = zipFile.getName();
                String destDirName = fileName.substring(0, fileName.lastIndexOf('.'));
                Path destDirPath = Paths.get(directory.getAbsolutePath(), destDirName);

                // Cria o diretório de destino
                Files.createDirectories(destDirPath);

                unzip(zipFile, directory.toPath());
                System.out.println("Arquivo " + zipFile.getName() + " extraído com sucesso.");

                // Apaga o arquivo .zip após a extração bem-sucedida
              /*  if (zipFile.delete()) {
                    System.out.println("Arquivo " + zipFile.getName() + " apagado.");
                } else {
                    System.err.println("Falha ao apagar o arquivo: " + zipFile.getName());
                }
*/
            } catch (IOException e) {
                System.err.println("Erro ao processar o arquivo " + zipFile.getName() + ": " + e.getMessage());
                // Em caso de erro na extração, o arquivo zip não é apagado.
            }
        }
    }

    /**
     * Extrai o conteúdo de um arquivo zip para um diretório de destino.
     *
     * @param file      O arquivo .zip a ser extraído.
     * @param outputDir O Path do diretório onde o conteúdo será extraído.
     * @throws IOException Se ocorrer um erro de I/O durante a extração.
     */
    private void unzip(File file, Path outputDir) throws IOException {
        // Usando try-with-resources para garantir que o ZipFile seja fechado automaticamente
        try (ZipFile zipFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path entryPath = outputDir.resolve(entry.getName());

                // Previne a vulnerabilidade "Zip Slip"
                if (!entryPath.normalize().startsWith(outputDir.normalize())) {
                    throw new IOException("Entrada de Zip maliciosa: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    // Cria o diretório se não existir
                    Files.createDirectories(entryPath);
                } else {
                    // Garante que o diretório pai exista
                    Files.createDirectories(entryPath.getParent());
                    // Extrai o arquivo usando um buffer
                    try (InputStream in = zipFile.getInputStream(entry);
                         FileOutputStream out = new FileOutputStream(entryPath.toFile())) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }
}