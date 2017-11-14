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

    UserResource() {
        MorphiaService morphiaService = new MorphiaService();
        this.userDAO = new UserDAOImpl(User.class, morphiaService.getDatastore());
    }

    @Override
    public String getPath() {
        return "/user";
    }

    @Override
    public String process(Route route) {
        if (route.hasUser()) {
            switch (route.getUser().getAction()) {
                case REGISTER:
                    return register(route.getUser());
                case ACCESS:
                    return access(route.getUser());
                case DELETE:
                    return delete(route.getUser());
            }
        }
        return null;
    }

    @Override
    public Route process(Route route, RoutingConf conf) throws Exception {
        return null;
    }

    private String register(Pipe.User user) {
        User newUser = new User(user.getEmail(), new Date().toString());
        userDAO.createUser(newUser);
        return null;
    }

    private String access(Pipe.User user) {

        return null;
    }

    private String delete(Pipe.User user) {

        return null;
    }
}
