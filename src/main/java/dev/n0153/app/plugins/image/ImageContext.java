package dev.n0153.app.plugins.image;

import dev.n0153.app.MediaContext;

import java.util.HashMap;
import java.util.Map;

public class ImageContext implements MediaContext {
    private final Map<String, Object> contextStorage = new HashMap<>();

    @Override
    public void put(String key, Object value) {
        contextStorage.put(key, value);
    }

    @Override
    public <ValueType> ValueType get(String key, Class<ValueType> type) {
        return type.cast(contextStorage.get(key));
    }

    @Override
    public void release() {
        contextStorage.clear();
    }
}
