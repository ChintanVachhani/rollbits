package gash.router.server.raft;

import gash.router.container.RoutingConf;
import gash.router.server.Node;
import gash.router.server.RoutingMap;
import gash.router.server.communication.ExternalCommServer;
import gash.router.server.communication.SendHeartbeat;
import gash.router.server.discovery.ExternalDiscoveryClient;
import gash.router.server.discovery.ExternalDiscoveryServer;
import gash.router.server.discovery.InternalDiscoveryClient;
import gash.router.server.discovery.InternalDiscoveryServer;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class Raft {
    protected static org.slf4j.Logger logger = LoggerFactory.getLogger("Raft");

    private static Raft ourInstance = new Raft();

    public static Raft getInstance() {
        return ourInstance;
    }

    private HashMap<String, String> routing;
    private RoutingConf conf;
    private String myIP;
    private Integer timeOut;
    private String leaderIP;

    private Raft() {

    }


    public void setConf(RoutingConf conf) {
        this.conf = conf;
        myIP = conf.getNodeAddress();
        timeOut = 5000;
        leaderIP = "";
        printRaftStatus("Starting Heartbeat thread...");
        TimeOut timeOut = new TimeOut();
        Thread thread = new Thread(timeOut);
        thread.start();
    }

    public RoutingConf getConf() {
        return conf;
    }

    public String getLeaderIP() {
        return leaderIP;
    }

    public void setLeaderIP(String leaderIP) {
        this.leaderIP = leaderIP;
    }

    public void election() {
        printRaftStatus("Starting election...");
        try {
            // finding all the active servers
            /*RoutingMap.getInstance().getInternalServers().clear();
            InternalDiscoveryClient internalDiscoveryClient = new InternalDiscoveryClient(conf);
            Thread dcthread = new Thread(internalDiscoveryClient);
            dcthread.start();*/
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Node node : RoutingMap.getInstance().getInternalServers().values()) {
            if (compareIP(conf.getNodeAddress(), node.getNodeAddress()) == 1 || compareIP(conf.getNodeAddress(), node.getNodeAddress()) == 0) {
            } else {
                try {
                    printRaftStatus("This IP is Higher than myIP, sleeping for 1000...");
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (Objects.equals(leaderIP, "")) {
            printRaftStatus("I am now leader...");

            // External network discovery
            // listening for discovery request by other servers
            ExternalDiscoveryServer externalDiscoveryServer = new ExternalDiscoveryServer(conf);
            Thread dsthread = new Thread(externalDiscoveryServer);
            dsthread.start();

            // finding all the active servers
            ExternalDiscoveryClient externalDiscoveryClient = new ExternalDiscoveryClient(conf);
            Thread dcthread = new Thread(externalDiscoveryClient);
            dcthread.start();

            leaderIP = conf.getNodeAddress();
            startHeartBeat();
        }
    }

    public Integer compareIP(String ip1S, String ip2S) {
        Integer ip1 = Integer.parseInt(ip1S.substring(ip1S.lastIndexOf('.') + 1, ip1S.length() - 1));
        Integer ip2 = Integer.parseInt(ip2S.substring(ip2S.lastIndexOf('.') + 1, ip2S.length() - 1));
        if (ip1 > ip2) {
            return 1;
        } else if (Objects.equals(ip1, ip2)) {
            return 0;
        } else return -1;
    }


    public void startHeartBeat() {
        printRaftStatus("Starting Heartbeat....");
        while (Objects.equals(leaderIP, conf.getNodeAddress())) {
            if (Objects.equals(conf.getNodeAddress(), leaderIP)) {
                for (Node node : RoutingMap.getInstance().getInternalServers().values()) {
                    //TODO: send heartbeat for each node in this list
                    printRaftStatus("Sending heartbeat...");
                    node.getSendHeartbeat().ping(leaderIP);
                }
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public void printRaftStatus(String message) {

        System.out.println(message);
        System.out.println("TimeOut: " + timeOut);
        System.out.println("LeaderIP: " + leaderIP);
        System.out.println("My IP: " + myIP);
    }

}
