import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionHandler extends Thread {
  private final Socket socket;
  private final ServerInformation serverInformation;

  private static final String EMPTY_RDB_FILE_BASE64_ENCODED = "UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog==";

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
        if(response.contains("FULLRESYNC")) {
          outputStream.write(sendEmptyRDBFile());
        }
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

  private byte[] sendEmptyRDBFile() {
    return ("$" + EMPTY_RDB_FILE_BASE64_ENCODED.length() + "\r\n" + EMPTY_RDB_FILE_BASE64_ENCODED)
            .getBytes(StandardCharsets.UTF_8);
  }
}