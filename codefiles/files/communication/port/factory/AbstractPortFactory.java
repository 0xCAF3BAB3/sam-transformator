package com.jwa.pushlistener.code.architecture.communication.port.factory;

import com.jwa.pushlistener.code.architecture.communication.port.Port;
import com.jwa.pushlistener.code.architecture.communication.port.config.PortConfig;

public interface AbstractPortFactory {
    Port createPort(final PortConfig portConfig) throws IllegalArgumentException;
}
