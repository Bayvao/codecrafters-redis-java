import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataModel {
    private ConcurrentHashMap<Object, String> setCommandMap = new ConcurrentHashMap<>();

    public String getSetCommandMap(String key) {
        return setCommandMap.get(key);
    }

    public void setSetCommandMap(Object key, String value) {
        this.setCommandMap.put(key, value);
    }
}
