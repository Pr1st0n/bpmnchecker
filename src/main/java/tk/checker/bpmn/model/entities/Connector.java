package tk.checker.bpmn.model.entities;

import tk.checker.bpmn.model.ConnectionEntity;

public class Connector implements ConnectionEntity {
    private final String id;
    private final String sourceRef;
    private final String targetRef;

    public Connector(String id, String sourceRef, String targetRef) {
        this.id = id;
        this.sourceRef = sourceRef;
        this.targetRef = targetRef;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getSourceRef() {
        return sourceRef;
    }

    @Override
    public String getTargetRef() {
        return targetRef;
    }
}
