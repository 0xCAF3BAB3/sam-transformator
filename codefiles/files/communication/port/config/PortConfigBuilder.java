package {{communicationPackageName}}.port.config;

import java.util.HashMap;
import java.util.Map;

public final class PortConfigBuilder {
    private String style;
    private String type;
    private final Map<String, String> parameters;

    public PortConfigBuilder() {
        this.parameters = new HashMap<>();
    }

    public final PortConfigBuilder setStyle(final String style) {
        this.style = style;
        return this;
    }

    public final PortConfigBuilder setType(final String type) {
        this.type = type;
        return this;
    }

    public final PortConfigBuilder setParameter(final String key, final String value) {
        parameters.put(key, value);
        return this;
    }

    public final PortConfig build() throws IllegalArgumentException {
        final PortConfig portConfig = new PortConfig(style, type, parameters);
        final String style = portConfig.getStyle();
        if (style == null || style.isEmpty()) {
            throw new IllegalArgumentException("Passed style is missing or invalid");
        }
        final String type = portConfig.getType();
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Passed type is missing or invalid");
        }
        return portConfig;
    }
}
