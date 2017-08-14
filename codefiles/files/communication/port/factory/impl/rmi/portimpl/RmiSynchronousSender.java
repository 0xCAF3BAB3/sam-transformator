package {{communicationPackageName}}.port.factory.impl.rmi.portimpl;

import com.google.common.base.Optional;

import {{communicationPackageName}}.Message;
import {{communicationPackageName}}.port.PortException;
import {{communicationPackageName}}.port.SynchronousSender;
import {{communicationPackageName}}.port.factory.impl.rmi.portimpl.config.RmiSenderConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class RmiSynchronousSender implements SynchronousSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(RmiSynchronousSender.class);
    private final RmiSenderConfig config;
    private RmiInterface remoteInterface;
    private boolean connected;

    public RmiSynchronousSender(final RmiSenderConfig config) {
        this.config = config;
    }

    @Override
    public final void connect() throws PortException {
        if (isConnected()) {
            throw new PortException("Already connected");
        }
        try {
            final Registry registry = LocateRegistry.getRegistry(config.getHostname(), config.getPortRegistry());
            remoteInterface = (RmiInterface) registry.lookup(config.getNameRemoteobjectRegistryLookup());
        } catch (RemoteException | NotBoundException e) {
            throw new PortException("Registry lookup failed: " + e.getMessage(), e);
        }
        connected = true;
        LOGGER.info("Connection to RMI server '" + config.getHostname() + "' on registry-port " + config.getPortRegistry() +
                " and registry-remoteobject-name '" + config.getNameRemoteobjectRegistryLookup() +
                "' is now established"
        );
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
        final Optional<Message> response;
        try {
            response = remoteInterface.execute(msg);
        } catch (RemoteException e) {
            throw new PortException("Message executing failed: " + e.getMessage(), e);
        }
        LOGGER.info("Message was executed");
        LOGGER.info("Message response received");
        return response;
    }

    @Override
    public final void disconnect() {
        if (isConnected()) {
            // Connection between RMI client and server is implicit and closes automatically after a short idle period of time.
            connected = false;
            LOGGER.info("Connection to RMI server '" + config.getHostname() + "' on registry-port " + config.getPortRegistry() +
                    " and registry-remoteobject-name '" + config.getNameRemoteobjectRegistryLookup() +
                    "' was closed"
            );
        }
    }
}
