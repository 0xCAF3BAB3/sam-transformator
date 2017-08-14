package {{communicationPackageName}}.port.factory;

import {{communicationPackageName}}.port.Port;
import {{communicationPackageName}}.port.config.PortConfig;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Port-factory implementations get loaded via reflection and must:
 *  o implement interface 'AbstractPortFactory'
 *  o have a zero argument constructor
 *  o have annotation 'PortFactorySupportedPortStyle' set
 *  o be located somewhere under the package <PACKAGE_PORTFACTORY_IMPLEMENTATIONS>
 */
public final class PortFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortFactory.class);
    private static final String PACKAGE_PORTFACTORY_IMPLEMENTATIONS = AbstractPortFactory.class.getPackage().getName() + ".impl";
    private final Map<String, AbstractPortFactory> loadedFactories;

    public PortFactory() {
        this.loadedFactories = new HashMap<>();
    }

    public final Port createPort(final PortConfig portConfig) throws IllegalArgumentException {
        if (portConfig == null) {
            throw new IllegalArgumentException("Passed port-config is null");
        }
        final String portStyleName = portConfig.getStyle();
        AbstractPortFactory factory = loadedFactories.get(portStyleName);
        if (factory == null) {
            boolean loaded = loadFactoryFor(portStyleName);
            if (loaded) {
                factory = loadedFactories.get(portStyleName);
            } else {
                throw portConfig.generateNoImplementationFoundForStyleException();
            }
        }
        return factory.createPort(portConfig);
    }

    private boolean loadFactoryFor(final String portStyleName) {
        final Reflections reflections = new Reflections(PACKAGE_PORTFACTORY_IMPLEMENTATIONS);
        for(Class<?> foundFactoryClass : reflections.getTypesAnnotatedWith(PortFactorySupportedPortStyle.class)) {
            final String foundFactorySupportedPortStyle = foundFactoryClass.getAnnotation(PortFactorySupportedPortStyle.class).name();
            if (foundFactorySupportedPortStyle.equals(portStyleName)) {
                if (!AbstractPortFactory.class.isAssignableFrom(foundFactoryClass)) {
                    throw new RuntimeException("Found class '" + foundFactoryClass.getName() + "' is not a valid port-factory implementation");
                }
                final AbstractPortFactory factoryInstance;
                try {
                    factoryInstance = (AbstractPortFactory) foundFactoryClass.newInstance();
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException("Instantiating found class '" + foundFactoryClass.getName() + "' failed", e);
                }
                loadedFactories.put(foundFactorySupportedPortStyle, factoryInstance);
                LOGGER.debug("Port-factory implementation '" + foundFactorySupportedPortStyle + "' loaded");
                return true;
            }
        }
        return false;
    }
}
