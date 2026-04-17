package dev.n0153.app.plugins.image;

import dev.n0153.app.CliConfig;

import java.util.Map;
import java.util.function.BiConsumer;

public class ImageCli implements CliConfig<ImageConfig> {
    private static final Map<String, BiConsumer<ImageConfig, String>> PARAM_STORAGE = Map.of(
            "--logo-size-limit", ((config, value) -> config.setLogoSizeLimit(Integer.parseInt(value))),
            "--keep-logo", ((config, value) -> config.setKeepLogo(Boolean.parseBoolean(value))),
            "--keep-image", ((config, value) -> config.setKeepImage(Boolean.parseBoolean(value))),
            "--image-max-width", ((config, value) -> config.setImgMaxWidth(Integer.parseInt(value))),
            "--image-max-height", ((config, value) -> config.setImgMaxHeight(Integer.parseInt(value))),
            "--logo-max-width", ((config, value) -> config.setLogoMaxWidth(Integer.parseInt(value))),
            "--logo-max-height", ((config, value) -> config.setLogoMaxHeight(Integer.parseInt(value)))
            );

    @Override
    public Map<String, BiConsumer<ImageConfig, String>> paramStorage() {
        return PARAM_STORAGE;
    }
}
