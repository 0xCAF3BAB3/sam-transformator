package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedComponentConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortsConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PortsCodegenerator implements Codegenerator<GeneratedComponentConfig, GeneratedPortsConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortsCodegenerator.class);

    @Override
    public final GeneratedPortsConfig generate(final InternalElement node, final GeneratedComponentConfig parentConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        LOGGER.trace("Generating ports for node '" + node.getName() + "' ...");

        // TODO: ...

        // TODO: generate communicationService and store path to it
        //final Path communicationServiceFile = Paths.get("code-output/ports/CommunicationService.java");
        //communicationServiceFile.getParentFile().mkdirs();

        /*
        for(InternalElement internalElement : node.getInternalElement()) {
            // TODO: for every port
            boolean isPort = AmlUtil.hasRole(internalElement, AmlmodelConstants.NAME_ROLE_PORT);
            if (isPort) {
                new PortCodegenerator(communicationServiceFile).generate(internalElement, codeGeneratorConfig);
            }
        }
        */

        LOGGER.trace("Generating ports for node '" + node.getName() + "' finished");

        return new GeneratedPortsConfig();
    }
}
