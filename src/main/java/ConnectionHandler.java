import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ConnectionHandler extends Thread {
  private final Socket socket;
  private final ServerInformation serverInformation;

  private static final String EMPTY_RDB_FILE_BASE64_ENCODED = "UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog==";

  public ConnectionHandler(Socket socket, ServerInformation serverInformation) {
    this.socket = socket;
    this.serverInformation =  serverInformation;
    System.out.println("client socket = " + socket + " connected!");
  }

  @Override
  public void run() {
    try (DataInputStream dataInputStream =
             new DataInputStream(socket.getInputStream());
         OutputStream outputStream = socket.getOutputStream()) {
      while (true) {
        String parsedCommand = ProtocolParser.parseInput(dataInputStream);

        System.out.printf("command received: %s\n", parsedCommand);
        String response = CommandHandler.handle(parsedCommand, serverInformation);

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));

        if(response.contains("FULLRESYNC")) {
          System.out.println(socket.getRemoteSocketAddress());
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
    byte[] emptyRdb = Base64.getDecoder().decode(EMPTY_RDB_FILE_BASE64_ENCODED);
    byte[] fileSize = "$%s\r\n".formatted(emptyRdb.length).getBytes(StandardCharsets.UTF_8);
    ByteBuffer buffer = ByteBuffer.wrap(new byte[fileSize.length + emptyRdb.length]);
    buffer.put(fileSize);
    buffer.put(emptyRdb);
    return buffer.array();
  }
}