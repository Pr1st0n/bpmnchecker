package tk.checker.bpmn.utils;

public interface Constants {
    String PROCESS = "process";

    interface Activity {
        String TASK = "task";
    }

    interface Event {
        String START_EVENT = "startEvent";
        String END_EVENT = "endEvent";
    }

    interface Gateway {
        String PARALLEL_GATEWAY = "parallelGateway";
        String INCLUSIVE_GATEWAY = "inclusiveGateway";
        String EXCLUSIVE_GATEWAY = "exclusiveGateway";
    }

    interface Connector {
        String SEQUENCE_FLOW = "sequenceFlow";
        String INCOMING = "incoming";
        String OUTGOING = "outgoing";
    }

    interface Attribute {
        String ID = "id";
        String SOURCE_REF = "sourceRef";
        String TARGET_REF = "targetRef";
    }
}
