import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    //  Uncomment this block to pass the first stage
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;
        try {
          serverSocket = new ServerSocket(port);
    //      // Since the tester restarts your program quite often, setting SO_REUSEADDR
    //      // ensures that we don't run into 'Address already in use' errors
          serverSocket.setReuseAddress(true);
    //      // Wait for connection from client.
          clientSocket = serverSocket.accept();
            var br = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            OutputStream outputStream = clientSocket.getOutputStream();
            String command;
            while ((command = br.readLine()) != null) {
                System.out.println("command: " + command);
                if (command.equalsIgnoreCase("ping")) {
                    outputStream.write("+PONG\r\n".getBytes(StandardCharsets.UTF_8));
                    // clientSocket.close();
                }
            }
            clientSocket.close();
            // OutputStream outputStream = clientSocket.getOutputStream();
            // outputStream.write("+PONG\r\n".getBytes(StandardCharsets.UTF_8));
            // clientSocket.close();
            //  }
            
        } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
        } finally {
          try {
            if (clientSocket != null) {
              clientSocket.close();
            }
          } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
          }
        }
  }
}
