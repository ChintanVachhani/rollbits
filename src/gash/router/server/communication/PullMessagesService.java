package gash.router.server.communication;

import gash.router.server.Node;
import gash.router.server.RoutingMap;
import io.netty.channel.Channel;
import routing.Pipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PullMessagesService {

    Pipe.Route route;
    Channel clientChannel;

    Pipe.Route.Builder collatedResponse;

    int responseCount;

    public PullMessagesService(Pipe.Route route, Pipe.Route response, Channel clientChannel) {
        this.route = route;
        this.clientChannel = clientChannel;
        collatedResponse = Pipe.Route.newBuilder(response);
    }

    public void init() {
        for (Node node : RoutingMap.getInstance().getExternalServers().values()) {
            PullMessagesClient pullMessagesClient = new PullMessagesClient(node.getNodeAddress(), node.getNodePort(), route, this);
        }
    }

    public void collateResponse(Pipe.Route response) {
        Pipe.MessagesResponse.Builder mrb = Pipe.MessagesResponse.newBuilder(collatedResponse.getMessagesResponse());
        mrb.addAllMessages(response.getMessagesResponse().getMessagesList());
        collatedResponse.setMessagesResponse(mrb.build());
        if (responseCount == RoutingMap.getInstance().getExternalServers().size()){
            sendToClient();
        }
    }

    public void sendToClient() {
        clientChannel.writeAndFlush(collatedResponse.build());
    }

}
