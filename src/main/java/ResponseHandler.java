public class ResponseHandler {

    private static final String CRLF_TERMINATOR = "\r\n";
    private static final String DOLLAR = "$";
    private static final String PLUS = "+";

    private static final String ROLE = "role:";
    private static final String MASTER_REPLICA_ID = "master_replid:";
    private static final String MASTER_REPLICA_OFFSET = "master_repl_offset:0";

    private ResponseHandler() {
    }
    public static byte[] handle(String parsedCommand, ServerInformation serverInformation) {
        String[] arguments = parsedCommand.split(" ");
        String command = arguments[0].toLowerCase();
        return switch (command) {
            case "pong" -> getReplConfBytes(serverInformation);
            default -> throw new RuntimeException("Unknown command: " + command);
        };

    }

    public static byte[] getReplConfBytes(ServerInformation serverInformation) {
        return ("*3\r\n$8\r\nREPLCONF\r\n$14\r\nlistening-port\r\n$4\r\n" +
                serverInformation.getPort() + "\r\n"
                + "*3\r\n$8\r\nREPLCONF\r\n$4\r\ncapa\r\n$6\r\npsync2\r\n")
                .getBytes();
    }

    private static String bulkString(String s) {
        return "$%d\r\n%s\r\n".formatted(s.length(), s);
    }
}