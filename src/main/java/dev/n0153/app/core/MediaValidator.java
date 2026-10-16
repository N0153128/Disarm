package dev.n0153.app.core;

import java.nio.file.Path;

public interface MediaValidator {
    boolean validateFileSize(Path osTargetPath);
    boolean validate(Path osTargetPath);
}
