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
package gash.router.app;

import gash.router.client.CommConnection;
import gash.router.client.CommListener;
import gash.router.client.MessageClient;
import routing.Pipe.Route;

import java.util.Scanner;

public class DemoApp implements CommListener {
    private MessageClient mc;

    private volatile boolean waitingForReply;

    public DemoApp(MessageClient mc) {
        waitingForReply = false;
        init(mc);
    }

    private void init(MessageClient mc) {
        this.mc = mc;
        this.mc.addListener(this);
    }

    private void waitForReply() {
        waitingForReply = true;
        while (waitingForReply) {

        }
    }

    private void handleCmd(String cmd) {
        String[] parsedCmd = cmd.split("\\s+");

        //[0] is the command
        switch (parsedCmd[0]) {
            case "ping":
                mc.ping();
                waitForReply();
                break;
            default:
                System.out.println("Invalid command.\n");
        }
    }

    @Override
    public String getListenerID() {
        return "demo";
    }

    @Override
    public void onMessage(Route msg) {
        System.out.println(msg);
        //System.out.println("------------------------------------------------------------");
        waitingForReply = false;
    }

    /**
     * sample application (client) use of our messaging service
     *
     * @param args
     */
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 4444;

        Scanner input = new Scanner(System.in);
        String cmd = null;

        try {
            MessageClient mc = new MessageClient(host, port);
            DemoApp da = new DemoApp(mc);

            // do stuff w/ the connection
            while (true) {
                System.out.print("> ");
                cmd = input.nextLine();
                da.handleCmd(cmd);
            }

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
