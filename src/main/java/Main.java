import java.io.*;
import java.net.Socket;
import java.util.HexFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.random.RandomGenerator;

public class Main {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private static final byte[] REPLICA_ID = new byte[40];

    public static void main(String[] args){
        ServerInformation serverInfo = new ServerInformation();
        int port = 6379;
        serverInfo.setRole("master");

        int argumentLength = args.length;

        if (argumentLength > 1) {
            if ("--port".equalsIgnoreCase(args[0])) {
                port = Integer.parseInt(args[1]);
            }

            if (argumentLength > 2 && "--replicaof".equalsIgnoreCase(args[2])) {
                serverInfo.setRole("slave");
                serverInfo.setMasterHost(args[3]);
                serverInfo.setMasterPort(args[4]);
            }
        }
        serverInfo.setPort(port);

        if (serverInfo.getRole().equalsIgnoreCase("master")) {
            RandomGenerator.getDefault().nextBytes(REPLICA_ID);
            serverInfo.setMasterReplid(HexFormat.of().formatHex(REPLICA_ID));
        }


        System.out.println("Logs from your program will appear here!");
        Connection.initiateConnection(serverInfo);
    }

}
