package gash.router.server.raft;

import gash.router.container.RoutingConf;
import gash.router.server.Node;
import gash.router.server.RoutingMap;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.*;

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
    private boolean iAmLeader;
    private Raft() {
        myIP = conf.getNodeAddress();
        iAmLeader = false;
    }

    public void setConf(RoutingConf conf) {
        this.conf = conf;
    }

    private String getLeaderIP() {
        Integer max = Integer.MIN_VALUE;
        String maxIP = "";
        for (Node node : RoutingMap.getInstance().getInternalServers().values()) {
            String ip = node.getNodeAddress();
            Integer lastNumber = Integer.parseInt(ip.substring(ip.lastIndexOf('.'), ip.length() - 1));
            if (max < lastNumber) {
                maxIP = node.getNodeAddress();
            }
        }
        return maxIP;
    }

    public boolean isLeader() {
        if (Objects.equals(conf.getNodeAddress(), getLeaderIP())) {
            return true;
        } else return false;
    }

    public void sendHeartBeat() {
        for (Node node : RoutingMap.getInstance().getInternalServers().values()) {
            if (!Objects.equals(node.getNodeAddress(), myIP)) {
                //TODO: write code to send heartbeat via TCP

            }
        }
    }

    public void election(){
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        final Future handler = executor.submit((Callable) () -> {
            sleep(1000);
            for(Node node: RoutingMap.getInstance().getInternalServers().values()){
                if (compareIP(conf.getNodeAddress(), node.getNodeAddress()) == -1 ){

                }
            }
            return null;
        });
        executor.schedule(new Runnable(){
            public void run(){
                handler.cancel(true);
            }
        }, 10000, TimeUnit.MILLISECONDS);
    }

    public Integer compareIP(String ip1S,String ip2S){
        Integer ip1 = Integer.parseInt(ip1S.substring(ip1S.lastIndexOf('.'), ip1S.length() -1 ));
        Integer ip2 = Integer.parseInt(ip2S.substring(ip2S.lastIndexOf('.'), ip2S.length() -1 ));
        if(ip1 > ip2){
            return 1;
        }
        else if (ip1 == ip2){
            return 0;
        }
        else return -1;
    }


    public boolean isiAmLeader() {
        return iAmLeader;
    }
}