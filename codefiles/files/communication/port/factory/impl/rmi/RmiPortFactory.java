package com.jwa.pushlistener.code.architecture.communication.port.factory.impl.rmi;

import com.jwa.pushlistener.code.architecture.communication.port.Port;
import com.jwa.pushlistener.code.architecture.communication.port.factory.AbstractPortFactory;
import com.jwa.pushlistener.code.architecture.communication.port.config.PortConfig;
import com.jwa.pushlistener.code.architecture.communication.port.factory.PortFactorySupportedPortStyle;
import com.jwa.pushlistener.code.architecture.communication.port.factory.impl.rmi.portimpl.RmiReceiver;
import com.jwa.pushlistener.code.architecture.communication.port.factory.impl.rmi.portimpl.RmiSynchronousSender;
import com.jwa.pushlistener.code.architecture.communication.port.factory.impl.rmi.portimpl.config.RmiReceiverConfig;
import com.jwa.pushlistener.code.architecture.communication.port.factory.impl.rmi.portimpl.config.RmiSenderConfig;

@PortFactorySupportedPortStyle(name = "Rmi")
public final class RmiPortFactory implements AbstractPortFactory {
    private static final String RMI_PARAMETER_PREFIX = "rmi.";

    @Override
    public final Port createPort(final PortConfig portConfig) throws IllegalArgumentException {
        switch (portConfig.getType()) {
            case "Receiver":
                return createReceiverPort(portConfig);
            case "Sender/SynchronousSender":
                return createSynchronousSender(portConfig);
            default:
                throw portConfig.generateNoImplementationFoundForTypeException();
        }
    }

    private RmiReceiver createReceiverPort(final PortConfig portConfig) throws IllegalArgumentException {
        final int portRegistry = portConfig.getParameterInt(RMI_PARAMETER_PREFIX + "portRegistry");
        final RmiReceiverConfig receiverConfig = new RmiReceiverConfig(portRegistry);
        return new RmiReceiver(receiverConfig);
    }

    private RmiSynchronousSender createSynchronousSender(final PortConfig portConfig) throws IllegalArgumentException {
        final String hostname = portConfig.getParameter(RMI_PARAMETER_PREFIX + "hostname");
        final int portRegistry = portConfig.getParameterInt(RMI_PARAMETER_PREFIX + "portRegistry");
        final RmiSenderConfig senderConfig = new RmiSenderConfig(hostname, portRegistry);
        return new RmiSynchronousSender(senderConfig);
    }
}
