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

    MessagesResource() {
        MorphiaService morphiaService = new MorphiaService();
        this.messageDAO = new MessageDAOImpl(Message.class, morphiaService.getDatastore());
        this.userDAO = new UserDAOImpl(User.class, morphiaService.getDatastore());
    }

    @Override
    public String getPath() {
        return "/messages";
    }

    @Override
    public Route process(Route route) {
        Route.Builder responseRoute = Route.newBuilder();
        List<Message> response = new ArrayList<>();

        if (route.getPath().equals(Route.Path.MESSAGES_REQUEST)) {
            response = fetch(route.getUser());
            responseRoute.setId(route.getId());
            responseRoute.setPath(Route.Path.MESSAGES_REQUEST);
            Pipe.MessagesResponse.Builder rb = Pipe.MessagesResponse.newBuilder();
            for (Message aResponse : response) {
                rb.addMessages(aResponse);
            }

            responseRoute.setResponse(rb);
        } else if (route.getPath().equals(Route.Path.MESSAGES_RESPONSE)) {
            route.getMessagesResponse().getMessagesList();

            Route.Builder responseRoute = Route.newBuilder();
            responseRoute.setId(route.getId());
            responseRoute.setPath(Route.Path.MESSAGES_RESPONSE);
            Pipe.MessagesResponse.Builder rb = Pipe.MessagesResponse.newBuilder();
            rb.addAllMessages(response);
            responseRoute.setResponse(rb);
        } else {

        }

        return responseRoute.build();
    }

    @Override
    public Route process(Route route, RoutingConf conf) throws Exception {
        return null;
    }

    private List<Message> fetch(Pipe.User user) {

        User existingUser = userDAO.getUser(user.getUname(), user.getPassword());

        return messageDAO.getAllMessagesByUser(existingUser.getUsername(), existingUser.getGroupIds());
    }
}
