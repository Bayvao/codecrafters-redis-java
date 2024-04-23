import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;

public class Connection {

    public static void initiateConnection(ServerInformation serverInfo) {
        try (ServerSocket serverSocket = new ServerSocket(serverInfo.getPort())) {

            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors

            serverSocket.setReuseAddress(true);
            if (serverInfo.getRole().equalsIgnoreCase("slave")) {
                System.out.println("Replica node initializing");
                new SlaveInitializer(serverInfo).start();
            } else {
                System.out.println("Master node initializing");
            }


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
