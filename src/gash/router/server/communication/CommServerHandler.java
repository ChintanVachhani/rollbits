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
package gash.router.server.communication;

import gash.router.container.RoutingConf;
import gash.router.server.resources.RouteResource;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import routing.Pipe.Route;

import java.beans.Beans;
import java.util.HashMap;

/**
 * The message handler processes json messages that are delimited by a 'newline'
 * <p>
 * TODO replace println with logging!
 *
 * @author gash
 */
public class CommServerHandler extends SimpleChannelInboundHandler<Route> {
    protected static Logger logger = LoggerFactory.getLogger("connect");

    private HashMap<String, String> routing;
    private RoutingConf conf;

    public CommServerHandler(RoutingConf conf) {
        this.conf = conf;
        if (conf != null)
            routing = conf.asHashMap();
    }

    /**
     * override this method to provide processing behavior. This implementation
     * mimics the routing we see in annotating classes to support a RESTful-like
     * behavior (e.g., jax-rs).
     *
     * @param msg
     */
    public void handleMessage(Route msg, Channel channel) {
        if (msg == null) {
            // TODO add logging
            System.out.println("ERROR: Unexpected content - " + msg);
            return;
        }

        System.out.println("---> request: \n" + msg);

        try {
            String clazz = routing.get("/" + msg.getPath().toString().toLowerCase());
            if (clazz != null) {
                RouteResource rsc = (RouteResource) Beans.instantiate(RouteResource.class.getClassLoader(), clazz);
                Route response;
                try {
                    switch (msg.getPath()) {
                        case HEARTBEAT:
                            response = rsc.process(msg, conf);
                            break;
                        case MESSAGES_REQUEST:
                            response = rsc.process(msg, channel);
                            break;
                        case GROUP:
                            response = rsc.process(msg, channel);
                            break;
                        case USER:
                            response = rsc.process(msg, channel);
                            break;
                        case MESSAGE:
                            response = rsc.process(msg, channel);
                            break;
                        default:
                            response = rsc.process(msg);
                            break;
                    }
                    System.out.println("<--- reply: \n" + response);
                    if (response != null) {
                        channel.writeAndFlush(response);
                    }
                } catch (Exception e) {
                    logger.error("Failed to read route.", e);
                }
            } else {
                // TODO add logging
                System.out.println("ERROR: unknown path - " + msg.getPath());
            }
        } catch (Exception ex) {
            // TODO add logging
            System.out.println("ERROR: processing request - " + ex.getMessage());
        }

        //System.out.flush();
    }

    /**
     * a message was received from the server. Here we dispatch the message to
     * the client's thread pool to minimize the time it takes to process other
     * messages.
     *
     * @param ctx The channel the message was received from
     * @param msg The message
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Route msg) throws Exception {
        //System.out.println("------------");
        handleMessage(msg, ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Unexpected exception from downstream.", cause);
        ctx.close();
    }

}