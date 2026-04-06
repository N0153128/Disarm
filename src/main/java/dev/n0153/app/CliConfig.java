package dev.n0153.app;

import java.util.Map;
import java.util.function.BiConsumer;

public interface CliConfig<Config extends ProcessorConfig, Builder> {
    Map<String, BiConsumer<Builder, String>> paramStorage();
    Builder createBuilder();
    Config build(Builder builder);
}
