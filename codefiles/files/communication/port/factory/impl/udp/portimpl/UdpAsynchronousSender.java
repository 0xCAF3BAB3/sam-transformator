package com.jwa.pushlistener.code.architecture.communication.port.factory.impl.udp.portimpl;

import com.google.common.base.Optional;

import com.jwa.pushlistener.code.architecture.communication.Message;
import com.jwa.pushlistener.code.architecture.communication.port.PortException;
import com.jwa.pushlistener.code.architecture.communication.port.AsynchronousSender;
import com.jwa.pushlistener.code.architecture.communication.port.AsynchronousSenderCallback;
import com.jwa.pushlistener.code.architecture.communication.port.factory.impl.udp.portimpl.config.UdpSenderConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class UdpAsynchronousSender implements AsynchronousSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(UdpAsynchronousSender.class);
    private final UdpSenderConfig config;
    private AsynchronousSenderCallback callback;
    private UdpServer udpServer;
    private boolean connected;

    public UdpAsynchronousSender(final UdpSenderConfig config) {
        this.config = config;
        this.callback = msg -> {
            LOGGER.debug("Default asynchronous-sender-callback got called");
        };
    }

    @Override
    public final void setCallback(final AsynchronousSenderCallback callback) {
        this.callback = callback;
        if (isConnected()) {
            udpServer.setCallback(callback);
        }
    }

    @Override
    public final void connect() throws PortException {
        if (isConnected()) {
            throw new PortException("Already connected");
        }
        udpServer = new UdpServer(config.getPort(), callback);
        udpServer.start();
        connected = true;
    }

    @Override
    public final boolean isConnected() {
        return connected;
    }

    @Override
    public final Optional<Message> execute(final Message msg) throws PortException {
        if (!isConnected()) {
            throw new PortException("Not connected");
        }
        try {
            final InetSocketAddress recipient = new InetSocketAddress(config.getHostname(), config.getPort());
            UdpUtils.send(msg, recipient, udpServer.getDatagramSocket());
        } catch (IOException e) {
            throw new PortException("Message sending failed: " + e.getMessage(), e);
        }
        LOGGER.info("Message was executed");
        return Optional.absent();
    }

    @Override
    public final void disconnect() {
        if (isConnected()) {
            udpServer.shutdown();
            connected = false;
        }
    }
}
