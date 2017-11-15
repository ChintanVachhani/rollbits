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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import routing.Pipe;

/**
 * responds to request for pinging the service
 *
 * @author gash
 *
 */
public class PingResource implements RouteResource {
    protected static Logger logger = LoggerFactory.getLogger("ping");

    @Override
    public String getPath() {
        return "/ping";
    }

    @Override
    public Pipe.Route process(Pipe.Route route) {
        Pipe.Route.Builder rb = Pipe.Route.newBuilder();
        rb.setId(route.getId());
        rb.setPath(Pipe.Route.Path.PING);
        rb.setPayload("ping");
        return rb.build();
    }

    @Override
    public Pipe.Route process(Pipe.Route route, RoutingConf conf) throws Exception {
        return null;
    }


}
