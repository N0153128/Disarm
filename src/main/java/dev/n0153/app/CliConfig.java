package dev.n0153.app;

import java.util.Map;
import java.util.function.BiConsumer;

public interface CliConfig<Config extends MediaConfig> {
    Map<String, BiConsumer<Config, String>> paramStorage();
}
