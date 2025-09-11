package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representa a posição de um componente visual no diagrama do processo.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "position")
public class Position {

    @XmlElement
    private Location location;

    // --- Getters e Setters ---

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    // --- Classe Interna para a tag <location> ---

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Location {

        @XmlAttribute
        private int x;

        @XmlAttribute
        private int y;

        // --- Getters e Setters ---

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}