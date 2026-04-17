package dev.n0153.app;

import java.util.Map;
import java.util.function.BiConsumer;

public interface CliConfig<Config extends MediaConfig, Builder> {
    Map<String, BiConsumer<Builder, String>> paramStorage();
    Builder createBuilder(String param, String value);
    Config build(Builder builder);
}
