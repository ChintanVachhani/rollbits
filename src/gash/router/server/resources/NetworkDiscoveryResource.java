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
import gash.router.server.RoutingMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Override
    public String getPath() {
        return "/networkDiscovery";
    }

    @Override
    public String process(Route route) {
        return null;
    }

    public Route process(Route route, RoutingConf conf) throws Exception {
        if (route.hasNetworkDiscoveryPacket()) {
            if (route.getNetworkDiscoveryPacket().getSecret().equals(conf.getSecret())) {
                if (route.getNetworkDiscoveryPacket().getMode().equals(NetworkDiscoveryPacket.Mode.REQUEST))
                    return processRequest(route, conf);
                else if (route.getNetworkDiscoveryPacket().getMode().equals(NetworkDiscoveryPacket.Mode.RESPONSE))
                    return processResponse(route, conf);
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            throw new NoSuchFieldException();
        }
        return null;
    }

    private Route processRequest(Route request, RoutingConf conf) throws Exception {

        NetworkDiscoveryPacket.Builder ndpb = NetworkDiscoveryPacket.newBuilder();
        ndpb.setMode(NetworkDiscoveryPacket.Mode.RESPONSE);
        ndpb.setSender(request.getNetworkDiscoveryPacket().getSender());
        ndpb.setGroupTag(conf.getGroupTag());
        ndpb.setNodeAddress(InetAddress.getLocalHost().getHostAddress());

        if (request.getNetworkDiscoveryPacket().getSender().equals(NetworkDiscoveryPacket.Sender.INTERNAL_SERVER_NODE))
            ndpb.setNodePort(conf.getInternalDiscoveryPort());
        else if (request.getNetworkDiscoveryPacket().getSender().equals(NetworkDiscoveryPacket.Sender.EXTERNAL_SERVER_NODE))
            ndpb.setNodePort(conf.getExternalDiscoveryPort());

        ndpb.setSecret(conf.getSecret());

        Route.Builder rb = Route.newBuilder(request);
        rb.setPath(Route.Path.NETWORK_DISCOVERY);
        rb.setNetworkDiscoveryPacket(ndpb);

        if (request.getNetworkDiscoveryPacket().getSender().equals(NetworkDiscoveryPacket.Sender.INTERNAL_SERVER_NODE) && !request.getNetworkDiscoveryPacket().getNodeAddress().equals(InetAddress.getLocalHost().getHostAddress()))
            RoutingMap.internalServers.add(request.getNetworkDiscoveryPacket().getNodeAddress());
        else if (request.getNetworkDiscoveryPacket().getSender().equals(NetworkDiscoveryPacket.Sender.EXTERNAL_SERVER_NODE) && !request.getNetworkDiscoveryPacket().getNodeAddress().equals(InetAddress.getLocalHost().getHostAddress()))
            RoutingMap.externalServers.add(request.getNetworkDiscoveryPacket().getNodeAddress());

        System.out.println(RoutingMap.internalServers);

        return rb.build();
    }

    private Route processResponse(Route response, RoutingConf conf) throws Exception {
        if (response.getNetworkDiscoveryPacket().getSender().equals(NetworkDiscoveryPacket.Sender.INTERNAL_SERVER_NODE) && !response.getNetworkDiscoveryPacket().getNodeAddress().equals(InetAddress.getLocalHost().getHostAddress()))
            RoutingMap.internalServers.add(response.getNetworkDiscoveryPacket().getNodeAddress());
        else if (response.getNetworkDiscoveryPacket().getSender().equals(NetworkDiscoveryPacket.Sender.EXTERNAL_SERVER_NODE) && !response.getNetworkDiscoveryPacket().getNodeAddress().equals(InetAddress.getLocalHost().getHostAddress()))
            RoutingMap.externalServers.add(response.getNetworkDiscoveryPacket().getNodeAddress());

        System.out.println(RoutingMap.internalServers);

        return null;
    }

}
