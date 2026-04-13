package dev.n0153.app;

import java.time.Instant;
import java.util.List;

/**
 * This interface holds context data for runtime.
 */
public class ProcessingContext {
    private final String id;
    private final String fileName;
    private final long byteSize;
    private final String mimeType;
    private final byte[] inputBuffer;
    private final String stage;
    private final String resolvedPlugin;
    private final byte[] outputBuffer;
    private final List<String> transformationsApplied;
    private final List<String> warnings;
    private final String error;
    private final Instant startedAt;
    private final Instant completedAt;
    private final MediaConfig configSnapshot;

    private ProcessingContext(Builder builder) {
        this.id = builder.id;
        this.fileName = builder.fileName;
        this.byteSize = builder.byteSize;
        this.mimeType = builder.mimeType;
        this.inputBuffer = builder.inputBuffer;
        this.stage = builder.stage;
        this.resolvedPlugin = builder.resolvedPlugin;
        this.outputBuffer = builder.outputBuffer;
        this.transformationsApplied = builder.transformationsApplied;
        this.warnings = builder.warnings;
        this.error = builder.error;
        this.startedAt = builder.startedAt;
        this.completedAt = builder.completedAt;
        this.configSnapshot = builder.configSnapshot;
    }

    public String getId() {
        return this.id;
    };

    public String getFilename() {
        return fileName;
    }

    public long getByteSize() {
        return byteSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public byte[] getInputBuffer() {
        return inputBuffer;
    }

    public String getStage() {
        return stage;
    }

    public String getResolvedPlugin() {
        return resolvedPlugin;
    }

    public byte[] getOutputBuffer() {
        return outputBuffer;
    }

    public List<String> getTransformationsApplied() {
        return transformationsApplied;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public String getError() {
        return error;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public MediaConfig getConfigSnapshot() {
        return configSnapshot;
    }

    public static class Builder {
        private String id;
        private String fileName;
        private long byteSize;
        private String mimeType;
        private byte[] inputBuffer;
        private String stage;
        private String resolvedPlugin;
        private byte[] outputBuffer;
        private List<String> transformationsApplied;
        private List<String> warnings;
        private String error;
        private Instant startedAt;
        private Instant completedAt;
        private MediaConfig configSnapshot;

        public Builder setConfigSnapshot(MediaConfig configSnapshot) {
            this.configSnapshot = configSnapshot;
            return this;
        }

        public Builder setCompletedAt(Instant completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public Builder setStartedAt(Instant startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        public Builder setError(String error) {
            this.error = error;
            return this;
        }

        public Builder setWarnings(List<String> warnings) {
            this.warnings = warnings;
            return this;
        }

        public Builder setTransformationsApplied(List<String> transformationsApplied) {
            this.transformationsApplied = transformationsApplied;
            return this;
        }

        public Builder setOutputBuffer(byte[] outputBuffer) {
            this.outputBuffer = outputBuffer;
            return this;
        }

        public Builder setResolvedPlugin(String resolvedPlugin) {
            this.resolvedPlugin = resolvedPlugin;
            return this;
        }

        public Builder setInputBuffer(byte[] inputBuffer) {
            this.inputBuffer = inputBuffer;
            return this;
        }

        public Builder setStage(String stage) {
            this.stage = stage;
            return this;
        }

        public Builder setMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder setByteSize(long byteSize) {
            this.byteSize = byteSize;
            return this;
        }

        public Builder setFilename(String filename) {
            this.fileName = filename;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public ProcessingContext build() {
            return new ProcessingContext(this);
        }
    }
}
