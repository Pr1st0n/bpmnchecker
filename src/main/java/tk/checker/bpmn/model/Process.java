package tk.checker.bpmn.model;

import tk.checker.bpmn.model.entities.Event;
import org.jetbrains.annotations.Nullable;
import tk.checker.bpmn.service.CommonVerificationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Process {
    private final String id;
    private List<FlowEntity> flowEntities = new ArrayList<>();
    private List<ConnectionEntity> connectionEntities = new ArrayList<>();
    private Event startEvent;
    private List<Event> endEvents = new ArrayList<>();
    private List<CommonVerificationError> errorList = new ArrayList<>();

    public Process(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void addFlowEntity(FlowEntity flowEntity) {
        this.flowEntities.add(flowEntity);
    }

    public void addConnectionEntity(ConnectionEntity connectionEntity) {
        this.connectionEntities.add(connectionEntity);
    }

    public List<FlowEntity> getFlowEntities() {
        return Collections.unmodifiableList(flowEntities);
    }

    public List<ConnectionEntity> getConnectionEntities() {
        return Collections.unmodifiableList(connectionEntities);
    }

    public void setStartEvent(Event startEvent) {
        addFlowEntity(startEvent);
        this.startEvent = startEvent;
    }

    public void addEndEvent(Event endEvent) {
        addFlowEntity(endEvent);
        this.endEvents.add(endEvent);
    }

    public Event getStartEvent() {
        return startEvent;
    }

    public List<Event> getEndEvents() {
        return Collections.unmodifiableList(endEvents);
    }

    public List<CommonVerificationError> getErrorList() {
        return Collections.unmodifiableList(errorList);
    }

    public void addError(CommonVerificationError error) {
        this.errorList.add(error);
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
        for (FlowEntity flowEntity0 : flowEntities) {
            if (flowEntity0.getId().equals(id)) {
                return flowEntity0;
            }
        }

        return null;
    }
}
