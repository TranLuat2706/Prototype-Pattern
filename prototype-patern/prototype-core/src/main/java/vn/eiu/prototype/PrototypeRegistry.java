package vn.eiu.prototype;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
public class PrototypeRegistry {
    private Map<String, Prototype> registry = new HashMap<>();
    public void registerPrototype(String key, Prototype prototype) {
        registry.put(key.toLowerCase(), prototype);
    }
    public Prototype getPrototype(String key) {
        if (key == null) return null;
        return registry.get(key.toLowerCase());
    }
    public Set<String> keys() { return registry.keySet(); }
}
