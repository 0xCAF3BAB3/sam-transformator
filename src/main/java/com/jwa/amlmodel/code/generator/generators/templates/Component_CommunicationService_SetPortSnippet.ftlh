        portsService.setPort(SynchronousSenders.PORT3.name(),
                new PortConfigBuilder()
                        .setStyle("Rmi")
                        .setType("Sender/SynchronousSender")
                        .setParameter("Rmi.hostname", "127.0.0.1")
                        .setParameter("Rmi.portRegistry", "11023")
                        .build()
        );