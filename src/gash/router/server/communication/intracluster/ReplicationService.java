package gash.router.server.communication.intracluster;

import gash.router.server.Node;
import gash.router.server.RoutingMap;
import io.netty.channel.Channel;
import routing.Pipe;

public class ReplicationService {

    Pipe.Route route;
    Channel clientChannel;

    public ReplicationService(Pipe.Route route, Channel clientChannel) {
        this.route = route;
        this.clientChannel = clientChannel;
        init();
    }

    public void init() {
        for (Node node : RoutingMap.getInstance().getInternalServers().values()) {
            ReplicationClient replicationClient = new ReplicationClient(node.getNodeAddress(), node.getNodePort(), route,this);
        }
    }
}
