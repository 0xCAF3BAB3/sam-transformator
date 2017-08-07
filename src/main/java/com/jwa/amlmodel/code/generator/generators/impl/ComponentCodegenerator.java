package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedComponentConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedServiceConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ComponentCodegenerator implements Codegenerator<GeneratedComponentConfig, GeneratedServiceConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentCodegenerator.class);

    @Override
    public final GeneratedComponentConfig generate(final InternalElement node, final GeneratedServiceConfig parentConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        LOGGER.trace("Generating component for node '" + node.getName() + "' ...");

        // TODO: generates Maven component module based on the passed data

        /*
        for(InternalElement internalElement : node.getInternalElement()) {
            // TODO: for every ports (improve: only one is allowed)
            boolean isPorts = AmlUtil.hasRole(internalElement, AmlmodelConstants.NAME_ROLE_PORTS);
            if (isPorts) {
                new PortsCodegenerator().generate(internalElement, codeGeneratorConfig);
            }
        }
        */

        LOGGER.trace("Generating component for node '" + node.getName() + "' finished");

        return new GeneratedComponentConfig();
    }
}
