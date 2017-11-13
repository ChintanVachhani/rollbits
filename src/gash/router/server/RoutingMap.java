package gash.router.server;

import java.net.InetAddress;
import java.util.HashSet;

public class RoutingMap {
    public static HashSet<String> internalServers = new HashSet<String>();
    public static HashSet<String> externalServers = new HashSet<>();
    public static HashSet<String> clients = new HashSet<>();
}
