import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DataModel {
    private ConcurrentMap<Object, String> setCommandMap = new ConcurrentHashMap<>();

    public String getSetCommandMap(String key) {
        return setCommandMap.get(key);
    }

    public void setSetCommandMap(Object key, String value) {
        this.setCommandMap.put(key, value);
    }
}
