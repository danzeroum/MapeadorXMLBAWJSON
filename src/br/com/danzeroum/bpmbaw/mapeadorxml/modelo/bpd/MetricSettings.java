package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representa as configurações de métricas para um item de processo.
 * Inclui as configurações de atribuição e os limiares (thresholds).
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "metricSettings")
public class MetricSettings {

    @XmlAttribute
    private int itemType;

    @XmlElement
    private Settings settings;

    // --- Getters e Setters ---

    public int getItemType() { return itemType; }
    public void setItemType(int itemType) { this.itemType = itemType; }

    public Settings getSettings() { return settings; }
    public void setSettings(Settings settings) { this.settings = settings; }


    // --- Classe Interna para a tag <settings> ---
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Settings {

        @XmlAttribute
        private String metricId;
        @XmlAttribute
        private int assignmentType;
        @XmlAttribute
        private boolean useDefaultAssignments;
        @XmlAttribute
        private boolean useDefaultThresholds;

        @XmlElement
        private Threshold threshold;

        // Getters e Setters
        public String getMetricId() { return metricId; }
        public void setMetricId(String metricId) { this.metricId = metricId; }

        public int getAssignmentType() { return assignmentType; }
        public void setAssignmentType(int assignmentType) { this.assignmentType = assignmentType; }

        public boolean isUseDefaultAssignments() { return useDefaultAssignments; }
        public void setUseDefaultAssignments(boolean useDefaultAssignments) { this.useDefaultAssignments = useDefaultAssignments; }

        public boolean isUseDefaultThresholds() { return useDefaultThresholds; }
        public void setUseDefaultThresholds(boolean useDefaultThresholds) { this.useDefaultThresholds = useDefaultThresholds; }

        public Threshold getThreshold() { return threshold; }
        public void setThreshold(Threshold threshold) { this.threshold = threshold; }
    }


    // --- Classe Interna para a tag <threshold> ---
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Threshold {

        @XmlAttribute
        private String type;
        @XmlAttribute
        private int min;
        @XmlAttribute
        private int expected;
        @XmlAttribute
        private int max;

        // Getters e Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public int getMin() { return min; }
        public void setMin(int min) { this.min = min; }

        public int getExpected() { return expected; }
        public void setExpected(int expected) { this.expected = expected; }

        public int getMax() { return max; }
        public void setMax(int max) { this.max = max; }
    }
}