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
package gash.router.client;

import routing.Pipe;
import routing.Pipe.Route;

import java.util.Date;

/**
 * front-end (proxy) to our service - functional-based
 *
 * @author gash
 */
public class MessageClient {
    // track requests
    private long curID = 0;

    public MessageClient(String host, int port) {
        init(host, port);
    }

    private void init(String host, int port) {
        CommConnection.initConnection(host, port);
    }

    public void addListener(CommListener listener) {
        CommConnection.getInstance().addListener(listener);
    }

    public void ping() {
        // construct the message to send
        Route.Builder rb = Route.newBuilder();
        rb.setId(nextId());
        rb.setPath(Route.Path.PING);
        rb.setPayloadMessage("ping");

        try {
            // direct no queue
            // CommConnection.getInstance().write(rb.build());

            // using queue
            CommConnection.getInstance().enqueue(rb.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void user(String action, String uname, String password) {
        // construct the message to send
        Pipe.User.Builder user = Pipe.User.newBuilder();
        user.setUname(uname);

        switch (action) {
            case "register":
                user.setAction(Pipe.User.ActionType.REGISTER);
                break;
            case "access":
                user.setAction(Pipe.User.ActionType.ACCESS);
                break;
            case "delete":
                user.setAction(Pipe.User.ActionType.DELETE);
                break;
        }

        Route.Builder rb = Route.newBuilder();
        rb.setId(nextId());
        rb.setPath(Route.Path.USER);
        rb.setUser(user.build());

        try {
            // using queue
            CommConnection.getInstance().enqueue(rb.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void group(String action, String uname, String gname) {
        // construct the message to send
        Pipe.Group.Builder group = Pipe.Group.newBuilder();
        group.setGname(uname);
        group.setGname(gname);

        switch (action) {
            case "create":
                group.setAction(Pipe.Group.ActionType.CREATE);
                break;
            case "delete":
                group.setAction(Pipe.Group.ActionType.DELETE);
                break;
            case "add-user":
                group.setAction(Pipe.Group.ActionType.ADD_USER);
                break;
            case "remove-user":
                group.setAction(Pipe.Group.ActionType.REMOVE_USER);
                break;
        }

        Route.Builder rb = Route.newBuilder();
        rb.setId(nextId());
        rb.setPath(Route.Path.GROUP);
        rb.setGroup(group.build());

        try {
            // using queue
            CommConnection.getInstance().enqueue(rb.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void message(String type, String receiverId, String payload, String senderId) {
        // construct the message to send
        Pipe.Message.Builder message = Pipe.Message.newBuilder();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setPayload(payload);

        switch (type) {
            case "user":
                message.setType(Pipe.Message.Type.SINGLE);
                break;
            case "group":
                message.setType(Pipe.Message.Type.GROUP);
                break;
        }

        Route.Builder rb = Route.newBuilder();
        rb.setId(nextId());
        rb.setPath(Route.Path.MESSAGE);
        rb.setMessage(message.build());

        try {
            // using queue
            CommConnection.getInstance().enqueue(rb.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMessages(String uname){
        // construct the message to send
        Pipe.MessagesRequest.Builder messagesRequest = Pipe.MessagesRequest.newBuilder();
        messagesRequest.setId(uname);

        Route.Builder rb = Route.newBuilder();
        rb.setId(nextId());
        rb.setPath(Route.Path.MESSAGES_REQUEST);
        rb.setMessagesRequest(messagesRequest.build());

        try {
            // using queue
            CommConnection.getInstance().enqueue(rb.build());
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
