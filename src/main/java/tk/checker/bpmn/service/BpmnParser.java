package tk.checker.bpmn.service;

import org.springframework.web.multipart.MultipartFile;
import tk.checker.bpmn.service.CommonVerificationError.VerificationErrorType;
import tk.checker.bpmn.utils.Constants;
import tk.checker.bpmn.utils.BpmnParseException;
import tk.checker.bpmn.model.FlowEntity;
import tk.checker.bpmn.model.Process;
import tk.checker.bpmn.model.entities.Connector;
import tk.checker.bpmn.model.entities.Activity;
import tk.checker.bpmn.model.entities.Activity.ActivityType;
import tk.checker.bpmn.model.entities.Event;
import tk.checker.bpmn.model.entities.Event.EventType;
import tk.checker.bpmn.model.entities.Gateway;
import tk.checker.bpmn.model.entities.Gateway.GatewayType;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

import static tk.checker.bpmn.utils.Constants.*;

public class BpmnParser {
    private BpmnParser() {}

    public static Process parse(MultipartFile file) throws BpmnParseException {
        Node processElement = null;

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file.getInputStream());

            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();

            NodeList childNodes = root.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i).getNodeName().contains(PROCESS)) {
                    processElement = childNodes.item(i);
                }
            }
        }
        catch (Exception e) {
            throw new BpmnParseException(e.getCause().getMessage());
        }

        if (processElement == null) {
            throw new BpmnParseException("Process node does not exist");
        }

        return parseProcess(processElement);
    }

    private static Process parseProcess(Node processElement) throws BpmnParseException {
        String id = getAttribute(processElement, Attribute.ID);
        Process process = new Process(id);
        NodeList childNodes = processElement.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (!validNode(node)) {
                continue;
            }

            parseEntityNode(process, node);
        }

        return process;
    }

    private static void parseEntityNode(Process process, Node node) throws BpmnParseException {
        String nodeName = node.getNodeName();
        NodeList childNodes = node.getChildNodes();
        String id = getAttribute(node, Attribute.ID);
        FlowEntity entity;

        if (nodeName.contains(Constants.Connector.SEQUENCE_FLOW)) {
            String sourceRef = getAttribute(node, Attribute.SOURCE_REF);
            String targetRef = getAttribute(node, Attribute.TARGET_REF);

            process.addConnectionEntity(new Connector(id, sourceRef, targetRef));

            return;
        }

        String incoming = null;
        String outgoing = null;

        // Handle Task Entity parse.
        if (nodeName.contains(Constants.Activity.TASK)) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node connection = childNodes.item(i);

                if (!validNode(connection)) {
                    continue;
                }

                if (connection.getNodeName().contains(Constants.Connector.INCOMING)) {
                    if (incoming != null) {
                        process.addError(new EntityVerificationError
                            (id, VerificationErrorType.INCORRECT_USAGE, "Task having multiple incoming connections"));

                        continue;
                    }

                    incoming = connection.getTextContent();
                } else if (connection.getNodeName().contains(Constants.Connector.OUTGOING)) {
                    if (outgoing != null) {
                        process.addError(new EntityVerificationError
                            (id, VerificationErrorType.INCORRECT_USAGE, "Task having multiple outgoing connections"));

                        continue;
                    }

                    outgoing = connection.getTextContent();
                }
            }

            if (incoming == null || outgoing == null) {
                process.addError(new EntityVerificationError
                    (id, VerificationErrorType.INCORRECT_USAGE, "Task having missed connection"));

                return;
            }

            process.addFlowEntity(new Activity(id, ActivityType.TASK, incoming, outgoing));

            return;
        }

        //Handle Event Entity parse.
        if (nodeName.contains(Constants.Event.START_EVENT)) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node connection = childNodes.item(i);

                if (!validNode(connection)) {
                    continue;
                }

                if (connection.getNodeName().contains(Constants.Connector.INCOMING)) {
                    process.addError(new EntityVerificationError
                        (id, VerificationErrorType.INCORRECT_USAGE, "Start event having incoming connection"));
                } else if (connection.getNodeName().contains(Constants.Connector.OUTGOING)) {
                    if (outgoing != null) {
                        process.addError(new EntityVerificationError
                            (id, VerificationErrorType.INCORRECT_USAGE, "Start event having multiple outgoing connections"));

                        continue;
                    }

                    outgoing = connection.getTextContent();
                }
            }

            if (outgoing == null) {
                process.addError(new EntityVerificationError
                    (id, VerificationErrorType.INCORRECT_USAGE, "Start event having missed outgoing connection"));

                return;
            }

            process.setStartEvent(new Event(id, EventType.START, null, outgoing));

            return;
        }

        if (nodeName.contains(Constants.Event.END_EVENT)) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node connection = childNodes.item(i);

                if (!validNode(connection)) {
                    continue;
                }

                if (connection.getNodeName().contains(Constants.Connector.INCOMING)) {
                    if (incoming != null) {
                        process.addError(new EntityVerificationError
                            (id, VerificationErrorType.ACTIVE_TERMENATION, "End event having multiple incoming connections"));

                        continue;
                    }

                    incoming = connection.getTextContent();
                } else if (connection.getNodeName().contains(Constants.Connector.OUTGOING)) {
                    process.addError(new EntityVerificationError
                        (id, VerificationErrorType.INCORRECT_USAGE, "End event having outgoing connection"));
                }
            }

            if (incoming == null) {
                process.addError(new EntityVerificationError
                    (id, VerificationErrorType.INCORRECT_USAGE, "End event having missed incoming connection"));

                return;
            }

            process.addEndEvent(new Event(id, EventType.END, incoming, null));

            return;
        }

        GatewayType gatewayType = null;


        if (nodeName.contains(Constants.Gateway.PARALLEL_GATEWAY)) {
            gatewayType = GatewayType.PARALLEL;
        } else if (nodeName.contains(Constants.Gateway.INCLUSIVE_GATEWAY)) {
            gatewayType = GatewayType.INCLUSIVE;
        } else if (nodeName.contains(Constants.Gateway.EXCLUSIVE_GATEWAY)) {
            gatewayType = GatewayType.EXCLUSIVE;
        }

        if (gatewayType == null) {
            throw new BpmnParseException("Unknown element type");
        }

        List<String> incomingList = new ArrayList<>();
        List<String> outgoingList = new ArrayList<>();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node gateway = childNodes.item(i);

            if (!validNode(gateway)) {
                continue;
            }

            if (gateway.getNodeName().contains(Constants.Connector.INCOMING)) {
                incomingList.add(gateway.getTextContent());
            }

            if (gateway.getNodeName().contains(Constants.Connector.OUTGOING)) {
                outgoingList.add(gateway.getTextContent());
            }
        }

        if (incomingList.isEmpty() || outgoingList.isEmpty()) {
            process.addError(new EntityVerificationError
                (id, VerificationErrorType.INCORRECT_USAGE, "Gateway having missed connections"));

            return;
        }

        process.addFlowEntity(new Gateway(id, gatewayType, incomingList, outgoingList));
    }

    private static String getAttribute(Node node, String attribute) throws BpmnParseException {
        NamedNodeMap attributes = node.getAttributes();

        for (int i = 0; i < attributes.getLength() ; i++) {
            if (attributes.item(i).getNodeName().equals(attribute)) {
                return attributes.item(i).getNodeValue();
            }
        }

        throw new BpmnParseException("No such attribute " + attribute);
    }

    private static boolean validNode(Node node) {
        return node.getAttributes() != null;
    }
}
