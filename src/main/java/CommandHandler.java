import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Set;
import java.util.random.RandomGenerator;

public class CommandHandler {

    private static final String CRLF_TERMINATOR = "\r\n";
    private static final String DOLLAR = "$";
    private static final String PLUS = "+";

    private static final String ROLE = "role:";
    private static final String MASTER_REPLICA_ID = "master_replid:";
    private static final String MASTER_REPLICA_OFFSET = "master_repl_offset:0";

    private CommandHandler() {
    }
    public static String handle(String parsedCommand, ServerInformation serverInformation, DataInputStream dataInputStream) {
        String[] arguments = parsedCommand.split(" ");
        String command = arguments[0].toUpperCase();
        return switch (command) {
            case "PING" -> PLUS + "PONG\r\n";
            case "ECHO" -> DOLLAR + arguments[1].length() + CRLF_TERMINATOR + arguments[1] + CRLF_TERMINATOR;
            case "SET" -> setCommandData(arguments, serverInformation, dataInputStream);
            case "GET" -> getCommandData(arguments);
            case "INFO" -> getServerInformation(serverInformation);
            case "REPLCONF" -> PLUS + "OK\r\n";
            case "PSYNC" -> PLUS + "FULLRESYNC " + serverInformation.getMasterReplid() + " 0" + CRLF_TERMINATOR;
            default -> throw new RuntimeException("Unknown command: " + command);
        };

    }



    private static String getServerInformation(ServerInformation serverInformation) {

        System.out.println(serverInformation.toString());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ROLE)
                .append(serverInformation.getRole())
                .append(CRLF_TERMINATOR);
        if (serverInformation.getMasterHost() == null && serverInformation.getMasterPort() == null) {
            stringBuilder.append(MASTER_REPLICA_ID)
                    .append(serverInformation.getMasterReplid())
                    .append(CRLF_TERMINATOR)
                    .append(MASTER_REPLICA_OFFSET);
        }
        return bulkString(stringBuilder.toString());
    }

    private static String bulkString(String s) {
        return "$%d\r\n%s\r\n".formatted(s.length(), s);
    }

    private static String setCommandData(String[] arguments, ServerInformation serverInformation, DataInputStream dataInputStream) {
        if (arguments.length > 3) {
            String argument = arguments[3].toLowerCase();
            if (argument.equalsIgnoreCase("px")) {
                Long expiration = Long.valueOf(arguments[4]);
                Cache.setDataWithTtl(arguments[1], arguments[2], expiration);
            }
        } else {
            Cache.setData(arguments[1], arguments[2]);
        }

        if (serverInformation.getRole().equalsIgnoreCase("master")) {
            Set<OutputStream> replicas = serverInformation.getReplicaSet();
            replicas.forEach(outputStream -> {
                try {
                    outputStream.write(dataInputStream.readAllBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return PLUS + "OK" + CRLF_TERMINATOR;
    }

    private static String getCommandData(String[] arguments) {
        String data = Cache.getData(arguments[1]);
        System.out.println(data);
        if (data == null) {
            return DOLLAR + "-1" + CRLF_TERMINATOR;
        }
        return DOLLAR + data.length() + CRLF_TERMINATOR + data + CRLF_TERMINATOR;
    }
}