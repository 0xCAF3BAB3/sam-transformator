package {{communicationPackageName}}.port.factory;

import {{communicationPackageName}}.port.Port;
import {{communicationPackageName}}.port.config.PortConfig;

public interface AbstractPortFactory {
    Port createPort(final PortConfig portConfig) throws IllegalArgumentException;
}
