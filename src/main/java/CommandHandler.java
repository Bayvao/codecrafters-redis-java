public class CommandHandler {

    private static final String CRLF_TERMINATOR = "\r\n";
    private static final String DOLLAR = "$";
    private CommandHandler() {
    }
    public static String handle(String parsedCommand) {
        String[] arguments = parsedCommand.split(" ");
        String command = arguments[0].toLowerCase();
        return switch (command) {
            case "ping" -> "+PONG\r\n";
            case "echo" -> DOLLAR + arguments[1].length() + CRLF_TERMINATOR + arguments[1] + CRLF_TERMINATOR;
            case "set" -> setCommandData(arguments);
            case "get" -> getCommandData(arguments);
            default -> throw new RuntimeException("Unknown command: " + command);
        };
    }

    private static String setCommandData(String[] arguments) {
        DataModel dataModel = new DataModel();
        dataModel.setSetCommandMap(arguments[1], arguments[2]);
        return DOLLAR + "OK" + CRLF_TERMINATOR;
    }

    private static String getCommandData(String[] arguments) {
        DataModel dataModel = new DataModel();
        String data = dataModel.getSetCommandMap(arguments[1]);
        if (data != null && !data.isEmpty()) {
            return DOLLAR + data.length() + CRLF_TERMINATOR + data + CRLF_TERMINATOR;
        }
        return DOLLAR + "-1" + CRLF_TERMINATOR;
    }
}