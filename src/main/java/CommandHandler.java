public class CommandHandler {

    private static final String CRLF_TERMINATOR = "\r\n";
    private static final String DOLLAR = "$";
    private static final String PLUS = "+";
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
            case "info" -> DOLLAR + (5 + serverInformation.getRole().length()) + CRLF_TERMINATOR + "role:"
                    + serverInformation.getRole() + CRLF_TERMINATOR;
            default -> throw new RuntimeException("Unknown command: " + command);
        };

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