package com.jwa.pushlistener.code.architecture.communication.port;

public final class PortException extends Exception {
    public PortException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PortException(final String message) {
        super(message);
    }
}
