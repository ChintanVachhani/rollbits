package gash.router.server.communication.intercluster;

import gash.router.server.Node;
import gash.router.server.RoutingMap;
import io.netty.channel.Channel;
import routing.Pipe;

public class AddUserToGroupService {

    Pipe.Route route;
    Channel clientChannel;

    public AddUserToGroupService(Pipe.Route route, Channel clientChannel) {
        this.route = route;
        this.clientChannel = clientChannel;
        init();
    }

    public void init() {
        for (Node node : RoutingMap.getInstance().getExternalServers().values()) {
            AddUserToGroupClient addUserToGroupClient = new AddUserToGroupClient(node.getNodeAddress(), node.getNodePort(), route, this);
        }
    }

    public void sendToClient() {
        Pipe.Route.Builder responseRoute = Pipe.Route.newBuilder();
        responseRoute.setId(route.getId());
        responseRoute.setPath(Pipe.Route.Path.RESPONSE);
        Pipe.Response.Builder rb = Pipe.Response.newBuilder();
        rb.setMessage("User added to group.");
        responseRoute.setResponse(rb);
        clientChannel.writeAndFlush(responseRoute.build());
    }

}
