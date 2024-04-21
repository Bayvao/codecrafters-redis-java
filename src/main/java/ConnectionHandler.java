import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionHandler extends Thread {
  private final Socket socket;
  private final ServerInformation serverInformation;

  public ConnectionHandler(Socket socket, ServerInformation serverInformation) {
    this.socket = socket;
    this.serverInformation =  serverInformation;
  }

  @Override
  public void run() {
    try (DataInputStream dataInputStream =
             new DataInputStream(socket.getInputStream());
         OutputStream outputStream = socket.getOutputStream()) {
      while (true) {
        String parsedCommand = ProtocolParser.parseInput(dataInputStream);
        String response = CommandHandler.handle(parsedCommand, serverInformation);
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
      }
    } catch (EOFException e) {
      // Ignore EOF exception
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