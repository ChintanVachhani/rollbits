package gash.router.server;

import gash.router.server.communication.HeartbeatClient;

public class Node {

    private String nodeType;

    private String groupTag;

    private String nodeId;

    private String nodeAddress;

    private int nodePort;

    public Node() {
    }

    public Node(String nodeType, String groupTag, String nodeId, String nodeAddress, int nodePort) {
        super();
        this.nodeType = nodeType;
        this.groupTag = groupTag;
        this.nodeId = nodeId;
        this.nodeAddress = nodeAddress;
        this.nodePort = nodePort;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getGroupTag() {
        return groupTag;
    }

    public void setGroupTag(String groupTag) {
        this.groupTag = groupTag;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public int getNodePort() {
        return nodePort;
    }

    public void setNodePort(int nodePort) {
        this.nodePort = nodePort;
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeType='" + nodeType + '\'' +
                ", groupTag='" + groupTag + '\'' +
                ", nodeId='" + nodeId + '\'' +
                ", nodeAddress='" + nodeAddress + '\'' +
                ", nodePort=" + nodePort +
                '}';
    }
}
