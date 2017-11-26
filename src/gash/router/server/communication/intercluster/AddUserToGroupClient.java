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
package gash.router.server.communication.intercluster;

import gash.router.client.CommConnection;
import gash.router.client.CommListener;
import gash.router.server.communication.ServerSideClient;
import routing.Pipe.Route;

public class AddUserToGroupClient implements CommListener {
    private ServerSideClient ssc;

    AddUserToGroupService addUserToGroupService;

    public AddUserToGroupClient(String host, int port, Route route, AddUserToGroupService addUserToGroupService) {
        this.addUserToGroupService = addUserToGroupService;
        ServerSideClient ssc = new ServerSideClient(host, port);
        init(ssc);
        addUserToGroup(route);
    }

    public AddUserToGroupClient(ServerSideClient ssc) {
        init(ssc);
    }

    private void init(ServerSideClient ssc) {
        this.ssc = ssc;
        this.ssc.addListener(this);
    }

    @Override
    public String getListenerID() {
        return "addUserToGroupClient";
    }

    @Override
    public void onMessage(Route msg) {
        System.out.println(msg);
        if(msg.getResponse().getSuccess()){
            addUserToGroupService.sendToClient();
        }
    }

    public void addUserToGroup(Route route) {
        try {
            ssc.addUserToGroup(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        ssc.release();
    }
}
