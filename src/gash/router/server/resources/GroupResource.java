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
import gash.router.server.dao.impl.GroupDAOImpl;
import gash.router.server.entity.Group;
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

    GroupResource() {
        MorphiaService morphiaService = new MorphiaService();
        this.groupDAO = new GroupDAOImpl(Group.class, morphiaService.getDatastore());
    }

    @Override
    public String getPath() {
        return "/group";
    }

    @Override
    public String process(Route route) {
        if (route.hasGroup()) {
            switch (route.getGroup().getAction()) {
                case CREATE:
                    return create(route.getGroup());
                case DELETE:
                    return delete(route.getGroup());
            }
        }
        return null;
    }

    @Override
    public Route process(Route route, RoutingConf conf) throws Exception {
        return null;
    }

    private String create(Pipe.Group group) {
        Group newGroup = new Group(group.getGname(), group.getGid());
        groupDAO.createGroup(newGroup);
        return null;
    }

    private String delete(Pipe.Group group) {
        groupDAO.deleteGroupByName(group.getGname());
        return null;
    }
}
