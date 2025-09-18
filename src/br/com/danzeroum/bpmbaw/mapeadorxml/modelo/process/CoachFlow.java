package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "coachflow")
public class CoachFlow {

    @XmlElement(name = "definitions", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private Definitions definitions;

    /**
     * CORREÇÃO: Usa @XmlMixed para capturar legalmente tanto o elemento 'definitions'
     * quanto o conteúdo de texto (o XML do layout legado).
     */
    @XmlMixed
    private List<String> content;

    public Definitions getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Definitions definitions) {
        this.definitions = definitions;
    }

    /**
     * NOVO MÉTODO: Extrai o layout XML legado da lista de conteúdo misto.
     * Este método substitui a necessidade do campo 'value' com @XmlValue.
     * @return O layout XML como uma String, ou null se não houver.
     */
    @XmlTransient // Garante que o JAXB ignore este método
    public String getValue() {
        if (content == null || content.isEmpty()) {
            return null;
        }
        // Concatena todas as partes de texto, que juntas formam o XML do layout.
        return content.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining());
    }
}