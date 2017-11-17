package gash.router.server.raft;

import gash.router.container.RoutingConf;
import gash.router.server.Node;
import gash.router.server.RoutingMap;
import gash.router.server.resources.RouteResource;
import io.netty.channel.Channel;
import org.slf4j.LoggerFactory;
import routing.Pipe;
import sun.rmi.runtime.Log;

import java.beans.Beans;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Raft {
    private static Raft ourInstance = new Raft();
    protected static org.slf4j.Logger logger = LoggerFactory.getLogger("Raft");
    public static Raft getInstance() {
        return ourInstance;
    }

    private HashMap<String, String> routing;
    private RoutingConf conf;

    private String myIP;
    private Integer currentHeartBeatID = 0;
    public void setConf(RoutingConf conf) {
        this.conf = conf;
    }

    private class HeartBeat {
        String IP;
        private ArrayList<Pipe.Route> messageBuffer;
        public HeartBeat(String pramMyIP, ArrayList<Pipe.Route> pramMessageBuffer){
            IP = pramMyIP;
            messageBuffer = pramMessageBuffer;
        }
    }

    private Raft() {
        while (conf == null){
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        myIP = conf.getNodeAddress();
    }

    private String getLeaderIP(){
        Integer max = Integer.MIN_VALUE;
        String maxIP = "";
        for (Node node : RoutingMap.getInstance().getInternalServers().values()){
            String ip = node.getNodeAddress();
            Integer lastNumber = Integer.parseInt(ip.substring(ip.lastIndexOf('.'), ip.length()-1));
            if (max < lastNumber){
                maxIP = node.getNodeAddress();
            }
        }
        return maxIP;
    }

    public boolean isLeader(){
        if(Objects.equals(conf.getNodeAddress(), getLeaderIP())){
            return true;
        }
        else return false;
    }

    public void respondHeartBeat(HeartBeat heartBeat){
        if (isLeader()){return;}

        if (heartBeat.IP != null){
            if (RoutingMap.getInstance().getInternalServers().containsKey(heartBeat.IP)){
                //Heartbeat is from known server list, process it here
                //TODO: process Heartbeat here.

            }
            else {
                //IP exists but is not in internal servers network, add it to map
                System.out.println("Heartbeat received from address not in internal server list!: "+heartBeat.IP+" adding to internal server list now..");
                //TODO: RoutingMap.getInstance().getInternalServers().add(heartBeat.IP);
                //TODO: process heartbeat here

            }

        }
        else {
            //inetaddress is null throw error
            System.out.println("Error! heartbeat.IP is null");
        }
    }

    public void sendHeartBeat(){
        currentHeartBeatID++;
        for(Node node : RoutingMap.getInstance().getInternalServers().values()){
            if (!Objects.equals(node.getNodeAddress(), myIP)){
                //TODO: write code to send heartbeat via TCP

            }
        }
    }

    public void handleMessage(Pipe.Route msg) {
        if (msg == null) {
            // TODO add logging
            System.out.println("ERROR: Unexpected content - " + msg);
            return;
        }

        System.out.println("---> " + msg.getId() + ": " + msg.getPath());

        try {
            System.out.println("/" + msg.getPath().toString().toLowerCase());
            String clazz = routing.get("/" + msg.getPath().toString().toLowerCase());
            if (clazz != null) {
                RouteResource rsc = (RouteResource) Beans.instantiate(RouteResource.class.getClassLoader(), clazz);
            } else {
                // TODO add logging
                System.out.println("ERROR: unknown path - " + msg.getPath());
            }
        } catch (Exception ex) {
            // TODO add logging
            System.out.println("ERROR: processing request - " + ex.getMessage());
        }

        System.out.flush();
    }


    public LogReplication getLogReplication() {
        return LogReplication.getInstance();
    }

    class TCPServer {
        public void main(String argv[]) throws Exception {



        }
    }

}
