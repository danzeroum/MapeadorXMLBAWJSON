package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representa a dimensão (tamanho) de um componente visual, como um pool,
 * no diagrama do processo.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dimension")
public class Dimension {

    @XmlElement
    private Size size;


    // --- Getters e Setters ---

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }


    // --- Classe Interna para a tag <size> ---

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Size {

        @XmlAttribute(name = "w")
        private int width;

        @XmlAttribute(name = "h")
        private int height;


        // --- Getters e Setters ---

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}