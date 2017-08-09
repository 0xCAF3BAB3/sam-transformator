package com.jwa.pushlistener.code.architecture.communication.port.factory.impl.rmi.portimpl.config;

public final class RmiSenderConfig extends AbstractRmiConfig {
    private final String hostname;

    public RmiSenderConfig(final String hostname, final int portRegistry) {
        super(portRegistry);
        this.hostname = hostname;
    }

    public final String getHostname() {
        return hostname;
    }
}
