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

public class PostMessageToGroupClient implements CommListener {
    private ServerSideClient ssc;
    String host;
    int port;

    PostMessageToGroupService postMessageToGroupService;

    public PostMessageToGroupClient(String host, int port, Route route, PostMessageToGroupService postMessageToGroupService) {
        this.host = host;
        this.port = port;
        ServerSideClient ssc = new ServerSideClient(host, port);
        init(ssc);
        postMessageToGroup(route);
    }

    public PostMessageToGroupClient(ServerSideClient ssc) {
        init(ssc);
    }

    private void init(ServerSideClient ssc) {
        this.ssc = ssc;
        this.ssc.addListener(this);
    }

    @Override
    public String getListenerID() {
        return "postMessageToGroupClient";
    }

    @Override
    public void onMessage(Route msg) {
        System.out.println(msg);
        if (msg.getResponse().getSuccess()) {
            postMessageToGroupService.sendToClient();
        }
    }

    public void postMessageToGroup(Route route) {
        try {
            ssc.postMessageToGroup(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        ssc.release();
    }
}
