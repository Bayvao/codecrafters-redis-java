import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Cache {
    private static final ConcurrentMap<Object, StorageData> cache = new ConcurrentHashMap<>();

    public static String getData(String key) {
        StorageData storageData = cache.get(key);
        if (storageData == null)
            return null;

        if (storageData.expiry().isBefore(Instant.now()))
            return null;

        return storageData.value();
    }

    public static void setData(Object key, String value) {
        setDataWithTtl(key, value, Instant.MAX);
    }

    public static void setDataWithTtl(Object key, String value, Instant expiration) {
        cache.put(key, new StorageData(value, expiration));
    }
}
