package com.jwa.pushlistener.code.architecture.communication.port.factory.impl.udp;

import com.jwa.pushlistener.code.architecture.communication.port.Port;
import com.jwa.pushlistener.code.architecture.communication.port.config.PortConfig;
import com.jwa.pushlistener.code.architecture.communication.port.factory.AbstractPortFactory;
import com.jwa.pushlistener.code.architecture.communication.port.factory.PortFactorySupportedPortStyle;
import com.jwa.pushlistener.code.architecture.communication.port.factory.impl.udp.portimpl.UdpAsynchronousSender;
import com.jwa.pushlistener.code.architecture.communication.port.factory.impl.udp.portimpl.UdpReceiver;
import com.jwa.pushlistener.code.architecture.communication.port.factory.impl.udp.portimpl.UdpSynchronousSender;
import com.jwa.pushlistener.code.architecture.communication.port.factory.impl.udp.portimpl.config.UdpReceiverConfig;
import com.jwa.pushlistener.code.architecture.communication.port.factory.impl.udp.portimpl.config.UdpSenderConfig;

@PortFactorySupportedPortStyle(name = "Udp")
public final class UdpPortFactory implements AbstractPortFactory {
    private static final String UDP_PARAMETER_PREFIX = "udp.";

    @Override
    public final Port createPort(final PortConfig portConfig) throws IllegalArgumentException {
        switch (portConfig.getType()) {
            case "Receiver":
                return createReceiverPort(portConfig);
            case "Sender/SynchronousSender":
                return createSynchronousSender(portConfig);
            case "Sender/AsynchronousSender":
                return createAsynchronousSender(portConfig);
            default:
                throw portConfig.generateNoImplementationFoundForTypeException();
        }
    }

    private UdpReceiver createReceiverPort(final PortConfig portConfig) throws IllegalArgumentException {
        final int port = portConfig.getParameterInt(UDP_PARAMETER_PREFIX + "port");
        final UdpReceiverConfig receiverConfig = new UdpReceiverConfig(port);
        return new UdpReceiver(receiverConfig);
    }

    private UdpSynchronousSender createSynchronousSender(final PortConfig portConfig) throws IllegalArgumentException {
        final String hostname = portConfig.getParameter(UDP_PARAMETER_PREFIX + "hostname");
        final int port = portConfig.getParameterInt(UDP_PARAMETER_PREFIX + "port");
        final UdpSenderConfig senderConfig = new UdpSenderConfig(hostname, port);
        return new UdpSynchronousSender(senderConfig);
    }

    private UdpAsynchronousSender createAsynchronousSender(final PortConfig portConfig) throws IllegalArgumentException {
        final String hostname = portConfig.getParameter(UDP_PARAMETER_PREFIX + "hostname");
        final int port = portConfig.getParameterInt(UDP_PARAMETER_PREFIX + "port");
        final UdpSenderConfig senderConfig = new UdpSenderConfig(hostname, port);
        return new UdpAsynchronousSender(senderConfig);
    }
}
