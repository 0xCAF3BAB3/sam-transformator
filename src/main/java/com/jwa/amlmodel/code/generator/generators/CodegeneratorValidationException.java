package com.jwa.amlmodel.code.generator.generators;

public final class CodegeneratorValidationException extends CodegeneratorException {
    public CodegeneratorValidationException(final String message) {
        super(message);
    }

    public CodegeneratorValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
