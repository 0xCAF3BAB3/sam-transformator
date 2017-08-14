package {{communicationPackageName}}.ports;

import com.google.common.base.Optional;

import {{communicationPackageName}}.port.config.PortConfig;
import {{communicationPackageName}}.port.factory.PortFactory;
import {{communicationPackageName}}.Message;
import {{communicationPackageName}}.port.AsynchronousSender;
import {{communicationPackageName}}.port.AsynchronousSenderCallback;
import {{communicationPackageName}}.port.Port;
import {{communicationPackageName}}.port.PortException;
import {{communicationPackageName}}.port.Receiver;
import {{communicationPackageName}}.port.ReceiverHandler;
import {{communicationPackageName}}.port.Sender;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PortsService {
    private final Map<String, Port> ports;
    private final PortFactory portFactory;

    public PortsService() {
        this.ports = new LinkedHashMap<>();
        this.portFactory = new PortFactory();
    }

    public final void setPort(final String portName, final PortConfig portConfig) throws PortsServiceException {
        if (portName == null || portName.isEmpty()) {
            throw new PortsServiceException("Passed port-name is invalid");
        }
        final Port port;
        try {
            port = portFactory.createPort(portConfig);
        } catch (IllegalArgumentException e) {
            throw new PortsServiceException("Port creation failed: " + e.getMessage(), e);
        }
        ports.put(portName, port);
    }

    public final void setReceiverHandler(final String portName, final ReceiverHandler handler) throws PortsServiceException {
        if (handler == null) {
            throw new PortsServiceException("Passed handler is null");
        }
        final Receiver receiver = getReceiverByName(portName);
        receiver.setHandler(handler);
    }

    public final void setAsynchronousSenderCallback(final String portName, final AsynchronousSenderCallback callback) throws PortsServiceException {
        if (callback == null) {
            throw new PortsServiceException("Passed callback is null");
        }
        final AsynchronousSender sender = getAsynchronousSenderByName(portName);
        sender.setCallback(callback);
    }

    public final void startReceiverPort(final String portName) throws PortsServiceException {
        final Receiver receiver = getReceiverByName(portName);
        startReceiverPort(receiver);
    }

    public final void startReceiverPorts() throws PortsServiceException {
        for(Map.Entry<String, Receiver> receiver : getReceivers().entrySet()) {
            try {
                startReceiverPort(receiver.getValue());
            } catch (PortsServiceException e) {
                throw new PortsServiceException("Starting receiver-port '" + receiver.getKey() + "' failed: " + e.getMessage(), e);
            }
        }
    }

    private void startReceiverPort(final Receiver receiver) throws PortsServiceException {
        if (!receiver.isStarted()) {
            try {
                receiver.start();
            } catch (PortException e) {
                throw new PortsServiceException("Starting receiver-port failed: " + e.getMessage(), e);
            }
        }
    }

    public final void stopReceiverPort(final String portName) throws PortsServiceException {
        final Receiver receiver = getReceiverByName(portName);
        stopReceiverPort(receiver);
    }

    public final void stopReceiverPorts() {
        for(Receiver receiver : getReceivers().values()) {
            stopReceiverPort(receiver);
        }
    }

    private void stopReceiverPort(final Receiver receiver) {
        if (receiver.isStarted()) {
            receiver.shutdown();
        }
    }

    public final void stopPorts() {
        stopReceiverPorts();
        for(Sender sender : getSenders().values()) {
            if (sender.isConnected()) {
                sender.disconnect();
            }
        }
    }

    public final void connectSender(final String portName) throws PortsServiceException {
        final Sender sender = getSenderByName(portName);
        try {
            sender.connect();
        } catch (PortException e) {
            throw new PortsServiceException("Connecting sender-port '" + portName + "' failed: " + e.getMessage(), e);
        }
    }

    public final Optional<Message> executeSender(final String portName, final Message msg) throws PortsServiceException {
        if (msg == null) {
            throw new PortsServiceException("Passed message is null");
        }
        final Sender sender = getSenderByName(portName);
        try {
            return sender.execute(msg);
        } catch (PortException e) {
            throw new PortsServiceException("Executing sender-port '" + portName + "' failed: " + e.getMessage(), e);
        }
    }

    private Port getPortByName(final String portName) throws PortsServiceException {
        final Port port = ports.get(portName);
        if (port == null) {
            throw new PortsServiceException("Passed port not found");
        }
        return port;
    }

    private Sender getSenderByName(final String portName) throws PortsServiceException {
        final Port port = getPortByName(portName);
        if (port instanceof Sender) {
             return (Sender) port;
        } else {
            throw new PortsServiceException("Passed port is not from type Sender");
        }
    }

    private AsynchronousSender getAsynchronousSenderByName(final String portName) throws PortsServiceException {
        final Port port = getPortByName(portName);
        if (port instanceof AsynchronousSender) {
            return (AsynchronousSender) port;
        } else {
            throw new PortsServiceException("Passed port is not from type AsynchronousSender");
        }
    }

    private Receiver getReceiverByName(final String portName) throws PortsServiceException {
        final Port port = getPortByName(portName);
        if (port instanceof Receiver) {
            return (Receiver) port;
        } else {
            throw new PortsServiceException("Passed port is not from type Receiver");
        }
    }

    private Map<String, Receiver> getReceivers() {
        Map<String, Receiver> receivers = new LinkedHashMap<>();
        for(Map.Entry<String, Port> port : ports.entrySet()) {
            if (port.getValue() instanceof Receiver) {
                receivers.put(port.getKey(), (Receiver) port.getValue());
            }
        }
        return Collections.unmodifiableMap(receivers);
    }

    private Map<String, Sender> getSenders() {
        Map<String, Sender> senders = new LinkedHashMap<>();
        for(Map.Entry<String, Port> port : ports.entrySet()) {
            if (port.getValue() instanceof Sender) {
                senders.put(port.getKey(), (Sender) port.getValue());
            }
        }
        return Collections.unmodifiableMap(senders);
    }
}
