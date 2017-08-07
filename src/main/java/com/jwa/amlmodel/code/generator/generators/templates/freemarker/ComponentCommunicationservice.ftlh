package ${packageName};

import ${communicationPackageName}.ports.PortsService;
import ${communicationPackageName}.ports.PortsServiceException;

public final class CommunicationService {
    public enum Receivers {
        ;

        private final String name;
        Receivers(final String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    public enum Senders {
        ;

        private final String name;
        Senders(final String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    public enum SynchronousSenders {
        ;

        private final String name;
        SynchronousSenders(final String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    public enum AsynchronousSenders {
        ;

        private final String name;
        AsynchronousSenders(final String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    private final PortsService portsService;

    public CommunicationService() {
        this.portsService = new PortsService();
    }

    public final void init() throws PortsServiceException {
    }

    public final PortsService getPortsService() {
        return portsService;
    }
}
