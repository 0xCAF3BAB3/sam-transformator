package {{communicationPackageName}}.port.factory.impl.udp;

import {{communicationPackageName}}.port.Port;
import {{communicationPackageName}}.port.config.PortConfig;
import {{communicationPackageName}}.port.factory.AbstractPortFactory;
import {{communicationPackageName}}.port.factory.PortFactorySupportedPortStyle;
import {{communicationPackageName}}.port.factory.impl.udp.portimpl.UdpAsynchronousSender;
import {{communicationPackageName}}.port.factory.impl.udp.portimpl.UdpReceiver;
import {{communicationPackageName}}.port.factory.impl.udp.portimpl.UdpSynchronousSender;
import {{communicationPackageName}}.port.factory.impl.udp.portimpl.config.UdpReceiverConfig;
import {{communicationPackageName}}.port.factory.impl.udp.portimpl.config.UdpSenderConfig;

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
