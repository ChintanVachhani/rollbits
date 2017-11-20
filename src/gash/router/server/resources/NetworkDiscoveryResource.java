/**
 * Copyright 2016 Gash.
 * <p>
 * This file and intellectual content is protected under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package gash.router.server.resources;

import gash.router.container.RoutingConf;
import gash.router.server.Node;
import gash.router.server.RoutingMap;
import gash.router.server.raft.Raft;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import routing.Pipe;
import routing.Pipe.NetworkDiscoveryPacket;
import routing.Pipe.Route;

import java.net.InetAddress;

/**
 * processes requests of networkDiscovery
 *
 * @author gash
 */
public class NetworkDiscoveryResource implements RouteResource {
    protected static Logger logger = LoggerFactory.getLogger("networkDiscovery");
    RoutingMap routingMap = RoutingMap.getInstance();

    @Override
    public String getPath() {
        return "/networkDiscovery";
    }

    @Override
    public Route process(Route route) {
        return null;
    }

    @Override
    public Route process(Route route, Channel ctx) {
        return null;
    }

    @Override
    public Route process(Route route, RoutingConf conf) throws Exception {
        if (route.hasNetworkDiscoveryPacket()) {
            if (route.getNetworkDiscoveryPacket().getSecret().equals(conf.getSecret())) {
                if (route.getNetworkDiscoveryPacket().getMode().equals(NetworkDiscoveryPacket.Mode.REQUEST))
                    return processRequest(route, conf);
                else if (route.getNetworkDiscoveryPacket().getMode().equals(NetworkDiscoveryPacket.Mode.RESPONSE))
                    return processResponse(route, conf);
                else if (route.getNetworkDiscoveryPacket().getMode().equals(NetworkDiscoveryPacket.Mode.REMOVE_NODE))
                    return processRemoval(route, conf);
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            throw new NoSuchFieldException();
        }
        return null;
    }

    private Route processRequest(Route request, RoutingConf conf) throws Exception {

        NetworkDiscoveryPacket requestNetworkDiscoveryPacket = request.getNetworkDiscoveryPacket();

        NetworkDiscoveryPacket.Builder ndpb = NetworkDiscoveryPacket.newBuilder();
        ndpb.setMode(NetworkDiscoveryPacket.Mode.RESPONSE);
        ndpb.setSender(requestNetworkDiscoveryPacket.getSender());
        ndpb.setGroupTag(conf.getGroupTag());
        ndpb.setNodeAddress(conf.getNodeAddress());
        ndpb.setNodeId(conf.getNodeId());

        if (requestNetworkDiscoveryPacket.getSender().equals(NetworkDiscoveryPacket.Sender.INTERNAL_SERVER_NODE))
            ndpb.setNodePort(conf.getInternalCommunicationPort());
        else if (requestNetworkDiscoveryPacket.getSender().equals(NetworkDiscoveryPacket.Sender.EXTERNAL_SERVER_NODE))
            ndpb.setNodePort(conf.getExternalCommunicationPort());
        else if (requestNetworkDiscoveryPacket.getSender().equals(NetworkDiscoveryPacket.Sender.END_USER_CLIENT))
            ndpb.setNodePort(conf.getExternalCommunicationPort());

        ndpb.setSecret(conf.getSecret());

        Route.Builder rb = Route.newBuilder(request);
        rb.setPath(Route.Path.NETWORK_DISCOVERY);
        rb.setNetworkDiscoveryPacket(ndpb);

        if (requestNetworkDiscoveryPacket.getSender().equals(NetworkDiscoveryPacket.Sender.INTERNAL_SERVER_NODE) && !requestNetworkDiscoveryPacket.getNodeAddress().equals(conf.getNodeAddress()) && requestNetworkDiscoveryPacket.getGroupTag().equals(conf.getGroupTag())) {
            Node node = new Node(requestNetworkDiscoveryPacket.getSender().toString(), requestNetworkDiscoveryPacket.getGroupTag(), requestNetworkDiscoveryPacket.getNodeId(), requestNetworkDiscoveryPacket.getNodeAddress(), (int) requestNetworkDiscoveryPacket.getNodePort());
            routingMap.addInternalServer(node);
        } else if (requestNetworkDiscoveryPacket.getSender().equals(NetworkDiscoveryPacket.Sender.EXTERNAL_SERVER_NODE)
                && !requestNetworkDiscoveryPacket.getNodeAddress().equals(conf.getNodeAddress())) {
            Node node = new Node(requestNetworkDiscoveryPacket.getSender().toString(), requestNetworkDiscoveryPacket.getGroupTag(), requestNetworkDiscoveryPacket.getNodeId(), requestNetworkDiscoveryPacket.getNodeAddress(), (int) requestNetworkDiscoveryPacket.getNodePort());
            routingMap.addExternalServer(node);
        } else if (requestNetworkDiscoveryPacket.getSender().equals(NetworkDiscoveryPacket.Sender.END_USER_CLIENT)) {
            Node node = new Node(requestNetworkDiscoveryPacket.getSender().toString(), requestNetworkDiscoveryPacket.getGroupTag(), requestNetworkDiscoveryPacket.getNodeId(), requestNetworkDiscoveryPacket.getNodeAddress(), (int) requestNetworkDiscoveryPacket.getNodePort());
            routingMap.addClient(node);
        }


        System.out.println("Internal Servers: " + routingMap.getInternalServers());
        System.out.println("External Servers: " + routingMap.getExternalServers().toString());
        System.out.println("Clients: " + routingMap.getClients().toString());

        return rb.build();
    }

    private Route processResponse(Route response, RoutingConf conf) throws Exception {

        NetworkDiscoveryPacket responseNetworkDiscoveryPacket = response.getNetworkDiscoveryPacket();

        if (responseNetworkDiscoveryPacket.getSender().equals(NetworkDiscoveryPacket.Sender.INTERNAL_SERVER_NODE) && !responseNetworkDiscoveryPacket.getNodeAddress().equals(conf.getNodeAddress()) && responseNetworkDiscoveryPacket.getGroupTag().equals(conf.getGroupTag())) {
            Node node = new Node(responseNetworkDiscoveryPacket.getSender().toString(), responseNetworkDiscoveryPacket.getGroupTag(), responseNetworkDiscoveryPacket.getNodeId(), responseNetworkDiscoveryPacket.getNodeAddress(), (int) responseNetworkDiscoveryPacket.getNodePort());
            routingMap.addInternalServer(node);
        } else if (responseNetworkDiscoveryPacket.getSender().equals(NetworkDiscoveryPacket.Sender.EXTERNAL_SERVER_NODE)
                && !responseNetworkDiscoveryPacket.getNodeAddress().equals(conf.getNodeAddress())) {
            Node node = new Node(responseNetworkDiscoveryPacket.getSender().toString(), responseNetworkDiscoveryPacket.getGroupTag(), responseNetworkDiscoveryPacket.getNodeId(), responseNetworkDiscoveryPacket.getNodeAddress(), (int) responseNetworkDiscoveryPacket.getNodePort());
            routingMap.addExternalServer(node);
        } else if (responseNetworkDiscoveryPacket.getSender().equals(NetworkDiscoveryPacket.Sender.END_USER_CLIENT)) {
            Node node = new Node(responseNetworkDiscoveryPacket.getSender().toString(), responseNetworkDiscoveryPacket.getGroupTag(), responseNetworkDiscoveryPacket.getNodeId(), responseNetworkDiscoveryPacket.getNodeAddress(), (int) responseNetworkDiscoveryPacket.getNodePort());
            routingMap.addClient(node);
        }


        System.out.println("Internal Servers: " + routingMap.getInternalServers());
        System.out.println("External Servers: " + routingMap.getExternalServers().toString());
        System.out.println("Clients: " + routingMap.getClients().toString());

        return null;
    }

    private Route processRemoval(Route route, RoutingConf conf) throws Exception {

        NetworkDiscoveryPacket routeNetworkDiscoveryPacket = route.getNetworkDiscoveryPacket();

        Node node = new Node(routeNetworkDiscoveryPacket.getSender().toString(), routeNetworkDiscoveryPacket.getGroupTag(), routeNetworkDiscoveryPacket.getNodeId(), routeNetworkDiscoveryPacket.getNodeAddress(), (int) routeNetworkDiscoveryPacket.getNodePort());

        if (routeNetworkDiscoveryPacket.getSender().equals(NetworkDiscoveryPacket.Sender.INTERNAL_SERVER_NODE) && !routeNetworkDiscoveryPacket.getNodeAddress().equals(conf.getNodeAddress()))
            routingMap.removeInternalServer(node);
        else if (routeNetworkDiscoveryPacket.getSender().equals(NetworkDiscoveryPacket.Sender.EXTERNAL_SERVER_NODE) && !routeNetworkDiscoveryPacket.getNodeAddress().equals(conf.getNodeAddress()))
            routingMap.removeExternalServer(node);
        else if (routeNetworkDiscoveryPacket.getSender().equals(NetworkDiscoveryPacket.Sender.END_USER_CLIENT))
            routingMap.removeClient(node);

        System.out.println("Internal Servers: " + routingMap.getInternalServers());
        System.out.println("External Servers: " + routingMap.getExternalServers());
        System.out.println("Clients: " + routingMap.getClients());

        return null;
    }

}
