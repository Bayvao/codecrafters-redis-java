import java.util.HashMap;
import java.util.Map;

public class DataModel {
    private static final Map<Object, String> setCommandMap = new HashMap<>();

    public static String getSetCommandMap(String key) {
        return setCommandMap.get(key);
    }

    public static void setSetCommandMap(Object key, String value) {
        setCommandMap.put(key, value);
    }
}
