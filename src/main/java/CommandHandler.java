public class CommandHandler {

    private static final String CRLF_TERMINATOR = "\r\n";
    private static final String DOLLAR = "$";
    private static final String PLUS = "+";
    private CommandHandler() {
    }
    public static String handle(String parsedCommand) {
        String[] arguments = parsedCommand.split(" ");
        String command = arguments[0].toLowerCase();
        return switch (command) {
            case "ping" -> PLUS + "PONG\r\n";
            case "echo" -> DOLLAR + arguments[1].length() + CRLF_TERMINATOR + arguments[1] + CRLF_TERMINATOR;
            case "set" -> setCommandData(arguments);
            case "get" -> getCommandData(arguments);
            default -> throw new RuntimeException("Unknown command: " + command);
        };
    }

    private static String setCommandData(String[] arguments) {
        Cache.setData(arguments[1], arguments[2]);
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