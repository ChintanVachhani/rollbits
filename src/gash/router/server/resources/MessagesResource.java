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
import gash.router.server.communication.PullMessagesService;
import gash.router.server.dao.MessageDAO;
import gash.router.server.dao.MorphiaService;
import gash.router.server.dao.UserDAO;
import gash.router.server.dao.impl.MessageDAOImpl;
import gash.router.server.dao.impl.UserDAOImpl;
import gash.router.server.entity.Message;
import gash.router.server.entity.User;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import routing.Pipe;
import routing.Pipe.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * processes requests of message passing - demonstration
 *
 * @author gash
 */
public class MessagesResource implements RouteResource {
    protected static Logger logger = LoggerFactory.getLogger("fetch");

    private MessageDAO messageDAO;
    private UserDAO userDAO;

    public MessagesResource() {
        MorphiaService morphiaService = new MorphiaService();
        this.messageDAO = new MessageDAOImpl(Message.class, morphiaService.getDatastore());
        this.userDAO = new UserDAOImpl(User.class, morphiaService.getDatastore());
    }

    @Override
    public String getPath() {
        return "/messages_request";
    }

    @Override
    public Route process(Route route) {
        return null;
    }

    @Override
    public Route process(Route route, Channel ctx) {
        Route.Builder responseRoute = Route.newBuilder();
        List<Message> response;

        response = fetch(route.getMessagesRequest().getId());
        System.out.println("->" + response);

        responseRoute.setId(route.getId());
        responseRoute.setPath(Route.Path.MESSAGES_RESPONSE);
        Pipe.MessagesResponse.Builder rb = Pipe.MessagesResponse.newBuilder();
        rb.setType(Pipe.MessagesResponse.Type.USER);
        rb.setId(route.getMessagesRequest().getId());
        for (Message aResponse : response) {
            Pipe.Message.Builder messageBuilder = Pipe.Message.newBuilder();
            if (aResponse.getType().equals("SINGLE"))
                messageBuilder.setType(Pipe.Message.Type.SINGLE);
            else if (aResponse.getType().equals("GROUP"))
                messageBuilder.setType(Pipe.Message.Type.GROUP);
            messageBuilder.setSenderId(aResponse.getFrom());
            messageBuilder.setReceiverId(aResponse.getTo());
            messageBuilder.setTimestamp(aResponse.getTimestamp());
            messageBuilder.setSenderId(aResponse.getFrom());
            messageBuilder.setPayload(aResponse.getPayload());
            messageBuilder.setAction(Pipe.Message.ActionType.POST);
            rb.addMessages(messageBuilder.build());
        }

        responseRoute.setMessagesResponse(rb);

        if (route.getHeader().getType().equals(Pipe.Header.Type.CLIENT)) {
            PullMessagesService pullMessagesService = new PullMessagesService(route, responseRoute.build(), ctx);
        }

        if (route.getHeader().getType().equals(Pipe.Header.Type.INTER_CLUSTER)) {
            return responseRoute.build();
        }
        return null;
    }

    @Override
    public Route process(Route route, RoutingConf conf) throws Exception {
        return null;
    }

    private List<Message> fetch(String username) {

        if (!username.equals("")) {
            User existingUser = userDAO.getUserByUsername(username);
            return messageDAO.getAllMessagesByUser(existingUser.getUsername(), existingUser.getGroupNames());
        }

        /*if (!username.equals("")) {
            if (messageDAO.getAllMessagesByUser(username, new ArrayList<>()) != null) {
                return messageDAO.getAllMessagesByUser(username, new ArrayList<>());
            }
        }*/
        return new ArrayList<Message>();
    }
}
