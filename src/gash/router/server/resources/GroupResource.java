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
import gash.router.server.dao.MorphiaService;
import gash.router.server.dao.UserDAO;
import gash.router.server.dao.impl.GroupDAOImpl;
import gash.router.server.dao.impl.UserDAOImpl;
import gash.router.server.entity.Group;
import gash.router.server.entity.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import routing.Pipe;
import routing.Pipe.Route;

/**
 * processes requests of message passing - demonstration
 *
 * @author gash
 */
public class GroupResource implements RouteResource {
    protected static Logger logger = LoggerFactory.getLogger("group");

    private GroupDAO groupDAO;
    private UserDAO userDAO;

    public GroupResource() {
        MorphiaService morphiaService = new MorphiaService();
        this.groupDAO = new GroupDAOImpl(Group.class, morphiaService.getDatastore());
        this.userDAO = new UserDAOImpl(User.class, morphiaService.getDatastore());
    }

    @Override
    public String getPath() {
        return "/group";
    }

    @Override
    public Route process(Route route) {
        String response = null;

        if (route.hasGroup()) {
            switch (route.getGroup().getAction()) {
                case CREATE:
                    response = create(route.getGroup());
                    break;
                case DELETE:
                    response = delete(route.getGroup());
                    break;
                case ADD_USER:
                    response = addUser(route.getGroup(), route.getUser());
                    break;
                case REMOVE_USER:
                    response = removeUser(route.getGroup(), route.getUser());
                    break;
                default:
                    response = "Invalid Action.";
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

    private String create(Pipe.Group group) {
        Group newGroup = new Group(group.getGname(), group.getGid());
        groupDAO.createGroup(newGroup);
        if (groupDAO.getGroupByName(newGroup.getName()) == null) {
            groupDAO.createGroup(newGroup);
            return "Group Created.";
        }
        return "Group already exists.";
    }

    private String delete(Pipe.Group group) {
        if (groupDAO.getGroupByName(group.getGname()) != null) {
            groupDAO.deleteGroupByName(group.getGname());
            return "Group Deleted.";
        }
        return "Group not found.";
    }

    private String addUser(Pipe.Group group, Pipe.User user) {
        userDAO.addGroupToUser(group.getGid(), user.getUname());
        return "User added to group.";
    }

    private String removeUser(Pipe.Group group, Pipe.User user) {
        userDAO.removeGroupFromUser(group.getGid(), user.getUname());
        return "User removed from group.";
    }
}
