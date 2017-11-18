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
import gash.router.server.raft.Raft;
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

    HeartbeatResource() {

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
    public Route process(Route route, RoutingConf conf) throws Exception {
        if (route.getHeartbeat().getMode() == Pipe.Heartbeat.Mode.PING) {
            if (Objects.equals(Raft.getInstance().getLeaderIP(), "")) {
                Raft.getInstance().setLeaderIP(route.getHeartbeat().getAddress());
            } else if (!Objects.equals(Raft.getInstance().getLeaderIP(), "")) {
                if (Objects.equals(Raft.getInstance().getLeaderIP(), route.getHeartbeat().getAddress())) {
                    //TODO: heartbeat is from leader/ all good, process heartbeat here
                } else {
                    if (Objects.equals(conf.getNodeAddress(), Raft.getInstance().getLeaderIP())) {

                    }
                }
            }

            if (Raft.getInstance() != null) {

            } else {
                Raft.getInstance().setTimeOut(1000);
            }
            return null;
        }else if (route.getHeartbeat().getMode() == Pipe.Heartbeat.Mode.ACK){
            Raft.getInstance().setTimeOut(2000);
        }
        return null;
    }
}
