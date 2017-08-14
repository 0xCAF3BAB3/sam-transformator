package {{communicationPackageName}}.port.factory.impl.rmi.portimpl;

import com.google.common.base.Optional;

import {{communicationPackageName}}.Message;
import {{communicationPackageName}}.port.PortException;
import {{communicationPackageName}}.port.ReceiverHandler;
import {{communicationPackageName}}.port.Receiver;
import {{communicationPackageName}}.port.factory.impl.rmi.portimpl.config.RmiReceiverConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public final class RmiReceiver implements Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(RmiReceiver.class);
    private final RmiReceiverConfig config;
    private ReceiverHandler handler;
    private RMIServerRunnable rmiServerRunnable;
    private boolean started;

    public RmiReceiver(final RmiReceiverConfig config) {
        this.config = config;
        this.handler = msg -> {
            LOGGER.debug("Default receiver-handler got called");
            return Optional.absent();
        };
    }

    @Override
    public final void setHandler(final ReceiverHandler handler) {
        this.handler = handler;
    }

    @Override
    public final void start() throws PortException {
        if (isStarted()) {
            throw new PortException("Already started");
        }
        rmiServerRunnable = new RMIServerRunnable();
        final Thread t = new Thread(rmiServerRunnable, "RmiReceiver{portRegistry=" + config.getPortRegistry() + "}");
        t.start();
        started = true;
        LOGGER.info("RmiReceiver started");
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public final void shutdown() {
        if (isStarted()) {
            rmiServerRunnable.shutdown();
            started = false;
            LOGGER.info("RmiReceiver{portRegistry=" + config.getPortRegistry() + ", nameRemoteobjectRegistryLookup='" +
                    config.getNameRemoteobjectRegistryLookup() + "'} was shutdown"
            );
        }
    }

    private final class RMIServerRunnable implements Runnable {
        private final class DelegateRmiInterface extends UnicastRemoteObject implements RmiInterface {
            private DelegateRmiInterface() throws RemoteException {}

            @Override
            public final Optional<Message> execute(final Message msg) throws RemoteException {
                return handler.handle(msg);
            }
        }

        private Registry registry;
        private Remote delegateInterfaceImpl;

        @Override
        public final void run() {
            try {
                registry = LocateRegistry.createRegistry(config.getPortRegistry());
                delegateInterfaceImpl = new DelegateRmiInterface();
                registry.bind(config.getNameRemoteobjectRegistryLookup(), delegateInterfaceImpl);
            } catch (RemoteException | AlreadyBoundException e) {
                LOGGER.error("Registry setup failed: " + e.getMessage(), e);
                return;
            }
            LOGGER.info("RmiReceiver{portRegistry=" + config.getPortRegistry() + ", nameRemoteobjectRegistryLookup='" +
                    config.getNameRemoteobjectRegistryLookup() + "'} is now running"
            );
        }

        final void shutdown() {
            if (registry != null) {
                try {
                    registry.unbind(config.getNameRemoteobjectRegistryLookup());
                } catch (RemoteException | NotBoundException e) {
                    LOGGER.warn("Removing registry binding for interface failed: " + e.getMessage(), e);
                }
                try {
                    UnicastRemoteObject.unexportObject(registry, true);
                } catch (NoSuchObjectException e) {
                    LOGGER.warn("Removing registry from RMI runtime failed: " + e.getMessage(), e);
                }
            }
            if (delegateInterfaceImpl != null) {
                try {
                    UnicastRemoteObject.unexportObject(delegateInterfaceImpl, true);
                } catch (NoSuchObjectException e) {
                    LOGGER.warn("Removing interface from RMI runtime failed: " + e.getMessage(), e);
                }
            }
        }
    }
}
