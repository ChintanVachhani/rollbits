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
package gash.router.server.communication;

import gash.router.server.communication.ServerSideClient;
import gash.router.client.CommConnection;
import gash.router.client.CommListener;
import routing.Pipe;
import routing.Pipe.Route;

import java.util.Scanner;

public class SendHeartbeat implements CommListener {
    private ServerSideClient ssc;

    public SendHeartbeat(ServerSideClient ssc) {
        init(ssc);
    }

    private void init(ServerSideClient ssc) {
        this.ssc = ssc;
        this.ssc.addListener(this);
    }


    public void sendHeartbeat(String leaderIp){
        ssc.sendHeartbeat(leaderIp);
    }

    @Override
    public String getListenerID() {
        return "sendHeartbeat";
    }

    @Override
    public void onMessage(Route msg) {
        System.out.println(msg);
    }

    public static void run(String host, int port, String leaderIp) {

        try {
            ServerSideClient ssc = new ServerSideClient(host, port);
            SendHeartbeat sh = new SendHeartbeat(ssc);
            sh.sendHeartbeat(leaderIp);
			/*System.out.println("\n** exiting in 60 seconds. **");
            System.out.flush();
			Thread.sleep(60 * 1000);*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CommConnection.getInstance().release();
        }
    }
}
