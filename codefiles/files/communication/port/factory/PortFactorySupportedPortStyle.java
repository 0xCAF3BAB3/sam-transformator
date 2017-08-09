package com.jwa.pushlistener.code.architecture.communication.port.factory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PortFactorySupportedPortStyle {
    String name();
}
