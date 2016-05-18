package tk.checker.bpmn.service;

import tk.checker.bpmn.model.*;
import tk.checker.bpmn.model.CommonVerificationError.VerificationErrorType;
import tk.checker.bpmn.model.Process;
import tk.checker.bpmn.model.element.Event;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.Set;

public class BpmnVerifier {
    private BpmnVerifier() {}

    public static Process verify(Process process) {
        DirectedGraph<FlowEntity, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (ConnectionEntity connection : process.getConnectionEntities()) {
            FlowEntity source = process.getFlowEntityById(connection.getSourceRef());
            FlowEntity target = process.getFlowEntityById(connection.getTargetRef());

            if (source == null || target == null) {
                process.addError(new EntityVerificationError(connection.getId(), VerificationErrorType.INCORRECT_USAGE, "Connection having missed endpoint"));
            }

            if (!directedGraph.containsVertex(source)) {
                directedGraph.addVertex(source);
            }

            if (!directedGraph.containsVertex(target)) {
                directedGraph.addVertex(target);
            }

            directedGraph.addEdge(source, target);
        }

        // Check model for Livelock.
        CycleDetector detector = new CycleDetector<>(directedGraph);

        if (detector.detectCycles()) {
            Set cycle = detector.findCycles();

            //TODO: Handle proper cycles!
            process.addError(new CommonVerificationError(VerificationErrorType.LIVELOCK, "Found Dead-lock for " + cycle));
        }

        // Check for connectivity.
        ConnectivityInspector<FlowEntity, DefaultEdge> connectivityInspector =
            new ConnectivityInspector<>(directedGraph);
        Event startEvent = process.getStartEvent();
        List<Event> endEvents = process.getEndEvents();

        if (endEvents.isEmpty() || startEvent == null) {
            process.addError(new CommonVerificationError(VerificationErrorType.LIVELOCK, "Incorrect model missing start or end events"));
        }

        for (Event endEvent : endEvents) {
            if (!connectivityInspector.pathExists(startEvent, endEvent)) {
                process.addError(new CommonVerificationError(VerificationErrorType.ACTIVE_TERMENATION, "Missing path from " + startEvent.getId() + " to " + endEvent.getId()));
            };
        }

        //TODO {
        // computes all the strongly connected components of the directed graph
        KosarajuStrongConnectivityInspector sci = new KosarajuStrongConnectivityInspector<>(directedGraph);
        List stronglyConnectedSubgraphs = sci.stronglyConnectedSubgraphs();

        // prints the strongly connected components
        System.out.println("Strongly connected components:");
        for (Object item : stronglyConnectedSubgraphs) {
            System.out.println(item);
        }
        // TODO }

        return process;
    }
}
