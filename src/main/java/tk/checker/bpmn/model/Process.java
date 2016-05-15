package tk.checker.bpmn.model;

import tk.checker.bpmn.model.element.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Process {
    private final String id;
    private final List<FlowEntity> flowEntities;
    private final List<ConnectionEntity> connectionEntities;

    public Process(String id, List<FlowEntity> flowEntities, List<ConnectionEntity> connectionEntities) {
        this.id = id;
        this.flowEntities = flowEntities;
        this.connectionEntities = connectionEntities;
    }

    public String getId() {
        return id;
    }

    public List<FlowEntity> getFlowEntities() {
        return Collections.unmodifiableList(flowEntities);
    }

    public List<ConnectionEntity> getConnectionEntities() {
        return Collections.unmodifiableList(connectionEntities);
    }

    @Nullable
    public Event getStartEvent() {
        Event start = null;

        for (FlowEntity flowEntity : flowEntities) {
            if (flowEntity.getType().equals(Event.EventType.START)) {
                start = (Event) flowEntity;

                break;
            }
        }

        return start;
    }

    @Nullable
    public List<Event> getEndEvents() {
        List<Event> endEvents = new ArrayList<>();

        for (FlowEntity flowEntity : flowEntities) {
            if (flowEntity.getType().equals(Event.EventType.END)) {
                endEvents.add((Event) flowEntity);
            }
        }

        return endEvents;
    }

    public List<ConnectionEntity> getConnectionsByEndPoint(FlowEntity flowEntity) {
        List<ConnectionEntity> connections = new ArrayList<>();

        for (ConnectionEntity connectionEntity : connectionEntities) {
            if (connectionEntity.getSourceRef().equals(flowEntity.getId())
                || connectionEntity.getTargetRef().equals(flowEntity.getId())) {
                connections.add(connectionEntity);
            }
        }

        return connections;
    }

    @Nullable
    public FlowEntity getFlowEntityById(String id) {
        FlowEntity flowEntity = null;

        for (FlowEntity flowEntity0 : flowEntities) {
            if (flowEntity0.getId().equals(id)) {
                flowEntity = flowEntity0;

                break;
            }
        }

        return flowEntity;
    }
}
