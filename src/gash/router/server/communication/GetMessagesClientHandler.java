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
import gash.router.server.RoutingMap;
import gash.router.server.resources.RouteResource;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import routing.Pipe;
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
public class GetMessagesClientHandler extends SimpleChannelInboundHandler<Route> {
    protected static Logger logger = LoggerFactory.getLogger("getMessagesHandler");

    public static int totalClusters = 0;
    public static Route collectedResponse;

    private Route request;
    private Route response;
    ChannelHandlerContext clientChannel;

    public GetMessagesClientHandler(Route request, Route response, ChannelHandlerContext clientChannel) {
        this.request = request;
        this.response = response;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(request);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Route msg) {
        if (totalClusters == 0) {
            Route.Builder r = Route.newBuilder(response);
            collectedResponse = r.build();
        }
        Pipe.MessagesResponse.Builder rb = Pipe.MessagesResponse.newBuilder(collectedResponse.getMessagesResponse());
        rb.addAllMessages(msg.getMessagesResponse().getMessagesList());
        Route.Builder r = Route.newBuilder(collectedResponse);
        r.setMessagesResponse(rb);
        collectedResponse = r.build();
        ++totalClusters;
        if (totalClusters >= RoutingMap.getInstance().getExternalServers().size()) {
            clientChannel.writeAndFlush(collectedResponse);
        }
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Unexpected exception from downstream.", cause);
        ctx.close();
    }

}