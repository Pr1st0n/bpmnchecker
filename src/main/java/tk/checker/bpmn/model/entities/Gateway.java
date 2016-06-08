package tk.checker.bpmn.model.entities;

import tk.checker.bpmn.model.FlowEntity;

import java.util.Collections;
import java.util.List;

public class Gateway implements FlowEntity {
    public enum GatewayType implements FlowEntityType {EXCLUSIVE, PARALLEL, INCLUSIVE}

    private final String id;
    private final GatewayType type;
    private final List<String> incoming;
    private final List<String> outgoing;

    public Gateway(String id, GatewayType type, List<String> incoming, List<String> outgoing) {
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
    public GatewayType getType() {
        return type;
    }

    public List<String> getIncoming() {
        return Collections.unmodifiableList(incoming);
    }

    public List<String> getOutgoing() {
        return Collections.unmodifiableList(outgoing);
    }
}
