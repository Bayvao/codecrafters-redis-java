import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Slave {

    public static void initiateSlaveConnection(Socket replicaSocket, ServerInformation serverInformation) {

        // initiating slave to master connection and sending a PING to establish the connection
        try (DataInputStream serverReader =
                     new DataInputStream(replicaSocket.getInputStream());
             OutputStream serverWriter = replicaSocket.getOutputStream()
        ) {
            initiateHandshakeWithMaster(serverInformation, serverWriter, serverReader);
        } catch (EOFException e) {
            //this is fine
        } catch (IOException e) {
            System.out.println(
                    "Exception occurred when tried to connect to the server: " +
                            e.getMessage());
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void initiateHandshakeWithMaster(ServerInformation serverInformation, OutputStream serverWriter, DataInputStream serverReader) throws IOException {
        String parsedMasterResponse;
        serverWriter.write("*1\r\n$4\r\nping\r\n".getBytes());
        parsedMasterResponse = ProtocolParser.parseInput(serverReader); //PONG

        String[] arguments = parsedMasterResponse.split(" ");
        String command = arguments[0].toLowerCase();

        if (command.equalsIgnoreCase("pong")) {
            serverWriter.write(getReplConfBytes1(serverInformation));

            serverReader.readByte();
            parsedMasterResponse  = ProtocolParser.parseInput(serverReader); //OK
            if (parsedMasterResponse.equalsIgnoreCase("ok")) {

                serverWriter.write(getReplConfBytes2(serverInformation));
                serverReader.readByte();
                parsedMasterResponse  = ProtocolParser.parseInput(serverReader); //OK

                if (parsedMasterResponse.equalsIgnoreCase("ok")) {
                    serverWriter.write(getPsyncConfBytes(serverInformation));
                }
            }
            serverWriter.flush();
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
