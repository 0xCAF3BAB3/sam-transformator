package {{communicationPackageName}}.ports;

public final class PortsServiceException extends Exception {
    public PortsServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PortsServiceException(final String message) {
        super(message);
    }
}
