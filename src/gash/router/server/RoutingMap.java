package gash.router.server;

import java.util.HashMap;

public class RoutingMap {
    private static RoutingMap ourInstance = new RoutingMap();

    public static RoutingMap getInstance() {
        return ourInstance;
    }

    private HashMap<String, Node> internalServers;
    private HashMap<String, Node> externalServers;
    private HashMap<String, Node> clients;

    private RoutingMap() {
        internalServers = new HashMap<>();
        externalServers = new HashMap<>();
        clients = new HashMap<>();
    }

    public HashMap<String, Node> getInternalServers() {
        return internalServers;
    }

    public void addInternalServer(Node node) {
        this.internalServers.put(node.getNodeAddress(), node);
    }

    public HashMap<String, Node> getExternalServers() {
        return externalServers;
    }

    public void addExternalServer(Node node) {
        this.externalServers.put(node.getNodeAddress(), node);
    }

    public HashMap<String, Node> getClients() {
        return clients;
    }

    public void addClient(Node node) {
        this.clients.put(node.getNodeAddress(), node);
    }

    public void removeInternalServer(String nodeAddress) {
        this.internalServers.remove(nodeAddress);
    }

    public void removeExternalServer(String nodeAddress) {
        this.externalServers.remove(nodeAddress);
    }

    public void removeClient(String nodeAddress) {
        this.clients.remove(nodeAddress);
    }
}
