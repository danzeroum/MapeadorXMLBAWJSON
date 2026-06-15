
package br.com.danzeroum.bpmbaw.mapeadorxml.util;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// A sua classe original.
public class ExtratorIdsXml {

    // BOA PRÁTICA (Compatível com Java 8): Inicializando um Map estático e imutável.
    private static final Map<String, String> TOOLKIT_ID_TO_PATH_MAP;

    // Bloco de inicialização estático. Este código é executado uma vez quando a classe é carregada.
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("05f76265-81ab-4a70-9e88-502127f50413", "toolkits/2064.739ededc-006c-41ef-ac0d-fba6cdecf8f6/objects");
        aMap.put("57315155-b91c-4199-8309-59e5739fccbc", "toolkits/2064.d6828c8b-cd06-4346-b2ee-f4d272ae36a5/objects");
        aMap.put("6574b63a-3998-4afa-92fb-5e7479e3e623", "toolkits/2064.830ab528-b9f5-46b6-9ee9-1bbab8af9ca9/objects");
        aMap.put("7a7b502a-ca43-4ff3-b0f3-7c2be4f1d321", "toolkits/2064.840262f5-75e7-462b-95b4-e050c77df861/objects");
        aMap.put("936744be-1bed-45de-a079-c5646bd6a6c5", "toolkits/2064.830ab528-b9f5-46b6-9ee9-1bbab8af9ca9/objects");
        aMap.put("bc6e96d4-6e3e-416f-bbfe-5dfa609691c0", "toolkits/2064.32c59703-9813-4084-bb1c-b5ba7032a053/objects");
        aMap.put("d2e6875a-a556-4e1d-ad00-52360a05fa7d", "toolkits/2064.dc4f1f18-ba92-449e-901a-64b7294ef93f/objects");
        aMap.put("ee354ebc-cc55-4b89-83a1-ecf604b34257", "toolkits/2064.8f16ba7e-825c-4e79-a3b6-d3fb7890b659/objects");
        aMap.put("fcada42d-a200-45ef-8ef7-69ffebbae213", "toolkits/2064.4593e8ef-13be-4f1d-ba87-cc2f5ec295f0/objects");
        aMap.put("ffe0487a-25fe-40c7-b4d0-ea3561bdade3", "toolkits/2064.5d120392-1f92-441f-89d6-1f6da7a6e7e3/objects");

        // Torna o Map imutável após a inicialização para garantir a segurança.
        TOOLKIT_ID_TO_PATH_MAP = Collections.unmodifiableMap(aMap);
    }


    // O seu método original, com a estrutura e assinatura idênticas.
    // Nenhuma alteração é necessária aqui, pois ele já é compatível.
    public static Set<String> extrairIds(Path xmlPath) {
        Set<String> ids = new HashSet<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlPath.toFile());
            doc.getDocumentElement().normalize();

            NodeList attachedProcessRef = doc.getElementsByTagName("attachedProcessRef");
            NodeList attached = doc.getElementsByTagName("attachedActivityId");
            NodeList embedded = doc.getElementsByTagName("embeddedProcessId");
/*
            for (int i = 0; i < attached.getLength(); i++) {
                ids.add(attached.item(i).getTextContent().trim());
            }

            for (int i = 0; i < embedded.getLength(); i++) {
                ids.add(embedded.item(i).getTextContent().trim());
            }
*/
            for (int i = 0; i < attachedProcessRef.getLength(); i++) {
                String rawContent = attachedProcessRef.item(i).getTextContent();
                if (rawContent == null || rawContent.trim().isEmpty()) {
                    continue;
                }

                String[] contentParts = rawContent.trim().split("/");
                if (contentParts.length == 0 || contentParts[0].isEmpty()) {
                    continue;
                }

                String toolkitId = contentParts[0];
                String toolkitBasePath = TOOLKIT_ID_TO_PATH_MAP.getOrDefault(toolkitId, "");

                if (toolkitBasePath.isEmpty()) {
                    ids.add(toolkitId);
                    System.err.println("AVISO: ID de toolkit não mapeado encontrado: " + toolkitId + " no arquivo " + xmlPath.getFileName());
                } else {
                    String finalPath = Paths.get(toolkitBasePath, contentParts[1]).toString();
                    ids.add(finalPath);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erro lendo XML: " + xmlPath.getFileName() + " → " + e.getMessage());
        }

        return ids;
    }
}