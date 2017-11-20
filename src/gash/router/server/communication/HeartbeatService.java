package gash.router.server.communication;

import gash.router.server.Node;
import gash.router.server.RoutingMap;
import gash.router.server.raft.Raft;
import io.netty.channel.Channel;
import routing.Pipe;

public class HeartbeatService {

    Pipe.Route route;
    Channel clientChannel;

    public HeartbeatService(Pipe.Route route, Pipe.Route response, Channel clientChannel) {
        this.route = route;
        this.clientChannel = clientChannel;
    }

    public void init() {
        for (Node node : RoutingMap.getInstance().getInternalServers().values()) {
            HeartbeatClient heartbeatClient = new HeartbeatClient(node.getNodeAddress(), node.getNodePort(), Raft.getInstance().getLeaderIP());
        }
    }

}
