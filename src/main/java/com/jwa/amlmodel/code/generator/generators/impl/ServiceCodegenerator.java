package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedRootConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedServiceConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceCodegenerator implements Codegenerator<GeneratedServiceConfig, GeneratedRootConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCodegenerator.class);

    @Override
    public final GeneratedServiceConfig generate(final InternalElement node, final GeneratedRootConfig parentConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        LOGGER.trace("Generating service for node '" + node.getName() + "' ...");

        // TODO: generates Maven project

        // TODO: generate Maven module 'messagemodel'
        // final Path file = Paths.get("code-output/messagemodel/src/");
        //file.mkdirs();

        /*
        for(InternalElement internalElement : node.getInternalElement()) {
            // TODO: for every component
            boolean isComponent = AmlUtil.hasRole(internalElement, AmlmodelConstants.NAME_ROLE_COMPONENT);
            if (isComponent) {
                new ComponentCodegenerator().generate(internalElement, codeGeneratorConfig);
            }
        }
        */

        // TODO: generates Maven module 'communication' which holds communication components (no data needed, just copy)

        LOGGER.trace("Generating service for node '" + node.getName() + "' finished");

        return new GeneratedServiceConfig();
    }
}
