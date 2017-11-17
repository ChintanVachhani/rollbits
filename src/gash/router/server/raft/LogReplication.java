package gash.router.server.raft;

public class LogReplication {
    private static LogReplication ourInstance = new LogReplication();

    public static LogReplication getInstance() {
        return ourInstance;
    }

    private LogReplication() {
    }
}
