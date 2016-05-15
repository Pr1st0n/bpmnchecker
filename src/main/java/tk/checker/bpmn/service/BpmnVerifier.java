package tk.checker.bpmn.service;

import tk.checker.bpmn.utils.exceptions.BpmnVerifyException;
import tk.checker.bpmn.model.ConnectionEntity;
import tk.checker.bpmn.model.FlowEntity;
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

    public static boolean verify(Process process) throws BpmnVerifyException {
        DirectedGraph<FlowEntity, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (ConnectionEntity connection : process.getConnectionEntities()) {
            FlowEntity source = process.getFlowEntityById(connection.getSourceRef());
            FlowEntity target = process.getFlowEntityById(connection.getTargetRef());

            if (source == null || target == null) {
                throw new BpmnVerifyException("Mising FlowEntity for connection id: " + connection.getId());
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
            throw new BpmnVerifyException("Found Dead-lock for " + cycle);
        }

        // Check for connectivity.
        ConnectivityInspector<FlowEntity, DefaultEdge> connectivityInspector =
            new ConnectivityInspector<>(directedGraph);
        Event startEvent = process.getStartEvent();
        List<Event> endEvents = process.getEndEvents();

        if (endEvents.isEmpty() || startEvent == null) {
            throw new BpmnVerifyException("Incorrect model missing start or end events");
        }

        for (Event endEvent : endEvents) {
            if (!connectivityInspector.pathExists(startEvent, endEvent)) {
                throw new BpmnVerifyException("Missing path from " + startEvent.getId() + " to " + endEvent.getId());
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

        return true;
    }
}
