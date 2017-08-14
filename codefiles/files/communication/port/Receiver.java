package {{communicationPackageName}}.port;

public interface Receiver extends Port {
    /**
     * If no handler is set, a default-handler gets set (which only logs the call).
     * @param handler must not be null
     */
    void setHandler(final ReceiverHandler handler);

    /**
     *
     * @throws PortException if starting is not possible or already started
     */
    void start() throws PortException;

    boolean isStarted();

    /**
     * Shutdown.
     * If already shutdown: no operation.
     */
    void shutdown();
}
