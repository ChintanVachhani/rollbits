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
import gash.router.server.dao.GroupDAO;
import gash.router.server.dao.MessageDAO;
import gash.router.server.dao.MorphiaService;
import gash.router.server.dao.UserDAO;
import gash.router.server.dao.impl.GroupDAOImpl;
import gash.router.server.dao.impl.MessageDAOImpl;
import gash.router.server.dao.impl.UserDAOImpl;
import gash.router.server.entity.Group;
import gash.router.server.entity.Message;
import gash.router.server.entity.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import routing.Pipe;
import routing.Pipe.Route;

import java.util.Date;

/**
 * processes requests of message passing - demonstration
 *
 * @author gash
 */
public class MessageResource implements RouteResource {
    protected static Logger logger = LoggerFactory.getLogger("message");

    private MessageDAO messageDAO;
    private UserDAO userDAO;
    private GroupDAO groupDAO;

    public MessageResource() {
        MorphiaService morphiaService = new MorphiaService();
        this.messageDAO = new MessageDAOImpl(Message.class, morphiaService.getDatastore());
        this.userDAO = new UserDAOImpl(User.class, morphiaService.getDatastore());
        this.groupDAO = new GroupDAOImpl(Group.class, morphiaService.getDatastore());
    }

    @Override
    public String getPath() {
        return "/message";
    }

    @Override
    public Route process(Route route) {

        String response = null;

        if (route.hasMessage()) {
            switch (route.getMessage().getAction()) {
                case POST:
                    response = post(route.getMessage());
            }
        }

        if (route.getHeader().getType().equals(Pipe.Header.Type.INTERNAL)) {
            return null;
        }

        Route.Builder responseRoute = Route.newBuilder();
        responseRoute.setId(route.getId());
        responseRoute.setPath(Route.Path.RESPONSE);
        Pipe.Response.Builder rb = Pipe.Response.newBuilder();
        rb.setMessage(response);
        rb.setSuccess(true);
        responseRoute.setResponse(rb);

        return responseRoute.build();
    }

    @Override
    public Route process(Route route, Channel ctx) {
        return null;
    }

    @Override
    public Route process(Route route, RoutingConf conf) throws Exception {
        return null;
    }

    private String post(Pipe.Message message) {
        if (!message.getSenderId().equals("")) {
            if (message.getType().equals(Pipe.Message.Type.SINGLE)) {
                Message newMessage = new Message(message.getType().toString(), message.getSenderId().toLowerCase(), message.getPayload(), message.getReceiverId().toLowerCase(), new Date().toString(), false);
                messageDAO.postMessage(newMessage);
                return "Message posted.";
            } else if (message.getType().equals(Pipe.Message.Type.GROUP)) {
                if(groupDAO.getGroupByName(message.getReceiverId().toLowerCase()) != null) {
                    if (userDAO.getUserByUsername(message.getSenderId().toLowerCase()).getGroupNames().contains(message.getReceiverId().toLowerCase())) {
                        Message newMessage = new Message(message.getType().toString(), message.getSenderId().toLowerCase(), message.getPayload(), message.getReceiverId().toLowerCase(), new Date().toString(), false);
                        messageDAO.postMessage(newMessage);
                        return "Message posted.";
                    }
                    return "User not in the group.";
                } else {
                    //TODO: Forward to others
                    return "Will be forwarded to others.";
                }
            }
            return "Receiver not found.";
        }
        return "Access the user first.";
    }
}
