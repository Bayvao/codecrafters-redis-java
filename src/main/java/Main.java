import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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

        if (serverInfo.getRole().equalsIgnoreCase("slave")) {
            initiateSlaveConnection(serverInfo);
        } else {
            initiateConnection(serverInfo);
        }
    }

    private static void initiateSlaveConnection(ServerInformation serverInformation) {

        // initiating slave to master connection and sending a PING to establish the connection
        try (Socket serverSocket = new Socket(serverInformation.getMasterHost(),
                Integer.parseInt(serverInformation.getMasterPort()));
             DataInputStream serverReader =
                     new DataInputStream(serverSocket.getInputStream());
             OutputStream serverWriter = serverSocket.getOutputStream()
        ) {
            String parsedMasterResponse;
            serverWriter.write("*1\r\n$4\r\nping\r\n".getBytes());
            parsedMasterResponse = ProtocolParser.parseInput(serverReader); //PONG
            System.out.println("here 1");
            serverWriter.write(ResponseHandler.handle(parsedMasterResponse, serverInformation));
            System.out.println("here 2");
            parsedMasterResponse  = ProtocolParser.parseInput(serverReader); //OK
            System.out.println("here 3");
            parsedMasterResponse = ProtocolParser.parseInput(serverReader); //OK
            System.out.println("here 4");
            serverWriter.write(ResponseHandler.handle(parsedMasterResponse, serverInformation));
            serverWriter.flush();
            initiateConnection(serverInformation);
        } catch (EOFException e) {
            //this is fine
        } catch (IOException e) {
            System.out.println(
                    "Exception occurred when tried to connect to the server: " +
                            e.getMessage());
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void initiateConnection(ServerInformation serverInfo) {
        try (ServerSocket serverSocket = new ServerSocket(serverInfo.getPort())) {

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

}
