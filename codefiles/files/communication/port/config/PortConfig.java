package com.jwa.pushlistener.code.architecture.communication.port.config;

import java.util.Map;

public final class PortConfig {
    private final String style;
    private final String type;
    private final Map<String, String> parameters;

    PortConfig(final String style, final String type, final Map<String, String> parameters) {
        this.style = style;
        this.type = type;
        this.parameters = parameters;
    }

    public final String getStyle() {
        return style;
    }

    public final String getType() {
        return type;
    }

    public final String getParameter(final String key) throws IllegalArgumentException {
        if (!parameters.containsKey(key)) {
            throw new IllegalArgumentException("Parameter '" + key + "' is missing");
        }
        final String value = parameters.get(key);
        if (value == null || value.isEmpty()) {
            throw generateInvalidParameterValueException(key);
        }
        return value;
    }

    public final int getParameterInt(final String key) throws IllegalArgumentException {
        final int value;
        try {
            value = Integer.parseInt(getParameter(key));
        } catch (NumberFormatException e) {
            throw generateInvalidParameterValueException(key);
        }
        return value;
    }

    public final IllegalArgumentException generateNoImplementationFoundForStyleException() {
        return new IllegalArgumentException("No implementation found for style '" + style + "'");
    }

    public final IllegalArgumentException generateNoImplementationFoundForTypeException() {
        return new IllegalArgumentException("No implementation found for type '" + type + "'");
    }

    private static IllegalArgumentException generateInvalidParameterValueException(final String parameterKey) {
        return new IllegalArgumentException("Parameter '" + parameterKey + "' has invalid value");
    }
}
