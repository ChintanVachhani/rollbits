package gash.router.server;

import java.util.HashSet;

public class RoutingMap {
    private static RoutingMap ourInstance = new RoutingMap();

    public static RoutingMap getInstance() {
        return ourInstance;
    }

    private HashSet<String> internalServers;
    private HashSet<String> externalServers;
    private HashSet<String> clients;

    private RoutingMap() {
        internalServers = new HashSet<>();
        externalServers = new HashSet<>();
        clients = new HashSet<>();
    }

    public HashSet<String> getInternalServers() {
        return internalServers;
    }

    public void setInternalServers(HashSet<String> internalServers) {
        this.internalServers = internalServers;
    }

    public HashSet<String> getExternalServers() {
        return externalServers;
    }

    public void setExternalServers(HashSet<String> externalServers) {
        this.externalServers = externalServers;
    }

    public HashSet<String> getClients() {
        return clients;
    }

    public void setClients(HashSet<String> clients) {
        this.clients = clients;
    }
}
