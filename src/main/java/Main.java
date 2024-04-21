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

            String[] arguments = parsedMasterResponse.split(" ");
            String command = arguments[0].toLowerCase();

            if (command.equalsIgnoreCase("pong")) {
                serverWriter.write(getReplConfBytes1(serverInformation));
                
                parsedMasterResponse  = ProtocolParser.parseInput(serverReader); //OK
                System.out.println("here 3: " + parsedMasterResponse);
                if (parsedMasterResponse.equalsIgnoreCase("ok")) {

                    serverWriter.write(getReplConfBytes2(serverInformation));
                    parsedMasterResponse  = ProtocolParser.parseInput(serverReader); //OK

                    if (parsedMasterResponse.equalsIgnoreCase("ok")) {
                        serverWriter.write(getPsyncConfBytes(serverInformation));
                    }
                }
                serverWriter.flush();
                initiateConnection(serverInformation);
            }
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

    private static byte[] getPsyncConfBytes(ServerInformation serverInformation) {
        return ("*3\r\n$5\r\nPSYNC\r\n$1\r\n?\r\n$2\r\n-1\r\n")
                .getBytes();
    }

    public static byte[] getReplConfBytes1(ServerInformation serverInformation) {
        return ("*3\r\n$8\r\nREPLCONF\r\n$14\r\nlistening-port\r\n$4\r\n" +
                serverInformation.getPort() + "\r\n")
                .getBytes();
    }

    public static byte[] getReplConfBytes2(ServerInformation serverInformation) {
        return ("*3\r\n$8\r\nREPLCONF\r\n$4\r\ncapa\r\n$6\r\npsync2\r\n")
                .getBytes();
    }

}
