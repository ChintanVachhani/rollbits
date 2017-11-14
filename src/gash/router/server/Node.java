package gash.router.server;

import routing.Pipe;

public class Node {

    private Pipe.NetworkDiscoveryPacket.Mode mode;

    private Pipe.NetworkDiscoveryPacket.Sender sender;

    private String groupTag;

    private String nodeId;

    private String nodeAddress;

    private int nodePort;

    private String secret;

    public Node() {}

    public Node(Pipe.NetworkDiscoveryPacket.Mode mode, Pipe.NetworkDiscoveryPacket.Sender sender, String groupTag, String nodeId, String nodeAddress, int nodePort, String secret) {
        super();
        this.mode = mode;
        this.sender = sender;
        this.groupTag = groupTag;
        this.nodeId = nodeId;
        this.nodeAddress = nodeAddress;
        this.nodePort = nodePort;
        this.secret = secret;
    }

    public Pipe.NetworkDiscoveryPacket.Mode getMode() {
        return mode;
    }

    public void setMode(Pipe.NetworkDiscoveryPacket.Mode mode) {
        this.mode = mode;
    }

    public Pipe.NetworkDiscoveryPacket.Sender getSender() {
        return sender;
    }

    public void setSender(Pipe.NetworkDiscoveryPacket.Sender sender) {
        this.sender = sender;
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

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
