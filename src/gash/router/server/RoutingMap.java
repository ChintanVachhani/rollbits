package gash.router.server;

import java.util.HashSet;

public class RoutingMap {
    private static RoutingMap ourInstance = new RoutingMap();

    public static RoutingMap getInstance() {
        return ourInstance;
    }

    private static HashSet<String> internalServers;
    private static HashSet<String> externalServers;
    private static HashSet<String> clients;

    private RoutingMap() {
        internalServers = new HashSet<>();
        externalServers = new HashSet<>();
        clients = new HashSet<>();
    }

    public static HashSet<String> getInternalServers() {
        return internalServers;
    }

    public static void setInternalServers(HashSet<String> internalServers) {
        RoutingMap.internalServers = internalServers;
    }

    public static HashSet<String> getExternalServers() {
        return externalServers;
    }

    public static void setExternalServers(HashSet<String> externalServers) {
        RoutingMap.externalServers = externalServers;
    }

    public static HashSet<String> getClients() {
        return clients;
    }

    public static void setClients(HashSet<String> clients) {
        RoutingMap.clients = clients;
    }
}
