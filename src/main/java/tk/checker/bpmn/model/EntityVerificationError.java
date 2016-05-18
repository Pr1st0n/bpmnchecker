package tk.checker.bpmn.model;

public class EntityVerificationError extends CommonVerificationError {
    private final String entityId;

    public EntityVerificationError(String entityId, VerificationErrorType type, String message) {
        super(type, message);
        this.entityId = entityId;
    }

    public String getEntityId() {
        return entityId;
    }
}
