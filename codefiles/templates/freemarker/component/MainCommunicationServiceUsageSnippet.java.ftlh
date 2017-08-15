        // example usage of CommunicationService
        final CommunicationService communicationService = new CommunicationService();
        final PortsService portsService = communicationService.getPortsService();
        try {
            communicationService.init();
            /*
            optional: set receiver-handlers, e.g.:
            portsService.setReceiverHandler(
                    CommunicationService.Receivers.PORTX.getName(),
                    msg -> {
                        ...
                        return ...;
                    }
            );
            */
            /*
            optional: set asynchronous-sender-callbacks, e.g.:
            portsService.setAsynchronousSenderCallback(
                    CommunicationService.AsynchronousSenders.PORTX.getName(),
                    msg -> {
                        ...
                    });
            */
            portsService.startReceiverPorts();
            /*
            optional: connect and execute senders, e.g.:
            portsService.connectSender(
                    CommunicationService.Senders.PORTX.getName()
            );
            portsService.executeSender(
                    CommunicationService.Senders.PORTX.getName(),
                    ...
            );
            */
        } catch (PortsServiceException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            portsService.stopPorts();
        }