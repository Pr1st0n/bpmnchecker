package tk.checker.bpmn.utils.exceptions;

public class BpmnVerifyException extends Exception {
    public BpmnVerifyException(Throwable t) {
        super(t);
    }

    public BpmnVerifyException(String msg) {
        super(msg);
    }
}
