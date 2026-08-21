package dev.n0153.app;

import java.nio.file.Path;

public interface MediaValidator {
    boolean validateFileSize(Path osTargetPath);
    boolean validate(Path osTargetPath);
}
