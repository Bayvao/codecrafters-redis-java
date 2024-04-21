import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionHandler extends Thread {
  private final Socket socket;
  private final String[] args;
  public ConnectionHandler(Socket socket, String[] args) {
    this.socket = socket;
    this.args = args;
  }

  @Override
  public void run() {
    try (DataInputStream dataInputStream =
             new DataInputStream(socket.getInputStream());
         OutputStream outputStream = socket.getOutputStream()) {
      while (true) {
        String parsedCommand = null;
        if (!(args.length > 0)) {
          parsedCommand = ProtocolParser.parseInput(dataInputStream);
        }

        String response = CommandHandler.handle(parsedCommand, args);
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } finally {
      try {
        socket.close();
        System.out.printf("%s socket closed%n", getName());
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }
}