import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProtocolParser {
    private ProtocolParser() {}
    public static String parseInput(DataInputStream inputStream) {
        try {
            char c = (char) inputStream.readByte();
            System.out.println(c);
            return switch (c) {
                case '+' -> parseSimpleString(inputStream);
                case '*' -> parseArray(inputStream);
                case '$' -> parseString(inputStream);
                default -> throw new RuntimeException("Unknown character: " + c);
            };
        } catch(IOException e){
                throw new RuntimeException(e);
        }
    }

    private static String parseSimpleString(DataInputStream inputStream) throws IOException {
        StringBuilder parsedData = new StringBuilder();
        char c;
        while ((c = (char) inputStream.readByte()) != '\r') {
            System.out.println("here 4: " + c);
            parsedData.append((char) c);
        }
        return parsedData.toString();
    }

    private static String parseArray(DataInputStream inputStream) throws IOException {
        int arraySize = parseDigits(inputStream);
        return IntStream.range(0, arraySize)
                .mapToObj(i -> parseInput(inputStream))
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
}