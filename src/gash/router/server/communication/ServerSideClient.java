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

import gash.router.client.CommConnection;
import gash.router.client.CommListener;
import routing.Pipe;
import routing.Pipe.Route;

/**
 * front-end (proxy) to our service - functional-based
 *
 * @author gash
 */
public class ServerSideClient {
    // track requests
    private long curID = 0;

    public ServerSideClient(String host, int port) {
        init(host, port);
    }

    private void init(String host, int port) {
        CommConnection.initConnection(host, port);
    }

    public void addListener(CommListener listener) {
        CommConnection.getInstance().addListener(listener);
    }

    public void sendHeartbeatPing(String leaderIp) {
        // construct the heartbeat to send
        Route.Builder rb = Route.newBuilder();
        rb.setId(nextId());
        rb.setPath(Route.Path.HEARTBEAT);
        rb.setHeartbeat(Pipe.Heartbeat.newBuilder().setMode(Pipe.Heartbeat.Mode.PING).setAddress(leaderIp));

        try {
            // direct no queue
            CommConnection.getInstance().write(rb.build());

            // using queue
            //CommConnection.getInstance().enqueue(rb.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendHeartbeatAck(String followerIp) {
        // construct the heartbeat to send
        Route.Builder rb = Route.newBuilder();
        rb.setId(nextId());
        rb.setPath(Route.Path.HEARTBEAT);
        rb.setHeartbeat(Pipe.Heartbeat.newBuilder().setMode(Pipe.Heartbeat.Mode.ACK).setAddress(followerIp));

        try {
            // direct no queue
            CommConnection.getInstance().write(rb.build());

            // using queue
            //CommConnection.getInstance().enqueue(rb.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pullMessages(Route route) {
        // construct the route to send
        Route.Builder rb = Route.newBuilder(route);
        rb.setHeader(Pipe.Header.newBuilder().setType(Pipe.Header.Type.INTER_CLUSTER));

        try {
            // direct no queue
            CommConnection.getInstance().write(rb.build());

            // using queue
            //CommConnection.getInstance().enqueue(rb.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addUserToGroup(Route route) {
        // construct the route to send
        Route.Builder rb = Route.newBuilder(route);
        rb.setHeader(Pipe.Header.newBuilder().setType(Pipe.Header.Type.INTER_CLUSTER));

        try {
            // direct no queue
            CommConnection.getInstance().write(rb.build());

            // using queue
            //CommConnection.getInstance().enqueue(rb.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postMessageToGroup(Route route) {
        // construct the route to send
        Route.Builder rb = Route.newBuilder(route);
        rb.setHeader(Pipe.Header.newBuilder().setType(Pipe.Header.Type.INTER_CLUSTER));

        try {
            // direct no queue
            CommConnection.getInstance().write(rb.build());

            // using queue
            //CommConnection.getInstance().enqueue(rb.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void replicate(Route route) {
        // construct the route to send
        Route.Builder rb = Route.newBuilder(route);
        rb.setHeader(Pipe.Header.newBuilder().setType(Pipe.Header.Type.INTERNAL));

        try {
            // direct no queue
            CommConnection.getInstance().write(rb.build());

            // using queue
            //CommConnection.getInstance().enqueue(rb.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        CommConnection.getInstance().release();
    }

    /**
     * Since the service/server is asynchronous we need a unique ID to associate
     * our requests with the server's reply
     *
     * @return
     */
    private synchronized long nextId() {
        return ++curID;
    }
}
