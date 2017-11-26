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
package gash.router.server.communication.intracluster;

import gash.router.client.Client;
import gash.router.server.communication.ServerSideClient;
import gash.router.client.CommConnection;
import gash.router.client.CommListener;
import gash.router.server.raft.Raft;
import routing.Pipe;
import routing.Pipe.Route;

import java.util.Scanner;

public class HeartbeatClient implements CommListener {
    private ServerSideClient ssc;

    public HeartbeatClient(ServerSideClient ssc) {
        init(ssc);
    }

    private void init(ServerSideClient ssc) {
        this.ssc = ssc;
        this.ssc.addListener(this);
    }

    @Override
    public String getListenerID() {
        return "heartbeatClient";
    }

    @Override
    public void onMessage(Route msg) {
        System.out.println(msg);
        if (msg.getHeartbeat().getMode().equals(Pipe.Heartbeat.Mode.ACK)) {
            Raft.getInstance().setTimeOut(2000);
        }
    }

    public void ping(String leaderIp) {
        try {
            ssc.sendHeartbeatPing(leaderIp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ack(String followerIp) {
        try {
            ssc.sendHeartbeatAck(followerIp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        ssc.release();
    }
}
