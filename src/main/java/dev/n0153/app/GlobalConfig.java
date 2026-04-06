package dev.n0153.app;

import java.nio.file.Path;

public class GlobalConfig {
    private final int generalSizeLimit;
    private final boolean keepInputs;
    private final boolean keepOriginal;
    private final int generalFileSizeUpperBoundLimit;
    private final Path generalOutputPath;
    private final int targetFileLength;

    private GlobalConfig(BUilder builder) {
        this.generalSizeLimit = builder.generalSizeLimit;
        this.keepInputs = builder.keepInputs;
        this.keepOriginal = builder.keepOriginal;
        this.generalFileSizeUpperBoundLimit = builder.generalFileSizeUpperBoundLimit;
        this.generalOutputPath = builder.generalOutputPath;
        this.targetFileLength = builder.targetFileLength;
    }

    class Builder {
        public GlobalConfig build() {
            return new GlobalConfig(this);
        }
    }

}
