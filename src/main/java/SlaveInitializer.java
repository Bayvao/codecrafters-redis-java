import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SlaveInitializer extends Thread {

    private final ServerInformation serverInformation;

    public SlaveInitializer(ServerInformation serverInformation) {
        this.serverInformation = serverInformation;
    }

    @Override
    public void run() {

        // initiating slave to master connection and sending a PING to establish the connection
        try (Socket masterSocket = new Socket(serverInformation.getMasterHost(), Integer.parseInt(serverInformation.getMasterPort()));
             DataInputStream serverReader = new DataInputStream(masterSocket.getInputStream());
             OutputStream serverWriter = masterSocket.getOutputStream()) {

            initiateHandshakeWithMaster(serverInformation, serverWriter, serverReader);

            System.out.println("Replica Initialized...");

            while (true) {

                System.out.println("Replica Server Started in here");
                String parsedCommand = ProtocolParser.parseInput(serverReader);
                System.out.printf("command received: %s\n", parsedCommand);
                String response = CommandHandler.handle(parsedCommand, serverInformation);
                serverWriter.write(response.getBytes(StandardCharsets.UTF_8));
            }

        } catch (EOFException e) {
            //this is fine
        } catch (IOException e) {
            System.out.println(
                    "Exception occurred when tried to connect to the server: " +
                            e.getMessage());
            System.out.println("IOException: " + e.getMessage());
            System.out.println(e);
        } catch (RuntimeException e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }
    }

    private static void initiateHandshakeWithMaster(ServerInformation serverInformation,
                                                    OutputStream serverWriter,
                                                    DataInputStream serverReader) throws IOException {

        serverWriter.write("*1\r\n$4\r\nping\r\n".getBytes());
        serverWriter.flush();

        System.out.println("Sent PING to master");
        String parsedMasterResponse = ProtocolParser.parseInput(serverReader); //PONG
        String[] arguments = parsedMasterResponse.split(" ");
        String command = arguments[0].toLowerCase();

        System.out.println("received PONG from master");
        if (command.equalsIgnoreCase("pong")) {
            serverWriter.write(getReplConfBytes1(serverInformation));
            serverWriter.flush();
            System.out.println("Sent ReplConfBytes1 to master");

            serverReader.readByte();
            parsedMasterResponse  = ProtocolParser.parseInput(serverReader); //OK
            System.out.println("received OK from master");

            if (parsedMasterResponse.equalsIgnoreCase("ok")) {
                serverWriter.write(getReplConfBytes2(serverInformation));
                serverWriter.flush();
                System.out.println("Sent ReplConfBytes2 to master");

                serverReader.readByte();
                parsedMasterResponse  = ProtocolParser.parseInput(serverReader); //OK
                System.out.println("received OK from master");

                if (parsedMasterResponse.equalsIgnoreCase("ok")) {
                    serverWriter.write(getPsyncConfBytes(serverInformation));
                    serverWriter.flush();
                    System.out.println("Sent PsyncConfBytes to master");
                }
            }

            parsedMasterResponse  = ProtocolParser.parseInput(serverReader);
            System.out.printf("Received response for PSYNC: %s\n", parsedMasterResponse);

            decodeRDbFile(serverReader);
            serverWriter.flush();
        }
    }

    public static void decodeRDbFile(DataInputStream reader) throws IOException {
        int fileSize = Integer.parseInt(reader.readLine().substring(1));
        byte[] buffer = new byte[fileSize];
        int bytesRead = reader.read(buffer, 0, fileSize - 1);
        String rdbFile = new String(buffer, 0, fileSize);
        System.out.println(rdbFile);
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
