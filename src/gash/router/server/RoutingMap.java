package gash.router.server;

import java.util.HashSet;

public class RoutingMap {
    private static RoutingMap ourInstance = new RoutingMap();

    public static RoutingMap getInstance() {
        return ourInstance;
    }

    private HashSet<Node> internalServers;
    private HashSet<Node> externalServers;
    private HashSet<Node> clients;

    private RoutingMap() {
        internalServers = new HashSet<>();
        externalServers = new HashSet<>();
        clients = new HashSet<>();
    }

    public HashSet<Node> getInternalServers() {
        return internalServers;
    }

    public void setInternalServers(HashSet<Node> internalServers) {
        this.internalServers = internalServers;
    }

    public HashSet<Node> getExternalServers() {
        return externalServers;
    }

    public void setExternalServers(HashSet<Node> externalServers) {
        this.externalServers = externalServers;
    }

    public HashSet<Node> getClients() {
        return clients;
    }

    public void setClients(HashSet<Node> clients) {
        this.clients = clients;
    }
}
