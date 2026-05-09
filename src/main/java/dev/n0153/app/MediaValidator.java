package dev.n0153.app;

import java.nio.file.Path;

public interface MediaValidator {
    boolean validate(Path osTargetPath);
}
