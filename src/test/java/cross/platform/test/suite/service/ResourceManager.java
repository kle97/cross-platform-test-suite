package cross.platform.test.suite.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class ResourceManager<K, V> {
    
    private final Map<K, V> unclaimedResources;
    private final Map<K, V> claimedResources;
    
    public ResourceManager(Map<K, V> resourcePool) {
        this.unclaimedResources = resourcePool;
        this.claimedResources = new HashMap<>();
    }
    
    protected abstract boolean isResourceAvailable(V resource);
    
    public synchronized V getResource() {
        return getResource(null);
    }

    public synchronized V getResource(K key) {
        if (key != null) {
            return getFreeResource(key);
        } else {
            return getFreeResource();
        }
    }

    protected synchronized V getFreeResource() {
        for (Iterator<Map.Entry<K, V>> it = unclaimedResources.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<K, V> entry = it.next();
            K key = entry.getKey();
            V resource = entry.getValue();
            it.remove();
            claimedResources.put(key, resource);
            if (isResourceAvailable(resource)) {
                return resource;
            }
        }
        
        throw new NoSuchElementException("No resources are available!");
    }

    protected synchronized V getFreeResource(K key) {
        if (unclaimedResources.containsKey(key)) {
            V resource = unclaimedResources.remove(key);
            claimedResources.put(key, resource);
            if (isResourceAvailable(resource)) {
                return resource;
            }
        }

        throw new NoSuchElementException(String.format("Request resource '%s' is not found or available!", key));
    }
    
}
