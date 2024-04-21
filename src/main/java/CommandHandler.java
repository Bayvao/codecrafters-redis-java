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
    public static String handle(String parsedCommand, ServerInformation serverInformation) {
        String[] arguments = parsedCommand.split(" ");
        String command = arguments[0].toLowerCase();
        return switch (command) {
            case "ping" -> PLUS + "PONG\r\n";
            case "echo" -> DOLLAR + arguments[1].length() + CRLF_TERMINATOR + arguments[1] + CRLF_TERMINATOR;
            case "set" -> setCommandData(arguments);
            case "get" -> getCommandData(arguments);
            case "info" -> getServerInformation(serverInformation);
            default -> throw new RuntimeException("Unknown command: " + command);
        };

    }

    private static String getServerInformation(ServerInformation serverInformation) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DOLLAR)
                .append((ROLE.length() + serverInformation.getRole().length()))
                .append(CRLF_TERMINATOR)
                .append(ROLE)
                .append(serverInformation.getRole())
                .append(CRLF_TERMINATOR);
        if (serverInformation.getReplicaOfHost() == null && serverInformation.getReplicaOfPort() == null) {
            stringBuilder.append(MASTER_REPLICA_ID)
                    .append(serverInformation.getMasterReplid())
                    .append(CRLF_TERMINATOR)
                    .append(MASTER_REPLICA_OFFSET);
        }
        return stringBuilder.toString();
    }

    private static String setCommandData(String[] arguments) {
        if (arguments.length > 3) {
            String argument = arguments[3].toLowerCase();
            if (argument.equalsIgnoreCase("px")) {
                Long expiration = Long.valueOf(arguments[4]);
                Cache.setDataWithTtl(arguments[1], arguments[2], expiration);
            }
        } else {
            Cache.setData(arguments[1], arguments[2]);
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