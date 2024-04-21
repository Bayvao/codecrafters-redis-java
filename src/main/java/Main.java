import java.io.*;
import java.net.ServerSocket;
import java.util.Arrays;
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
                serverInfo.setReplicaOfHost(args[3]);
                serverInfo.setReplicaOfPort(args[4]);
            }
        }
        serverInfo.setPort(port);

        if (serverInfo.getRole().equalsIgnoreCase("master")) {
            RandomGenerator.getDefault().nextBytes(REPLICA_ID);
            serverInfo.setMasterReplid(HexFormat.of().formatHex(REPLICA_ID));
        }


        System.out.println("Logs from your program will appear here!");
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors

            serverSocket.setReuseAddress(true);

            // Wait for connection from client.
            while (true) {
                new ConnectionHandler(serverSocket.accept(), serverInfo).start();
            }
        } catch (EOFException e) {
            //this is fine
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

  /*
    private static void handleConcurrentConnections(Socket clientSocket) throws IOException {
        var br = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        OutputStream outputStream = clientSocket.getOutputStream();
        String command;
        while ((command = br.readLine()) != null) {
            System.out.println("command: " + command);
            if (command.equalsIgnoreCase("ping")) {
                outputStream.write("+PONG\r\n".getBytes(StandardCharsets.UTF_8));
            }
        }
        outputStream.flush();
    }
   */

}
