package tk.checker.bpmn.model.entities;

import tk.checker.bpmn.model.FlowEntity;

public class Activity implements FlowEntity {
    public enum ActivityType implements FlowEntityType {TASK}

    private final String id;
    private final ActivityType type;
    private final String incoming;
    private final String  outgoing;

    public Activity(String id, ActivityType type, String incoming, String outgoing) {
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
    public ActivityType getType() {
        return type;
    }

    public String getIncoming() {
        return incoming;
    }

    public String getOutgoing() {
        return outgoing;
    }
}
