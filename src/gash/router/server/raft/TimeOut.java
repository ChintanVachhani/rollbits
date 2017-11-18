package gash.router.server.raft;

import static java.lang.Thread.sleep;

public class TimeOut implements Runnable {

    public TimeOut() {
        Raft.getInstance();
    }

    public void run() {
        // code in the other thread, can reference "var" variable
        while (true) {
            while (Raft.getInstance().getTimeOut() > 0) {
                try {
                    sleep(100);
                    Raft.getInstance().setTimeOut(Raft.getInstance().getTimeOut() - 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Raft.getInstance().election();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
