package dev.n0153.app.plugins.image;

import dev.n0153.app.CliConfig;

import java.util.Map;
import java.util.function.BiConsumer;

public class ImageCli implements CliConfig<ImageConfig, ImageConfig.Builder> {
    private static final Map<String, BiConsumer<ImageConfig.Builder, String>> PARAM_STORAGE = Map.of(
            "logo-size-limit", ((builder, value) -> builder.setLogoSizeLimit(Integer.parseInt(value))),
            "keep-logo", ((builder, value) -> builder.setKeepLogo(Boolean.parseBoolean(value))),
            "keep-image", ((builder, value) -> builder.setKeepImage(Boolean.parseBoolean(value))),
            "image-max-width", ((builder, value) -> builder.setImgMaxWidth(Integer.parseInt(value))),
            "image-max-height", ((builder, value) -> builder.setImgMaxHeight(Integer.parseInt(value))),
            "logo-max-width", ((builder, value) -> builder.setLogoMaxWidth(Integer.parseInt(value))),
            "logo-max-height", ((builder, value) -> builder.setLogoMaxHeight(Integer.parseInt(value)))
            );

    @Override
    public Map<String, BiConsumer<ImageConfig.Builder, String>> paramStorage() {
        return PARAM_STORAGE;
    }

    @Override
    public ImageConfig.Builder createBuilder() {
        return new ImageConfig.Builder();
    }

    @Override
    public ImageConfig build(ImageConfig.Builder builder) {
        return builder.build();
    }
}
