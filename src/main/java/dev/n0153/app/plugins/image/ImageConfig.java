package dev.n0153.app.plugins.image;

import dev.n0153.app.ProcessorConfig;

public class ImageConfig implements ProcessorConfig {

    private final String name = "Image";
    private final int id = 1;
    private final int version = 1;
    private final int maxFileSizeInBytes = 5_000_000;
    private final String logLevel = "info";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public double getVersion() {
        return version;
    }

    @Override
    public int maxFileSizeInBytes(String mime) {
        return maxFileSizeInBytes;
    }

    @Override
    public String getLogLevel() {
        return logLevel;
    }


}
