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
import gash.router.server.raft.Raft;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import routing.Pipe;
import routing.Pipe.Route;

import java.util.Objects;

/**
 * processes requests of message passing - demonstration
 *
 * @author gash
 */
public class HeartbeatResource implements RouteResource {
    protected static Logger logger = LoggerFactory.getLogger("heartbeat");

    public HeartbeatResource() {

    }

    @Override
    public String getPath() {
        return "/heartbeat";
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
        if (route.getHeartbeat().getMode() == Pipe.Heartbeat.Mode.PING) {
            Raft.getInstance().printRaftStatus("PING");
            if (Objects.equals(Raft.getInstance().getLeaderIP(), "")) {
                Raft.getInstance().setLeaderIP(route.getHeartbeat().getAddress());
                Raft.getInstance().printRaftStatus("New Leader");
                // construct the heartbeat ack to send
                Route.Builder rb = Route.newBuilder(route);
                rb.setHeartbeat(Pipe.Heartbeat.newBuilder().setMode(Pipe.Heartbeat.Mode.ACK).setAddress(conf.getNodeAddress()));
                return rb.build();
            } else if (!Objects.equals(Raft.getInstance().getLeaderIP(), "")) {
                Raft.getInstance().printRaftStatus("Old Leader");
                if (Objects.equals(Raft.getInstance().getLeaderIP(), route.getHeartbeat().getAddress())) {
                    Raft.getInstance().printRaftStatus("Same Leader");
                    //heartbeat is from leader/ all good, process heartbeat here
                    Raft.getInstance().setTimeOut(1000);

                    // construct the heartbeat ack to send
                    Route.Builder rb = Route.newBuilder(route);
                    rb.setHeartbeat(Pipe.Heartbeat.newBuilder().setMode(Pipe.Heartbeat.Mode.ACK).setAddress(conf.getNodeAddress()));
                    return rb.build();
                } else {
                    if (Objects.equals(conf.getNodeAddress(), Raft.getInstance().getLeaderIP())) {

                    }
                }
            }

            if (Raft.getInstance() != null) {

            } else {
                Raft.getInstance().setTimeOut(1000);
            }
        }
        Route.Builder rb = Route.newBuilder(route);
        rb.setHeartbeat(Pipe.Heartbeat.newBuilder().setMode(Pipe.Heartbeat.Mode.ACK).setAddress(conf.getNodeAddress()));
        return rb.build();
    }
}
