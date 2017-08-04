package com.jwa.amlmodel.code.generator.generators.templates.component;

import com.google.common.base.Optional;

import com.jwa.pushlistener.code.architecture.communication.ports.PortsService;
import com.jwa.pushlistener.code.architecture.communication.ports.PortsServiceException;

public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {
        // TODO: implement me

        // example usage of CommunicationService
        try {
            final CommunicationService communicationService = new CommunicationService();
            final PortsService portsService = communicationService.getPortsService();

            // TODO: optional: set receiver-handlers on receiver-ports of ports-service

            // TODO: optional: set asynchronous-sender-callbacks on asynchronous-sender-ports of ports-service

            portsService.startReceiverPorts();

            try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            // TODO: optional: connect and execute senders

            try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            portsService.stopPorts();
        } catch (PortsServiceException | IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
