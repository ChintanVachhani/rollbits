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
import gash.router.server.dao.MorphiaService;
import gash.router.server.dao.impl.UserDAOImpl;
import gash.router.server.entity.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import routing.Pipe;
import routing.Pipe.Route;

import java.util.Date;

import gash.router.server.dao.UserDAO;

/**
 * processes requests of message passing - demonstration
 *
 * @author gash
 */
public class UserResource implements RouteResource {
    protected static Logger logger = LoggerFactory.getLogger("user");

    private UserDAO userDAO;

    public UserResource() {
        MorphiaService morphiaService = new MorphiaService();
        this.userDAO = new UserDAOImpl(User.class, morphiaService.getDatastore());
    }

    @Override
    public String getPath() {
        return "/user";
    }

    @Override
    public Route process(Route route) {
        String response = null;

        if (route.hasUser()) {
            switch (route.getUser().getAction()) {
                case REGISTER:
                    response = register(route.getUser());
                    break;
                case ACCESS:
                    response = access(route.getUser());
                    break;
                case DELETE:
                    response = delete(route.getUser());
                    break;
                default:
                    response = "Invalid action.";
            }
        }

        Route.Builder responseRoute = Route.newBuilder();
        responseRoute.setId(route.getId());
        responseRoute.setPath(Route.Path.RESPONSE);
        Pipe.Response.Builder rb = Pipe.Response.newBuilder();

        rb.setMessage(response);
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

    private String register(Pipe.User user) {
        String username = user.getUname().toLowerCase();
        String password = user.getPassword();
        if (!user.hasPassword())
            password = "default";
        User newUser = new User(username, password);
        if (userDAO.getUserByUsername(newUser.getUsername()) == null) {
            userDAO.createUser(newUser);
            return "User registered.";
        }
        return "User already exists.";
    }

    private String access(Pipe.User user) {
        String username = user.getUname().toLowerCase();
        String password = user.getPassword();
        if (!user.hasPassword())
            password = "default";
        User existingUser = userDAO.getUser(username, password);
        if (existingUser != null) {
            return "User access granted.";
        }
        return "User access denied.";
    }

    private String delete(Pipe.User user) {
        String username = user.getUname().toLowerCase();
        String password = user.getPassword();
        if (!user.hasPassword())
            password = "default";
        if (userDAO.getUser(username, password) != null) {
            userDAO.deleteUser(username, password);
            return "User deleted.";
        }
        return "User not found.";
    }
}
