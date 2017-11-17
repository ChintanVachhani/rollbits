package gash.router.server;

import java.util.ArrayList;
import java.util.HashMap;

public class RoutingMap {
    private static RoutingMap ourInstance = new RoutingMap();

    public static RoutingMap getInstance() {
        return ourInstance;
    }

    private HashMap<String, Node> internalServers;
    private HashMap<String, ArrayList<Node>> externalServers;
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

    public HashMap<String, ArrayList<Node>> getExternalServers() {
        return externalServers;
    }

    public void addExternalServer(Node node) {
        this.externalServers.get(node.getGroupTag()).add(node);
    }

    public HashMap<String, Node> getClients() {
        return clients;
    }

    public void addClient(Node node) {
        this.clients.put(node.getNodeAddress(), node);
    }

    public void removeInternalServer(Node node) {
        this.internalServers.remove(node.getNodeAddress());
    }

    public void removeExternalServer(Node node) {
        this.externalServers.get(node.getGroupTag()).remove(node);
    }

    public void removeClient(Node node) {
        this.clients.remove(node.getNodeAddress());
    }
}
