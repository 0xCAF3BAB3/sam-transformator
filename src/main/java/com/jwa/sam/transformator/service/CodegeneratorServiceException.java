package com.jwa.sam.transformator.service;

public class CodegeneratorServiceException extends Exception {
    public CodegeneratorServiceException(final String message) {
        super(message);
    }

    public CodegeneratorServiceException(final Throwable cause) {
        super(cause);
    }

    public CodegeneratorServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
