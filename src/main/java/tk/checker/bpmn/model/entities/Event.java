package tk.checker.bpmn.model.entities;

import tk.checker.bpmn.model.FlowEntity;
import org.jetbrains.annotations.Nullable;

public class Event implements FlowEntity {
    public enum EventType implements FlowEntityType {START, END}

    private final String id;
    private final EventType type;
    private final String incoming;
    private final String outgoing;

    public Event(String id, EventType type, @Nullable String incoming, @Nullable String outgoing) {
        this.id = id;
        this.type = type;
        this.incoming = incoming;
        this.outgoing = outgoing;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Nullable
    public String getIncoming() {
        return incoming;
    }

    @Nullable
    public String getOutgoing() {
        return outgoing;
    }
}
