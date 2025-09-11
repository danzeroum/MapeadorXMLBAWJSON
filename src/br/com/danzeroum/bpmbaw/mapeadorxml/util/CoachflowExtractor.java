package br.com.danzeroum.bpmbaw.mapeadorxml.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Classe utilitária para extrair o conteúdo da tag <coachflow>
 * de um arquivo de serviço legado do IBM BAW e salvá-lo como um novo XML.
 */
public class CoachflowExtractor {

    /**
     * Lê um arquivo XML de serviço, encontra a tag <coachflow> e salva seu conteúdo em um novo arquivo XML.
     *
     * @param arquivoServico O arquivo XML do serviço legado de entrada.
     * @param arquivoSaida   O arquivo XML de saída onde o coachflow será salvo.
     * @throws Exception se ocorrer um erro durante a leitura, extração ou escrita do arquivo.
     */
    public void extrairCoachflow(File arquivoServico, File arquivoSaida) throws Exception {
        System.out.println("[LOG] Iniciando extração do coachflow do arquivo: " + arquivoServico.getAbsolutePath());

        // 1. Configurar o parser de XML (DOM)
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // É importante desativar a validação e a consciência de namespace para ler os arquivos do BAW de forma mais flexível
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();

        // 2. Ler e parsear o arquivo de serviço
        Document doc = builder.parse(arquivoServico);
        doc.getDocumentElement().normalize();

        // 3. Encontrar a tag <coachflow>
        NodeList coachflowNodes = doc.getElementsByTagName("coachflow");

        if (coachflowNodes.getLength() == 0) {
            System.out.println("[AVISO] Nenhuma tag <coachflow> foi encontrada no arquivo " + arquivoServico.getName());
            return;
        }

        // Pega o primeiro nó <coachflow> encontrado
        Node coachflowNode = coachflowNodes.item(0);
        System.out.println("[LOG] Tag <coachflow> encontrada.");

        // 4. Configurar o 'Transformer' para escrever o nó em um novo arquivo XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Configurações para uma saída formatada (pretty print)
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); // 4 espaços de indentação

        // 5. Escrever o nó <coachflow> no arquivo de saída
        DOMSource source = new DOMSource(coachflowNode);

        // Garante que o diretório de saída exista
        arquivoSaida.getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(arquivoSaida);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

            StreamResult result = new StreamResult(osw);
            transformer.transform(source, result);
        }

        System.out.println("[SUCESSO] O conteúdo do coachflow foi extraído e salvo em: " + arquivoSaida.getAbsolutePath());
    }

    /**
     * Exemplo de como usar a classe.
     */

}