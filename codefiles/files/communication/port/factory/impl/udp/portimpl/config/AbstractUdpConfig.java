package {{communicationPackageName}}.port.factory.impl.udp.portimpl.config;

abstract class AbstractUdpConfig {
    private final int port;

    AbstractUdpConfig(final int port) {
        this.port = port;
    }

    public final int getPort() {
        return port;
    }
}
