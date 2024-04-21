import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Cache {
    private static final ConcurrentMap<Object, String> cache = new ConcurrentHashMap<>();

    public static String getData(String key) {
        return cache.get(key);
    }

    public static void setData(Object key, String value) {
        cache.put(key, value);
    }
}
