package br.com.danzeroum.bpmbaw.mapeadorxml.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Classe utilitária para extrair o conteúdo XML BPMN 2.0 de dentro da tag <bpmn2Data>
 * de um arquivo de BPD exportado do IBM BAW.
 */
public class Bpmn2DataExtractor {

    /**
     * Lê um arquivo XML de BPD, encontra a tag <bpmn2Data> e salva seu conteúdo
     * no caminho de arquivo de saída especificado. Cria o diretório de saída se não existir.
     *
     * @param inputFilePath O caminho completo para o arquivo XML do BPD.
     * @param outputFilePath O caminho completo onde o novo arquivo .bpmn será salvo.
     * @throws Exception Se ocorrer um erro de leitura, parsing ou gravação.
     */
    public static void extractAndSave(String inputFilePath, String outputFilePath) throws Exception {
        File inputXmlFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);

        if (!inputXmlFile.exists()) {
            throw new Exception("Arquivo de entrada não encontrado: " + inputXmlFile.getAbsolutePath());
        }

        // --- LÓGICA ATUALIZADA PARA CRIAR O DIRETÓRIO ---
        File outputDirectory = outputFile.getParentFile();
        if (outputDirectory != null && !outputDirectory.exists()) {
            System.out.println("Criando diretório de saída: " + outputDirectory.getAbsolutePath());
            if (!outputDirectory.mkdirs()) {
                throw new Exception("Não foi possível criar o diretório de saída.");
            }
        }
        // --- FIM DA ATUALIZAÇÃO ---

        System.out.println("Lendo o arquivo: " + inputXmlFile.getName());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputXmlFile);
        doc.getDocumentElement().normalize();

        NodeList bpdNodes = doc.getElementsByTagName("bpd");
        if (bpdNodes.getLength() == 0) {
            throw new Exception("O arquivo não parece ser uma definição de BPD (tag <bpd> não encontrada).");
        }

        Node bpdNode = bpdNodes.item(0);
        Node bpmn2DataNode = findBpmn2DataNode(bpdNode);

        if (bpmn2DataNode == null) {
            System.out.println("[AVISO] Tag <bpmn2Data> não encontrada no arquivo " + inputXmlFile.getName());
            return;
        }

        String bpmn2Content = bpmn2DataNode.getTextContent().trim();

        if (bpmn2Content.isEmpty()) {
            System.out.println("[AVISO] A tag <bpmn2Data> foi encontrada, mas está vazia.");
            return;
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(outputFile))) {
            out.write(bpmn2Content);
        }

        System.out.println("Sucesso! Conteúdo BPMN 2.0 salvo em: " + outputFile.getAbsolutePath());
    }

    private static Node findBpmn2DataNode(Node parentNode) {
        NodeList childNodes = parentNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node currentNode = childNodes.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE && "bpmn2Data".equals(currentNode.getNodeName())) {
                return currentNode;
            }
        }
        return null;
    }



}