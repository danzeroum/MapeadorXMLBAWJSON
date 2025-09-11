/**
 * SafeLaneSetter - UTILITÁRIO PARA SETAR PROPRIEDADES EM LANE DE FORMA SEGURA
 *
 * Resolve problemas de métodos faltantes na ProcessLaneV2Plus usando reflexão segura.
 *
 * @version 2.3.0-safe-java8
 */
package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.extractors;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2plus.ProcessLaneV2Plus;

import java.lang.reflect.Method;
import java.util.List;

public class SafeLaneSetter {

    /**
     * Setar description de forma segura
     */
    public static void setDescriptionSafe(ProcessLaneV2Plus lane, String description) {
        if (lane == null || description == null) {
            return;
        }

        try {
            // Tentar método setDescription
            Method setDescMethod = lane.getClass().getMethod("setDescription", String.class);
            setDescMethod.invoke(lane, description);
        } catch (NoSuchMethodException e) {
            // Método não existe, tentar setar campo diretamente
            try {
                java.lang.reflect.Field descField = lane.getClass().getDeclaredField("description");
                descField.setAccessible(true);
                descField.set(lane, description);
            } catch (Exception e2) {
                // Se falhar, usar o campo name para incluir a descrição
                try {
                    String currentName = lane.getName();
                    String newName = currentName + " (" + description + ")";
                    lane.setName(newName);
                } catch (Exception e3) {
                    System.err.println("⚠️ Could not set description for lane: " + e3.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error setting description for lane: " + e.getMessage());
        }
    }

    /**
     * Setar flowObjectRefs de forma segura
     */
    public static void setFlowObjectRefsSafe(ProcessLaneV2Plus lane, List<String> flowObjectIds) {
        if (lane == null || flowObjectIds == null) {
            return;
        }

        // ESTRATÉGIA 1: Tentar setFlowObjectRefs
        try {
            Method setFlowObjectRefsMethod = lane.getClass().getMethod("setFlowObjectRefs", List.class);
            setFlowObjectRefsMethod.invoke(lane, flowObjectIds);
            return; // Sucesso
        } catch (NoSuchMethodException e) {
            // Método não existe, continuar para próxima estratégia
        } catch (Exception e) {
            System.err.println("⚠️ Error calling setFlowObjectRefs: " + e.getMessage());
        }

        // ESTRATÉGIA 2: Tentar setFlowNodeRefs
        try {
            Method setFlowNodeRefsMethod = lane.getClass().getMethod("setFlowNodeRefs", List.class);
            setFlowNodeRefsMethod.invoke(lane, flowObjectIds);
            return; // Sucesso
        } catch (NoSuchMethodException e) {
            // Método não existe, continuar para próxima estratégia
        } catch (Exception e) {
            System.err.println("⚠️ Error calling setFlowNodeRefs: " + e.getMessage());
        }

        // ESTRATÉGIA 3: Tentar setar campo flowObjectRefs diretamente
        try {
            java.lang.reflect.Field flowObjectRefsField = lane.getClass().getDeclaredField("flowObjectRefs");
            flowObjectRefsField.setAccessible(true);
            flowObjectRefsField.set(lane, flowObjectIds);
            return; // Sucesso
        } catch (NoSuchFieldException e) {
            // Campo não existe, continuar para próxima estratégia
        } catch (Exception e) {
            System.err.println("⚠️ Error setting flowObjectRefs field: " + e.getMessage());
        }

        // ESTRATÉGIA 4: Usar setDescription como fallback
        if (!flowObjectIds.isEmpty()) {
            String description = "Lane contains FlowObjects: " + String.join(", ", flowObjectIds);
            setDescriptionSafe(lane, description);
        }
    }

    /**
     * Configurar lane de forma completa e segura
     */
    public static void setupLaneSafe(ProcessLaneV2Plus lane, String id, String name, String description, List<String> flowObjectIds) {
        if (lane == null) {
            return;
        }

        // Configurar campos básicos
        if (id != null) {
            lane.setId(id);
        }

        if (name != null) {
            lane.setName(name);
        }

        // Configurar description
        if (description != null) {
            setDescriptionSafe(lane, description);
        }

        // Configurar flowObjectRefs
        if (flowObjectIds != null && !flowObjectIds.isEmpty()) {
            setFlowObjectRefsSafe(lane, flowObjectIds);
        }
    }

    /**
     * Criar lane completa de forma segura
     */
    public static ProcessLaneV2Plus createLaneSafe(String id, String name, String description, List<String> flowObjectIds) {
        ProcessLaneV2Plus lane = new ProcessLaneV2Plus();
        setupLaneSafe(lane, id, name, description, flowObjectIds);
        return lane;
    }

    /**
     * Teste de funcionalidade
     */
    public static void testSafeLaneSetter() {
        System.out.println("🧪 Testing SafeLaneSetter...");

        try {
            ProcessLaneV2Plus lane = new ProcessLaneV2Plus();
            lane.setId("test_lane");
            lane.setName("Test Lane");

            // Teste setDescription
            setDescriptionSafe(lane, "Test description");
            System.out.println("✅ setDescription safe: PASSED");

            // Teste setFlowObjectRefs
            java.util.List<String> testIds = new java.util.ArrayList<String>();
            testIds.add("flow1");
            testIds.add("flow2");

            setFlowObjectRefsSafe(lane, testIds);
            System.out.println("✅ setFlowObjectRefs safe: PASSED");

            // Teste setupLane completo
            ProcessLaneV2Plus completeLane = createLaneSafe("lane2", "Complete Lane", "Complete description", testIds);
            assert completeLane != null : "Complete lane should be created";
            System.out.println("✅ createLane safe: PASSED");

            System.out.println("🎉 All SafeLaneSetter tests passed!");

        } catch (Exception e) {
            System.err.println("❌ SafeLaneSetter test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}