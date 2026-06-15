package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA;

import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.Teamworks;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpd.Lane;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.bpmn.*;
import br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Item;

import java.util.*;
import java.util.stream.Stream;

/**
 * Extracts dependency discovery logic from ProcessLoader.
 * Responsible for finding all artifacts that a given artifact depends on.
 */
public class DependencyExtractorService {

    /**
     * Extracts all dependency IDs from any supported artifact type.
     *
     * @param artifact The artifact to analyze (Definitions or Teamworks)
     * @return Set of dependency IDs that this artifact calls/references
     */
    public Set<String> extractDependencies(Object artifact) {
        if (artifact instanceof Definitions) {
            return extractBpmnDependencies((Definitions) artifact);
        } else if (artifact instanceof Teamworks) {
            return extractLegacyDependencies((Teamworks) artifact);
        }
        return Collections.emptySet();
    }

    /**
     * Extracts dependencies from modern BPMN processes.
     */
    private Set<String> extractBpmnDependencies(Definitions definitions) {
        Set<String> dependencies = new HashSet<>();

        if (definitions.getProcess() != null) {
            collectCallActivities(definitions.getProcess().getFlowElements(), dependencies);
        }

        return dependencies;
    }

    /**
     * Extracts dependencies from legacy Teamworks artifacts.
     * Handles both Services and BPDs.
     */
    private Set<String> extractLegacyDependencies(Teamworks teamworks) {
        Set<String> dependencies = new HashSet<>();

        // Extract from Service processes
        if (teamworks.getProcess() != null) {
            extractServiceDependencies(teamworks.getProcess(), dependencies);
        }

        // Extract from BPD processes
        if (teamworks.getBpd() != null) {
            extractBpdDependencies(teamworks.getBpd(), dependencies);
        }

        return dependencies;
    }

    /**
     * Recursively collects CallActivity references from BPMN flow elements.
     */
    private void collectCallActivities(List<Object> elements, Set<String> dependencies) {
        if (elements == null) return;

        for (Object element : elements) {
            if (element instanceof CallActivity) {
                CallActivity callActivity = (CallActivity) element;
                String calledElement = callActivity.getCalledElement();
                if (isValidDependencyId(calledElement)) {
                    dependencies.add(calledElement);
                }
            } else if (element instanceof SubProcess) {
                SubProcess subProcess = (SubProcess) element;
                collectCallActivities(subProcess.getFlowElements(), dependencies);
            }
        }
    }

    /**
     * Extracts dependencies from legacy Service processes.
     */
    private void extractServiceDependencies(br.com.danzeroum.bpmbaw.mapeadorxml.modelo.process.Process process,
                                            Set<String> dependencies) {
        // Extract from Coachflow (modern service structure)
        if (process.getCoachflow() != null && process.getCoachflow().getDefinitions() != null) {
            GlobalUserTask globalUserTask = process.getCoachflow().getDefinitions().getGlobalUserTask();
            if (globalUserTask != null && globalUserTask.getImplementation() != null) {
                collectCallActivities(globalUserTask.getImplementation().getFlowElements(), dependencies);
            }
        }

        // Extract from legacy service items
        if (process.getItems() != null) {
            process.getItems().stream()
                    .map(Item::getTwComponent)
                    .filter(Objects::nonNull)
                    .map(component -> component.getAttachedProcessRef())
                    .filter(this::isValidDependencyId)
                    .forEach(dependencies::add);
        }
    }

    /**
     * Extracts dependencies from legacy BPD structures.
     * Handles the complex nested structure of BPDs without bpmn2Data.
     */
    private void extractBpdDependencies(Bpd bpd, Set<String> dependencies) {
        if (bpd.getBusinessProcessDiagram() == null) return;

        BusinessProcessDiagram diagram = bpd.getBusinessProcessDiagram();
        if (diagram.getPools() == null) return;

        diagram.getPools().stream()
                .filter(Objects::nonNull)
                .flatMap(this::extractLanesFromPool)
                .flatMap(this::extractFlowObjectsFromLane)
                .filter(this::hasValidImplementation)
                .map(FlowObject::getComponent)
                .map(Component::getImplementation)
                .forEach(impl -> extractImplementationDependencies(impl, dependencies));
    }

    /**
     * Safely extracts lanes from a pool.
     */
    private Stream<Lane> extractLanesFromPool(Pool pool) {
        return pool.getLanes() != null ?
                pool.getLanes().stream().filter(Objects::nonNull) :
                Stream.empty();
    }

    /**
     * Safely extracts flow objects from a lane.
     */
    private Stream<FlowObject> extractFlowObjectsFromLane(Lane lane) {
        return lane.getFlowObjects() != null ?
                lane.getFlowObjects().stream().filter(Objects::nonNull) :
                Stream.empty();
    }

    /**
     * Checks if a flow object has a valid implementation for dependency extraction.
     */
    private boolean hasValidImplementation(FlowObject flowObject) {
        return flowObject.getComponent() != null &&
                flowObject.getComponent().getImplementation() != null;
    }

    /**
     * Extracts dependency IDs from an Implementation object.
     * Checks multiple possible reference fields.
     */
    private void extractImplementationDependencies(Implementation implementation, Set<String> dependencies) {
        // Check attached activity ID
        if (isValidDependencyId(implementation.getAttachedActivityId())) {
            dependencies.add(implementation.getAttachedActivityId());
        }

        // Check attached process ID
        if (isValidDependencyId(implementation.getAttachedProcessId())) {
            dependencies.add(implementation.getAttachedProcessId());
        }

        // Check embedded process ID
        if (isValidDependencyId(implementation.getEmbeddedProcessId())) {
            dependencies.add(implementation.getEmbeddedProcessId());
        }
    }

    /**
     * Validates that a dependency ID is not null or empty.
     */
    private boolean isValidDependencyId(String dependencyId) {
        return dependencyId != null && !dependencyId.trim().isEmpty();
    }

    /**
     * Gets a human-readable summary of dependencies for debugging.
     */
    public DependencyAnalysisSummary analyzeDependencies(Object artifact) {
        Set<String> dependencies = extractDependencies(artifact);

        String artifactType = determineArtifactType(artifact);
        String artifactName = determineArtifactName(artifact);

        return new DependencyAnalysisSummary(
                artifactName,
                artifactType,
                dependencies,
                dependencies.size()
        );
    }

    private String determineArtifactType(Object artifact) {
        if (artifact instanceof Definitions) {
            return "BPMN Process";
        } else if (artifact instanceof Teamworks) {
            Teamworks tw = (Teamworks) artifact;
            if (tw.getProcess() != null) {
                return "Legacy Service";
            } else if (tw.getBpd() != null) {
                return "Legacy BPD";
            }
            return "Teamworks (Unknown)";
        }
        return "Unknown";
    }

    private String determineArtifactName(Object artifact) {
        if (artifact instanceof Definitions) {
            Definitions def = (Definitions) artifact;
            if (def.getProcess() != null) {
                return def.getProcess().getName() != null ?
                        def.getProcess().getName() : "Unnamed BPMN Process";
            }
        } else if (artifact instanceof Teamworks) {
            Teamworks tw = (Teamworks) artifact;
            if (tw.getProcess() != null && tw.getProcess().getName() != null) {
                return tw.getProcess().getName();
            }
        }
        return "Unnamed Artifact";
    }

    /**
     * Summary of dependency analysis for an artifact.
     */
    public static class DependencyAnalysisSummary {
        private final String artifactName;
        private final String artifactType;
        private final Set<String> dependencies;
        private final int dependencyCount;

        public DependencyAnalysisSummary(String artifactName, String artifactType,
                                         Set<String> dependencies, int dependencyCount) {
            this.artifactName = artifactName;
            this.artifactType = artifactType;
            this.dependencies = new HashSet<>(dependencies);
            this.dependencyCount = dependencyCount;
        }

        public String getArtifactName() { return artifactName; }
        public String getArtifactType() { return artifactType; }
        public Set<String> getDependencies() { return Collections.unmodifiableSet(dependencies); }
        public int getDependencyCount() { return dependencyCount; }

        @Override
        public String toString() {
            return String.format("DependencyAnalysis{name='%s', type='%s', dependencies=%d, refs=%s}",
                    artifactName, artifactType, dependencyCount, dependencies);
        }
    }
}