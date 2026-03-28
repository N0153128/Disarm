package dev.n0153.app.plugins.image;

import dev.n0153.app.ProcessorConfig;

public class ImageConfig implements ProcessorConfig {
    @Override
    public int getId() {
        return 0;
    }

    @Override
    public double getVersion() {
        return 0;
    }

    @Override
    public int maxFileSizeInBytes(String mime) {
        return 0;
    }

    @Override
    public String getLogLevel() {
        return "";
    }
}
