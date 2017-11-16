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
    private String clientUname;
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
            case "ping": // ex. ping
                mc.ping();
                waitForReply();
                break;
            case "user": // ex. user [register|access|delete] <username>
                if ((parsedCmd[1].equals("register") || parsedCmd[1].equals("access") || parsedCmd[1].equals("delete")) && parsedCmd[2] != null) {
                    clientUname = parsedCmd[2];
                    mc.user(parsedCmd[1], parsedCmd[2]);
                    waitForReply();
                } else {
                    System.out.println("Invalid command.\n");
                }
                break;
            case "group": // ex. group [create|delete] <groupname> (or) group [add-user|remove-user] <username> <groupname>
                if ((parsedCmd[1].equals("create") || parsedCmd[1].equals("delete")) && parsedCmd[2] != null) {
                    mc.group(parsedCmd[1], clientUname, parsedCmd[2]);
                    waitForReply();
                } else if ((parsedCmd[1].equals("add-user") || parsedCmd[1].equals("remove-user")) && parsedCmd[2] != null && parsedCmd[3] != null) {
                    mc.group(parsedCmd[1], parsedCmd[2], parsedCmd[3]);
                } else {
                    System.out.println("Invalid command.\n");
                }
                break;
            case "message": // ex. message [user|group] <username|groupname> <message>
                if ((parsedCmd[1].equals("user") || parsedCmd[1].equals("group")) && parsedCmd[2] != null && parsedCmd[3] != null) {
                    mc.message(parsedCmd[1], parsedCmd[2], parsedCmd[3], clientUname);
                    waitForReply();
                } else {
                    System.out.println("Invalid command.\n");
                }
                break;
            case "fetch": // ex. fetch
                mc.getMessages(clientUname);
                waitForReply();
                break;
            case "help":
                printBox("\033[0;1mCOMMANDS\n",
                        "\u001B[34mping\u001B[0m : to ping the connected server",
                        "\u001B[34muser \u001B[31m[register|access|delete] \u001B[32m<username>\u001B[0m : to register, access or delete a user",
                        "\u001B[34mgroup \u001B[31m[create|delete] \u001B[32m<groupname>\u001B[0m (or) \u001B[34mgroup \u001B[31m[add-user|remove-user] \u001B[32m<username> <groupname>\u001B[0m : to create or delete a group, or to add or remove a user from a group",
                        "\u001B[34mmessage \u001B[31m[user|group] \u001B[32m<username|groupname> <message>\u001B[0m : to send a message to a user or a group",
                        "\u001B[34mfetch\u001B[0m : to get all your messages",
                        "\u001B[34mhelp\u001B[0m : to get the commands list");
                System.out.println();
                break;
            default:
                System.out.println("Invalid command.\n");
        }
    }

    // Code for help box
    private static int getMaxLength(String... strings) {
        int len = Integer.MIN_VALUE;
        for (String str : strings) {
            len = Math.max(str.length(), len);
        }
        return len;
    }

    private static String fill(char ch, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    private static String padString(String str, int len) {
        StringBuilder sb = new StringBuilder(str);
        return sb.append(fill(' ', len - str.length())).toString();
    }

    public static void printBox(String... strings) {
        int maxBoxWidth = getMaxLength(strings);
        String line = "+" + fill('-', maxBoxWidth + 2) + "+";
        System.out.println(line);
        for (String str : strings) {
            System.out.printf(" %s %n", padString(str, maxBoxWidth));
        }
        System.out.println(line);
    }

    // Color codes
    /*public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BOLD = "\033[0;1m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";*/

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

            System.out.println();
            printBox("\033[0;1mCOMMANDS\n",
                    "\u001B[34mping\u001B[0m : to ping the connected server",
                    "\u001B[34muser \u001B[31m[register|access|delete] \u001B[32m<username>\u001B[0m : to register, access or delete a user",
                    "\u001B[34mgroup \u001B[31m[create|delete] \u001B[32m<groupname>\u001B[0m (or) \u001B[34mgroup \u001B[31m[add-user|remove-user] \u001B[32m<username> <groupname>\u001B[0m : to create or delete a group, or to add or remove a user from a group",
                    "\u001B[34mmessage \u001B[31m[user|group] \u001B[32m<username|groupname> <message>\u001B[0m : to send a message to a user or a group",
                    "\u001B[34mfetch\u001B[0m : to get all your messages",
                    "\u001B[34mhelp\u001B[0m : to get the commands list");
            System.out.println();

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
