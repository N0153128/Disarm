package dev.n0153.app;

import java.nio.file.Path;

public class GlobalConfig {
    private final int generalSizeLimit;
    private final boolean keepInputs;
    private final boolean keepOriginal;
    private final int generalFileSizeUpperBoundLimit;
    private final Path generalOutputPath;
    private final int targetFileLength;

    private GlobalConfig(Builder builder) {
        this.generalSizeLimit = builder.generalSizeLimit;
        this.keepInputs = builder.keepInputs;
        this.keepOriginal = builder.keepOriginal;
        this.generalFileSizeUpperBoundLimit = builder.generalFileSizeUpperBoundLimit;
        this.generalOutputPath = builder.generalOutputPath;
        this.targetFileLength = builder.targetFileLength;
    }

    public int getGeneralFileSizeUpperBoundLimit() {
        return generalFileSizeUpperBoundLimit;
    }

    public boolean getKeepInputs() {
        return keepInputs;
    }

    public boolean isKeepOriginal() {
        return keepOriginal;
    }

    public Path getGeneralOutputPath() {
        return generalOutputPath;
    }

    public int getTargetFileLength() {
        return targetFileLength;
    }

    public int getGeneralSizeLimit() {
        return  generalSizeLimit;
    }

    public static class Builder {

        private int generalSizeLimit;
        private boolean keepInputs;
        private boolean keepOriginal;
        private int generalFileSizeUpperBoundLimit;
        private Path generalOutputPath;
        private int targetFileLength;

        public GlobalConfig build() {
            return new GlobalConfig(this);
        }

        public Builder setKeepInputs(boolean newKeepInputs) {
            keepInputs = newKeepInputs;
            return this;
        }

        public Builder setKeepOriginal(boolean keepOriginal) {
            this.keepOriginal = keepOriginal;
            return this;
        }

        public Builder setGeneralSizeLimit(int newSizeLimit) {
            if (newSizeLimit < 0) {
                throw new IllegalArgumentException("Size limit cannot be less than zero");
            }
            if (newSizeLimit > generalFileSizeUpperBoundLimit) {
                throw new IllegalArgumentException("Size limit cannot exceed upper bound");
            }
            generalSizeLimit = newSizeLimit;
            return this;
        }

        public Builder setGeneralOutputPath(Path generalOutputPath) {
            if (generalOutputPath == null) {
                throw new IllegalArgumentException("General output path cannot be empty");
            }
            this.generalOutputPath = generalOutputPath;
            return this;
        }

        private Builder setTargetFileLength(int newTargetFileLength) {
            if (newTargetFileLength < 0) {
                throw new IllegalArgumentException("Target file length cannot be less than zero");
            }
            targetFileLength = newTargetFileLength;
            return this;
        }
    }
}
