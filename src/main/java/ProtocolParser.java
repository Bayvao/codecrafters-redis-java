import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProtocolParser {
    private ProtocolParser() {}
    public static String parseInput(DataInputStream inputStream, ServerInformation serverInformation) {
        try {

            if (serverInformation.getRole().equalsIgnoreCase("slave"))
                inputStream.readByte();

            char c = (char) inputStream.readByte();
            return switch (c) {
                case '+' -> parseSimpleString(inputStream);
                case '*' -> parseArray(inputStream);
                case '$' -> parseString(inputStream);
                default -> throw new RuntimeException("Unknown character: " + c);
            };
        } catch (EOFException e){
            throw new RuntimeException(e.getMessage());
        }
        catch(IOException e){
                throw new RuntimeException(e);
        }
    }

    private static String parseSimpleString(DataInputStream inputStream) throws IOException {
        StringBuilder parsedData = new StringBuilder();
        char c;
        while ((c = (char) inputStream.readByte()) != '\r') {
            parsedData.append((char) c);
        }
        return parsedData.toString();
    }

    private static String parseArray(DataInputStream inputStream) throws IOException {
        int arraySize = parseDigits(inputStream);
        return IntStream.range(0, arraySize)
                .mapToObj(i -> parseInput(inputStream, null))
                .collect(Collectors.joining(" "));
    }
    private static String parseString(DataInputStream inputStream) throws IOException {
        int stringLength = parseDigits(inputStream);
        byte[] bytes = new byte[stringLength];
        inputStream.readFully(bytes);
        inputStream.skipBytes(2); // skip terminating '\r\n'
        return new String(bytes);
    }
    private static int parseDigits(DataInputStream inputStream) throws IOException {
        StringBuilder digits = new StringBuilder();
        char c;
        while ((c = (char) inputStream.readByte()) != '\r') {
            digits.append(c);
        }
        inputStream.readByte(); // skip '\n' after '\r'
        return Integer.parseInt(digits.toString());
    }

    public static String decodeRDbFile(DataInputStream stream) throws IOException {
        char ch = (char) stream.readByte();

        if (ch != '$') throw new RuntimeException(String.format("Unexpected start of RDB file: %s", ch));

        int stringLength = parseDigits(stream);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stringLength; i++) {
            stringBuilder.append((char) stream.readByte());
        }

        return stringBuilder.toString();
    }
}