package tk.checker.bpmn.utils.exceptions;

public class BpmnParseException extends Exception {
    public BpmnParseException(Throwable t) {
        super(t);
    }

    public BpmnParseException(String msg) {
        super(msg);
    }
}
