package tk.checker.bpmn.model;

public interface FlowEntity {
    interface FlowEntityType {}

    String getId();

    FlowEntityType getType();
}
