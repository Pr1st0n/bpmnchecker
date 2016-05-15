package tk.checker.bpmn.service;

import org.springframework.web.multipart.MultipartFile;
import tk.checker.bpmn.utils.Constants;
import tk.checker.bpmn.utils.exceptions.BpmnParseException;
import tk.checker.bpmn.utils.exceptions.BpmnVerifyException;
import tk.checker.bpmn.model.ConnectionEntity;
import tk.checker.bpmn.model.FlowEntity;
import tk.checker.bpmn.model.Process;
import tk.checker.bpmn.model.connection.Connector;
import tk.checker.bpmn.model.element.Activity;
import tk.checker.bpmn.model.element.Activity.ActivityType;
import tk.checker.bpmn.model.element.Event;
import tk.checker.bpmn.model.element.Event.EventType;
import tk.checker.bpmn.model.element.Gateway;
import tk.checker.bpmn.model.element.Gateway.GatewayType;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

import static tk.checker.bpmn.utils.Constants.*;

public class BpmnParser {
    private BpmnParser() {}

    @Nullable
    public static Process parse(MultipartFile file) throws BpmnParseException {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file.getInputStream());

            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();

            NodeList childNodes = root.getChildNodes();
            Node processElement = null;

            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i).getNodeName().contains(PROCESS)) {
                    processElement = childNodes.item(i);
                }
            }

            if (processElement == null) {
                throw new BpmnParseException("Process node not found");
            }

            return parseProcess(processElement);
        }
        catch (Exception e) {
            throw new BpmnParseException(e);
        }
    }

    private static Process parseProcess(Node processElement) throws BpmnParseException, BpmnVerifyException {
        String id = getAttribute(processElement, Attribute.ID);
        List<FlowEntity> flowEntities = new ArrayList<>();
        List<ConnectionEntity> connectionEntities = new ArrayList<>();
        NodeList childNodes = processElement.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (!validNode(node)) {
                continue;
            }

            ConnectionEntity connectionEntity = parseConnectionEntity(node);

            if (connectionEntity != null) {
                connectionEntities.add(connectionEntity);

                continue;
            }

            FlowEntity flowEntity = parseFlowEntity(node);

            if (flowEntity != null) {
                flowEntities.add(flowEntity);
            }
        }

        return new Process(id, flowEntities, connectionEntities);
    }

    @Nullable
    private static FlowEntity parseFlowEntity(Node node) throws BpmnParseException, BpmnVerifyException {
        String nodeName = node.getNodeName();
        NodeList childNodes = node.getChildNodes();
        String id = getAttribute(node, Attribute.ID);
        String incoming = null;
        String outgoing = null;
        FlowEntity entity;

        if (nodeName.contains(Constants.Activity.TASK)) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node connection = childNodes.item(i);

                if (!validNode(connection)) {
                    continue;
                }

                if (connection.getNodeName().contains(Constants.Connector.INCOMING)) {
                    if (incoming != null) {
                        throw new BpmnVerifyException
                            ("Invalid start event " + id + " having multiple incoming connections");
                    }

                    incoming = connection.getTextContent();
                }

                if (connection.getNodeName().contains(Constants.Connector.OUTGOING)) {
                    if (outgoing != null) {
                        throw new BpmnVerifyException
                            ("Invalid start event " + id + " having multiple outgoing connections");
                    }

                    outgoing = connection.getTextContent();
                }
            }

            if (incoming == null || outgoing == null) {
                throw new BpmnVerifyException("Invalid task " + id + " having missed connection");
            }

            entity = new Activity(id, ActivityType.TASK, incoming, outgoing);
        } else if (nodeName.contains(Constants.Event.START_EVENT)) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node connection = childNodes.item(i);

                if (!validNode(connection)) {
                    continue;
                }

                if (connection.getNodeName().contains(Constants.Connector.INCOMING)) {
                    throw new BpmnVerifyException("Invalid start event " + id + " having incoming connection");
                }

                if (connection.getNodeName().contains(Constants.Connector.OUTGOING)) {
                    if (outgoing != null) {
                        throw new BpmnVerifyException
                            ("Invalid start event " + id + " having multiple outgoing connections");
                    }

                    outgoing = connection.getTextContent();
                }
            }

            if (outgoing == null) {
                throw new BpmnVerifyException("Invalid start event " + id + " having missed outgoing connection");
            }

            entity = new Event(id, EventType.START, null, outgoing);
        } else if (nodeName.contains(Constants.Event.END_EVENT)) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node connection = childNodes.item(i);

                if (!validNode(connection)) {
                    continue;
                }

                if (connection.getNodeName().contains(Constants.Connector.INCOMING)) {
                    if (incoming != null) {
                        throw new BpmnVerifyException
                            ("Invalid start event " + id + " having multiple incoming connections");
                    }

                    incoming = connection.getTextContent();
                }

                if (connection.getNodeName().contains(Constants.Connector.OUTGOING)) {
                    throw new BpmnVerifyException("Invalid start event " + id + " having outgoing connection");
                }
            }

            if (incoming == null) {
                throw new BpmnVerifyException("Invalid start event " + id + " having missed incoming connection");
            }

            entity = new Event(id, EventType.END, incoming, null);
        } else {
            GatewayType gatewayType = null;

            if (nodeName.contains(Constants.Gateway.PARALLEL_GATEWAY)) {
                gatewayType = GatewayType.PARALLEL;
            } else if (nodeName.contains(Constants.Gateway.INCLUSIVE_GATEWAY)) {
                gatewayType = GatewayType.INCLUSIVE;
            } else if (nodeName.contains(Constants.Gateway.EXCLUSIVE_GATEWAY)) {
                gatewayType = GatewayType.EXCLUSIVE;
            }


            //TODO
            if (gatewayType == null) {
                return null;
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
                throw new BpmnVerifyException("Invalid gateway " + id + " having missed connections");
            }

            entity = new Gateway(id, gatewayType, incomingList, outgoingList);
        }

        return entity;
    }

    @Nullable
    private static ConnectionEntity parseConnectionEntity(Node node) throws BpmnParseException  {
        String nodeName = node.getNodeName();
        ConnectionEntity entity = null;

        if (nodeName.contains(Constants.Connector.SEQUENCE_FLOW)) {
            String id = getAttribute(node, Attribute.ID);
            String sourceRef = getAttribute(node, Attribute.SOURCE_REF);
            String targetRef = getAttribute(node, Attribute.TARGET_REF);

            entity = new Connector(id, sourceRef, targetRef);
        }

        return entity;
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
