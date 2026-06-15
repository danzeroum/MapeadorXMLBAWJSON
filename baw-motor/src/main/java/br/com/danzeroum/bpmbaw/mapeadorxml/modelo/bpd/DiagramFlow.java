package br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class DiagramFlow {

    @XmlAttribute
    private String id;

    // NOVO: Mapeia o nome visível do fluxo (ex: "Entidade Externa")
    @XmlElement
    private String name;

    @XmlElement
    private Connection connection;

    public String getId() { return id; }
    public String getName() { return name; }
    public Connection getConnection() { return connection; } // Getter para a conexão inteira

    // Método de conveniência para obter o alvo
    public String getTarget() {
        return (connection != null) ? connection.getTargetId() : null;
    }

    // Método de conveniência para obter a expressão
    public String getExpression() {
        if (connection != null && connection.getCondition() != null) {
            return connection.getCondition().getExpression();
        }
        return null;
    }

    // Classe interna para mapear a tag <connection>
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "BpdDiagramFlowConnection")
    public static class Connection {
        @XmlAttribute
        private String target;

        // NOVO: Mapeia a tag <condition>
        @XmlElement
        private Condition condition;

        public String getTargetId() { return target; }
        public Condition getCondition() { return condition; }
    }
}