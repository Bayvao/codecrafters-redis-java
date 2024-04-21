import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final ExecutorService executorService =
      Executors.newCachedThreadPool();
    public static void main(String[] args){
        int port = 6379;

        if ("--port".equalsIgnoreCase(args[0])) {
            port = Integer.parseInt(args[1]);
        }

        System.out.println("Logs from your program will appear here!");
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors

            serverSocket.setReuseAddress(true);

            // Wait for connection from client.
            while (true) {
                new ConnectionHandler(serverSocket.accept()).start();
            }
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
