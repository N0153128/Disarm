package dev.n0153.app;

public interface MediaValidator<Config extends ProcessorConfig> {
    ValidationResult validate(ProcessingContext context, Config config);
}
