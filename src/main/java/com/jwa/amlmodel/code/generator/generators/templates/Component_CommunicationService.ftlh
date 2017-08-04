package com.jwa.amlmodel.code.generator.generators.templates.component;

import com.jwa.pushlistener.code.architecture.communication.port.config.PortConfigBuilder;
import com.jwa.pushlistener.code.architecture.communication.port.factory.PortFactory;
import com.jwa.pushlistener.code.architecture.communication.port.factory.config.PortFactoryConfig;
import com.jwa.pushlistener.code.architecture.communication.port.factory.config.PortFactoryConfigBuilder;
import com.jwa.pushlistener.code.architecture.communication.port.factory.impl.rmi.RmiPortFactory;
import com.jwa.pushlistener.code.architecture.communication.ports.PortsService;

public final class CommunicationService {
    public enum Receivers {
        <#-- values get added via PortCodeGenerator -->
    }

    public enum Senders {
        <#-- values get added via PortCodeGenerator -->
    }

    public enum SynchronousSenders {
        <#-- values get added via PortCodeGenerator -->
    }

    public enum AsynchronousSenders {
        <#-- values get added via PortCodeGenerator -->
    }

    private final PortsService portsService;

    public CommunicationService() throws IllegalArgumentException {
        final PortFactoryConfig portFactoryConfig = new PortFactoryConfigBuilder()
                <#-- factories get added via PortCodeGenerator -->
                .build();
        final PortFactory portFactory = new PortFactory(portFactoryConfig);
        this.portsService = new PortsService(portFactory);
        init();
    }

    public final PortsService getPortsService() {
        return portsService;
    }

    private void init() throws IllegalArgumentException {
        portsService.setPort(SynchronousSenders.PORT1.name(),
                new PortConfigBuilder()
                        .setStyle("Rmi")
                        .setType("Sender/SynchronousSender")
                        .setParameter("Rmi.hostname", "127.0.0.1")
                        .setParameter("Rmi.portRegistry", "11021")
                        .build()
        );
        portsService.setPort(Receivers.PORT2.name(),
                new PortConfigBuilder()
                        .setStyle("Rmi")
                        .setType("Receiver")
                        .setParameter("Rmi.portRegistry", "11012")
                        .build()
        );
        portsService.setPort(SynchronousSenders.PORT3.name(),
                new PortConfigBuilder()
                        .setStyle("Rmi")
                        .setType("Sender/SynchronousSender")
                        .setParameter("Rmi.hostname", "127.0.0.1")
                        .setParameter("Rmi.portRegistry", "11023")
                        .build()
        );
        portsService.setPort(SynchronousSenders.PORT4.name(),
                new PortConfigBuilder()
                        .setStyle("Rmi")
                        .setType("Sender/SynchronousSender")
                        .setParameter("Rmi.hostname", "127.0.0.1")
                        .setParameter("Rmi.portRegistry", "11033")
                        .build()
        );
        <#-- ports get set via PortCodeGenerator -->
    }
}
