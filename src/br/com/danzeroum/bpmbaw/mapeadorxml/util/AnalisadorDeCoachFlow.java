package br.com.danzeroum.bpmbaw.mapeadorxml.util;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.Definitions;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.FormTask;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.GlobalUserTask;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.UserTaskImplementation;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.coachng.Layout;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.CoachFlow;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process;

public class AnalisadorDeCoachFlow {

    public void analisarConteudoCoachFlow(Process process) {
        if (process == null || process.getCoachflow() == null) {
            System.out.println("O processo não contém um CoachFlow.");
            return;
        }

        System.out.println("Analisando Serviço: " + process.getName());

        CoachFlow coachFlow = process.getCoachflow();
        Definitions definitions = coachFlow.getDefinitions();
        if (definitions == null) {
            System.out.println("  -> CoachFlow não possui definições BPMN.");
            return;
        }

        GlobalUserTask userTask = definitions.getGlobalUserTask();
        if (userTask == null) {
            System.out.println("  -> Definições não contêm uma GlobalUserTask.");
            return;
        }

        System.out.println("  -> Tarefa de Usuário: " + userTask.getName());

        UserTaskImplementation implementation = userTask.getImplementation();
        if (implementation == null || implementation.getFlowElements() == null) {
            System.out.println("    -> Tarefa não possui implementação ou elementos de fluxo.");
            return;
        }

        // Itera pelos elementos do fluxo para encontrar a(s) tela(s)
        for (Object flowElement : implementation.getFlowElements()) {
            if (flowElement instanceof FormTask) {
                FormTask formTask = (FormTask) flowElement;
                System.out.println("    -> Encontrado Coach (FormTask): " + formTask.getName());

                if (formTask.getFormDefinition() != null &&
                        formTask.getFormDefinition().getCoachDefinition() != null &&
                        formTask.getFormDefinition().getCoachDefinition().getLayout() != null) {

                    Layout layout = formTask.getFormDefinition().getCoachDefinition().getLayout();
                    System.out.println("      --> Layout do Coach encontrado com " + layout.getLayoutItems().size() + " item(ns) raiz.");
                    // Aqui você pode adicionar a lógica da classe CoachReportPrinter
                    // para imprimir os detalhes dos layoutItems.
                } else {
                    System.out.println("      --> Layout do Coach não definido ou vazio.");
                }
            }
        }
    }
}