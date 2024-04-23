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

        if (storageData.expiry().isBefore(Instant.now())){
            remove(key);
            return null;
        }

        return storageData.value();
    }

    public static void setData(Object key, String value) {
        cache.put(key, new StorageData(value, Instant.MAX));
    }

    public static void setDataWithTtl(Object key, String value, Long expiration) {
        cache.put(key, new StorageData(value, Instant.now().plusMillis(expiration)));
    }

    public static void remove(Object key) {
        cache.remove(key);
    }

    public static ConcurrentMap<Object, StorageData> getCache() {
        return cache;
    }
}
