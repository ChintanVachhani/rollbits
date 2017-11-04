package gash.router.server;

import java.net.InetAddress;
import java.util.HashSet;

public class RoutingMap {
    public static HashSet<InetAddress> internalServers = new HashSet<InetAddress>();
    public static HashSet<InetAddress> externalServers = new HashSet<InetAddress>();
    public static HashSet<InetAddress> clients = new HashSet<InetAddress>();
}
