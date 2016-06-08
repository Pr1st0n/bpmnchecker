package tk.checker.bpmn.service;

public class CommonVerificationError {
    public enum VerificationErrorType {
        INCORRECT_USAGE,
        DEADLOCK,
        LIVELOCK,
        UNINTENTIONAL_MULTIPLE_EXECUTION,
        ACTIVE_TERMENATION,
        OTHER
    }

    private final VerificationErrorType type;
    private final String message;

    public CommonVerificationError(VerificationErrorType type, String message) {
        this.type = type;
        this.message = message;
    }


    public VerificationErrorType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
